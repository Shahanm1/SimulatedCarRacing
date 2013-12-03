package jmf.ais.torcsChamp;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Properties;
import java.util.StringTokenizer;

import champ2009client.Action;
import champ2009client.Controller;
import champ2009client.SensorModel;

/**
 *
 * @author Jorge Muñoz Fuentes
 */
public class HandCodedCar implements Controller{
	
	public static double MIN_SPEED = -100;
	public static double MAX_SPEED = 400;
	public static double MIN_ANGLE_TRACK = -3.15;
	public static double MAX_ANGLE_TRACK = 3.15;
	public static double MIN_GEAR = -1;
	public static double MAX_GEAR = 6;
	public static double MIN_LATERAL_SPEED = -200;
	public static double MAX_LATERAL_SPEED = 200;
	public static double MIN_RPM = 0;
	public static double MAX_RPM = 11000;
	public static double MIN_SENSORS = 0;
	public static double MAX_SENSORS = 100;
	
	public static int PARAMETERS_VECTOR_SIZE = 29; 
	
	private double[] parametersVector;
	
	private double[] defaultParametersVector;
	
	private Action action;
	
	private int lastGearChange;
	
	private int recoveryCount;
	
	private enum RECOVERY {NONE, BRAKING, REVERSE, TRACK};
	
	private RECOVERY statusRecovery;
	
	private boolean defaultMode;
	
	private enum OUT_SIDE { NONE, RIGHT, LEFT}
	
	private OUT_SIDE sideOut;
	
	private double topSpeed;
	
	public HandCodedCar(){
		this.lastGearChange = 0;
		this.action = new Action();
		this.statusRecovery = RECOVERY.NONE;
		this.sideOut = OUT_SIDE.NONE;
		boolean loaded = loadParameters();
		if(loaded){
			System.out.println("Parameters loaded");
		}
		initDefaultParameters(loaded);
		setDefaultParameters();	
		this.topSpeed = 0;
		this.defaultMode = false;
	}
	
	public void setDefaultParameters(){
		this.setParameters(getDefaultParameters());
		this.defaultMode = true;
	}
	
	private void initDefaultParameters(boolean userloadedParameters){
		if(userloadedParameters){
			this.defaultParametersVector = new double[PARAMETERS_VECTOR_SIZE];
			for(int i = 0; i < this.defaultParametersVector.length; i++){
				this.defaultParametersVector[i] = this.parametersVector[i];
			}
		}else{
			this.defaultParametersVector = new double[PARAMETERS_VECTOR_SIZE];
			this.defaultParametersVector[0] = 0.8;
			this.defaultParametersVector[1] = 0.6;
			this.defaultParametersVector[2] = 0.8;
			this.defaultParametersVector[3] = 0.5;//0.6
			this.defaultParametersVector[4] = 0.11;
			this.defaultParametersVector[5] = 0.10;//0.14
			this.defaultParametersVector[6] = 0.12;
			this.defaultParametersVector[7] = 0.4;
			this.defaultParametersVector[8] = 0.1;
			this.defaultParametersVector[9] = 0.46;
			this.defaultParametersVector[10] = 1;
			this.defaultParametersVector[11] = 1;
			this.defaultParametersVector[12] = 0.5;
			this.defaultParametersVector[13] = 0.3;
			this.defaultParametersVector[14] = 0.8;
			this.defaultParametersVector[15] = 0.8;
			this.defaultParametersVector[16] = 0.8;
			this.defaultParametersVector[17] = 0.8;
			this.defaultParametersVector[18] = 0.75;
			this.defaultParametersVector[19] = 0.2;
			this.defaultParametersVector[20] = 0.125;
			this.defaultParametersVector[21] = 0.7;
			this.defaultParametersVector[22] = 0.3;
			this.defaultParametersVector[23] = 0.3;
			this.defaultParametersVector[24] = 1;
			this.defaultParametersVector[25] = 165/MAX_SPEED;
			this.defaultParametersVector[26] = 4/MAX_GEAR;
			this.defaultParametersVector[27] = 70/MAX_SPEED;
			this.defaultParametersVector[28] = 2/MAX_GEAR;
		}		
	}
	
	public double[] getDefaultParameters(){
		return this.defaultParametersVector;
	}
	
