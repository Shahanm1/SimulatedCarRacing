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
public class StateRun extends State {

    //NON CONFIGURABLES
    private double _fullBrake;
    private double _fullAccel;
    private double _maintainAccel;
    private double _maintainTurn;
    
    //CONFIGURABLES
    private double _brakeOnEdge;
    

    //Constants
    private double _initial_alpha_side;
    private double _initial_alpha_center;

    private double _alpha_side;
    private double _alpha_center;
    
    public StateRun(StateManager manager)
    {
        //NON CONFIGURABLES
        _manager = manager;
        _fullBrake = 0.0f;
        _fullAccel = 1.0f;
        _maintainAccel = 0.5f;
        _maintainTurn  = 0.0f;
    
        //CONFIGURABLES
        _brakeOnEdge  = 0.25f;
        
        _initial_alpha_side = 0.02f;
        _initial_alpha_center = 0.07f; //0.02f;
        
        
    }
    
    
	//Return configurable parameters
    public GeneticCapsule[] getGeneticInfo()
    {
        return  null;
        /*GeneticCapsule[] info = new GeneticCapsule[1];
        GeneticCapsule c;
        int i = 0;
        
        c = new GeneticCapsule("StateRun", "_brakeOnEdge");
        c.setData(new Double(_brakeOnEdge), EvolutionConstants.TYPE_ACCEL);
        info[i++] = c;
        
        return info;*/
    }

    //Set configurable parameters
    public void setGeneticInfo(GeneticCapsule info)
    {
     
        String surname = info.getSurname();
        if(surname.equals("_brakeOnEdge"))
        {
            _brakeOnEdge = ((Double)info.getData()).doubleValue();
        }   
        
        if(EvolutionManager.DEBUG) debugGeneticInfo();
    }    
    
    public void debugGeneticInfo() { System.out.println("RUN: _brakeOnEdge = " + _brakeOnEdge); }
    
    
    public void OnEnter()
    {
        _alpha_side = _initial_alpha_side;
        _alpha_center = _initial_alpha_center;
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
        
        
        DecimalFormat myF = new DecimalFormat("0.000f");
        
        //INPUT 
        //////////
        
        //CLASSIFIER DATA
        int currentClass = _manager.getClassifier().getCurrentClass();
               
        //RAW sesors data
        boolean trackPosLeft = _manager.getSensors().getTrackPosition() > 0.0f;
        double  angle  = _manager.getSensors().getAngleToTrackAxis();
        double  carSpeed = _manager.getSensors().getSpeed();

        int fuzzySpeed      = _manager.getFuzzySystem().getFuzzySensorState(FuzzyConstants.INDEX_SPEED_SENSOR);
        int fuzzyEdge       = _manager.getFuzzySystem().getFuzzySensorState(FuzzyConstants.INDEX_EDGE_SENSOR);
        int fuzzyTrackPos   = _manager.getFuzzySystem().getFuzzySensorState(FuzzyConstants.INDEX_TRACKPOS_SENSOR);
        String speedTxt = FuzzyConstants.FUZZY_RELATIONS[fuzzySpeed][1];
        String edgeTxt = FuzzyConstants.FUZZY_RELATIONS[fuzzyEdge][1];
        int maxTrackIndex = _manager.getMaxTrackValIndex();
        double decrementMult = (trackPosLeft)? -0.001f : 0.001f;
        
        double steer = 0.0f;
        double accel = 1.0f;


        if(StateManager.DEBUG) System.out.print("<" + _manager.getClassifier().getClassStr() + ", RUN> " + 
                         "disRaced: " + myF.format(_manager.getSensors().getDistanceRaced()) +
                         ", ANG = " + myF.format(angle) + 
                         ", trackP: " + myF.format(_manager.getSensors().getTrackPosition()) +
                         ", speed: " + speedTxt +
                         ", EDGE: " + edgeTxt +
                         ", ON_LEFT: " + trackPosLeft 
                         );
                                
        
        //In case of classifier input (e.g. we are in a TURN, STRAIGHT, PRE_TURN...) we do different things.
        switch(currentClass)
        {
            //TURN
            case ClassifierConstants.CLASS_TURN:
                
		//Decide steering, following the angle that offers a maximum "free space" ahead
                if(maxTrackIndex != -1)
                    steer = _manager.getClassifier().getSensorAngle(maxTrackIndex) * -1.0f;
                else
                    steer = _maintainTurn;

                break;
        
            case ClassifierConstants.CLASS_STRAIGHT:
                
                    steer = angle - _alpha_center;
                    _alpha_center += decrementMult * deltaTime;

                    //Control end of steering
                    if(trackPosLeft)
                    {
                        if(_alpha_center <= 0) _alpha_center = 0.0f;
                    }
                    else if(_alpha_center >= 0)
                    {
                        _alpha_center = 0.0f;
                    }
        
                    break;
                
            //NEXT TO TURN
            case ClassifierConstants.CLASS_PRE_TURN:
		//We are near a turn, better to change to the state that manages this.
                _manager.setState(StateConstants.PREPARE_TURN);
                break;
                
                
            default:
                if(StateManager.DEBUG) System.out.println("NOT STRAIGHT NOR PRE_TURN_LEFT!: " + _manager.getClassifier().getCurrentClass());
        }
  
        //ACCELERATION
        //This is for ending a recovering from StateEmergency (it goes in reverse)
        if(carSpeed < 0)
        {
            accel = _fullBrake;
            steer = _maintainTurn;
 	//For safety: if damage is higher than 80%, limit speed at 180km/h, so we can finish the race without crashing the car            
        }else if((_manager.getSensors().getDamage() > 8000) && (carSpeed > 180.0f))
        {
            accel = 0.5f;
        //Decide acceleration, depending on speed and edge fuzzy set states.
        }else
        {        
            switch(fuzzySpeed)
            {
                case FuzzyConstants.INDEX_SPEED_MEDIUM:                        
                case FuzzyConstants.INDEX_SPEED_SLOW:
                    accel = _fullAccel;
                    break;

                case FuzzyConstants.INDEX_SPEED_FAST:
                    if(fuzzyEdge == FuzzyConstants.INDEX_EDGE_FAR) accel = _fullAccel;
                    else if((fuzzyEdge == FuzzyConstants.INDEX_EDGE_CLOSE) || (fuzzyEdge == FuzzyConstants.INDEX_EDGE_VERY_CLOSE))
                    {
                        accel = _fullBrake;
                        _manager.setState(StateConstants.PREPARE_TURN);
                    }else{
                        if(StateManager.DEBUG) System.out.println("(StateRun.java) ON FAST, FUZZY_EDGE VALUE UNDEFINED!!! ");
                        accel = _maintainAccel;
                    }

                    break;                

                case FuzzyConstants.INDEX_SPEED_VERY_FAST:
                    if(fuzzyEdge == FuzzyConstants.INDEX_EDGE_FAR) accel = _fullAccel;
                    else if(fuzzyEdge == FuzzyConstants.INDEX_EDGE_CLOSE) accel = _brakeOnEdge/2.0f;
                    else if(fuzzyEdge == FuzzyConstants.INDEX_EDGE_VERY_CLOSE) accel = _maintainAccel; //GAS FOR STEERING (near 0 means NO steer!!)
                    else{
                        if(StateManager.DEBUG) System.out.println("(StateRun.java) ON VERY FAST, FUZZY_EDGE VALUE UNDEFINED!!! ");
                        accel = _maintainAccel;
                    }

                    break;
                default: //-1, no set selected
                    accel = 1.0f;
            }
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
