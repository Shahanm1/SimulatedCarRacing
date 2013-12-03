    /*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package champ2009client.automata;

/**
 *
 * @author Diego
 */
public class StateConstants {

	//Numeric codification for each state
    public static final int RUN = 0;
    public static final int PREPARE_TURN = 1; 
    public static final int TAKE_TURN = 2;
    public static final int EMERGENCY = 3;
    public static final int RECOVER = 4;
    public static final int BACK_TO_TRACK = 5;
    public static final int OVERTAKE = 6; //Not in use in this version.
    
    public static final int NUM_STATES = 6;
    
    public static final float MAX_UNSTUCK_SPEED = 5.0f;  //Minimum speed to be stuck
    public static final float MAX_UNSTUCK_TIME = 1.5f;   //Seconds before consider the car stuck    
    
    public static final float MAX_UNSTUCK_CAR_SPEED = 15.0f;
    public static final float MAX_UNSTUCK_CAR_DISTANCE = 12.0f;
    //public static final float MAX_UNSTUCK_CAR_TIME = 0.75f;
    
}
