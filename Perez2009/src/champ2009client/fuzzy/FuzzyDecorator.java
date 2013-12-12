/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package champ2009client.fuzzy;

import champ2009client.evolution.GAParams;

/**
 * Decorator for fuzzy evolutionay operators
 * @author Diego
 */
public abstract class FuzzyDecorator {
    
    FuzzyManager _individual;
    double _fitness;
    int _ban;
    String _fitnessStr;
    boolean _dirty;
    
    public abstract FuzzyManager cross(FuzzyManager another, GAParams params);

    public abstract FuzzyManager mutate(GAParams params);

    public abstract FuzzyManager createMutated(FuzzyManager base, GAParams params);
    
    public abstract void printGenome();

    public abstract double[] getPrintableGenome();

    public abstract void setReadGenome(double[] gen);

    public abstract void readIndividual();
    
    public void setFitnessStr(String f) {
        _fitnessStr = f;
    }

    public String getFitnessStr() {
        return _fitnessStr;
    }
    
    public void setFitness(double f) {
        _fitness = f;
    }

    public double getFitness() {
        return _fitness;
    }
    
    public void setBan(int value){
        _ban = value;
    }
    
    public int getBan(){
        return _ban;
    }
    
    public void addBan(){
        _ban++;
    }
    
    public void setDirty(boolean d){
        _dirty = d;
    }
    
    public boolean isDirty(){
        return _dirty;
    }
}
