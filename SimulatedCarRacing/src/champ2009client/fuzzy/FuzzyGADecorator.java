/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package champ2009client.fuzzy;

import champ2009client.evolution.EvolutionConstants;
import champ2009client.evolution.GAParams;
import java.text.DecimalFormat;

/**
 * Implementation of genetic operators for fuzzy sets
 * @author Diego
 */
public class FuzzyGADecorator extends FuzzyDecorator {
    
    int genome_length;
    
    public FuzzyGADecorator(FuzzyManager fm) {
        _individual = fm;
        _ban = 0;
        _fitness = -Double.MAX_VALUE;
        genome_length = calculateGenomeLength();
        _fitnessStr = new String();
        _dirty = false;
    }
    
    //Calculates genome length (deprecated)
    private int calculateGenomeLength(){
        FuzzyManagerIterator fmi = _individual.getIterator();
        fmi.init();
        genome_length = 0;
        FuzzySet fs;
        
        while(fmi.hasMoreElements()){
            fs = fmi.next();
            genome_length += fs.getNumVars();
        }
        
        return genome_length;
    }

    public void readIndividual(){
        //FuzzyFileHandler inout = new FuzzyFileHandler(null);
        FuzzyFileHandler inout = new FuzzyFileHandler();
        double[] gen = inout.readFileIndividual();
        setReadGenome(gen);
    }
    
    public void setReadGenome(double[] gen){
        
        FuzzyManagerIterator fmi = _individual.getIterator();
        fmi.init();
        int pos = 0;
        FuzzySet fs;
        
        //Set values to the sets (NEEDED FOR DRIVING)
        while(fmi.hasMoreElements()){
            fs = fmi.next();
            int numVars = fs.getNumVars();

            boolean left = fs.isLeft();
            if(numVars == 0){
                //Skip this one (is not fuzzy)
            }else if(numVars == 4){
                //Center
                fs.initialize(gen[pos], gen[pos+1], gen[pos+2], gen[pos+3]);
            }else if(numVars == 2){
                if(left){
                    fs.initialize(0,0,gen[pos], gen[pos+1]);    
                }else{//Must be right
                    fs.initialize(gen[pos], gen[pos+1], 0, 0);    
                }
            }
            
            pos += numVars;
        }
                
    }
    
    public double[] getPrintableGenome(){
        
        FuzzyManagerIterator fmi = _individual.getIterator();
        fmi.init();
        int pos = 0;
        FuzzySet fs;
        double genome[] = new double[genome_length];
        
        while(fmi.hasMoreElements()){
            fs = fmi.next();
            int numVars = fs.getNumVars();
            boolean left = fs.isLeft();
            
            if(numVars == 0){
                //Skip this one (is not fuzzy)
            }else if(numVars == 4){
                //Center
                genome[pos] = fs.getFuzzyLimit(0);
                genome[pos+1] = fs.getFuzzyLimit(1);
                genome[pos+2] = fs.getFuzzyLimit(2);
                genome[pos+3] = fs.getFuzzyLimit(3);
                
            }else if(numVars == 2){
                if(left){
                    genome[pos] = fs.getFuzzyLimit(2);
                    genome[pos+1] = fs.getFuzzyLimit(3);
                }else{//Must be right
                    genome[pos] = fs.getFuzzyLimit(0);
                    genome[pos+1] = fs.getFuzzyLimit(1);
                }
            }
            
            pos += numVars;
        }
        return genome;
    }
    
    public void printGenome() {
        //TO TEST
        //double genome[] = getPrintableGenome();
        //for(int i = 0; i < genome_length; i++) System.out.print(genome[i] + " ");
        //System.out.println();
        
        FuzzyManagerIterator fmi = _individual.getIterator();
        fmi.init();
        FuzzySet fs;
        int which = -1;
        
        while(fmi.hasMoreElements()){
            fs = fmi.next();
            which++;
            String pair[] = FuzzyConstants.FUZZY_RELATIONS[fmi._pointer];
            
            System.out.print(EvolutionConstants.TYPE_FUZZY_MANAGER + " " + which + " " + pair[0] + " " + pair[1]);
            for(int i = 0; i < 4; i++)
            {
                System.out.print(" " + fs.getFuzzyLimit(i));
            }
            System.out.println();

        }
        
    }

    //CROSSED INDIVIDUAL: THIS
    public FuzzyManager cross(FuzzyManager another, GAParams params) {
        FuzzyManager newOne = new FuzzyManager();
        
        FuzzyManagerIterator fmi1 = _individual.getIterator();
        FuzzyManagerIterator fmi2 = another.getIterator();
        FuzzyManagerIterator fmiNew = newOne.getIterator();
        fmi1.init();
        fmi2.init();
        fmiNew.init();
        
        FuzzySet fs1, fs2, fsNew;
        
        while(fmi1.hasMoreElements()){
            fs1 = fmi1.next();
            fs2 = fmi2.next();
            fsNew = fmiNew.next();
        
            double dice = Math.random();
            if(dice >= 0.5f){
                //FROM PARENT 1
                fsNew.setFuzzyLimit(0, fs1.getFuzzyLimit(0));
                fsNew.setFuzzyLimit(1, fs1.getFuzzyLimit(1));
                fsNew.setFuzzyLimit(2, fs1.getFuzzyLimit(2));
                fsNew.setFuzzyLimit(3, fs1.getFuzzyLimit(3));
            }else{
                //FROM PARENT 2
                fsNew.setFuzzyLimit(0, fs2.getFuzzyLimit(0));
                fsNew.setFuzzyLimit(1, fs2.getFuzzyLimit(1));
                fsNew.setFuzzyLimit(2, fs2.getFuzzyLimit(2));
                fsNew.setFuzzyLimit(3, fs2.getFuzzyLimit(3));
            }
        }
        
        return newOne;
    }

