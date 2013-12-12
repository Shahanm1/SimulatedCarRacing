/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package champ2009client.automata;

import champ2009client.classifier.ClassifierConstants;
import champ2009client.evolution.EvolutionConstants;
import champ2009client.evolution.EvolutionManager;
import champ2009client.evolution.GeneticCapsule;
import champ2009client.fuzzy.FuzzyConstants;
import java.text.DecimalFormat;


/**
 *
 * @author Diego
 */
public class StatePrepareTurn extends State {
    
    //CONFIGURABLES
    private double _desired_alpha;
    private double _steering_to_side;
    private double _safe_distance;     
    private double _onSlowSpeedAccel;
    
    //NON CONFIGURABLES
    private double  _distance_to_edge;
    private int     _turn_sense;
    private int     _turn_type; //hard, soft...
    private double  _turn_value; //_turn_type VALUE (DEBUG)
    private double _alpha;
    private double _end_prepare_distance;
    private double _mid_prepare_distance;
    private double _fullBrake;
    private double _maintainAccel;
    private int    _subState;    
    private int    _brakeCounter;
    private final int eFIRST = 0;
    private final int eSECOND = 1;

    private int percLowBrake;
    private int percHighBrake;
    
    public StatePrepareTurn(StateManager manager)
    {
        //CONFIGURABLES
        _desired_alpha          = 0.04f; 
        _steering_to_side       = 0.1f;
        _safe_distance          = 33.0f;
        _onSlowSpeedAccel       = 0.9f;
        
        //NON CONFIGURABLES
        _manager                = manager;
        _turn_sense             = ClassifierConstants.FLAG_TURN_UNDEFINED;
        _end_prepare_distance   = 0.0f;
        _mid_prepare_distance   = 0.0f;
        _turn_type              = 0;
        _turn_value             = 0;
        _subState               = eFIRST;
        _distance_to_edge       = 0.0f;   
        _fullBrake              = 0.0f; //= 0.0f;
        _maintainAccel          = 0.5f;
        _brakeCounter           = 0;
        percLowBrake            = 3;
        percHighBrake           = 7;
    }

    //Return configurable parameters
	public GeneticCapsule[] getGeneticInfo()
	{
		GeneticCapsule[] info = new GeneticCapsule[3];
		GeneticCapsule c;
		int i = 0;
        
		//desired alpha
		c = new GeneticCapsule("StatePrepareTurn", "_desired_alpha");
		c.setData(new Double(_desired_alpha), EvolutionConstants.TYPE_MID_ANGLE);
		info[i++] = c;
        
		//mid accel
		c = new GeneticCapsule("StatePrepareTurn", "_steering_to_side");
		c.setData(new Double(_steering_to_side), EvolutionConstants.TYPE_MID_TURN);
		info[i++] = c;
        
		//slight turn
		c = new GeneticCapsule("StatePrepareTurn", "_safe_distance");
		c.setData(new Double(_safe_distance), EvolutionConstants.TYPE_DISTANCE);
		info[i++] = c;
        
		//soft turn
		//c = new GeneticCapsule("StatePrepareTurn", "_onSlowSpeedAccel");
		//c.setData(new Double(_onSlowSpeedAccel), EvolutionConstants.TYPE_ACCEL);
		//info[i++] = c;
        
		return info;
	}
    
    //Set configurable parameters
	public void setGeneticInfo(GeneticCapsule info)
	{
		String surname = info.getSurname();
		if(surname.equals("_desired_alpha"))
		{
			_desired_alpha = ((Double)info.getData()).doubleValue();
		}
		else if(surname.equals("_steering_to_side"))
		{
			_steering_to_side = ((Double)info.getData()).doubleValue();
		}
		else if(surname.equals("_safe_distance"))
		{
			_safe_distance = ((Double)info.getData()).doubleValue();   
		}
		else if(surname.equals("_onSlowSpeedAccel"))
		{
			_onSlowSpeedAccel = ((Double)info.getData()).doubleValue();
		}
		if(EvolutionManager.DEBUG) debugGeneticInfo();
	}
    
	public void debugGeneticInfo() 
	{ 
		System.out.println("PREPARE TURN: _desired_alpha = " + _desired_alpha + 
			" _steering_to_side = " + _steering_to_side + 
			" _safe_distance = " + _safe_distance + 
			" _onSlowSpeedAccel = " + _onSlowSpeedAccel); 
	}
    
