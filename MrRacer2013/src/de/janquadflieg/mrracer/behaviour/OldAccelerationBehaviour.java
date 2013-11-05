/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.janquadflieg.mrracer.behaviour;

import de.janquadflieg.mrracer.telemetry.ModifiableAction;
import de.janquadflieg.mrracer.telemetry.SensorData;
import static de.janquadflieg.mrracer.data.CarConstants.CAR_WIDTH;

/**
 *
 * @author quad
 */
public class OldAccelerationBehaviour
        extends AbstractDampedAccelerationBehaviour{

    /** Anti lock brakes. */
    private Behaviour abs = new de.janquadflieg.mrracer.behaviour.ABS();    
    /** Track margin in meter at the outside of a corner. */
    private static final double OUTSIDE_MARGIN = 1.0;

    public OldAccelerationBehaviour() {
    }    

    @Override
    public void execute(SensorData data, ModifiableAction action) {
        //System.out.println(targetSpeed+" "+System.currentTimeMillis());
        if (data.getSpeed() <= targetSpeed) {
            action.setAcceleration(1.0);
            action.setBrake(0.0);

        } else if (data.getSpeed() > targetSpeed + 2.0) {
            action.setAcceleration(0.0);
            action.setBrake(1.0);

        } else {
            action.setAcceleration(0.2);
            action.setBrake(0.0);

            if ((current != null && !current.isFull()) || !s.isFull()) {
                if (data.getSpeed() > 40.0) {
                    action.setAcceleration(0.0);
                    //System.out.println("Sonderfall @" + data.getDistanceFromStartLineS());
                }
            }
        }

        if (action.getAcceleration() > 0.0) {
            if ((current != null && current.isCorner()) || s.isCorner()) {
                boolean mirror = false;
                double trackPos = data.getTrackPosition();

                if (current != null) {
                    mirror = current.isRight();

                } else {
                    mirror = s.isRight();
                }

                if (mirror) {
                    trackPos *= -1.0;
                }

                double absTrackPos = SensorData.calcAbsoluteTrackPosition(trackPos, trackWidth);

                // outside
                if (absTrackPos + (CAR_WIDTH * 0.5) > trackWidth - OUTSIDE_MARGIN && data.getSpeed() > 75.0) {
                    double beta = ((absTrackPos + (CAR_WIDTH * 0.5)) - (trackWidth - OUTSIDE_MARGIN)) / OUTSIDE_MARGIN;
                    beta = Math.min(beta, 1.0);
                    double factor = 1.0 - (0.8 * (1.0 - beta));
                    action.setAcceleration(action.getAcceleration() * factor);
                    //System.out.println("Curve damp @" + data.getDistanceFromStartLineS());
                }
            }

            double steeringAngle = Math.abs(action.getSteering());
            double damp = accDamp.getMirroredValue(steeringAngle);

            //System.out.println(damp);

            action.setAcceleration(action.getAcceleration() * damp);

            if (data.getSpeed() < 10.0 && (action.getAcceleration() < 0.5 || Double.isNaN(action.getAcceleration()))) {
                //System.out.println("Save mode @" + data.getDistanceFromStartLineS());
                action.setAcceleration(0.5);
            }
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