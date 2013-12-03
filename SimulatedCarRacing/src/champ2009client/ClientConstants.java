/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package champ2009client;

/**
 * Constants for different execution modes 
 * @author Diego
 */
public class ClientConstants {

    public static final int EXE_GO = 0;                     //NORMAL DRIVING, NO ENDS
    public static final int EXE_GA = 1;                     //EXECUTE GA
    public static final int EXE_WRITE_BASE_FILE = 2;        //TO WRITE BASE FILE
    public static final int EXE_PREPARE_INITIAL_GRID = 3; //TO READ POP FILE AND WRITE GA BASE FILE
    public static final int EXE_RUN = 4;                    //TO RACE (ONE CONFIG ONLY)
    public static final int EXE_RUN_COMBI = 5;              //TO RACE (TWO CONFIGs)
    public static final int EXE_WRITE_GRID_FILES = 6;        //TO READ POP FILE AND WRITE ONE FILE PER INDIVIDUAL


    //COMBI:
    public static final int NORMAL_INDIVIDUAL = 0;
    public static final int SECURE_INDIVIDUAL = 1;

}

