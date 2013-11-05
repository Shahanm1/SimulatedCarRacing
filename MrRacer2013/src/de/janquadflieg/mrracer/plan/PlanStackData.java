/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.janquadflieg.mrracer.plan;

import java.util.ArrayList;

import de.janquadflieg.mrracer.Utils;


/**
 * Helper class to pass data between the different planning stages.
 */
public class PlanStackData {

    /** Start of planning. */
    private double planStart;
    /** End of planning. */
    private double planEnd;
    /** TrackSegments to plan for. */
    private ArrayList<Integer> segments = new ArrayList<>();
    /** Start point for each segment, in distance raced. */
    private ArrayList<Double> starts = new ArrayList<>();
    /** End points for each segment, in distance raced. */
    private ArrayList<Double> ends = new ArrayList<>();
    /** Predicted speed at the beginning of each segment. */
    private ArrayList<Double> speeds = new ArrayList<>();
    /** Index (into the segments list) of the current segment to plan for. */
    private int idx = -1;
    /** Approach speed, passed from the last element. */
    public double approachSpeed = Plan2011.MAX_SPEED;    

    public PlanStackData(double start){
        this.planStart = start;
        this.planEnd = start;
        this.starts.add(start);
    }

    public void addSegment(int index, double length, double speed) {
        segments.add(index);
        speeds.add(speed);
        ends.add(starts.get(starts.size()-1)+length);
        starts.add(ends.get(ends.size()-1));
        idx = segments.size() - 1;
        planEnd += length;
    }

    public int currentSegment() {
        return segments.get(idx);
    }

    public void popSegment() {        
        --idx;
    }

    /**
     * Is the current one the first segment? This is the one i am in.
     * @return
     */
    public boolean first(){
        return idx == 0;
    }

    /** Is this the last element?
     * 
     * @return
     */
    public boolean last(){
        return idx == segments.size()-1;
    }

    public boolean hasMoreSegments() {
        return idx >= 0;
    }

    public double planEnd(){
        return this.planEnd;
    }

    public double planStart(){
        return this.planStart;
    }

    public double start(){
        return starts.get(idx);
    }

    public double end(){
        return ends.get(idx);
    }

    /**
     * The speed with which I expect to arrive at the current segment.
     * @return
     */
    public double speed(){
        return speeds.get(idx);
    }

    public void print(){
        System.out.println("PlanStackData ["+Utils.dTS(planStart)+", "+Utils.dTS(planEnd)+"]");
        System.out.println("Approach speed: "+Utils.dTS(approachSpeed)+"km/h");
        for(int i=idx; i >= 0; --i){
            System.out.println("SegIdx["+segments.get(i)+"], start="+Utils.dTS(starts.get(i))+
                    ", end="+Utils.dTS(ends.get(i))+", speed="+Utils.dTS(speeds.get(i)));
        }
    }
}