/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.janquadflieg.mrracer.controller;

import de.janquadflieg.mrracer.Utils;
import de.janquadflieg.mrracer.behaviour.*;
import de.janquadflieg.mrracer.classification.*;
import de.janquadflieg.mrracer.evo.FitnessEvaluator;
import de.janquadflieg.mrracer.functions.*;
import de.janquadflieg.mrracer.gui.GraphicDebugable;
import de.janquadflieg.mrracer.plan.*;
import de.janquadflieg.mrracer.telemetry.*;
import de.janquadflieg.mrracer.track.*;

import de.delbrueg.steering.behaviour.CircleSteeringBehaviour;

import flanagan.interpolation.CubicSpline;
import java.awt.Color;

import java.io.*;

import java.util.*;

/**
 * A copy from MrRacer2013.
 * @author Jan Quadflieg
 */
public final class PositionTester
        extends BaseController
        implements GraphicDebugable {

    /** Generate log text for the telemetry? */
    private static final boolean TEXT_LOG = false;
    /** Text debug messages? */
    private static final boolean TEXT_DEBUG = false;
    /** Debug online learning? -> Don't learn a new track model! */
    private static final boolean DEBUG_WARMUP = false;
    /** Debug painter. */
    private TrackModelDebugger debugPainter;
    /** Graphical debug? */
    private static final boolean GRAPHICAL_DEBUG = true;
    /** Save debug telemetry? */
    private static final boolean SAVE_DEBUG_TELEMETRY = false;
    private Telemetry debugT;
    /** Default data path. */
    private static final String DEFAULT_PARAMETERS = "/de/janquadflieg/mrracer/data/2013_1";
    /** Alternative parameter sets. */
    private static final String[] ALTERNATIVE_PARAMETERS = {"/de/janquadflieg/mrracer/data/2013_2"};
    /** List of parameter sets. */
    private ArrayList<Properties> parameterSets = new ArrayList<>();
    /** Track specific parameter sets. */
    private HashMap<String, Properties> trackParameters = new HashMap<>();
    /** Maximum fuel level. */
    //private static final double MAX_FUEL = 94.0;
    /** Noisy classifier. */
    private AngleClassifierWithQuadraticRegression noisyClassifier = new AngleClassifierWithQuadraticRegression();
    /** Clutch Behaviour. */
    private Behaviour clutch = new Clutch();
    //private Behaviour clutch = new ClutchMulti();
    //private Behaviour clutch = new ClutchConstant();
    //private Behaviour clutch = new ClutchAutopia();
    /** SteeringBehaviour. */
    private SteeringBehaviour standardSteering = new CorrectingPureHeuristicSteering(angles);
    //private SteeringBehaviour standardSteering = new SimonsSteering(angles);    
    //private SteeringBehaviour standardSteering = new PureHeuristicSteering(angles);
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
    /** Only use heuristic steering? */
    public static final String HEURISTIC_STEERING = "-MrRacer2012.onlyHeuristicSteering-";
    /** Flag. */
    private boolean onlyHeuristicSteering = false;
    /** Start time. */
    private long controllerStartTime = 0;
    /** Speed modifier for opponent tests to make this car slower. */
    //private final double opponentSpeedModifier = 1.0;
    /** Param file identifier. */
    public static final String PARAM_FILE = "Parameters";
    /** Stuck Detection. */
    private StuckDetection stuck = new StuckDetection();

    private enum Phase {

        START {
        }, POSITIONING {
        }, ACCELERATING {
        }, SWITCHING {
        }, SWITCH_FINISHED;
        public Interpolator targetPosition;
        public double targetSpeed;
    };
    private Phase phase;
    public double targetSpeed = 230.0;
    public double switchDistance = 50.0;
    public double switchDelta = 10.0;
    public double error = 0.0;
    public int errorCtr = 0;
    public double errorMax = 0.0;
    private double measureStart = 0.0;
    public ArrayList<javax.vecmath.Vector4d> dataList = new ArrayList<>();

    static {
        // init angles        
        for (int i = 0; i < 19; ++i) {
            angles[i] = -90 + i * 10;
        }
    }

    public PositionTester() {
        this(null, DEFAULT_PARAMETERS, true, 200.0, 100.0, 10.0);
    }

    public PositionTester(Telemetry t) {
        this(t, DEFAULT_PARAMETERS, true, 200.0, 100.0, 10.0);
    }

    public PositionTester(Telemetry t, String s, boolean loadFromCP,
            double speedToTest, double distanceToTest, double deltaToTest) {
        super(t);

        targetSpeed = speedToTest;
        switchDistance = distanceToTest;
        switchDelta = deltaToTest;

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

        if (GRAPHICAL_DEBUG) {
            debugPainter = new TrackModelDebugger();
            debugPainter.setModel(trackModel);
            debugPainter.setName("TrackModel");
        }

        //String modifier = System.getProperty("speedModifier");
        /*if(modifier != null){
        System.out.println("Modifier: "+modifier);
        opponentSpeedModifier = Double.parseDouble(modifier);
        }*/



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

            if (TEXT_DEBUG) {
                System.out.println("Loading alternative parameter sets...");
            }
            for (int i = 0; i < ALTERNATIVE_PARAMETERS.length; ++i) {
                if (TEXT_DEBUG) {
                    System.out.println(ALTERNATIVE_PARAMETERS[i]);
                }
                in = new Object().getClass().getResourceAsStream(ALTERNATIVE_PARAMETERS[i]);
                p = new Properties();
                p.load(in);
                in.close();
                parameterSets.add(p);
            }

            if (TEXT_DEBUG) {
                System.out.println("Loading track specific parameter sets...");
            }
            File currentDirectory = new File(".");
            File[] files = currentDirectory.listFiles();

            for (File f : files) {
                if (f.isFile() && f.getName().endsWith(BaseController.PARAMETER_EXT)) {
                    String filename = "." + java.io.File.separator + f.getName();
                    String trackName = f.getName().substring(0, f.getName().length() - BaseController.PARAMETER_EXT.length());
                    if (TEXT_DEBUG) {
                        System.out.println(filename + " - " + trackName);
                    }
                    in = new FileInputStream(filename);
                    p = new Properties();
                    p.load(in);
                    in.close();
                    trackParameters.put(trackName, p);
                }
            }

        } catch (Exception e) {
            e.printStackTrace(System.out);
        }

        precompile();

        if (SAVE_DEBUG_TELEMETRY) {
            debugT = new Telemetry();
        }

        if (TEXT_DEBUG) {
            System.out.println("##############################################");
            System.out.println("##############################################");
            System.out.println("##############################################");
            System.out.println("##############################################");
            System.out.println();
            System.out.println();
            System.out.println();
            System.out.println();
            System.out.println();
        }
    }

    @Override
    public void setParameters(Properties params) {
        acceleration.setParameters(params, ACC);
        backupBehaviour.setParameters(params, RECOVERY);
        clutch.setParameters(params, CLUTCH);

        if (params.containsKey(HEURISTIC_STEERING)) {
            onlyHeuristicSteering = Boolean.parseBoolean(params.getProperty(HEURISTIC_STEERING));
        }
        //new RuntimeException().printStackTrace();
        //System.out.println("onlyHeuristicSteering="+onlyHeuristicSteering);
    }

    @Override
    public void getParameters(Properties params) {
        //System.out.println("Parameters in use:");
        //params.list(System.out);
        acceleration.getParameters(params, ACC);
        backupBehaviour.getParameters(params, RECOVERY);
        clutch.getParameters(params, CLUTCH);

        params.setProperty(HEURISTIC_STEERING, String.valueOf(onlyHeuristicSteering));
    }

    public void paint(String baseFileName, java.awt.Dimension d) {
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
    }

    @Override
    public void setTrackName(String s) {
        super.setTrackName(s);

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

        if (GRAPHICAL_DEBUG) {
            debugPainter.setModel(this.trackModel);
            debugPainter.repaint();
        }
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

        //return data.onTrack() && angleOK && speedOK && !stuck.isStuck();
        return true;
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
            controllerStartTime = System.currentTimeMillis();
            firstPacket = false;
        }

        if (noise.isNoisy() && data.getCurrentLapTime() < 0.0) {
            trackModel.adjustWidth(noise.getWidth());
        }

        Situation s;

        if (noise.isNoisy()) {
            s = noisyClassifier.classify(data);
        } else {
            s = classifier.classify(data);
        }

        boolean wasComplete = trackModel.complete();
        trackModel.append(data, s, noise.isNoisy());
        if (!wasComplete && trackModel.complete() && TEXT_DEBUG) {
            System.out.println("TrackModel complete");
            double seconds = (System.currentTimeMillis() - controllerStartTime) / 1000.0;
            System.out.println("Learning took " + Utils.timeToString(seconds));
        }

        if (canHandle(data)) {
            controllerLog.delete(0, controllerLog.length());
            backupBehaviour.reset();
            if (wasRecovering) {
                noisyClassifier.reset();
                noise.clearBuffer();
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

            double nextDistance = data.getDistanceRaced() + Math.max(0.2, (data.getSpeed() / 3.6) / 50);

            switch (phase) {
                case START: {
                    phase = Phase.POSITIONING;
                    phase.targetSpeed = 50.0;
                    double[] xp = new double[3];
                    double[] yp = new double[3];
                    xp[0] = data.getDistanceRaced();
                    xp[2] = xp[0] + 100.0;
                    xp[1] = (xp[0] + xp[2]) / 2.0;
                    yp[0] = data.getTrackPosition();
                    yp[2] = SensorData.calcRelativeTrackPosition(2.0, trackModel.getWidth());
                    yp[1] = (yp[0] + yp[2]) / 2.0;

                    CubicSpline spline = new CubicSpline(xp, yp);
                    spline.setDerivLimits(0.0, 0.0);
                    phase.targetPosition = new FlanaganCubicWrapper(spline);

                    break;
                }
                case POSITIONING: {
                    if (nextDistance >= phase.targetPosition.getXmax()) {
                        phase = Phase.ACCELERATING;
                        phase.targetSpeed = targetSpeed;
                        phase.targetPosition = new ConstantValue(data.getDistanceRaced(),
                                data.getDistanceRaced() + 3000.0,
                                SensorData.calcRelativeTrackPosition(2.0, trackModel.getWidth()));
                    }

                    break;
                }
                case ACCELERATING: {
                    if (data.getSpeed() > phase.targetSpeed - 5) {
                        phase = Phase.SWITCHING;
                        phase.targetSpeed = targetSpeed;

                        double[] xp = new double[3];
                        double[] yp = new double[3];
                        xp[0] = data.getDistanceRaced();
                        xp[2] = xp[0] + switchDistance;
                        xp[1] = (xp[0] + xp[2]) / 2.0;
                        yp[0] = data.getTrackPosition();
                        yp[2] = SensorData.calcRelativeTrackPosition(2.0 + switchDelta, trackModel.getWidth());
                        yp[1] = (yp[0] + yp[2]) / 2.0;

                        CubicSpline spline = new CubicSpline(xp, yp);
                        spline.setDerivLimits(0.0, 0.0);
                        phase.targetPosition = new FlanaganCubicWrapper(spline);

                        error = 0.0;
                        errorCtr = 0;
                        errorMax = 0.0;
                        measureStart = data.getDistanceRaced();
                        dataList.clear();
                    }

                    break;
                }
                case SWITCHING: {
                    if (nextDistance >= phase.targetPosition.getXmax()) {
                        phase = Phase.SWITCH_FINISHED;
                        phase.targetSpeed = targetSpeed;
                        phase.targetPosition = new ConstantValue(data.getDistanceRaced(),
                                data.getDistanceRaced() + 100.0,
                                SensorData.calcRelativeTrackPosition(2.0 + switchDelta, trackModel.getWidth()));

                    } else {
                        logData(data);
                    }

                    break;
                }
                case SWITCH_FINISHED: {
                    if (nextDistance >= phase.targetPosition.getXmax()) {
                        if (!isPreCompiling()) {
                            System.out.println("-------- " + Utils.dTS(targetSpeed) + "kmh - "
                                    + Utils.dTS(switchDistance) + "m - " + Utils.dTS(switchDelta) + "m --------");
                            System.out.println("Error: " + Utils.dTS(error));
                            System.out.println("ErrorCtr: " + errorCtr);
                            System.out.println("Avg Error: " + Utils.dTS(Math.sqrt(error / errorCtr)));
                            System.out.println("Max Error: " + Utils.dTS(errorMax));
                            System.out.println("DONE");

                            String filename = String.valueOf((int) targetSpeed) + "kmh-"
                                    + String.valueOf((int) switchDelta) + "m-"
                                    + String.valueOf((int) switchDistance) + "m";

                            final double ppm = 10.0;
                            int height = (int) Math.round(ppm * trackModel.getWidth());
                            int width = (int) Math.round(ppm * (dataList.get(dataList.size() - 1).x));
                            java.awt.image.BufferedImage img = new java.awt.image.BufferedImage(width, height, java.awt.image.BufferedImage.TYPE_INT_RGB);
                            java.awt.Graphics2D g = (java.awt.Graphics2D) img.getGraphics();
                            g.setColor(java.awt.Color.WHITE);
                            g.fillRect(0, 0, width, height);

                            for (int i = 1; i < trackModel.getWidth(); ++i) {
                                int y = (int) Math.round(i * ppm);
                                g.setColor(Color.BLACK);
                                g.drawLine(0, y, width, y);
                            }

                            for (int i = 0; i < dataList.size(); ++i) {
                                javax.vecmath.Vector4d v = dataList.get(i);
                                int x = (int) Math.round(v.getX() * ppm);

                                g.setColor(Color.GREEN);
                                int y = (int) Math.round(v.getY() * ppm);
                                g.fillRect(x - 1, y - 1, 3, 3);

                                g.setColor(Color.RED);
                                y = (int) Math.round(v.getZ() * ppm);
                                g.fillRect(x - 1, y - 1, 3, 3);
                            }

                            try {
                                javax.imageio.ImageIO.write(img, "PNG", new java.io.File(filename + ".png"));
                            } catch (java.io.IOException e) {
                                e.printStackTrace(System.out);
                            }

                            action.setRestartRace(true);
                        }

                    } else {
                        logData(data);
                    }

                    break;
                }
            }

            gear.setSituation(s);
            gear.execute(data, action);

            acceleration.setSituation(s);
            acceleration.setWidth(trackModel.getWidth());
            //double speedModifier = 1.0 - (0.1 * Math.max(Math.min((MAX_FUEL - data.getFuelLevel()) / MAX_FUEL, 1.0), 0.0));
            //speedModifier *= opponentSpeedModifier;
            acceleration.setTargetSpeed(phase.targetSpeed);
            acceleration.setTrackSegment(current);
            acceleration.execute(data, action);



            SteeringBehaviour steering = circleSteering;
            double targetPosition = phase.targetPosition.interpolate(nextDistance);

            steering.setSituation(s);
            steering.setWidth(trackModel.getWidth());
            steering.setTargetPosition(new java.awt.geom.Point2D.Double(targetPosition,
                    nextDistance - data.getDistanceRaced()));
            steering.setTrackSegment(current);
            steering.execute(data, action);

            acceleration.execute(data, action);

            clutch.execute(data, action);

            if (TEXT_LOG) {
                controllerLog.append("-");
            }

        } else {
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

        if (GRAPHICAL_DEBUG) {
            debugPainter.update(data.getDistanceFromStartLine());
        }

        return action.getRaceClientAction();
    }

    private void logData(SensorData data) {
        // daten aufzeichnen
        double distance = data.getDistanceRaced() - measureStart;
        double realAbsPosition = data.calcAbsoluteTrackPosition(trackModel.getWidth());
        double plannedAbsPosition = SensorData.calcAbsoluteTrackPosition(phase.targetPosition.interpolate(data.getDistanceRaced()), trackModel.getWidth());

        error += (realAbsPosition - plannedAbsPosition) * (realAbsPosition - plannedAbsPosition);
        ++errorCtr;
        if (Math.abs(realAbsPosition - plannedAbsPosition) > errorMax) {
            errorMax = Math.abs(realAbsPosition - plannedAbsPosition);
        }

        dataList.add(new javax.vecmath.Vector4d(distance, plannedAbsPosition, realAbsPosition, 0.0));

    }

    @Override
    public javax.swing.JComponent[] getComponent() {
        if (GRAPHICAL_DEBUG) {
            javax.swing.JComponent[] result = new javax.swing.JComponent[1];


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

        // this prevents the loss of all warmup information when using the maxSteps
        // argument of the standard Client combined with the standard server
        RuntimeException e = new RuntimeException();
        StackTraceElement[] list = e.getStackTrace();

        //for(int i=0; i < list.length; ++i){
        //    System.out.println(i+" "+list[i].getClassName()+" "+list[i].getFileName()+
        //            list[i].getMethodName()+" "+list[i].getLineNumber());
        //}

        if (list.length > 1 && list[1].getClassName().equalsIgnoreCase("scr.Client")
                && list[1].getMethodName().equalsIgnoreCase("main")) {
            return;
        }

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
        noise.reset();
        noisyClassifier.reset();
        clutch.reset();
        stuck.reset();
        phase = Phase.START;
    }

    @Override
    protected void functionalReset() {
        backupBehaviour.reset();
        gear.reset();
        acceleration.reset();
        circleSteering.reset();
        standardSteering.reset();
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
            if (DEBUG_WARMUP) {
                suffix = new Date().toString().replace(':', '-').replace(' ', '-');
            }
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

    public static void main(String[] args) {
        try {
            int port = 3001;//Integer.parseInt(args[0]);
            double startSpeed = 250;//Double.parseDouble(args[1]);
            ArrayList<javax.vecmath.Point4d> results = new ArrayList<>();
            ArrayList<javax.vecmath.Point4d> results2 = new ArrayList<>();

            java.io.FileWriter writer = new java.io.FileWriter("switch" + port + ".log");
            writer.write("speed distance delta error ctr max mse\n");
            writer.flush();

            for (double targetSpeed = startSpeed; targetSpeed < Math.min(startSpeed + 555, 280); targetSpeed += 25.0) {
                for (double switchDelta = 1.0; switchDelta < 5.5; switchDelta += 1.0) {
                    targetSpeed = 250.0;
                    switchDelta = 1;
                    double switchDistance = Math.round(2 * targetSpeed);
                    double stepSize = switchDistance;
                    double mse = 0.0;
                    double maxError = 0.0;

                    for (int ctr = 0; ctr < 10; ++ctr) {
                        if (ctr >= 1) {
                            stepSize *= 0.5;

                            // adjust switchDistance
                            if (mse < 0.5 && maxError < 0.5) {
                                switchDistance -= stepSize;

                            } else {
                                switchDistance += stepSize;
                            }
                        }

                        PositionTester controller = new PositionTester(null, PositionTester.DEFAULT_PARAMETERS, true,
                                targetSpeed, switchDistance, switchDelta);

                        controller.setStage(scr.Controller.Stage.RACE);
                        controller.setTrackName("otsw20");

                        FitnessEvaluator fe = new FitnessEvaluator("127.0.0.1", port,
                                new Evaluator(controller, 40 * 50), 1);

                        try {
                            fe.join();
                        } catch (InterruptedException e) {
                            e.printStackTrace(System.out);
                        }

                        mse = Math.sqrt(controller.error / controller.errorCtr);
                        maxError = controller.errorMax;

                        writer.write(String.valueOf(controller.targetSpeed) + " "
                                + String.valueOf(controller.switchDistance) + " "
                                + String.valueOf(controller.switchDelta) + " "
                                + String.valueOf(controller.error) + " "
                                + String.valueOf(controller.errorCtr) + " "
                                + String.valueOf(controller.errorMax) + " "
                                + String.valueOf(Math.sqrt(controller.error / controller.errorCtr)) + "\n");

                        writer.flush();

                        results.add(new javax.vecmath.Point4d(controller.targetSpeed,
                                controller.switchDistance, controller.switchDelta, controller.error));
                        results2.add(new javax.vecmath.Point4d(controller.errorCtr,
                                controller.errorMax, Math.sqrt(controller.error / controller.errorCtr), 0.0));
                    }
                }
            }

            writer.close();
        } catch (Exception e) {
            e.printStackTrace(System.out);
        }

        /*System.out.println("TS1 TS2 Planned Real Diff");
        for (javax.vecmath.Point4d p : results) {
        System.out.println(p.x + " " + p.y + " " + p.z + " " + p.w + " " + (p.z - p.w));
        }

        System.out.println("TS1 TS2 Planned Real Diff");
        for (javax.vecmath.Point4d p : results) {
        System.out.println(Utils.dTS(p.x) + "km/h " + Utils.dTS(p.y) + "km/h " + Utils.dTS(p.z) + "m "
        + Utils.dTS(p.w) + "m " + Utils.dTS(p.z - p.w) + "m");
        }*/
    }

    public static void main2(String[] args) {
        try {
            javax.swing.JFileChooser fileChooser = new javax.swing.JFileChooser("f:\\");
            fileChooser.setFileSelectionMode(javax.swing.JFileChooser.FILES_ONLY);
            fileChooser.setAcceptAllFileFilterUsed(true);
            fileChooser.setMultiSelectionEnabled(false);


            int result = fileChooser.showOpenDialog(null);
            if (result != javax.swing.JFileChooser.APPROVE_OPTION) {
                System.exit(-1);
            }

            File file = fileChooser.getSelectedFile();

            ArrayList<javax.vecmath.Point4d> results = new ArrayList<>();
            ArrayList<javax.vecmath.Point4d> results2 = new ArrayList<>();

            //java.io.FileReader reader = new java.io.FileReader(file);
            java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.FileReader(file));

            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("s")) {
                    continue;
                }
                StringTokenizer tokenizer = new StringTokenizer(line, " ");

                results.add(new javax.vecmath.Point4d(Double.parseDouble(tokenizer.nextToken()),
                        Double.parseDouble(tokenizer.nextToken()), Double.parseDouble(tokenizer.nextToken()), Double.parseDouble(tokenizer.nextToken())));
                results2.add(new javax.vecmath.Point4d(Double.parseDouble(tokenizer.nextToken()),
                        Double.parseDouble(tokenizer.nextToken()), Double.parseDouble(tokenizer.nextToken()), 0.0));
            }

            java.io.FileWriter writer = new java.io.FileWriter("cut.log");
            writer.write("speed distance delta error ctr max mse\n");
            writer.flush();

            for (double targetSpeed = 50.0; targetSpeed < 280; targetSpeed += 25.0) {
                for (double switchDelta = 1.0; switchDelta < 14.5; switchDelta += 1.0) {
                    double smallest = 100000.0;
                    javax.vecmath.Point4d w1 = null;
                    javax.vecmath.Point4d w2 = null;

                    for (int i = 0; i < results.size(); ++i) {
                        javax.vecmath.Point4d r1 = results.get(i);
                        javax.vecmath.Point4d r2 = results2.get(i);

                        if (r1.getX() == targetSpeed && r1.getZ() == switchDelta
                                && r2.getY() < 0.5 && r2.getZ() < 0.5 && smallest > r1.getY()) {
                            smallest = r1.getY();
                            w1 = r1;
                            w2 = r2;
                        }
                    }

                    if (w1 != null && w2 != null) {

                        writer.write(String.valueOf(w1.getX()) + " "
                                + String.valueOf(w1.getY()) + " "
                                + String.valueOf(w1.getZ()) + " "
                                + String.valueOf(w1.getW()) + " "
                                + String.valueOf(w2.getX()) + " "
                                + String.valueOf(w2.getY()) + " "
                                + String.valueOf(w2.getZ()) + "\n");

                        writer.flush();
                    }
                }

            }

            writer.close();
        } catch (Exception e) {
            e.printStackTrace(System.out);
        }

        /*System.out.println("TS1 TS2 Planned Real Diff");
        for (javax.vecmath.Point4d p : results) {
        System.out.println(p.x + " " + p.y + " " + p.z + " " + p.w + " " + (p.z - p.w));
        }

        System.out.println("TS1 TS2 Planned Real Diff");
        for (javax.vecmath.Point4d p : results) {
        System.out.println(Utils.dTS(p.x) + "km/h " + Utils.dTS(p.y) + "km/h " + Utils.dTS(p.z) + "m "
        + Utils.dTS(p.w) + "m " + Utils.dTS(p.z - p.w) + "m");
        }*/
    }
}
