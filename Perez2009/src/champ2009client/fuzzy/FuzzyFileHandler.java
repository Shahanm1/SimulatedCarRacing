/*
 * FileHandler.java
 * 
 * Created on 05-may-2008, 22:03:08
 * 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package champ2009client.fuzzy;
//import champ2009client.Evolution;
import java.io.*;

/**
 *
 * @author Diego
 */
public class FuzzyFileHandler {

    //private Evolution evol;
    private final String FITNESS_FILE = "FITNESS.txt";
    private final String POP_FILE = "FUZZY_POPULATION.txt";
    private final String INDIVIDUAL_FILE = "INDIVIDUAL.txt";
    
    
/*    public FuzzyFileHandler(Evolution ev){
        evol = ev;
    }
  */  
    
    public FuzzyFileHandler(){
    }
    
    public double[] readFileIndividual(){
        double gen[];
        try{
            BufferedReader br = new BufferedReader(new FileReader(INDIVIDUAL_FILE));   
            
            //Bye, fitness, bye!
            br.readLine();
            
            //Genome
            String genome = br.readLine();
            String[] genome_data =  genome.split(" "); 

            gen = new double[genome_data.length];
            for(int j = 0; j < genome_data.length; j++){
                gen[j] = Double.parseDouble(genome_data[j]);

            }
            
            br.close();
            
        }catch(Exception e){
            e.printStackTrace();
            gen = null;
        }
        
        return gen;
    }
    
    public void readPopulation(){
        /*try{
            BufferedReader br = new BufferedReader(new FileReader(POP_FILE));   
            
            int num_ind = evol.getParams().getPopulationSize();

            for(int i = 0; i < num_ind; i++){
                //Individual fitness
                evol.getPop().get(i).setFitness(Float.parseFloat(br.readLine()));
                
                //Genome
                String genome = br.readLine();
                String[] genome_data =  genome.split(" "); 
                
                double gen[] = new double[genome_data.length];
                for(int j = 0; j < genome_data.length; j++){
                    gen[j] = Double.parseDouble(genome_data[j]);
                    
                }

                evol.getPop().get(i).setGenome(gen);
                
            }
            
            //Generation number
            evol.setCurrentGen(Integer.parseInt(br.readLine()));
            
        }catch(Exception e){
            e.printStackTrace();
        }
        */
    }
        
    public void printPopulation(){
       /* try{
            
            PrintWriter bw = new PrintWriter(new FileOutputStream(POP_FILE));
        
            int num_ind = evol.getParams().getPopulationSize();
            int size_ind = evol.getParams().getGenomeSize();
             
            for(int i = 0; i < num_ind; i++){
                StringBuffer genome = new StringBuffer();
                
                double gen[] = evol.getPop().get(i).getGenome();
                for(int j = 0; j < gen.length; j++){
                    genome.append(gen[j] + " ");
                }
                
                //PRINT Fitness and genome
                bw.println(evol.getPop().get(i).getFitness());
                bw.println(genome.toString());
            }
            
            //Finally, print current generation
            bw.println(evol.getCurrentGen());
            bw.close();

        }catch(Exception e){
            e.printStackTrace();
        }*/
    }
    
    //Prints the fitness of the Best individual
    public void printPopFitness(){
           //evol.getPop().getBestIndividual().printGenome();
    }
    
    //Prints the fitness of the Best individual
    public void printFitness(){
        /*try{
            PrintWriter bw = new PrintWriter(new FileOutputStream(FITNESS_FILE,true));
            //bw.println(evol.getPop().getBestIndividual().getFitness());
            String fitStr = evol.getPop().getBestIndividual().getFitnessStr();
            if(fitStr == null || fitStr.length() == 0) fitStr = "" + evol.getPop().getBestIndividual().getFitness();
            bw.println(fitStr);
            bw.close();
        }catch(Exception e){
            e.printStackTrace();
        }*/
    }
    
    public void printIndividual(FuzzyManager ind){
        try{
            
            PrintWriter bw = new PrintWriter(new FileOutputStream(INDIVIDUAL_FILE));
            StringBuffer genome = new StringBuffer();
                
            double gen[] = ind.getGenome();
            for(int j = 0; j < gen.length; j++){
                genome.append(gen[j] + " ");
            }
                
            //PRINT Fitness and genome
            bw.println(ind.getFitness());
            bw.println(genome.toString());

            bw.close();

        }catch(Exception e){
            e.printStackTrace();
        }

    }
     
    
    
    public void printFileIndividual(double gen[]){

        //RETRIEVING FITNESS
        double fit;
        try{
            BufferedReader br = new BufferedReader(new FileReader(INDIVIDUAL_FILE));   
            fit = Double.parseDouble(br.readLine());
            br.close();
        }catch(Exception e){
            e.printStackTrace();
            fit = -1;
        }
        
        try{
            
            PrintWriter bw = new PrintWriter(new FileOutputStream(INDIVIDUAL_FILE));
            StringBuffer genome = new StringBuffer();
                
            for(int j = 0; j < gen.length; j++){
                genome.append(gen[j] + " ");
            }
                
            //PRINT Fitness and genome
            bw.println(fit);
            bw.println(genome.toString());

            bw.close();

        }catch(Exception e){
            e.printStackTrace();
        }

    }
}
