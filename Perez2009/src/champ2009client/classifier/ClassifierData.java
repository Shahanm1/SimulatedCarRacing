/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package champ2009client.classifier;

/**
 *
 * @author Diego
 */
public class ClassifierData {

	//data to do classifying
    public double[]    trackSensors;
    public double      trackPosition;
    public double      angleAxis;
    
	//Class of data
    public String      mClass;
	//This works as a dirty bit
    public boolean     valid; 
    
    public ClassifierData()
    {
        trackSensors = new double[19];
        trackPosition = -100.0f;
        valid = false;
    }
    
}
