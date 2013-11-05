/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.janquadflieg.mrracer.controller;

import de.janquadflieg.mrracer.Utils;
import de.janquadflieg.mrracer.behaviour.*;
import de.janquadflieg.mrracer.classification.*;
import de.janquadflieg.mrracer.gui.GraphicDebugable;
import de.janquadflieg.mrracer.plan.*;
import de.janquadflieg.mrracer.telemetry.*;
import de.janquadflieg.mrracer.track.*;

import de.delbrueg.steering.behaviour.CircleSteeringBehaviour;

import java.io.*;

import java.util.*;

import javax.swing.JFileChooser;

/**
 *
 * @author Jan Quadflieg
 */
public final class MrSimpleRacer
        extends BaseController
        implements GraphicDebugable {

    /** Generate log text for the telemetry? */
    private static final boolean TEXT_LOG = false;
    /** Text debug messages? */
    private static final boolean TEXT_DEBUG = false;
    /** Debug painter. */
    private TrackModelDebugger debugPainter;
    /** Graphical debug? */
    private static final boolean DEBUG = false;
    /** Save debug telemetry? */
    private static final boolean SAVE_DEBUG_TELEMETRY = false;
    private Telemetry debugT;
    /** Default data path. */
    private static final String DEFAULT_PARAMETERS = "/de/janquadflieg/mrracer/data/testset";
    /** List of parameter sets. */
    private ArrayList<Properties> parameterSets = new ArrayList<>();
    /** Noisy classifier. */
    private AngleClassifierWithQuadraticRegression noisyClassifier = new AngleClassifierWithQuadraticRegression();
    /** Plan. */
    private Plan2011 plan;
    /** Clutch Behaviour. */
    private Behaviour clutch = new Clutch();
    /** SteeringBehaviour. */
    //private SteeringBehaviour standardSteering = new SimonsSteering(angles);
    private SteeringBehaviour standardSteering = new CorrectingPureHeuristicSteering(angles);
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
    /** Start time. */
    private long controllerStartTime = 0;
    /** Speed modifier for opponent tests to make this car slower. */
    //private final double opponentSpeedModifier = 1.0;
    /** Param file identifier. */
    public static final String PARAM_FILE = "Parameters";
    /** Stuck Detection. */
    private StuckDetection stuck = new StuckDetection();

    static {
        // init angles        
        for (int i = 0; i < 19; ++i) {
            angles[i] = -90 + i * 10;
        }
    }

    public MrSimpleRacer() {
        this(null, DEFAULT_PARAMETERS, true);
    }

    public MrSimpleRacer(Telemetry t) {
        this(t, DEFAULT_PARAMETERS, true);
    }

    public MrSimpleRacer(Telemetry t, String s, boolean loadFromCP) {
        super(t);

        // This has to be done first, so that the EAMode flag gets set before
        // the plan object is created
        if (System.getProperties().containsKey(PARAM_FILE)) {
            s = System.getProperty(PARAM_FILE);
            loadFromCP = false;
            System.setProperty("EAMode", "");
        }

        backupBehaviour = new DefensiveFallbackBehaviour(angles);
        noise = new NoiseDetector();
        classifier = new AngleBasedClassifier(angles);

        plan = new Plan2011(this);

        if (DEBUG) {
            debugPainter = new TrackModelDebugger();
            debugPainter.setModel(trackModel);
            debugPainter.setName("TrackModel");
        }

        try {
            if (TEXT_DEBUG) {
                System.out.print("Loading default parameter set " + s);
            }
            InputStream in;

            if (loadFromCP) {
                if (TEXT_DEBUG) {
                    System.out.println(" from class path");
                }
                in = new Object().getClass().getResourceAsStream(s);
            } else {
                if (TEXT_DEBUG) {
                    System.out.println(" from file");
                }
                in = new FileInputStream(s);
            }

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

        if (SAVE_DEBUG_TELEMETRY) {
            debugT = new Telemetry();
        }
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

    public void paint(String baseFileName, java.awt.Dimension d) {
        plan.paint(baseFileName, d);
        acceleration.paint(baseFileName, d);
        clutch.paint(baseFileName, d);
    }

    @Override
    public int getParameterSetCount() {
        return parameterSets.size();
    }

    @Override
    public void selectParameterSet(int i) {
        if (i >= 0 && i < parameterSets.size()) {
            setParameters(parameterSets.get(i));
        }
    }

    @Override
    public void setStage(Stage s) {
        super.setStage(s);
        plan.setStage(s);
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
        long startTime = System.nanoTime();
        controllerLog.delete(0, controllerLog.length());
        ModifiableAction action = new ModifiableAction();
        SensorData data = new SensorData(model);
        SensorData rawData = new SensorData(model);

        noise.update(data);

        circleSteering.setNoisy(noise.isNoisy());

        if (noise.isNoisy()) {
            data = noise.filterNoise(data);
        }

        if (firstPacket) {
            firstPacket();
        }

        Situation s;

        if (noise.isNoisy()) {
            s = noisyClassifier.classify(data);
        } else {
            s = classifier.classify(data);
        }

        if (canHandle(data)) {
            controllerLog.delete(0, controllerLog.length());
            backupBehaviour.reset();
            if (wasRecovering) {
                plan.functionalReset();
                noisyClassifier.reset();
                noise.clearBuffer();
                gear.reset();
                stuck.reset();
            }
            wasRecovering = false;

            action.reset();

            double v = Math.abs(s.getMeasure());
            TrackSegment.Apex apex = new TrackSegment.Apex();
            apex.unknown = false;
            apex.value = v;
            apex.targetSpeed = TrackSegment.DEFAULT_SPEED;

            double targetSpeed = plan.getTargetSpeed(apex);

            gear.setSituation(s);
            gear.execute(data, action);

            acceleration.setSituation(s);
            acceleration.setWidth(trackModel.getWidth());
            acceleration.setTargetSpeed(targetSpeed);
            acceleration.execute(data, action);

            SteeringBehaviour steering = standardSteering;

            steering.setSituation(s);
            steering.setWidth(trackModel.getWidth());
            steering.setTrackSegment(null);
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

//        if(!this.precompile){
//            System.out.println(data.getGear()+" "+data.getCurrentLapTimeS()+" "+data.getSpeedS());//+" "+gear.getLog()+" a.gear:"+action.getGearS());
//        }

        // zum testen
        //action.setFocusAngle(-(int) Math.round(data.getAngleToTrackAxis()));

        if (telemetry != null) {
            telemetry.log(data, action, controllerLog.toString());
        }

        if (SAVE_DEBUG_TELEMETRY && debugT != null) {
            debugT.log(rawData, action, String.valueOf(s.getMeasure()));
        }

        if (TEXT_DEBUG) {
            double dT = ((double) (System.nanoTime() - startTime)) / 1000000.0;
            if (dT > 5.0 && !precompile) {
                if (TEXT_DEBUG) {
                    System.out.println("Warning, exceeding 5,0ms");
                    System.out.println(String.format("%.2fms", dT));
                }
            }
        }

        if (DEBUG) {
            debugPainter.update(data.getDistanceFromStartLine());
        }

        return action.getRaceClientAction();
    }

    private void firstPacket() {
        controllerStartTime = System.currentTimeMillis();

        if (getStage() == Stage.WARMUP) {
            if (!precompile && TEXT_DEBUG) {
                System.out.println("Warmup, learning the trackmodel for track " + getTrackName() + ".");
            }
            trackModel = new TrackModel(getTrackName());


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

        } else {
            System.out.println("Warning, unknown stage, setting stage to warmup");
            System.out.println("Stage given was: " + getStage());
            setStage(Stage.WARMUP);
            println("Warmup, learning the trackmodel for track " + trackModel.getName() + ".");
            trackModel = new TrackModel(getTrackName());
        }

        firstPacket = false;
        plan.setTrackModel(this.trackModel);

        if (DEBUG) {
            debugPainter.setModel(this.trackModel);
            debugPainter.repaint();
        }
    }

    @Override
    public javax.swing.JComponent[] getComponent() {
        if (DEBUG) {
            javax.swing.JComponent[] c = plan.getComponent();
            javax.swing.JComponent[] result = new javax.swing.JComponent[c.length + 1];

            System.arraycopy(c, 0, result, 0, c.length);
            result[result.length - 1] = debugPainter;

            return result;

        } else {
            return new javax.swing.JComponent[0];
        }
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
        standardSteering.reset();
        wasRecovering = false;
        firstPacket = true;
        plan.resetFull();
        noise.reset();
        noisyClassifier.reset();
        clutch.reset();
        stuck.reset();
    }

    @Override
    protected void functionalReset() {
        backupBehaviour.reset();
        gear.reset();
        acceleration.reset();
        circleSteering.reset();
        standardSteering.reset();
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
        standardSteering.shutdown();
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

    public static void main(String args[]) {
        try {
            JFileChooser fileChooser = new JFileChooser("f:\\quad\\experiments");
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            fileChooser.setAcceptAllFileFilterUsed(true);
            fileChooser.setMultiSelectionEnabled(false);


            int result = fileChooser.showOpenDialog(null);
            if (result != JFileChooser.APPROVE_OPTION) {
                System.exit(-1);
            }

            File file = fileChooser.getSelectedFile();

            InputStream in = new FileInputStream(file);

            Properties p = new Properties();
            p.load(in);
            in.close();

            MrSimpleRacer controller = new MrSimpleRacer();
            controller.setParameters(p);

            controller.paint(file.getAbsoluteFile().getPath(), new java.awt.Dimension(600, 400));

        } catch (Exception e) {
            e.printStackTrace(System.out);
        }
    }
}
