/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package champ2009client.fuzzy;

import champ2009client.SensorModel;
import champ2009client.automata.StateManager;
import champ2009client.classifier.ClassifierSystem;
import champ2009client.evolution.EvolutionConstants;
import champ2009client.evolution.GeneticCapsule;
import java.text.DecimalFormat;
import java.util.Vector;

/**
 * Manages the fuzzy system for the controller
 * @author Diego
 */
public class FuzzySystem {

    private FuzzyManager _manager;

    /* 
     * Truth value for all propositions
     */
    private double[] _fuzzyState;

    private int[] _fuzzySensors;
    
    //Reference to the state machine.
    private StateManager        _automata;

    
    public FuzzySystem() {
        _manager = new FuzzyManager();

	  //State for each fuzzy set.
        _fuzzyState = new double[FuzzyConstants.NUM_FUZZY_SETS];
        _fuzzySensors = new int[FuzzyConstants.FUZZY_SENSORS.length];
    }

    public void setAutomata(StateManager automata) {
        _automata = automata;
    }
    
    public void createFromFile() {
        _manager.createSetsFromFile();
    }

    // KEEP AN EYE: fm MUST BE initialized
    public void setIndividual(FuzzyManager fm) {
        _manager = fm;
    }

    public double getTruth(int fuzzySetCode) {
        return _fuzzyState[fuzzySetCode];
    }

    public int getFuzzySensorState(int fuzzySensorCode)
    {
        return _fuzzySensors[fuzzySensorCode];
    }
    
