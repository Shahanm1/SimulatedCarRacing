package champ2009client;

import champ2009client.fuzzy.*;
import champ2009client.classifier.FMOClassifier;
//import java.util.Hashtable;
//import java.text.DecimalFormat;


public class DiegoGAController implements Controller {
  final float lowerGear = 3000;
  final float higherGear = 6000;
  //private Hashtable<String,String> ht;
  final private float MAX_UNSTUCK_SPEED = 5.0F;	 //Minimum speed to be stuck
  final private float MAX_UNSTUCK_SPEED_BACKWARDS = -20.0f;//-10.0F;	 //Minimum speed to be stuck (backwards)
  final private int MAX_UNSTUCK_COUNT = 350;     //Count before consider the car stuck
  private int stuck;
  private int ticCounter;
  
  private FuzzySystem _fs;
  
  public DiegoGAController(){
    _fs = new FuzzySystem();
    _fs.createFromFile();
    stuck = 0;
    ticCounter=0;
  }

  private boolean isStuck(SensorModel sensors){
     if(sensors.getSpeed() < MAX_UNSTUCK_SPEED){
        if((sensors.getSpeed() < 0) && (sensors.getSpeed() < MAX_UNSTUCK_SPEED_BACKWARDS)){
            stuck = 0;
            return false;
        }else if(stuck > MAX_UNSTUCK_COUNT){
            return true;
        }else{
            stuck++;
            return false;
        }
     }else{
        stuck = 0;
        return false;
     }
  }

  
  private void getGearing(double rpm, int currentGear, Action action){
    boolean mustDown =  ((rpm < lowerGear) && (currentGear > 1));
    boolean mustUp =  ( (rpm > higherGear) || (currentGear == 0));
    if (mustDown){
      action.gear = currentGear-1;
    }else if (mustUp) { 
      action.gear = currentGear+1;
    }else if(!mustUp && !mustDown){
      action.gear = currentGear;
      if(currentGear == -1) action.gear = 1;
    }
  }

  private float getAccel(float value){
    if(value >= 0.5F)
      return (value-0.5F)*2.0F;
    else return 0.0F;
  }
  private float getBrake(float value){
    if(value < 0.5F){
      return 1.0F-(value*2.0F);
    }else return 0.0F;
  }
  
  public Action control(SensorModel sensors){
    EvolutionAction action = new EvolutionAction();
   
    if (isStuck(sensors)) {
      action.gear = -1;
      action.evol_acceleration = 1.0F;
      action.evol_brake = 0.0F;  
      action.evol_steering = -1.0F * (float)sensors.getAngleToTrackAxis();
      return action;
    }

    //Update the fuzzy state
    //_fs.update(sensors, 0, sensors.getTrackEdgeSensors()); 
 
    //Get the action that fuzzy system suggests
    //action = _fs.getAction();

    //Adjust gearing and acceleration
    getGearing(sensors.getRPM(),sensors.getGear(),action);
    
    double abs_accel = action.evol_acceleration;
    action.evol_acceleration = getAccel((float)abs_accel);
    action.evol_brake = getBrake((float)abs_accel);
    
    return action;
  }
  
  public void reset() {}

  public void shutdown() 
  {
      //All sensors one by one
//      FMOClassifier.printARFFRaw(); 
      
      //All sensors, differenced
      FMOClassifier.printARFFRawTrack(); 
  }
  
}
