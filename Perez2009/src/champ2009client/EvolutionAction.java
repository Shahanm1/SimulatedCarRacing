/*
 * EvolutionAction.java
 * 
 * Created on 10-may-2008, 12:17:25
 * 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package champ2009client;

/**
 *
 * @author Diego
 */
public class EvolutionAction extends Action{
    
    public float evol_acceleration = 0;    // 0..1
    public float evol_brake = 0;           // 0..1
    public float evol_steering = 0;        //-1 .. 1
    public String toString () {
        return "(accel " + evol_acceleration + ") " +
               "(brake " + evol_brake + ") " +
               "(gear " + gear + ") " +
               "(steer " + evol_steering + ") " +
               "(meta " + (restartRace ? 1 : 0) + ")";
    }
    
}
