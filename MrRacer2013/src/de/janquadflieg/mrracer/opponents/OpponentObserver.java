/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.janquadflieg.mrracer.opponents;

import scr.Controller.Stage;

import java.awt.geom.Point2D;

import de.janquadflieg.mrracer.behaviour.Component;
import de.janquadflieg.mrracer.classification.Situation;
import de.janquadflieg.mrracer.telemetry.SensorData;
import de.janquadflieg.mrracer.track.TrackModel;

/**
 *
 * @author quad
 */
public interface OpponentObserver
extends Component{

    public enum PositionType {OVERTAKING, BLOCKING};

    public static final java.awt.geom.Point2D NO_RECOMMENDED_POINT = null;

    public static final double NO_RECOMMENDED_SPEED = -1.0;

    public void update(SensorData data, Situation s);

    /**
     * Returns the recommended position on the track to avoid, block or overtake other cars. The
     * x-coordinate corresponds to the relative position on the track,
     * as defined by the SCRC sensormodel (1 left edge of the track, 0 middle
     * of the track, -1 right edge of the track).
     * The y-coordinate is the race distance at which the given x position should
     * be reached. This can be computed as the current race distance given by
     * the sensor data plus a certain distance needed to move the car to the
     * x position.
     * Might return NO_RECOMMENDED_POINT if there is no recommendation.
     *
     * @return
     */
    public java.awt.geom.Point2D getRecommendedPosition();

    /**
     * Provides further information about the intention of the target position.
     * 
     * @return
     */
    public PositionType getPositionType();



    /**
     * Returns the recommended speed to avoid crashing into other cars.
     * Might return NO_RECOMMENDE_SPEED if there is no need to slow down.
     *
     * @return
     */
    public double getRecommendedSpeed();   

    public Point2D getMinDistance();

    public boolean otherCars();

    public void setTrackModel(TrackModel trackModel);

    public void setStage(Stage s);

    public void reset();
    
    public TrackModel getTrackModel();
    
    public SensorData getData();

}
