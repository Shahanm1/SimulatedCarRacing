/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package champ2009client.automata;

import champ2009client.evolution.EvolutionConstants;
import champ2009client.evolution.EvolutionManager;
import champ2009client.evolution.GeneticCapsule;
import champ2009client.fuzzy.FuzzyConstants;
import java.text.DecimalFormat;

/**
 *
 * @author Diego
 */
public class StateEmergency extends State{

	//Stuck against a wall or a car

    private float _fullAccel;
    private float _hardTurn;
    
    private float _mTime;       //in seconds
    private float _mMaxTime;    //in seconds
    
    private int _mMaxTics;
    private int _mTics;
    
    public StateEmergency(StateManager manager)
    {
        _manager    = manager; 
        _mTime      = 0.0f;
    
        _fullAccel  = 1.0f;        
        _hardTurn   = 1.0f;
        _mMaxTime   = 2.7f;
    }
   
    //Return configurable parameters 
    public GeneticCapsule[] getGeneticInfo()
    {
        return null;
        /*GeneticCapsule[] info = new GeneticCapsule[1];
        GeneticCapsule c;
        int i = 0;
        
        //desired alpha
        c = new GeneticCapsule("StateEmergency", "_mMaxTime");
        c.setData(new Double(_mMaxTime), EvolutionConstants.TYPE_TIME);
        info[i++] = c;
        
        return info;
        */
    }
    
	//Set configurable parameters
    public void setGeneticInfo(GeneticCapsule info)
    {
        String surname = info.getSurname();
        if(surname.equals("_mMaxTime"))
        {
            _mMaxTime = (float) ((Double)info.getData()).doubleValue();
        }
        if(EvolutionManager.DEBUG) debugGeneticInfo();
    }
    
    public void debugGeneticInfo() { System.out.println("EMERGENCY: _mMaxTime = " + _mMaxTime); }
    
    public void OnEnter()
    { 
        _mTime      = 0.0f;
        
        _mTics = 0;
        _mMaxTics = 200; // (int)_mMaxTime*50;
    }
        
    public void OnUpdate(double deltaTime)
    {
        _mTime += deltaTime;
        _mTics++;
        
        //INPUT 
        //////////
               
        //FUZZY SYSTEM DATA
        int fuzzyTrackPos   = _manager.getFuzzySystem().getFuzzySensorState(FuzzyConstants.INDEX_TRACKPOS_SENSOR);
        //RAW sesors data
        double angle    =  - _manager.getSensors().getAngleToTrackAxis(); //adjust angle (signs are STUPID)
        double trackPos =    _manager.getSensors().getTrackPosition();
        
        
        //CHECK FOR END OF EMERGENCY
        int emergencyRecoverState = manageEmergencies();

        if((emergencyRecoverState == -1) && (fuzzyTrackPos == FuzzyConstants.INDEX_TRACKPOS_CENTERED))
        {
			//In this case, we wait for being on the center of the track before exiting this state
            recoverState();
            _manager.getAction().gear = 1;
            return;
        }else if(_mTics >= _mMaxTics)
        {
			//OR... maybe we have skipped the center part because of speed... 
			//as we do not want to go in reverse ad infinitum... change state
            recoverState();
            _manager.getAction().gear = 1;
            return;
        }
                
        double accel = _fullAccel;
        double steer = _hardTurn * angle;
       
        if(StateManager.DEBUG) 
        {
            DecimalFormat myF = new DecimalFormat("0.000f");
            System.out.print("EMERGENCY! mTime = " + _mTime + 
                                     ", angle: "   + myF.format(angle)
                            );
            System.out.print(", ST: " + myF.format(steer));
            System.out.println(", AC: " + myF.format(accel));
        }
        
		//Set the action to execute in this state.
        _manager.getAction().evol_steering = (float)steer;
        _manager.getAction().evol_acceleration = (float)accel;
        _manager.getAction().gear = -1;
        
        //Brake if we are close to the center part of the track.
        if(Math.abs(trackPos) < 0.35f)
        {
            _manager.getAction().evol_acceleration = 0.0f;
        }
        
    }
    
    public void OnExit()
    {
        
    }

}
