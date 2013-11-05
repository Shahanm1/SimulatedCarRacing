/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.janquadflieg.mrracer.plan;

import de.delbrueg.experiment.PlanInterface;
import de.janquadflieg.mrracer.Utils;
import de.janquadflieg.mrracer.behaviour.Component;
import de.janquadflieg.mrracer.classification.Situation;
import de.janquadflieg.mrracer.classification.Situations;
import de.janquadflieg.mrracer.controller.BaseController;
import de.janquadflieg.mrracer.functions.ConstantValue;
import de.janquadflieg.mrracer.functions.FlanaganCubicWrapper;
import de.janquadflieg.mrracer.functions.GeneralisedLogisticFunction;
import de.janquadflieg.mrracer.functions.GeneralisedLogisticFunction.XAxis;
import de.janquadflieg.mrracer.functions.GeneralisedLogisticFunction.YAxis;
import de.janquadflieg.mrracer.gui.GraphicDebugable;
import de.janquadflieg.mrracer.opponents.Observer2013;
import de.janquadflieg.mrracer.opponents.OpponentObserver;
import de.janquadflieg.mrracer.telemetry.ModifiableSensorData;
import de.janquadflieg.mrracer.telemetry.SensorData;
import de.janquadflieg.mrracer.track.*;

import java.util.ArrayList;
import java.util.Properties;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;

import flanagan.interpolation.CubicSpline;

/**
 *
 * @author quad
 */
