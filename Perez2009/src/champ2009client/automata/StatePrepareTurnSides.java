/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package champ2009client.automata;

import champ2009client.EvolutionAction;
import champ2009client.classifier.ClassifierConstants;
import champ2009client.classifier.ClassifierSystem;
import champ2009client.fuzzy.FuzzyConstants;
import java.text.DecimalFormat;


    
    
    
/**
 *
 * @author Diego
 */
public class StatePrepareTurnSides extends State{

    //Constants
    private double _desired_alpha;
    
    private double _alpha;
    private double _steering_to_side;
     
    private int     _turn_sense;
    private int     _turn_type; //hard, soft...
    private double  _turn_value; //_turn_type VALUE

    private double _distance_to_edge;
    private double _safe_distance;
    private double _end_prepare_distance;
    
    public StatePrepareTurnSides(StateManager manager)
    {
        _manager                = manager;
        _desired_alpha          = 0.03f; //= 0.075f;
        _distance_to_edge       = 0.0f;
        _steering_to_side       = 0.075f; 
        _turn_sense             = ClassifierConstants.FLAG_TURN_UNDEFINED;
        //_steerHasEnded          = false;
        _end_prepare_distance   = 0.0f;
        _safe_distance          = 30.0f;
        _turn_type              = 0;
        _turn_value             = 0;
    }
    
    
    public void OnEnter()
    {
        _alpha = _desired_alpha;
       
        _distance_to_edge = _manager.getSensors().getTrackEdgeSensors()[9];
        _turn_sense = _manager.getClassifier().getFlag();
        _turn_type = _manager.getFuzzySystem().getFuzzySensorState(FuzzyConstants.INDEX_TURN_SENSOR);
        _turn_value = _manager.getHardnessTurnValue();             
        //_steerHasEnded = false;
        
        double edge_meters = _manager.getSensors().getDistanceRaced() + _distance_to_edge; //meters from start to edge
        _end_prepare_distance = edge_meters - _safe_distance;                              //meters to end preparing
        
        if(StateManager.DEBUG) System.out.println("DISTANCE: " + _distance_to_edge + ", LEFT_SENSE: " + _turn_sense + ", TYPE: " + _turn_type);
    }
        
    
    public void OnUpdate(double deltaTime)
    {
    
        DecimalFormat myF = new DecimalFormat("0.00f");
        
        if(manageEmergencies() == -1)
        {
            return;
        }
        
        if(_turn_sense == ClassifierConstants.FLAG_TURN_UNDEFINED)
        {
            _turn_sense = _manager.getClassifier().getFlag();
            _turn_type = _manager.getFuzzySystem().getFuzzySensorState(FuzzyConstants.INDEX_TURN_SENSOR);
            if(_turn_sense == ClassifierConstants.FLAG_TURN_UNDEFINED)
            {
                //turn ahead... but I do not know still the sense. Just slow speed and keep ahead
                _manager.getAction().evol_steering = 0.0f;
                _manager.getAction().evol_acceleration = 0.5f;
                return;
            }
        }
        
        //INPUT 
        //////////
        
        //CLASSIFIER DATA
        int currentClass = _manager.getClassifier().getCurrentClass();
               
        //FUZZY SYSTEM DATA
        //int fuzzyAngle      = _manager.getFuzzySystem().getFuzzySensorState(FuzzyConstants.INDEX_ANGLE_SENSOR);
        int fuzzyTrackPos   = _manager.getFuzzySystem().getFuzzySensorState(FuzzyConstants.INDEX_TRACKPOS_SENSOR);
        int fuzzySpeed      = _manager.getFuzzySystem().getFuzzySensorState(FuzzyConstants.INDEX_SPEED_SENSOR);
        //int fuzzyTurn       = _manager.getFuzzySystem().getFuzzySensorState(FuzzyConstants.INDEX_TURN_SENSOR);
        
        //RAW sesors data
        //boolean trackPosLeft = _manager.getSensors().getTrackPosition() > 0.0f;
        double  trackPos = _manager.getSensors().getTrackPosition();
        double  angle  = _manager.getSensors().getAngleToTrackAxis();
        double  carSpeed = _manager.getSensors().getSpeed();
        double  distRaced = _manager.getSensors().getDistanceRaced();
        
        double steer = 0.0f;
        double accel = 0.0f;
        
        //Adjust sense of steering
        //double decrementMult = (trackPosLeft)? -0.0001f : 0.0001f;
        //double decrementMult = (_turn_sense == ClassifierConstants.FLAG_TURN_ON_LEFT)? -0.1f : 0.1f;
        if(_turn_sense != ClassifierConstants.FLAG_TURN_ON_LEFT && _alpha>0)  _alpha *= -1;

        boolean hardTurn = (_turn_type == FuzzyConstants.INDEX_TRACK_TURN_HARD) || 
                ((_turn_type == FuzzyConstants.INDEX_TRACK_TURN_MID) && 
                (fuzzySpeed == FuzzyConstants.INDEX_SPEED_FAST || fuzzySpeed == FuzzyConstants.INDEX_SPEED_VERY_FAST));
        
        //Run... and center on the track
        //if(fuzzyTrackPos == FuzzyConstants.INDEX_TRACKPOS_CENTERED)
        
//        boolean placeOk = ((_turn_sense == ClassifierConstants.FLAG_TURN_ON_LEFT) && trackPos<0) ||
//                          ((_turn_sense == ClassifierConstants.FLAG_TURN_ON_RIGHT) && trackPos>0);      
        
//        if( (fuzzyTrackPos == FuzzyConstants.INDEX_TRACKPOS_CENTERED) ||
//            (fuzzyTrackPos == FuzzyConstants.INDEX_TRACKPOS_SIDE && !placeOk) )
        if(fuzzyTrackPos == FuzzyConstants.INDEX_TRACKPOS_CENTERED)
        {
            //RESET ALPHA SIDE (DOES NOT CHANGE IN THIS STATE!)
            _alpha = _desired_alpha; 
            if(!hardTurn) _alpha *= 0.5f;
            /*
            System.out.print("PREPARE TURN CT! ALPHA = " + myF.format(_alpha) + 
                                     ", ANGLE: " + myF.format(angle) + 
                                //     ", delta: " + myF.format(deltaTime) +
                                     ", speed: " + myF.format(carSpeed) +
                                     ", TO_LEFT: " + (_turn_sense == ClassifierConstants.FLAG_TURN_ON_LEFT) +                                      
                                     ", TYPE: "    + _turn_type    
                                       );
            */
            
            
            //Go to the side of the track
            if(Math.abs(angle) >= _alpha)
            {
                //If we have reached desired angle, stop steering
                steer = 0.0f;
                if(StateManager.DEBUG) System.out.println("ALPHA REACHED!!");
            }
            else
            {
                //if we haven't reached desired angle, turn more.
                //steer = _alpha;
                steer = _steering_to_side;
                //turn to left => modify steer
                if(_turn_sense == ClassifierConstants.FLAG_TURN_ON_LEFT)
                {
                    steer *= -1.0f;
                }
            }
            
            if(StateManager.DEBUG) System.out.print("TURN TYPE " + _turn_type + ", VAL: " + _turn_value + ", ANGLE: " + angle);
            if(StateManager.DEBUG) System.out.print(", ST: " + myF.format(steer));
            
            switch(fuzzySpeed)
            {
                case FuzzyConstants.INDEX_SPEED_SLOW:
                    if(hardTurn) accel = 0.6f;
                    else accel = 0.65f;
                    break;

                case FuzzyConstants.INDEX_SPEED_MEDIUM:
                    if(hardTurn) accel = 0.2f;
                    else accel = 0.55f;
                    break;

                case FuzzyConstants.INDEX_SPEED_FAST:
                    accel = 0.1f;
                    break;

                case FuzzyConstants.INDEX_SPEED_VERY_FAST:
                    accel = 0.0f;
                    break;

                default: //-1, no set selected
                    if(StateManager.DEBUG) System.out.println("Warning: NO SPEED SELECTED!");
                    accel = 0.25f;
            }
            
            if(StateManager.DEBUG) System.out.println(", AC: " + myF.format(accel));
            
            
            if(_end_prepare_distance < distRaced) 
            {
                //END PREPARING!!
                if(StateManager.DEBUG) System.out.println("WE ARE OUT!");
                _manager.setState(StateConstants.TAKE_TURN);
            }
            
            

        }else if(fuzzyTrackPos == FuzzyConstants.INDEX_TRACKPOS_SIDE)
        {
            //System.out.print("MIN ALPHA => " +  myF.format(_alpha) + "," + myF.format(angle));

            //ABS VALUE of Closer to 0
            double value = Math.min(Math.abs(_alpha), Math.abs(angle)); 
            //SIGNUM of Closer to 0
            _alpha = value * ((value == Math.abs(_alpha)) ? Math.signum(_alpha) : Math.signum(angle)); 


            if(StateManager.DEBUG) System.out.print("PREPARE TURN SD! ALPHA = " + myF.format(_alpha) + 
                                ", ANGLE: " + myF.format(angle) + 
                                ", speed: " + myF.format(carSpeed) +
                                ", TO_LEFT: " + (_turn_sense == ClassifierConstants.FLAG_TURN_ON_LEFT) +                                      
                                ", TYPE: "    + _turn_type    +
                                ", TO_END: " + (_end_prepare_distance - distRaced)
                                );
            

            //Center the car
            steer = angle;
            accel = 0.65f;
            
            //PRUEBA!
            switch(fuzzySpeed)
            {
                case FuzzyConstants.INDEX_SPEED_SLOW:
                    if(hardTurn) accel = 0.6f;
                    else accel = 0.65f;
                    break;

                case FuzzyConstants.INDEX_SPEED_MEDIUM:
                    if(hardTurn) accel = 0.2f;
                    else accel = 0.55f;
                    break;

                case FuzzyConstants.INDEX_SPEED_FAST:
                    accel = 0.1f;
                    break;

                case FuzzyConstants.INDEX_SPEED_VERY_FAST:
                    accel = 0.0f;
                    break;

                default: //-1, no set selected
                    if(StateManager.DEBUG) System.out.println("Warning: NO SPEED SELECTED!");
                    accel = 0.25f;
            }
            
            

            int maxTrackIndex = _manager.getMaxTrackValIndex();
            if(maxTrackIndex != -1)
                steer = _manager.getClassifier().getSensorAngle(maxTrackIndex) * -1.0f;
            else
                steer = 0.0f;

            
            
            if(StateManager.DEBUG) System.out.print(", ST: " + myF.format(steer));
            if(StateManager.DEBUG) System.out.println(", AC: " + myF.format(accel));
            
            //if(_end_prepare_distance < distRaced)
            if(_end_prepare_distance < distRaced) 
            {
                //END PREPARING!!
                if(StateManager.DEBUG) System.out.println("WE ARE OUT!");
                _manager.setState(StateConstants.TAKE_TURN);
            }
         
        }else
        {
            if(StateManager.DEBUG) System.out.println("POSITION UNDEFINED ??");
        }
                
        _manager.getAction().evol_steering = (float)steer;
        _manager.getAction().evol_acceleration = (float)accel;
        
        
//        System.out.println("CLASE: " + currentClass  + 
//                " (last: " + _manager.getClassifier().getLastClass() + ")");
        
    }
    public void OnExit()
    {
        
    }
    
}
