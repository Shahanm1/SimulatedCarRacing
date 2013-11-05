/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.janquadflieg.mrracer.behaviour;

import de.janquadflieg.mrracer.functions.GeneralisedLogisticFunction;
import de.janquadflieg.mrracer.functions.GeneralisedLogisticFunction.XAxis;
import de.janquadflieg.mrracer.functions.GeneralisedLogisticFunction.YAxis;
import de.janquadflieg.mrracer.telemetry.ModifiableAction;
import de.janquadflieg.mrracer.telemetry.ModifiableSensorData;
import de.janquadflieg.mrracer.telemetry.SensorData;

import java.util.Properties;

/**
 *
 * @author quad
 */
public class Clutch
implements Behaviour{

    private GeneralisedLogisticFunction f = new GeneralisedLogisticFunction();

    public static final String F = "-Clutch.f-";

    public static final String MS = "-Clutch.maxSpeed-";

    private double MIN_SPEED = 0.0;

    private double MAX_SPEED = 84.0;

    public Clutch(){        
    }

    public Clutch(double lb, double ub){
        MIN_SPEED = Math.min(lb, ub);
        MAX_SPEED = Math.max(lb, ub);
    }

    @Override
    public void execute(final SensorData data, ModifiableAction action){
        action.setClutch(0.0);

        if(data.getSpeed() < MIN_SPEED || data.getSpeed() > MAX_SPEED){
            return;
        }

        double value = (data.getSpeed()-MIN_SPEED) / (MAX_SPEED - MIN_SPEED);
        double result = this.f.getMirroredValue(value);

        action.setClutch(result);
    }

    @Override
    public void setParameters(Properties params, String prefix){
        f.setParameters(params, prefix+F);
        MAX_SPEED = Double.parseDouble(params.getProperty(prefix+MS, String.valueOf(MAX_SPEED)));
    }

    @Override
    public void getParameters(Properties params, String prefix){
        f.getParameters(params, prefix+F);
        params.setProperty(prefix+MS, String.valueOf(MAX_SPEED));
    }

    /**
     * The current situation of the car, as classified by an
     * appropriate classifier chosen by the controller which uses this behaviour.
     */
    @Override
    public void setSituation(de.janquadflieg.mrracer.classification.Situation s){        
    }

    @Override
    public void reset(){
    }

    @Override
    public void shutdown(){        
    }

    public void paint(String baseFileName, java.awt.Dimension d){
        java.awt.image.BufferedImage img = new java.awt.image.BufferedImage(d.width, d.height, java.awt.image.BufferedImage.TYPE_INT_RGB);
        XAxis x = new XAxis();
        YAxis y = new YAxis();
        x.labelMin = 0.0;
        x.labelMax = 330.0;
        x.xmin = 0.0;
        x.xminY = 0.0;
        x.xmax = MAX_SPEED;
        x.xmaxY = 0.0;
        x.ticks = 50.0;
        x.unit = "km/h";

        y.mirror = true;
        y.labelMin = 0.0;
        y.y0 = 0.0;
        y.labelMax = 1.0;
        y.y1 = 1.0;
        y.ticks = 0.2;
        y.unit = "";
        f.paint(img.createGraphics(), d, x, y);
        try{
            javax.imageio.ImageIO.write(img, "PNG", new java.io.File(baseFileName+ "-clutch.png"));
        } catch(java.io.IOException e){
            e.printStackTrace(System.out);
        }
    }

    public static void main(String[] args){
        Properties p = new Properties();
        p.setProperty("-MrRacer2012.Clutch--Clutch.f--GLF.A-", "0");
        p.setProperty("-MrRacer2012.Clutch--Clutch.f--GLF.K-", "1");
        p.setProperty("-MrRacer2012.Clutch--Clutch.f--GLF.B-", "1");
        p.setProperty("-MrRacer2012.Clutch--Clutch.f--GLF.M-", "0");
        p.setProperty("-MrRacer2012.Clutch--Clutch.f--GLF.V-", "0.01");
        p.setProperty("-MrRacer2012.Clutch--Clutch.f--GLF.Q-", "0");
        p.setProperty("-MrRacer2012.Clutch--Clutch.maxSpeed-", "330.0");

        Clutch c = new Clutch();
        c.setParameters(p, "-MrRacer2012.Clutch-");

        ModifiableSensorData data = new ModifiableSensorData();
        ModifiableAction action = new ModifiableAction();

        for(double speed = 0.0; speed <= 300.0; speed +=10.0){
            data.setSpeed(speed);
            c.execute(data, action);
            System.out.println(action.getClutchS());
        }
    }
}
