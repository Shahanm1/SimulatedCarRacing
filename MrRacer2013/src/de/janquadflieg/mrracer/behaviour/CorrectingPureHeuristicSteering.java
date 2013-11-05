/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.janquadflieg.mrracer.behaviour;

import de.janquadflieg.mrracer.Utils;
import de.janquadflieg.mrracer.classification.*;
import de.janquadflieg.mrracer.telemetry.ModifiableAction;
import de.janquadflieg.mrracer.telemetry.SensorData;
import de.janquadflieg.mrracer.track.*;
import static de.janquadflieg.mrracer.data.CarConstants.CAR_WIDTH;

/**
 *
 * @author quad
 */
public class CorrectingPureHeuristicSteering
        implements SteeringBehaviour {

    public static final boolean TEXT_DEBUG = false;
    /** The angles of the track sensors. */
    private float[] angles = new float[19];
    private Situation situation;
    private TrackSegment segment;
    private double trackWidth;
    /** Track margin in meter at the inside of a corner. */
    private static final double INSIDE_MARGIN = 1.0;
    /** Track margin in meter at the outside of a corner. */
    private static final double OUTSIDE_MARGIN = 1.0;
    /** Measure at which the outside steering angle is doubled. */
    private static final double OUTSIDE_DOUBLE = 100.0;

    public CorrectingPureHeuristicSteering(float[] f) {
        System.arraycopy(f, 0, angles, 0, f.length);
    }

    private boolean withinCorner() {
        return (segment != null && !segment.isUnknown() && segment.isCorner())
                || situation.isCorner();
    }

    private boolean withinRightCorner() {
        return (segment != null && !segment.isUnknown() && segment.isRight())
                || situation.isRight();
    }

    //@Override
    public void execute2(SensorData data, ModifiableAction action) {
        double absMeasure = Math.abs(situation.getMeasure());
        double trackPos = data.getTrackPosition();
        double trackAngle = data.getAngleToTrackAxis();

        boolean mirror = situation.isRight();

        if (mirror) {
            trackPos *= -1.0;
            trackAngle *= -1.0;
        }
        //0.976807459	0,008231851	-0,209828013	-0,634093073	0,046558592	-0,079286572	1,05025865	0,517923684	-0,158864381	1,460560156	0,944967234	0,836506662	0,99226593	1,01798939


        double result = 0.002708663 * absMeasure * absMeasure +
                0.983090667 * trackPos * trackPos +
                0.995064624 * trackAngle * trackAngle +
                0.0 * absMeasure * trackPos +
                1.695369918 * absMeasure * trackAngle +
                1.010313665 * trackPos * trackAngle +
                1.020039262;

        if(mirror){
            result *= -1.0;
        }

        result = Math.min(45.0, Math.max(-45.0, result));
        action.setSteering(result / 45.0);
        action.limitValues();        
    }

    @Override
    public void execute(SensorData data, ModifiableAction action) {
        // steering based on the "biggest sensor value" heuristic
        double[] rawSensors = data.getRawTrackEdgeSensors();
        int index = 0;
        double longest = rawSensors[0];
        for(int i=1; i < rawSensors.length; ++i){
            if(rawSensors[i] > longest){
                index = i;
                longest = rawSensors[i];
            }
        }
        
        double angleD = -angles[index];

        if (TEXT_DEBUG) {
            System.out.println(Utils.timeToExactString(data.getCurrentLapTime()) + " @ " + data.getDistanceRacedS() + "- track: "
                    + data.getTrackPositionS() + ", index: " + index + ", angleD: " + Utils.dTS(angleD));
        }

        double absMeasure = Math.abs(situation.getMeasure());

        if (absMeasure >= 20.0 && absMeasure < 35.0) {
            angleD *= 1.0 + ((absMeasure - 20.0) / (35.0 - 20.0));

        } else if (absMeasure >= 35.0 && absMeasure < 60.0) {
            angleD *= 2.0 + ((absMeasure - 35.0) / (60.0 - 35.0));

        } else if (absMeasure >= 60.0) {
            angleD *= 3.0;
        }

        if (TEXT_DEBUG) {
            System.out.println("absMeasure: " + Utils.dTS(absMeasure) + ", angleD: "
                    + Utils.dTS(angleD));
        }

        double trackPos = data.getTrackPosition();
        double trackAngle = data.getAngleToTrackAxis();

        if (situation.isCorner()) {
            boolean mirror = situation.isRight();

            if (mirror) {
                trackPos *= -1.0;
                trackAngle *= -1.0;
                angleD *= -1.0;
                index = 18 - index;
            }

            double absTrackPos = SensorData.calcAbsoluteTrackPosition(trackPos, trackWidth);

            // inside
            if (absTrackPos - (CAR_WIDTH * 0.5) < INSIDE_MARGIN && trackAngle < 0.0) {
                double beta = (INSIDE_MARGIN - (absTrackPos - (CAR_WIDTH * 0.5))) / INSIDE_MARGIN;
                if (TEXT_DEBUG) {
                    System.out.println("Inside");
                }

                // not so close -> open steering
                if (beta < 0.5) {
                    //System.out.println("IN - Opening@"+Utils.dTS(data.getDistanceFromStartLine()));
                    if (TEXT_DEBUG) {
                        System.out.print("Beta: " + Utils.dTS(beta) + ", old: " + Utils.dTS(angleD));
                    }
                    double alpha = (beta - 0.5) * -(1 / 0.5);
                    angleD *= alpha;
                    if (TEXT_DEBUG) {
                        System.out.println(", alpha: " + Utils.dTS(alpha) + ", new: " + Utils.dTS(angleD));
                    }

                } else {
                    //System.out.println("IN - Other@"+Utils.dTS(data.getDistanceFromStartLine()));
                    beta = Math.min(beta, 1.0);
                    // very close, steer in the other direction
                    if (TEXT_DEBUG) {
                        System.out.print("Beta: " + Utils.dTS(beta) + ", old: " + Utils.dTS(angleD));
                    }
                    double alpha = (beta - 0.5) * (1 / 0.5);
                    angleD = Math.toDegrees(trackAngle) * alpha;
                    //angleD = 2.0*alpha;
                    if (TEXT_DEBUG) {
                        System.out.println(", alpha: " + Utils.dTS(alpha) + ", new: " + Utils.dTS(angleD));
                    }
                }
            }

            // outside
            if (absTrackPos + (CAR_WIDTH * 0.5) > trackWidth - OUTSIDE_MARGIN) {
                //System.out.println("OUT@"+Utils.dTS(data.getDistanceFromStartLine()));
                if (TEXT_DEBUG) {
                    System.out.println("Outside");
                }
                // increase steering angle
                double alpha = Math.min(absMeasure, OUTSIDE_DOUBLE) / OUTSIDE_DOUBLE;
                double beta = ((absTrackPos + (CAR_WIDTH * 0.5)) - (trackWidth - OUTSIDE_MARGIN)) / OUTSIDE_MARGIN;

                if (TEXT_DEBUG) {
                    System.out.print("Beta: " + Utils.dTS(beta) + ", old: " + Utils.dTS(angleD));
                }

                beta = Math.min(beta, 1.0);
                angleD *= 1.0 + (alpha * beta);

                if (TEXT_DEBUG) {
                    System.out.println(", 1+alpha: " + Utils.dTS(1.0 + alpha) + ", new: " + Utils.dTS(angleD));
                }
            }

            //if(Math.abs(Math.toDegrees(trackAngle)) < 10.0){
            //    System.out.println(absMeasure + ";" + trackPos + ";" + Math.toDegrees(trackAngle) + ";" + index + ";" + angleD);
           // }

            if (mirror) {
                angleD *= -1.0;
            }
        }

        angleD = Math.min(45.0, Math.max(-45.0, angleD));
        action.setSteering(angleD / 45.0);
        action.limitValues();
        if (TEXT_DEBUG) {
            System.out.println("Final value: " + action.getSteeringS());
        }
    }

    /**
     * The current situation of the car, as classified by an
     * appropriate classifier chosen by the controller which uses this behaviour.
     */
    @Override
    public void setSituation(Situation s) {
        this.situation = s;
    }

    /**
     * The segment of the trackmodel containing the current position of the car.
     * Might be null, if the trackmodel has not beeen initialized or unknown, if
     * the controller is still learning the track during the first lap.
     * @param s The track segment.
     */
    @Override
    public void setTrackSegment(TrackSegment s) {
        this.segment = s;
    }

    /**
     * The desired target position on the track (1 left edge of the track, 0 middle,
     * -1 right edge.
     * @param position The position.
     */
    @Override
    public void setTargetPosition(java.awt.geom.Point2D position) {
    }

    /**
     * The width of the race track in meter.
     * @param width
     */
    @Override
    public void setWidth(double width) {
        trackWidth = width;
    }

    @Override
    public void reset() {
    }

    @Override
    public void shutdown() {
    }

    @Override
    public String getDebugInfo() {
        return "";
    }

    @Override
    public void setParameters(java.util.Properties params, String prefix) {
    }

    @Override
    public void getParameters(java.util.Properties params, String prefix) {
    }

    public void paint(String baseFileName, java.awt.Dimension d){
    }

    public static void main(String[] args) {

        for (double absMeasure = 0.0; absMeasure < 100.0; absMeasure += 1.0) {
            double angleD = 10.0;

            System.out.print(Utils.dTS(angleD));

            //double absMeasure = Math.abs(situation.getMeasure());

            if (absMeasure >= 20.0 && absMeasure < 35.0) {
                angleD *= 1.0 + ((absMeasure - 20.0) / (35.0 - 20.0));

            } else if (absMeasure >= 35.0 && absMeasure < 60.0) {
                angleD *= 2.0 + ((absMeasure - 35.0) / (60.0 - 35.0));

            } else if (absMeasure >= 60.0) {
                angleD *= 3.0;
            }
            System.out.println(" " + Utils.dTS(absMeasure) + " " + angleD);
        }
    }
}
