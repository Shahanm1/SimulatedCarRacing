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
public class StateTakeTurn extends State{

    //NON CONFIGURABLES
    private double _fullBrake;
    private double _fullAccel;
    private double _maintainAccel;
    private double _maintainTurn;
    private boolean _turn_sense_left;
    private boolean _didIBrake; 
    private int     _percMediumBrake;
    private int    _brakeCounter;

            
    //CONFIGURABLES
    private double  _brakeOnEdge;
    
    
    public StateTakeTurn(StateManager manager)
    {
        _manager                = manager;
        _turn_sense_left        = false;
        _fullBrake = 0.0f;
        _fullAccel = 1.0f;
        _maintainAccel = 0.5f;
        _maintainTurn  = 0.0f;
        
        //CONFIGURABLES
        _brakeOnEdge  = 0.45f;
        _didIBrake = false;
        _percMediumBrake = 8;
        _brakeCounter = 0;
    }
    
	//Set configurable parameters
    public GeneticCapsule[] getGeneticInfo()
    {
        return null;
        /*GeneticCapsule[] info = new GeneticCapsule[1];
        GeneticCapsule c;
        int i = 0;
        
        //desired alpha
        c = new GeneticCapsule("StateTakeTurn", "_brakeOnEdge");
        c.setData(new Double(_brakeOnEdge), EvolutionConstants.TYPE_ACCEL);
        info[i++] = c;
        
        return info;*/
    }

    
    public void setGeneticInfo(GeneticCapsule info)
    {
        String surname = info.getSurname();
        if(surname.equals("_brakeOnEdge"))
        {
            _brakeOnEdge = ((Double)info.getData()).doubleValue();
        }   
        if(EvolutionManager.DEBUG) debugGeneticInfo();
    }
    
    public void debugGeneticInfo() { System.out.println("TAKE TURN: _brakeOnEdge = " + _brakeOnEdge); }
    
    public void OnEnter()
    {
        _didIBrake = false;
        _brakeCounter = 0;
    }
    
