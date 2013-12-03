/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package champ2009client.evolution;

import champ2009client.ClientConstants;
import champ2009client.DiegoFMOClient;
import champ2009client.DriverManager;

/**
 *
 * @author Diego
 */
public class EvolutionManager {

	//Manager for Evolutionary Algorithms

    private FileHandler _inout;
    private Population  _pop;
    private GAParams    _params;
    private DriverManager _driver; //This is the car driver
    private int         _currentGen;
    public static final boolean DEBUG = false;
    public static final boolean NSGA2_DEBUG = false;
    
    public EvolutionManager(DriverManager driver)
    {
        _currentGen = 0;
        _inout = new FileHandler(this);
        _driver = driver;
        _params = new GAParams();
        _pop = new Population(_params);
    }
    
    //STEP FOR GA STEADY STATE
    public void newGASteadyStateStep() throws Exception {

        //Select
        if (DEBUG) System.out.println("Selecting...");
        Individual[] parents = _pop.select();    
            
        //Crossover and mutation
        if (DEBUG) System.out.println("Crossover...");    
        Individual offspring = parents[0].crossAndMutate(parents[1], _params);    
        
        //evaluate new individual
        System.out.println("Evaluating GEN: " + _currentGen);
        evaluateGA(offspring);
        if (DEBUG) offspring.printGenome();
        
        //Creck for replacement
        _pop.replacement(offspring);
        
        //FILE Stuff
        if (_currentGen % _params.getFlush() == 0) {
            if (DEBUG) {
                System.out.println("Printing...");
            }
            _inout.printPopulation(champ2009client.evolution.FileHandler.POP_GA_FILE, -1);
            _inout.printGAFitness();
            //_inout.printIndividual((FuzzyManager) pop.getBestIndividual());
        }
    }
    
	//STEP FOR GA 
    public void newGAStep() throws Exception {

        //Elitism: get best individual and prepare it
        Individual ind = _pop.getBestIndividual();
        
        //FILE Stuff
        if (_currentGen % _params.getFlush() == 0) {
            if (DEBUG) {
                System.out.println("Printing...");
            }
            _inout.printPopulation(champ2009client.evolution.FileHandler.POP_GA_FILE, -1);
            _inout.printGAFitness();
        }
    }
    
    //STEP FOR NSGA2 STEADY STATE
    public void newNSGA2SteadyStateStep() throws Exception {
        
        //Select
        if (DEBUG) System.out.println("Selecting...");
        Individual[] parents = _pop.selectDominance();    
            
        //Crossover and mutation
        if (DEBUG) System.out.println("Crossover...");    
        Individual offspring = parents[0].crossAndMutate(parents[1], _params);    
        
        //evaluate new individual
        System.out.println("Evaluating GEN: " + _currentGen);
        evaluateNSGA2(offspring);
        if (DEBUG) offspring.printGenome();
        
        //Pareto fronting and replacement
        _pop.paretoFronts(offspring);
        
        //FILE Stuff
        if (_currentGen % _params.getFlush() == 0) {
            if (DEBUG) {
                System.out.println("Printing...");
            }
            _inout.printPopulation(champ2009client.evolution.FileHandler.POP_NSGA2_FILE, -1);
            _inout.printNSGA2Fitness();
            _inout.printBestsNSGA2(champ2009client.evolution.FileHandler.BESTS_NSGA2_FILE);
        }
    }
    
	//Starts NSGA2
    public void runNSGA2()
    {
		//Rerads population from a file.
        _inout.readPopulation(champ2009client.evolution.FileHandler.POP_NSGA2_FILE, -1);
        if(DEBUG) System.out.println("POP READ for NSGA2");
        try{
			//Steps through generations
            for(int i = _currentGen; i < _params.getNumGenerations(); i++)
            {
                _currentGen = i;
                newNSGA2SteadyStateStep(); 
            }
        }catch(Exception e)
        {
            e.printStackTrace();
        }
    }
        
