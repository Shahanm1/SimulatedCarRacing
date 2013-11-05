/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.janquadflieg.mrracer.behaviour;

import de.janquadflieg.mrracer.classification.*;
import de.janquadflieg.mrracer.telemetry.*;

import java.util.*;

/**
 *
 *
 * @author Jan Quadflieg
 */
public class DefensiveFallbackBehaviour
        implements Behaviour {


    private Situation s;

    private PureHeuristicSteering steering;

    /** Gear change behaviour. */
    private StandardGearChangeBehaviour gearChange = new StandardGearChangeBehaviour();
    private int onTrackRecoveryCtr = 0;
    private OnTrackRecoveryBehaviour onTrackRecovery;
    private OffTrackRecoveryBehaviour offTrackRecovery = new OffTrackRecoveryBehaviour();
    private StuckDetection stuck = new StuckDetection();

    public static final String OFF_TRACK = "-DFBB.offTrack-";
    
    public DefensiveFallbackBehaviour(float[] f){
        steering = new PureHeuristicSteering(f);
        onTrackRecovery = new OnTrackRecoveryBehaviour(f);
    }

    public DefensiveFallbackBehaviour(){
        float[] angles = new float[19];
        // init angles
		for (int i = 0; i < 19; ++i){
			angles[i]=-90+i*10;
        }
        steering = new PureHeuristicSteering(angles);
        onTrackRecovery = new OnTrackRecoveryBehaviour(angles);
    }

    public void setParameters(Properties params, String prefix){
        offTrackRecovery.setParameters(params, prefix+OFF_TRACK);
    }

    public void getParameters(Properties params, String prefix){
        offTrackRecovery.getParameters(params, prefix+OFF_TRACK);
    }

    public void paint(String baseFileName, java.awt.Dimension d){
    }

    @Override
    public void execute(SensorData data, ModifiableAction action) {
        if (!data.onTrack()) {
            //System.out.println("Using offTrack");
            offTrackRecovery.execute(data, action);
            onTrackRecovery.reset();
            stuck.reset();
            return;
        }

        if (Math.toDegrees(data.getAngleToTrackAxis()) < -80 ||
                Math.toDegrees(data.getAngleToTrackAxis()) > 80 ||
                data.getSpeed() < 0) {
           //System.out.println("Using on track");
            ++onTrackRecoveryCtr;

            onTrackRecovery.execute(data, action);
            offTrackRecovery.reset();
            stuck.reset();

            return;
        }

        //System.out.println("Defensive");

        stuck.execute(data, action);
        if(stuck.isStuck()){
           //System.out.println("Stuck");
            ++onTrackRecoveryCtr;
            onTrackRecovery.execute(data, action);
            offTrackRecovery.reset();
            return;
        }

        onTrackRecoveryCtr = 0;
        offTrackRecovery.reset();
        onTrackRecovery.reset();
        
        if (s.hasError()) {
            ++onTrackRecoveryCtr;
            onTrackRecovery.execute(data, action);
            offTrackRecovery.reset();
            return;
        }

        // gear
        if(data.getGear() < 1){
            action.setGear(1);
            
        } else {
            gearChange.execute(data, action);
        }

        // acc / brake
        if (s.isStraight()) {
            if (data.getSpeed() < 68.0) {
                action.setAcceleration(1.0);
            } else {
                action.setAcceleration(0);
            }
            action.setBrake(0);

        } else if (s.isStraightAC()) {
            if (data.getSpeed() < 68.0) {
                action.setAcceleration(1);
                action.setBrake(0);

            } else {
                action.setAcceleration(0);
                action.setBrake(0.5);
                if (data.getSpeed() > 150) {
                    action.setBrake(1.0);
                }
            }

        } else {
            if (data.getSpeed() < 68.0) {
                action.setAcceleration(1.0);
            } else {
                action.setAcceleration(0);
            }
            action.setBrake(0);
        }

        // steering based on the "biggest sensor value" heuristic
        steering.setSituation(s);
        steering.execute(data, action);      

        
        action.setClutch(0.0);

        action.limitValues();
    }

    /**
     * The current situation of the car, as classified by an
     * appropriate classifier chosen by the controller which uses this behaviour.
     */
    @Override
    public void setSituation(de.janquadflieg.mrracer.classification.Situation s){
        this.s = s;
    }

    public void setStuck(){
        stuck.setStuck();
        onTrackRecovery.setStuck();
    }

    @Override
    public void reset() {
        gearChange.reset();
        offTrackRecovery.reset();
        onTrackRecovery.reset();
        onTrackRecoveryCtr = 0;
        stuck.reset();
    }

    @Override
    public void shutdown() {
        gearChange.shutdown();
        offTrackRecovery.shutdown();
        onTrackRecovery.shutdown();
    }
}