/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.janquadflieg.mrracer.gui;

import de.janquadflieg.mrracer.Utils;
import de.janquadflieg.mrracer.plan.Plan2011;
import de.janquadflieg.mrracer.telemetry.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.*;
import javax.swing.*;

/**
 *
 * @author Jan Quadflieg
 */
public class TelemetryDisplay
        extends JComponent
        implements TelemetryListener {

    public enum Display {

        SPEED(0.0, Plan2011.MAX_SPEED) {            
        },
        LAT_SPEED(-20.0, 20.0),
        POSITION(-1.0, 1.0),
        ANGLE_TO_TRACK(-Math.PI, Math.PI),
        ACTIONS(-1.0, 1.0);
        protected final static Color DARK_GREEN = new Color(0, 129, 36);
        protected final static int X_INC = 1;
        /** Min and max value to display, other values are clamped. */
        protected double min, max;
        /** Current x coordinate. */
        protected int x = 0;
        /** Screen position and size. */
        protected Rectangle coords = new Rectangle();

        /** Constructor. */
        Display(double min, double max) {
            this.min = min;
            this.max = max;
        }

        public int getPrefferedHeight() {
            return 100;
        }

        public void paint(Graphics2D g, SensorData s1, SensorData s2,
                de.janquadflieg.mrracer.telemetry.Action a1,
                de.janquadflieg.mrracer.telemetry.Action a2) {
            g.translate(coords.x, coords.y);
            paintImpl(g, s1, s2, a1, a2);            
            g.translate(-coords.x, -coords.y);
        }

        public void paintBackground(Graphics2D g){
            g.setColor(Color.LIGHT_GRAY);
            g.fill(coords);
        }

        protected void paintImpl(Graphics2D g, SensorData s1, SensorData s2,
                de.janquadflieg.mrracer.telemetry.Action a1,
                de.janquadflieg.mrracer.telemetry.Action a2) {
            g.setColor(DARK_GREEN);

            double f1 = s1.getSpeed() / Plan2011.MAX_SPEED;
            double f2 = s2.getSpeed() / Plan2011.MAX_SPEED;
            paintValue(g, f1, f2);
        }

        protected void paintValue(Graphics2D g, double f1, double f2) {
            int y1 = Utils.truncate((int) Math.round(f1 * coords.height), 0, coords.height);            
            int y2 = Utils.truncate((int) Math.round(f2 * coords.height), 0, coords.height);

            g.drawLine(x, coords.height - y1, x + X_INC, coords.height - y2);
            x += X_INC;
        }

        public void reset(){
            x = 0;
        }

        public void setBounds(int x, int y, int width, int height) {
            coords.setBounds(x, y, width, height);
        }
    }
    /** The telemetry object belonging to this display. */
    private Telemetry telemetry;
    /** First offscreen buffer. */
    private BufferedImage buffer1 = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
    /** Second offscreen buffer. */
    private BufferedImage buffer2 = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
    /** The front buffer. */
    private BufferedImage front = buffer1;
    /** The back buffer. */
    private BufferedImage back = buffer2;
    /** SensorData. */
    private ModifiableSensorData oldSData = new ModifiableSensorData();
    /** SensorData.*/
    private ModifiableSensorData toDrawSOld = new ModifiableSensorData();
    /** SensorData.*/
    private ModifiableSensorData toDrawSNew = new ModifiableSensorData();
    /** ActionData.*/
    private ModifiableAction oldAData = new ModifiableAction();
    /** ActionData.*/
    private ModifiableAction toDrawAOld = new ModifiableAction();
    /** ActionData.*/
    private ModifiableAction toDrawANew = new ModifiableAction();
    /** Updater. */
    private Updater updater;
    
    /** The size and position of the speed data area. */
    private Rectangle latspeedarea = new Rectangle();
    /** The size and position of the position data area. */
    private Rectangle positionarea = new Rectangle();
    /** The size and position of the angle to track axis data area. */
    private Rectangle trackanglearea = new Rectangle();
    /** The size and position of the action data area. */
    private Rectangle actionarea = new Rectangle();
    /** The Current x coordinate, when drawing incremental. */
    private int x = 0;
    /** Resolution (meter per pixel). */
    private double mpp = 1.0;
    /** X Increment. */
    private final static int X_INC = 1;
    /** The current state of the display. */
    private int state;
    /** The lap to draw. */
    private int lap = -1;
    /** State indicating that the buffers are recreated. */
    private static final int RESET_BUFFERS = 0x000001;
    /** State indicating that a complete redraw takes place. */
    private static final int COMPLETE_REPAINT = 0x000002;
    /** State indicatind that data just gets appended. */
    private static final int PAINT_APPEND = 0x000004;
    private final Color DARK_GREEN = new Color(0, 129, 36);

    public TelemetryDisplay(Telemetry t) {
        super();

        this.telemetry = t;

        telemetry.addListener(this);

        this.addComponentListener(new ComponentAdapter() {

            @Override
            public void componentResized(ComponentEvent e) {
                componentResizedImpl();
            }
        });

        updater = new Updater(this);
    }

    private synchronized void componentResizedImpl() {
        state = RESET_BUFFERS;
        updater.interrupt();
    }

    @Override
    public void newLap() {
        synchronized (this) {
            state = COMPLETE_REPAINT;
        }
    }

    @Override
    public void loaded() {
        synchronized (this) {
            state = COMPLETE_REPAINT;
        }
    }

    @Override
    public void cleared() {
        synchronized (this) {
            oldSData.setData(new SensorData());
            toDrawSOld.setData(new SensorData());
            toDrawSNew.setData(new SensorData());
            state = COMPLETE_REPAINT;
            updater.interrupt();
        }
    }

    public synchronized void setLap(int l) {
        state = COMPLETE_REPAINT;
        lap = l;
        updater.interrupt();
    }

    @Override
    public void newData(SensorData model, de.janquadflieg.mrracer.telemetry.Action a, String l, Telemetry.Mode mode) {
        if (oldSData.getTimeStamp() == Utils.NO_DATA_L) {
            oldSData.setData(model);
            oldAData.setData(a);

        } else if (model.getDistanceRaced() - oldSData.getDistanceRaced() >= (X_INC * mpp)) {
            synchronized (this) {
                toDrawSOld.setData(oldSData);
                toDrawSNew.setData(model);

                toDrawAOld.setData(oldAData);
                toDrawANew.setData(a);
            }
            oldSData.setData(model);
            oldAData.setData(a);
            updater.interrupt();
        }
    }

    @Override
    public void modeChanged(int newMode) {
    }

    private synchronized void copyToBackBuffer() {
        Graphics2D g = this.back.createGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, front.getWidth(), front.getHeight());
        g.drawImage(front, 0, 0, this);
    }

    private synchronized void asyncRepaint() {
        Graphics2D g = this.back.createGraphics();

        g.setColor(Color.WHITE);
        g.fillRect(0, 0, getWidth(), getHeight());

        g.setColor(Color.BLACK);
        g.drawString("Current lap: " + telemetry.getCurrentLap(), 200, 20);
        g.drawString("TrackLength: " + telemetry.getTrackLength(), 200, 40);
        g.drawString("Lap to draw: " + lap, 200, 60);
        
        for(Display d: Display.values()){
            d.reset();
            d.paintBackground(g);
        }
        
        g.fill(latspeedarea);
        g.fill(positionarea);
        g.fill(trackanglearea);
        g.fill(actionarea);

        mpp = telemetry.getTrackLength() / this.getWidth();
        g.drawString(String.valueOf(mpp), 10, 30);

        x = 0;

        if (lap != -1) {
            int idx1 = telemetry.getLapStartIndex(lap);
            int idx2 = telemetry.getLapEndIndex(lap);

            double d = telemetry.getSensorData(idx1).getDistanceFromStartLine();
            x = (int) Math.round(d / mpp);

            toDrawSOld.setData(telemetry.getSensorData(idx1));
            toDrawAOld.setData(telemetry.getAction(idx1));

            for (int i = idx1 + 1; i <= idx2; ++i) {
                boolean draw = i == idx2;

                double dist = (x + X_INC) * mpp;

                draw |= telemetry.getSensorData(i).getDistanceFromStartLine() >= dist;

                if (draw) {
                    toDrawSNew.setData(telemetry.getSensorData(i));
                    toDrawANew.setData(telemetry.getAction(i));

                    incPaint(false);

                    toDrawSOld.setData(toDrawSNew);
                    toDrawAOld.setData(toDrawANew);
                }
            }
        }

        swapBuffers();

        copyToBackBuffer();

        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                repaint();
            }
        });
    }

    @Override
    public void paintComponent(Graphics graphics) {
        Graphics2D g = (Graphics2D) graphics;

        g.setColor(Color.WHITE);

        g.fillRect(0, 0, getWidth(), getHeight());

        synchronized (this) {
            g.drawImage(front, 0, 0, this);
        }
    }

    private void incPaint(boolean swap) {
        Graphics2D g = back.createGraphics();

        for (Display d : Display.values()) {
            
        }

        Display.SPEED.paint(g, toDrawSOld, toDrawSNew, toDrawAOld, toDrawANew);

        // sensors        
        g.translate(latspeedarea.x, latspeedarea.y);
        incPaintLatSpeed(g, latspeedarea.height);
        g.translate(-latspeedarea.x, -latspeedarea.y);

        g.translate(positionarea.x, positionarea.y);
        incPaintPosition(g, positionarea.height);
        g.translate(-positionarea.x, -positionarea.y);

        g.translate(trackanglearea.x, trackanglearea.y);
        incPaintTrackAngle(g, trackanglearea.height);
        g.translate(-trackanglearea.x, -trackanglearea.y);

        // actions
        g.translate(actionarea.x, actionarea.y);
        incPaintBrake(g, actionarea.height);
        incPaintAccelerate(g, actionarea.height);
        incPaintSteering(g, actionarea.height);
        g.translate(-actionarea.x, -actionarea.y);

        x += X_INC;

        if (swap) {
            swapBuffers();
            copyToBackBuffer();
        }
    }   

    private void incPaintLatSpeed(Graphics2D g, int height) {
        g.setColor(Color.ORANGE);

        double f1 = toDrawSOld.getLateralSpeed() / 30.0;
        int y1 = Utils.truncate((int) Math.round(f1 * height / 2), -height / 2, height / 2);

        double f2 = toDrawSNew.getLateralSpeed() / 30.0;
        int y2 = Utils.truncate((int) Math.round(f2 * height / 2), -height / 2, height / 2);

        g.drawLine(x, (height / 2) - y1, x + X_INC, (height / 2) - y2);
    }

    private void incPaintPosition(Graphics2D g, int height) {
        g.setColor(Color.ORANGE);

        double f1 = toDrawSOld.getTrackPosition() / 1.0;
        int y1 = Utils.truncate((int) Math.round(f1 * height / 2), -height / 2, height / 2);

        double f2 = toDrawSNew.getTrackPosition() / 1.0;
        int y2 = Utils.truncate((int) Math.round(f2 * height / 2), -height / 2, height / 2);

        g.drawLine(x, (height / 2) - y1, x + X_INC, (height / 2) - y2);
    }

    private void incPaintTrackAngle(Graphics2D g, int height) {
        g.setColor(Color.ORANGE);

        double f1 = toDrawSOld.getAngleToTrackAxis() / Math.PI;
        int y1 = Utils.truncate((int) Math.round(f1 * height / 2), -height / 2, height / 2);

        double f2 = toDrawSNew.getAngleToTrackAxis() / Math.PI;
        int y2 = Utils.truncate((int) Math.round(f2 * height / 2), -height / 2, height / 2);

        g.drawLine(x, (height / 2) - y1, x + X_INC, (height / 2) - y2);
    }

    private void incPaintBrake(Graphics2D g, int height) {
        g.setColor(Color.RED);

        int y1 = (int) Math.round(toDrawAOld.getBrake() * (height / 2));
        int y2 = (int) Math.round(toDrawANew.getBrake() * (height / 2));

        g.drawLine(x, (height / 2) + y1, x + X_INC, (height / 2) + y2);
    }

    private void incPaintAccelerate(Graphics2D g, int height) {
        g.setColor(DARK_GREEN);
        int y1 = (int) Math.round(toDrawAOld.getAcceleration() * (height / 2));
        int y2 = (int) Math.round(toDrawANew.getAcceleration() * (height / 2));

        g.drawLine(x, (height / 2) - y1, x + X_INC, (height / 2) - y2);
    }

    private void incPaintSteering(Graphics2D g, int height) {
        g.setColor(Color.BLUE);
        int y1 = (int) Math.round(toDrawAOld.getSteering() * -1 * (height / 2));
        int y2 = (int) Math.round(toDrawANew.getSteering() * -1 * (height / 2));

        g.drawLine(x, (height / 2) + y1, x + X_INC, (height / 2) + y2);
    }

    private synchronized void resetBuffers() {
        this.buffer1 = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
        this.buffer2 = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
        this.front = buffer1;
        this.back = buffer2;

        Display.SPEED.setBounds(0, 100, getWidth(), 200);
        latspeedarea.setBounds(0, 320, getWidth(), 100);
        positionarea.setBounds(0, 440, getWidth(), 100);
        trackanglearea.setBounds(0, 560, getWidth(), 100);
        actionarea.setBounds(0, 680, getWidth(), 150);

        Graphics2D g = this.front.createGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, getWidth(), getHeight());
        g.setColor(Color.BLACK);
        g.drawString("resetBuffers: buffer 1", 30, 30);
        g.drawString(getWidth() + " / " + getHeight(), 30, 60);


        g = this.back.createGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, getWidth(), getHeight());
        g.setColor(Color.BLACK);
        g.drawString("resetBuffers: buffer 2", 30, 30);
        g.drawString(getWidth() + " / " + getHeight(), 30, 60);
        //g.drawImage(front, 0, 0, this);
        //g.drawString("This is the backbuffer", 30, 90);
        //g.setColor(Color.LIGHT_GRAY);
        //g.fill(sensorarea);
        //g.fill(actionarea);
    }

    private synchronized void swapBuffers() {
        if (front == buffer1) {
            front = buffer2;
            back = buffer1;

        } else {
            front = buffer1;
            back = buffer2;
        }
    }

    private static class Updater
            implements Runnable {

        private TelemetryDisplay peer = null;
        private Thread t = null;
        private boolean keepRunning = true;

        public Updater(TelemetryDisplay d) {
            this.peer = d;
            t = new Thread(this, "Telemetry Updater");
            t.start();
        }

        public void interrupt() {
            t.interrupt();
        }

        @Override
        public void run() {
            while (keepRunning) {
                if (peer.state == TelemetryDisplay.COMPLETE_REPAINT) {
                    peer.asyncRepaint();
                    peer.state = TelemetryDisplay.PAINT_APPEND;

                } else if (peer.state == TelemetryDisplay.RESET_BUFFERS) {
                    peer.resetBuffers();
                    peer.asyncRepaint();
                    peer.state = TelemetryDisplay.PAINT_APPEND;

                } else if (peer.state == TelemetryDisplay.PAINT_APPEND) {
                    peer.incPaint(true);
                }

                SwingUtilities.invokeLater(new Runnable() {

                    public void run() {
                        peer.repaint();
                    }
                });
                synchronized (this) {
                    try {
                        wait();
                    } catch (InterruptedException e) {
                    }
                }
            }
        }

        public void stop() {
            keepRunning = false;
            t.interrupt();
        }
    }
}