	//Starts GA
    public void runGA()
    {
        _inout.readPopulation(champ2009client.evolution.FileHandler.POP_GA_FILE, -1);
        if(DEBUG) System.out.println("POP READ");
        try{     
			//Steps through generations
            for(int i = _currentGen; i < _params.getNumGenerations(); i++)
            {
                _currentGen = i;
                //newGASteadyStateStep();
                newGASteadyStateStep();
            }
        }catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    
	//Creates a population taking a base individual (replicating and mutating from it) for NSGA2
    public void createPopFromBaseNSGA2(Individual base)
    {
        //_pop.set(base, 0);
        System.out.println("[NSGA2] Evaluating base individual...");
        evaluateNSGA2(base);
            
        for(int i = 0; i < _params.getPopulationSize(); i++)
        {
            Individual mutatedOne = base.getMutation(_params);
            _pop.set(mutatedOne, i);   
            System.out.println("[NSGA2] Evaluating mutation " + i + " of base individual...");
			//We need to evaluate it to know its fitness
            evaluateNSGA2(mutatedOne);
        }
        
        if(EvolutionManager.NSGA2_DEBUG)
        {
            for(int i = 0; i < _params.getPopulationSize(); i++)
            {
                Individual ind = _pop.get(i);
                System.out.println("[NSGA2] Ind: " + i + ", fit0: " + ind.getFitness(0) + ", fit1: " + ind.getFitness(1));
            }
            System.out.println("[NSGA2] Ind base, fit0: " + base.getFitness(0) + ", fit1: " + base.getFitness(1));

            System.out.println("[NSGA2] Extracting pareto fronts");
        }
        
		//In nsga2, we need to extract pareto fronts from the population.
        _pop.paretoFronts(base);
    }

	//Evaluates an individual (for NSGA2)
    public void evaluateNSGA2   (Individual ind)    { 
        try {
            int numServers = _params.getNumServers();
            double fitness[][] = new double[numServers][2];
            double fitness_acum[] = new double[2];
            System.out.println(" [Evaluating on " + numServers + " servers]");
            double acum = 0.0f;
            StringBuffer sb = new StringBuffer();
            
            for(int i = 1; i <= numServers; i++){
                int portNumber = _params.getStartServer() + i;
                
				//Evaluates the driver.
                _driver.evaluate(ind, portNumber); 
                
                //Fitness on this server
                fitness[i-1][0] = ind.getFitness(0);
                fitness[i-1][1] = ind.getFitness(1);
                fitness_acum[0] += ind.getFitness(0);
                fitness_acum[1] += ind.getFitness(1);
                
                sb.append("[" + fitness[i-1][0] + "," + fitness[i-1][1] + "]");
                System.out.println("  Fitness on " + portNumber + ": [" + 
                        (float)fitness[i-1][0] + "," + (float)fitness[i-1][1] + "]");
            }
            
			//Set obtained fitness
            ind.setFitness(0,(float)fitness_acum[0]);
            ind.setFitness(1,(float)fitness_acum[1]);
                    
            sb.append("[" + fitness_acum[0] + "," + fitness_acum[1] + "]");
            System.out.println("  FINAL FITNESS: " + (float)fitness_acum[0] + "," + (float)fitness_acum[1]);
            
        } catch (Exception e) {
            System.out.println("EXCEPTION PROCESSING INDIVIDUAL: " + e.toString());
            e.printStackTrace();
        }   
    }
    
	//Creates a population taking a base individual (replicating and mutating from it) for NSGA2
    public void createPopFromBaseGA(boolean splitInFiles)
    {
        //Read population from Starting grid file
        Individual[] elite = _inout.readBestsNSGA2(champ2009client.evolution.FileHandler.POP_NSGA2_FILE, DiegoFMOClient.getNumCars());
        
        //Prepare and run the race to get the initial fitness
        for(int i = 0; i < elite.length; i++)    
        {
            _pop.set(elite[i], i);
            //evaluateGA(elite[i]);
        }
        
        //And write new pop
        _inout.printPopulation(champ2009client.evolution.FileHandler.POP_BASE_GA_FILE, elite.length);
        _inout.printPopulationGrid(champ2009client.evolution.FileHandler.POP_GRID_FILE, elite.length);
    }
    
    public void runRace()
    {
        //read individual (object name is only to encourage him :P)
        Individual bestPilotEver = _inout.readIndividual(champ2009client.evolution.FileHandler.INDIVIDUAL_FILE);
        _driver.race(bestPilotEver); 
    }
    
	//If we would use 2 individual configurations (we don't now), this is for swapping between them.
    public void changeRunner(int individualFile)
    {
        //read individual (object name is only to encourage him :P)
        Individual bestPilotEver = null;
        
        switch(individualFile)
        {
            case ClientConstants.NORMAL_INDIVIDUAL:
                bestPilotEver = _inout.readIndividual(champ2009client.evolution.FileHandler.INDIVIDUAL_FILE);
                break;
                
            case ClientConstants.SECURE_INDIVIDUAL:
                bestPilotEver = _inout.readIndividual(champ2009client.evolution.FileHandler.INDIVIDUAL_FILE_SECURE);
                break;
        }
        
        _driver.individualToCar(bestPilotEver);
        
    }
    
    //Evaluates an individual (for GA)
    public void evaluateGA   (Individual ind)    { 
        try {
            int numServers = _params.getNumServers();
            int fitness[] = new int[numServers];
            int fitness_acum = 0;
            System.out.println(" [Evaluating on " + numServers + " servers]");
            
            //For each server
            for(int i = 1; i <= numServers; i++){
                int portNumber = _params.getStartServer() + i;
                
                //Launch the car in this port
                _driver.evaluate(ind, portNumber); 

                System.out.println("  Fitness on " + portNumber + ": " + fitness[i-1]);
                fitness_acum += fitness[i-1];
            }
            
            //Set final fitness 
            ind.setFitness(EvolutionConstants.FITNESS_POINTS,fitness_acum);
            System.out.println("  FINAL FITNESS: " + fitness_acum);
            
        } catch (Exception e) {
            System.out.println("EXCEPTION PROCESSING INDIVIDUAL: " + e.toString());
            e.printStackTrace();
        }   
    }
    
	//Evaluates an individual
    public void evaluate        (Individual ind)
    {
        int portNumber = _params.getStartServer() + 1;
        _driver.evaluate(ind, portNumber); 
    }
    
    
	//Some getters & setters    
    public int getCurrentGen    ()                  { return _currentGen; }
    public void setCurrentGen   (int gen)           { _currentGen = gen; }
    public GAParams getParams   ()                  { return _params; }
    public Population getPop    ()                  { return _pop; }    
    public void printPopulation (String fileName)   { _inout.printPopulation(fileName, -1); }
    public void printPareto     ()                  { _inout.printNSGA2Fitness(); }


	public void testsNSGA2()
	{
		//TEST for print pareto front 0 individuals
		//_inout.readPopulation(champ2009client.evolution.FileHandler.POP_NSGA2_FILE);
		//_pop.paretoFronts(_pop.get(0));
		//_inout.printBestsNSGA2(champ2009client.evolution.FileHandler.BESTS_NSGA2_FILE);
        
		//TEST for read N individuals from a file with individuals from front 0
		//Individual[] elite = _inout.readBestsNSGA2(champ2009client.evolution.FileHandler.BESTS_NSGA2_FILE, 4);
		//for(int i = 0; i < 3; i++)
		//{
		//    elite[i].printGenome();
		//    System.out.println("##################################################");
		//}
	}

}

