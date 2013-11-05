/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.janquadflieg.mrracer.opponents;

import scr.Controller.Stage;

import java.awt.geom.Point2D;

import de.janquadflieg.mrracer.classification.Situation;
import de.janquadflieg.mrracer.plan.Plan;
import de.janquadflieg.mrracer.plan.Plan2013;
import de.janquadflieg.mrracer.telemetry.SensorData;
import de.janquadflieg.mrracer.track.TrackModel;
import de.janquadflieg.mrracer.track.TrackSegment;

/**
 *
 * @author quad
 */
public class DebugObserverBlock
implements OpponentObserver/*, de.janquadflieg.mrracer.gui.GraphicDebugable*/{

    SensorData data;
    TrackModel model;
    Point2D point = OpponentObserver.NO_RECOMMENDED_POINT;
    Plan plan;
    Stage stage;

    javax.swing.JLabel alibiDebugLabel = new javax.swing.JLabel("Isch mach nix");

    public DebugObserverBlock(Plan p){
        this.plan = p;
    }

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
        this.stage = s;
    }

    public void paint(String baseFileName, java.awt.Dimension d) {
        
    }

    public java.awt.geom.Point2D getMinDistance(){
        return null;
    }

    @Override
    public void update(SensorData data, Situation s){
        if(stage != Stage.RACE || !model.complete()){
            return;
        }

        int index = model.getIndex(data.getDistanceFromStartLine());
        TrackSegment current = model.getSegment(index);

        if((point == NO_RECOMMENDED_POINT && current.isStraight()) ||
                (point != NO_RECOMMENDED_POINT && current.isStraight() && point.getY() <= data.getDistanceRaced())){
            double length = current.getEnd()-data.getDistanceFromStartLine();

            int nextIndex = model.incIdx(index);
            TrackSegment next = model.getSegment(nextIndex);

            while(next.isStraight()){
                length += next.getLength();
                nextIndex = model.incIdx(nextIndex);
                next = model.getSegment(nextIndex);
            }

            double anchorPoint = -1.0 * plan.getAnchorPoint(current, next);
            
            point = new Point2D.Double(anchorPoint, data.getDistanceRaced()+(length*0.5));

        } else if (point != NO_RECOMMENDED_POINT && current.isCorner()) {
            point = NO_RECOMMENDED_POINT;
        }
    }

    /**
     * Calculates the distance needed to switch the position on the track by delta meter.
     * @param delta
     * @return
     */
    public double calcSwitchDistance(SensorData data, double delta){
        return Plan2013.calcSwitchDistance(data, delta);
    }

    /**
     * Calculates the possible absolute change in trackposition.
     * @param data
     * @param length
     * @return
     */
    public double calcPossibleSwitchDelta(SensorData data, double length){
        return Plan2013.calcPossibleSwitchDelta(data, length);
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
        return PositionType.BLOCKING;
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
        return point != NO_RECOMMENDED_POINT;

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
