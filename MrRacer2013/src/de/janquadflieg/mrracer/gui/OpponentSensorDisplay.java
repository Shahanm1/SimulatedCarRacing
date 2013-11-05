/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.janquadflieg.mrracer.gui;

import de.janquadflieg.mrracer.Utils;
import de.janquadflieg.mrracer.telemetry.SensorData;
import de.janquadflieg.mrracer.telemetry.ModifiableSensorData;

import java.awt.*;
import java.awt.geom.*;
import javax.swing.*;

/**
 *
 * @author Jan Quadflieg
 */
public class OpponentSensorDisplay
        extends JComponent {

    private ModifiableSensorData m = new ModifiableSensorData();
    private static final int CAR_WIDTH = 2;
    private static final int CAR_LENGTH = 5;
    /** Pixel per meter. */
    private static final int PPM = 5;

    private boolean newInterpretation = true;

    public OpponentSensorDisplay() {
    }

    public void setMode(boolean b){
        this.newInterpretation = b;
        repaint();
    }

    @Override
    public void paintComponent(Graphics graphics) {
        Graphics2D g = (Graphics2D) graphics;

        g.setColor(Color.WHITE);

        g.fillRect(0, 0, getWidth(), getHeight());

        if (m.getAngleToTrackAxis() != Utils.NO_DATA_D) {
            // my car
            drawCar(g);

            double[] sensors = m.getOpponentSensors();
            boolean allHundred = true;
            for (int i = 0; i < sensors.length; ++i) {
                allHundred &= (sensors[i] == 200.0);
            }

            if (allHundred) {
                g.setColor(Color.BLACK);
                g.drawString("No opponent in sight", 30, 30);

            } else {
                // track sensors
                if(newInterpretation){
                    drawOpponentSensors(g);

                } else {
                    drawOpponentSensorsOld(g);
                }
            }

        } else {
            g.setColor(Color.BLACK);
            g.drawString("No data available", 30, 30);
            
            //drawOpponentSensors(g);
        }
    }

    private void drawCar(Graphics2D g) {
        AffineTransform backup = g.getTransform();

        g.translate(getWidth() / 2, getHeight()/2);
        g.setColor(Color.BLACK);
        g.drawRect(-(CAR_WIDTH*PPM / 2), - (CAR_LENGTH * PPM / 2), CAR_WIDTH*PPM, CAR_LENGTH*PPM);

        g.setTransform(backup);
    }

    private void drawOpponentSensorsOld(Graphics2D g) {
        AffineTransform backup = g.getTransform();

        g.translate(getWidth() / 2, getHeight()/ 2);

        g.setColor(Color.RED);

        double[] sensors = m.getOpponentSensors();        

        for (int i = 0; i < sensors.length; ++i) {
            if (sensors[i] < 200.0) {
                double angle = Math.toRadians(-180.0) + Math.toRadians(i * 10.0);
                int x_offset = (int) Math.round(Math.sin(angle) * sensors[i] * PPM);
                int y_offset = (int) Math.round(-Math.cos(angle) * sensors[i] * PPM);


                g.setColor(Color.RED);
                g.drawLine(0, 0, x_offset, y_offset);
                g.fillRect(x_offset - 1, y_offset - 1, 2, 2);


                g.setColor(Color.BLUE);
                x_offset = (int) Math.round(Math.sin(angle) * (sensors[i]+5) * PPM);
                y_offset = (int) Math.round(-Math.cos(angle) * (sensors[i]+5) * PPM);
                g.drawString(Integer.toString(i)+"-"+String.format("%.1f", sensors[i]),
                        x_offset + 2, y_offset + 2); 
            }
        }       

        g.setTransform(backup);
    }

    private void drawOpponentSensors(Graphics2D g) {
        AffineTransform backup = g.getTransform();

        g.translate(getWidth() / 2, getHeight() / 2);

        g.setColor(Color.RED);

        double[] sensors = m.getOpponentSensors();
        //double[] sensors = new double[36];
        //java.util.Arrays.fill(sensors, 200.0);
        //sensors[35] = 23;

        for (int i = 0; i < sensors.length; ++i) {             
            if (sensors[i] < 200.0) {
                double angle = Math.toRadians(-175.0) + Math.toRadians(i * 10.0);
                int x_offset = (int) Math.round(Math.sin(angle) * sensors[i] * PPM);
                int y_offset = (int) Math.round(-Math.cos(angle) * sensors[i] * PPM);


                g.setColor(Color.RED);
                g.drawLine(0, 0, x_offset, y_offset);
                g.fillRect(x_offset - 1, y_offset - 1, 2, 2);

                g.setColor(new Color(255, 0, 0, 50));
                double size = 2 * sensors[i] * PPM;
                double arcAngle = 85-Math.toDegrees(angle);
                double arc = 10.0;
                g.fill(new java.awt.geom.Arc2D.Double(-size/2, -size/2, size, size, arcAngle, arc, java.awt.geom.Arc2D.PIE));
                
                //g.drawString(Utils.dTS(Math.toDegrees(angle)), 50, 50);
                //g.drawString(Utils.dTS(arcAngle), 50, 60);

                g.setColor(Color.BLUE);
                x_offset = (int) Math.round(Math.sin(angle) * (sensors[i]+5) * PPM);
                y_offset = (int) Math.round(-Math.cos(angle) * (sensors[i]+5) * PPM);
                g.drawString(Integer.toString(i)+"-"+String.format("%.1f", sensors[i]),
                        x_offset + 2, y_offset + 2);
            }
        }

        g.setTransform(backup);
    }

    public void setSensorData(SensorData model) {
        this.m.setData(model);
        repaint();
    }
}
