/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package champ2009client.automata;
import champ2009client.DriverManager;
import champ2009client.EvolutionAction;
import champ2009client.SensorModel;
import champ2009client.classifier.ClassifierSystem;
import champ2009client.evolution.EvolutionConstants;
import champ2009client.evolution.GeneticCapsule;
import champ2009client.fuzzy.FuzzySystem;
/**
 *
 * @author Diego
 */
public class StateManager {

    //STATES
    private int _currentState;
    private State[] _states;

    //Action to perform in active state
    private EvolutionAction _action;
	
	//References to other controller managers
    private FuzzySystem     _fs;
    private ClassifierSystem _cs; 
    private DriverManager   _dm;
    private SensorModel _sensors; 

    //Car and fuzzy values to be used by states
    private int     _maxTrackValIndex;
    private double  _maxTrackValue;
    private double  _hardnessTurnValue;

	//For emergencies, variables for being stuck
    private boolean _stuckWarning;
    private double  _timeToStuck;
    private boolean _carStuckWarning;
    private double  _timeToCarStuck;
    
    public static boolean DEBUG = false;
    
    
    public StateManager(ClassifierSystem cs, FuzzySystem fs, DriverManager dm)
    {
        _currentState = -1;
        
        _maxTrackValIndex = -1;
        _maxTrackValue = 0;
        _hardnessTurnValue = 0;
        _stuckWarning = false;
        _timeToStuck = 0.0f;
        _carStuckWarning = false;
        _timeToCarStuck = 0.0f;
        
		//Initialize all states
        _states = new State[StateConstants.NUM_STATES];
        _states[StateConstants.RUN] = new StateRun(this);
        _states[StateConstants.PREPARE_TURN] = new StatePrepareTurn(this);                
        _states[StateConstants.TAKE_TURN] = new StateTakeTurn(this);                
        _states[StateConstants.EMERGENCY] = new StateEmergency(this);
        _states[StateConstants.RECOVER] = new StateRecover(this);
        _states[StateConstants.BACK_TO_TRACK] = new StateBackToTrack(this);
        
		//Default state is always RUN
        setState(StateConstants.RUN);
        _action = new EvolutionAction();
        _fs = fs;
        _cs = cs;
        _dm = dm;
        _sensors = null;
    }
  
	//To be called every cycle
    public void update(SensorModel sensors, double delta)
    {
        //normal state update: current state must execute update() method.
        _sensors = sensors;
        _states[_currentState].OnUpdate(delta);
        
        //update stuck warning (This is for being stuck against a wall)
        if(_action.evol_acceleration > 0.5f && 
           sensors.getSpeed() < StateConstants.MAX_UNSTUCK_SPEED && 
           sensors.getSpeed() > -StateConstants.MAX_UNSTUCK_SPEED && //Also R gear
           _currentState != StateConstants.EMERGENCY && //We are not currently in emergency
           _dm.getDistRaced() > 10.0f)					//To avoid stuck before race starts (on grid formation)
        {
            _timeToStuck += delta;
            if(_timeToStuck >= StateConstants.MAX_UNSTUCK_TIME)
                _stuckWarning = true;
        }else
        {
            _timeToStuck = 0.0f;
            _stuckWarning = false;
        }
        
        
        //Update car stuck warning (This is for being stuck against another car... maybe it is stopped)
        if( _currentState != StateConstants.EMERGENCY &&
            sensors.getOpponentSensors()[18] < StateConstants.MAX_UNSTUCK_CAR_DISTANCE &&
           _dm.getDistRaced() > 10.0f)
        {
            {
                if(sensors.getSpeed() < StateConstants.MAX_UNSTUCK_CAR_SPEED)
                    _action.evol_acceleration = 0.0f;
                else 
                    _action.evol_acceleration = 0.9f;
            }
        }
    }

	//Manages state changes
	public void setState(int state)
	{
		if(_currentState != state) //To not change to the same state as the one is active
		{
            
			if(StateManager.DEBUG) System.out.println("CHANGING FROM " + _currentState + " TO " + state);
			if(_currentState != -1 ) _states[_currentState].OnExit(); //Execute exit() method of old active state
            
			_currentState = state;
			_states[_currentState].OnEnter(); //execute enter() of new active state
		}
	}

