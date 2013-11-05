/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.janquadflieg.mrracer.plan;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Properties;

import de.janquadflieg.mrracer.Utils;
import de.janquadflieg.mrracer.functions.ConstantValue;
import de.janquadflieg.mrracer.functions.FlanaganCubicWrapper;
import de.janquadflieg.mrracer.functions.Interpolator;
import de.janquadflieg.mrracer.opponents.OpponentObserver;
import de.janquadflieg.mrracer.telemetry.SensorData;
import de.janquadflieg.mrracer.track.*;

import flanagan.interpolation.CubicSpline;

/**
 *
 * @author quad
 */
public final class PlanStraight2013
        implements Planner {

    private Plan plan;
    /** The minimum absolute delta needed to switch the position at all. */
    private static final double MIN_RELEVANT_ABS_DELTA = 0.2;
    /** The minimum length to plan a straight line before the corner. */
    private static final double MIN_RELEVANT_STRAIGHT = 1.0;

    public PlanStraight2013(Plan p) {
        this.plan = p;
    }

    public void setParameters(Properties params, String prefix) {
    }

    public void getParameters(Properties params, String prefix) {
    }

    public void paint(String baseFileName, java.awt.Dimension d) {
    }

    public final void calcApproachSpeed(PlanStackData planData,
            SensorData data, TrackModel trackModel, OpponentObserver observer) {
        plan(planData, data, trackModel, observer, true);
    }

    public final PlanElement2011 plan(PlanStackData planData,
            SensorData data, TrackModel trackModel, OpponentObserver observer) {
        return plan(planData, data, trackModel, observer, true);
    }

    private PlanElement2011 plan(PlanStackData planData,
            SensorData data, TrackModel trackModel, OpponentObserver observer,
            boolean planning) {
        int index = planData.currentSegment();
        TrackSegment current = trackModel.getSegment(index);
        TrackSegment next = trackModel.getSegment(trackModel.incrementIndex(index));
        TrackSegment prev = trackModel.getSegment(trackModel.decrementIndex(index));

        double end = planData.end();
        double start = planData.start();

        if (Plan.TEXT_DEBUG && planning) {
            plan.println("");
            plan.println("------------ PlanStraight------------");
            plan.println("Start/End: " + Utils.dTS(start) + ", " + Utils.dTS(end) + " [" + Utils.dTS(end - start) + "m]" + " - ");
            planData.print();
            plan.println("");
        }

        // plan like the last element?
        boolean planLikeLast = current.contains(data.getDistanceFromStartLine());

        if (planLikeLast) {
            start = data.getDistanceRaced();

            if (Plan.TEXT_DEBUG && planning) {
                plan.println("This is the last segment to plan for...");
            }
        }

        // check if we can combine the planning of this segment and the previous
        if (!planLikeLast && prev.isStraight()) {
            if (Plan.TEXT_DEBUG && planning) {
                plan.println("Previous is also straight, checking further...");
            }
            if (start - prev.getLength() <= data.getDistanceRaced()) {
                if (Plan.TEXT_DEBUG && planning) {
                    plan.println("Prev is the last segment to plan for");
                }
                start = data.getDistanceRaced();
                planLikeLast = true;

            } else {
                if (Plan.TEXT_DEBUG && planning) {
                    plan.println("Prev is also a middle segment");
                }
                start -= prev.getLength();
                int prevIndex = trackModel.decrementIndex(index);
                int prevPrevIndex = trackModel.decrementIndex(prevIndex);
                if (Plan.TEXT_DEBUG && planning) {
                    plan.println("Switching prev from " + prevIndex + " to " + prevPrevIndex);
                }
                prev = trackModel.getSegment(prevPrevIndex);
            }

            if (Plan.TEXT_DEBUG && planning) {
                plan.println("Moving start to " + Utils.dTS(start) + ", new length " + Utils.dTS(end - start) + "m");
            }
            planData.popSegment();
        }



        Point2D targetPosition = OpponentObserver.NO_RECOMMENDED_POINT;

        if (observer.otherCars() && observer.getRecommendedPosition() != OpponentObserver.NO_RECOMMENDED_POINT) {
            Point2D recommendation = new Point2D.Double();
            recommendation.setLocation(observer.getRecommendedPosition());

            if (start <= recommendation.getY() && recommendation.getY() < end) {
                targetPosition = new Point2D.Double();
                targetPosition.setLocation(recommendation);
            }
        }

        // track positions
        ArrayList<Interpolator> positions = new ArrayList<>();

        double currStart = start;   // current start
        double remainingLength = end - start;   // remaining length in this segment
        double currPosition = plan.getAnchorPoint(prev, current);   // the current position in the track
        if (planLikeLast) {
            currPosition = data.getTrackPosition();
        }

        if (Plan.TEXT_DEBUG && planning) {
            plan.println(Utils.dTS(remainingLength) + "m remain in this segment...");
        }

        if (targetPosition != OpponentObserver.NO_RECOMMENDED_POINT) {
            if (Plan.TEXT_DEBUG && planning) {
                plan.println("I need to take care of other cars, point is " + targetPosition.toString());
                plan.println("Right now, i'm planning with currStart: " + Utils.dTS(currStart));
                plan.println("Type of the target position: " + observer.getPositionType().toString());
            }
            if (targetPosition.getY() < currStart) {
                if (Plan.TEXT_DEBUG && planning) {
                    plan.println("Point is before currStart, moving it to +1m");
                }
                targetPosition.setLocation(targetPosition.getX(), currStart + 1.0);
            }

            if (observer.getPositionType() == OpponentObserver.PositionType.OVERTAKING) {
                if (Plan.TEXT_DEBUG && planning) {
                    plan.println("Checking, if there is enough room...");
                }

                double lengthNeeded = targetPosition.getY() - currStart;

                if (Plan.TEXT_DEBUG && planning) {
                    plan.println("Observer tells me, that I need at least " + Utils.dTS(lengthNeeded) + "m, " + Utils.dTS(remainingLength) + "m remain");
                }

                if (remainingLength < lengthNeeded) {
                    targetPosition = OpponentObserver.NO_RECOMMENDED_POINT;
                    if (Plan.TEXT_DEBUG && planning) {
                        plan.println("Cannot overtake, not enough room");
                    }
                }

            } else if (observer.getPositionType() == OpponentObserver.PositionType.BLOCKING) {
                if (Plan.TEXT_DEBUG && planning) {
                    plan.println("Trying to block");
                }
            }
        }

        if (targetPosition != OpponentObserver.NO_RECOMMENDED_POINT) {
            if (observer.getPositionType() == OpponentObserver.PositionType.OVERTAKING) {
                Point2D planEnd = planRaceLineOvertake(positions, currStart, end,
                        currPosition, targetPosition, planning);
                currStart = planEnd.getY();
                currPosition = planEnd.getX();
                remainingLength = end - currStart;

            } else {
                Point2D planEnd = planRaceLineBlock(positions, currStart, end,
                        currPosition, targetPosition, planning);
                currStart = planEnd.getY();
                currPosition = planEnd.getX();
                remainingLength = end - currStart;
            }
        }

        // simply drive towards the target position for the next corner
        if (Plan.TEXT_DEBUG && planning) {
            plan.println("Planning towards the next corner...");
            plan.println("Current start: " + Utils.dTS(currStart));
            plan.println("Current position: " + Utils.dTS(currPosition));
            plan.println("Remaining length: " + Utils.dTS(remainingLength));
        }

        // switch position
        if (currStart != end) {
            Point2D planEnd = planSwitchPosition(positions, data, trackModel,
                    current, next, currStart, end, currPosition, planning);
            currStart = planEnd.getY();
            currPosition = planEnd.getX();
            remainingLength = end - currStart;
        }

        if (currStart != end) {
            if (Plan.TEXT_DEBUG && planning) {
                plan.println("Adding straight part:");
            }

            Interpolator brakeLine = new ConstantValue(currStart, end, currPosition);
            positions.add(brakeLine);
        }

        if (Plan.TEXT_DEBUG && planning) {
            plan.println("Checking the final position");
        }

        double absFinalPosition = SensorData.calcAbsoluteTrackPosition(currPosition, trackModel.getWidth());
        double absDesiredPosition = SensorData.calcAbsoluteTrackPosition(plan.getAnchorPoint(current, next), trackModel.getWidth());

        if (Math.abs(absFinalPosition - absDesiredPosition) > 1.0) {
            if (Plan.TEXT_DEBUG && planning) {
                plan.println("Difference > 1.0");
                plan.println("Reducing approach speed from " + Utils.dTS(planData.approachSpeed) + "km/h");
            }

            if (planData.approachSpeed == Plan2013.MAX_SPEED) {
                planData.approachSpeed = 240.0;

            } else {
                planData.approachSpeed *= 0.8;
            }

            if (Plan.TEXT_DEBUG && planning) {
                plan.println("to " + Utils.dTS(planData.approachSpeed) + "km/h");
            }
        }

        // target speeds
        double brakeDistance = 0.0;

        if (planData.approachSpeed != Plan2013.MAX_SPEED) {
            if (planData.first()) {
                brakeDistance = plan.calcBrakingZoneStraight(data.getSpeed(), end - start,
                        planData.approachSpeed);
            } else {
                brakeDistance = plan.calcBrakingZoneStraight(planData.speed(), end - start,
                        planData.approachSpeed);
            }
        }

        if (Plan.TEXT_DEBUG && planning) {
            plan.println("Brake distance: " + Utils.dTS(brakeDistance) + "m");
        }

        double[] xS;
        double[] yS;

        if (brakeDistance >= end - start || brakeDistance == 0.0) {
            xS = new double[3];
            yS = new double[3];

            xS[0] = start;
            xS[2] = end;
            xS[1] = (xS[0] + xS[2]) / 2.0;

            if (brakeDistance == 0.0) {
                yS[0] = Plan2013.MAX_SPEED;
                yS[1] = Plan2013.MAX_SPEED;
                yS[2] = Plan2013.MAX_SPEED;
                planData.approachSpeed = Plan2013.MAX_SPEED;

            } else {
                yS[0] = planData.approachSpeed;
                yS[1] = planData.approachSpeed;
                yS[2] = planData.approachSpeed;
                planData.approachSpeed = plan.calcApproachSpeedStraight(planData.approachSpeed, end - start);
            }

        } else {
            xS = new double[4];
            yS = new double[4];

            xS[0] = start;
            xS[1] = end - brakeDistance;
            xS[2] = end - (brakeDistance * 0.99);
            xS[3] = end;

            yS[0] = Plan2013.MAX_SPEED;
            yS[1] = Plan2013.MAX_SPEED;
            yS[2] = planData.approachSpeed;
            yS[3] = planData.approachSpeed;

            planData.approachSpeed = Plan2013.MAX_SPEED;
        }

        if (Plan.TEXT_DEBUG && planning) {
            plan.println("Speed:");
            for (int i = 0; i < xS.length; ++i) {
                plan.println(xS[i] + " , " + yS[i]);
            }
        }

        LinearInterpolator speed;

        try {
            speed = new LinearInterpolator(xS, yS);

        } catch (RuntimeException e) {
            System.out.println("*****************EXCEPTION**************");
            System.out.println("Start/End: " + Utils.dTS(start) + ", " + Utils.dTS(end) + " [" + Utils.dTS(end - start) + "m]" + " - ");
            System.out.println("Segment: " + current.toString());
            System.out.println("Start: " + start);
            System.out.println("End: " + end);
            System.out.println("BrakeDistance: " + brakeDistance);
            System.out.println("SensorData:");
            try {
                java.io.OutputStreamWriter osw = new java.io.OutputStreamWriter(System.out);
                SensorData.writeHeader(osw);
                osw.append('\n');
                data.write(osw);
                osw.flush();

            } catch (Exception schwupp) {
            }
            System.out.println("");
            System.out.println("Speed:");
            for (int i = 0; i < xS.length; ++i) {
                System.out.println(xS[i] + " , " + yS[i]);
            }
            System.out.println("Complete Model:");
            trackModel.print();

            throw e;
        }

        PlanElement2011 element = new PlanElement2011(xS[0], xS[xS.length - 1],
                "Accelerate");

        for (Interpolator cs : positions) {
            element.attachPosition(cs);
        }

        element.setSpeed(speed);

        return element;
    }

    /**
     * Plans the normal racing line towards the next corner.
     */
    private Point2D planSwitchPosition(ArrayList<Interpolator> positions,
            SensorData data, TrackModel trackModel, TrackSegment current,
            TrackSegment next,
            double currStart, double end, double currPosition,
            boolean planning) {
        // the current positon in the absolute coordinate system
        double absCurrPosition = SensorData.calcAbsoluteTrackPosition(currPosition, trackModel.getWidth());
        // the possible delta
        double possibleDelta = Plan2013.calcPossibleSwitchDelta(data, end - currStart);
        // the anchor point towards the next tracksegment
        double anchor = plan.getAnchorPoint(current, next);
        // the anchor in the absolute coordinate system
        double absDesiredPosition = SensorData.calcAbsoluteTrackPosition(anchor, trackModel.getWidth());
        // the length needed to switch to the desired position
        double lengthNeeded = Plan2013.calcSwitchDistance(data, Math.abs(absCurrPosition - absDesiredPosition));
        // remaining length
        double remainingLength = end - currStart;

        if (Plan.TEXT_DEBUG && planning) {
            plan.println("absCurrPosition: " + Utils.dTS(absCurrPosition));
            plan.println("PossibleDelta: " + Utils.dTS(possibleDelta));
            plan.println("DesiredDelta: " + Utils.dTS(Math.abs(absDesiredPosition - absCurrPosition)));
            plan.println("absDesiredPosition: " + Utils.dTS(absDesiredPosition));
            plan.println("length needed: " + Utils.dTS(lengthNeeded));
        }

        if (lengthNeeded >= remainingLength) {
            if (Plan.TEXT_DEBUG && planning) {
                plan.println("\tNot enough room, calculating what is possible");
            }

            if (absDesiredPosition < absCurrPosition) {
                // move to the left
                absDesiredPosition = absCurrPosition - possibleDelta;

            } else {
                // move to the right
                absDesiredPosition = absCurrPosition + possibleDelta;
            }

            anchor = SensorData.calcRelativeTrackPosition(absDesiredPosition, trackModel.getWidth());
            lengthNeeded = remainingLength;

            if (Plan.TEXT_DEBUG && planning) {
                plan.println("\tNew absDesiredPosition: " + Utils.dTS(absDesiredPosition));
                plan.println("\tNew anchor: " + Utils.dTS(anchor));
            }
        }

        // check if we want to switch the position at all
        if (Math.abs(absDesiredPosition - absCurrPosition) < MIN_RELEVANT_ABS_DELTA) {
            if (Plan.TEXT_DEBUG && planning) {
                plan.println("Close engough to the desired position - nothing to do");
            }
            Point2D.Double result = new Point2D.Double(currPosition, currStart);
            return result;
        }

        double[] xP = new double[3];
        double[] yP = new double[3];

        xP[0] = currStart;
        xP[2] = currStart + lengthNeeded;
        xP[1] = (xP[0] + xP[2]) / 2.0;

        yP[0] = currPosition;
        yP[2] = anchor;
        yP[1] = (yP[0] + yP[2]) / 2.0;

        if (remainingLength - lengthNeeded < MIN_RELEVANT_STRAIGHT) {
            if (Plan.TEXT_DEBUG && planning) {
                plan.println("So close to the corner, moving end of switch to end of segment");
            }
            xP[2] = end;
            xP[1] = (xP[0] + xP[2]) / 2.0;
        }

        if (Plan.TEXT_DEBUG && planning) {
            plan.println("Positioning towards the next corner:");
            for (int k = 0; k < xP.length; ++k) {
                plan.println(xP[k] + " , " + yP[k]);
            }
        }

        CubicSpline spline = new CubicSpline(xP, yP);
        spline.setDerivLimits(0.0, 0.0);
        positions.add(new FlanaganCubicWrapper(spline));



        return new Point2D.Double(yP[2], xP[2]);
    }

    /**
     * Plans the racing line to block other cars.
     */
    private Point2D planRaceLineBlock(ArrayList<Interpolator> positions, double currStart, double end, double currentPosition,
            Point2D targetPosition, boolean planning) {
        if (Plan.TEXT_DEBUG && planning) {
            plan.println("Trying to block");
        }

        // switch Position 1
        double[] xP = new double[3];
        double[] yP = new double[3];

        xP[0] = currStart;
        xP[2] = targetPosition.getY();
        xP[1] = (xP[0] + xP[2]) / 2.0;

        yP[0] = currentPosition;
        yP[2] = targetPosition.getX();
        yP[1] = (yP[0] + yP[2]) / 2.0;

        if (Plan.TEXT_DEBUG && planning) {
            plan.println("Switch Position 1:");
            for (int k = 0; k < xP.length; ++k) {
                plan.println(xP[k] + " , " + yP[k]);
            }
        }

        CubicSpline spline = new CubicSpline(xP, yP);
        spline.setDerivLimits(0.0, 0.0);
        positions.add(new FlanaganCubicWrapper(spline));

        currStart = xP[2];
        double remainingLength = end - currStart;
        currentPosition = targetPosition.getX();

        // block line
        if (Plan.TEXT_DEBUG && planning) {
            plan.println("Block line, remaining length: " + Utils.dTS(remainingLength));
            plan.println("Blocking line is: " + Utils.dTS(currStart) + " " + Utils.dTS(end) + " " + targetPosition.getX());
        }

        positions.add(new ConstantValue(currStart, end, targetPosition.getX()));

        return new Point2D.Double(targetPosition.getX(), end);
    }

    /**
     * Plans the racing line to overtake other cars. Returns the race distance
     * at which this method stopped to plan.
     */
    private Point2D planRaceLineOvertake(ArrayList<Interpolator> positions, double currStart, double end, double currentPosition,
            Point2D targetPosition, boolean planning) {
        if (Plan.TEXT_DEBUG && planning) {
            plan.println("Trying to overtake");
        }

        // switch Position 1
        double[] xP = new double[3];
        double[] yP = new double[3];

        xP[0] = currStart;
        xP[2] = targetPosition.getY();
        xP[1] = (xP[0] + xP[2]) / 2.0;

        yP[0] = currentPosition;
        yP[2] = targetPosition.getX();
        yP[1] = (yP[0] + yP[2]) / 2.0;

        if (Plan.TEXT_DEBUG && planning) {
            plan.println("Switch Position 1:");
            for (int k = 0; k < xP.length; ++k) {
                plan.println(xP[k] + " , " + yP[k]);
            }
        }

        CubicSpline spline = new CubicSpline(xP, yP);
        spline.setDerivLimits(0.0, 0.0);
        positions.add(new FlanaganCubicWrapper(spline));

        currStart = xP[2];
        double remainingLength = end - currStart;
        currentPosition = targetPosition.getX();

        // overtaking line
        if (Plan.TEXT_DEBUG && planning) {
            plan.println("Overtaking line, remaining length: " + Utils.dTS(remainingLength));
        }

        final double ols = currStart;
        double ole;

        if (remainingLength > 11.0) {
            ole = currStart + 10.0;

        } else {
            ole = end;
        }

        if (Plan.TEXT_DEBUG && planning) {
            plan.println("Overtaking line: " + Utils.dTS(ols) + " " + Utils.dTS(ole) + " " + targetPosition.getX());
        }

        positions.add(new ConstantValue(ols, ole, targetPosition.getX()));

        return new Point2D.Double(targetPosition.getX(), ole);
    }

    /*public static void main(String[] args) {
    try {
    PlanStackData planData = new PlanStackData(10);

    TrackModel model = TrackModel.load("wheel2.saved_model");



    Point2D.Double point = new Point2D.Double(0.5, 90.0);
    OpponentObserver obs = new de.janquadflieg.mrracer.opponents.DebugObserver(point);

    double dist = 5.0;

    planData.addSegment(0, model.getSegment(0).getLength() - dist, 25.0);

    String msg = "(angle 0)(curLapTime 10.21)(damage 0)(distFromStart " + String.valueOf(dist) + ")(distRaced 10.0)(fuel 94)(gear 5)(lastLapTime 0)(opponents 200 200 200 200 200 200 200 200 200 200 200 200 200 200 200 200 200 200 200 200 200 200 200 200 200 200 200 200 200 200 200 200 200 200 200 200)(racePos 1)(rpm 942.478)(speedX 170.0)(speedY 0)(speedZ 0.0196266)(track 4.00001 4.06171 4.25672 4.61881 5.22164 6.22291 8.00001 11.6952 23.0351 200 46.0701 23.3904 16 12.4458 10.4433 9.2376 8.51342 8.12341 7.99999)(trackPos 0.333332)(wheelSpinVel 0 0 0 0)(z 0.339955)(focus -1 -1 -1 -1 -1)";

    PlanStraight2013 planner = new PlanStraight2013(new Plan2011(new de.janquadflieg.mrracer.controller.MrRacer2012()));

    planner.plan(planData, new SensorData(new scr.MessageBasedSensorModel(msg)), model, obs);


    } catch (Exception e) {
    e.printStackTrace(System.out);
    }
    }*/
}