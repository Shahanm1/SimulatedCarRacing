/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package champ2009client.evolution;

import champ2009client.fuzzy.FuzzyConstants;
import champ2009client.fuzzy.FuzzyManager;
import champ2009client.fuzzy.FuzzySet;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.PrintWriter;

/**
 * This file reads and writes the files used by the evolutionary algorithm
 * @author Diego
 */
public class FileHandler {

    private EvolutionManager _evol;
        
    public FileHandler(EvolutionManager ev){
        _evol = ev;
    }
    
    public static final String INDIVIDUAL_FILE = "Individual.txt";
    public static final String INDIVIDUAL_FILE_SECURE = "IndividualSec.txt";
    
    public Individual readIndividual(String filename){
        try{
            BufferedReader br = new BufferedReader(new FileReader(filename));   
            FuzzyManager fm;
            
            int j = 0;
            fm = new FuzzyManager();
            GeneticCapsule[] caps = new GeneticCapsule[EvolutionConstants.NUM_CAPSULES];

            String genomeLine = br.readLine();
            String[] genomeData = genomeLine.split(" "); 
            int many = genomeData.length;

            while(many > 0 && !genomeData[0].equals(FITNESS_STRING))
            {
                int type = Integer.parseInt(genomeData[0]);
                if(type == EvolutionConstants.TYPE_FUZZY_MANAGER)
                {
                    String prop = genomeData[2];
                    String set = genomeData[3];
                    double x1 = Double.parseDouble(genomeData[4]);
                    double x2 = Double.parseDouble(genomeData[5]);
                    double x3 = Double.parseDouble(genomeData[6]);
                    double x4 = Double.parseDouble(genomeData[7]);

                    fm.getFuzzySet(prop, set).initialize(x1, x2, x3, x4);

                }else
                {
                    String name = genomeData[1];
                    String surname = genomeData[2];
                    Double data = new Double(genomeData[3]);

                    GeneticCapsule cap = new GeneticCapsule(name,surname);
                    cap.setData(data, type);
                    caps[j++] = cap;
                }

                genomeLine = br.readLine();
                if(genomeLine != null)
                {
                    genomeData = genomeLine.split(" "); 
                    many = genomeData.length;
                }else many = 0;
            }

            GeneticCapsule fmCap = new GeneticCapsule("FuzzyManager","_manager");
            fmCap.setData(fm, EvolutionConstants.TYPE_FUZZY_MANAGER);
            caps[j++] = fmCap;

            //set capsules
            Individual newOne = new Individual(_evol.getPop());
            newOne.setCapsules(caps);

            return newOne;      
            
        }catch(Exception e){
            e.printStackTrace();
        }
        
        return null;
    }
    
    
    //if howMany==-1, read params data
    public void readPopulation(String filename, int howMany){
        try{
            BufferedReader br = new BufferedReader(new FileReader(filename));   
            FuzzyManager fm;
            
            int num_ind = howMany;
            if(num_ind == -1 || num_ind > _evol.getParams().getPopulationSize())
                num_ind = _evol.getParams().getPopulationSize();

            for(int i = 0; i < num_ind; i++){
                int j = 0;
                fm = new FuzzyManager();
                GeneticCapsule[] caps = new GeneticCapsule[EvolutionConstants.NUM_CAPSULES];
                
                String genomeLine = br.readLine();
                String[] genomeData = genomeLine.split(" "); 
                int many = genomeData.length;
                
                while(many > 0 && !genomeData[0].equals(FITNESS_STRING))
//                while(many > 1)
                {
                    int type = Integer.parseInt(genomeData[0]);
                    if(type == EvolutionConstants.TYPE_FUZZY_MANAGER)
                    {
                        String prop = genomeData[2];
                        String set = genomeData[3];
                        double x1 = Double.parseDouble(genomeData[4]);
                        double x2 = Double.parseDouble(genomeData[5]);
                        double x3 = Double.parseDouble(genomeData[6]);
                        double x4 = Double.parseDouble(genomeData[7]);

                        fm.getFuzzySet(prop, set).initialize(x1, x2, x3, x4);
                        
                    }else
                    {
                        String name = genomeData[1];
                        String surname = genomeData[2];
                        Double data = new Double(genomeData[3]);

                        GeneticCapsule cap = new GeneticCapsule(name,surname);
                        cap.setData(data, type);
                        caps[j++] = cap;
                    }
                    
                    genomeLine = br.readLine();
                    if(genomeLine != null)
                    {
                        genomeData = genomeLine.split(" "); 
                        many = genomeData.length;
                    }else many = 0;
                }
                
                GeneticCapsule fmCap = new GeneticCapsule("FuzzyManager","_manager");
                fmCap.setData(fm, EvolutionConstants.TYPE_FUZZY_MANAGER);
                caps[j++] = fmCap;
                
                //set capsules
                Individual newOne = new Individual(_evol.getPop());
                newOne.setCapsules(caps);
            
                //Individual fitness                
                //PRINT Fitness
                String[] fitnessData = genomeLine.split(" ");    
                if(fitnessData[0].equals(FITNESS_STRING))
                {
                    if(_evol.getParams().getPhase() == 0) //NSGA2
                    { 
                        newOne.setFitness(EvolutionConstants.FITNESS_DIST_RACED, new Double(fitnessData[1]).floatValue() );
                        newOne.setFitness(EvolutionConstants.FITNESS_ACCIDENTS, new Double(fitnessData[2]).floatValue() );
                        newOne.setFront( new Integer(fitnessData[3]).intValue() );
                        newOne.setCrowdingDistance(new Double(fitnessData[4]).doubleValue());
                    }else
                    {
                        newOne.setFitness(EvolutionConstants.FITNESS_POINTS, Float.parseFloat(genomeLine)); 
                    }
                }
                
                _evol.getPop().set(newOne, i);
                
            }
            
            //Generation number
            _evol.setCurrentGen(Integer.parseInt(br.readLine()));
            
        }catch(Exception e){
            e.printStackTrace();
        }
    }
        
