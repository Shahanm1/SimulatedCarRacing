/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.janquadflieg.mrracer.plan;

import de.janquadflieg.mrracer.controller.BaseController;
import de.janquadflieg.mrracer.Utils;
import static de.janquadflieg.mrracer.data.CarConstants.*;
import de.janquadflieg.mrracer.telemetry.SensorData;
import de.janquadflieg.mrracer.track.*;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.Properties;

/**
 *
 * @author quad
 */
public class OnlineLearning2013 {

    /** Active ? */
    private boolean active = false;
    /** Planning module. */
    private Plan plan;
    /** Output of debug text messages? */
    private static final boolean TEXT_DEBUG = false;
    /** The controller. */
    private BaseController controller;
    /** Data collected during the learning process. */
    private ArrayList<LapData> lapData = new ArrayList<>(50);
    /** The trackmodel. */
    private TrackModel trackModel;
    /** The last sensordata. */
    private SensorData lastData;

    enum LearningPhase {
        TrackModel, Waiting, Testing, GoingDown, GoingFaster
    };
    /** The current phase. */
    private LearningPhase phase = LearningPhase.Waiting;

    public OnlineLearning2013() {
        // This should cause the GC to keep enough objects in the pool to make
        // memory allocations fast later
        for (int i = 0; i < lapData.size(); ++i) {
            LapData dummy = new LapData(i);
            for (int k = 0; k < 50; ++k) {
                dummy.add(new DataSet(k));
            }
        }
        lapData.clear();
    }

    public boolean active() {
        return active;
    }

    private LapData getBestLap(LearningPhase phase) {
        LapData result = null;
        double bestTime = Double.POSITIVE_INFINITY;

        for (int i = 0; i < lapData.size(); ++i) {
            LapData lap = lapData.get(i);
            if (lap.getPhase() == phase && lap.isOk() && lap.isFlying()
                    && lap.isComplete()) {
                System.out.println(lap);
                if (lap.getTime() < bestTime) {
                    result = lap;
                    bestTime = lap.getTime();
                }
            }
        }

        return result;
    }

    public void init(TrackModel model, Plan plan, SensorData data, BaseController c) {
        this.active = true;
        this.trackModel = model;
        this.controller = c;
        this.plan = plan;
        int currentLap = ((int) (data.getDistanceRaced() / model.getLength())) + 1;

        if (TEXT_DEBUG) {
            System.out.println("Initializing Online Learner [" + model.getName() + "] @lap "
                    + currentLap);
            System.out.println("Last lap time: " + Utils.timeToExactString(data.getLastLapTime()));
        }

        // add data entries for the laps used to learn the trackmodel
        for (int i = 0; i < currentLap - 1; ++i) {
            if (i == currentLap - 2) {
                lapData.add(new LapData(i + 1, data.getLastLapTime()));
            } else {
                lapData.add(new LapData(i + 1, 0.0));
            }
        }

        // add data entry for the current lap, depending on what we want to do
        if (data.getDistanceFromStartLine() < 20.0) {
            if (TEXT_DEBUG) {
                System.out.println("Near to the start finish line, no waiting needed");
            }

            lapData.add(new LapData(trackModel, lapData.size() + 1, LearningPhase.Testing, plan));
            phase = LearningPhase.Testing;
            updateTesting(true, data);

        } else {
            lapData.add(new LapData(lapData.size() + 1));
            phase = LearningPhase.Waiting;
        }

        lastData = data;
    }

