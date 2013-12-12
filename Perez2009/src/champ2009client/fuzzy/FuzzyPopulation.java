/*
 * Population.java
 * 
 * Created on 04-may-2008, 21:13:18
 * 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package champ2009client.fuzzy;
import java.util.Vector;
import java.io.*;

/**
 *
 * @author Diego
 */
public class FuzzyPopulation {

    protected FuzzyManager []individuals;
    protected GAFuzzyParams params;
    protected int currentGen;
    
    public FuzzyPopulation(){
    }
    
    public FuzzyPopulation(GAFuzzyParams p){
        params = p;
        currentGen = 0;
        if(params.getPhase() == 1){
            individuals = new FuzzyManager[params.getPopulationSize()];
            for(int i = 0; i < params.getPopulationSize(); i++){
                individuals[i] = new FuzzyManager(); //(params) ?
            }        
        }
    }
    
    public void setCurrentGen(int cg){
        currentGen = cg;
    }

    public double getCircuitLength(){
        return params.getCircuitLength();
    }
    
    //Creates a population from coded individual
    public void generate(){
        FuzzyManager fm = new FuzzyManager();
        fm.createFuzzySets();
        
        //Base must be part of the population
        individuals[0] = fm;
        for(int i = 1; i < params.getPopulationSize(); i++) {
        //for(int i = 1; i < 2; i++) {
            //individuals[i] = fm.createMutated(fm, params);
        }

    }
    
    public void set(FuzzyManager ind, int which){
        if(which >= 0 && which < params.getPopulationSize()) 
            individuals[which] = ind;
    }
    
    public FuzzyManager get(int which){
        if(which >= 0 && which < params.getPopulationSize()) 
            return individuals[which];
        return null;
    }
    
    private int selectOne(int diff){
        int size = params.getTournamentSize();
        int pop_size = params.getPopulationSize();
        
        Vector<Integer> tournament = new Vector<Integer>();
        int selected = (int)((Math.random())*pop_size);
        while(tournament.contains(new Integer(selected)) || selected == diff){
              selected = (int)((Math.random())*pop_size);
        }
        int howMany = 1;
        tournament.add(new Integer(selected));
        int best_index = selected;
        double best_fit = individuals[selected].getFitness();
        
        while(howMany < size){

            selected = (int)((Math.random())*pop_size);
            while(tournament.contains(new Integer(selected)) || selected == diff){
                selected = (int)((Math.random())*pop_size);
            }
            howMany++;
            tournament.add(new Integer(selected));
            
            /*if(individuals[selected].getFitness() < min_fit){
                min_fit = individuals[selected].getFitness();
                min_index = selected;
            }*/
            if(FuzzyConstants.compareFitness(individuals[selected].getFitness(), best_fit, params.getFitnessRule())){
                best_fit = individuals[selected].getFitness();
                best_index = selected;
            }
        }
        
        return best_index;
    }
    
    public FuzzyManager[] select(){
        FuzzyManager parents[] = new FuzzyManager[2];
        int firstParent = selectOne(-1);
        int secondParent = selectOne(firstParent);
        parents[0] = individuals[firstParent];
        parents[1] = individuals[secondParent];
        
        return parents;
    }
    
    private int getWorstIndividualIndex(){
        int worst_index = 0;
        double worst_fit = individuals[0].getFitness();
    
        for(int i = 1; i < params.getPopulationSize(); i++){
            if(FuzzyConstants.compareFitness(worst_fit, individuals[i].getFitness(), params.getFitnessRule())){
                worst_fit = individuals[i].getFitness();
                worst_index = i;
            }
        }
        return worst_index;
        
    }
        
    public void replacement(FuzzyManager ind){
        int worst_index = getWorstIndividualIndex();
        if(FuzzyConstants.compareFitness(ind.getFitness(), individuals[worst_index].getFitness(), params.getFitnessRule())){
            individuals[worst_index] = ind;
        }
    }
    
    private int getBestIndividualIndex(){
        int best_index = 0;
        double best_fit = individuals[0].getFitness();
    
        for(int i = 1; i < params.getPopulationSize(); i++){
            //if(individuals[i].getFitness() <= best_fit){
            if(FuzzyConstants.compareFitness(individuals[i].getFitness(),best_fit,params.getFitnessRule())){
                best_fit = individuals[i].getFitness();
                best_index = i;
            }
        }
        return best_index;
        
    }
    
    public FuzzyManager getBestIndividual(){
        return individuals[getBestIndividualIndex()];
    }

    public void printDebug(String line){
        try{
            PrintWriter bw = new PrintWriter(new FileOutputStream("DEBUG_FILE.txt",true));
            bw.println(currentGen + ": " + line);
            bw.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
}
