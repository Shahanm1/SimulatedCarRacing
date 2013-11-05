/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.janquadflieg.mrracer.plan;

import de.janquadflieg.mrracer.behaviour.Component;
import de.janquadflieg.mrracer.opponents.OpponentObserver;
import de.janquadflieg.mrracer.telemetry.SensorData;
import de.janquadflieg.mrracer.track.TrackModel;

/**
 *
 * @author quad
 */
public interface Planner
extends Component{
    public void calcApproachSpeed(PlanStackData planData,
            SensorData data, TrackModel trackModel, OpponentObserver observer);

    public PlanElement2011 plan(PlanStackData planData, SensorData data, TrackModel model,
            OpponentObserver observer);
}