    //This function updates the state of each fuzzy set	
    public void update(SensorModel sensors, ClassifierSystem c) {

        DecimalFormat myF = new DecimalFormat("0.00");
        double trackSensors[] = c.getTrackSensors();
        
        double max = -1;
        int which = -1;
        
        double opponents[] = sensors.getOpponentSensors();
        double tracks[] = sensors.getTrackEdgeSensors(); 
        
        //keep the ones with cars NEAR
        Vector<Integer> vProx = new Vector<Integer>(); 
        
        //Distance to compare: (CARE: THIS IS SPEED IN LAST CYCLE! -> not to disorder below :P)
        double distance = FuzzyConstants.DISTANCE_FOR_OPPONENT; 
        if(_fuzzySensors[FuzzyConstants.INDEX_SPEED_SENSOR] == FuzzyConstants.INDEX_SPEED_MEDIUM)
            distance *= 2;
        else if(_fuzzySensors[FuzzyConstants.INDEX_SPEED_SENSOR] == FuzzyConstants.INDEX_SPEED_FAST)
            distance *= 3;
        else if(_fuzzySensors[FuzzyConstants.INDEX_SPEED_SENSOR] == FuzzyConstants.INDEX_SPEED_VERY_FAST)
            distance *= 4;
                    
        for(int i = 0; i < opponents.length; i++)
        {
            if(opponents[i] < distance) vProx.add(new Integer(i));
        }
                
        
        //FOR EVERY TRACK ANGLE:
        for(int i = 0; i < tracks.length; i++)
        {           
            //This is the absolute angle in relation with the track...
             double virtualAngle = sensors.getAngleToTrackAxis() + c.getSensorAngle(i);
            
            //...used to assure that the better angle is not looking backwards!
            // Furthermore, we are not interested in worst angles than the better we have until now.
            if((virtualAngle < (Math.PI/2.0f)) && (tracks[i] > max))
            {
                //This is a good angle... candidate for being the best.
                double distanceOnThisAngle = tracks[i];
                
                //But a checking is needed in order this angle not to be the same where there is a car. 
                for(int j = 0; j < vProx.size(); j++)
                {
                    //Opps... there is another car in the way of my candidate angle
                    if( Math.abs( c.getOpponentAngle( vProx.get(j) ) - c.getSensorAngle(i) ) < FuzzyConstants.ANGLE_ADJUSTMENT )
                    {
                        //so the value to assing is really the distance to this car (or 0), NOT to the track
                       distanceOnThisAngle = 0; 
                    }
                }
                
                  //Finally, check if new distance is better than the previous best one.
                if(distanceOnThisAngle > max)
                {
                    max = distanceOnThisAngle;
                    which = i;
                }
            }
        }
        
        if(which != -1)
        {
            _automata.setMaxTrackValIndex(which);
        }
        _automata.setMaxTrackValue(max);
        _automata.setHardnessTurnValue(max);
        
        /* ANGLE */
        double sensorAbs = Math.abs(sensors.getAngleToTrackAxis());
        double max_value = -1.0f;
        int better_rule = -1;
        
        String prop = FuzzyConstants.FUZZY_RELATIONS[FuzzyConstants.INDEX_ANGLE_BACKWARDS][0];
        String set = FuzzyConstants.FUZZY_RELATIONS[FuzzyConstants.INDEX_ANGLE_BACKWARDS][1];
        _fuzzyState[FuzzyConstants.INDEX_ANGLE_BACKWARDS] = _manager.getTruthValue(prop, set, sensorAbs);
        if(_fuzzyState[FuzzyConstants.INDEX_ANGLE_BACKWARDS] > max_value)
        {
            better_rule = FuzzyConstants.INDEX_ANGLE_BACKWARDS;
            max_value = _fuzzyState[FuzzyConstants.INDEX_ANGLE_BACKWARDS];
        }
        
        prop = FuzzyConstants.FUZZY_RELATIONS[FuzzyConstants.INDEX_ANGLE_ORIENTED_HARD][0];
        set = FuzzyConstants.FUZZY_RELATIONS[FuzzyConstants.INDEX_ANGLE_ORIENTED_HARD][1];
        _fuzzyState[FuzzyConstants.INDEX_ANGLE_ORIENTED_HARD] = _manager.getTruthValue(prop, set, sensorAbs);
        if(_fuzzyState[FuzzyConstants.INDEX_ANGLE_ORIENTED_HARD] > max_value)
        {
            better_rule = FuzzyConstants.INDEX_ANGLE_ORIENTED_HARD;
            max_value = _fuzzyState[FuzzyConstants.INDEX_ANGLE_ORIENTED_HARD];
        }
        
        prop = FuzzyConstants.FUZZY_RELATIONS[FuzzyConstants.INDEX_ANGLE_ORIENTED_SOFT][0];
        set = FuzzyConstants.FUZZY_RELATIONS[FuzzyConstants.INDEX_ANGLE_ORIENTED_SOFT][1];
        _fuzzyState[FuzzyConstants.INDEX_ANGLE_ORIENTED_SOFT] = _manager.getTruthValue(prop, set, sensorAbs);
        if(_fuzzyState[FuzzyConstants.INDEX_ANGLE_ORIENTED_SOFT] > max_value)
        {
            better_rule = FuzzyConstants.INDEX_ANGLE_ORIENTED_SOFT;
            max_value = _fuzzyState[FuzzyConstants.INDEX_ANGLE_ORIENTED_SOFT];
        }
        
         
        prop = FuzzyConstants.FUZZY_RELATIONS[FuzzyConstants.INDEX_ANGLE_CENTERED][0];
        set = FuzzyConstants.FUZZY_RELATIONS[FuzzyConstants.INDEX_ANGLE_CENTERED][1];
        _fuzzyState[FuzzyConstants.INDEX_ANGLE_CENTERED] = _manager.getTruthValue(prop, set, sensorAbs);
        if(_fuzzyState[FuzzyConstants.INDEX_ANGLE_CENTERED] > max_value)
        { 
            better_rule = FuzzyConstants.INDEX_ANGLE_CENTERED;
            max_value = _fuzzyState[FuzzyConstants.INDEX_ANGLE_CENTERED];
        }
        
        if(max_value < 0.01f) better_rule = -1;
        _fuzzySensors[FuzzyConstants.INDEX_ANGLE_SENSOR] = better_rule;
        
        /* TRACK POS */
        max_value = -1.0f;
        better_rule = -1;
        sensorAbs = Math.abs(sensors.getTrackPosition());
        
        prop = FuzzyConstants.FUZZY_RELATIONS[FuzzyConstants.INDEX_TRACKPOS_OUTSIDE][0];
        set = FuzzyConstants.FUZZY_RELATIONS[FuzzyConstants.INDEX_TRACKPOS_OUTSIDE][1];
        _fuzzyState[FuzzyConstants.INDEX_TRACKPOS_OUTSIDE] = _manager.getTruthValue(prop, set, sensorAbs);
        if(_fuzzyState[FuzzyConstants.INDEX_TRACKPOS_OUTSIDE] > max_value)
        {
            better_rule = FuzzyConstants.INDEX_TRACKPOS_OUTSIDE;
            max_value = _fuzzyState[FuzzyConstants.INDEX_TRACKPOS_OUTSIDE];
        }
        
        prop = FuzzyConstants.FUZZY_RELATIONS[FuzzyConstants.INDEX_TRACKPOS_SIDE][0];
        set = FuzzyConstants.FUZZY_RELATIONS[FuzzyConstants.INDEX_TRACKPOS_SIDE][1];
        _fuzzyState[FuzzyConstants.INDEX_TRACKPOS_SIDE] = _manager.getTruthValue(prop, set, sensorAbs);
        if(_fuzzyState[FuzzyConstants.INDEX_TRACKPOS_SIDE] > max_value)
        {
            better_rule = FuzzyConstants.INDEX_TRACKPOS_SIDE;
            max_value = _fuzzyState[FuzzyConstants.INDEX_TRACKPOS_SIDE];
        }
        
        prop = FuzzyConstants.FUZZY_RELATIONS[FuzzyConstants.INDEX_TRACKPOS_CENTERED][0];
        set = FuzzyConstants.FUZZY_RELATIONS[FuzzyConstants.INDEX_TRACKPOS_CENTERED][1];
        _fuzzyState[FuzzyConstants.INDEX_TRACKPOS_CENTERED] = _manager.getTruthValue(prop, set, sensorAbs);
        if(_fuzzyState[FuzzyConstants.INDEX_TRACKPOS_CENTERED] > max_value)
        {
            better_rule = FuzzyConstants.INDEX_TRACKPOS_CENTERED;
            max_value = _fuzzyState[FuzzyConstants.INDEX_TRACKPOS_CENTERED];
        }
        if(max_value < 0.01f) better_rule = -1;
        _fuzzySensors[FuzzyConstants.INDEX_TRACKPOS_SENSOR] = better_rule;
        
        /* SPEED */
        max_value = -1.0f;
        better_rule = -1;
        
        prop = FuzzyConstants.FUZZY_RELATIONS[FuzzyConstants.INDEX_SPEED_SLOW][0];
        set = FuzzyConstants.FUZZY_RELATIONS[FuzzyConstants.INDEX_SPEED_SLOW][1];
        _fuzzyState[FuzzyConstants.INDEX_SPEED_SLOW] = _manager.getTruthValue(prop, set, sensors.getSpeed());
        if(_fuzzyState[FuzzyConstants.INDEX_SPEED_SLOW] > max_value)
        {
            better_rule = FuzzyConstants.INDEX_SPEED_SLOW;
            max_value = _fuzzyState[FuzzyConstants.INDEX_SPEED_SLOW];
        }
        
        
        prop = FuzzyConstants.FUZZY_RELATIONS[FuzzyConstants.INDEX_SPEED_MEDIUM][0];
        set = FuzzyConstants.FUZZY_RELATIONS[FuzzyConstants.INDEX_SPEED_MEDIUM][1];
        _fuzzyState[FuzzyConstants.INDEX_SPEED_MEDIUM] = _manager.getTruthValue(prop, set, sensors.getSpeed());
        if(_fuzzyState[FuzzyConstants.INDEX_SPEED_MEDIUM] > max_value)
        {
            better_rule = FuzzyConstants.INDEX_SPEED_MEDIUM;
            max_value = _fuzzyState[FuzzyConstants.INDEX_SPEED_MEDIUM];
        }
        
        prop = FuzzyConstants.FUZZY_RELATIONS[FuzzyConstants.INDEX_SPEED_FAST][0];
        set = FuzzyConstants.FUZZY_RELATIONS[FuzzyConstants.INDEX_SPEED_FAST][1];
        _fuzzyState[FuzzyConstants.INDEX_SPEED_FAST] = _manager.getTruthValue(prop, set, sensors.getSpeed());
        if(_fuzzyState[FuzzyConstants.INDEX_SPEED_FAST] > max_value)
        {
            better_rule = FuzzyConstants.INDEX_SPEED_FAST;
            max_value = _fuzzyState[FuzzyConstants.INDEX_SPEED_FAST];
        }
        
        prop = FuzzyConstants.FUZZY_RELATIONS[FuzzyConstants.INDEX_SPEED_VERY_FAST][0];
        set = FuzzyConstants.FUZZY_RELATIONS[FuzzyConstants.INDEX_SPEED_VERY_FAST][1];
        _fuzzyState[FuzzyConstants.INDEX_SPEED_VERY_FAST] = _manager.getTruthValue(prop, set, sensors.getSpeed());
        if(_fuzzyState[FuzzyConstants.INDEX_SPEED_VERY_FAST] > max_value)
        {
            better_rule = FuzzyConstants.INDEX_SPEED_VERY_FAST;
            max_value = _fuzzyState[FuzzyConstants.INDEX_SPEED_VERY_FAST];
        }
        
        if(max_value < 0.01f) better_rule = -1;
        _fuzzySensors[FuzzyConstants.INDEX_SPEED_SENSOR] = better_rule;
        
        
        /* TRACK SENSORS */
        max_value = -1.0f;
        better_rule = -1;
                
        prop = FuzzyConstants.FUZZY_RELATIONS[FuzzyConstants.INDEX_TRACK_TURN_SOFT][0];
        set = FuzzyConstants.FUZZY_RELATIONS[FuzzyConstants.INDEX_TRACK_TURN_SOFT][1];
        _fuzzyState[FuzzyConstants.INDEX_TRACK_TURN_SOFT] = _manager.getTruthValue(prop, set, max);
        if(_fuzzyState[FuzzyConstants.INDEX_TRACK_TURN_SOFT] > max_value)
        {
            better_rule = FuzzyConstants.INDEX_TRACK_TURN_SOFT;
            max_value = _fuzzyState[FuzzyConstants.INDEX_TRACK_TURN_SOFT];
        } 
        
        prop = FuzzyConstants.FUZZY_RELATIONS[FuzzyConstants.INDEX_TRACK_TURN_MID][0];
        set = FuzzyConstants.FUZZY_RELATIONS[FuzzyConstants.INDEX_TRACK_TURN_MID][1];
        _fuzzyState[FuzzyConstants.INDEX_TRACK_TURN_MID] = _manager.getTruthValue(prop, set, max);
        if(_fuzzyState[FuzzyConstants.INDEX_TRACK_TURN_MID] > max_value)
        {
            better_rule = FuzzyConstants.INDEX_TRACK_TURN_MID;
            max_value = _fuzzyState[FuzzyConstants.INDEX_TRACK_TURN_MID];
        }
                        
        prop = FuzzyConstants.FUZZY_RELATIONS[FuzzyConstants.INDEX_TRACK_TURN_HARD][0];
        set = FuzzyConstants.FUZZY_RELATIONS[FuzzyConstants.INDEX_TRACK_TURN_HARD][1];
        _fuzzyState[FuzzyConstants.INDEX_TRACK_TURN_HARD] = _manager.getTruthValue(prop, set, max);
        if(_fuzzyState[FuzzyConstants.INDEX_TRACK_TURN_HARD] > max_value)
        {
            better_rule = FuzzyConstants.INDEX_TRACK_TURN_HARD;
            max_value = _fuzzyState[FuzzyConstants.INDEX_TRACK_TURN_HARD];
        }
        
        if(max_value < 0.01f) better_rule = -1;
        _fuzzySensors[FuzzyConstants.INDEX_TURN_SENSOR] = better_rule;

        
        /* EDGE SETS */              
        int index = _automata.getMaxTrackValIndex();
        double sensValue = tracks[9];//tracks[index]; //tracks[9];
        max_value = -1.0f;
        better_rule = -1;
        prop = FuzzyConstants.FUZZY_RELATIONS[FuzzyConstants.INDEX_EDGE_VERY_CLOSE][0];
        set = FuzzyConstants.FUZZY_RELATIONS[FuzzyConstants.INDEX_EDGE_VERY_CLOSE][1];
        _fuzzyState[FuzzyConstants.INDEX_EDGE_VERY_CLOSE] = _manager.getTruthValue(prop, set, sensValue);
        if(_fuzzyState[FuzzyConstants.INDEX_EDGE_VERY_CLOSE] > max_value)
        {
            better_rule = FuzzyConstants.INDEX_EDGE_VERY_CLOSE;
            max_value = _fuzzyState[FuzzyConstants.INDEX_EDGE_VERY_CLOSE];
        }
        
        prop = FuzzyConstants.FUZZY_RELATIONS[FuzzyConstants.INDEX_EDGE_CLOSE][0];
        set = FuzzyConstants.FUZZY_RELATIONS[FuzzyConstants.INDEX_EDGE_CLOSE][1];
        _fuzzyState[FuzzyConstants.INDEX_EDGE_CLOSE] = _manager.getTruthValue(prop, set, sensValue);
        if(_fuzzyState[FuzzyConstants.INDEX_EDGE_CLOSE] > max_value)
        {
            better_rule = FuzzyConstants.INDEX_EDGE_CLOSE;
            max_value = _fuzzyState[FuzzyConstants.INDEX_EDGE_CLOSE];
        }
                
        prop = FuzzyConstants.FUZZY_RELATIONS[FuzzyConstants.INDEX_EDGE_FAR][0];
        set = FuzzyConstants.FUZZY_RELATIONS[FuzzyConstants.INDEX_EDGE_FAR][1];
        _fuzzyState[FuzzyConstants.INDEX_EDGE_FAR] = _manager.getTruthValue(prop, set, sensValue);
        if(_fuzzyState[FuzzyConstants.INDEX_EDGE_FAR] > max_value)
        {
            better_rule = FuzzyConstants.INDEX_EDGE_FAR;
            max_value = _fuzzyState[FuzzyConstants.INDEX_EDGE_FAR];
        }
        
        
        if(max_value < 0.01f) better_rule = -1;
        _fuzzySensors[FuzzyConstants.INDEX_EDGE_SENSOR] = better_rule;
    }
    
    public GeneticCapsule[] getGeneticInfo()
    {
        GeneticCapsule[] info = new GeneticCapsule[1];
        GeneticCapsule c;
        int i = 0;
        
        //desired alpha
        c = new GeneticCapsule("FuzzySystem", "_manager");
        c.setData(_manager, EvolutionConstants.TYPE_FUZZY_MANAGER);
        info[i++] = c;        
        
        return info;
    }
    
    public GeneticCapsule[] getBaseGeneticInfo()
    {
        GeneticCapsule[] info = new GeneticCapsule[1];
        FuzzyManager fm = new FuzzyManager();
        GeneticCapsule c;
        int i = 0;
        
        //desired alpha
        c = new GeneticCapsule("FuzzySystem", "_manager");
        c.setData(fm, EvolutionConstants.TYPE_FUZZY_MANAGER);
        info[i++] = c;        
        
        return info;
    }
    
    public void setGeneticInfo(GeneticCapsule info)
    {
        //NOTHING TO DO (to be overloaded)
    }
    
}
