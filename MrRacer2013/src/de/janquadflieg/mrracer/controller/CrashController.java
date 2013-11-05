/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.janquadflieg.mrracer.controller;

import de.janquadflieg.mrracer.Utils;
import de.janquadflieg.mrracer.telemetry.*;
import de.janquadflieg.mrracer.track.*;

import java.util.ArrayList;

/**
 *
 * @author quad
 */
public class CrashController
        extends BaseController {

    private BaseController controller;
    private int gameTickCtr = 0;
    private int lapCtr = -1;
    private SensorData lastData = null;
    private int nextCrashIdx = 0;
    private ArrayList<Integer> crashPoints = new ArrayList<>();
    private ArrayList<Double> crashAngles = new ArrayList<>();

    private int counter = 0;

    private double crashAt = 0.0;

    private static final int DRIVE = 0;
    private static final int WAIT_FOR_CRASH = 1;
    private static final int CRASH = 2;

    private int state = DRIVE;

    public CrashController(BaseController c) {
        super(null);
        this.controller = c;

        /*crashPoints.add(1);
        crashAngles.add(0.1);

        crashPoints.add(4);
        crashAngles.add(-0.5);

        crashPoints.add(10);
        crashAngles.add(0.1);

        crashPoints.add(13);
        crashAngles.add(0.1);

        crashPoints.add(21);
        crashAngles.add(0.1);

        crashPoints.add(25);
        crashAngles.add(0.1);*/
    }

    @Override
    public scr.Action unsaveControl(scr.SensorModel m) {
        SensorData data = new SensorData(m);
        ++gameTickCtr;
        if (lastData != null && data.getDistanceFromStartLine() < lastData.getDistanceFromStartLine()
                && data.getDistanceRaced() > lastData.getDistanceRaced()) {
            ++lapCtr;
        }

        if (firstPacket) {
            firstPacket(data);
        }

        scr.Action action = controller.control(m);

        if(state == DRIVE){
            //System.out.println("Driving");
            if(initiateCrash(data)){
                TrackSegment seg = trackModel.getSegment(data.getDistanceFromStartLine());
                crashAt = data.getDistanceRaced()+(Math.random()*seg.getLength()*0.3);
                state = WAIT_FOR_CRASH;
                System.out.println("InitiateCrash @ "+Utils.dTS(crashAt));
            }
            
        } else if(state == WAIT_FOR_CRASH){
            //System.out.println("Waiting");
            if(data.getDistanceRaced() >= crashAt){
                counter = 100;
                state = CRASH;
                System.out.println("Reached crash point");
            }

        } else if(state == CRASH){
            //System.out.println("Crashing");
            --counter;

            if(counter <= 0){
                state = DRIVE;
                nextCrashIdx = (nextCrashIdx + 1) % crashPoints.size();
                System.out.println("Crashed enough, driving again");
            }
        }

        // do something?

        if(state == DRIVE || state == WAIT_FOR_CRASH){
            // nothing to do        

        } else if(state == CRASH){
            //System.out.println("Crashing is so much fun");
            int idx = crashPoints.indexOf(trackModel.getIndex(data.getDistanceFromStartLine()));
            action.steering = crashAngles.get(idx);
            action.limitValues();
        }      

        lastData = data;

        return action;
    }

    private boolean initiateCrash(SensorData d) {
        int segIdx = trackModel.getIndex(d.getDistanceFromStartLine());
        
        // first lap?
        boolean result = lapCtr >= 0;        

        // is this the next crash segment?
        result &= crashPoints.get(nextCrashIdx) == segIdx;

        return result;
    }

    private void firstPacket(SensorData data) {
        if (getStage() == Stage.WARMUP) {
            System.out.println("CrashController cannot be used in warmup mode!");
            System.exit(1);

        } else if (getStage() == Stage.QUALIFYING || getStage() == Stage.RACE) {
            TrackModel result = trackDB.getByName(getTrackName());

            if (result == TrackDB.UNKNOWN_MODEL) {
                System.out.println("Failed to get the trackmodel for track " + getTrackName() + ".");
                System.exit(1);

            } else {
                trackModel = result;
            }

            // init crash points
            for(int i=0; i < trackModel.size(); ++i){
                TrackSegment seg = trackModel.getSegment(i);
                if(seg.isCorner()){
                    crashPoints.add(i);
                    if((i % 2) == 0){
                        crashAngles.add(1.0);
                    } else {
                        crashAngles.add(-1.0);
                    }
                    System.out.println("Crash at "+crashPoints.get(crashPoints.size()-1)+" "+
                            crashAngles.get(crashPoints.size()-1));
                }
            }

        } else {
            System.out.println("Unknown stage");
            System.out.println("Stage given was: " + getStage());
            System.exit(1);
        }

        firstPacket = false;
    }

    public void resetFull() {
        super.resetFull();
        controller.resetFull();
    }

    public void reset() {
        super.reset();
        controller.reset();


    }

    public void shutdown() {
        super.shutdown();
        controller.shutdown();

    }
}