    public void OnEnter()
    {
        _alpha = _desired_alpha;
       
        _distance_to_edge = _manager.getSensors().getTrackEdgeSensors()[9];
        _turn_sense = _manager.getClassifier().getFlag();
        _turn_type = _manager.getFuzzySystem().getFuzzySensorState(FuzzyConstants.INDEX_TURN_SENSOR);
        _turn_value = _manager.getHardnessTurnValue();             
        
        double edge_meters = _manager.getSensors().getDistanceRaced() + _distance_to_edge; //meters from start to edge
        _end_prepare_distance = edge_meters - _safe_distance;                              //meters to end preparing
        double gap = 3.0f*(_end_prepare_distance - _manager.getSensors().getDistanceRaced())/4.0f;
        _mid_prepare_distance = _manager.getSensors().getDistanceRaced() + gap;
        _subState               = eFIRST;   
        _brakeCounter           = 0;
        
        if(StateManager.DEBUG) System.out.println("DISTANCE: " + _distance_to_edge + ", LEFT_SENSE: " + 
                _turn_sense + ", TYPE: " + _turn_type + ", END_DISTANCE: " + _end_prepare_distance 
                + ", MID: " + _mid_prepare_distance + ", DISTRACED: " + _manager.getSensors().getDistanceRaced() );
    }
        
    
    
