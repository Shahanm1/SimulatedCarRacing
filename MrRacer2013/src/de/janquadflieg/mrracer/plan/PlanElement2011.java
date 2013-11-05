/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.janquadflieg.mrracer.plan;

import de.janquadflieg.mrracer.Utils;
import de.janquadflieg.mrracer.telemetry.SensorData;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;

import java.util.ArrayList;
import java.util.Collections;

import de.janquadflieg.mrracer.functions.Interpolator;

/**
 *
 * @author Jan Quadflieg
 */
public class PlanElement2011 {

    private static final boolean ERROR_HANDLING_BY_EXCEPTION = false;
    private static final Color DARK_GREEN = new Color(0, 129, 36);
    
    private double start, end;
    private ArrayList<Interpolator> positions = new ArrayList<>();
    private ArrayList<Interpolator> speeds = new ArrayList<>();
    private String info = "";    

    public PlanElement2011(double start, double end, String info) {
        this.start = start;
        this.end = end;
        this.info = info;
    }
    
    public String getInfo() {
        return info;
    }

    public boolean contains(double d) {
        return (d >= start && d <= end);
    }

    public double getStart() {
        return start;
    }

    public double getEnd() {
        return end;
    }

    public double getLength() {
        return getEnd() - getStart();
    }

    protected void attachPosition(Interpolator spline) {
        this.positions.add(spline);
    }

    protected void setPosition(Interpolator spline) {
        if (spline.getXmin() != start || spline.getXmax() != end) {
            System.out.println(spline.getXmin() + "," + this.start);
            System.out.println(spline.getXmax() + "," + this.end);
            if (ERROR_HANDLING_BY_EXCEPTION) {
                throw new RuntimeException("Position spline limits don't match this plan element.");
            } else {
                System.out.println("Position spline limits don't match this plan element.");
            }
        }
        this.positions.clear();
        this.positions.add(spline);
    }

    protected void setSpeed(Interpolator spline) {
        if (spline.getXmin() != start || spline.getXmax() != end) {
            System.out.println(spline.getXmin() + "," + this.start);
            System.out.println(spline.getXmax() + "," + this.end);
            if (ERROR_HANDLING_BY_EXCEPTION) {
                throw new RuntimeException("Speed spline limits don't match this plan element.");
            } else {
                System.out.println("Speed spline limits don't match this plan element.");
            }
        }
        this.speeds.clear();
        this.speeds.add(spline);
    }

    protected void attachSpeed(Interpolator spline) {
        this.speeds.add(spline);
    }

    public double getPosition(double d) {
        if (positions.isEmpty()) {
            return Plan2011.NO_RACE_LINE;

        } else {
            for (Interpolator position : positions) {
                if (position.getXmin() <= d && position.getXmax() >= d) {
                    return position.interpolate(d);
                }
            }
        }
        return Plan2011.NO_RACE_LINE;
    }

    public double getSpeed(double d) {
        if (speeds.isEmpty()) {
            return 50.0;
        } else {
            for (Interpolator speed : speeds) {
                if (speed.getXmin() <= d && speed.getXmax() >= d) {
                    return speed.interpolate(d);
                }
            }
        }
        return 50.0;
    }