    public void update(SensorData data) {
        boolean newLap = data.getDistanceRaced() > lastData.getDistanceRaced()
                && lastData.getDistanceFromStartLine() > data.getDistanceFromStartLine();

        if (newLap) {
            lapData.get(lapData.size() - 1).closeLap(data, trackModel);
            if (TEXT_DEBUG) {
                System.out.println("");
                System.out.println("-------------------------------------------------------");
                System.out.println(lapData.size() + " " + Utils.timeToExactString(data.getLastLapTime()));
                /*System.out.println(DataSet.toHeader());
                for (int i = 0; i < lapData.get(lapData.size() - 1).size(); ++i) {
                System.out.println(lapData.get(lapData.size() - 1).get(i).toString());
                }*/

                System.out.println("[" + phase.toString() + "] Evaluating last lap...");
            }
            if (phase == LearningPhase.Waiting) {
                if (TEXT_DEBUG) {
                    System.out.println("Done with waiting, starting to test parameter sets");
                }

                lapData.add(new LapData(trackModel, lapData.size() + 1, LearningPhase.Testing, plan));
                phase = LearningPhase.Testing;
                updateTesting(true, data);

            } else if (phase == LearningPhase.Testing) {
                updateTesting(false, data);

            } else if (phase == LearningPhase.GoingDown) {
                updateGoingDown(false, -1);

            } else if (phase == LearningPhase.GoingFaster) {
                updateGoingFaster(false, -1);
            }

            if (TEXT_DEBUG) {
                System.out.println("-------------------------------------------------------");
                System.out.println("");
            }

        } else {
            if (phase != LearningPhase.Waiting) {
                lapData.get(lapData.size() - 1).update(lastData, data, trackModel);
            }
        }

        lastData = data;
    }

    private void updateTesting(boolean justSwitched, SensorData data) {
        if (justSwitched) {
            lapData.get(lapData.size() - 1).setParameterSet(0);
            plan.replan();
            return;
        }

        if (!lapData.get(lapData.size() - 1).isFlying()) {
            if (TEXT_DEBUG) {
                System.out.println("One more lap to go with this parameter set.");
            }

            lapData.add(new LapData(trackModel, lapData.size() + 1, LearningPhase.Testing, plan));
            lapData.get(lapData.size() - 1).setFlying(true);
            lapData.get(lapData.size() - 1).setParameterSet(lapData.get(lapData.size() - 2).getParameterSet());
            return;
        }

        int paramSet = lapData.get(lapData.size() - 1).getParameterSet();

        // Was this the last parameter set to test?
        if (paramSet == controller.getParameterSetCount() - 1) {
            if (TEXT_DEBUG) {
                System.out.println("Done testing all parameter sets: ");
            }
            int best = 0;
            double bestTime = Double.POSITIVE_INFINITY;
            int bestLap = 0;

            for (int i = 0; i < controller.getParameterSetCount(); ++i) {
                for (LapData ld : lapData) {
                    if (ld.isFlying() && ld.getParameterSet() == i) {
                        if (TEXT_DEBUG) {
                            System.out.println(i + "/" + (controller.getParameterSetCount() - 1) + ": " + Utils.timeToExactString(ld.getTime()));
                        }
                        if (ld.getTime() < bestTime) {
                            bestTime = ld.getTime();
                            best = i;
                            bestLap = ld.getLap();
                        }
                    }
                }
            }
            if (TEXT_DEBUG) {
                System.out.println("Best: " + (best + 1) + " " + Utils.timeToExactString(bestTime)
                        + " in lap " + bestLap);
            }
            controller.selectParameterSet(best);

            // switch to going down
            phase = LearningPhase.GoingDown;
            updateGoingDown(true, bestLap);

        } else {
            if (TEXT_DEBUG) {
                System.out.println("Lap time for parameter set " + (paramSet + 1)
                        + "/" + controller.getParameterSetCount() + ": " + Utils.timeToExactString(data.getLastLapTime()));
            }

            paramSet++;

            if (TEXT_DEBUG) {
                System.out.println("Now testing parameter set " + (paramSet + 1));
            }

            controller.selectParameterSet(paramSet);
            lapData.add(new LapData(trackModel, lapData.size() + 1, LearningPhase.Testing, plan));
            lapData.get(lapData.size() - 1).setParameterSet(paramSet);
            plan.replan();

            // Shortcut
//            if (lapData.get(lapData.size() - 1).lastGood(4)) {
//                if (TEXT_DEBUG) {
//                    System.out.println("Last 4 segments were good, this can already be the flying lap!");
//                }
//                lapData.get(lapData.size() - 1).setFlying(true);
//            }
        }
    }

