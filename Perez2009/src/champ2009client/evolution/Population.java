/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package champ2009client.evolution;

import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.Vector;

/**
 *
 * @author Diego
 */
public class Population {

	//Population of individuals.
    private Individual[] _individuals;
    private GAParams _params;
	//Indicates which fitness to use.
    private int _fitnessToUse; 

    //For nsga2
    private static final double MAX_CROWD_DISTANCE = 2000000.0f;
    private int _front_counter[];
    private int _front_array[][];

    public Population(GAParams params) {
        _params = params;
        if (_params.getPhase() == 0) {
            _fitnessToUse = EvolutionConstants.FITNESS_DIST_RACED;
        } else {
            _fitnessToUse = EvolutionConstants.FITNESS_POINTS;
        }

        _individuals = new Individual[_params.getPopulationSize()];
        _front_counter = new int[_params.getPopulationSize() + 1];
        _front_array = new int[_params.getPopulationSize() + 1][_params.getPopulationSize() + 1];
    }

	//Sets an individual in a population with an index
    public void set(Individual ind, int which) {
        if (which >= 0 && which < _individuals.length) {
            _individuals[which] = ind;
        }
    }

	//Gets an individual from population with an index
    public Individual get(int which) {
        if (which >= 0 && which < _individuals.length) {
            return _individuals[which];
        }
        return null;
    }


	// GA ALGORITHM
	////////////////////////////////////

	//Selection operator: Tournament. Selects one individual from population to cross
	//The individual selected must be different than the one in the index "diff".
	//This is used to avoid choosing twice the same individual for crossover.
    private int selectOne(int diff) 
	{
        int size = _params.getTournamentSize();
        int pop_size = _individuals.length;

        Vector<Integer> tournament = new Vector<Integer>();
        int selected = (int) ((Math.random()) * pop_size);
		//Select until this one has not been ever selected
        while (tournament.contains(new Integer(selected)) || selected == diff) {
            selected = (int) ((Math.random()) * pop_size);
        }
        int howMany = 1;
        tournament.add(new Integer(selected));


        int best_index = selected;
        float best_fit = _individuals[selected].getFitness(_fitnessToUse);

		//Select individuals until reach the tournament slots.
        while (howMany < size) {

			selected = (int) ((Math.random()) * pop_size);
			//Select until this one has not been ever selected
            while (tournament.contains(new Integer(selected)) || selected == diff) 
			{
                selected = (int) ((Math.random()) * pop_size);
            }
            howMany++;
            tournament.add(new Integer(selected));

			//Keep the best one to return the winner of the tournament.
            if (compareFitness(_individuals[selected].getFitness(_fitnessToUse), best_fit, EvolutionConstants.FITNESS_POINTS) == -1) {
                best_fit = _individuals[selected].getFitness(_fitnessToUse);
                best_index = selected;
            }
        }

        return best_index;
    }


	//Compares two fitness values.
    //-1 if a better than b, 0 if equals, +1 if b better than a
    public int compareFitness(double a, double b, int n_obj) 
	{
       
        if(n_obj == EvolutionConstants.FITNESS_POINTS)
        {
            return compareFitness(a, b, _params.getFitnessRule());
        }else if(n_obj == EvolutionConstants.FITNESS_DIST_RACED)
        {
            return compareFitness(a, b, false);
        }else if(n_obj == EvolutionConstants.FITNESS_ACCIDENTS)
        {
            return compareFitness(a, b, true);
        }
        System.out.println("WARNING: Unknown fitness: " + n_obj);
        return 0;
    }
    
    
    
    //Compares two fitness values, taking 2 values and a rule (minimize or maximize)
    private int compareFitness(double a, double b, boolean minimize)
    {
        if (minimize) {
            //minimizing
            if (a < b) {
                return -1;
            } else if (a > b) {
                return 1;
            } else {
                return 0;
            }
        } else {
            //Maximizing
            if (a < b) {
                return 1;
            } else if (a > b) {
                return -1;
            } else {
                return 0;
            }
        }   
    }