    public void printPopulation(String filename, int howMany){
        try{
            
            PrintWriter bw = new PrintWriter(new FileOutputStream(filename));
        
            int num_ind = howMany;
            if(num_ind == -1 || num_ind > _evol.getParams().getPopulationSize())
                num_ind = _evol.getParams().getPopulationSize();
    
            GeneticCapsule[] capsules;
            
            for(int i = 0; i < num_ind; i++){
                capsules = _evol.getPop().get(i).getCapsules();
                
                for(int j = 0; j < capsules.length; j++){
                    StringBuffer genomeData = new StringBuffer();
                
                    GeneticCapsule c = capsules[j];
                    if(c!=null)
                    {
                        if(c.getType() == EvolutionConstants.TYPE_FUZZY_MANAGER)
                        {
                            FuzzyManager fm = (FuzzyManager)c.getData();
                            for(int k = 0; k < FuzzyConstants.NUM_FUZZY_SETS; k++)
                            {
                                genomeData = new StringBuffer();
                                String fuzzySetTxt[] = FuzzyConstants.FUZZY_RELATIONS[k];
                                FuzzySet fs = fm.getFuzzySet(fuzzySetTxt[0], fuzzySetTxt[1]);
                                genomeData.append(c.getType() + " " + k + " " + fuzzySetTxt[0]
                                         + " " + fuzzySetTxt[1] + " ");
                                double values[] = fs.getFuzzyLimits();
                                for(int m = 0; m < values.length; m++)
                                {
                                    genomeData.append(values[m] + " ");
                                }
                                bw.println(genomeData.toString());
                            }
                        }else
                        {
                            double value = ((Double)c.getData()).doubleValue();
                            genomeData.append(c.getType() + " " + c.getName() + " " 
                                    + c.getSurname() + " " + (float)value);
                            bw.println(genomeData.toString());
                        }
                    }
                }
                
                //PRINT Fitness
                if(_evol.getParams().getPhase() == 0) //NSGA2
                {
                     bw.println(FITNESS_STRING + " " + _evol.getPop().get(i).getFitness(EvolutionConstants.FITNESS_DIST_RACED) + " " + 
                             _evol.getPop().get(i).getFitness(EvolutionConstants.FITNESS_ACCIDENTS) + " " + 
                             _evol.getPop().get(i).getFront() + " " + _evol.getPop().get(i).getCrowdingDistance()); 
                }else
                {
                    bw.println(FITNESS_STRING + " " + _evol.getPop().get(i).getFitness(EvolutionConstants.FITNESS_POINTS));
                }
                
            }
            
            //Finally, print current generation
            bw.println(_evol.getCurrentGen());
            bw.close();

        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void printPopulationGrid(String filename, int howMany){
        try{
            
            int num_ind = howMany;
            if(num_ind == -1 || num_ind > _evol.getParams().getPopulationSize())
                num_ind = _evol.getParams().getPopulationSize();
    
            GeneticCapsule[] capsules;
            
            for(int i = 0; i < num_ind; i++){
                
                PrintWriter bw = new PrintWriter(new FileOutputStream(POP_GRID_FILE + i + ".txt"));
                
                capsules = _evol.getPop().get(i).getCapsules();
                
                for(int j = 0; j < capsules.length; j++){
                    StringBuffer genomeData = new StringBuffer();
                
                    GeneticCapsule c = capsules[j];
                    if(c!=null)
                    {
                        if(c.getType() == EvolutionConstants.TYPE_FUZZY_MANAGER)
                        {
                            FuzzyManager fm = (FuzzyManager)c.getData();
                            for(int k = 0; k < FuzzyConstants.NUM_FUZZY_SETS; k++)
                            {
                                genomeData = new StringBuffer();
                                String fuzzySetTxt[] = FuzzyConstants.FUZZY_RELATIONS[k];
                                FuzzySet fs = fm.getFuzzySet(fuzzySetTxt[0], fuzzySetTxt[1]);
                                genomeData.append(c.getType() + " " + k + " " + fuzzySetTxt[0]
                                         + " " + fuzzySetTxt[1] + " ");
                                double values[] = fs.getFuzzyLimits();
                                for(int m = 0; m < values.length; m++)
                                {
                                    genomeData.append(values[m] + " ");
                                }
                                bw.println(genomeData.toString());
                            }
                        }else
                        {
                            double value = ((Double)c.getData()).doubleValue();
                            genomeData.append(c.getType() + " " + c.getName() + " " 
                                    + c.getSurname() + " " + (float)value);
                            bw.println(genomeData.toString());
                        }
                    }
                }
                
                //PRINT Fitness
                if(_evol.getParams().getPhase() == 0) //NSGA2
                {
                     bw.println(FITNESS_STRING + " " + _evol.getPop().get(i).getFitness(EvolutionConstants.FITNESS_DIST_RACED) + " " + 
                             _evol.getPop().get(i).getFitness(EvolutionConstants.FITNESS_ACCIDENTS) + " " + 
                             _evol.getPop().get(i).getFront() + " " + _evol.getPop().get(i).getCrowdingDistance()); 
                }else
                {
                    bw.println(FITNESS_STRING + " " + _evol.getPop().get(i).getFitness(EvolutionConstants.FITNESS_POINTS));
                }
                
                bw.close();
            }
            
            //Finally, print current generation

        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    /////////////////////
    // GA functions    //
    /////////////////////
    public static final String FITNESS_FILE     = "FITNESS.txt";
    public static final String POP_GA_FILE      = "GA_POPULATION.txt";
    public static final String POP_BASE_GA_FILE = "BASE_GA_POPULATION.txt";
    public static final String POP_GRID_FILE = "INDIVIDUAL_";
    public static final String STARTING_GRID_FILE  = "STARTING_GA_GRID.txt";
    
    //Prints the fitness of the Best individual
    public void printGAFitness(){
        try{
            PrintWriter bw = new PrintWriter(new FileOutputStream(FITNESS_FILE,true));
            //bw.println(evol.getPop().getBestIndividual().getFitness());
            String fitStr = null;//_evol.getPop().getBestIndividual().getFitnessStr();
            if(fitStr == null || fitStr.length() == 0) fitStr = "" + 
                    _evol.getPop().getBestIndividual().getFitness(EvolutionConstants.FITNESS_POINTS);
            bw.println(fitStr);
            bw.close();
        }catch(Exception e){
            e.printStackTrace(); 
        }
    }

    
    
    /////////////////////
    // NSGA2 functions //
    /////////////////////

    public static final String BESTS_NSGA2_FILE = "INDIVIDUAL_NSGA2.txt";
    public static final String POP_NSGA2_FILE      = "NSGA2_POPULATION.txt";
    public static final String POP_BASE_NSGA2_FILE = "BASE_NSGA2_POPULATION.txt";
    public static final String NSGA2_FITNESS_FILE     = "NSGA2_FITNESS.txt";
    public static final String FITNESS_STRING   = "FITNESS_DATA";
    public static final String PARETO_FILE     = "PARETO_FRONTS.txt";
    
    //Prints all individual in pareto front 0
    public void printBestsNSGA2(String filename){
        try{
            
            PrintWriter bw = new PrintWriter(new FileOutputStream(filename));
        
            //int num_ind = _evol.getParams().getPopulationSize();
            int num_ind = _evol.getPop().get_front_counter()[0];
            GeneticCapsule[] capsules;
            
            for(int indi = 0; indi < num_ind; indi++){
                
                int i = _evol.getPop().get_front_array()[0][indi];
                
                capsules = _evol.getPop().get(i).getCapsules();
                
                for(int j = 0; j < capsules.length; j++){
                    StringBuffer genomeData = new StringBuffer();
                
                    GeneticCapsule c = capsules[j];
                    if(c!=null)
                    {                    
                        if(c.getType() == EvolutionConstants.TYPE_FUZZY_MANAGER)
                        {
                            FuzzyManager fm = (FuzzyManager)c.getData();
                            for(int k = 0; k < FuzzyConstants.NUM_FUZZY_SETS; k++)
                            {
                                genomeData = new StringBuffer();
                                String fuzzySetTxt[] = FuzzyConstants.FUZZY_RELATIONS[k];
                                FuzzySet fs = fm.getFuzzySet(fuzzySetTxt[0], fuzzySetTxt[1]);
                                genomeData.append(c.getType() + " " + k + " " + fuzzySetTxt[0]
                                         + " " + fuzzySetTxt[1] + " ");
                                double values[] = fs.getFuzzyLimits();
                                for(int m = 0; m < values.length; m++)
                                {
                                    genomeData.append(values[m] + " ");
                                }
                                bw.println(genomeData.toString());
                            }
                        }else
                        {
                            double value = ((Double)c.getData()).doubleValue();
                            genomeData.append(c.getType() + " " + c.getName() + " " 
                                    + c.getSurname() + " " + (float)value);
                            bw.println(genomeData.toString());
                        }
                    }
                }
                
                //PRINT Fitness
                if(_evol.getParams().getPhase() == 0) //NSGA2
                {
                     bw.println(FITNESS_STRING + " " + _evol.getPop().get(i).getFitness(EvolutionConstants.FITNESS_DIST_RACED) + " " + 
                             _evol.getPop().get(i).getFitness(EvolutionConstants.FITNESS_ACCIDENTS) + " " + 
                             _evol.getPop().get(i).getFront() + " " + _evol.getPop().get(i).getCrowdingDistance()); 
                }else
                {
                    bw.println(FITNESS_STRING + " " + _evol.getPop().get(i).getFitness(EvolutionConstants.FITNESS_POINTS));
                }
                
            }
            
            bw.close();

        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    //reads howMany individuals from a file (does not read generation number or assign them to pop).
    //Returns the individuals read from a file in an array. If more individuals are requested than the number of them
    //in the file, null will be retrieved in the array for those non-existants individuals.
    public Individual[] readBestsNSGA2(String filename, int howMany){
        try{
            BufferedReader br = new BufferedReader(new FileReader(filename));   
            FuzzyManager fm;
            
            int num_ind = howMany;
            if(num_ind == -1 || num_ind >= _evol.getParams().getPopulationSize())
                num_ind = _evol.getParams().getPopulationSize();
    
            Individual[] individuals = new Individual[num_ind];
            
            for(int i = 0; i < num_ind; i++){
                int j = 0;
                fm = new FuzzyManager();
                GeneticCapsule[] caps = new GeneticCapsule[EvolutionConstants.NUM_CAPSULES];
                
                String genomeLine = br.readLine();
                if(genomeLine == null) break;
                String[] genomeData = genomeLine.split(" "); 
                int many = genomeData.length;
                
                while(many > 0 && !genomeData[0].equals(FITNESS_STRING))
//                while(many > 1)
                {
                    int type = Integer.parseInt(genomeData[0]);
                    if(type == EvolutionConstants.TYPE_FUZZY_MANAGER)
                    {
                        String prop = genomeData[2];
                        String set = genomeData[3];
                        double x1 = Double.parseDouble(genomeData[4]);
                        double x2 = Double.parseDouble(genomeData[5]);
                        double x3 = Double.parseDouble(genomeData[6]);
                        double x4 = Double.parseDouble(genomeData[7]);

                        fm.getFuzzySet(prop, set).initialize(x1, x2, x3, x4);
                        
                    }else
                    {
                        String name = genomeData[1];
                        String surname = genomeData[2];
                        Double data = new Double(genomeData[3]);

                        GeneticCapsule cap = new GeneticCapsule(name,surname);
                        cap.setData(data, type);
                        caps[j++] = cap;
                    }
                    
                    genomeLine = br.readLine();
                    if(genomeLine != null)
                    {
                        genomeData = genomeLine.split(" "); 
                        many = genomeData.length;
                    }else many = 0;
                }
                
                GeneticCapsule fmCap = new GeneticCapsule("FuzzyManager","_manager");
                fmCap.setData(fm, EvolutionConstants.TYPE_FUZZY_MANAGER);
                caps[j++] = fmCap;
                
                //set capsules
                Individual newOne = new Individual(_evol.getPop());
                newOne.setCapsules(caps);
            
                //Individual fitness                
                //PRINT Fitness
                String[] fitnessData = genomeLine.split(" ");    
                if(fitnessData[0].equals(FITNESS_STRING))
                {
                    if(_evol.getParams().getPhase() == 0) //NSGA2
                    { 
                        newOne.setFitness(EvolutionConstants.FITNESS_DIST_RACED, new Double(fitnessData[1]).floatValue() );
                        newOne.setFitness(EvolutionConstants.FITNESS_ACCIDENTS, new Double(fitnessData[2]).floatValue() );
                        newOne.setFront( new Integer(fitnessData[3]).intValue() );
                        newOne.setCrowdingDistance(new Double(fitnessData[4]).doubleValue());
                    }else
                    {
                        //newOne.setFitness(EvolutionConstants.FITNESS_POINTS, Float.parseFloat(genomeLine)); 
                        newOne.setFitness(EvolutionConstants.FITNESS_POINTS, 0); 
                    }
                }
                
                individuals[i] = newOne;
                
            }
            
            return individuals;
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }
    
    //Prints the fitness of the Best individual
    public void printNSGA2Fitness(){
        try{
            int frontCounter[] = _evol.getPop().get_front_counter();
            int frontArray[][] = _evol.getPop().get_front_array();
            StringBuffer fitStr = new StringBuffer();
            
            //Fitness file. Print up to 5 of first front
            PrintWriter bw = new PrintWriter(new FileOutputStream(NSGA2_FITNESS_FILE,true));
            for(int i = 0; i < frontCounter[0] && i < 5; i++)
            {
                Individual ind = _evol.getPop().get(frontArray[0][i]);
                if(ind != null) //This could happen when it is base population
                {
                    fitStr.append("[ " + ind.getFitness(0) + " " + ind.getFitness(1) + "] ");
                }
            }
            bw.println(fitStr.toString());
            bw.close();
            
            
            //Printing pareto fronts. Prints the number of individuals in each front, from front 0 to N
            // where N is the first front with 0 individuals
            fitStr = new StringBuffer();
            bw = new PrintWriter(new FileOutputStream(PARETO_FILE,true));
            //for(int i = 0; i < frontCounter.length; i++)
            int i = -1;
            do
            {
                fitStr.append(frontCounter[++i] + " ");
            }while(i+1<frontCounter.length && frontCounter[i]>0); //this mess with 'i' is to show also first front with 0 inds.
            
            
            bw.println(fitStr.toString());
            bw.close();
            
        }catch(Exception e){
            e.printStackTrace(); 
        }
    }

    
}
