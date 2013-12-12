package champ2009client.fuzzy;
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Diego
 */
public class FuzzyConstants {

    //FITNESS COMPARISION (TRUE if fit1 is BETTER than fit2)
    static boolean compareFitness(double fit1, double fit2, boolean minimize){
        if(minimize && fit1<fit2)  return true;
        if(!minimize && fit1>fit2) return true;
        return false;
    }
        
    //TYPES of FUZZY SETS
    final static int TRAPEZOIDAL_LEFT = 0;
    final static int TRAPEZOIDAL_CENTER = 1;
    final static int TRAPEZOIDAL_RIGHT = 2;
    final static int SMOOTH_TRAPEZOIDAL_LEFT = 3;
    final static int SMOOTH_TRAPEZOIDAL_CENTER = 4;
    final static int SMOOTH_TRAPEZOIDAL_RIGHT = 5;
    final static int STEP_LEFT = 6; //(not fuzzy)
    final static int STEP_CENTER = 7; //(not fuzzy)
    final static int STEP_RIGHT = 8; //(not fuzzy)
    
    //TYPES of DEFF. METHODS
    final static int CENTER_OF_GRAVITY = 0;
    final static int ABSOLUTE = 1; //(not fuzzy)
    
    //VALUE FOR ABSOLUTE SETS
    final static int STEP_ABSOLUTE_VALUE = 2;
    
    //FUZZY SETS INDEXES
    public final static int NUM_FUZZY_SETS = 17;
    public final static int INDEX_ANGLE_CENTERED = 0;
    public final static int INDEX_ANGLE_ORIENTED_SOFT = 1;
    public final static int INDEX_ANGLE_ORIENTED_HARD = 2;
    public final static int INDEX_ANGLE_BACKWARDS = 3;
    
    public final static int INDEX_TRACKPOS_CENTERED = 4;
    public final static int INDEX_TRACKPOS_SIDE = 5;
    public final static int INDEX_TRACKPOS_OUTSIDE = 6;
    
    public final static int INDEX_SPEED_SLOW = 7;
    public final static int INDEX_SPEED_MEDIUM = 8;
    public final static int INDEX_SPEED_FAST = 9;
    public final static int INDEX_SPEED_VERY_FAST = 10;
    
    public final static int INDEX_TRACK_TURN_SOFT = 11;   
    public final static int INDEX_TRACK_TURN_MID = 12;   
    public final static int INDEX_TRACK_TURN_HARD = 13;     
    
    public final static int INDEX_EDGE_VERY_CLOSE = 14;   
    public final static int INDEX_EDGE_CLOSE = 15;  
    public final static int INDEX_EDGE_FAR = 16;     
    
	//All fuzzy sets
    public final static String[][] FUZZY_RELATIONS= {
        {"angle","centered"},
        {"angle","oriented_soft"},
        {"angle","oriented_hard"},
        {"angle","backwards"},
        {"track_position","centered"},
        {"track_position","side"},
        {"track_position","outside"},
        {"speed","slow"},
        {"speed","medium"},
        {"speed","fast"},
        {"speed","very_fast"},
        {"track","turn_soft"},
        {"track","turn_mid"},
        {"track","turn_hard"},
        {"edge","very_close"},
        {"edge","close"},
        {"edge","far"}
    };
    
    public final static int INDEX_ANGLE_SENSOR = 0;
    public final static int INDEX_TRACKPOS_SENSOR = 1;
    public final static int INDEX_SPEED_SENSOR = 2;
    public final static int INDEX_TURN_SENSOR = 3;
    public final static int INDEX_EDGE_SENSOR = 4;
    
	//All fuzzy sensors
    public final static String[] FUZZY_SENSORS= {
        "angle",
        "track_position",
        "speed",
        "track",
        "edge"
    };
    
    
    public final static double DISTANCE_FOR_OPPONENT = 7.5f;
    public final static double ANGLE_ADJUSTMENT = 0.15f;      
}

    