public class Plan2013
        implements GraphicDebugable, PlanInterface, Plan, Component {

    /** Maximum possible speed. */
    public static final double MAX_SPEED = 330.0;
    /** Constant indicating that no racing line is available. */
    public static final double NO_RACE_LINE = 777.0;
    /** Graphics Debug? */
    private static final boolean GRAPHICAL_DEBUG = false;
    /** Debug painter. */
    private DebugPainter debugPainter;    
    /** In this mode, we ignore the target speeds saved in the track model and always use the logistic function. */
    private static boolean EA_MODE = false;
    /** Maximum deviation allowed before a replan occurs. */
    private final static double MAX_POS_DEVIATION_M = 1.0;
    /** Modifier for the target speeds. */
    private final static double SPEED_MODIFIER = 1.0;
    /** Look ahead. */
    private final static double LOOK_AHEAD = 200.0;
    /** The stage of the race. */
    private scr.Controller.Stage stage = scr.Controller.Stage.QUALIFYING;
    /** Online learning data during the warmup. */
    private OnlineLearning2013 onlineLearner = new OnlineLearning2013();
    /** Observer for opponents. */
    private OpponentObserver observer;
    /** Last target point. */
    private Point2D lastTargetPoint = OpponentObserver.NO_RECOMMENDED_POINT;
    /** The Trackmodel. */
    private TrackModel trackModel = new TrackModel();
    /** Debug Info.*/
    private StringBuilder info = new StringBuilder();
    /** The plan. */
    private ArrayList<PlanElement2011> plan = new ArrayList<>(20);
    /** Index of the current plan element. */
    private int planIndex;
    /** Planning module for straights. */
    private Planner moduleStraight;
    /** String identifier. */
    public static final String MODULE_STRAIGHT = "-PLAN.straight-";
    /** Planning module for corners. */
    private Planner moduleCorner;
    /** String identifier. */
    public static final String MODULE_CORNER = "-PLAN.corner-";
    /** The estimated next race distance. */
    private double nextDistance = 0.0;
    /** The current race distance. */
    private double currentDistance = 0.0;
    /** The desired position for approching corners and at their apex. */
    private double offset = 0.0;
    /** The distance from the start line, from which the car started to drive. */
    private double startDistance = 0.0;
    /** Last data packet. */
    private ModifiableSensorData lastData = new ModifiableSensorData();
    /** Brake coefficient for corners. */
    private double brakeCornerCoeff = 0.5;
    /** String Identifier. */
    public static final String BRAKE_CORNER_COEFF = "-PLAN.BCC-";
    /** Friction value mu. */
    private double mu = 1.1;
    /** String Identifier. */
    public static final String MU = "-PLAN.MU-";
    /** Target Speeds. */
    private GeneralisedLogisticFunction targetSpeeds = new GeneralisedLogisticFunction();
    /** String Identifier. */
    public static final String TARGET_SPEEDS = "-PLAN.targetSpeeds-";
    /** String Identifier. */
    public static final String OBSERVER = "-PLAN.observer-";
    /** String Identifier. */
    public static final String BRAKE_PREDICTOR = "-PLAN.brakepredictor-";
    /** Controller. */
    private BaseController controller;
    /** Speed predictor. */
    private SpeedPredictor speedPredictor = new SpeedPredictor();
    /** Brake Predictor. */
    private BrakePredictor brakePredictor = new BrakePredictor();

    public Plan2013(BaseController c) {
        this.observer = new Observer2013();
        //this.observer = new de.janquadflieg.mrracer.opponents.DebugObserverBlock(this);
        //this.observer = new de.janquadflieg.mrracer.opponents.DebugObserverOvertake(this, observer);
        this.moduleStraight = new PlanStraight2013(this);
        this.moduleCorner = new PlanCorner2013(this);
        this.controller = c;
        if (GRAPHICAL_DEBUG) {
            debugPainter = new DebugPainter();
            debugPainter.setName("Plan");
        }

        if (System.getProperties().containsKey("EAMode")) {
            System.out.println("Plan: Turning EA Mode on");
            EA_MODE = true;
        }

        Properties params = new Properties();
        params.setProperty(GeneralisedLogisticFunction.M, String.valueOf(0.2));
        targetSpeeds.setParameters(params, "");
    }

    public void setParameters(Properties params, String prefix) {
        targetSpeeds.setParameters(params, prefix + TARGET_SPEEDS);
        observer.setParameters(params, prefix + OBSERVER);
        brakePredictor.setParameters(params, prefix + Plan2013.BRAKE_PREDICTOR);
        moduleCorner.setParameters(params, prefix + Plan2013.MODULE_CORNER);
        moduleStraight.setParameters(params, prefix + Plan2013.MODULE_STRAIGHT);
        mu = Double.parseDouble(params.getProperty(prefix + Plan2013.MU, String.valueOf(mu)));
        brakeCornerCoeff = Double.parseDouble(params.getProperty(prefix + Plan2013.BRAKE_CORNER_COEFF, String.valueOf(brakeCornerCoeff)));
    }

    public void getParameters(Properties params, String prefix) {
        targetSpeeds.getParameters(params, prefix + TARGET_SPEEDS);
        observer.getParameters(params, prefix + OBSERVER);
        brakePredictor.getParameters(params, prefix + Plan2013.BRAKE_PREDICTOR);
        moduleCorner.getParameters(params, prefix + Plan2013.MODULE_CORNER);
        moduleStraight.getParameters(params, prefix + Plan2013.MODULE_STRAIGHT);
        params.setProperty(prefix + Plan2013.BRAKE_CORNER_COEFF, String.valueOf(brakeCornerCoeff));
        params.setProperty(prefix + Plan2013.MU, String.valueOf(mu));
    }

    public void paint(String baseFileName, java.awt.Dimension d) {
        java.awt.image.BufferedImage img = new java.awt.image.BufferedImage(d.width, d.height, java.awt.image.BufferedImage.TYPE_INT_RGB);
        XAxis x = new XAxis();
        YAxis y = new YAxis();
        x.labelMin = 0.0;
        x.labelMax = 100.0;
        x.xmin = 0.0;
        x.xmax = 100.0;
        x.ticks = 10.0;
        x.unit = "°";

        y.mirror = true;
        y.labelMin = 0.0;
        y.y0 = 50;
        y.labelMax = 330.0;
        y.y1 = 330.0;
        y.ticks = 50.0;
        y.unit = "km/h";
        targetSpeeds.paint(img.createGraphics(), d, x, y);
        try {
            javax.imageio.ImageIO.write(img, "PNG", new java.io.File(baseFileName + "-speeds.png"));
        } catch (java.io.IOException e) {
            e.printStackTrace(System.out);
        }
    }

    public void setStage(scr.Controller.Stage s) {
        stage = s;
        observer.setStage(s);
        //System.out.println("Plan, stage="+stage.toString());
    }

    public void replan() {
        planIndex = plan.size();
    }

    public void resetFull() {
        reset();
        onlineLearner.resetFull();
    }

    @Override
    public void reset() {
        observer.reset();
        lastTargetPoint = OpponentObserver.NO_RECOMMENDED_POINT;
        plan.clear();
        planIndex = 1;
        startDistance = 0.0;
        onlineLearner.reset();
    }

    public void setStartDistance(double d) {
        startDistance = d;
    }

    public double toDistanceFromStartLine(double d) {
        if (!trackModel.initialized()) {
            return d;
        }

        double result = (d + startDistance) % trackModel.getLength();

        return result;
    }

    public void functionalReset() {
        plan.clear();
        planIndex = 1;
        observer.reset();
        lastTargetPoint = OpponentObserver.NO_RECOMMENDED_POINT;
    }

    public final double calcApproachSpeedCorner(double targetSpeed, double distance) {
        return brakePredictor.calcApproachSpeed(targetSpeed, distance, mu, brakeCornerCoeff);
    }

    public final double calcApproachSpeedStraight(double targetSpeed, double distance) {
        return brakePredictor.calcApproachSpeed(targetSpeed, distance, mu, 1.0);
    }

    public final void calcApproachSpeed(PlanStackData planData) {
        int index = planData.currentSegment();
        TrackSegment segment = trackModel.getSegment(index);
        double end = planData.planEnd();

        if (TEXT_DEBUG && !controller.isPreCompiling()) {
            println("---------------------------------------------------------");
            println("    calcApproachSpeed[" + planData.currentSegment() + "], " + Utils.dTS(planData.planEnd()) + ", " + Utils.dTS(planData.speed()) + "km/h");
        }

        ModifiableSensorData dummyData = new ModifiableSensorData();
        dummyData.setDistanceFromStartline(segment.getEnd());
        dummyData.setAngleToTrackAxis(0.0);
        dummyData.setSpeed(Plan2013.MAX_SPEED);
        dummyData.setRaceDistance(planData.planEnd());

        // create a dummy planData object and use the normal planning process
        PlanStackData dummyPlanData = new PlanStackData(planData.planEnd());

        // search the segment which contains the point planEnd + LOOK_AHEAD
        do {
            index = trackModel.incIdx(index);
            segment = trackModel.getSegment(index);
            end += segment.getLength();
            dummyPlanData.addSegment(index, segment.getLength(), Plan2013.MAX_SPEED);

        } while (end - planData.planEnd() < LOOK_AHEAD);

        if (TEXT_DEBUG  && !controller.isPreCompiling()) {
            println("    Lookahead falls into: [" + index + "], end: " + Utils.dTS(end));
            println("    DummyPlanData:");
            dummyPlanData.print();
        }

        // calculate back to front, corner entry
        while (dummyPlanData.hasMoreSegments()) {
            TrackSegment current = trackModel.getSegment(dummyPlanData.currentSegment());

            if (current.isUnknown()) {
                dummyPlanData.approachSpeed = 50.0;

            } else if (current.isStraight()) {
                moduleStraight.calcApproachSpeed(dummyPlanData, dummyData, trackModel, observer);

            } else { // corner
                moduleCorner.calcApproachSpeed(dummyPlanData, dummyData, trackModel, observer);
            }

            if (TEXT_DEBUG  && !controller.isPreCompiling()) {
                println("    [" + dummyPlanData.currentSegment() + "] "
                        + (current.getDirection() != 0 ? Situations.toShortString(current.getDirection()) : "u")
                        + " " + Utils.dTS(dummyPlanData.approachSpeed) + "km/h");
            }

            dummyPlanData.popSegment();
        }

        if (TEXT_DEBUG  && !controller.isPreCompiling()) {
            println("    Result: " + Utils.dTS(dummyPlanData.approachSpeed) + "km/h");
            println("---------------------------------------------------------");
        }
        planData.approachSpeed = dummyPlanData.approachSpeed;
    }

    public final double calcBrakeDistanceStraight(double speed, double targetSpeed) {
        return brakePredictor.calcBrakeDistance(speed, targetSpeed, mu, 1.0);
    }

    public final double calcBrakeDistanceCorner(double speed, double targetSpeed) {
        return brakePredictor.calcBrakeDistance(speed, targetSpeed, mu, brakeCornerCoeff);
    }

    public final double calcBrakingZoneStraight(double refSpeed, double length, double targetSpeed) {
        return calcBrakingZone(refSpeed, length, targetSpeed, 1.0);
    }

    public final double calcBrakingZoneCorner(double refSpeed, double length, double targetSpeed) {
        return calcBrakingZone(refSpeed, length, targetSpeed, brakeCornerCoeff);
    }

    /**
     * This method calculates the length of the braking zone needed to
     * brake down to the given target speed, assuming that the car is driving
     * with the given reference speed and length m of track is available.
     * The method takes into account that the available track can be used
     * to further accelerate.
     * 
     * @param refSpeed
     * @param length
     * @param targetSpeed
     * @param bcc
     * @return
     */
    private double calcBrakingZone(double refSpeed, double length, double targetSpeed, double bcc) {
        //System.out.println("Calculate braking zone: length=" + length + ", refSpeed=" + refSpeed + ", targetSpeed="+targetSpeed);
        double result = brakePredictor.calcBrakeDistance(speedPredictor.predictSpeed(refSpeed, length), targetSpeed, mu, bcc);

        for (int i = 0; i < 3; ++i) {
            //System.out.println(i + " " + result);
            result = brakePredictor.calcBrakeDistance(speedPredictor.predictSpeed(refSpeed, length - result), targetSpeed, mu, bcc);
        }

        return Math.max(result, 0.0);
    }

    protected double calcSpeed(double v) {
        double value = Math.abs(v) / 100.0;
        double result = this.targetSpeeds.getMirroredValue(value);
        result *= 330 - 50;
        result += 50;

        return result;
    }

    @Override
    public javax.swing.JComponent[] getComponent() {
        if (GRAPHICAL_DEBUG) {
            if (observer instanceof GraphicDebugable) {
                javax.swing.JComponent[] c = ((GraphicDebugable) observer).getComponent();
                javax.swing.JComponent[] result = new javax.swing.JComponent[c.length + 1];

                System.arraycopy(c, 0, result, 0, c.length);
                result[result.length - 1] = debugPainter;

                return result;

            } else {
                return new javax.swing.JComponent[]{debugPainter};
            }
        } else {
            return new javax.swing.JComponent[0];
        }
    }

    @Override
    public java.awt.geom.Point2D getTargetPosition() {
        double p = plan.get(planIndex).getPosition(nextDistance);

        double y = nextDistance - currentDistance;

        return new java.awt.geom.Point2D.Double(p, y);
    }

    @Override
    public double getTargetSpeed() {
        double speed = plan.get(planIndex).getSpeed(nextDistance);

        if (observer.otherCars() && observer.getRecommendedSpeed() != OpponentObserver.NO_RECOMMENDED_SPEED) {
            speed = Math.min(speed, observer.getRecommendedSpeed());
        }

        return speed;
    }

    public String getInfo() {
        return info.toString() + plan.get(planIndex).getInfo();
    }

    /**
     * Calculates the difference between the current position and the planned
     * target position in meter.
     * @param data Current sensor data.
     * @return Deviation from the planned racing line.
     */
    private double getDeviation(SensorData data) {
        if (!plan.isEmpty() && planIndex < plan.size() && plan.get(planIndex).contains(nextDistance)
                && trackModel != null && trackModel.initialized()) {

            if (plan.get(planIndex).getPosition(nextDistance) == NO_RACE_LINE) {
                return 0.0;

            } else {
                double tw_half = trackModel.getWidth() * 0.5;
                double planned = plan.get(planIndex).getPosition(nextDistance) * tw_half;
                double current = data.getTrackPosition() * tw_half;
                return Math.abs(current - planned);
            }
        }

        return 0.0;
    }

    /**
     * Updates the plan when the car is not on the track. This is only necessary
     * during the warmup.
     * @param data
     */
    public void updateOffTrack(SensorData data) {
        if (stage != scr.Controller.Stage.WARMUP || !trackModel.complete() || EA_MODE) {
            return;
        }

        if (!onlineLearner.active()) {
            onlineLearner.init(trackModel, this, data, controller);
        }
        onlineLearner.update(data);
    }

    @Override
    public void update(SensorData data, Situation situation) {
        boolean repaint = false;

        this.currentDistance = data.getDistanceRaced();

        //double plannedSpeed = data.getSpeed();
        //if(planIndex < )
        observer.update(data, situation);

        if (GRAPHICAL_DEBUG) {
            repaint = Math.abs(data.getDistanceRaced() - debugPainter.lastPosition) > 10;
        }

        info = new StringBuilder();
        lastData.setData(data);

        nextDistance = data.getDistanceRaced() + Math.max(0.0, (data.getSpeed() / 3.6) / 50);

        // handle online learning
        if (stage == scr.Controller.Stage.WARMUP && trackModel.complete() && !EA_MODE) {
            if (!onlineLearner.active()) {
                onlineLearner.init(trackModel, this, data, controller);
            }
            onlineLearner.update(data);
        }

        if (trackModel.initialized()) {
            offset = ((trackModel.getWidth() - 4) / 2) / (trackModel.getWidth() / 2);

            while (planIndex < plan.size() && !plan.get(planIndex).contains(nextDistance)) {
                ++planIndex;
            }

            boolean newTargetPoint = lastTargetPoint == OpponentObserver.NO_RECOMMENDED_POINT && observer.getRecommendedPosition() != OpponentObserver.NO_RECOMMENDED_POINT;
            boolean targetPointChanged = lastTargetPoint != OpponentObserver.NO_RECOMMENDED_POINT && observer.getRecommendedPosition() != OpponentObserver.NO_RECOMMENDED_POINT && lastTargetPoint.distanceSq(observer.getRecommendedPosition()) != 0.0;

            boolean reactToOtherCars = observer.otherCars() && (newTargetPoint || targetPointChanged);

            double deviation = getDeviation(data);
            boolean offRaceLine = deviation > MAX_POS_DEVIATION_M
                    && ((stage == scr.Controller.Stage.WARMUP && !trackModel.complete()) || (trackModel.complete() && trackModel.getSegment(data.getDistanceFromStartLine()).isStraight()));

            if (planIndex >= plan.size() || nextDistance > plan.get(plan.size() - 1).getEnd() || reactToOtherCars || offRaceLine) {
                if (TEXT_DEBUG  && !controller.isPreCompiling() && reactToOtherCars) {
                    println("Need to to replan, because of other cars");
                }

                if (TEXT_DEBUG && !controller.isPreCompiling() && offRaceLine) {
                    println("Need to replan because I left the racing line");
                }

                if (TEXT_DEBUG && !controller.isPreCompiling() && planIndex >= plan.size()) {
                    println("Need to replan because nextDistance is not covered by the plan");
                }

                plan(data);

                while (planIndex < plan.size() && !plan.get(planIndex).contains(nextDistance)) {
                    ++planIndex;
                    if (TEXT_DEBUG && !controller.isPreCompiling()) {
                        println("Adjusting plan index according to the next distance");
                    }
                }

                lastTargetPoint = observer.getRecommendedPosition();
                repaint = true;
                if (GRAPHICAL_DEBUG) {
                    debugPainter.debugData.clear();
                }
            }

        } else {
            plan.clear();
            PlanElement2011 pe = new PlanElement2011(data.getDistanceRaced(),
                    data.getDistanceRaced() + 25, "Model not initialized");
            if (Math.abs(data.getTrackPosition()) < 0.1) {
                pe.attachPosition(new ConstantValue(data.getDistanceRaced(), data.getDistanceRaced()+25.0, 0.0));

            } else {
                double[] x = {data.getDistanceRaced(), data.getDistanceRaced() + 12.5, data.getDistanceRaced() + 25.0};
                double[] y = {data.getTrackPosition(), data.getTrackPosition() / 2.0, 0};
                CubicSpline spline = new CubicSpline(x, y);
                spline.setDerivLimits(0.0, 0.0);
                pe.attachPosition(new FlanaganCubicWrapper(spline));
            }
            pe.attachSpeed(new ConstantValue(data.getDistanceRaced(), data.getDistanceRaced() + 25, 50.0));

            plan.add(pe);
            planIndex = 0;
            info.append("Model not initialized");
            repaint = true;

            if (GRAPHICAL_DEBUG) {
                debugPainter.debugData.clear();
            }
        }
        if (GRAPHICAL_DEBUG) {
            debugPainter.debugData.add(data);
        }
        if (GRAPHICAL_DEBUG && repaint) {
            this.debugPainter.repaint();
        }
    }

    private void plan(SensorData data) {
        int index = trackModel.getIndex(data.getDistanceFromStartLine());
        TrackSegment current = trackModel.getSegment(index);

        if (current.isUnknown()) {
            planUnknown(data, current);

        } else {
            planNormal(data);
        }
    }

    private void planNormal(SensorData data) {
        int index = trackModel.getIndex(data.getDistanceFromStartLine());
        TrackSegment current = trackModel.getSegment(index);

        if (TEXT_DEBUG  && !controller.isPreCompiling()) {
            println("");
            println("*****************************************************");
            println("** planNormal @" + Utils.dTS(data.getDistanceRaced()) + "m/"
                    + Utils.dTS(data.getDistanceFromStartLine()) + "m, " + data.getSpeedS() + "km/h");
            println("*****************************************************");
            println(current.toString());
        }

        // Clear current plan
        plan.clear();

        // PlanStackData Object to control the planning process
        PlanStackData planData = new PlanStackData(data.getDistanceRaced());

        // the next corner
        TrackSegment corner = null;

        // estimated speed at the corner
        double speedAtCorner = data.getSpeed();
        // distance to accelerate
        double accDist = 0.0;

        // add the current element
        accDist += current.getEnd() - data.getDistanceFromStartLine();
        speedAtCorner = speedPredictor.predictSpeed(data.getSpeed(), accDist);
        planData.addSegment(index,
                current.getEnd() - data.getDistanceFromStartLine(),
                data.getSpeed());

        // search the next corner
        int searchIndex = trackModel.incrementIndex(index);

        while (corner == null && searchIndex != index) {
            TrackSegment element = trackModel.getSegment(searchIndex);

            planData.addSegment(searchIndex, element.getLength(),
                    speedAtCorner);

            if (element.isCorner()) {
                corner = element;

            } else if (element.isUnknown()) {
                corner = element;

            } else if (element.isStraight()) {
                accDist += element.getLength();
                speedAtCorner = speedPredictor.predictSpeed(data.getSpeed(), accDist);
            }

            searchIndex = trackModel.incrementIndex(searchIndex);
        }

        if (corner == null) {
            if (TEXT_DEBUG && !controller.isPreCompiling()) {
                println("corner == null, setze letztes stack element");
            }
            corner = trackModel.getSegment(planData.currentSegment());
        }

        if (TEXT_DEBUG && !controller.isPreCompiling()) {
            System.out.println("");
            println("Starting to plan, start=" + Utils.dTS(planData.planStart()) + ", end=" + Utils.dTS(planData.planEnd()));
            println("I'm at [" + index + "], planning towards [" + planData.currentSegment() + "] where I will arrive with " + Utils.dTS(speedAtCorner) + "km/h");

            if (!corner.isUnknown()) {
                println(corner.toString(true));
                /*TrackSegment.Apex[] apexes = corner.getApexes();
                for (TrackSegment.Apex a : apexes) {
                println("Apex: " + Utils.dTS(a.position) + ", " + Situations.toString(a.type) + ", speed: " + Utils.dTS(getTargetSpeed(a)) + "km/h");
                }*/

            } else {
                println("The corner is an unknown segment.");
            }
            System.out.println("");
        }

        // plan back to front, corner entry
        while (planData.hasMoreSegments()) {
            PlanElement2011 planElement = null;
            if (TEXT_DEBUG && !controller.isPreCompiling()) {
                println("Planning for tracksegment " + planData.currentSegment());
            }

            current = trackModel.getSegment(planData.currentSegment());

            if (current.isUnknown()) {
                planElement = new PlanElement2011(planData.start(),
                        planData.end(), "Unknown segment");
                planElement.attachPosition(new ConstantValue(planData.start(), planData.end(), 0.0));
                planElement.attachSpeed(new ConstantValue(planData.start(), planData.end(), 50.0));

            } else if (current.isStraight()) {
                planElement = moduleStraight.plan(planData, data, trackModel, observer);

            } else { // corner
                planElement = moduleCorner.plan(planData, data, trackModel, observer);
            }

            plan.add(0, planElement);

            planData.popSegment();
        }

        if (TEXT_DEBUG && !controller.isPreCompiling()) {
            println("finished");
        }

        planIndex = 0;

        if (GRAPHICAL_DEBUG) {
            debugPainter.repaint();
        }
    }

    private void planUnknown(SensorData data, TrackSegment current) {
        if (TEXT_DEBUG && !controller.isPreCompiling()) {
            println("");
            println("*****************************************************");
            println("**                  Within unknown segment         **");
            println("*****************************************************");
        }

        plan.clear();
        planIndex = 0;
        double remaining = current.getLength() - (data.getDistanceFromStartLine() - current.getStart());
        double end = data.getDistanceRaced() + remaining;

        if (TEXT_DEBUG && !controller.isPreCompiling()) {
            println("Remaning: " + remaining + "m");
            println("End: " + end + "m");
            println("NextDistance: " + nextDistance);
        }

        if (end <= nextDistance + 10.0) {
            end = nextDistance + 10.0;
            if (TEXT_DEBUG && !controller.isPreCompiling()) {
                println("Too close to nextDistance, moving end to: " + end + "m");
            }
        }

        PlanElement2011 pe = new PlanElement2011(data.getDistanceRaced(), end,
                "Within unknown segment");

        if (Math.abs(data.getTrackPosition()) < 0.1) {
            pe.attachPosition(new ConstantValue(data.getDistanceRaced(), end, 0.0));

        } else {
            double[] x = {data.getDistanceRaced(),
                Math.min(data.getDistanceRaced() + 25.0, (data.getDistanceRaced() + end) / 2.0),
                end};
            double[] y = {data.getTrackPosition(), data.getTrackPosition() / 2.0, 0.0};
            CubicSpline spline = new CubicSpline(x, y);
            spline.setDerivLimits(0.0, 0.0);
            pe.attachPosition(new FlanaganCubicWrapper(spline));
        }
        pe.attachSpeed(new ConstantValue(data.getDistanceRaced(), end, 50.0));

        plan.add(pe);
    }

    public double getTargetSpeed(TrackSegment.Apex a) {
        if (a.unknown) {
            return 50.0;
        }

        if (a.targetSpeed == TrackSegment.DEFAULT_SPEED || EA_MODE) {
            return calcSpeed(a.value) * SPEED_MODIFIER;

        } else {
            return a.targetSpeed * SPEED_MODIFIER;
        }
    }

    @Override
    public void setTrackModel(TrackModel t) {
        this.trackModel = t;        
        observer.setTrackModel(t);
    }

    private void print(String s) {
        System.out.print(s);
    }

    public void println(String s) {
        System.out.println(s);
    }   

    public double getAnchorPoint(TrackSegment first, TrackSegment second) {
        if (first.getDirection() == Situations.DIRECTION_FORWARD && second.getDirection() == Situations.DIRECTION_RIGHT && !second.isFull()) {
            //System.out.println("F / R "+offset);

            return offset;

        } else if (first.getDirection() == Situations.DIRECTION_FORWARD && second.getDirection() == Situations.DIRECTION_LEFT && !second.isFull()) {
            //System.out.println("F / L");

            return -offset;

        } else if (first.getDirection() == Situations.DIRECTION_RIGHT && second.getDirection() == Situations.DIRECTION_FORWARD && !first.isFull()) {
            //System.out.println("R / F");

            return offset;

        } else if (first.getDirection() == Situations.DIRECTION_LEFT && second.getDirection() == Situations.DIRECTION_FORWARD && !first.isFull()) {
            //System.out.println("L / F");

            return -offset;

        } else if (first.getDirection() == Situations.DIRECTION_LEFT && second.getDirection() == Situations.DIRECTION_LEFT) {
            //System.out.println("L / L");

            return -offset;

        } else if (first.getDirection() == Situations.DIRECTION_RIGHT && second.getDirection() == Situations.DIRECTION_RIGHT) {
            //System.out.println("R / R");

            return offset;
        }
        //System.out.println("sonst");
        return 0;
    }

    public void saveOnlineData(String suffix) {
        System.out.println("SaveOnlineData");
        System.out.println("Stage: "+stage);
        System.out.println("OnlineLearner: "+onlineLearner);
        System.out.println("EA MODE: "+EA_MODE);
        if (stage == scr.Controller.Stage.WARMUP && onlineLearner.active() && !EA_MODE) {
            onlineLearner.saveData(suffix);
        }
    }

    /**
     * Calculates the distance needed to switch the position on the track by delta meter.
     * @param delta
     * @return
     */
    public static double calcSwitchDistance(SensorData data, double delta) {
        return calcSwitchDistance(data.getSpeed(), delta);
    }

    public static double calcSwitchDistance(double speed, double delta) {
        int index = Math.max(0, Math.min(M.length-1, (int)Math.floor((speed-50.0+12.5)/25.0)));
        return M[index] * delta + B[index];
    }

    /**
     * Calculates the possible absolute change in trackposition.
     * @param data
     * @param length
     * @return
     */
    public static double calcPossibleSwitchDelta(SensorData data, double length) {
        int index = Math.max(0, Math.min(M.length-1, (int)Math.floor((data.getSpeed()-50.0+12.5)/25.0)));
        return (length - B[index])/M[index];
    }

    private static final double[] M = {13.809, 17.959, 23.32, 28.857,
        34.277, 40.947, 51.484, 60.293, 60.839, 62.842};
    private static final double[] B = {-8.1055, -6.8262, 6.9141, -6.7871,
        -7.6758, -8.9551, -17.891, -9.668, -7.5175, -10.205};

    public static void main(String[] args) {
        Plan p = new Plan2013(new de.janquadflieg.mrracer.controller.MrRacer2012());
        System.out.println(p.calcBrakingZoneCorner(242, 16, 176));
    }

    private class DebugPainter
            extends javax.swing.JPanel {

        /** Debug data. */
        protected ArrayList<SensorData> debugData = new ArrayList<>();
        private final Color DARK_GREEN = new Color(0, 129, 36);
        private final Color ORANGE = new Color(255, 154, 23);
        private double lastPosition = 0.0;

        @Override
        public void paintComponent(Graphics graphics) {
            Graphics2D g = (Graphics2D) graphics;
            try {
                paintComponent(g);
            } catch (Exception e) {
                e.printStackTrace(System.out);
            }
        }

        public void paintComponent(Graphics2D g) {
            ArrayList<PlanElement2011> plan = new ArrayList<>(Plan2013.this.plan);

            if (plan.isEmpty()) {
                return;
            }

            lastPosition = lastData.getDistanceRaced();
            Dimension size = getSize();

            g.setColor(Color.WHITE);
            g.fillRect(0, 0, getWidth(), getHeight());

            g.setColor(Color.BLACK);

            g.translate(10, 10);

            double totalLength = 0.0;
            double rest = 0.0;

            for (int i = 0; i < plan.size(); ++i) {
                totalLength += plan.get(i).getLength();

                if (Plan2013.this.trackModel.initialized()) {
                    double d1 = Plan2013.this.toDistanceFromStartLine(Math.min(
                            (plan.get(i).getStart() + plan.get(i).getEnd()) / 2.0,
                            plan.get(i).getStart() + 0.5));
                    TrackSegment s1 = Plan2013.this.trackModel.getSegment(d1);

                    double d2 = Plan2013.this.toDistanceFromStartLine(Math.max(
                            (plan.get(i).getStart() + plan.get(i).getEnd()) / 2.0,
                            plan.get(i).getEnd() - 0.5));
                    TrackSegment s2 = Plan2013.this.trackModel.getSegment(d2);

                    double tsLength = s1.getLength();

                    if (s1 != s2) {
                        /*System.out.println("Plan "+i+" -> 2 tracksegments!");
                        System.out.println(s1);
                        System.out.println(s2);*/
                        tsLength += s2.getLength();
                    }

                    if (plan.get(i).getLength() + 0.1 < tsLength) {
                        rest = tsLength - plan.get(i).getLength();
                        totalLength += rest;
                    }
                }
            }

            double ppm = (1.0 * (size.width - 20)) / totalLength;

            if (Plan2013.this.trackModel.initialized()) {
                int offset = 0;
                for (int i = 0; i < plan.size(); ++i) {
                    PlanElement2011 e = plan.get(i);

                    double d1 = Plan2013.this.toDistanceFromStartLine(Math.min(
                            (e.getStart() + e.getEnd()) / 2.0,
                            e.getStart() + 0.5));
                    TrackSegment s1 = Plan2013.this.trackModel.getSegment(d1);

                    double d2 = Plan2013.this.toDistanceFromStartLine(Math.max(
                            (e.getStart() + e.getEnd()) / 2.0,
                            e.getEnd() - 0.5));
                    TrackSegment s2 = Plan2013.this.trackModel.getSegment(d2);

                    Dimension d = TrackSegment.Painter.draw(s1, g, ppm,
                            TrackSegment.Painter.DRAW_SUBSEGMENTS | TrackSegment.Painter.DRAW_START_END);
                    offset += d.width;
                    g.translate(d.width, 0);

                    if (s1 != s2) {
                        //System.out.println("s1 != s2, ");
                        d = TrackSegment.Painter.draw(s2, g, ppm,
                                TrackSegment.Painter.DRAW_SUBSEGMENTS | TrackSegment.Painter.DRAW_START_END);
                        offset += d.width;
                        g.translate(d.width, 0);
                    }

                    if (i == plan.size() - 1) {
                        g.translate(0, d.height);
                    }

                    //System.out.println(Utils.dTS(d)+ " "+Utils.dTS(d2));
                }
                g.translate(-offset, 0);
            }

            int offset = (int) Math.round(rest * ppm);

            g.translate(0, 5);

            for (int i = 0; i < plan.size(); ++i) {
                PlanElement2011 e = plan.get(i);

                g.translate(offset, 0);

                Dimension d = PlanElement2011.Painter.draw(e, g, ppm, lastData,
                        debugData);

                g.translate(-offset, 0);

                offset += d.width;
            }
        }
    }
}