    public void printSpeeds() {
        System.out.println("Speeds:");
        if (speeds.isEmpty()) {
            System.out.println("50km/h");
        } else {
            Collections.sort(speeds);
            for (Interpolator i : speeds) {
                System.out.println(Utils.dTS(i.getXmin())
                        + ": " + Utils.dTS(i.interpolate(i.getXmin())) + "km/h, "
                        + Utils.dTS(i.getXmax()) + ": "
                        + Utils.dTS(i.interpolate(i.getXmax())) + "km/h");
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();

        result.append("PlanElement ").append(Utils.dTS(getStart()));
        result.append("-");
        if (getPosition(getStart()) == Plan2011.NO_RACE_LINE) {
            result.append("NRL");

        } else {
            result.append(Utils.dTS(getPosition(getStart())));
        }
        result.append(" : ").append(Utils.dTS(getEnd())).append("-");
        if (getPosition(getEnd()) == Plan2011.NO_RACE_LINE) {
            result.append("NRL");

        } else {
            result.append(Utils.dTS(getPosition(getEnd())));
        }

        return result.toString();
    }

    public static class Painter {

        private static final int BLOCK_HEIGHT = 100;
        private static final int BLOCK_DISTANCE = 10;

        public static Dimension draw(PlanElement2011 e, Graphics2D g, double ppm,
                SensorData data, ArrayList<SensorData> debugData) {
            Dimension result = new Dimension();

            result.width = (int) Math.round(e.getLength() * ppm);
            result.height = (2 * BLOCK_HEIGHT) + BLOCK_DISTANCE;

            g.setColor(DARK_GREEN);

            g.fillRect(0, 0, result.width, BLOCK_HEIGHT);
            g.fillRect(0, BLOCK_HEIGHT + BLOCK_DISTANCE, result.width, BLOCK_HEIGHT);


            g.setColor(Color.WHITE);
            g.fillRect(4, 4, result.width - 8, BLOCK_HEIGHT - 8);
            g.fillRect(4, BLOCK_HEIGHT + BLOCK_DISTANCE + 4, result.width - 8, BLOCK_HEIGHT - 8);

            final double mpp = e.getLength() / (result.width * 1.0);

            for (int x = 0; x < result.width; ++x) {
                double d = e.getStart() + (mpp * x);
                double v = e.getSpeed(d);

                g.setColor(Color.GREEN);
                int y = BLOCK_HEIGHT - 4 - (int) Math.round((BLOCK_HEIGHT - 8) * (v / Plan2011.MAX_SPEED));
                g.drawRect(x, y, 1, 1);

                v = e.getPosition(d);
                if (v != Plan2011.NO_RACE_LINE) {
                    v = Math.max(-1.0, Math.min(1.0, v));
                    y = (2 * BLOCK_HEIGHT) + BLOCK_DISTANCE - 4 - (int) Math.round((BLOCK_HEIGHT - 8) * ((v + 1.0) / 2.0));
                    g.drawRect(x, y, 1, 1);
                }
            }

            if (e.contains(data.getDistanceRaced())) {
                g.setColor(Color.MAGENTA);
                int x = (int) Math.round((data.getDistanceRaced() - e.getStart()) * ppm);
                g.drawLine(x, 0, x, BLOCK_HEIGHT);
                g.drawLine(x, BLOCK_HEIGHT + BLOCK_DISTANCE, x, (2 * BLOCK_HEIGHT) + BLOCK_DISTANCE);
            }

            g.setColor(Color.BLACK);
            String s = Utils.dTS(e.getStart());
            if (result.width >= g.getFontMetrics().stringWidth(s) + 5) {
                g.drawString(s, 5, (2 * BLOCK_HEIGHT) + BLOCK_DISTANCE + g.getFontMetrics().getHeight());
            }

            s = Utils.dTS(e.getEnd());
            if (result.width >= g.getFontMetrics().stringWidth(s) + 5) {
                g.drawString(s, result.width - (g.getFontMetrics().stringWidth(s) + 5),
                        (2 * BLOCK_HEIGHT) + BLOCK_DISTANCE + g.getFontMetrics().getHeight());
            }

            g.setColor(Color.MAGENTA);

            for (int i = 0; i < debugData.size(); ++i) {
                SensorData d = debugData.get(i);

                int x = (int) Math.round((d.getDistanceRaced() - e.getStart()) * ppm);

                if (x < 0 || x > result.width) {
                    continue;
                }

                double v = d.getSpeed();
                v = Math.max(0.0, Math.min(330.0, v));
                int y = BLOCK_HEIGHT - 4 - (int) Math.round((BLOCK_HEIGHT - 8) * (v / Plan2011.MAX_SPEED));
                g.drawRect(x, y, 1, 1);

                v = d.getTrackPosition();
                v = Math.max(-1.0, Math.min(1.0, v));
                y = (2 * BLOCK_HEIGHT) + BLOCK_DISTANCE - 4 - (int) Math.round((BLOCK_HEIGHT - 8) * ((v + 1.0) / 2.0));
                g.drawRect(x, y, 1, 1);
            }


            return result;
        }
    }
}