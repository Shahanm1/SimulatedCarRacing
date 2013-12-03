/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package champ2009client;

import champ2009client.automata.StateConstants;
import champ2009client.automata.StateManager;
import champ2009client.fuzzy.FuzzySystem;
import champ2009client.classifier.ClassifierSystem;
import champ2009client.evolution.EvolutionConstants;
import champ2009client.evolution.EvolutionManager;
import champ2009client.evolution.GeneticCapsule;
import champ2009client.evolution.Individual;

/**
 * Puts all managers together and allows the controller to run
 * @author Diego
 */
public class DriverManager {

    private StateManager _automata;
    private EvolutionManager _evolution;
    private FuzzySystem _fuzzy;
    private ClassifierSystem _classifier;
    private DiegoFMOClient _client;
    private double _lastTimer; //in seconds
    private double _deltaTime; //in seconds
    private boolean _realRace;
    
    private int _ticCounter;
    
    //Fitness data
    private Individual  _currentInd;
    private double      _distRaced;
    private double      _damage;
    private double      _damageGap;
    private int         _badTics;
    private int         _racePos;
    private boolean     _damageFlag;
    private final int   MAX_DAMAGE_GAP = 1000;

    //Combi configs
    private boolean     _isCombi; 
    private int         _currentRunner;
    
    //Initialization of the whole controller
    public DriverManager(Controller car, DiegoFMOClient client) {
        _client = client;
        _damageFlag = false;
        _isCombi = false;
        _realRace = false;
        _ticCounter = 0;
        _currentRunner = ClientConstants.NORMAL_INDIVIDUAL;
        _evolution = new EvolutionManager(this);
        _fuzzy = new FuzzySystem();
        _classifier = new ClassifierSystem();
        _automata = new StateManager(_classifier, _fuzzy, this);

        _fuzzy.setAutomata(_automata);
        _classifier.setAutomata(_automata);

        java.util.Date now = new java.util.Date();
        long miliseconds = now.getTime();
        _lastTimer = (double) miliseconds / 1000.0f;
        _deltaTime = 0.0f;
        _distRaced=0;
        _badTics = 0;
        _damage = 0;
        _damageGap = 0;
        _racePos = 0;
        _currentInd = null;
    }

    //Timer
    private void updateTimer() {
        java.util.Date now = new java.util.Date();
        long miliseconds = now.getTime();
        double nextTimer = (double) miliseconds / 1000.0f;

        _deltaTime = nextTimer - _lastTimer;
        _lastTimer = nextTimer;
        
        //System.out.println("DELTA TIME antes: " + _deltaTime);
        _deltaTime = 0.016f;
    }

    //Updates the controller state
    public void update(SensorModel sensors) {

        //Update automata
        updateTimer();
        
        //Classify situation
        _classifier.classify(sensors);
        
        //Update fuzzy system
        _fuzzy.update(sensors, _classifier);
        _automata.update(sensors, _deltaTime);        
        
        _distRaced = sensors.getDistanceRaced();

	  //If this is a real race (not evaluating), that is all...
        if(_realRace) return;
        
	  //If not, we need some fitness values.

        //Update some fitness data
        int state = _automata.getCurrentState();
        if(state == StateConstants.BACK_TO_TRACK || state == StateConstants.EMERGENCY || state == StateConstants.RECOVER)
        {
            _badTics++;
        }

        _damage = sensors.getDamage();
        _racePos = sensors.getRacePosition();
        
        if(_isCombi)
        {
            int suggestedRunner = ClientConstants.NORMAL_INDIVIDUAL;
            if(_racePos == 0 || _damage > 7500)
            {
                suggestedRunner = ClientConstants.SECURE_INDIVIDUAL;
            }
                
            if(_currentRunner != suggestedRunner)
            {
                _currentRunner = suggestedRunner;
                _evolution.changeRunner(_currentRunner);
            }            
        }
        
    }

    //Re-inits the controller parameters for a new evaluation.
    private void prepareForRace(Individual ind) 
    {
        _distRaced = 0.0f;
        _badTics = 0;
        _damage = 0;
        _damageGap = 0;
        _racePos = 0;
        _currentInd = ind;
        _damageFlag = false;
        _automata.getAction().restartRace = false;
        
        individualToCar(ind);
    }

    //Evaluates an individual
    public void evaluate(Individual ind, int port) 
    {
        prepareForRace(ind);
        _client.evaluate(port);
    }

    //Starts a race
    public void race(Individual ind) 
    {
        _realRace = true;
        prepareForRace(ind);
        _client.go();
    }
    
