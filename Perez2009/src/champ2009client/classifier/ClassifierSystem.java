/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package champ2009client.classifier;
import champ2009client.SensorModel;
import champ2009client.automata.StateManager;

/**
 *
 * @author Diego
 */
public class ClassifierSystem {

    private boolean DEBUG = false;
    
	//Classifier itself
    private Classifier      _classifier;

	//Reference to state manager.
    private StateManager    _automata;

	//Class and stuff
    private int _currentClass;
    private int _confiableClass;
    private int _classifierFlag;
    
    private int _buffer[];
    private int _classRank[];
    private double _sensorAngles[];
    private double _dataTrack[]; //track sensors treated
    private double _opponentAngles[];
    
    public ClassifierSystem()
    {
        _classifier = new J48ClassifierP3_T7(); //This is our classifier
        _currentClass = _confiableClass = ClassifierConstants.CLASS_STRAIGHT; //current class by default
        _classifierFlag = ClassifierConstants.FLAG_NORMAL; //To detect anormal things
        
        _buffer = new int[ClassifierConstants.CLASS_THRESHOLD];
        for(int i = 0; i < ClassifierConstants.CLASS_THRESHOLD; i++)
        {
            _buffer[i] = -1;
        }
        
        //Num track sensors size
        _sensorAngles = new double[19];
        double angleVal = - (Math.PI / 2.0f);
        double increment = Math.PI / (_sensorAngles.length-1); //This is the angle between each opponent sensor
        for(int i = 0; i < _sensorAngles.length; i++)
        {
            _sensorAngles[i] = angleVal;
            angleVal += increment;
        }

        
        //Num opponents sensors size
        _opponentAngles = new double[36];
        angleVal = - Math.PI;
        increment = 2*Math.PI / (_opponentAngles.length-1); //This is the angle between each opponent sensor
        for(int i = 0; i < _opponentAngles.length; i++)
        {
            _opponentAngles[i] = angleVal;
            angleVal += increment;
        }
        
        _classRank = new int[ClassifierConstants.NUM_CLASSES];
        _dataTrack = new double[ClassifierConstants.NUM_TRACK_SENSORS];
    }
        
	//Takes the track sensors used for classifying from the whole set of sensors
	//The N sensors taken are chosen so the sensor N/2 is the one parallel to the track axis
    public double[] transformTrackSensors(double angle, double[] track_sensors)
    {
        int numFinalTrackSensors = ClassifierConstants.NUM_TRACK_SENSORS;
        double[] data = new double[numFinalTrackSensors];
        
        //Get central sensor
        double diff = 2*Math.PI;
        int diffIndex = -1;
        for(int i = 0; i < _sensorAngles.length; i++)
        {
            double diffAux = _sensorAngles[i] + angle;
            if(Math.abs(diffAux) < diff)
            {
                diff = Math.abs(diffAux);
                diffIndex = i;
            }
        }
        
        int perSide = numFinalTrackSensors/2;
        if((diffIndex < perSide) || (diffIndex > (_sensorAngles.length-1 - perSide)))
        {
            return null;
        }else
        {
             for(int j = diffIndex-perSide, i = 0; j <= diffIndex+perSide; j++, i++)
            {
                 data[i] = track_sensors[j];
            }
        }
         
        return data;
    }
    