	public void setParameters(double[] parameters){
		if(parameters.length != PARAMETERS_VECTOR_SIZE){
			throw new RuntimeException("Wrong number of parameters");
		}
		this.parametersVector = parameters;
		this.defaultMode = false;
	}

	
	public Action control(SensorModel sensors) {
		double[] trackSensors = sensors.getTrackEdgeSensors();
		double[] opponentSensors = sensors.getOpponentSensors();
		double[] spin = sensors.getWheelSpinVelocity();
		int seek = 4;
		double[] inputs = new double[seek+trackSensors.length+spin.length];//+opponentSensors.length];
		
		inputs[0] = normalice(sensors.getSpeed(),MIN_SPEED,MAX_SPEED);
		inputs[1] = normalice(sensors.getAngleToTrackAxis(),MIN_ANGLE_TRACK,MAX_ANGLE_TRACK);
		inputs[2] = normalice(sensors.getLateralSpeed(),MIN_LATERAL_SPEED,MAX_LATERAL_SPEED);
		inputs[3] = normalice(sensors.getRPM(),MIN_RPM,MAX_RPM);
		for(int i = 0; i < spin.length; i++){
			inputs[i+seek] = normalice(spin[i],MIN_SPEED,MAX_SPEED);
		}
		seek += spin.length;
		boolean out = false;
		for(int i = 0; i < trackSensors.length; i++){
			inputs[i+seek] = normalice(Math.min(trackSensors[i],opponentSensors[i]), MIN_SENSORS, MAX_SENSORS);
			out = out || trackSensors[i] < 0;
		}
		seek += trackSensors.length;
		// 36 opponent sensors
		//for(int i = 0; i < opponentSensors.length; i++){
		//	inputs[i+seek] = normalice(opponentSensors[i], MIN_SENSORS, MAX_SENSORS);
		//}
		
		if(!out){
			this.sideOut = OUT_SIDE.NONE;
		}
		
		if(out || this.statusRecovery != RECOVERY.NONE){
			
			if(this.sideOut == OUT_SIDE.NONE){
				int aux = 0;
				int middle = trackSensors.length/2;
				for(int i = 0; i < trackSensors.length; i++){
					if(trackSensors[i] < 0 && i != middle){
						//System.out.println("i " + i);
						if(i < middle){
							aux--;
						}else{
							aux++;
						}
					}
				}
				if(aux == 0){
					//System.out.println("angle: " + sensors.getAngleToTrackAxis());
					if(sensors.getAngleToTrackAxis() > 0){
						aux = 1;
					}else{
						aux = -1;
					}
				}
				//System.out.println("Aux: " + aux + " Speed: " + sensors.getSpeed());
				if(sensors.getSpeed() < 0){
					aux *= -1;
				}
				if(aux < 0){
					this.sideOut = OUT_SIDE.LEFT;
				}else if(aux > 0){
					this.sideOut = OUT_SIDE.RIGHT;
				}
			}else if(this.sideOut == OUT_SIDE.LEFT){
				//System.out.println("OUT IN SIDE LEFT");
			}else if(this.sideOut == OUT_SIDE.RIGHT){
				//System.out.println("OUT IN SIDE RIGHT");
			}
			
			// recovery mode
			//System.out.print("OUT: ");
			switch(this.statusRecovery){
				case NONE:
					//System.out.println("BRAKING");
					this.statusRecovery = RECOVERY.BRAKING;
					this.recoveryCount = 0;
					this.action.brake = 1;
					this.action.accelerate = 0;
					this.action.steering = 0;
					this.action.gear = 0;
					break;
				case BRAKING:
					//System.out.println("BRAKING");
					this.action.brake = 1;
					this.action.accelerate = 0;
					this.action.steering = 0;
					this.action.gear = 0;
					if(sensors.getSpeed() < 5){
						this.statusRecovery = RECOVERY.REVERSE;
					}
					break;
				case REVERSE:
					//System.out.println("REVERSE " + sensors.getSpeed());
					this.recoveryCount++;
					this.action.brake = 0;
					if(sensors.getSpeed() < -25){
						this.action.brake = 1;
					}
					this.action.accelerate = (15.0 + sensors.getSpeed()) / 10;
					this.action.steering = -0.3 * sensors.getAngleToTrackAxis();
					this.action.gear = -1;
					if(this.recoveryCount > 170 || ( !out && this.recoveryCount > 70 && Math.abs(sensors.getAngleToTrackAxis()) < 0.4)){
						this.statusRecovery = RECOVERY.TRACK;
						this.recoveryCount = 0;
					}
					break;
				case TRACK:
					//System.out.println("TRACK " + sensors.getSpeed());
					this.recoveryCount++;
					if(sensors.getSpeed() < 0){
						this.action.brake = 1;
						this.action.accelerate = 0;
					}else{
						this.action.brake = 0;
						this.action.accelerate = (25.0 - sensors.getSpeed()) / 20;
					}
					this.action.steering = sensors.getAngleToTrackAxis();
					this.action.gear = 1;
					if(out){
						if(this.sideOut == OUT_SIDE.LEFT){
							this.action.steering -= 0.4;
						}else if(this.sideOut == OUT_SIDE.RIGHT){
							this.action.steering += 0.4;
						}else{
							System.err.println("error, car is suppose to be out of the track, check sideOut");
						}
					}
					if(this.recoveryCount > 250  || (!out && Math.abs(sensors.getAngleToTrackAxis()) < 0.25)){
						this.statusRecovery = RECOVERY.NONE;
					}
					break;
			}
			
		}else{
			
			this.statusRecovery = RECOVERY.NONE;
		
			double x1,x2,y1,y2,m,b;
			double speed = sensors.getSpeed();
			if(speed > this.topSpeed){
				this.topSpeed = speed;
			}
			
			double sumFront = (inputs[17]) + (inputs[18]-inputs[26])*this.parametersVector[0]
			                + (inputs[16]-inputs[8])*this.parametersVector[0]
			                + inputs[15]*this.parametersVector[1] + inputs[19]*this.parametersVector[1];
			double stimatedSpeed = 0;
			
			x1=this.parametersVector[2]*3;
			y1=this.parametersVector[3];
			x2=this.parametersVector[4]*3;
			y2=this.parametersVector[5];
			m = (y1-y2)/(x1-x2);
			b = y1 - m * x1;
			stimatedSpeed = sumFront*m+b;
			if(stimatedSpeed < this.parametersVector[6]){
				stimatedSpeed = this.parametersVector[6];
			}
			if(stimatedSpeed > 1){
				stimatedSpeed = 1;
			}
			if(inputs[17] == 1){
				stimatedSpeed = 1;
			}
			double difference = stimatedSpeed - speed/MAX_SPEED;
			if(difference > 0){
				this.action.accelerate = difference/stimatedSpeed+this.parametersVector[7];
				this.action.brake = 0;
				if(inputs[17] == 1){
					this.action.accelerate = 1;
				}
				if(speed > 150 && this.defaultMode){
					this.action.accelerate = 0;
				}
			}else{
				this.action.accelerate = 0;			
				this.action.brake = difference/stimatedSpeed*-(1.0+this.parametersVector[8]);
			}
			if(inputs[17] < this.parametersVector[10] && speed/MAX_SPEED > this.parametersVector[9]){
				this.action.brake = this.parametersVector[11];
				this.action.accelerate = 0;	
			}
			
			if(inputs[17] == 1){
				difference = inputs[16] - inputs[18];
				this.action.steering = difference*this.parametersVector[12] + sensors.getAngleToTrackAxis()*this.parametersVector[13];
			}else{
				difference = (inputs[16] - inputs[18]) + (inputs[15] - inputs[19]);
				if(inputs[15] < this.parametersVector[14] && inputs[19] < this.parametersVector[14]){
					difference += (inputs[14] - inputs[20]);
					if(inputs[14] < this.parametersVector[15] && inputs[20] < this.parametersVector[15]){
						difference += (inputs[13] - inputs[21]);
						if(inputs[13] < this.parametersVector[16] && inputs[21] < this.parametersVector[16]){
							difference += (inputs[12] - inputs[22]);
							if(inputs[12] < this.parametersVector[17] && inputs[22] < this.parametersVector[17]){
								difference += inputs[11] - inputs[23];
							}
						}
					}
				}
	
				x1=this.parametersVector[18];
				y1=this.parametersVector[19];
				x2=this.parametersVector[20];
				y2=this.parametersVector[21];
				m = (y1-y2)/(x1-x2);
				b = y1 - m * x1;
				
				double mult = m*speed/MAX_SPEED+b;
				this.action.steering = difference*mult + sensors.getAngleToTrackAxis()*this.parametersVector[22];
			}
			if(this.action.brake > this.parametersVector[23]){
				this.action.steering = this.action.steering * (1-this.action.brake*this.parametersVector[24]);
			}
			
			x1=this.parametersVector[25];
			y1=this.parametersVector[26];
			x2=this.parametersVector[27];
			y2=this.parametersVector[28];
			m = (y1-y2)/(x1-x2);
			b = y1 - m * x1;
			int newgear = (int)(0.4 + (m*speed/MAX_SPEED + b)*MAX_GEAR);
			if(newgear != this.action.gear && this.lastGearChange > 50){
				this.action.gear = newgear;
				this.lastGearChange = 0;
			}else{
				this.lastGearChange++;
			}
			if(this.action.gear < 1){
				this.action.gear = 1;
			}
			if(this.action.gear > 6){
				this.action.gear = 6;
			}
		}
		
		return this.action;
	}
	
	public boolean ready(){
		return this.statusRecovery == RECOVERY.NONE;
	}
	
	private double normalice(double value, double minimum, double maximum){
		return (value - minimum) / (maximum - minimum);
	}

	public void reset() {
		
	}

	public void shutdown() {
	}
	
	private boolean loadParameters(){
		boolean loaded = false;
		try{
			Properties properties = new Properties();
			properties.load(new FileReader("cig2009.properties"));
			BufferedReader reader = new BufferedReader(new FileReader(properties.getProperty("carParameters")));
			String line = reader.readLine();
			line = line.trim();
			reader.close();
			StringTokenizer stk = new StringTokenizer(line);
			this.parametersVector = new double[PARAMETERS_VECTOR_SIZE];
			for(int i = 0; i < this.parametersVector.length; i++){
				this.parametersVector[i] = Double.parseDouble(stk.nextToken());
			}
			loaded = true;
		}catch(Exception e){}
		return loaded;
	}
	public double getTopSpeed(){
		return this.topSpeed;
	}
	
	public void resetTopSpeed(){
		this.topSpeed = 0;
	}

}
