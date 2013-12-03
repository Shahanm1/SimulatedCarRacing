/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package champ2009client.automata;

import java.text.DecimalFormat;

/**
 *
 * @author Diego
 */

public class StateRecover extends State{

	//I am looking backwards
    
    private double  _fullAccel;
    private double  _hardTurn;
    private boolean _turn_clockwise;
    
    
    public StateRecover(StateManager manager)
    {
        _manager    = manager;
        _turn_clockwise = false;
        
        _fullAccel  = 1.0f;
        _hardTurn   = 1.0f;
    }
   
    public void OnEnter()
    {        
		//On entering on this state, we must decide the sense of the steering
        double trackPos = _manager.getSensors().getTrackPosition();
        if(trackPos > 0) //On left
        {
            _turn_clockwise = false;
        }else //On right
        {
            _turn_clockwise = true;
        }
    }
        
    
    public void OnUpdate(double deltaTime)
    {
    
        DecimalFormat myF = new DecimalFormat("0.000f");
        
        //CHECK FOR OTHER EMERGENCIES OR END OF IT
        int emergencyRecoverState = manageEmergencies();
        if(emergencyRecoverState != -1 && emergencyRecoverState != StateConstants.RECOVER)
        {
            _manager.setState(emergencyRecoverState);
            return;
        }else if(emergencyRecoverState == -1)
        {
			//No emergency now, let's go back to a normal state
            recoverState();
            return;
        }
        
        double accel = _fullAccel;
        double steer = _hardTurn;
        //Steer while accelerating 
        if(_turn_clockwise)
        {
            steer *= -1.0f;
        }
        
        if(StateManager.DEBUG) 
        {
            System.out.print("RECOVER! clockwise = " + _turn_clockwise);
            System.out.print(", ST: " + myF.format(steer));
            System.out.println(", AC: " + myF.format(accel));
        }               
        
		//Set the action to execute in this state.
        _manager.getAction().evol_steering = (float)steer;
        _manager.getAction().evol_acceleration = (float)accel;
    
    }
    
    public void OnExit()
    {
        
    }

}
