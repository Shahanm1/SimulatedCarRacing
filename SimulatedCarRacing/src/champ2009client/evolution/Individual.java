/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package champ2009client.evolution;

import champ2009client.fuzzy.FuzzyManager;

/**
 *
 * @author Diego
 */
public class Individual {

	//Each individual has a genetic code of EvolutionConstants.NUM_CAPSULES capsules
    private GeneticCapsule[] _capsules;
	//Individual fitness
    private float[] _fitness;    
	//Reference to my population.
    private Population _myPop;
    
    //for NSGA2
    private int _front;
    private double _crowding_distance;
    
    public Individual(Population pop)
    {
        _capsules = new GeneticCapsule[EvolutionConstants.NUM_CAPSULES];
        _fitness = new float[3];
        _myPop = pop;
        _front = 0;
        _crowding_distance = 0.0f;
    }

    //This crosses and mutates this individual with another one
    public Individual crossAndMutate(Individual other, GAParams params) throws Exception
    {
        Individual ind = new Individual(_myPop);
        GeneticCapsule[] caps = new GeneticCapsule[EvolutionConstants.NUM_CAPSULES];
        GeneticCapsule[] thisCaps = this.getCapsules();
        GeneticCapsule[] otherCaps = other.getCapsules();
        
        //CROSSOVER (for each capsule)
        for(int i = 0; i < EvolutionConstants.NUM_CAPSULES; i++)
        {
            if(thisCaps[i] != null)
            {
                GeneticCapsule p1 = thisCaps[i].getCopy();
                GeneticCapsule p2 = otherCaps[i].getCopy();  
                caps[i] = p1.cross(p2, params);
            }
        }        
        ind.setCapsules(caps);
        
        if(EvolutionManager.DEBUG) 
        {
            System.out.println("COMBINING: ");
            this.printGenome();
            other.printGenome();
            System.out.println("RESULT CROSS: "); 
            ind.printGenome();
        }
        
        
        //MUTATION (for each capsule)
        for(int i = 0; i < EvolutionConstants.NUM_CAPSULES; i++)
        {
            if(caps[i]!=null)
                caps[i].mutate(params);
        }
        ind.setCapsules(caps);
        
        if(EvolutionManager.DEBUG)
        {
            System.out.println("RESULT MUTATED: "); 
            ind.printGenome();
        }
        
        return ind; 
    }
    
    //Creates a new individual, mutating THIS.
    public Individual getMutation(GAParams params)
    {
        Individual ind = new Individual(_myPop);
        GeneticCapsule[] caps = new GeneticCapsule[EvolutionConstants.NUM_CAPSULES];
        GeneticCapsule[] baseCaps = this.getCapsules();
        
		//For each capsule, mutate
        for(int i = 0; i < EvolutionConstants.NUM_CAPSULES; i++)
        {
            if(baseCaps[i] != null)
            {
                GeneticCapsule c = baseCaps[i].getCopy();
                c.mutate(params);
                caps[i] = c;
            }
        }
        
        ind.setCapsules(caps);
        return ind;
    }
    
    //Yeah... prints the genome
    public void printGenome()
    { 
        for(int i = 0; i < EvolutionConstants.NUM_CAPSULES; i++)
        {
            GeneticCapsule c = _capsules[i];
            if(c != null && c.getType() == EvolutionConstants.TYPE_FUZZY_MANAGER)
            {
                FuzzyManager fm = (FuzzyManager) c.getData();
                fm.printGenome();
            }else
            {
                c.printGenome();
            }
        }   
    }
            
    //Indicates if this dominates the individual received, considering fronts and crowding distances.
    public boolean dominates(Individual ind){
        if( (_front < ind.getFront()) ||
            (_front == ind.getFront() && _crowding_distance > ind.getCrowdingDistance())) 
            return true;
        else return false;
    }    
    
    //Indicates if this dominates the individual received.
    public boolean simple_dominates(Individual ind){
        boolean domination = true;
        int i = 0;
        int n_obj = EvolutionConstants.NUM_NSGA2_OBJ;
		
        //Condition 1: THIS is NO WORSE than IND in all objectives
        while (domination && i < n_obj){

            if(_myPop.compareFitness(_fitness[i],ind.getFitness(i),i) == 1) domination = false;
            else i++;
        }

        //Condition 2: THIS must have at least one objective BETTER than IND
        if(domination){
            i=0;
            domination = false;
            while (!domination && i < n_obj){
                //This objective is better, so "this" DOES dominate "ind"
                if(_myPop.compareFitness(_fitness[i],ind.getFitness(i),i) == -1) domination = true;
                else i++;
            }
        }

        return domination;
    }

    
    public double getCrowdingDistance() {
        return _crowding_distance;
    }

    public int getFront() {
        return _front;
    }

    public void setCrowdingDistance(double crowding_distance) {
        _crowding_distance = crowding_distance;
    }

    public void setFront(int front) {
        _front = front;
    }
           
	public float getFitness(int which)
	{
		return _fitness[which];   
	}
    
	public void setFitness(int which, float fitness)
	{
		_fitness[which] = fitness;
	}
        
	public void setCapsules(GeneticCapsule[] capsules)
	{
		_capsules = capsules;
	}
    
	public GeneticCapsule[] getCapsules()
	{
		return _capsules;
	}
    
}
