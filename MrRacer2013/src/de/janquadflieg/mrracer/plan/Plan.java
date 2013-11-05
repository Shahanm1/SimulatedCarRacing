/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.janquadflieg.mrracer.plan;

import de.janquadflieg.mrracer.track.TrackSegment;

/**
 *
 * @author quad
 */
public interface Plan {
    public double calcApproachSpeedStraight(double targetSpeed, double distance);
    
    public double calcApproachSpeedCorner(double targetSpeed, double distance);

    public double calcBrakeDistanceCorner(double speed, double targetSpeed);

    public void calcApproachSpeed(PlanStackData planData);

    public double getAnchorPoint(TrackSegment first, TrackSegment second);

    public double getTargetSpeed(TrackSegment.Apex a);

    public double calcBrakingZoneStraight(double refSpeed, double length, double targetSpeed);

    public double calcBrakingZoneCorner(double refSpeed, double length, double targetSpeed);

    public void replan();

    public void println(String s);

    /** Debug in text mode? */
    public static final boolean TEXT_DEBUG = false;
}
