/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package champ2009client.fuzzy;


import champ2009client.evolution.GAParams;
import java.util.Hashtable;


/**
 *
 * @author Diego
 */
public class FuzzyManager {

    private Hashtable<String,FuzzyProposition> _propositions;
    private FuzzyDecorator _decorator;
    private FuzzyManagerIterator _setIterator;
    
    public FuzzyManager(){
        _propositions = new Hashtable<String, FuzzyProposition>();
        createFuzzySets();
        _setIterator = new FuzzyManagerIterator(this, true);
        _decorator = new FuzzyGADecorator(this);
    }
    
    public void createSetsFromFile(){
        _decorator.readIndividual();
    }
    
    public void createFuzzySets(){

        //Sensors
        createFuzzyAngle();
        createFuzzySpeed();
        createFuzzyTrackPosition();
        createFuzzyTrack();
        createFuzzyEdge();
     //   createFuzzyOverhead();
        
    }
    
    /* ANGLE PROPOSITION AND FUZZY SETS*/
    private void createFuzzyAngle(){
        
        FuzzyProposition fp = new FuzzyProposition("angle", 0, Math.PI);
        //Fuzzy sets associated
        FuzzySet f0 = new FuzzySet(FuzzyConstants.STEP_RIGHT, FuzzyConstants.ABSOLUTE, "backwards");
        FuzzySet f1 = new FuzzySet(FuzzyConstants.SMOOTH_TRAPEZOIDAL_RIGHT, FuzzyConstants.CENTER_OF_GRAVITY, "oriented_hard");
        FuzzySet f2 = new FuzzySet(FuzzyConstants.SMOOTH_TRAPEZOIDAL_CENTER, FuzzyConstants.CENTER_OF_GRAVITY, "oriented_soft");
        FuzzySet f3 = new FuzzySet(FuzzyConstants.SMOOTH_TRAPEZOIDAL_LEFT, FuzzyConstants.CENTER_OF_GRAVITY, "centered");

        //Initializations (STABLE 3)
        f0.initialize(Math.PI/2.0f,0,0,0);
        f1.initialize(0.15, 0.2, 0, 0);
        f2.initialize(0.07, 0.08,0.4,0.65);
        f3.initialize(0, 0, 0.09, 0.019);
                        
        f0.setMutPrecision(0.05f);
        f1.setMutPrecision(0.05f);
        f2.setMutPrecision(0.05f);
        f3.setMutPrecision(0.05f);        
        
        fp.addFuzzySet(f0);
        fp.addFuzzySet(f1);
        fp.addFuzzySet(f2);
        fp.addFuzzySet(f3);
        
        _propositions.put("angle", fp);
    }
    