	//Mutates a value within parameters
    private double localMutate(double value, double min, double max, double prob, double precision){
        DecimalFormat df = new DecimalFormat("0.000");
        double dice = Math.random();
        if(dice <= prob){
            
			//Calculate change values
            int mux = 1 + (int)(Math.random() * 4); //1 .. 4
            int sign = (Math.random() >= 0.5f)? 1 : -1; //can be '+' or '-'
            float mutation = mux * sign * (float)precision;
            
            //DO mutate
            value += mutation;
            
            //Adjust to limits
            if(value < min) value = min;
            if(value > max) value = max;

        }
        return value;
    } 
    
	//Mutates all fuzzy sets
    public FuzzyManager mutate(GAParams params) {
        
        FuzzyManagerIterator fmi = _individual.getIterator();
        fmi.init();
        FuzzySet fs;
        double prob = params.getMutation();
        
        while(fmi.hasMoreElements()){
            fs = fmi.next();
            int numVars = fs.getNumVars();
            boolean left = fs.isLeft();
            double boundaries[] = fs.getMinMax();
            //double range = boundaries[1] - boundaries[0]; 
            double precision = fs.getMutPrecision();           
            
            if(numVars == 0){
                //SKIP THIS.
            }else if(numVars == 4){
                //CENTER
                double new0 = localMutate(fs.getFuzzyLimit(0), boundaries[0] /*MIN*/, fs.getFuzzyLimit(1), prob, precision);   
                double new1 = localMutate(fs.getFuzzyLimit(1), new0, fs.getFuzzyLimit(2), prob, precision);
                double new2 = localMutate(fs.getFuzzyLimit(2), new1, fs.getFuzzyLimit(3), prob, precision);
                double new3 = localMutate(fs.getFuzzyLimit(3), new2, boundaries[1] /*MAX*/, prob, precision);
                
                fs.setFuzzyLimit(0, new0);
                fs.setFuzzyLimit(1, new1);
                fs.setFuzzyLimit(2, new2);
                fs.setFuzzyLimit(3, new3);
            }else if(numVars == 2){
                if(left){
                
                    double new2 = localMutate(fs.getFuzzyLimit(2), boundaries[0] /*MIN*/, fs.getFuzzyLimit(3), prob, precision);
                    double new3 = localMutate(fs.getFuzzyLimit(3), new2, boundaries[1] /*MAX*/, prob, precision);
                    fs.setFuzzyLimit(2, new2);
                    fs.setFuzzyLimit(3, new3);
                    
                }else{//must be right
                    
                    double new0 = localMutate(fs.getFuzzyLimit(0), boundaries[0] /*MIN*/, fs.getFuzzyLimit(1), prob, precision);   
                    double new1 = localMutate(fs.getFuzzyLimit(1), new0, boundaries[1] /*MAX*/, prob, precision);
                    fs.setFuzzyLimit(0, new0);
                    fs.setFuzzyLimit(1, new1);
                }
            }
            
        }
        return _individual;
    }
    
	//Craetes a mutated fuzzy set from a base individual.
    public FuzzyManager createMutated(FuzzyManager base, GAParams params){
        
        FuzzyManagerIterator fmi1 = base.getIterator();
        
        FuzzyManager newOne = new FuzzyManager();
        newOne.createFuzzySets();
        FuzzyManagerIterator fmi2 = newOne.getIterator();
        
        fmi1.init();
        fmi2.init();
        FuzzySet fs1, fs2;
        double prob = params.getMutation();
        
        while(fmi1.hasMoreElements()){
            fs1 = fmi1.next();
            fs2 = fmi2.next();
            
            int numVars = fs1.getNumVars();
            boolean left = fs1.isLeft();
            double boundaries[] = fs1.getMinMax();
            //double range = boundaries[1] - boundaries[0]; 
            double precision = fs1.getMutPrecision();           

            if(numVars == 0){
                //SKIP THIS.
            }else if(numVars == 4){
                //CENTER
                double new0 = localMutate(fs1.getFuzzyLimit(0), boundaries[0] /*MIN*/, fs1.getFuzzyLimit(1), prob, precision);   
                double new1 = localMutate(fs1.getFuzzyLimit(1), new0, fs1.getFuzzyLimit(2), prob, precision);
                double new2 = localMutate(fs1.getFuzzyLimit(2), new1, fs1.getFuzzyLimit(3), prob, precision);
                double new3 = localMutate(fs1.getFuzzyLimit(3), new2, boundaries[1] /*MAX*/, prob, precision);
                
                fs2.setFuzzyLimit(0, new0);
                fs2.setFuzzyLimit(1, new1);
                fs2.setFuzzyLimit(2, new2);
                fs2.setFuzzyLimit(3, new3);
            }else if(numVars == 2){
                if(left){
                
                    double new2 = localMutate(fs1.getFuzzyLimit(2), boundaries[0] /*MIN*/, fs1.getFuzzyLimit(3), prob, precision);
                    double new3 = localMutate(fs1.getFuzzyLimit(3), new2, boundaries[1] /*MAX*/, prob, precision);
                    fs2.setFuzzyLimit(2, new2);
                    fs2.setFuzzyLimit(3, new3);
                      
                }else{//must be right
                    
                    double new0 = localMutate(fs1.getFuzzyLimit(0), boundaries[0] /*MIN*/, fs1.getFuzzyLimit(1), prob, precision);   
                    double new1 = localMutate(fs1.getFuzzyLimit(1), new0, boundaries[1] /*MAX*/, prob, precision);
                    fs2.setFuzzyLimit(0, new0);
                    fs2.setFuzzyLimit(1, new1);
                }
            }
            
        }
        return newOne;
        
    }
}
