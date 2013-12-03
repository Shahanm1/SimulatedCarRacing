package champ2009client.fuzzy;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import java.util.Enumeration;
import java.util.Hashtable;

/**
 * Represents a fuzzy proposition, for instance "track position"
 * Each fuzzy proposition can have one or more fuzzy sets (for instance "left", "center", "right")
 * @author Diego
 */
public class FuzzyProposition {

    private int _numSets;
    private Hashtable<String,FuzzySet> _sets;
    private String TEXT;
    private double _min;
    private double _max;
    
    
    public FuzzyProposition(String propositionText, double min, double max){
        _numSets = 0; 
        _sets = new Hashtable<String,FuzzySet>();  
        TEXT = propositionText;
        _min = min;
        _max = max;
    }
    
    //Adds a new fuzzy set to this proposition
    public void addFuzzySet(FuzzySet set){
        _numSets++;
        set.setMinMax(_min, _max);
        set.initDeffuzifier();
        _sets.put(set.getText(), set);
    }
    
    public FuzzySet getFuzzySet(String text){
        return _sets.get(text);
    }
    
    public void testFuzzySets(){
        Enumeration<FuzzySet> elements = _sets.elements();
        
        while(elements.hasMoreElements()){
            FuzzySet fs = elements.nextElement();
            System.out.println("Testing " + TEXT + ": " + fs.getText());
            for(double x = _min; x < _max; x+=0.1){
                System.out.println ("  x = " + x + "; truth = " + fs.truth_value(x) + " crispy: " + fs.getCrispValue());
            }
        }
    }
    
    
}