    private void createFuzzyTrackPosition(){
        
        FuzzyProposition fp = new FuzzyProposition("track_position", 0, 1.1f);
        //Fuzzy sets associated
        
        //-1 means RIGHT EDGE
        FuzzySet f1 = new FuzzySet(FuzzyConstants.STEP_RIGHT, FuzzyConstants.ABSOLUTE, "outside");
        FuzzySet f2 = new FuzzySet(FuzzyConstants.SMOOTH_TRAPEZOIDAL_RIGHT, FuzzyConstants.CENTER_OF_GRAVITY, "side");
        FuzzySet f3 = new FuzzySet(FuzzyConstants.SMOOTH_TRAPEZOIDAL_LEFT, FuzzyConstants.CENTER_OF_GRAVITY, "centered");
        
        //Initializations

        f1.initialize(1.1f, 0, 0.1, 0.1);
        f2.initialize(0.2, 0.35, 0, 0);
        f3.initialize(0, 0, 0.1, 0.1);
        
        f1.setMutPrecision(0.05f);
        f2.setMutPrecision(0.05f);
        f3.setMutPrecision(0.05f);        
                
        fp.addFuzzySet(f1);
        fp.addFuzzySet(f2);
        fp.addFuzzySet(f3);
        
        _propositions.put("track_position", fp);
    }
    
    
    //speedX
    private void createFuzzySpeed(){
        
        FuzzyProposition fp = new FuzzyProposition("speed", 0, 350);
        //Fuzzy sets associated
        FuzzySet f1 = new FuzzySet(FuzzyConstants.SMOOTH_TRAPEZOIDAL_LEFT, FuzzyConstants.CENTER_OF_GRAVITY, "slow");
        FuzzySet f2 = new FuzzySet(FuzzyConstants.SMOOTH_TRAPEZOIDAL_CENTER, FuzzyConstants.CENTER_OF_GRAVITY, "medium");
        FuzzySet f3 = new FuzzySet(FuzzyConstants.SMOOTH_TRAPEZOIDAL_CENTER, FuzzyConstants.CENTER_OF_GRAVITY, "fast");
        FuzzySet f4 = new FuzzySet(FuzzyConstants.SMOOTH_TRAPEZOIDAL_RIGHT, FuzzyConstants.CENTER_OF_GRAVITY, "very_fast");
   
        //Initializations
        f1.initialize(0, 0, 80, 100);
        f2.initialize(90,110,150,160);
        f3.initialize(150,180,223,242);
        f4.initialize(227,248,0,0);
        
        f1.setMutPrecision(2.0f);
        f2.setMutPrecision(2.0f);
        f3.setMutPrecision(2.0f);        
        f4.setMutPrecision(2.0f);
        
        fp.addFuzzySet(f1);
        fp.addFuzzySet(f2);
         fp.addFuzzySet(f3);
        fp.addFuzzySet(f4);
        
        _propositions.put("speed", fp);
    }
    
     private void createFuzzyTrack(){
        
        FuzzyProposition fp = new FuzzyProposition("track", 0, 100);
        //Fuzzy sets associated
        FuzzySet f1 = new FuzzySet(FuzzyConstants.SMOOTH_TRAPEZOIDAL_RIGHT, FuzzyConstants.CENTER_OF_GRAVITY, "turn_soft");
        FuzzySet f2 = new FuzzySet(FuzzyConstants.SMOOTH_TRAPEZOIDAL_CENTER, FuzzyConstants.CENTER_OF_GRAVITY, "turn_mid");
        FuzzySet f3 = new FuzzySet(FuzzyConstants.SMOOTH_TRAPEZOIDAL_LEFT, FuzzyConstants.CENTER_OF_GRAVITY, "turn_hard");

        f1.initialize(75.0, 88.0, 0, 0);
        f2.initialize(69.0, 73.0, 82.0, 83.0);
        f3.initialize(0, 0, 74.0, 80.0);
        
        f1.setMutPrecision(1.0f);
        f2.setMutPrecision(1.0f);
        f3.setMutPrecision(1.0f);
        
        fp.addFuzzySet(f1);
        fp.addFuzzySet(f2);
        fp.addFuzzySet(f3);
        
        _propositions.put("track", fp);
    }
    
     
     private void createFuzzyEdge(){
        
        FuzzyProposition fp = new FuzzyProposition("edge", 0, 100);
        //Fuzzy sets associated
        FuzzySet f1 = new FuzzySet(FuzzyConstants.SMOOTH_TRAPEZOIDAL_LEFT, FuzzyConstants.CENTER_OF_GRAVITY, "very_close");
        FuzzySet f2 = new FuzzySet(FuzzyConstants.SMOOTH_TRAPEZOIDAL_CENTER, FuzzyConstants.CENTER_OF_GRAVITY, "close");
        FuzzySet f3 = new FuzzySet(FuzzyConstants.SMOOTH_TRAPEZOIDAL_RIGHT, FuzzyConstants.CENTER_OF_GRAVITY, "far");

        f1.initialize(0, 0, 7.0, 24.0);
        f2.initialize(18.0, 19.0, 24.0, 37.0);
        f3.initialize(25.0, 39.0, 0, 0);
        
        f1.setMutPrecision(1.0f);
        f2.setMutPrecision(1.0f);
        f3.setMutPrecision(1.0f);
        
        fp.addFuzzySet(f1);
        fp.addFuzzySet(f2);
        fp.addFuzzySet(f3);
        
        _propositions.put("edge", fp);
    }
     