    private void updateGoingDown(boolean justSwitched, int bestLap) {
        LapData toAnalyze;

        if (justSwitched) {
            toAnalyze = lapData.get(bestLap - 1);
            if (toAnalyze.isOk()) {
                if (TEXT_DEBUG) {
                    System.out.println("Flying lap during parameter test was ok, directly switching to going faster");
                }
                phase = LearningPhase.GoingFaster;
                updateGoingFaster(true, bestLap);
                return;
            }

        } else {
            toAnalyze = lapData.get(lapData.size() - 1);
        }

        if (toAnalyze.isOk()) {
            LapData secondToLastLap = lapData.get(lapData.size() - 2);

            if (secondToLastLap.getPhase() == LearningPhase.GoingDown
                    && secondToLastLap.isOk()) {
                if (TEXT_DEBUG) {
                    System.out.println("I completed two laps without problems, switching to going faster...");
                }
                phase = LearningPhase.GoingFaster;
                updateGoingFaster(true, lapData.size());
                return;
            }

            if (TEXT_DEBUG) {
                System.out.println("Last lap was ok but I have to drive another one.");
            }

            LapData newLap = new LapData(lapData.size() + 1, LearningPhase.GoingDown, toAnalyze);
            newLap.setFlying(true);
            lapData.add(newLap);

            return;
        }

        // trouble, do correction and try another lap
        LapData newLap = new LapData(lapData.size() + 1, LearningPhase.GoingDown, toAnalyze);
        lapData.add(newLap);

        for (int i = 0; i < toAnalyze.size(); ++i) {
            DataSet ds = toAnalyze.get(i);
            DataSet newDs = newLap.get(i);

            if (!ds.corner) {
                continue;
            }

            if (TEXT_DEBUG) {
                System.out.println("Segment [" + i + "] latSpeedInt: " + Utils.dTS(ds.latSpeedInt)
                        + ", max: " + Utils.dTS(ds.maxLatSpeed));
            }

            if (ds.offTrack) {
                TrackSegment seg = trackModel.getSegment(i);
                if (TEXT_DEBUG) {
                    System.out.println("I was too fast in segment " + i);
                }
                ds.problematic = true;
                newDs.problematic = true;

                // going slower
                for (int k = 0; k < ds.speeds.length; ++k) {
                    // try to make this faster again later, by setting plus > 0
                    newDs.plusCorrection = ds.minusCorrection * 0.5;
                    newDs.speeds[k] = ds.speeds[k] * (1.0 - ds.minusCorrection);
                    if (TEXT_DEBUG) {
                        System.out.println("Setting speed from " + Utils.dTS(ds.speeds[k]) + "km/h to " + Utils.dTS(newDs.speeds[k]) + "km/h");
                    }
                    seg.setTargetSpeed(k, newDs.speeds[k]);
                }
            }
        }
        plan.replan();
    }    

