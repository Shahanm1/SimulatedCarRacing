/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package champ2009client.classifier;

/**
 *
 * @author Diego
 */
public class ClassifierConstants {

    //Num track in classifying
    public static final int NUM_TRACK_SENSORS = 7;
    
    //Threshold 
    public static final int CLASS_THRESHOLD = 25;

    //Classes
    public static final int CLASS_STRAIGHT = 0;
    public static final int CLASS_PRE_TURN = 1;
    public static final int CLASS_TURN = 2;
    public static final int NUM_CLASSES = 3;
    
    //FLAGS
    public static final int FLAG_NORMAL = 0;
    public static final int FLAG_RIGHT = 1;
    public static final int FLAG_LEFT = 2;
    public static final int FLAG_TURN_ON_LEFT = 3;
    public static final int FLAG_TURN_ON_RIGHT = 4;
    public static final int FLAG_TURN_UNDEFINED = 5;
}
