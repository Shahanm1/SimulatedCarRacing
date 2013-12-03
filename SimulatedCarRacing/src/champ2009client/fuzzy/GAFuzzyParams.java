/*
 * GAParams.java
 * 
 * Created on 04-may-2008, 18:55:13
 * 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package champ2009client.fuzzy;

import java.io.*;

/**
 *
 * @author Diego
 */
public class GAFuzzyParams {

    private int genome_size; //Genomoe size
    private int num_generations; //Number of generations
    private int pop_size; // Number of individuals on pop.
    private int tournament_size; //Tournament size
    private float mutation; //Mutation probability
    private boolean minimize; //Fitness Rule: [1: minimize], [0: maximize]
    private int phase; //Phase of the GA (1: Normal GA, 2: pop GA)
    private float circuit_length;
    private int numServers;
    private int startServer;
    private int flush;
    
    public GAFuzzyParams(){
        try{
            BufferedReader br = new BufferedReader(new FileReader("raceclient/params.txt"));
            
            pop_size = (Integer.parseInt(br.readLine()));
            genome_size = (Integer.parseInt(br.readLine()));
            num_generations = (Integer.parseInt(br.readLine()));
            tournament_size = (Integer.parseInt(br.readLine()));
            mutation = (Float.parseFloat(br.readLine()));
            minimize = ((Integer.parseInt(br.readLine())) == 1);
            circuit_length = (Float.parseFloat(br.readLine()));
            numServers = (Integer.parseInt(br.readLine()));
            flush = (Integer.parseInt(br.readLine()));
            
            String start = br.readLine();
            if(start != null) startServer = Integer.parseInt(start);
            else startServer = 3000;
            
            phase = 1;
            
        }catch(Exception e){
            System.out.println("Error en el fichero de parametros:");
            e.printStackTrace();
        }
    }
    
    public float getCircuitLength   ()      {return circuit_length;}
    public float getMutation        ()      {return mutation;}
    public boolean getFitnessRule   ()      {return minimize;}
    public int getNumGenerations    ()      {return num_generations;}
    public int getGenomeSize        ()      {return genome_size;}
    public int getPhase             ()      {return phase;}
    public int getPopulationSize    ()      {return pop_size;}
    public int getTournamentSize    ()      {return tournament_size;}
    public int getNumServers        ()      {return numServers;}
    public int getFlush             ()      {return flush;}
    public int getStartServer       ()      {return startServer;};
    
    public void setPhase(int p){phase = p;}
}
