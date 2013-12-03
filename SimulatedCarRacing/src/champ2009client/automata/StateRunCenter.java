/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package champ2009client.automata;

import champ2009client.classifier.ClassifierConstants;
import champ2009client.fuzzy.FuzzyConstants;
import java.text.DecimalFormat;


/**
 *
 * @author Diego
 */
public class StateRunCenter extends State{

            //Constants
            private double _initial_alpha_side;
            private double _initial_alpha_center;

            private double _alpha_side;
            private double _alpha_center;

            public StateRunCenter(StateManager manager)
            {
                _manager = manager;
                _initial_alpha_side = 0.07f;
                _initial_alpha_center = 0.02f;
            }

            private void paletoDriving()
            {
                double speed = _manager.getSensors().getSpeed();
                if(speed > 30)
                {
                    _manager.getAction().evol_acceleration = 0.5f;
                }else
                {
                    _manager.getAction().evol_acceleration = 0.6f;
                }

                double angle = _manager.getSensors().getAngleToTrackAxis();
                if(angle < 0 && angle > -0.1){
                    _manager.getAction().evol_steering = -0.1f;
                }else if(angle < -0.1 && angle > -0.6){
                    _manager.getAction().evol_steering = -0.8f;
                }else if(angle < -0.6){
                    _manager.getAction().evol_steering = -1.0f;
                }

                if(angle > 0 && angle < 0.1){
                    _manager.getAction().evol_steering = 0.1f;
                }else if(angle > 0.1 && angle < 0.6){
                    _manager.getAction().evol_steering = 0.8f;
                }else if(angle > 0.6){
                    _manager.getAction().evol_steering = 1.0f;
                }

                double trackPos = _manager.getSensors().getTrackPosition();
                if(trackPos <= -1)
                {
                    _manager.getAction().evol_steering = 0.8f;
                }
                else if(trackPos > -1 && trackPos < -0.5)
                {
                    _manager.getAction().evol_steering = 0.5f;
                }
                else if(trackPos >= 1)
                {
                    _manager.getAction().evol_steering = -0.8f;      
                }else if(trackPos < 1 && trackPos > 0.5)
                {
                    _manager.getAction().evol_steering = -0.5f;
                }

            }


            public void OnEnter()
            {
                _alpha_side = _initial_alpha_side;
                _alpha_center = _initial_alpha_center;
            }


