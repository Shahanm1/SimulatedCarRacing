/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.janquadflieg.mrracer.functions;

import flanagan.interpolation.CubicSpline;

/**
 *
 * @author quad
 */
public class FlanaganCubicWrapper
implements Interpolator{
    private CubicSpline spline;

    public FlanaganCubicWrapper(CubicSpline s){
        this.spline = s;
    }

    /**
     * Compares this object with the specified object for order.  Returns a
     * negative integer, zero, or a positive integer as this object is less
     * than, equal to, or greater than the specified object.
     */
    public int compareTo(Interpolator other){
        if(getXmin() < other.getXmin()){
            return -1;
        } else if(getXmin() > other.getXmin()){
            return 1;
        }
       return 0;
    }

    @Override
    public double getXmin(){
        return this.spline.getXmin();
    }
    
    @Override
    public double getXmax(){
        return this.spline.getXmax();

    }

    @Override
    public double interpolate(double v){
        return this.spline.interpolate(v);        
    }
}
