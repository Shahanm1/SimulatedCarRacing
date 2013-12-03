/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package champ2009client.evolution;

import champ2009client.fuzzy.FuzzyManager;
import java.text.DecimalFormat;

/**
 * This "capsule" encapsulates the data that can be mutated and crossed by de ev. algorithm
 * @author Diego
 */
public class GeneticCapsule {

	//Capsule data.
    private String  _name;
    private String  _surname;
    private Object  _data;
    private int     _type;
    
    public GeneticCapsule()
    {
        _name = "name";
        _surname = "surname";
        _data = null;
        _type = EvolutionConstants.TYPE_UNKNOWN;
    }
    
    public GeneticCapsule(String name, String surname)
    {
        _name  = name;
        _surname = surname;
    }
 
    
    public void printGenome()
    {
        double value = ((Double)_data).doubleValue();
        System.out.println(_type + " " + _name + " " + _surname + " " + (float)value);
    }
    
    public GeneticCapsule getCopy()
    {
        GeneticCapsule cap = new GeneticCapsule(_name, _surname);
        cap.setData(_data, _type);
        return cap;
    }
    
	//Performs a mutation on the data, receiving valuek, limits, mutation probability and precision of change.
    private double localMutate(double value, double min, double max, double prob, double precision){
        DecimalFormat df = new DecimalFormat("0.000");
        double dice = Math.random();
        double init_value = value;
        if(dice <= prob){
            //mutate
            int mux = 1 + (int)(Math.random() * 4); //1 .. 4
            int sign = (Math.random() >= 0.5f)? 1 : -1; //can be '+' or '-'
            float mutation = mux * sign * (float)precision;
            
            //DO mutate
            value += mutation;
            
            //Check limits
            if(value < min) value = min;
            if(value > max) value = max;
            
            if(EvolutionManager.DEBUG)
                System.out.println(_type + ", " + _name + ", " + _surname + ". VAL " + init_value + " => " + value);
            _data = new Double(value);
            
            
        }
        return value;
    } 
    

	//Performs the mutation
    public void mutate(GAParams params)
    {
        double min = 0.0f, max = 0.0f, precision = 0.0f;
		//Establishes the parameters for a mutation (precision of change and limits) depending on capsule's type.
        switch(_type)
        {
            case EvolutionConstants.TYPE_FUZZY_MANAGER:
                FuzzyManager fm = (FuzzyManager) _data;
                FuzzyManager newData = fm.createMutated(fm, params);
                _data = newData;
                break;
            case EvolutionConstants.TYPE_MID_ANGLE:
                max = Math.PI/2.0f;
                precision = 0.05f;
                break;
            case EvolutionConstants.TYPE_ACCEL:
                max = 1.0f;
                precision = 0.05f;
                break;
            case EvolutionConstants.TYPE_MID_TURN:
                max = 1.0f;
                precision = 0.05f;
                break;
            case EvolutionConstants.TYPE_TIME:
                max = 10.0f;
                precision = 0.1f;
                break;
            case EvolutionConstants.TYPE_DISTANCE:
                max = 100.0f;
                precision = 1.0f;
                break;
        }
        
        //FOR ALL TYPES (different than FUZZY_MANAGER)
        if(_type != EvolutionConstants.TYPE_FUZZY_MANAGER)
        {
            double init_value = ((Double)_data).doubleValue();
            localMutate(init_value,min,max,params.getMutation(),precision);
        }

    }
    
	//Performs crossover between this and another capsule received.
    public GeneticCapsule cross(GeneticCapsule other, GAParams params) throws Exception
    {
        GeneticCapsule newCap;
		//Capsules 
        if(_type != other.getType())
        {
            throw new Exception("INCOMPATIBLE CAPSULES TYPE");
        }
        
		//Fuzzy manager is special (Fuzzy sets manage themselves their crossover and mutation).
        if( _type == EvolutionConstants.TYPE_FUZZY_MANAGER)
        {
            FuzzyManager otherFm = (FuzzyManager) other.getData();
            FuzzyManager thisFm  = (FuzzyManager) _data;
            FuzzyManager newFm  = thisFm.cross(otherFm, params);

            newCap = new GeneticCapsule(_name,_surname); //both the same in this and other capsules
            newCap.setData(newFm, EvolutionConstants.TYPE_FUZZY_MANAGER);
            
        }else
        {
			//Crossover is done with 0.5 prob for each parent.
            double dice = Math.random();
            if(dice >= 0.5f){
                //FROM PARENT 1  (this)
                Double data = new Double(((Double)_data).doubleValue());
                newCap = new GeneticCapsule(_name,_surname);
                newCap.setData(data, _type);
            }else
            {
                //FROM PARENT 2  (other)
                Double data = new Double(((Double)other.getData()).doubleValue());
                newCap = new GeneticCapsule(other.getName(),other.getSurname());
                newCap.setData(data, other.getType());
            }
        }
        
        return newCap;
    }
    
    
	public void setData(Object data, int dataType)
	{
		_data = data;
		_type = dataType;
	}

	public Object getData() 
	{
		return _data;
	}

	public String getName() 
	{
		return _name;
	}

	public String getSurname() 
	{
		return _surname;
	}

	public int getType() 
	{
		return _type;
	}   


}
