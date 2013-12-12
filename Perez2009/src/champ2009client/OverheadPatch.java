/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package champ2009client;
import champ2009client.fuzzy.*;

/**
 *
 * @author Diego
 */
public class OverheadPatch {

    private FuzzyFileHandler _ffh;
    private final int NUM_VARIABLES_PREPATCH = 38;
    
    public OverheadPatch() {
        _ffh = new FuzzyFileHandler();
    }

    public void patch(){
        
        double gen_old[] = _ffh.readFileIndividual();

        double new_gen[] = new double[gen_old.length + 6];
        for(int i = 0; i < NUM_VARIABLES_PREPATCH; i++){
            new_gen[i] = gen_old[i]; 
        }

        new_gen[NUM_VARIABLES_PREPATCH]   = 10.0f; 
        new_gen[NUM_VARIABLES_PREPATCH+1] = 22.0f; 
        new_gen[NUM_VARIABLES_PREPATCH+2] = 10.0f; 
        new_gen[NUM_VARIABLES_PREPATCH+3] = 22.0f; 
        new_gen[NUM_VARIABLES_PREPATCH+4] = 10.0f; 
        new_gen[NUM_VARIABLES_PREPATCH+5] = 22.0f; 
        
        for(int i = NUM_VARIABLES_PREPATCH+6, j=NUM_VARIABLES_PREPATCH ; i < new_gen.length; i++, j++){
            new_gen[i] = gen_old[j]; 
        }
        
        _ffh.printFileIndividual(new_gen);
        
    }
    
    

    public static void main(String args[]){
        OverheadPatch op = new OverheadPatch();
        op.patch();
    }

}
