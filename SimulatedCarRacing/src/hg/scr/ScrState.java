package hg.scr;

import scr.SensorModel;

public class ScrState {
	public int inputnum = 7;
	
	private double speed;
	private double angle;
	private double damage;
	private double curLapTime;
	private double focus;
	private double gear;
	private double fuel;
	public ScrState(SensorModel sensor){
		angle = sensor.getAngleToTrackAxis();
		damage = sensor.getDamage();
		curLapTime= sensor.getCurrentLapTime();
//		focus= sensor.getFocusSensors();
		fuel= sensor.getFuelLevel();
		gear= sensor.getGear();
	}
	public double getSpeed() {
		return speed;
	}
	public void setSpeed(double speed) {
		this.speed = speed;
	}
	public double getAngle() {
		return angle;
	}
	public void setAngle(double angle) {
		this.angle = angle;
	}
	public double getDamage() {
		return damage;
	}
	public void setDamage(double damage) {
		this.damage = damage;
	}
	public double getCurLapTime() {
		return curLapTime;
	}
	public void setCurLapTime(double curLapTime) {
		this.curLapTime = curLapTime;
	}
	public double getFocus() {
		return focus;
	}
	public void setFocus(double focus) {
		this.focus = focus;
	}
	public double getGear() {
		return gear;
	}
	public void setGear(double gear) {
		this.gear = gear;
	}
	public double getFuel() {
		return fuel;
	}
	public void setFuel(double fuel) {
		this.fuel = fuel;
	}
	
	
	
	
	/*
	    public double getSpeed ();

    public double getAngleToTrackAxis ();

    public double[] getTrackEdgeSensors ();
    
    public double[] getFocusSensors ();//ML

    public double getTrackPosition();

    public int getGear ();

    // basic information about other cars (only useful for multi-car races)

    public double[] getOpponentSensors ();

    public int getRacePosition ();

    // additional information (use if you need)

    public double getLateralSpeed ();
    

    public double getCurrentLapTime ();

    public double getDamage ();

    public double getDistanceFromStartLine ();

    public double getDistanceRaced ();

    public double getFuelLevel ();

    public double getLastLapTime ();

    public double getRPM ();

    public double[] getWheelSpinVelocity ();
    
    public double getZSpeed ();
    
    public double getZ ();
}
	 */
	
	
	

}
