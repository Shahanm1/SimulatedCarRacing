/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.janquadflieg.mrracer.behaviour;

import de.janquadflieg.mrracer.telemetry.ModifiableAction;
import de.janquadflieg.mrracer.telemetry.SensorData;


/**
 *
 * @author quad
 */
public class DampedAccelerationBehaviour
        extends AbstractDampedAccelerationBehaviour{

    /** Anti lock brakes. */
    private Behaviour abs = new de.janquadflieg.mrracer.behaviour.ABS();    

    public DampedAccelerationBehaviour() {
    }    

    @Override
    public void execute(SensorData data, ModifiableAction action) {
        //System.out.println(targetSpeed+" "+System.currentTimeMillis());
        action.setAcceleration(0.0);
        action.setBrake(0.0);
        if (data.getSpeed() < targetSpeed) {
            if(targetSpeed-data.getSpeed() >= 5.0){
                action.setAcceleration(1.0);
                
            } else {
                action.setAcceleration((targetSpeed-data.getSpeed())/5.0);
            }

        } else if (data.getSpeed() > 0) {
            action.setBrake(1.0);
        }

        if (action.getAcceleration() > 0.0) {
            double steeringAngle = Math.abs(action.getSteering());
            double damp = accDamp.getMirroredValue(steeringAngle);

            action.setAcceleration(action.getAcceleration() * damp);

            /*if (data.getSpeed() < 10.0 && action.getAcceleration() < 0.5) {
                action.setAcceleration(0.5);
            }*/
        }

        if (action.getBrake() > 0.0) {
            double steeringAngle = Math.abs(action.getSteering());

            double damp = brakeDamp.getMirroredValue(steeringAngle);

            action.setBrake(damp);
        }

        abs.execute(data, action);
        action.limitValues();
    }   
}