    //Collects genetic info (state parameters) from all the states of the FSM
	public GeneticCapsule[] getGeneticInfo()
	{
		GeneticCapsule[] info = new GeneticCapsule[11];
		int k = 0;
        
		for(int i = 0; i < StateConstants.NUM_STATES; i++)
		{
			GeneticCapsule[] subInfo = _states[i].getGeneticInfo();
			if(subInfo != null)
			{
				for(int j = 0; j < subInfo.length; j++)
				{
					info[k] = subInfo[j];
					k++;
				}
			}
		}
        
		return info;
	}
    
	//Retrieve base genetic (DEFAULT state parameters) info from all states of the FSM
	public GeneticCapsule[] getBaseGeneticInfo()
	{
		GeneticCapsule[] info = new GeneticCapsule[11];
		GeneticCapsule[] subInfo;
		int k = 0;
		State st;
        
		st = new StateRun(this);
		subInfo = st.getGeneticInfo();
		if(subInfo != null) for(int j = 0; j < subInfo.length; j++)
                {
                        info[k++] = subInfo[j];
                }
        
		st = new StatePrepareTurn(this);
		subInfo = st.getGeneticInfo();
		if(subInfo != null) for(int j = 0; j < subInfo.length; j++)
                {
                        info[k++] = subInfo[j];
                }

		st = new StateTakeTurn(this);
		subInfo = st.getGeneticInfo();
		if(subInfo != null) for(int j = 0; j < subInfo.length; j++)
                {
                        info[k++] = subInfo[j];
                }

		st = new StateEmergency(this);
		subInfo = st.getGeneticInfo();
		if(subInfo != null) for(int j = 0; j < subInfo.length; j++)
                {
                        info[k++] = subInfo[j];
                }

		st = new StateRecover(this);
		subInfo = st.getGeneticInfo();
		if(subInfo != null) for(int j = 0; j < subInfo.length; j++)
                {
                        info[k++] = subInfo[j];
                }

		st = new StateBackToTrack(this);
		subInfo = st.getGeneticInfo();
		if(subInfo != null) for(int j = 0; j < subInfo.length; j++)
                {
                        info[k++] = subInfo[j];
                }
       
		return info;
	}
    
    //Assigns genetic info to the parametes of each state in the FSM
	public void setGeneticInfo(GeneticCapsule info)
	{
		String name = info.getName();
        
		if(name.equals("StateBackToTrack"))
		{
			_states[StateConstants.BACK_TO_TRACK].setGeneticInfo(info);
		}
		else if(name.equals("StateEmergency"))
		{
			_states[StateConstants.EMERGENCY].setGeneticInfo(info);
		}
		else if(name.equals("StateRun"))
		{
			_states[StateConstants.RUN].setGeneticInfo(info);
		}
		else if(name.equals("StatePrepareTurn"))
		{
			_states[StateConstants.PREPARE_TURN].setGeneticInfo(info);
		}
		else if(name.equals("StateTakeTurn"))
		{
			_states[StateConstants.TAKE_TURN].setGeneticInfo(info);
		}
	}
    

	// Getters and Setters
	/////////////////////////////////

    public double getTimeToStuck() 
	{
        return _timeToStuck;
    }

    public boolean stuckWarning() {
        return _stuckWarning;
    }

    public void setMaxTrackValIndex(int maxTrackValIndex) 
    {
            _maxTrackValIndex = maxTrackValIndex;
    }

    public void setMaxTrackValue(double maxTrackValue) 
    {
            _maxTrackValue = maxTrackValue;
    }

    public int getMaxTrackValIndex() 
    {
            return _maxTrackValIndex;
    }

    public double getMaxTrackValue() 
    {
            return _maxTrackValue;
    }

    public int getCurrentState() {
        return _currentState;
    }

    public EvolutionAction getAction()
    {
        return _action;
    }

    public FuzzySystem getFuzzySystem() {
        return _fs;
    }

    public ClassifierSystem getClassifier() {
        return _cs;
    }

    public SensorModel getSensors() {
        return _sensors;
    }

    public void setHardnessTurnValue(double val)
    {
        _hardnessTurnValue = val;
    }
    
    public double getHardnessTurnValue()
    {
        return _hardnessTurnValue;
    }        
    
    
}