    private void updateGoingFaster(boolean justSwitched, int referenceLap) {
        LapData lastLap = lapData.get(lapData.size() - 1);
        LapData newLap = new LapData(lapData.size() + 1, LearningPhase.GoingFaster, lastLap);

        if (justSwitched) {
            if (TEXT_DEBUG) {
                System.out.println("Just switched, initializing increments...");
            }

            lastLap = lapData.get(referenceLap - 1);
            newLap = new LapData(lapData.size() + 1, LearningPhase.GoingFaster, lastLap);

            for (int i = 0; i < lastLap.size(); ++i) {
                DataSet ds = lastLap.get(i);
                DataSet newDs = newLap.get(i);

                if (ds.corner && !ds.problematic && !ds.onLimit) {
                    if (ds.latSpeedInt < 1) {
                        newDs.plusCorrection = 0.20;

                    } else if (ds.latSpeedInt < 10) {
                        newDs.plusCorrection = 0.10;

                    } else if(ds.latSpeedInt < 20){
                        newDs.plusCorrection = 0.05;

                    } else {
                        newDs.plusCorrection = 0.01;
                    }

                } else {
                    //newDs.plusCorrection = 0.0;
                }
            }
        }

        lapData.add(newLap);

        if (lastLap.isOk() || justSwitched) {
            if (lastLap.isFlying() || justSwitched) {
                if (TEXT_DEBUG) {
                    if (justSwitched) {
                        System.out.println("Incrementing target speeds");
                    } else {
                        System.out.println("2 laps without problems, incrementing target speeds again");
                    }
                }

                for (int i = 0; i < lastLap.size(); ++i) {
                    DataSet ds = lastLap.get(i);
                    DataSet newDs = newLap.get(i);

                    if (newDs.corner && newDs.plusCorrection != 0.0) {
                        TrackSegment seg = trackModel.getSegment(i);

                        if (TEXT_DEBUG) {
                            System.out.print("Maybe can go faster in " + i + " ");
                        }

                        for (int k = 0; k < newDs.speeds.length; ++k) {
                            newDs.speeds[k] = ds.speeds[k] * (1.0 + newDs.plusCorrection);

                            if (ds.speeds[k] > Plan2011.MAX_SPEED) {
                                if (TEXT_DEBUG) {
                                    System.out.print(Utils.dTS(ds.speeds[k]) + " reached max speed! ");
                                }
                                newDs.speeds[k] = ds.speeds[k];
                                newDs.plusCorrection = 0.0;
                            }
                            if (TEXT_DEBUG) {
                                System.out.print("[" + Utils.dTS(ds.speeds[k]) + "->" + Utils.dTS(newDs.speeds[k]) + "] ");
                            }
                            seg.setTargetSpeed(k, newDs.speeds[k]);
                        }
                        if (TEXT_DEBUG) {
                            System.out.println("");
                        }
                    }
                }
                plan.replan();
                return;

            } else {
                if (TEXT_DEBUG) {
                    System.out.println("Last lap was ok, lets drive another one");
                }
                newLap.setFlying(true);
                return;
            }
        }

        // lap was not ok - adjust speeds
        for (int i = 0; i < newLap.size(); ++i) {
            DataSet ds = lastLap.get(i);
            DataSet newDs = newLap.get(i);

            if (!ds.corner) {
                continue;
            }

            if (TEXT_DEBUG) {
                System.out.print("[" + i + "] latSpeedInt: " + Utils.dTS(ds.latSpeedInt)
                        + ", max: " + Utils.dTS(ds.maxLatSpeed) + " ");
            }

            if (ds.offTrack || (ds.onLimit && ds.plusCorrection > 0.0)) {
                TrackSegment seg = trackModel.getSegment(i);

                if (ds.plusCorrection > 0.0) {
                    if (TEXT_DEBUG) {
                        System.out.print("Tried too much, reset to previous speed ");
                    }
                    newDs.minusCorrection = ds.plusCorrection;

                } else if (ds.offTrack && ds.plusCorrection == 0.0) {
                    if (TEXT_DEBUG) {
                        System.out.print("Again a problem in an already problematic corner, decreasing ");
                    }
                    newDs.minusCorrection = 0.025;
                }

                newDs.plusCorrection = 0.0;
                newDs.problematic = true;
                newDs.onLimit = true;
                for (int k = 0; k < newDs.speeds.length; ++k) {
                    newDs.speeds[k] = ds.speeds[k] / (1.0 + newDs.minusCorrection);
                    if (TEXT_DEBUG) {
                        System.out.print("[" + Utils.dTS(ds.speeds[k]) + "->" + Utils.dTS(newDs.speeds[k]) + "] ");
                    }
                    seg.setTargetSpeed(k, newDs.speeds[k]);
                }
            }
            if (TEXT_DEBUG) {
                System.out.println("");
            }
        }
        plan.replan();
    }
    
