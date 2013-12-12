package champ2009client;

public class DiegoController implements Controller {

    final float minimumGear = 3000; //3000
    final float lowerGear = 6000; //3000
    final float higherGear = 7000; //6000
    
    private DriverManager _driver;

    public DiegoController() {
    }

    public void setDriverManager(DriverManager dm) {
        _driver = dm;
    }

    //Control of gearing.
    private void getGearing(double rpm, int currentGear, EvolutionAction action) {
        boolean mustDown = ((rpm < lowerGear)   && (currentGear > 1) && (action.evol_acceleration <= 0.5f)) || 
                           ((rpm < minimumGear) && (currentGear > 1) );
        boolean mustUp = ((rpm > higherGear) || (currentGear == 0));
        if (mustDown) {
            action.gear = currentGear - 1;
        } else if (mustUp) {
            action.gear = currentGear + 1;
        } else if (!mustUp && !mustDown) {
            action.gear = currentGear;
            if (currentGear == -1) {
                action.gear = 1;
            }
        }
        if(currentGear == 0) action.gear = 1; //For sure
    }

    private float getAccel(float value) {
        if (value >= 0.5F) {
            return (value - 0.5F) * 2.0F;
        } else {
            return 0.0F;
        }
    }

    private float getBrake(float value) {
        if (value < 0.5F) {
            return 1.0F - (value * 2.0F);
        } else {
            return 0.0F;
        }
    }

    public Action control(SensorModel sensors) {

        EvolutionAction action = new EvolutionAction();

        //Update the controller state
        _driver.update(sensors);

        //Get the action that fuzzy system suggests
        action = new EvolutionAction();
        action = _driver.getAction();

        //Adjust gearing and acceleration
        if (action.gear != -1) {
            getGearing(sensors.getRPM(), sensors.getGear(), action);
        }
        double abs_accel = action.evol_acceleration;
        action.evol_acceleration = getAccel((float) abs_accel);
        action.evol_brake = getBrake((float) abs_accel);
        
	  //Retrun action to take
        return action;
    }

    
    public void reset() {
        //Fitness info
        _driver.setFitness();
    }
    
    public void shutdown() {
    }
}
