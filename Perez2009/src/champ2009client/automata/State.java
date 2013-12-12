/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package champ2009client.automata;

import champ2009client.classifier.ClassifierConstants;
import champ2009client.evolution.GeneticCapsule;
import champ2009client.fuzzy.FuzzyConstants;
import champ2009client.fuzzy.FuzzySet;

/**
 *
 * @author Diego
 */
public abstract class State {

	//Manager of finite state machine
    protected StateManager _manager;
    
	//Functions to be called when this state is the active one
    public abstract void OnEnter();
    public abstract void OnUpdate(double deltaTime);
    public abstract void OnExit();
    
	//Genetic information of the state for the evolutionary algorithm
    public GeneticCapsule[] getGeneticInfo() {  return null;  }
    public void setGeneticInfo(GeneticCapsule info) { }
    public void debugGeneticInfo() {}
    
    //Function to recognize an emergency
    public int manageEmergencies()
    {
        int emergency = -1;
        
		//Consult fuzzy sets to determine de emergency
        boolean look_back  = (_manager.getFuzzySystem().getFuzzySensorState(FuzzyConstants.INDEX_ANGLE_SENSOR) == 
                            FuzzyConstants.INDEX_ANGLE_BACKWARDS );
        boolean outside = (_manager.getFuzzySystem().getFuzzySensorState(FuzzyConstants.INDEX_TRACKPOS_SENSOR) == 
                            FuzzyConstants.INDEX_TRACKPOS_OUTSIDE);

        
        if( _manager.stuckWarning() ){  //Stuck against a wall
            emergency = StateConstants.EMERGENCY;
        }else if( look_back ){		    //I am looking backwards
            emergency = StateConstants.RECOVER;
        }else if( outside ){			//I am outside the track
            emergency = StateConstants.BACK_TO_TRACK;
        }
       
        //Return the emergency if there is one (if not, return -1)
        return emergency;
    }
    
    //Function to be called when emergency is off
    public void recoverState()
    {
		//Depending on classifier info, select the most apropriate state.
        if( _manager.getClassifier().getCurrentClass() == ClassifierConstants.CLASS_STRAIGHT )
            _manager.setState(StateConstants.RUN);
        else if(_manager.getClassifier().getCurrentClass() == ClassifierConstants.CLASS_TURN)
            _manager.setState(StateConstants.TAKE_TURN);
        else 
            _manager.setState(StateConstants.TAKE_TURN);
    }
    
}