    public void saveData(String suffix) {
        System.out.println("----------------------------------------------------------");
        System.out.println("Done with online learning, saving data");
        System.out.println("");
        System.out.println(LapData.header());
        for (int i = 0; i < lapData.size(); ++i) {
            System.out.println(lapData.get(i).toString());
        }

        System.out.println("Analyzing results...");
        System.out.println("Candidates from phase " + LearningPhase.GoingFaster.toString());
        LapData best = getBestLap(LearningPhase.GoingFaster);        

        if (best == null) {
            System.out.println("No candidate found, taking a look at phase " + LearningPhase.GoingDown.toString());
            best = getBestLap(LearningPhase.GoingDown);
        }

        if (best == null) {
            System.out.println("Still no candidate found, taking a look at phase " + LearningPhase.Testing.toString());
            best = getBestLap(LearningPhase.Testing);
        }

        if (best != null) {
            System.out.println("Best lap:");
            System.out.println(best);
            System.out.println("Saving data...");
            for (int i = 0; i < best.size(); ++i) {
                TrackSegment seg = trackModel.getSegment(i);
                DataSet ds = best.get(i);

                if (seg.isCorner()) {
                    for (int k = 0; k < ds.speeds.length; ++k) {
                        seg.setTargetSpeed(k, ds.speeds[k]);
                    }
                }
            }
        } else {
            System.out.println("No good found, resetting to default speeds");
            for (int i = 0; i < trackModel.size(); ++i) {
                TrackSegment seg = trackModel.getSegment(i);
                TrackSegment.Apex[] apexes = seg.getApexes();

                if (seg.isCorner()) {
                    for (int k = 0; k < apexes.length; ++k) {
                        seg.setTargetSpeed(k, TrackSegment.DEFAULT_SPEED);
                    }
                }
            }
        }

        double time = 0.0;
        for (LapData ld : lapData) {
            if (ld.getPhase() == LearningPhase.TrackModel) {
                time += ld.getTime();
            }
        }
        System.out.println("Learning the trackmodel took " + Utils.timeToExactString(time));

        time = 0.0;
        for (LapData ld : lapData) {
            if (ld.getPhase() == LearningPhase.Waiting) {
                time += ld.getTime();
            }
        }
        System.out.println("Waiting took " + Utils.timeToExactString(time));

        time = 0.0;
        for (LapData ld : lapData) {
            if (ld.getPhase() == LearningPhase.Testing) {
                time += ld.getTime();
            }
        }
        System.out.println("Testing parameter sets took " + Utils.timeToExactString(time));

        time = 0.0;
        for (LapData ld : lapData) {
            if (ld.getPhase() == LearningPhase.GoingDown) {
                time += ld.getTime();
            }
        }
        System.out.println("Correcting speeds down took " + Utils.timeToExactString(time));        

        time = 0.0;
        for (LapData ld : lapData) {
            if (ld.getPhase() == LearningPhase.GoingFaster) {
                time += ld.getTime();
            }
        }
        System.out.println("Trying to go faster took " + Utils.timeToExactString(time));

        System.out.println("Saving the chosen parameter set");
        try {
            Properties p = new Properties();
            controller.getParameters(p);

            FileWriter out = new FileWriter(new File("." + java.io.File.separator + controller.getTrackName() + suffix + BaseController.PARAMETER_EXT));
            out.write("Selected during warmup" + "\n");
            out.write(Utils.list(p, "\n"));
            out.close();           

        } catch (Exception e) {
            e.printStackTrace(System.out);
        }
        System.out.println("Detailed data from all laps:");
        for (LapData ld : lapData) {
            System.out.println("[" + ld.getLap() + "]");
            System.out.println(DataSet.toHeader());
            for (int i = 0; i < ld.size(); ++i) {
                System.out.println(ld.get(i).toString());
            }
        }
    }

    public void reset() {
        lapData = new ArrayList<>(50);
        lastData = null;
    }

    public void resetFull() {
        reset();
        active = false;
    }

    /**
     * Helper class to collect data about a tracksegment.
     */
    static class DataSet {

        private int index = -1;
        public boolean onLimit = false;
        public boolean problematic = false;
        public boolean corner = false;
        public boolean offTrack = false;
        public double minusCorrection = 0.2;
        public double plusCorrection = 0.0;
        /** Speeds used. */
        public double[] speeds = new double[0];
        public double latSpeedInt = 0.0;
        public double maxLatSpeed = 0.0;

        /**
         * Default constructor.
         */
        public DataSet(int i) {
            this.index = i;
        }