	//Selection of two indidivuals to cross (GA).
    public Individual[] select() {
        Individual parents[] = new Individual[2];
        int firstParent = selectOne(-1);
        int secondParent = selectOne(firstParent); //To avoid taking the same again
        parents[0] = _individuals[firstParent];
        parents[1] = _individuals[secondParent];

        if (EvolutionManager.DEBUG) {
            System.out.print("[" + firstParent + "] ");
            parents[0].printGenome();
            System.out.print("[" + secondParent + "] ");
            parents[1].printGenome();
        }

        return parents;
    }

	//Gets index of the best individual
	private int getBestIndividualIndex() 
	{
		int best_index = 0;
		float best_fit = Float.MAX_VALUE;
		if (!_params.getFitnessRule()) 
		{
			best_fit = 0; //maximizing

		}
		for (int i = 0; i < _individuals.length; i++) 
		{
			//if(individuals[i].getFitness() <= best_fit){
			if (compareFitness(_individuals[i].getFitness(_fitnessToUse), best_fit, EvolutionConstants.FITNESS_POINTS) == -1) 
			{
				best_fit = _individuals[i].getFitness(_fitnessToUse);
				best_index = i;
			}
		}
		return best_index;

	}

	//Gets index of the worst individual
    private int getWorstIndividualIndex() 
	{
        int worst_index = 0;
        float worst_fit = 0;
        if (!_params.getFitnessRule()) {
            worst_fit = Float.MAX_VALUE; //maximizing

        }
        for (int i = 0; i < _individuals.length; i++) {
            if (compareFitness(_individuals[i].getFitness(_fitnessToUse), worst_fit, EvolutionConstants.FITNESS_POINTS) == 1) {
                worst_fit = _individuals[i].getFitness(_fitnessToUse);
                worst_index = i;
            }
        }
        return worst_index;

    }

	//If Individual ind is better than the worst individual in the population, replace it
    public void replacement(Individual ind) {
        int worst_index = getWorstIndividualIndex();
        if (compareFitness(_individuals[worst_index].getFitness(_fitnessToUse), ind.getFitness(_fitnessToUse), EvolutionConstants.FITNESS_POINTS) == 1) {
            _individuals[worst_index] = ind;
        }
    }

	//Gets the best individual
    public Individual getBestIndividual() {
        return _individuals[getBestIndividualIndex()];
    }

