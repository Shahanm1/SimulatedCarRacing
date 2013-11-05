/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.janquadflieg.mrracer.plan;

import de.janquadflieg.mrracer.Utils;
import de.janquadflieg.mrracer.classification.Situations;
import de.janquadflieg.mrracer.functions.*;
import de.janquadflieg.mrracer.opponents.OpponentObserver;
import de.janquadflieg.mrracer.telemetry.SensorData;
import de.janquadflieg.mrracer.track.*;

import java.util.Properties;

/**
 *
 * @author quad
 */
public class PlanCorner2013
        implements Planner{

    private Plan plan;
    /** Fraction of the subsegment which should be driven with constant speed.
     * 1 will cause the module to plan a constant speed for the whole subsegment,
     * 0 will cause the module to behave like the old 2011 version.
     */
    private double fraction = 1.0;

    public static final String FRACTION = "-PCF.factor-";

    public PlanCorner2013(Plan p) {
        this.plan = p;
    }

    public void setParameters(Properties params, String prefix) {
        fraction = Double.parseDouble(params.getProperty(prefix + FRACTION, String.valueOf(fraction)));
    }

    public void getParameters(Properties params, String prefix) {
        params.setProperty(prefix + FRACTION, String.valueOf(fraction));
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
            plan.println("----------  CORNER[Flexible Apex Speed]  ---------------");
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
                plan.println("This corner is the last element to plan for, calculating approach speed");
            }
            // look ahead to avoid surprisingly slow corners
            plan.calcApproachSpeed(planData);
            if (Plan.TEXT_DEBUG && planning) {
                plan.println("Done");
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

            if (i == apexes.length - 1) {
                // last apex
                length = (current.getEnd() - sub.getEnd())+
                        ((sub.getEnd()-apexes[i].position)*(1.0-fraction));
                nextSpeed = planData.approachSpeed;
                preSpeed = apexSpeeds[i];
                apexPlanEnd = current.getEnd();

                // if this is a full speed corner, there is only one subsegment!
                if (current.isFull()) {
                    length = current.getLength();
                }                

            } else if (i == -1) {
                // corner entry
                length = (current.getSubSegment(apexes[i + 1].position).getStart() - current.getStart())+
                        ((apexes[i + 1].position-current.getSubSegment(apexes[i + 1].position).getStart())*(1.0-fraction));
                nextSpeed = apexSpeeds[i + 1];
                preSpeed = planData.speed();
                // BUGFIX
                if(preSpeed <= nextSpeed){
                    preSpeed = plan.calcApproachSpeedCorner(nextSpeed, length);
                }
                apexPlanEnd = current.getSubSegment(apexes[i + 1].position).getStart()+
                        ((apexes[i + 1].position-current.getSubSegment(apexes[i + 1].position).getStart())*(1.0-fraction));

            } else {
                // within
                length = (current.getSubSegment(apexes[i + 1].position).getStart() - sub.getEnd())+
                        ((apexes[i + 1].position-current.getSubSegment(apexes[i + 1].position).getStart())*(1.0-fraction))+  //
                        ((sub.getEnd()-apexes[i].position)*(1.0-fraction));
                nextSpeed = apexSpeeds[i + 1];
                preSpeed = apexSpeeds[i];
                apexPlanEnd = current.getSubSegment(apexes[i + 1].position).getStart()+
                        ((apexes[i + 1].position-current.getSubSegment(apexes[i + 1].position).getStart())*(1.0-fraction));
            }

            brakeDist = plan.calcBrakingZoneCorner(preSpeed, length, nextSpeed);

            if (Plan.TEXT_DEBUG && planning) {
                plan.println("Previous Speed: " + Utils.dTS(preSpeed)
                        + "km/h, nextSpeed: " + Utils.dTS(nextSpeed) + "km/h");
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
                    // BUGFIX
                    double saveSpeed = Math.min(plan.calcApproachSpeedCorner(nextSpeed, x4-x3),
                            Plan2013.MAX_SPEED);
                    element.attachSpeed(new ConstantValue(x3, x4, saveSpeed));
                    //element.attachSpeed(new ConstantValue(x3, x4, ));
                    
                    if (i == -1) {
                        // BUGFIX
                        //planData.approachSpeed = Plan2013.MAX_SPEED;
                        planData.approachSpeed = Math.min(plan.calcApproachSpeedCorner(nextSpeed, length),
                                Plan2013.MAX_SPEED);
                    }
                }

                // apex part with constant speed
                if (i >= 0) {
                    double frontApex = (apexes[i].position-sub.getStart())*(1.0-fraction);
                    double afterApex = (sub.getEnd()-apexes[i].position)*(1.0-fraction);
                    
                    if (brakeDist > length) {
                        double saveSpeed = plan.calcApproachSpeedCorner(nextSpeed, length);
                        brakeDist = plan.calcBrakeDistanceCorner(apexSpeeds[i], saveSpeed);
                        double usedSubLength = sub.getLength()*fraction;

                        if (brakeDist > usedSubLength) {
                            apexSpeeds[i] = saveSpeed;
                            double x1 = start + ((sub.getStart()+frontApex) - current.getStart());
                            double x2 = start + ((sub.getEnd()-afterApex) - current.getStart());
                            element.attachSpeed(new ConstantValue(x1, x2, apexSpeeds[i]));

                        } else {
                            double x1 = start + ((sub.getStart()+frontApex) - current.getStart());
                            double x2 = start + ((sub.getEnd()-afterApex) - current.getStart() - brakeDist);
                            element.attachSpeed(new ConstantValue(x1, x2, apexSpeeds[i]));

                            double x3 = start + ((sub.getEnd()-afterApex) - current.getStart() - brakeDist);
                            double x4 = start + ((sub.getEnd()-afterApex) - current.getStart());
                            element.attachSpeed(new ConstantValue(x3, x4, saveSpeed));
                        }

                    } else {                        
                        double x1 = start + ((sub.getStart()+frontApex) - current.getStart());
                        double x2 = start + ((sub.getEnd()-afterApex) - current.getStart());
                        element.attachSpeed(new ConstantValue(x1, x2, apexSpeeds[i]));
                    }
                }
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