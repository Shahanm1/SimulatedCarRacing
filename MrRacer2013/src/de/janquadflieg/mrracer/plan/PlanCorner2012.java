/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.janquadflieg.mrracer.plan;


import java.util.Properties;

import de.janquadflieg.mrracer.Utils;
import de.janquadflieg.mrracer.classification.Situations;
import de.janquadflieg.mrracer.functions.*;
import de.janquadflieg.mrracer.opponents.OpponentObserver;
import de.janquadflieg.mrracer.telemetry.SensorData;
import de.janquadflieg.mrracer.track.*;

/**
 *
 * @author quad
 */
public class PlanCorner2012
        implements Planner {

    private Plan plan;

    public PlanCorner2012(Plan p) {
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
        planConstantApexSpeed(planData, data, trackModel, observer, false);
    }

    public final PlanElement2011 plan(PlanStackData planData,
            SensorData data, TrackModel trackModel, OpponentObserver observer) {

        return planConstantApexSpeed(planData, data, trackModel, observer, true);
    }

    /**
     * New variant: Constant speed within the apex segments.
     */
    private PlanElement2011 planConstantApexSpeed(PlanStackData planData,
            SensorData data, TrackModel trackModel, OpponentObserver observer,
            boolean planning) {
        TrackSegment current = trackModel.getSegment(planData.currentSegment());

        double start = planData.start();
        double end = planData.end();

        TrackSegment.Apex[] apexes = current.getApexes();

        if (Plan.TEXT_DEBUG && planning) {
            plan.println("");
            plan.println("----------  CORNER[Constant Apex Speed]  ---------------");
            plan.println("Start/End: " + Utils.dTS(start) + ", " + Utils.dTS(end) + " [" + Utils.dTS(end - start) + "m]");
            for (int i = 0; i < apexes.length; ++i) {
                TrackSegment.Apex a = apexes[i];
                plan.println("Apex[" + i + "]: " + Utils.dTS(a.position) + ", " + Situations.toString(a.type) + ", speed: " + Utils.dTS(plan.getTargetSpeed(a)) + "km/h");
            }
        }

        double offsetFromStart = 0.0;    // offset within the segment
        // if this is the first segment to plan for, adjust start
        if (planData.first()) {
            offsetFromStart = data.getDistanceFromStartLine() - current.getStart();

            if (offsetFromStart > 0.0) {
                if (Plan.TEXT_DEBUG && planning) {
                    plan.println("This is the first segment to plan for and i'm "
                            + Utils.dTS(offsetFromStart) + "m into the segment - adjusting start.");
                }
                start -= offsetFromStart;
                if (Plan.TEXT_DEBUG && planning) {
                    plan.println("New Start/End: " + Utils.dTS(start) + ", " + Utils.dTS(end) + " [" + Utils.dTS(end - start) + "m]");
                }
            }
        }

        if (planData.last() && planning) {
            if (Plan.TEXT_DEBUG && planning) {
                System.out.println("This corner is the last element to plan for, calculating approach speed");
            }
            // look ahead to avoid surprisingly slow corners
            plan.calcApproachSpeed(planData);
            if (plan.TEXT_DEBUG && planning) {
                System.out.println("Done");
            }
        }

        if (Plan.TEXT_DEBUG && planning) {
            planData.print();
            plan.println("");
        }

        double[] apexSpeeds = new double[apexes.length];
        for (int i = 0; i < apexes.length; ++i) {
            apexSpeeds[i] = plan.getTargetSpeed(apexes[i]);
        }

        PlanElement2011 element = new PlanElement2011(start, end, "Turn in");

        // handle all apexes, back to front
        for (int i = apexes.length - 1; i >= -1; --i) {
            if (Plan.TEXT_DEBUG && planning) {
                if (i >= 0) {
                    plan.println("Handling Apex " + (i + 1) + "/" + apexes.length);
                } else {
                    plan.println("Handling corner entry");
                }
            }

            TrackSubSegment sub = null;
            if (i >= 0) {
                sub = current.getSubSegment(apexes[i].position);
            }

            if (Plan.TEXT_DEBUG && planning && sub != null) {
                plan.println("Part of subsegment: " + Utils.dTS(sub.getStart()) + " - " + Utils.dTS(sub.getEnd()));
            }

            double length;      // length between the end of the apex subsegment and the next apex or end
            double nextSpeed;   // the next target speed
            double brakeDist;   // brake distance needed to brake down from preSpeed to targetSpeed
            double preSpeed;    // preSpeed
            double apexPlanEnd; // end of the planning for this apex
            boolean shortCut = false; // end the planning because we reached the position of the car

            if (i == apexes.length - 1) {
                // last apex
                length = current.getEnd() - sub.getEnd();
                nextSpeed = planData.approachSpeed;
                preSpeed = apexSpeeds[i];
                apexPlanEnd = current.getEnd();

                // if this is a full speed corner, there is only one subsegment!
                if (current.isFull()) {
                    length = current.getLength();
                }

                if (planData.first()) {
                    shortCut = data.getDistanceFromStartLine() >= sub.getStart()
                            && data.getDistanceFromStartLine() <= current.getEnd();

                    if (shortCut) {
                        if (!current.isFull()) {
                            preSpeed = data.getSpeed();
                        }
                        length = current.getEnd() - data.getDistanceFromStartLine();
                    }
                }

            } else if (i == -1) {
                // corner entry
                length = current.getSubSegment(apexes[i + 1].position).getStart() - current.getStart();
                nextSpeed = apexSpeeds[i + 1];
                preSpeed = planData.speed();
                apexPlanEnd = current.getSubSegment(apexes[i + 1].position).getStart();

                if (planData.first()) {
                    shortCut = data.getDistanceFromStartLine() >= current.getStart()
                            && data.getDistanceFromStartLine() <= current.getSubSegment(apexes[i + 1].position).getStart();

                    if (shortCut) {
                        preSpeed = data.getSpeed();
                        length = current.getSubSegment(apexes[i + 1].position).getStart() - data.getDistanceFromStartLine();
                    }
                }

            } else {
                // within
                length = current.getSubSegment(apexes[i + 1].position).getStart() - sub.getEnd();
                nextSpeed = apexSpeeds[i + 1];
                preSpeed = apexSpeeds[i];
                apexPlanEnd = current.getSubSegment(apexes[i + 1].position).getStart();

                if (planData.first()) {
                    shortCut = data.getDistanceFromStartLine() >= sub.getStart()
                            && data.getDistanceFromStartLine() <= current.getSubSegment(apexes[i + 1].position).getStart();

                    if (shortCut) {
                        preSpeed = data.getSpeed();
                        length = current.getSubSegment(apexes[i + 1].position).getStart() - data.getDistanceFromStartLine();
                    }
                }
            }

            brakeDist = plan.calcBrakingZoneCorner(preSpeed, length, nextSpeed);

            if (Plan.TEXT_DEBUG && planning) {
                plan.println("Previous Speed: " + Utils.dTS(preSpeed)
                        + "km/h, nextSpeed: " + Utils.dTS(nextSpeed) + "km/h");
                plan.println("Shortcut: " + shortCut);
                plan.println("Length: " + Utils.dTS(length) + ", brakeDistance: " + Utils.dTS(brakeDist));
            }

            if (current.isFull()) {
                if (brakeDist >= length) {
                    element.attachSpeed(new ConstantValue(start, end, nextSpeed));
                    double saveSpeed = plan.calcApproachSpeedCorner(nextSpeed, length);
                    planData.approachSpeed = saveSpeed;

                } else {
                    if (brakeDist > 0) {
                        element.attachSpeed(new ConstantValue(end - brakeDist, end, nextSpeed));
                    }
                    element.attachSpeed(new ConstantValue(start, end - brakeDist, preSpeed));
                    planData.approachSpeed = preSpeed;
                }

                i = -1;

            } else {
                if (brakeDist >= length) {
                    double x3 = start + (apexPlanEnd - current.getStart() - length);
                    double x4 = start + (apexPlanEnd - current.getStart());
                    element.attachSpeed(new ConstantValue(x3, x4, nextSpeed));
                    double saveSpeed = plan.calcApproachSpeedCorner(nextSpeed, length);

                    if (i == -1) {
                        planData.approachSpeed = saveSpeed;
                    }

                } else {
                    if (brakeDist > 0) {
                        double x5 = start + (apexPlanEnd - current.getStart() - brakeDist);
                        double x6 = start + (apexPlanEnd - current.getStart());
                        element.attachSpeed(new ConstantValue(x5, x6, nextSpeed));
                    }
                    double x3 = start + (apexPlanEnd - current.getStart() - length);
                    double x4 = start + (apexPlanEnd - current.getStart() - brakeDist);
                    element.attachSpeed(new ConstantValue(x3, x4, Plan2011.MAX_SPEED));
                    
                    if (i == -1) {
                        planData.approachSpeed = Plan2011.MAX_SPEED;
                    }
                }

                // apex part with constant speed
                if (i >= 0) {
                    if (brakeDist > length) {
                        double saveSpeed = plan.calcApproachSpeedCorner(nextSpeed, length);
                        brakeDist = plan.calcBrakeDistanceCorner(apexSpeeds[i], saveSpeed);

                        if (brakeDist > sub.getLength()) {
                            apexSpeeds[i] = saveSpeed;
                            double x1 = start + (sub.getStart() - current.getStart());
                            double x2 = start + (sub.getEnd() - current.getStart());
                            element.attachSpeed(new ConstantValue(x1, x2, apexSpeeds[i]));

                        } else {
                            double x1 = start + (sub.getStart() - current.getStart());
                            double x2 = start + (sub.getEnd() - current.getStart() - brakeDist);
                            element.attachSpeed(new ConstantValue(x1, x2, apexSpeeds[i]));

                            double x3 = start + (sub.getEnd() - current.getStart() - brakeDist);
                            double x4 = start + (sub.getEnd() - current.getStart());
                            element.attachSpeed(new ConstantValue(x3, x4, saveSpeed));
                        }

                    } else {
                        double x1 = start + (sub.getStart() - current.getStart());
                        double x2 = start + (sub.getEnd() - current.getStart());
                        element.attachSpeed(new ConstantValue(x1, x2, apexSpeeds[i]));
                    }
                }
            }

            if (shortCut) {
                i = -1;
            }
            if (Plan.TEXT_DEBUG && planning) {
                plan.println("");
            }
        }

        if (Plan.TEXT_DEBUG && planning) {
            plan.println(element.toString());
            element.printSpeeds();
            plan.println("-------------------------------");
        }

        return element;
    }
}