    public void printDebug(String line) {
        try {
            PrintWriter bw = new PrintWriter(new FileOutputStream("DEBUG_FILE.txt", true));
            bw.println(": " + line);
            bw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


	// NSGA-2 ALGORITHM
	////////////////////////////////////	
    
	//Selection of tournament size 2, including dominance checkings for NSGA2
	private int selectOneDominance(int diff) 
	{ 
        
		int pop_size = _individuals.length;
        
		int selected1 = (int) ((Math.random()) * pop_size);
		while (selected1 == diff) 
		{
			selected1 = (int) ((Math.random()) * pop_size);
		}
        
		int selected2 = (int) ((Math.random()) * pop_size);
		while (selected2 == diff || selected2 == selected1) 
		{
			selected2 = (int) ((Math.random()) * pop_size);
		}
        
		if(_individuals[selected1].dominates(_individuals[selected2]))
		{
			return selected1;
		}
		else
		{
			return selected2;
		}
	}
    
	//Selection of two individuals to cross (NSGA2).
	public Individual[] selectDominance() 
	{
		Individual parents[] = new Individual[2];
		int firstParent = selectOneDominance(-1);
		int secondParent = selectOneDominance(firstParent); //To avoid taking the same again
		parents[0] = _individuals[firstParent];
		parents[1] = _individuals[secondParent];

		if (EvolutionManager.DEBUG) 
		{
			System.out.print("[" + firstParent + "] ");
			parents[0].printGenome();
			System.out.print("[" + secondParent + "] ");
			parents[1].printGenome();
		}

		return parents;
	}
    
    // Performs fast_non_dominated_sort for NSGA-2 
    public void fast_non_dominated_sort(Individual[] inds) 
	{

        final int TAM = _individuals.length + 1;
        
        int[] domination_count = new int[TAM]; //For solution i, indicates how many dominate it.
        int[] rank = new int[TAM];
        int remaining;			   //Remaining individuals to assign front
        boolean end;			   //Process must stop if this is true.

        //Initialize sets for dominance
        for (int i = 0; i < TAM; i++) {
            domination_count[i] = 0;
            _front_counter[i] = 0;
            rank[i] = 1;
            inds[i].setFront(-1);
        }

        remaining = TAM;
        end = false;

        //For each front
        for (int front = 0; front < TAM && !end; front++) {
   
            /* FIND NEXT FRONT */
            for (int i = 0; i < TAM; i++) { //For each individual...
                domination_count[i] = 0;
                if (rank[i] == 1) { //If 'i' has no front assigned
  
					for (int j = 0; (j < TAM) && (domination_count[i] == 0); j++) {
                        if ((j != i) && (rank[j] == 1)) { //If 'j' has no front assigned
                            if (inds[j].simple_dominates(inds[i])) { //If i is dominated...
                                domination_count[i] = 1;
                            }
                        }
                    }// end for-j 

                }
            }// end for-i
            /* NEXT FRONT FOUND */

            /* ASSIGN NEXT FRONT */
            for (int i = 0; i < TAM && !end; i++) { //For each individual...
                if (rank[i] == 1 && domination_count[i] == 0) {           //If 'i' has no front assigned and is NON-DOMINATED
                    inds[i].setFront(front);                        //assign this front to 'i'.
                    rank[i] = 0;					//'i' has front assigned.
                    _front_array[front][_front_counter[front]] = i; //i belongs to this front.
                    _front_counter[front]++;			//add one to the counter of this front
                    remaining--;					//One solution less to be assigned.
                    if (remaining == 0) {
                        end = true;
                    }
                }
            }// end for-i
            /* NEXT FRONT ASSIGNED*/

        }// end for-front

    } // end fast_non_dominated_sort
 

    // Performs crowding_distance_assignment for NSGA-2 
    public void crowding_distance_assignment(Individual[] inds) {

        final int TAM = _individuals.length + 1;

        Individual current, comparable1, comparable2;
        int n_obj = EvolutionConstants.NUM_NSGA2_OBJ;
        double[] dist = new double[TAM];			//For solution i, indicates its crowding distance.

        //Initialize distances
        for (int i = 0; i < TAM; i++) {
            inds[i].setCrowdingDistance(1.0F);
            dist[i] = 1.0F;
        }

        //For each front
        for (int front = 0; front < TAM; front++) {
         if(EvolutionManager.NSGA2_DEBUG) System.out.println("[NSGA2] [cda] Sorting on front " + front);
            //For each objective...
            for (int obj = 0; obj < n_obj; obj++) {
                /* Sort, according to this objective, the individuals of this pareto front*/
                for (int i = 0; i < _front_counter[front]; i++) {
                    int min_index = -1;
                    int min = i;
                    for (int j = i + 1; j < _front_counter[front]; j++) {
                        current = inds[_front_array[front][j]];
                        comparable1 = inds[_front_array[front][min]];
                        if (current.getFitness(obj) < comparable1.getFitness(obj)) {
                            min = j; //New minimun
                        }
                    }//end for-j

                    //Swap mimimum one for this
                    min_index = _front_array[front][min];
                    _front_array[front][min] = _front_array[front][i];
                    _front_array[front][i] = min_index;
                }//end for-i

                if(EvolutionManager.NSGA2_DEBUG) for(int i = 0;	i < _front_counter[front]; i++){
                    System.out.println("[NSGA2] [cda] Individual _front_array[" + front + "][" + i +  "] = " + _front_array[front][i]);
                }
                                
                /* Add distances */
                for (int i = 0; i < _front_counter[front]; i++) {
                    if (i == 0 || i == _front_counter[front] - 1) {
                        dist[_front_array[front][i]] += Population.MAX_CROWD_DISTANCE;
                    } else {
                        comparable1 = inds[_front_array[front][i + 1]];
                        comparable2 = inds[_front_array[front][i - 1]];
                        dist[_front_array[front][i]] += Math.abs(comparable1.getFitness(obj) - comparable2.getFitness(obj));
                    }
                }
            }//end for-obj
        }//end for-front

        //Set distances
        for (int i = 0; i < TAM; i++) {
            inds[i].setCrowdingDistance(dist[i]);
        }
        
        if(EvolutionManager.NSGA2_DEBUG) for(int front = 0; front < TAM && _front_counter[front]>0; front++){
            for (int indi = 0; indi < _front_counter[front]; indi++){
		System.out.println("[NSGA2] [cda] FRONT " + front + ", INDIVIDUAL " + _front_array[front][indi] + ", DIST: " 
                        + dist[_front_array[front][indi]]);
            }
	}        
        
    } // end crowding_distance


    //Gets N individuals from a set of individuals 
    //after applying fast_non_dominated_sort and crowding_distance_assignment
    //As Steady State is implemented, only drop worst one
    public int environmental_selection(Individual inds[]) {
        int worst = 0;
        double dist;
        final int TAM = _individuals.length + 1;
        boolean found = false;

        int front = TAM - 1;
        while (!found && front >= 0) {
            if (_front_counter[front] > 0) {//This is the front. Obtain the individual which minor distance (worse)

                found = true;
                worst = _front_array[front][0];
                dist = inds[_front_array[front][0]].getCrowdingDistance();
                
                for (int i = 0; i < _front_counter[front]; i++) {
                    double this_distance = inds[_front_array[front][i]].getCrowdingDistance();
                    if (this_distance < dist) {
                        worst = _front_array[front][i];
                        dist = this_distance;
                    }
                }
            } else {
                front--;
            }
        }

        //Returns index of individual to eliminate
        return worst;
    }

    /* EXTRACTS PARETO FRONTS. PERFORMS:
    1. Fast-non-dominated-sort
    2. Crowding-distance-assignment
    3. Environmental selection
     */
    public void paretoFronts(Individual ind) {

        //Initialize the population with current one and the new individual
        Individual a_pop[] = new Individual[_individuals.length + 1];
        for (int i = 0; i < _individuals.length; i++) {
            a_pop[i] = _individuals[i];
        }

        a_pop[_individuals.length] = ind;

        if(EvolutionManager.NSGA2_DEBUG) System.out.println("[NSGA2] Sorting on fronts...");
        // Fast-non-dominated-sort
        fast_non_dominated_sort(a_pop);

        if(EvolutionManager.NSGA2_DEBUG) System.out.println("[NSGA2] Assigning crowd distance...");
        // Crowding distance assignment
        crowding_distance_assignment(a_pop);


        for (int i = 0; i < _individuals.length; i++) {
            _individuals[i].setFront(a_pop[i].getFront());
            _individuals[i].setCrowdingDistance(a_pop[i].getCrowdingDistance());
        }

        //Environmental selection (drop worst one from population - ONLY FOR STEADY STATE)
        int worst_index = environmental_selection(a_pop);
        if (worst_index != _individuals.length) {
            //The one to eliminate is in the  initial population (before joining it with the steady state individual).
            //Substitute the worst with the new one.
            _individuals[worst_index] = ind;
            _individuals[worst_index].setFront(a_pop[_individuals.length].getFront());
            _individuals[worst_index].setCrowdingDistance(a_pop[_individuals.length].getCrowdingDistance());
            if(EvolutionManager.NSGA2_DEBUG) System.out.println("[NSGA2] New individual substitutes " + worst_index + " in population.");
        } else {
            if(EvolutionManager.NSGA2_DEBUG) System.out.println("[NSGA2] New individual is not added to the population.");
        }
    }

    public int[][] get_front_array() {
        return _front_array;
    }

    public int[] get_front_counter() {
        return _front_counter;
    }

    
    
}
