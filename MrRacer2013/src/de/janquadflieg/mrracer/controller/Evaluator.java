/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.janquadflieg.mrracer.controller;

import de.janquadflieg.mrracer.Utils;
import de.janquadflieg.mrracer.telemetry.*;

/**
 *
 * @author Jan Quadflieg
 */
public class Evaluator
        extends scr.Controller {
    /** Constant indicating no maximum. */
    public static final int NO_MAXIMUM = Integer.MIN_VALUE;
    /** The controller to evaluate. */
    private BaseController controller;
    /** Distance raced. */
    private double distanceRaced = 0;
    /** Number of gameTicks off the track. */
    private int offTrackCtr = 0;
    /** Damage of the car. */
    private double damage = 0;
    /** Damage of the car. */
    private double maxDamage = Utils.NO_DATA_D;
    /** Number of gameticks after which a restart should be requested. */
    private int maxGameTicks = 0;
    /** Counter for gameticks. */
    private int gameTickCtr = 0;
    /** Counter for laps. */
    private int lapCtr = -1;
    /** Number of laps after which a restart should be requested. */
    private int maxLaps = NO_MAXIMUM;
    /** Last sensor data. */
    private SensorData lastData = null;    
    private double fastestLap = 60 * 100;
    private double time = 0.0;
    private double sumTime = 0.0;
    private double latSpeedIntegral = 0.0;
    private boolean aborted = false;
    private boolean stop = false;
    /** Counter for the number of overtakings. */
    private int overtakingCtr = 0;    

    public Evaluator(BaseController c, int i) {
        this.controller = c;
        this.maxGameTicks = i;
    }

    public boolean aborted(){
        return aborted;
    }

    public double getFastestLap() {
        return this.fastestLap;
    }

    public double getOverallTime() {
        return this.time;
    }

    public void setMaxLaps(int d) {
        this.maxLaps = d;
    }

    public int getGameTickCtr() {
        return gameTickCtr;
    }

    public int getLapCtr() {
        return lapCtr;
    }

    public double getDistanceRaced() {
        return this.distanceRaced;
    }

    public int getOffTrackCtr() {
        return this.offTrackCtr;
    }

    public double getDamage() {
        return this.damage;
    }

    public int getOvertakingCtr(){
        return overtakingCtr;
    }

    public double getLatSpeedIntegral(){
        return latSpeedIntegral;
    }

    @Override
    public scr.Action control(scr.SensorModel m) {
        if(aborted){
            scr.Action action = new scr.Action();
            action.restartRace = true;
            return action;
        }

        SensorData data = new SensorData(m);
        ++gameTickCtr;
        if (lastData != null && data.getDistanceFromStartLine() < lastData.getDistanceFromStartLine()
                && data.getDistanceRaced() > lastData.getDistanceRaced()) {
            ++lapCtr;
            if (lapCtr > 0) {
                fastestLap = Math.min(fastestLap, data.getLastLapTime());
                sumTime += data.getLastLapTime();
                //System.out.println(sumTime);
                //System.out.println(fastestLap);
            }
        }

        if(lastData != null){
            latSpeedIntegral += Math.abs(data.getLateralSpeed())*
                    Math.abs(data.getDistanceRaced()-lastData.getDistanceRaced());
        }

        if(lastData != null && data.getRacePosition() != lastData.getRacePosition()){
            // question? record absolute values?
            overtakingCtr += lastData.getRacePosition()-data.getRacePosition();
        }

        //if(aborted){
        //    System.out.println("control called after the evaluation has been aborted!");
        //}

        time = sumTime + data.getCurrentLapTime();

        if (!data.onTrack()) {
            ++offTrackCtr;
        }

        distanceRaced = data.getDistanceRaced();
        damage = data.getDamage();

        scr.Action action = controller.control(m);

        // end of evaluation
        if (maxTicksReached() || maxDamageReached() || maxLapsReached() || stop){
            action.restartRace = true;

            if ((maxTicksReached() || maxDamageReached()) && !maxLapsReached()) {
                System.out.println("Abort@ Laps[" + lapCtr + "/" + (maxLaps != NO_MAXIMUM? String.valueOf(maxLaps) : "No Maximum") + "] "
                        + "Ticks["+gameTickCtr+"/"+(maxGameTicks != NO_MAXIMUM? String.valueOf(maxGameTicks) : "No Maximum")+"] "
                        + "Damage: " + data.getDamage()+" "
                        + "Time raced: "+Utils.timeToExactString(time));
                if(maxLaps != NO_MAXIMUM){
                    double overallDist = this.maxLaps * controller.trackModel.getLength();
                    System.out.print("Distance[" + Utils.dTS(Math.max(1.0, data.getDistanceRaced())) + "/" + Utils.dTS(overallDist) + "] " + Utils.timeToExactString(time));
                    double ratio = Math.max(1.0, data.getDistanceRaced()) / overallDist;
                    time *= 1.0 / ratio;
                }
                // damage multiplier
                time *= (1.0 + (2.0 * (data.getDamage()/5000.0)));
                System.out.println(" -> " + Utils.timeToExactString(time));
                System.out.println("Stopped? "+stop);
                aborted = true;
            }
        }

        lastData = data;

        return action;
    }

    private boolean maxTicksReached(){
        boolean result = (maxGameTicks != NO_MAXIMUM) && gameTickCtr >= maxGameTicks;
        if(result){
            System.out.println("Reached MaxTicks");
        }
        return result;
    }

    public boolean maxDamageReached(){
        boolean result = (maxDamage != Utils.NO_DATA_D) && damage > maxDamage;
        if(result){
            System.out.println("Reached MaxDamage");
        }
        return result;
    }

    private boolean maxLapsReached(){
        boolean result = (maxLaps != NO_MAXIMUM) && lapCtr == maxLaps;
        if(result){
            System.out.println("Reached MaxLaps");
        }
        return result;
    }

    public void resetFull() {
        controller.resetFull();
    }

    public void reset() {
        controller.reset();
    }

    public void shutdown() {
        controller.shutdown();
    }

    public void stop(){
        stop = true;
    }
}
