/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.janquadflieg.mrracer.opponents;

import scr.Controller.Stage;

import java.awt.geom.Point2D;

import de.janquadflieg.mrracer.classification.Situation;
import de.janquadflieg.mrracer.telemetry.SensorData;
import de.janquadflieg.mrracer.track.TrackModel;

/**
 *
 * @author quad
 */
public class DummyObserver
implements OpponentObserver/*, de.janquadflieg.mrracer.gui.GraphicDebugable*/{

    SensorData data;
    TrackModel model;
    Point2D point = OpponentObserver.NO_RECOMMENDED_POINT;

    javax.swing.JLabel alibiDebugLabel = new javax.swing.JLabel("Isch mach nix");

    @Override
    public void setParameters(java.util.Properties params, String prefix){
    }

    @Override
    public void getParameters(java.util.Properties params, String prefix){
    }
     @Override
    public TrackModel getTrackModel(){
        return null;
    }
     @Override
    public SensorData getData(){
        return null;
    }

    public void setStage(Stage s){
        
    }

    public void paint(String baseFileName, java.awt.Dimension d) {
        
    }

    public java.awt.geom.Point2D getMinDistance(){
        return new java.awt.geom.Point2D.Double(2.0, 30.0);
    }

    @Override
    public void update(SensorData data, Situation s){
        this.data = data;

        if(model != null && model.initialized() && model.getLength()-data.getDistanceFromStartLine() < 4000.0 &&
                point == OpponentObserver.NO_RECOMMENDED_POINT && model.getLength()-data.getDistanceFromStartLine() >= 1000.0 ){

            double position = data.getDistanceRaced();
            position += 150;
            //System.out.println("Setting test point.");

            point = new Point2D.Double(-0.75, position);

        } else if(point != OpponentObserver.NO_RECOMMENDED_POINT && model.getLength()-data.getDistanceFromStartLine() >= 1000.0){
            double position = data.getDistanceRaced();
            position += 150;
            point = new Point2D.Double(-0.75, position);
            //System.out.println("Updating test point.");

        } else {
            point = OpponentObserver.NO_RECOMMENDED_POINT;
            //System.out.println("Removing test point.");
        }
    }

    /**
     * Calculates the distance needed to switch the position on the track by delta meter.
     * @param delta
     * @return
     */
    public double calcSwitchDistance(SensorData data, double delta){
        return 20;
    }

    /**
     * Calculates the possible absolute change in trackposition.
     * @param data
     * @param length
     * @return
     */
    public double calcPossibleSwitchDelta(SensorData data, double length){
        return 1;
    }

    /**
     * Returns the recommended position on the track to avoid other cars. The
     * x-coordinate corresponds to the position on the track and the y-coordinate
     * to the race distance, at which the given x coordinate should be reached.
     *
     * Might return NO_RECOMMENDED_POINT if there is no recommendation.
     *
     * @return
     */
    @Override
    public java.awt.geom.Point2D getRecommendedPosition(){
        return point;
    }

    @Override
    public PositionType getPositionType(){
        return PositionType.OVERTAKING;
    }

    /**
     * Returns the recommended speed to avoid crashing into other cars.
     * Might return NO_RECOMMENDE_SPEED if there is no need to slow down.
     *
     * @return
     */
    @Override
    public double getRecommendedSpeed(){
        return OpponentObserver.NO_RECOMMENDED_SPEED;
    }

    @Override
    public boolean otherCars(){
        return this.point != OpponentObserver.NO_RECOMMENDED_POINT;

    }

    @Override
    public void setTrackModel(TrackModel trackModel){
        this.model = trackModel;
    }

    @Override
    public void reset(){
        point = OpponentObserver.NO_RECOMMENDED_POINT;
    }

    public javax.swing.JComponent[] getComponent(){
        return new javax.swing.JComponent[]{this.alibiDebugLabel};
    }
}
