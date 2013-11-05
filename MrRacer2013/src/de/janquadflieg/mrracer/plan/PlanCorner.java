/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.janquadflieg.mrracer.plan;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Properties;

import de.janquadflieg.mrracer.Utils;
import de.janquadflieg.mrracer.classification.Situations;
import de.janquadflieg.mrracer.opponents.OpponentObserver;
import de.janquadflieg.mrracer.telemetry.SensorData;
import de.janquadflieg.mrracer.track.*;

/**
 *
 * @author quad
 */
public class PlanCorner
implements Planner{

    private Plan2011 plan;
    private SpeedPredictor speedPredictor = new SpeedPredictor();

    public PlanCorner(Plan2011 p){
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

    private final PlanElement2011 plan(PlanStackData planData,
            SensorData data, TrackModel trackModel, OpponentObserver observer,
            boolean output) {
        TrackSegment current = trackModel.getSegment(planData.currentSegment());

        double start = planData.start();
        double end = planData.end();
        double offsetFromStart = 0.0;    // offset within the segment

        TrackSegment.Apex[] apexes = current.getApexes();

        if (Plan.TEXT_DEBUG) {
            plan.println("");
            plan.println("----------  CORNER  ---------------");
            plan.println("Start/End: " + Utils.dTS(start) + ", " + Utils.dTS(end) + " [" + Utils.dTS(end - start) + "m]");
            for (int i = 0; i < apexes.length; ++i) {
                TrackSegment.Apex a = apexes[i];
                plan.println("Apex[" + i + "]: " + a.position + ", " + Situations.toString(a.type) + ", speed: " + Utils.dTS(plan.getTargetSpeed(a)) + "km/h");
            }
        }

        // if this is the first segment to plan for, adjust start
        if (planData.first()) {
            offsetFromStart = data.getDistanceFromStartLine() - current.getStart();

            if (offsetFromStart > 0.0) {
                if (Plan.TEXT_DEBUG) {
                    plan.println("This is the first segment to plan for and i'm "
                            + offsetFromStart + "m into the segment, adjusting start.");
                }
                start -= offsetFromStart;
                if (Plan.TEXT_DEBUG) {
                    plan.println("New Start/End: " + Utils.dTS(start) + ", " + Utils.dTS(end) + " [" + Utils.dTS(end - start) + "m]");
                }
            }
        }

        if (planData.last()) {
            // look ahead to avoid surprisingly slow corners
            plan.calcApproachSpeed(planData);
        }

        double[] apexSpeeds = new double[apexes.length];
        for (int i = 0; i < apexes.length; ++i) {
            apexSpeeds[i] = plan.getTargetSpeed(apexes[i]);
        }

        if (Plan.TEXT_DEBUG) {
            planData.print();
            plan.println("");
        }

        ArrayList<Point2D> points = new ArrayList<>();

        // last point
        points.add(0, new Point2D.Double(end, planData.approachSpeed));

        for (int i = 0; i < apexes.length; ++i) {
            if (Plan.TEXT_DEBUG) {
                plan.println(i + "/" + (apexes.length - 1));
            }
            // init helper data
            double length;
            double preSpeed;
            double nextSpeed;
            double brakeDistance;

            if (i == 0) {
                // last apex
                length = current.getEnd() - apexes[apexes.length - (i + 1)].position;
                preSpeed = apexSpeeds[apexSpeeds.length - (i + 1)];
                nextSpeed = planData.approachSpeed;

            } else {
                length = apexes[apexes.length - i].position
                        - apexes[apexes.length - (i + 1)].position;
                preSpeed = apexSpeeds[apexSpeeds.length - (i + 1)];
                nextSpeed = apexSpeeds[apexSpeeds.length - i];
            }

            brakeDistance = plan.calcBrakeDistanceCorner(preSpeed, nextSpeed);

            if (Plan.TEXT_DEBUG) {
                plan.println("Length: " + length + ", brakeDistance: " + brakeDistance);
            }

            if (brakeDistance >= length) {
                double saveSpeed = plan.calcApproachSpeedCorner(nextSpeed, length);

                apexSpeeds[apexSpeeds.length - (i + 1)] = saveSpeed;

                if (Plan.TEXT_DEBUG) {
                    plan.println("Save speed: " + Utils.dTS(saveSpeed) + "km/h");
                }

            } else {
                double bz = plan.calcBrakingZoneCorner(preSpeed, length, nextSpeed);
                double accSpeed = speedPredictor.predictSpeed(preSpeed, (length - bz));
                double position = start + (apexes[apexes.length - (i + 1)].position - current.getStart() + (length - bz));

                if (Plan.TEXT_DEBUG) {
                    plan.println("Calculated braking zone: " + bz + "m");
                    plan.println("In apex acceleration for " + (length - bz) + "m, " + accSpeed + "km/h");
                    plan.println("Position: " + position + ", last position: " + points.get(0).getX());
                }

                if (bz >= 0.0 && length > bz) {
                    if(bz > 0.0){
                        points.add(0, new Point2D.Double(position, accSpeed));
                    }

                    double smallOffset = (length - bz) * 0.01;
                    position = start + (apexes[apexes.length - (i + 1)].position - current.getStart() + smallOffset);
                    points.add(0, new Point2D.Double(position, accSpeed));
                }
            }

            // apex
            points.add(0, new Point2D.Double(start + (apexes[apexes.length - (i + 1)].position - current.getStart()),
                    apexSpeeds[apexSpeeds.length - (i + 1)]));

            // if this was the last apex, check / set the approach speed
            if (i == apexes.length - 1) {
                length = apexes[apexes.length - (i + 1)].position - current.getStart();
                preSpeed = planData.speed();
                nextSpeed = apexSpeeds[apexSpeeds.length - (i + 1)];

                brakeDistance = plan.calcBrakeDistanceCorner(preSpeed, nextSpeed);

                if (Plan.TEXT_DEBUG) {
                    plan.println("Checking corner entry....");
                    plan.println("Length: " + length + ", brakeDistance: " + brakeDistance);
                }

                if (brakeDistance > length) {
                    double saveSpeed = plan.calcApproachSpeedCorner(nextSpeed, length);

                    planData.approachSpeed = saveSpeed;

                    if (Plan.TEXT_DEBUG) {
                        plan.println("Save speed: " + Utils.dTS(saveSpeed) + "km/h");
                    }

                } else {
                    planData.approachSpeed = planData.speed();
                }
            }
        }

        // first point, approachSpeed wurde oben in der Schleife gesetzt!
        points.add(0, new Point2D.Double(start, planData.approachSpeed));

        if (Plan.TEXT_DEBUG) {
            plan.println("Speeds:");
            for (int i = 0; i < points.size(); ++i) {
                plan.println(points.get(i).getX() + ", " + points.get(i).getY());
            }
            plan.println("-------------------------------");
        }

        LinearInterpolator speed = new LinearInterpolator(points);

        PlanElement2011 element = new PlanElement2011(start, end, "Turn in");
        element.setSpeed(speed);

        return element;
    }
}