        /**
         * Copy constructor.
         * @param a The other dataset.
         */
        public DataSet(DataSet a) {
            this.index = a.index;
            this.onLimit = a.onLimit;
            this.problematic = a.problematic;
            this.corner = a.corner;
            this.offTrack = false;
            this.minusCorrection = a.minusCorrection;
            this.plusCorrection = a.plusCorrection;
            this.speeds = new double[a.speeds.length];
            System.arraycopy(a.speeds, 0, this.speeds, 0, a.speeds.length);
            this.latSpeedInt = 0.0;
            this.maxLatSpeed = 0.0;
        }

        public static String toHeader() {
            Formatter f = new Formatter();
            f.format("%-4s", "Seg");
            f.format("%-2s", "C");
            f.format("%-2s", "P");
            f.format("%-2s", "L");
            f.format("%-3s", "OT");
            f.format("%-8s", "LS");
            f.format("%-8s", "mLS");
            f.format("%-6s", "-");
            f.format("%-6s", "+");
            f.format("%-10s", "Speeds");
            return f.toString();
        }

        public String toString() {
            Formatter f = new Formatter();
            f.format("%-4d", index);
            if (corner) {
                f.format("%-2s", "x");
            } else {
                f.format("%-2s", "");
            }
            if (problematic) {
                f.format("%-2s", "x");
            } else {
                f.format("%-2s", "");
            }
            if (onLimit) {
                f.format("%-2s", "x");
            } else {
                f.format("%-2s", "");
            }
            if (offTrack) {
                f.format("%-2s", "x");
            } else {
                f.format("%-2s", "");
            }
            f.format("%8s", Utils.dTS(latSpeedInt) + " ");
            f.format("%8s", Utils.dTS(maxLatSpeed) + " ");
            f.format("%6s", Utils.dTS(minusCorrection) + " ");
            f.format("%6s", Utils.dTS(plusCorrection) + " ");
            for (int i = 0; i < speeds.length; ++i) {
                f.format("%-11s", Utils.dTS(speeds[i]) + "km/h");
            }
            return f.toString();
        }
    }

    static class LapData extends ArrayList<DataSet> {

        /** Which lap is represented by this data entry? */
        private int lap = -1;
        /** Time of this lap in seconds. */
        private double time = -1;
        /** To which phase does this lap belong? */
        private OnlineLearning2013.LearningPhase phase;
        /** Was this lap a flying lap? */
        private boolean flying = false;
        /** If the phase of this lap is "testing parameter sets", to which set does it belong? */
        private int paramSet = -1;
        /** Has this lap been completed? */
        private boolean complete = false;

        public LapData(int lap) {
            this.lap = lap;
            this.phase = OnlineLearning2013.LearningPhase.Waiting;
        }

        public LapData(int lap, double time) {
            this.lap = lap;
            this.phase = OnlineLearning2013.LearningPhase.TrackModel;
            this.time = time;
        }

        public LapData(int lap, OnlineLearning2013.LearningPhase phase, LapData a) {
            this.lap = lap;
            this.phase = phase;
            this.paramSet = a.paramSet;

            for (int i = 0; i < a.size(); ++i) {
                add(new DataSet(a.get(i)));
            }
        }

        public LapData(TrackModel model, int lap, OnlineLearning2013.LearningPhase phase,
                Plan plan) {
            super(model.size());
            this.lap = lap;
            this.phase = phase;

            for (int i = 0; i < model.size(); ++i) {
                TrackSegment s = model.getSegment(i);
                if (s.isCorner()) {
                    //System.out.println("Segment " + i + " is a corner");
                    DataSet ds = new DataSet(i);
                    ds.corner = true;

                    TrackSegment.Apex[] apexes = s.getApexes();

                    ds.speeds = new double[apexes.length];

                    for (int k = 0; k < apexes.length; ++k) {
                        apexes[k].targetSpeed = TrackSegment.DEFAULT_SPEED;
                        double speed = plan.getTargetSpeed(apexes[k]);
                        s.setTargetSpeed(k, speed);
                        ds.speeds[k] = speed;
                    }

                    add(ds);

                } else {
                    add(new DataSet(i));
                }
            }
        }

