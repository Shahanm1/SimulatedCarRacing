/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.janquadflieg.mrracer.behaviour;

import de.janquadflieg.mrracer.telemetry.ModifiableAction;
import de.janquadflieg.mrracer.telemetry.SensorData;


/**
 *
 * @author Jan Quadflieg
 */
public class ASR
        implements Behaviour {

    /* ASR Filter Constants */
    final float wheelRadius[] = {(float) 0.3179, (float) 0.3179, (float) 0.3276, (float) 0.3276};
    final float asrSlip = (float) 2.0;
    final float asrRange = (float) 3.0;
    final float asrMinSpeed = (float) 3.0;

    @Override
    public void execute(SensorData data, ModifiableAction action) {
        if (action.getAcceleration() == 0.0) {
            return;
        }

        // convert speed to m/s
        double speed = data.getSpeed() / 3.6;
        // when spedd lower than min speed for abs do nothing
        if (speed < asrMinSpeed) {
            return;
        }

        // compute the speed of wheels in m/s
        double slip = 0.0;
        for (int i = 0; i < 4; i++) {
            slip += data.getWheelSpinVelocity()[i] * wheelRadius[i];
        }
        // slip is the difference between actual speed of car and average speed of wheels
        slip = speed - (slip / 4.0f);

        double a = action.getAcceleration();

        if (slip > asrSlip) {
            a = a - (slip - asrSlip) / asrRange;
        }

        // check brake is not negative, otherwise set it to zero
        a = Math.max(0.0, a);
        action.setAcceleration(a);
    }

    /**
     * The current situation of the car, as classified by an
     * appropriate classifier chosen by the controller which uses this behaviour.
     */
    @Override
    public void setSituation(de.janquadflieg.mrracer.classification.Situation s) {
    }

    @Override
    public void reset() {
    }

    @Override
    public void shutdown() {
    }

    @Override
    public void setParameters(java.util.Properties params, String prefix){
    }

    @Override
    public void getParameters(java.util.Properties params, String prefix){
    }

    public void paint(String baseFileName, java.awt.Dimension d){
    }
}
