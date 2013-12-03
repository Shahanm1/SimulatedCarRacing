/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 * -- jFuzzyLogic --
 */

package champ2009client.fuzzy;

/**
 *
 * @author Diego
 */
public class Deffuzifier {
    
    /** number of points for 'values[]' */
    public static int NUMBER_OF_POINTS = 1000;

    double _max;
    double _min;

    // stepSize = (max - min) / values.length 
    double _stepSize;

    /** 
     * Funcion values: A generic continuous function
     * 	y = f(x)
     * where x : [min, max] 
     * Values are stored in 'values[]' array.
     * Array's index is calculated as: 
     * 	index = (x - min) / (max - min) * (values.length)
     */
    double _values[];
        
        
    public Deffuzifier(double min, double max){
        _max = max;
        _min = min;
        _stepSize = (max-min)/NUMBER_OF_POINTS;
        _values = new double[NUMBER_OF_POINTS];
        reset();
    }
    
    public void reset(){
        for(int i = 0; i < NUMBER_OF_POINTS; i++) _values[i] = 0;        
    }
    
    
    public void setValue(int index, double value) {
	_values[index] = value;
    }
    
    public double getValue(int index) {
        return _values[index];
    }
    
    
    //Center of gravity deffuzification method
    public double cog(){
        double x = _min, sum = 0, weightedSum = 0;

        // Calculate integrals (approximated as sums)
        for( int i = 0; i < _values.length; i++, x += _stepSize ) {
                sum += _values[i];
                weightedSum += x * _values[i];
        }

        // No sum? => this variable has no active antecedent
        if( sum <= 0 ) return Double.NaN;

        // Calculate center of gravity
        double cog = weightedSum / sum;
        return cog;
    }

}
