/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.janquadflieg.mrracer.functions;

/**
 *
 * @author quad
 */
public class ConstantValue
implements Interpolator{
    private double xmin;
    private double xmax;
    private double value;

    public ConstantValue(double x1, double x2, double v){
        this.xmin = Math.min(x1, x2);
        this.xmax = Math.max(x1, x2);
        this.value = v;
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
        return xmin;
    }

    @Override
    public double getXmax(){
        return xmax;
    }

    @Override
    public double interpolate(double v){
        return value;
    }
}