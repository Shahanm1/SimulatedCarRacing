/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.janquadflieg.mrracer.behaviour;

import de.janquadflieg.mrracer.classification.*;
import de.janquadflieg.mrracer.functions.GeneralisedLogisticFunction;
import de.janquadflieg.mrracer.functions.GeneralisedLogisticFunction.XAxis;
import de.janquadflieg.mrracer.functions.GeneralisedLogisticFunction.YAxis;
import de.janquadflieg.mrracer.telemetry.ModifiableAction;
import de.janquadflieg.mrracer.telemetry.SensorData;
import de.janquadflieg.mrracer.track.*;

import java.util.Properties;

/**
 *
 * @author quad
 */
public abstract class AbstractDampedAccelerationBehaviour
        implements AccelerationBehaviour {

    protected Situation s;
    protected double trackWidth;    
    protected double targetSpeed = 0.0;
    protected TrackSegment current;
    protected GeneralisedLogisticFunction accDamp = new GeneralisedLogisticFunction();
    public static final String ACC_DAMP = "-DAB.accDamp-";
    protected GeneralisedLogisticFunction brakeDamp = new GeneralisedLogisticFunction();
    public static final String BRAKE_DAMP = "-DAB.brakeDamp-";    

    public AbstractDampedAccelerationBehaviour() {
    }

    @Override
    public void setParameters(Properties params, String prefix) {
        params.setProperty(prefix + ACC_DAMP + GeneralisedLogisticFunction.F, String.valueOf(1.0));
        accDamp.setParameters(params, prefix + ACC_DAMP);
        double f0 = 1.0 / accDamp.getMirroredValue(0.0);
        params.setProperty(prefix + ACC_DAMP + GeneralisedLogisticFunction.F, String.valueOf(f0));
        accDamp.setParameters(params, prefix + ACC_DAMP);

        params.setProperty(prefix + BRAKE_DAMP + GeneralisedLogisticFunction.F, String.valueOf(1.0));
        brakeDamp.setParameters(params, prefix + BRAKE_DAMP);
        f0 = 1.0 / brakeDamp.getMirroredValue(0.0);
        params.setProperty(prefix + BRAKE_DAMP + GeneralisedLogisticFunction.F, String.valueOf(f0));
        brakeDamp.setParameters(params, prefix + BRAKE_DAMP);
        //System.out.println("dampOnStraight: "+dampOnStraight);
    }

    @Override
    public void getParameters(Properties params, String prefix) {
        accDamp.getParameters(params, prefix + ACC_DAMP);
        brakeDamp.getParameters(params, prefix + BRAKE_DAMP);
    }

    public void paint(String baseFileName, java.awt.Dimension d) {
        java.awt.image.BufferedImage img = new java.awt.image.BufferedImage(d.width, d.height, java.awt.image.BufferedImage.TYPE_INT_RGB);
        XAxis x = new XAxis();
        YAxis y = new YAxis();
        x.labelMin = 0.0;
        x.labelMax = 45.0;
        x.ticks = 10.0;
        x.xmin = 0.0;
        x.xmax = 45.0;
        x.unit = "°";

        y.mirror = true;
        y.labelMin = 0.0;
        y.y0 = 0.0;
        y.labelMax = 1.0;
        y.y1 = 1.0;
        y.ticks = 0.2;
        y.unit = "";
        accDamp.paint(img.createGraphics(), d, x, y);
        try {
            javax.imageio.ImageIO.write(img, "PNG", new java.io.File(baseFileName + "-acc.png"));
        } catch (java.io.IOException e) {
            e.printStackTrace(System.out);
        }
        img = new java.awt.image.BufferedImage(d.width, d.height, java.awt.image.BufferedImage.TYPE_INT_RGB);
        brakeDamp.paint(img.createGraphics(), d, x, y);
        try {
            javax.imageio.ImageIO.write(img, "PNG", new java.io.File(baseFileName + "-brake.png"));
        } catch (java.io.IOException e) {
            e.printStackTrace(System.out);
        }
    }

    @Override
    public abstract void execute(SensorData data, ModifiableAction action);        

    @Override
    public void reset() {
    }

    @Override
    public void shutdown() {
    }

    /**
     * The current situation of the car, as classified by an
     * appropriate classifier chosen by the controller which uses this behaviour.
     */
    @Override
    public void setSituation(de.janquadflieg.mrracer.classification.Situation s) {
        this.s = s;
    }

    /**
     * The segment of the trackmodel containing the current position of the car.
     * Might be null, if the trackmodel has not beeen initialized or unknown, if
     * the controller is still learning the track during the first lap.
     * @param s The track segment.
     */
    @Override
    public void setTrackSegment(TrackSegment s) {
        this.current = s;
    }

    /**
     * The desired target speed.
     * @param speed The speed.
     */
    @Override
    public void setTargetSpeed(double speed) {
        this.targetSpeed = speed;
    }

    /**
     * The width of the race track in meter.
     * @param width
     */
    @Override
    public void setWidth(double width) {
        trackWidth = width;
    }
}
