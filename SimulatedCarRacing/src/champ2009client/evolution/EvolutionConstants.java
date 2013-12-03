/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package champ2009client.evolution;

/**
 *
 * @author Diego
 */
public class EvolutionConstants {

	//Type of genetic capsule.
    public static final int TYPE_UNKNOWN = 0;
    public static final int TYPE_MID_ANGLE = 1;     //From 0.0 to PI/2  (prec: 0.05)
    public static final int TYPE_ACCEL = 2;         //From 0.0 to 1.0   (prec: 0.05)
    public static final int TYPE_MID_TURN = 3;      //From 0.0 to 1.0   (prec: 0.05)
    public static final int TYPE_TIME = 4;          //From 0.0 to 10.0  (prec: 0.1)
    public static final int TYPE_DISTANCE = 5;      //From 0.0 to 100.0 (prec: 1.0)
    public static final int TYPE_FUZZY_MANAGER = 6; //For fuzzy managers
    
	//Genetic individual size
    public static final int NUM_CAPSULES = 12;
    
	//Fitness type
    public static final int FITNESS_DIST_RACED = 0;
    public static final int FITNESS_ACCIDENTS = 1;
    public static final int FITNESS_POINTS = 2;
    
	//Objectives for NSGA2
	public static final int NUM_NSGA2_OBJ = 2;

	//VALUES FOR FITNESS
    public static final int[] POINTS = {10,8,6,5,4,3,2,1};
    public static final double[] DISTANCE = {3147.47 * 5, 3260.43 * 5, 4103.84 * 5, 2587.54 * 5};
}