    public void OnUpdate(double deltaTime)
    {
        //Manage emergencies
        int emergencyRecoverState = manageEmergencies();
        if(emergencyRecoverState != -1)
        {
            _manager.setState(emergencyRecoverState);
            return;
        }
            
        DecimalFormat myF = new DecimalFormat("0.00f");
        
        //INPUT 
        //////////
        
        //CLASSIFIER DATA
        int currentClass = _manager.getClassifier().getCurrentClass();
               
        //FUZZY SYSTEM DATA
        int fuzzySpeed      = _manager.getFuzzySystem().getFuzzySensorState(FuzzyConstants.INDEX_SPEED_SENSOR);
        int fuzzyEdge       = _manager.getFuzzySystem().getFuzzySensorState(FuzzyConstants.INDEX_EDGE_SENSOR);
        
        //RAW sesors data
        double  angle  = _manager.getSensors().getAngleToTrackAxis();
        double  carSpeed = _manager.getSensors().getSpeed();
        double  distRaced = _manager.getSensors().getDistanceRaced();
        int turn_type = _manager.getFuzzySystem().getFuzzySensorState(FuzzyConstants.INDEX_TURN_SENSOR);
        
        
        double steer = 0.0f;
        double accel = 0.6f;
        
        if(StateManager.DEBUG) System.out.print("<" + _manager.getClassifier().getClassStr() + 
                            "> TURN! ANGLE: " + myF.format(angle) + 
                                ", delta: " + myF.format(deltaTime) +
                                 ", speed: " + myF.format(carSpeed) +
                                 ", TO_LEFT: " + _turn_sense_left                                       
                                   );

        // We did the turn! It is over, so go back to RUN state
        if( currentClass == ClassifierConstants.CLASS_STRAIGHT )
        {
            _manager.setState(StateConstants.RUN);
        }

        //Decide steering, following the angle that offers a maximum "free space" ahead
        int maxTrackIndex = _manager.getMaxTrackValIndex();
        if(maxTrackIndex != -1)
            steer = _manager.getClassifier().getSensorAngle(maxTrackIndex) * -1.0f;
        else
            steer = 0.0f;
        
        //This is for debug
        String speedTxt = FuzzyConstants.FUZZY_RELATIONS[fuzzySpeed][1];
        String edgeTxt = FuzzyConstants.FUZZY_RELATIONS[fuzzyEdge][1];

        //This is for ending a recovering from StateEmergency (it goes in reverse)
        if(carSpeed < 0)
        {
            accel = _fullAccel;
            steer = _maintainTurn;
        }else
        {
        
			//Let's decide if this turn is hard.. or can be done with only one hand
            boolean hardTurn = (turn_type == FuzzyConstants.INDEX_TRACK_TURN_HARD) || 
                ((turn_type == FuzzyConstants.INDEX_TRACK_TURN_MID) && 
                (fuzzySpeed == FuzzyConstants.INDEX_SPEED_FAST || fuzzySpeed == FuzzyConstants.INDEX_SPEED_VERY_FAST));
         
            
            switch(fuzzySpeed)
            {
		//We are going slow, no brake
                case FuzzyConstants.INDEX_SPEED_SLOW:
                    accel = _fullAccel;
                    break;

                case FuzzyConstants.INDEX_SPEED_MEDIUM:
                    if(fuzzyEdge == FuzzyConstants.INDEX_EDGE_FAR) accel = _fullAccel;
                    else if(fuzzyEdge == FuzzyConstants.INDEX_EDGE_CLOSE)
                    {
                        // Intermittent brake: 66% of the times maintain speed, otherwise push brake
                        if(hardTurn && ((_brakeCounter%3) < 2))
                        {
                            accel = _fullBrake + 0.2f;
                            _didIBrake = true;
                        }else
                        {
                            //accel = _brakeOnEdge;
                            accel = _maintainAccel;
                        }
                        _brakeCounter++;
                    }
                    else if(fuzzyEdge == FuzzyConstants.INDEX_EDGE_VERY_CLOSE)
                    {
                        //accel = _fullBrake;
			//If we are very close to the edge, and we have no brake before, we have to do it.
                        if(_didIBrake)
                            accel = _maintainAccel; //GAS FOR STEERING (near 0 means NO steer!)
                        else
                            accel = _fullBrake;    
                        
                        _brakeCounter++;
                    }
                    break;

                case FuzzyConstants.INDEX_SPEED_FAST:
                    if(fuzzyEdge == FuzzyConstants.INDEX_EDGE_FAR) accel = _fullAccel;
                    else if(fuzzyEdge == FuzzyConstants.INDEX_EDGE_CLOSE) /*accel = _fullBrake;*/
                    {
                        accel = _fullBrake;
                        if(hardTurn && ((_brakeCounter%3) < 2))
                        {
                            accel = _fullBrake + 0.1f;
                            _didIBrake = true;
                        }else
                        {
                            //accel = _brakeOnEdge;
                            accel = _maintainAccel;
                        }
                        _brakeCounter++;
                    }
                    else if(fuzzyEdge == FuzzyConstants.INDEX_EDGE_VERY_CLOSE) /*accel = _maintainAccel;*/ //GAS FOR STEERING (near 0 means NO steer!!)
                    {
                        if(_didIBrake)
                            accel = _maintainAccel; //GAS FOR STEERING (near 0 means NO steer!)
                        else
                            accel = _fullBrake;    
                        
                        _brakeCounter++;
                    
                    }
                    
                    else accel = _maintainAccel;
                    break;                

                case FuzzyConstants.INDEX_SPEED_VERY_FAST:
                    if(fuzzyEdge == FuzzyConstants.INDEX_EDGE_FAR) accel = _fullAccel;
                    else if(fuzzyEdge == FuzzyConstants.INDEX_EDGE_CLOSE) accel = _brakeOnEdge / 2.0f;
                    else if(fuzzyEdge == FuzzyConstants.INDEX_EDGE_VERY_CLOSE) accel = _maintainAccel; //GAS FOR STEERING (near 0 means NO steer!!)
                    else accel = _maintainAccel;

                    break;
                    
                default: //-1, no set selected
                    accel = 0.75f;
            }
        }
        
        //For safety: if damage is at 80%, limit speed at 180km/h, so we can finish the race without crashing the car
        if(_manager.getSensors().getDamage() > 8000)
        {
            if(carSpeed > 180.0f)
            {
                accel = 0.5f;
            }
        }
        
        
        //Go to the side of the track
        if(StateManager.DEBUG) System.out.print(", SPEED: " + speedTxt);
        if(StateManager.DEBUG) System.out.print(", EDGE: " + edgeTxt);
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