	//Classify this track stretch depending on sensors
    public void classify(SensorModel sensors)
    {
		//Take the sensors to classify
        double track_sensors[] = sensors.getTrackEdgeSensors();
        Double data[] = new Double[21];
        //data[0] = new Double(sensors.getAngleToTrackAxis());
        data[0] = new Double(sensors.getTrackPosition());
         _dataTrack = transformTrackSensors(sensors.getAngleToTrackAxis(), track_sensors);
        
        if(_dataTrack != null)
        {
            for(int i = 1, j = 0; i < 1 + ClassifierConstants.NUM_TRACK_SENSORS; i++, j++)
            {
                data[i] = new Double( _dataTrack[j]);
            }

            try {
				//Classify and update class
                _currentClass = (int) _classifier.classify(data);
                updateClass();
                
                //study sense for turns (and pre-turns)
                if(_confiableClass != ClassifierConstants.CLASS_STRAIGHT)
                {
                    
                    int maxTrackIndex = _automata.getMaxTrackValIndex();
                    if(maxTrackIndex != -1)
                    {
                        if(maxTrackIndex < 9)
                        {
                            //if(DEBUG) System.out.println(" --> A LA IZQUIERDA. -------------"+maxTrackIndex+"----------");
                            _classifierFlag = ClassifierConstants.FLAG_TURN_ON_LEFT;
                        }else if(maxTrackIndex > 9)
                        {
                           // if(DEBUG) System.out.println(" --> A LA DERECHA. -------------"+maxTrackIndex+"----------");
                            _classifierFlag = ClassifierConstants.FLAG_TURN_ON_LEFT;
                        }
                        else
                        {   
                           // if(DEBUG) System.out.println(" --> INDEFINIDO. -------------"+maxTrackIndex+"----------");
                            _classifierFlag = ClassifierConstants.FLAG_TURN_UNDEFINED;
                        }
                    }else
                    {
                    
                        int medium = ClassifierConstants.NUM_TRACK_SENSORS/2;
                        //if((_dataTrack[medium-1] > _dataTrack[medium]) && (_dataTrack[medium] > _dataTrack[medium+1]))
                        if(_dataTrack[medium-1] > _dataTrack[medium+1])
                        {
                            //left
                           // if(DEBUG) System.out.println(" --> A LA IZQUIERDA. -----------------------");
                            _classifierFlag = ClassifierConstants.FLAG_TURN_ON_LEFT;
                        //}else if((_dataTrack[medium+1] > _dataTrack[medium]) && (_dataTrack[medium] > _dataTrack[medium-1]))
                        }else if(_dataTrack[medium-1] < _dataTrack[medium+1])
                        {
                            //right
                           // if(DEBUG) System.out.println(" --> A LA DERECHA. -----------------------");
                            _classifierFlag = ClassifierConstants.FLAG_TURN_ON_RIGHT;
                        }else
                        {
                           // if(DEBUG) System.out.println(" --> INDEFINIDA" + "(" + _dataTrack[medium-1] + ")" +
                           //         "(" + _dataTrack[medium] + ")" + "(" + _dataTrack[medium+1] + ")" + "-----------------------");
                            _classifierFlag = ClassifierConstants.FLAG_TURN_UNDEFINED;
                        }
                    }
                }else
                {
                   // if(DEBUG) System.out.println("-----------------------");
                    _classifierFlag = ClassifierConstants.FLAG_TURN_UNDEFINED;
                }

            } catch (Exception e) {
                System.out.println(" --> ERROR!!: " + e);
                e.printStackTrace();
            }
        }else
        {
            if(DEBUG) System.out.println("ORIENTATION FLAG ON: " + _classifierFlag);
        }
    }

    //Updates the class. Used after classifying
    private void updateClass()
    {
		//IN _buffer, we store the last CLASS_THRESHOLD classifications.
		//IN _classRank, we store the number of classifications assigned to each class on the last CLASS_THRESHOLD classifications.

        //forget about last element in buffer
        if(_buffer[ClassifierConstants.CLASS_THRESHOLD-1] != -1)
            _classRank[_buffer[ClassifierConstants.CLASS_THRESHOLD-1]]--;
        
        //move elements one position
        for(int i = ClassifierConstants.CLASS_THRESHOLD-2; i >= 0 ; i--)
        {
            _buffer[i+1] = _buffer[i];
        }
        
        //first position for latest class, and mark it.
        _buffer[0] = _currentClass;
        _classRank[_currentClass]++;
        
        
		//KEEP the class with most occurrences on buffer.
        int maxCount = 0;
        for(int i = 0; i < ClassifierConstants.NUM_CLASSES; i++)
        {
            //System.out.print(" " + _classRank[i]);
            if(_classRank[i] > maxCount)
            {
                _confiableClass=i;
                maxCount = _classRank[i];
            }
        }
        
        if(DEBUG) 
        {
            String className = "";
            if(_confiableClass == 0) className = "RECTA";
            else if(_confiableClass == 1) className = "PRE-CURVA";
            else if(_confiableClass == 2) className = "CURVA";
            System.out.println(" ----------------------- CLASS: " + className + "(" + maxCount + ") ");
        }
    }

    // Getters and Setters
	//////////////////////////

	public void setAutomata(StateManager automata) 
	{
		_automata = automata;
	}

    public int getFlag()
    {
        return _classifierFlag;
    }
    
    public int getLastClass()
    {
        return _currentClass;
    }
        
    public int getCurrentClass()
    {
        return _confiableClass;
    }
    
    public double[] getTrackSensors()
    {
        return _dataTrack;
    }    
    
    public double getSensorAngle(int which)
    {
        return _sensorAngles[which];
    }
    
    public double getOpponentAngle(int which)
    {
        return _opponentAngles[which];
    }
    
    public String getClassStr()
    {
      if(_confiableClass == 0) return "RECTA";
      if(_confiableClass == 1) return "PRE-CURVA";
      if(_confiableClass == 2) return "CURVA";  
      return "NONE";
    }
}