            public void OnUpdate(double deltaTime)
            {

                if(manageEmergencies() == -1)
                {
                    return;
                }

                //paleto driving. for debug only
                if(false)
                {
                    paletoDriving();
                    return;
                }

                DecimalFormat myF = new DecimalFormat("0.0000f");

                //INPUT 
                //////////

                //CLASSIFIER DATA
                int currentClass = _manager.getClassifier().getCurrentClass();

                //FUZZY SYSTEM DATA
                //int fuzzyAngle      = _manager.getFuzzySystem().getFuzzySensorState(FuzzyConstants.INDEX_ANGLE_SENSOR);
                int fuzzyTrackPos   = _manager.getFuzzySystem().getFuzzySensorState(FuzzyConstants.INDEX_TRACKPOS_SENSOR);
                //int fuzzySpeed      = _manager.getFuzzySystem().getFuzzySensorState(FuzzyConstants.INDEX_SPEED_SENSOR);
                //int fuzzyTurn       = _manager.getFuzzySystem().getFuzzySensorState(FuzzyConstants.INDEX_TURN_SENSOR);

                //RAW sesors data
                boolean trackPosLeft = _manager.getSensors().getTrackPosition() > 0.0f;
                double  angle  = _manager.getSensors().getAngleToTrackAxis();

                double steer = 0.0f;
                double accel = 1.0f;

                //Adjust sense of steering
                //double decrementMult = (trackPosLeft)? -0.0001f : 0.0001f;
                double decrementMult = (trackPosLeft)? -0.001f : 0.001f;
                if(!trackPosLeft && _alpha_side>0) _alpha_side *= -1;
                if(!trackPosLeft && _alpha_center>0)  _alpha_center *= -1;

                switch(currentClass)
                {
                    //RUN
                    case ClassifierConstants.CLASS_TURN:
                        //_manager.setState(StateConstants.TAKE_TURN);
                        //break;

                    //STRAIGHT
                    case ClassifierConstants.CLASS_STRAIGHT:

                        //Run... and center on the track
                        if(fuzzyTrackPos == FuzzyConstants.INDEX_TRACKPOS_SIDE)
                        {
                            //RESET ALPHA CENTER FOR WHEN I GO THERE
                            _alpha_center = _initial_alpha_center;                     
                            if(StateManager.DEBUG) System.out.print("RUN! ALPHA_SIDE = " + myF.format(_alpha_side) + 
                                             ", ANGLE: " + myF.format(angle) + 
                                             ", delta: " + myF.format(deltaTime) +
                                             ", ON_THE_LEFT: " + trackPosLeft                                       
                                               );


                            //Go to the center of the track
                            steer = angle - _alpha_side;
                            _alpha_side += decrementMult * deltaTime;

                            //Control end of steering
                            if(trackPosLeft)
                            {
                                if(_alpha_side <= 0) _alpha_side = 0.0f;
                            }
                            else if(_alpha_side >= 0)
                            {
                                _alpha_side = 0.0f;
                            }


                            int maxTrackIndex = _manager.getMaxTrackValIndex();
                            if(maxTrackIndex != -1)
                                steer = _manager.getClassifier().getSensorAngle(maxTrackIndex) * -1.0f;
                            else
                                steer = 0.0f;


                            //PROBLEMA: A muy bajas velocidades, el coche no gira (aunq con steering altos).
                            // Como disminuyo alpha_side hasta 0 antes de que coja alta velocidad, no gira nunca.
                            // habria q disminuirlo en menos (HACIENDO ESTO)
                            // Otras opciones: ... o NO en cada ciclo... o por distancia...

                            if(StateManager.DEBUG) System.out.print(", ST: " + myF.format(steer));
                            if(StateManager.DEBUG) System.out.println(", AC: " + myF.format(accel));

                        }else if(fuzzyTrackPos == FuzzyConstants.INDEX_TRACKPOS_CENTERED)
                        {
                            //System.out.print("MIN ALPHA_CENTER => " +  myF.format(_alpha_side) + "," + myF.format(_alpha_center));

                            //ABS VALUE of Closer to 0
                            double value = Math.min(Math.abs(_alpha_side), Math.abs(_alpha_center)); 
                            //SIGNUM of Closer to 0
                            _alpha_center = value * ((value == Math.abs(_alpha_side)) ? Math.signum(_alpha_side) : Math.signum(_alpha_center)); 
                            //RESET ALPHA SIDE... FOR WHEN I GO BACK THERE
                            _alpha_side = _initial_alpha_side; 


                            if(StateManager.DEBUG) System.out.print("RUN! ALPHA_CENTER = " + myF.format(_alpha_center) + 
                                             ", ANGLE: " + myF.format(angle) + 
                                             ", TRACKPOS: " + myF.format(_manager.getSensors().getTrackPosition()) + 
                                             ", delta: " + myF.format(deltaTime) +
                                             ", ON_THE_LEFT: " + trackPosLeft                                       
                                               );

                            int maxTrackIndex = _manager.getMaxTrackValIndex();
                            if(maxTrackIndex != -1)
                                steer = _manager.getClassifier().getSensorAngle(maxTrackIndex) * -1.0f;
                            else
                                steer = 0.0f;




                            //PROBLEMA: A muy bajas velocidades, el coche no gira (aunq con steering altos).
                            // Como disminuyo alpha_side hasta 0 antes de que coja alta velocidad, no gira nunca.
                            // habria q disminuirlo en menos (HACIENDO ESTO)
                            // Otras opciones: ... o NO en cada ciclo... o por distancia...

                            if(StateManager.DEBUG) System.out.print(", ST: " + myF.format(steer));
                            if(StateManager.DEBUG) System.out.println(", AC: " + myF.format(accel));


                        }else
                        {
                            //NOT SIDE OR CENTERED. Keep going
                            steer = 0.0f;
                            accel = 1.0f;
                        }



                        break;

                    case ClassifierConstants.CLASS_PRE_TURN:
                        _manager.setState(StateConstants.PREPARE_TURN);
                        break;

                    default:
                        if(StateManager.DEBUG) System.out.println("NOT STRAIGHT NOR PRE_TURN_LEFT!: " + _manager.getClassifier().getCurrentClass());
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
