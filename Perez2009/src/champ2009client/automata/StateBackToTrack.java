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
public class StateBackToTrack extends State{

	//We are outside the track!

    private double _desired_alpha;
    private double  _alpha;
        
    private double  _midAccel;
    private double  _fullAccel;
    private double  _softTurn;
    private double  _slightTurn;
    private double  _hardTurn;
    private double  _maintainAccel;
    private double  _brakeCounter;
    
    public StateBackToTrack(StateManager manager)
    {
        _manager                = manager;
        _alpha                  = 0.0f;
    
        _desired_alpha          = 0.35f;
        _midAccel   = 0.6f;
        _fullAccel  = 1.0f;
        _slightTurn = 0.1f;
        _softTurn   = 0.4f;
        _hardTurn   = 1.0f; 
        _maintainAccel = 0.5f;
        _brakeCounter = 0;
    }
    
	//Return configurable parameters 
    public GeneticCapsule[] getGeneticInfo()
    {
        GeneticCapsule[] info = new GeneticCapsule[4];
        GeneticCapsule c;
        int i = 0;
        
        //desired alpha
        c = new GeneticCapsule("StateBackToTrack", "_desired_alpha");
        c.setData(new Double(_desired_alpha), EvolutionConstants.TYPE_MID_ANGLE);
        info[i++] = c;
        
        //mid accel
        c = new GeneticCapsule("StateBackToTrack", "_midAccel");
        c.setData(new Double(_midAccel), EvolutionConstants.TYPE_ACCEL);
        info[i++] = c;
        
        //slight turn
        c = new GeneticCapsule("StateBackToTrack", "_slightTurn");
        c.setData(new Double(_slightTurn), EvolutionConstants.TYPE_MID_TURN);
        info[i++] = c;
        
        //soft turn
        c = new GeneticCapsule("StateBackToTrack", "_softTurn");
        c.setData(new Double(_softTurn), EvolutionConstants.TYPE_MID_TURN);
        info[i++] = c;
        
        return info;
    }
    
    //Set configurable parameters
    public void setGeneticInfo(GeneticCapsule info)
    {
        String surname = info.getSurname();
        if(surname.equals("_desired_alpha"))
        {
            _desired_alpha = ((Double)info.getData()).doubleValue();
        }else if(surname.equals("_midAccel"))
        {
            _midAccel = ((Double)info.getData()).doubleValue();
        }else if(surname.equals("_slightTurn"))
        {
            _slightTurn = ((Double)info.getData()).doubleValue();   
        }else if(surname.equals("_softTurn"))
        {
            _softTurn = ((Double)info.getData()).doubleValue();
        }
        
        if(EvolutionManager.DEBUG) debugGeneticInfo();
    }
    
    public void debugGeneticInfo() { 
        System.out.println("BACK TO TRACK: _desired_alpha = " + _desired_alpha + 
                " _midAccel = " + _midAccel + 
                " _slightTurn = " + _slightTurn + 
                " _softTurn = " + _softTurn); 
    }
    
    public void OnEnter()
    {
        _brakeCounter = 0;
    }
    
    public void OnUpdate(double deltaTime)
    {
        DecimalFormat myF = new DecimalFormat("0.00f");
        
        //CHECK FOR OTHER EMERGENCIES OR END OF IT
        int emergencyRecoverState = manageEmergencies();
        if((emergencyRecoverState != -1) && (emergencyRecoverState != StateConstants.BACK_TO_TRACK))
        {
            _manager.setState(emergencyRecoverState);
            return;
        }else if(emergencyRecoverState == -1)
        {
			//No emergency now, let's go back to a normal state
            recoverState();
            return;
        }
     
        //INPUT 
        //////////
               
        //FUZZY SYSTEM DATA
        int fuzzySpeed      = _manager.getFuzzySystem().getFuzzySensorState(FuzzyConstants.INDEX_SPEED_SENSOR);
        
        //RAW sesors data
        double angle    =  _manager.getSensors().getAngleToTrackAxis(); 
        double trackPos = _manager.getSensors().getTrackPosition();

        //internal flags
        boolean outsideLeft     = (trackPos > 1);
        boolean outsideRight    = (trackPos < -1);
        boolean speedSlow       = (fuzzySpeed == FuzzyConstants.INDEX_SPEED_SLOW);
        boolean speedMedium     = (fuzzySpeed == FuzzyConstants.INDEX_SPEED_MEDIUM);
        boolean speedFast       = (fuzzySpeed == FuzzyConstants.INDEX_SPEED_FAST);

        _alpha = _desired_alpha;     
                
        double accel = _midAccel;
        double steer = _hardTurn;
        
	//Depending on where are we (left or right outside), we steer to go back to the track.
        if(outsideLeft){
            if(angle > _alpha) steer = 0; //Limit reached
            else if(speedSlow) steer = -_softTurn;
            else if(speedFast || speedMedium) steer = -_slightTurn;
        }else if(outsideRight){
            if(angle < -_alpha) steer = 0; //Limit reached
            else if(speedSlow) steer = _softTurn;
            else if(speedFast || speedMedium) steer = _slightTurn;
        }
        
        if(speedSlow)
        {
	    //Came on, man, I can not spend the whole day on this...
            accel = _fullAccel;
        }
        else
        {
            // Intermittent brake: 66% of the times maintain speed, otherwise push brake
            if((_brakeCounter%20) < 2)
            {
                accel = 0.0f;
            }else
            {
                accel = _maintainAccel;
            }
            _brakeCounter++;
        }
        
        if(StateManager.DEBUG) 
        {
            String speedTxt = FuzzyConstants.FUZZY_RELATIONS[fuzzySpeed][1];
            System.out.print("BACK_TO_TRACK! ANGLE: " + myF.format(angle) + 
                            ", speed: " + speedTxt +
                             ", alpha: " + myF.format(_alpha) +
                             ", trackPos: " + myF.format(_manager.getSensors().getTrackPosition()) +
                             ", ON_LEFT: " + outsideLeft + 
                             ", LAT_SPEED: " + _manager.getSensors().getLateralSpeed()
                               );

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