     /*
     private void createFuzzyOverhead(){
        
        FuzzyProposition fp = new FuzzyProposition("car on", 0, 100);
        //Fuzzy sets associated
        FuzzySet f1 = new FuzzySet(FuzzyConstants.SMOOTH_TRAPEZOIDAL_LEFT, FuzzyConstants.CENTER_OF_GRAVITY, "left");
        FuzzySet f2 = new FuzzySet(FuzzyConstants.SMOOTH_TRAPEZOIDAL_LEFT, FuzzyConstants.CENTER_OF_GRAVITY, "center");
        FuzzySet f3 = new FuzzySet(FuzzyConstants.SMOOTH_TRAPEZOIDAL_LEFT, FuzzyConstants.CENTER_OF_GRAVITY, "right");
        
        f1.initialize(0,0,10.0,22.0);
        f2.initialize(0,0,10.0,22.0);
        f3.initialize(0,0,10.0,22.0);
        
        fp.addFuzzySet(f1);
        fp.addFuzzySet(f2);
        fp.addFuzzySet(f3);
        
        _propositions.put("car on", fp);
    }
    */ 
     
    public void testFuzzyPropositions(){
        FuzzyProposition fp = _propositions.get("accel");
        fp.testFuzzySets();
    }
    
    public FuzzySet getFuzzySet(String prop, String set){
        FuzzyProposition fp = _propositions.get(prop);
        return fp.getFuzzySet(set);
    }
    
    public double getTruthValue(String prop, String set, double x){
        FuzzySet fs = getFuzzySet(prop, set);
        return fs.truth_value(x);
    }
    
    public double getCrispValue(String prop, String set){
        FuzzySet fs = getFuzzySet(prop, set);
        return fs.getCrispValue();
    }
    
    public FuzzyManagerIterator getIterator(){
        return _setIterator;
    }
    
    /*****************************/
    /* DECORATOR METHODS FOR GA. */
    /*****************************/
    public void setFitness (double f){
        _decorator.setFitness(f);
    }
    
    public void setFitnessStr (String f){
        _decorator.setFitnessStr(f);
    }
    
    public String getFitnessStr(){
        return _decorator.getFitnessStr();
    }
    
    public double getFitness(){
        return _decorator.getFitness();
    }
    
    public void setEmergencyBan(int value){
        _decorator.setBan(value);
    }
    
    public void addEmergencyBan(){
        _decorator.addBan();
    }
    
    public int getEmergencyBan(){
        return _decorator.getBan();
    }

    public void setDirty(boolean d){
       _decorator.setDirty(d);
    }

    public boolean isDirty(){
       return _decorator.isDirty();
    }
   
    public void setGenome(double[] gen){
        _decorator.setReadGenome(gen);
    }
    
    public double[] getGenome(){
        return _decorator.getPrintableGenome();
    }
    
    public void printGenome(){
        _decorator.printGenome();
    }
    
    public FuzzyManager cross(FuzzyManager another, GAParams params){
        return _decorator.cross(another, params);
    }
    
    
    public FuzzyManager createMutated(FuzzyManager base, GAParams params){
        return _decorator.createMutated(base, params);
    }
    
    
    
    //CREATED FOR CEC2009
    public FuzzyManager mutate(GAParams params){
        return _decorator.mutate(params);
    }
    
    /***********************************************/

    
    
    public static void main(String args[]){
        FuzzyManager fm = new FuzzyManager();
        fm.createFuzzySets();
        fm.testFuzzyPropositions();
    }
    
}