    public void OnUpdate(double deltaTime)
    {
    
        DecimalFormat myF = new DecimalFormat("0.00f");
        //Manage emergencies
        int emergencyRecoverState = manageEmergencies();
        if(emergencyRecoverState != -1)
        {
            _manager.setState(emergencyRecoverState);
            return;
        }

        
        //INPUT 
        //////////
        
        //FUZZY SYSTEM DATA
        int fuzzyTrackPos   = _manager.getFuzzySystem().getFuzzySensorState(FuzzyConstants.INDEX_TRACKPOS_SENSOR);
        int fuzzySpeed      = _manager.getFuzzySystem().getFuzzySensorState(FuzzyConstants.INDEX_SPEED_SENSOR);
        
        //RAW sesors data
        double  trackPos = _manager.getSensors().getTrackPosition();
        double  angle  = _manager.getSensors().getAngleToTrackAxis();
        double  distRaced = _manager.getSensors().getDistanceRaced();
        
        
		//We need to know if the turn is to the left, or to the right
        if(_turn_sense == ClassifierConstants.FLAG_TURN_UNDEFINED)
        {
            _turn_sense = _manager.getClassifier().getFlag();
            _turn_type = _manager.getFuzzySystem().getFuzzySensorState(FuzzyConstants.INDEX_TURN_SENSOR);
            double gap = 3.0f*(_end_prepare_distance - _manager.getSensors().getDistanceRaced())/4.0f;
            _mid_prepare_distance = _manager.getSensors().getDistanceRaced() + gap;
            _subState = eFIRST;
            
            if(_turn_sense == ClassifierConstants.FLAG_TURN_UNDEFINED)
            {                
                float steer;

                int maxTrackIndex = _manager.getMaxTrackValIndex();
                if(maxTrackIndex != -1)
                {
                    steer = (float) _manager.getClassifier().getSensorAngle(maxTrackIndex) * -1.0f;
                }
                else
                    steer = 0.0f;
                
                _manager.getAction().evol_steering = steer; //0.0f;
                _manager.getAction().evol_acceleration = 0.9f;
                return;
            }
        }
        
        double steer = 0.0f;
        double accel = 0.0f;
        
		//Let's decide if this turn is hard.. or can be done with only one hand
        boolean hardTurn = (_turn_type == FuzzyConstants.INDEX_TRACK_TURN_HARD) || 
                ((_turn_type == FuzzyConstants.INDEX_TRACK_TURN_MID) && 
                (fuzzySpeed == FuzzyConstants.INDEX_SPEED_FAST || fuzzySpeed == FuzzyConstants.INDEX_SPEED_VERY_FAST));
               
        
        // ACCELERATION
        switch(fuzzySpeed)
        {
	    //We are going slow, no brake
            case FuzzyConstants.INDEX_SPEED_SLOW:
                accel = 1.0f;
                break;

            case FuzzyConstants.INDEX_SPEED_MEDIUM:
                accel = 1.0f;
                if((_subState == eFIRST) && hardTurn)
                {
                    // Intermittent brake: 50% of the times maintain speed, otherwise push brake
                    if((_brakeCounter%10) < percLowBrake) // 3/10
                    {
                        accel = _fullBrake + 0.4f;
                    }

                    _brakeCounter++;
                    
                }else if(_subState == eSECOND)
                {
                    if(hardTurn)
                    {
                         accel = _fullBrake; 
                    }
                    else accel = _maintainAccel; 
                }
                break;

            case FuzzyConstants.INDEX_SPEED_FAST:
            case FuzzyConstants.INDEX_SPEED_VERY_FAST:
                
                if(_subState == eFIRST)
                {
                    // Intermittent brake: 70% of the times maintain speed, otherwise push brake
                    if(!hardTurn && ((_brakeCounter%10) < percHighBrake))  // 7/10
                    {
                        accel = _fullBrake + 0.3f;
                    }
                    else if(hardTurn)
                    {
                        accel = _fullBrake;
                    }else
                    {
                        accel = _maintainAccel;
                    }
                }
                else
                {
                    if(hardTurn)
                    {
                        accel = _fullBrake; 
                    }
                }
                _brakeCounter++;
                break;

            default: //-1, no set selected
                if(StateManager.DEBUG) System.out.println("Warning: NO SPEED SELECTED!");
                accel = 0.9f;
        }
        
        
        //STEERING: Adjust sense of steering and placement on the track
        if(_turn_sense != ClassifierConstants.FLAG_TURN_ON_LEFT && _alpha>0)  _alpha *= -1;
        boolean placed = false;
        if((_turn_sense == ClassifierConstants.FLAG_TURN_ON_LEFT) && 
           (fuzzyTrackPos == FuzzyConstants.INDEX_TRACKPOS_SIDE) && (trackPos<0))
        {
            placed = true;
        }else if((_turn_sense == ClassifierConstants.FLAG_TURN_ON_RIGHT) && 
           (fuzzyTrackPos == FuzzyConstants.INDEX_TRACKPOS_SIDE) && (trackPos>0))
        {
            placed = true;
        }

        String speedTxt = FuzzyConstants.FUZZY_RELATIONS[fuzzySpeed][1];
        if(StateManager.DEBUG) System.out.print("<" + _manager.getClassifier().getClassStr() + 
                                ",PRE" + (_subState+1) + "> " + 
                                 " ALP = " + myF.format(_alpha) + 
                                 ", ANG: " + myF.format(angle) + 
                                 ", spd: " + speedTxt +
                                 ", placed: " + placed +
                                 ", dRaced: " + myF.format(distRaced) +
                                 //", TO_LEFT: " + (_turn_sense == ClassifierConstants.FLAG_TURN_ON_LEFT) +                                      
                                 ", TYPE: "    + _turn_type                                     
                                 //", HARD: "    + hardTurn + 
                                 //", TO_MID: " + myF.format(_mid_prepare_distance - distRaced) +
                                 //", TO_END: " + myF.format(_end_prepare_distance - distRaced) 
                                );        
        
        //We use a substate to determine if we are close or far to the turn
        switch(_subState)
        {
            //FIRST: FAR TO TURN
            case eFIRST:

                _alpha = _desired_alpha; 
                if(!hardTurn) _alpha *= 0.5f;

                if(placed)
                {
                    steer = angle;

                //Go to the other side of the track
                }else if(Math.abs(angle) >= _alpha)
                {
                    //If we have reached desired angle, stop steering
                    steer = 0.0f;
                //if we haven't reached desired angle, turn more.
                }else
                {
                    steer = _steering_to_side;
                    //turn to left => modify steer
                    if(_turn_sense == ClassifierConstants.FLAG_TURN_ON_LEFT)
                    {
                        steer *= -1.0f;
                    }
                }
                        
                if(distRaced >= _mid_prepare_distance)
                    _subState = eSECOND;
                
                break;

            //SECOND: NEAR TO THE TURN
            case eSECOND:
		//SIGNUM of closer to 0
                double value = Math.min(Math.abs(_alpha), Math.abs(angle)); 
                
		//Decide steering, following the angle that offers a maximum "free space" ahead
		_alpha = value * ((value == Math.abs(_alpha)) ? Math.signum(_alpha) : Math.signum(angle)); 
				
                int maxTrackIndex = _manager.getMaxTrackValIndex();
                if(maxTrackIndex != -1)
                    steer = _manager.getClassifier().getSensorAngle(maxTrackIndex) * -1.0f;
                else
                    steer = 0.0f;

		//We are to close now. It is time to turn.
                if(distRaced > _end_prepare_distance) 
                {
                    //END PREPARING!!
                    if(StateManager.DEBUG) System.out.println("WE ARE OUT!");
                    _manager.setState(StateConstants.TAKE_TURN);
                }
                break;            
        }

        if(StateManager.DEBUG) System.out.print(", ST: " + myF.format(steer));
        if(StateManager.DEBUG) System.out.println(", AC: " + myF.format(accel));    
		
		//Set the action to execute in this state.
        _manager.getAction().evol_steering = (float)steer;
        _manager.getAction().evol_acceleration = (float)accel;
    }
    
    public void OnExit()
    {
    }
}
