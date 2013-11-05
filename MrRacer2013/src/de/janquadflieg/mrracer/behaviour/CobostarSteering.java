/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.janquadflieg.mrracer.behaviour;

import de.janquadflieg.mrracer.classification.*;
import de.janquadflieg.mrracer.telemetry.ModifiableAction;
import de.janquadflieg.mrracer.telemetry.SensorData;
import de.janquadflieg.mrracer.track.*;

/**
 *
 * @author quad
 */
public class CobostarSteering
        implements SteeringBehaviour {

    /** The angles of the track sensors. */
    private float[] angles = new float[19];

    public CobostarSteering(float[] f) {
        System.arraycopy(f, 0, angles, 0, f.length);
    }

    @Override
    public void execute(SensorData data, ModifiableAction action) {
        // steering based on the "biggest sensor value" heuristic
        int index = SensorData.maxTrackIndexLeft(data);
        double value = data.getTrackEdgeSensors()[index];
        int lindex = -1;
        double lvalue = 0.0;
        int rindex = -1;
        double rvalue = 0.0;

        // check neighbours
        if (index > 0) {
            lindex = index - 1;
            lvalue = data.getTrackEdgeSensors()[lindex];
        }
        if (index < 18) {
            rindex = index + 1;
            rvalue = data.getTrackEdgeSensors()[rindex];
        }



        if (rindex != -1 && lindex != -1) {
            double angularAdjustments = 0.5;
            double diffLeft = value - lvalue;
            double diffRight = value - rvalue;
            double maxAngle = index - angularAdjustments
                    + 2. * angularAdjustments * (diffLeft / (diffLeft + diffRight));

            double p10 = 0.39;

            double angle = (9.0-maxAngle) * p10;
            action.setSteering(angle);
            
        } else {
            double angleD = -angles[index];
            action.setSteering(angleD / 45.0);
        }
        action.limitValues();
    }

    /**
     * The current situation of the car, as classified by an
     * appropriate classifier chosen by the controller which uses this behaviour.
     */
    @Override
    public void setSituation(Situation s) {
    }

    /**
     * The segment of the trackmodel containing the current position of the car.
     * Might be null, if the trackmodel has not beeen initialized or unknown, if
     * the controller is still learning the track during the first lap.
     * @param s The track segment.
     */
    @Override
    public void setTrackSegment(TrackSegment s) {
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
}