        /**
         * Called when a lap is finished.
         * @param data
         */
        public void closeLap(SensorData data, TrackModel model) {
            time = data.getLastLapTime();
            complete = true;
            for (int i = 0; i < size(); ++i) {
                get(i).latSpeedInt /= model.getSegment(i).getLength();
            }
        }

        public int getLap() {
            return lap;
        }

        public int getParameterSet() {
            return this.paramSet;
        }

        public OnlineLearning2013.LearningPhase getPhase() {
            return phase;
        }

        public double getTime() {
            return time;
        }

        public boolean isComplete() {
            return complete;
        }

        public boolean isFlying() {
            return flying;
        }

        public boolean isOk() {
            return lastGood(size());
        }

        public boolean lastGood(int number) {
            boolean result = true;

            for (int i = 0; i < number && result; ++i) {
                int index = size() - 1 - i;

                if (index > 0) {
                    result &= !(get(index).offTrack);
                }
            }

            return result;
        }

        public void setFlying(boolean b) {
            this.flying = b;
        }

        public void setParameterSet(int i) {
            this.paramSet = i;
        }

        /**
         * Called to collect data during a lap.
         * @param lastData SensorData of the last timestep
         * @param data SensorData
         */
        public void update(SensorData lastData, SensorData data, TrackModel trackModel) {
            this.time = data.getCurrentLapTime();
            boolean gotDamage = data.getDamage() > lastData.getDamage() + 20.0;
            if (gotDamage) {
                if (TEXT_DEBUG) {
                    System.out.println("I've got damage");
                }
            }

            if (data.getDistanceRaced() > lastData.getDistanceRaced()) {
                int index = trackModel.getIndex(data.getDistanceFromStartLine());
                get(index).latSpeedInt += Math.abs(data.getLateralSpeed())
                        * (data.getDistanceRaced() - lastData.getDistanceRaced());
                get(index).maxLatSpeed = Math.max(get(index).maxLatSpeed,
                        Math.abs(data.getLateralSpeed()));
            }

            if (data.onTrack()) {
                double absPosition = SensorData.calcAbsoluteTrackPosition(data.getTrackPosition(),
                        trackModel.getWidth());
                int index = trackModel.getIndex(data.getDistanceFromStartLine());
                DataSet ds = get(index);
                TrackSegment seg = trackModel.getSegment(index);
                int prevIndex = trackModel.decIdx(index);
                TrackSegment prevSeg = trackModel.getSegment(prevIndex);
                DataSet prevDs = get(prevIndex);

                // left side of the track
                if (absPosition < CAR_WIDTH * 0.5) {
                    if (seg.isRight() && !ds.onLimit) {
                        ds.onLimit = true;
                        if (TEXT_DEBUG) {
                            System.out.println("On limit in corner " + index);
                        }

                    } else if (!prevDs.onLimit && prevSeg.isCorner() && prevSeg.isRight() && seg.isLeft()
                            && data.getDistanceRaced() - seg.getStart() < 3 * trackModel.getWidth()) {
                        prevDs.onLimit = true;
                        if (TEXT_DEBUG) {
                            System.out.println("On limit inside in corner " + index + ","
                                    + "blaming the previous corner");
                        }
                    }

                    // right side of the track
                } else if (absPosition > trackModel.getWidth() - CAR_WIDTH * 0.5) {
                    if (seg.isLeft() && !ds.onLimit) {
                        ds.onLimit = true;
                        if (TEXT_DEBUG) {
                            System.out.println("On limit in corner " + index);
                        }

                    } else if (!prevDs.onLimit && prevSeg.isCorner() && prevSeg.isLeft() && seg.isRight()
                            && data.getDistanceRaced() - seg.getStart() < 3 * trackModel.getWidth()) {
                        prevDs.onLimit = true;
                        if (TEXT_DEBUG) {
                            System.out.println("On limit inside in corner " + index + ","
                                    + "blaming the previous corner");
                        }
                    }

                }
            }

            if ((!data.onTrack() && lastData.onTrack()) || gotDamage) {
                int index = trackModel.getIndex(data.getDistanceFromStartLine());
                DataSet ds = get(index);
                TrackSegment seg = trackModel.getSegment(index);

                if (ds.corner && !ds.offTrack) {
                    if (TEXT_DEBUG) {
                        if (!data.onTrack()) {
                            System.out.print("Left the track ");
                        }
                        if (gotDamage) {
                            System.out.print("Got damage ");
                        }
                        System.out.println("in corner " + index + ", " + "remembering that!");
                    }

                    ds.offTrack = true;
                    TrackSegment.Apex[] apexes = seg.getApexes();
                    int prevIndex = trackModel.decIdx(index);
                    int nextIndex = trackModel.incIdx(index);
                    TrackSegment prevSeg = trackModel.getSegment(prevIndex);
                    TrackSegment nextSeg = trackModel.getSegment(nextIndex);
                    DataSet prevDs = get(prevIndex);
                    DataSet nextDs = get(nextIndex);

                    if (data.getDistanceFromStartLine() < apexes[apexes.length - 1].position) {
                        if (TEXT_DEBUG) {
                            System.out.println("Happened before the last apex");
                        }
                        if (prevSeg.isCorner()) {
                            TrackSegment.Apex[] prevApexes = prevSeg.getApexes();
                            double distance = data.getDistanceFromStartLine() - prevApexes[prevApexes.length - 1].position;

                            if (distance < 100.0) {
                                if (TEXT_DEBUG) {
                                    System.out.println("Previous segment was a corner too");
                                    System.out.println("Blaming that one too :-)");
                                }
                                prevDs.offTrack = true;
                            } else {
                                if (TEXT_DEBUG) {
                                    System.out.println("Distance bigger than 100m, don't blame previous corner");
                                }
                            }
                        }

                    } else {
                        double distance = data.getDistanceFromStartLine() - apexes[apexes.length - 1].position;
                        if (TEXT_DEBUG) {
                            System.out.println("Happened " + Utils.dTS(distance) + "m after the last apex");
                        }
                        if (distance > 100.0) {
                            if (TEXT_DEBUG) {
                                System.out.println("Distance bigger than 100m, don't blame this corner");
                            }
                            ds.offTrack = false;

                        }
                        if (nextSeg.isCorner()) {
                            if (TEXT_DEBUG) {
                                System.out.println("Next segment is a corner");
                                System.out.println("Blaming that one too :-)");
                            }
                            nextDs.offTrack = true;
                        }
                    }

                } else if (seg.isStraight() && (data.getDistanceFromStartLine() - seg.getStart()) < 30.0) {
                    int prevIndex = trackModel.decIdx(index);
                    TrackSegment prevSeg = trackModel.getSegment(prevIndex);
                    DataSet prevDs = get(prevIndex);
                    if (prevSeg.isCorner() && !prevDs.offTrack) {
                        prevDs.offTrack = true;
                        if (TEXT_DEBUG) {
                            System.out.println("I left the track on a straight [" + index + "] " + Utils.dTS(data.getDistanceFromStartLine() - seg.getStart()) + "m away from corner " + prevIndex);
                            System.out.println("Blaming the corner for that");
                        }
                    }
                }
            }
        }

        public static String header() {
            Formatter f = new Formatter();
            f.format("%-4s", "Lap");
            f.format("%-12s", "Phase");
            f.format("%-9s", "Time");
            f.format("%-4s", "Fly");
            f.format("%-3s", "PS");
            f.format("%-3s", "OK");
            return f.toString();
        }

        public String toString() {
            Formatter f = new Formatter();
            f.format("%-4d", lap);
            f.format("%-12s", phase);
            f.format("%-9s", Utils.timeToExactString(time));
            if (flying) {
                f.format("%-4s", "x");
            } else {
                f.format("%-4s", "");
            }
            if (paramSet == -1) {
                f.format("%-3s", "df");
            } else {
                f.format("%-3d", paramSet);
            }
            if (isOk()) {
                f.format("%-3s", "x");
            } else {
                f.format("%-3s", "");
            }
            return f.toString();
        }
    }

    public static void main(String[] args) {
        System.out.println(LapData.header());
        System.out.println(new LapData(1, 813.1).toString());
    }
}