    //Sets a fitness value for the individual that is being evaluated.
    public void setFitness()
    {
        double points = 0;
        double ban = _badTics + _damage;
        _currentInd.setFitness(EvolutionConstants.FITNESS_DIST_RACED, (float)_distRaced);
        _currentInd.setFitness(EvolutionConstants.FITNESS_ACCIDENTS, (float) ban);
    
        if(!_damageFlag && !_realRace)
        {
            if(_racePos-1 >= EvolutionConstants.POINTS.length) points = 0;
            else points = EvolutionConstants.POINTS[_racePos - 1];
        }
        _currentInd.setFitness(EvolutionConstants.FITNESS_POINTS, (float)points);
        
        if(EvolutionManager.DEBUG)
        {
            System.out.println("SETTING FITNESS " + EvolutionConstants.FITNESS_DIST_RACED + ": " + _distRaced);
            System.out.println("SETTING FITNESS " + EvolutionConstants.FITNESS_ACCIDENTS + ": " + ban + 
                                " (" + _badTics + ", " + _damage + ")");
            System.out.println("SETTING FITNESS " + EvolutionConstants.FITNESS_POINTS + ": " + points + 
                                " ( RACE POSITION: " + _racePos + ")");
        }
        
    }
    
    // Starts the genetic algorithm    
    public void startGA()
    {
        if(_evolution.getParams().getPhase() == 0) //NSGA2
            _evolution.runNSGA2();
        else
            _evolution.runGA();
        
    }

    public void createBaseFile()
    {
        if(_evolution.getParams().getPhase() == 0) //NSGA2
        {
            Individual base = baseCarToIndividual();
            _evolution.createPopFromBaseNSGA2(base);
            _evolution.printPopulation(champ2009client.evolution.FileHandler.POP_BASE_NSGA2_FILE);
            _evolution.printPareto();        
        }else
        {   
            _evolution.createPopFromBaseGA(false);
        }
    }
    
    public void createGridFiles()
    {
            _evolution.createPopFromBaseGA(true);
    }    
    
    //Runs the evolutionary algorithm
    public void run()
    {
        _isCombi = false;                
        _evolution.runRace();
    }
    
    public void runCombi()
    {
        _isCombi = true;
        _currentRunner = ClientConstants.NORMAL_INDIVIDUAL;
        _evolution.runRace();
    }
    
    //Takes an evolvable individual and sets its parameter to this controller
    public void individualToCar(Individual ind)
    {
        GeneticCapsule[] caps = ind.getCapsules();
        for(int i = 0; i < caps.length; i++)
        {
            if(caps[i] != null)
            {
                String name = caps[i].getName();
                if(name.equals("FuzzySystem"))
                {
                    _fuzzy.setGeneticInfo(caps[i]);
                }else
                {
                    _automata.setGeneticInfo(caps[i]);
                }
            }
        }    
    }
    
    //Converts a base controller in a individual that can be evolved by the evolutionary algorithms    
    private Individual baseCarToIndividual()
    {
        GeneticCapsule[] fuzzyCaps = _fuzzy.getBaseGeneticInfo();
        GeneticCapsule[] stateCaps = _automata.getBaseGeneticInfo();
    
        GeneticCapsule[] indiCaps = new GeneticCapsule[EvolutionConstants.NUM_CAPSULES];
        for(int i = 0; i < fuzzyCaps.length; i++)
        {
            indiCaps[i] = fuzzyCaps[i];
        }
    
        for(int i = 0, j = fuzzyCaps.length; i < stateCaps.length; i++, j++)
        {
            indiCaps[j] = stateCaps[i];
        }
        
        Individual base = new Individual(_evolution.getPop());
        base.setCapsules(indiCaps);
        return base;
    }
    
    //Converts this controller in a individual that can be evolved by the evolutionary algorithms
    private Individual carToIndividual()
    {
        GeneticCapsule[] fuzzyCaps = _fuzzy.getGeneticInfo();
        GeneticCapsule[] stateCaps = _automata.getGeneticInfo();
    
        GeneticCapsule[] indiCaps = new GeneticCapsule[EvolutionConstants.NUM_CAPSULES];
        for(int i = 0; i < fuzzyCaps.length; i++)
        {
            indiCaps[i] = fuzzyCaps[i];
        }
    
        for(int i = 0, j = fuzzyCaps.length; i < stateCaps.length; i++, j++)
        {
            indiCaps[j] = fuzzyCaps[i];
        }
        
        Individual individual = new Individual(_evolution.getPop());
        individual.setCapsules(indiCaps);
        return individual;
    }
    

    // Some getter & setters
    /////////////////////////////////
    public void setDamageFlag(boolean damageFlag) {
        _damageFlag = damageFlag;
    }
    
    public EvolutionAction getAction() {
        return _automata.getAction();
    }

    public double getDistRaced() {
        return _distRaced;
    }

    public Individual getCurrentIndividual() {
        return _currentInd;
    }
    
}
