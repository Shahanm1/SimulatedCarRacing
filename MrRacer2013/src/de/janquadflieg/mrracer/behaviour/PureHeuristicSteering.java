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
public class PureHeuristicSteering
        implements SteeringBehaviour {

    /** The angles of the track sensors. */
    private float[] angles = new float[19];

    private Situation situation;

    private static final boolean DO_ADJUST = true;

    public PureHeuristicSteering(float[] f) {
        System.arraycopy(f, 0, angles, 0, f.length);
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

        if (DO_ADJUST && situation.isHairpin()) {
            angleD *= 3.0;

        } else if (DO_ADJUST && situation.isSlow()) {
            angleD *= 2.0;
        }

        angleD = Math.min(45.0, Math.max(-45.0, angleD));        

        action.setSteering(angleD / 45.0);
        action.limitValues();
    }

    public void paint(String baseFileName, java.awt.Dimension d){
    }

    /**
     * The current situation of the car, as classified by an
     * appropriate classifier chosen by the controller which uses this behaviour.
     */
    @Override
    public void setSituation(Situation s){
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
    public void setParameters(java.util.Properties params, String prefix){
    }

    @Override
    public void getParameters(java.util.Properties params, String prefix){
    }
}