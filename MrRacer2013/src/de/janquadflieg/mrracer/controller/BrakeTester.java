/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.janquadflieg.mrracer.controller;

import de.janquadflieg.mrracer.Utils;
import de.janquadflieg.mrracer.behaviour.*;
import de.janquadflieg.mrracer.classification.*;
import de.janquadflieg.mrracer.evo.FitnessEvaluator;
import de.janquadflieg.mrracer.plan.*;
import de.janquadflieg.mrracer.telemetry.*;
import de.janquadflieg.mrracer.track.*;

import de.delbrueg.steering.behaviour.CircleSteeringBehaviour;

import java.awt.geom.Point2D;
import java.io.*;

import java.util.*;

/**
 *
 * @author Jan Quadflieg
 */
public final class BrakeTester
        extends BaseController {

    /** Generate log text for the telemetry? */
    private static final boolean TEXT_LOG = false;
    /** Text debug messages? */
    private static final boolean TEXT_DEBUG = false;
    /** Debug online learning? -> Don't learn a new track model! */
    private static final boolean DEBUG_WARMUP = true;
    /** Save debug telemetry? */
    private static final boolean SAVE_DEBUG_TELEMETRY = false;
    private Telemetry debugT;
    /** Default data path. */
    private static final String DEFAULT_PARAMETERS = "/de/janquadflieg/mrracer/data/testset";
    /** List of parameter sets. */
    private ArrayList<Properties> parameterSets = new ArrayList<>();
    /** Track specific parameter sets. */
    private HashMap<String, Properties> trackParameters = new HashMap<>();
    /** Plan. */
    private Plan2011 plan;
    /** Clutch Behaviour. */
    private Behaviour clutch = new Clutch();
    /** SteeringBehaviour. */
    private CircleSteeringBehaviour circleSteering = new CircleSteeringBehaviour(angles);
    /** AccelerationBehaviour. */
    private DampedAccelerationBehaviour acceleration = new DampedAccelerationBehaviour();
    /** Gear change behaviour. */
    private StandardGearChangeBehaviour gear = new StandardGearChangeBehaviour();
    /** Angles used for the track edge sensor. */
    private final static float[] angles = new float[19];
    /** Prefix for properties. */
    public static final String PLAN = "-MrRacer2012.Plan-";
    /** Prefix for properties. */
    public static final String ACC = "-MrRacer2012.Acc-";
    /** Prefix for properties. */
    public static final String RECOVERY = "-MrRacer2012.Recovery-";
    /** Prefix for properties. */
    public static final String CLUTCH = "-MrRacer2012.Clutch-";
    /** Param file identifier. */
    public static final String PARAM_FILE = "Parameters";
    /** Stuck Detection. */
    private StuckDetection stuck = new StuckDetection();
    private double ts1;
    private double ts2;
    private int holdCtr = 0;
    private double brakeStart = 0;
    private double plannedBrakeDistance = 0.0;
    private double brakeEnd = 0.0;
    private static final double EPS = 0.5;
    private double targetPosition = 0.0;

    private enum Phase {

        ACC, HOLD, BRAKE, FINISHED
    };
    private Phase phase;

    static {
        // init angles        
        for (int i = 0; i < 19; ++i) {
            angles[i] = -90 + i * 10;
        }
    }

    public BrakeTester() {
        this(null, 150, 100);
    }

    public BrakeTester(Telemetry t) {
        this(t, 150, 100);
    }

    public BrakeTester(Telemetry t, double ts1, double ts2) {
        super(t);

        this.ts1 = ts1;
        this.ts2 = ts2;

        backupBehaviour = new DefensiveFallbackBehaviour(angles);
        plan = new Plan2011(this);
        classifier = new AngleBasedClassifier(angles);

        try {
            if (TEXT_DEBUG) {
                System.out.print("Loading default parameter set " + DEFAULT_PARAMETERS);
            }
            InputStream in;

            if (TEXT_DEBUG) {
                System.out.println(" from class path");
            }
            in = new Object().getClass().getResourceAsStream(DEFAULT_PARAMETERS);


            Properties p = new Properties();
            p.load(in);
            in.close();

            //System.out.println(Utils.list(p, "\n"));

            setParameters(p);
            parameterSets.add(p);

        } catch (Exception e) {
            e.printStackTrace(System.out);
        }

        precompile();
    }

    @Override
    public void setParameters(Properties params) {
        acceleration.setParameters(params, ACC);
        plan.setParameters(params, PLAN);
        backupBehaviour.setParameters(params, RECOVERY);
        clutch.setParameters(params, CLUTCH);
    }

    @Override
    public void getParameters(Properties params) {
        //System.out.println("Parameters in use:");
        //params.list(System.out);
        acceleration.getParameters(params, ACC);
        plan.getParameters(params, PLAN);
        backupBehaviour.getParameters(params, RECOVERY);
        clutch.getParameters(params, CLUTCH);
    }

    @Override
    public int getParameterSetCount() {
        return 0;
    }

    @Override
    public void selectParameterSet(int i) {
    }

    @Override
    public void setStage(Stage s) {
        super.setStage(s);
    }

    @Override
    public float[] initAngles() {
        return angles;
    }

    private boolean canHandle(SensorData data) {
        boolean angleOK = Math.toDegrees(data.getAngleToTrackAxis()) > -45.0 && Math.toDegrees(data.getAngleToTrackAxis()) < 45.0;

        if (wasRecovering) {
            angleOK = Math.toDegrees(data.getAngleToTrackAxis()) > -9 && Math.toDegrees(data.getAngleToTrackAxis()) < 9;
        }

        boolean speedOK = data.getSpeed() >= -0.15;

        if (!wasRecovering) {
            stuck.execute(data, null);
        }

        if (stuck.isStuck() && !wasRecovering) {
            backupBehaviour.setStuck();
        }

        if (TEXT_LOG) {
            controllerLog.append("onTrack? ");
            controllerLog.append(String.valueOf(data.onTrack()).substring(0, 1));
            controllerLog.append(", angle? ");
            controllerLog.append(String.valueOf(angleOK).substring(0, 1));
            controllerLog.append(", speed? ");
            controllerLog.append(String.valueOf(speedOK).substring(0, 1));
            controllerLog.append(", stuck? ").append(stuck.isStuck());
            System.out.println(controllerLog.toString());
        }

        return data.onTrack() && angleOK && speedOK && !stuck.isStuck();
    }

    @Override
    public scr.Action unsaveControl(scr.SensorModel model) {
        controllerLog.delete(0, controllerLog.length());
        ModifiableAction action = new ModifiableAction();
        SensorData data = new SensorData(model);

        Situation s = classifier.classify(data);

        if (firstPacket) {
            firstPacket();
            targetPosition = data.getTrackPosition();
        }

        trackModel.append(data, s, false);

        if (canHandle(data)) {
            controllerLog.delete(0, controllerLog.length());
            backupBehaviour.reset();
            if (wasRecovering) {
                plan.functionalReset();
                gear.reset();
                stuck.reset();
            }
            wasRecovering = false;

            TrackSegment current = null;

            if (trackModel.initialized()) {
                int index = trackModel.getIndex(data.getDistanceFromStartLine());
                current = trackModel.getSegment(index);

                if (TEXT_LOG) {
                    logTrackSegment(data, index, current);
                }
            }

            if (s.hasError()) {
                if (TEXT_LOG) {
                    controllerLog.append("?");
                }
                if (TEXT_DEBUG) {
                    System.out.println("Classifier error: " + s.toString());
                }

            } else {
                if (TEXT_LOG) {
                    logSituation(s);
                }
            }

            action.reset();

            if (phase == Phase.ACC) {
                double delta = Math.abs(data.getSpeed() - ts1);
                if (delta < EPS) {
                    phase = Phase.HOLD;
                    holdCtr = 0;
                }

            } else if (phase == Phase.HOLD) {
                ++holdCtr;
                if (holdCtr > 50) {
                    phase = Phase.BRAKE;
                    brakeStart = data.getDistanceRaced();
                    plannedBrakeDistance = plan.calcBrakeDistanceStraight(data.getSpeed(), ts2);
                }

            } else if (phase == Phase.BRAKE) {
                //System.out.println(data.getSpeedS());
                double delta = Math.abs(data.getSpeed() - ts2);
                if (delta < EPS || data.getSpeed() <= ts2) {                    
                    brakeEnd = data.getDistanceRaced();
                    System.out.println("planned: " + Utils.dTS(plannedBrakeDistance) + "m, real: "
                            + Utils.dTS(brakeEnd - brakeStart) + "m");
                    phase = Phase.FINISHED;
                }
            }


            double ts = 0.0;
            if (phase == Phase.ACC || phase == Phase.HOLD) {
                ts = ts1;

            } else if (phase == Phase.BRAKE || phase == Phase.FINISHED) {
                ts = ts2;
            }

            gear.setSituation(s);
            gear.execute(data, action);

            acceleration.setSituation(s);
            acceleration.setWidth(trackModel.getWidth());

            acceleration.setTargetSpeed(ts);
            acceleration.setTrackSegment(current);
            acceleration.execute(data, action);

            SteeringBehaviour steering = circleSteering;

            steering.setSituation(s);
            steering.setWidth(trackModel.getWidth());
            steering.setTargetPosition(new Point2D.Double(targetPosition, 2.0));
            steering.setTrackSegment(current);
            steering.execute(data, action);

            acceleration.execute(data, action);

            if (trackModel.complete()) {
                clutch.execute(data, action);

            } else {
                action.setClutch(0.0);
            }

            if (TEXT_LOG) {
                controllerLog.append("-");
                controllerLog.append(plan.getInfo());
            }

        } else {
            plan.updateOffTrack(data);
            wasRecovering = true;
            backupBehaviour.setSituation(s);
            backupBehaviour.execute(data, action);
            stuck.reset();
        }

        if (telemetry != null) {
            telemetry.log(data, action, controllerLog.toString());
        }

        return action.getRaceClientAction();
    }

    private void firstPacket() {
        if (getStage() == Stage.WARMUP) {
            if (DEBUG_WARMUP) {
                if (!precompile && TEXT_DEBUG) {
                    System.out.println("Warmup, debug mode, getting trackmodel for track " + getTrackName() + ".");
                }
                TrackModel result = trackDB.getByName(getTrackName());
                if (result == null) {
                    if (!precompile && TEXT_DEBUG) {
                        System.out.println("Warning, failed to get the trackmodel for track " + getTrackName() + ".");
                    }
                } else {
                    trackModel = result;
                }

            } else {
                if (!precompile && TEXT_DEBUG) {
                    System.out.println("Warmup, learning the trackmodel for track " + getTrackName() + ".");
                }
                trackModel = new TrackModel(getTrackName());
            }

        } else if (getStage() == Stage.QUALIFYING || getStage() == Stage.RACE) {
            if (!precompile && TEXT_DEBUG) {
                System.out.println("Qualifying or race, getting trackmodel for track " + getTrackName() + ".");
            }

            TrackModel result = trackDB.getByName(getTrackName());

            if (result == TrackDB.UNKNOWN_MODEL) {
                if (!precompile) {
                    System.out.println("Warning, failed to get the trackmodel for track " + getTrackName() + ".");
                }
                trackModel = new TrackModel(getTrackName());

            } else {
                trackModel = result;
            }

            Properties p = trackParameters.get(getTrackName());

            if (System.getProperties().containsKey(PARAM_FILE)) {
                if (!precompile && TEXT_DEBUG) {
                    System.out.println("Using the given parameter file " + System.getProperty(PARAM_FILE));
                }
                setParameters(parameterSets.get(0));
                //parameterSets.get(0).list(System.out);

            } else if (p != null && !System.getProperties().containsKey("EAMode")) {
                setParameters(p);

            } else if (p == null && !precompile && !System.getProperties().containsKey("EAMode")) {
                System.out.println("Warning, failed to find a parameter set selected during warmup, using default");
                setParameters(parameterSets.get(0));
            }

        } else {
            System.out.println("Warning, unknown stage, setting stage to warmup");
            System.out.println("Stage given was: " + getStage());
            setStage(Stage.WARMUP);
            println("Warmup, learning the trackmodel for track " + trackModel.getName() + ".");
            trackModel = new TrackModel(getTrackName());
        }
        firstPacket = false;
        plan.setTrackModel(this.trackModel);
    }

    private void print(String s) {
        //System.out.print(s);
    }

    private void println(String s) {
        //System.out.println(s);
    }

    @Override
    public void reset() {
        //System.out.println("RESET");
        this.resetFull();
    }

    @Override
    public void resetFull() {
        super.reset();
        backupBehaviour.reset();
        gear.reset();
        acceleration.reset();
        circleSteering.reset();
        wasRecovering = false;
        firstPacket = true;
        plan.resetFull();
        clutch.reset();
        stuck.reset();
        phase = Phase.ACC;
        holdCtr = 0;
    }

    @Override
    protected void functionalReset() {
        backupBehaviour.reset();
        gear.reset();
        acceleration.reset();
        circleSteering.reset();
        plan.functionalReset();
        clutch.reset();
        stuck.reset();
    }

    private void saveTrackModel(String suffix) {
        String filename = "." + java.io.File.separator + getTrackName() + suffix + TrackModel.TM_EXT;
        try {
            trackModel.setName(trackModel.getName() + suffix);
            trackModel.save(filename);

        } catch (Exception e) {
            System.out.println("Warning, failed to save the trackmodel for track " + getTrackName() + " to file \"" + filename + "\"!");
            System.out.println("Reason:");
            e.printStackTrace(System.out);
        }
        trackModel.print();
    }

    @Override
    public void shutdown() {
        backupBehaviour.shutdown();
        gear.shutdown();
        acceleration.shutdown();
        circleSteering.shutdown();
        clutch.shutdown();

        //System.out.println("SHUTDOWN");

        if (SAVE_DEBUG_TELEMETRY) {
            System.out.println("Stopping telemetry");
            debugT.shutdown();
            System.out.println("Saving telemetry for debugging purposes.");
            String filename = "telemetry-" + (new Date().toString().replace(':', '-').replace(' ', '-')) + ".txt";
            debugT.save(new File(filename));
        }

        if (getStage() == Stage.WARMUP) {
            System.out.println("End of Warmup, saving learned data for track " + getTrackName() + ".");
            String suffix = "";
            if (DEBUG_WARMUP) {
                suffix = new Date().toString().replace(':', '-').replace(' ', '-');
            }
            plan.saveOnlineData(suffix);
            saveTrackModel(suffix);

        } else if (getStage() == Stage.QUALIFYING || getStage() == Stage.RACE) {
            // nothing todo
        }

        if (!System.getProperties().containsKey("EAMode")) {
            System.out.println("MrRacer2012 by Team Dortmund");
            System.out.println("Team Dortmund: Jan Quadflieg, Tim Delbruegger and Mike Preuss");
            System.out.println("Thanks to Michael Flanagan for his Java Scientific Library");
        }

        /*Properties p = new Properties();
        this.getParameters(p);
        p.list(System.out);*/
    }

    public boolean finished(){
        return phase == Phase.FINISHED && !precompile;
    }

    public double getPlannedDistance() {
        return plannedBrakeDistance;
    }

    public double getRealDistance() {
        return this.brakeEnd - this.brakeStart;
    }

    public static void main(String[] args) {
        ArrayList<javax.vecmath.Point4d> results = new ArrayList<>();

        for (double ts1 = 10.0; ts1 < 300.0; ts1 += 5.0) {
            for (double ts2 = 5.0; ts2 < ts1; ts2 += 5.0) {
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                }

                //ts1 = 290;
                //ts2 = 280;
                BrakeTester controller = new BrakeTester(null, ts1, ts2);

                controller.setStage(scr.Controller.Stage.QUALIFYING);
                controller.setTrackName("OT-Speedway");

                FitnessEvaluator fe = new FitnessEvaluator("127.0.0.1", 3001,
                        new Evaluator(controller, 50 * 50), 1);

                /*try{
                    fe.join();

                } catch(InterruptedException e){
                    e.printStackTrace(System.out);
                }*/

                while (!fe.finished()) {
                    try {
                        Thread.sleep(1000);
                        if(controller.finished()){
                            fe.stop();
                        }
                    } catch (Exception e) {
                    }
                }                

                System.out.println(Utils.dTS(controller.getPlannedDistance()) + "m, " + Utils.dTS(controller.getRealDistance()) + "m");
                results.add(new javax.vecmath.Point4d(ts1, ts2, controller.getPlannedDistance(), controller.getRealDistance()));
            }
        }

        System.out.println("TS1 TS2 Planned Real Diff");
        for (javax.vecmath.Point4d p : results) {
            System.out.println(p.x + " " + p.y + " " + p.z + " " + p.w + " " + (p.z - p.w));
        }

        System.out.println("TS1 TS2 Planned Real Diff");
        for (javax.vecmath.Point4d p : results) {
            System.out.println(Utils.dTS(p.x) + "km/h " + Utils.dTS(p.y) + "km/h " + Utils.dTS(p.z) + "m "
                    + Utils.dTS(p.w) + "m " + Utils.dTS(p.z - p.w) + "m");
        }
    }
}
