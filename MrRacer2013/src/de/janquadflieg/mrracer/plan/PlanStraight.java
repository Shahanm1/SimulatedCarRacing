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
public final class PlanStraight
        implements Planner {

    private Plan plan;

    public PlanStraight(Plan p) {
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
        plan(planData, data, trackModel, observer, false);
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


        double currStart = start;
        double remainingLength = end - start;
        double currPosition = plan.getAnchorPoint(prev, current);
        if (planLikeLast) {
            currPosition = data.getTrackPosition();
        }

        if (Plan.TEXT_DEBUG && planning) {
            plan.println(Utils.dTS(remainingLength) + "m remain...");
        }

        if (targetPosition != OpponentObserver.NO_RECOMMENDED_POINT) {
            if (Plan.TEXT_DEBUG && planning) {
                plan.println("I need to take care of other cars, point is " + targetPosition.toString());
                plan.println("Right now, i'm planning with currStart: " + Utils.dTS(currStart));
            }
            if (targetPosition.getY() < currStart) {
                if (Plan.TEXT_DEBUG && planning) {
                    plan.println("Point is before currStart, moving it to +1m");
                }
                targetPosition.setLocation(targetPosition.getX(), targetPosition.getY() + 1.0);
            }

            if (Plan.TEXT_DEBUG && planning) {
                plan.println("Checking, if there is enough room...");
            }

            double lengthNeeded = targetPosition.getY() - currStart;

            if (Plan.TEXT_DEBUG && planning) {
                plan.println("I need at least " + Utils.dTS(lengthNeeded) + "m, " + Utils.dTS(remainingLength) + "m remain");
            }

            if (remainingLength < lengthNeeded) {
                targetPosition = OpponentObserver.NO_RECOMMENDED_POINT;
                if (Plan.TEXT_DEBUG && planning) {
                    plan.println("Cannot overtake, not enough room");
                }
            }
        }

        if (targetPosition != OpponentObserver.NO_RECOMMENDED_POINT) {
            if (Plan.TEXT_DEBUG && planning) {
                plan.println("Trying to overtake");
            }

            // switch Position 1
            double[] xP = new double[3];
            double[] yP = new double[3];

            xP[0] = currStart;
            xP[2] = targetPosition.getY();
            xP[1] = (xP[0] + xP[2]) / 2.0;

            yP[0] = currPosition;
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
            remainingLength = end - currStart;
            currPosition = targetPosition.getX();


            // overtaking
            if (Plan.TEXT_DEBUG && planning) {
                plan.println("Overtaking line");
            }

            xP = new double[3];

            xP[0] = currStart;
            xP[2] = end - 150.0;

            if (Plan.TEXT_DEBUG && planning) {
                plan.println("Overtaking line: " + Utils.dTS(currStart) + " " + Utils.dTS(end - 150.0) + " " + targetPosition.getX());
            }

            positions.add(new ConstantValue(xP[0], xP[2], targetPosition.getX()));

            currStart = xP[2];
            remainingLength = end - currStart;
            currPosition = targetPosition.getX();
        }


        // simply drive towards the target position for the next corner
        if (Plan.TEXT_DEBUG && planning) {
            plan.println("Planning towards the next corner...");
        }

        double[] xP = new double[3];
        double[] yP = new double[3];

        xP[0] = currStart;
        xP[2] = end;
        xP[1] = (xP[0] + xP[2]) / 2.0;



        yP[0] = currPosition;

        double absCurrPosition = SensorData.calcAbsoluteTrackPosition(currPosition, trackModel.getWidth());
        double possibleDelta = Plan2013.calcPossibleSwitchDelta(data, end - currStart);
        double anchor = plan.getAnchorPoint(current, next);
        double absDesiredPosition = SensorData.calcAbsoluteTrackPosition(anchor, trackModel.getWidth());

        if (Plan.TEXT_DEBUG && planning) {
            plan.println("absCurrPosition: " + Utils.dTS(absCurrPosition));
            plan.println("PossibleDelta: " + Utils.dTS(possibleDelta));
            plan.println("absDesiredPosition: " + Utils.dTS(absDesiredPosition));
        }

        if (Math.abs(absDesiredPosition - absCurrPosition) <= possibleDelta) {
            if (Plan.TEXT_DEBUG && planning) {
                plan.println("Positioning ok");
            }
            yP[2] = plan.getAnchorPoint(current, next);

        } else {
            // move to the right
            if (anchor < 0) {
                yP[2] = SensorData.calcRelativeTrackPosition(absCurrPosition + possibleDelta, trackModel.getWidth());

            } else {
                // move to the left
                yP[2] = SensorData.calcRelativeTrackPosition(absCurrPosition - possibleDelta, trackModel.getWidth());
            }
        }

        yP[1] = (yP[0] + yP[2]) / 2.0;

        if (Plan.TEXT_DEBUG && planning) {
            plan.println("Position towards the next corner:");
            for (int k = 0; k < xP.length; ++k) {
                plan.println(xP[k] + " , " + yP[k]);
            }
        }

        CubicSpline spline = new CubicSpline(xP, yP);
        spline.setDerivLimits(0.0, 0.0);
        positions.add(new FlanaganCubicWrapper(spline));

        // target speed
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

        try{
            speed = new LinearInterpolator(xS, yS);

        } catch(RuntimeException e){
            System.out.println("*****************EXCEPTION**************");
            System.out.println("Start/End: " + Utils.dTS(start) + ", " + Utils.dTS(end) + " [" + Utils.dTS(end - start) + "m]" + " - ");
            System.out.println("Segment: "+current.toString());
            System.out.println("Start: "+start);
            System.out.println("End: "+end);
            System.out.println("BrakeDistance: "+brakeDistance);
            System.out.println("Data:");
            try{
                java.io.OutputStreamWriter osw = new java.io.OutputStreamWriter(System.out);
                SensorData.writeHeader(osw);
                osw.append('\n');
                data.write(osw);
                osw.flush();
                
            } catch(Exception schwupp){
                
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

    public static void main(String[] args) {
        try {
            PlanStackData planData = new PlanStackData(10);
            
            TrackModel model = TrackModel.load("wheel2.saved_model");

            

            Point2D.Double point = new Point2D.Double(0.5, 90.0);
            OpponentObserver obs = new de.janquadflieg.mrracer.opponents.DebugObserver(point);

            double dist = 5.0;

            planData.addSegment(0, model.getSegment(0).getLength()-dist, 25.0);

            String msg = "(angle 0)(curLapTime 10.21)(damage 0)(distFromStart "+String.valueOf(dist)+")(distRaced 10.0)(fuel 94)(gear 5)(lastLapTime 0)(opponents 200 200 200 200 200 200 200 200 200 200 200 200 200 200 200 200 200 200 200 200 200 200 200 200 200 200 200 200 200 200 200 200 200 200 200 200)(racePos 1)(rpm 942.478)(speedX 170.0)(speedY 0)(speedZ 0.0196266)(track 4.00001 4.06171 4.25672 4.61881 5.22164 6.22291 8.00001 11.6952 23.0351 200 46.0701 23.3904 16 12.4458 10.4433 9.2376 8.51342 8.12341 7.99999)(trackPos 0.333332)(wheelSpinVel 0 0 0 0)(z 0.339955)(focus -1 -1 -1 -1 -1)";

            PlanStraight planner = new PlanStraight(new Plan2011(new de.janquadflieg.mrracer.controller.MrRacer2012()));

            planner.plan(planData, new SensorData(new scr.MessageBasedSensorModel(msg)), model, obs);


        } catch (Exception e) {
            e.printStackTrace(System.out);
        }
    }
}
