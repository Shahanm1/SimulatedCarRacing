/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.janquadflieg.mrracer.behaviour;

import java.util.Properties;

import de.janquadflieg.mrracer.classification.Situation;
import de.janquadflieg.mrracer.telemetry.*;

/**
 *
 * @author quad
 */
public class StuckDetection
implements Behaviour{
    private SensorData oldData = null;
    /** A counter to determine if the car is stuck. */
    private int speedCounter = 0;

    public void paint(String baseFileName, java.awt.Dimension d){
    }

    /**
     * Executes this behaviour.
     *
     * @param data The current sensor data.
     * @param action An action object, which gets modified by this behaviour.
     */
    public void execute(final SensorData data, ModifiableAction action){
        if(data.getCurrentLapTime() < 0){
            return;
        }

        if(oldData == null){
            oldData = data;
            return;
        }

        // when facing away from the track, we might hit an obstacle and get stuck
        if ((data.getSpeed() <= oldData.getSpeed()+0.25 && data.getSpeed() < 5)) {
            //|| Math.abs(lastRaceDistance - data.getDistanceRaced()) < 0.2) {
            ++speedCounter;

        } else if(data.getSpeed() >= 5.0){
            speedCounter = 0;

        } else if(data.getTrackPosition() > -0.5 && data.getTrackPosition() < 0.5){
            speedCounter = 0;
        }
        oldData = data;
    }

    public void setParameters(Properties params, String prefix){

    }

    public void getParameters(Properties params, String prefix){
        
    }

    public void setStuck(){
        speedCounter = 200;
    }

    public boolean isStuck(){
        return speedCounter > 100;
    }

    /**
     * The current situation of the car, as classified by an
     * appropriate classifier chosen by the controller which uses this behaviour.
     */
    public void setSituation(Situation s){
        
    }

    public void reset(){
        oldData = null;
        speedCounter = 0;
    }

    public void shutdown(){
        
    }
}
