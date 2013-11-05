/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.janquadflieg.mrracer.plan;

import de.janquadflieg.mrracer.behaviour.Component;
import de.janquadflieg.mrracer.Utils;

import java.io.*;
import java.util.*;
import javax.vecmath.Point4d;

/**
 *
 * @author quad
 */
public class BrakePredictor
        implements Component {

    private final static double G = 9.81;
    public static final String CW = "-BrakePredictor.CW-";
    private double cw = 0.076771125;
    public static final String CA = "-BrakePredictor.CA-";
    private double ca = 3.64999;
    public static final String MASS = "-BrakePredictor.MASS-";
    private double mass = 1150.0;
    ArrayList<Point4d> list = new ArrayList<>();

    public BrakePredictor() {
        try {
            InputStream in = getClass().getResourceAsStream("/de/janquadflieg/mrracer/data/brake_f1_1");

            BufferedReader reader = new BufferedReader(new InputStreamReader(in));

            String line;

            while ((line = reader.readLine()) != null) {
                line.trim();

                StringTokenizer tokenizer = new StringTokenizer(line, " ");

                if (!line.isEmpty()) {
                    double x = Double.parseDouble(tokenizer.nextToken());
                    double y = Double.parseDouble(tokenizer.nextToken());
                    double z = Double.parseDouble(tokenizer.nextToken());
                    double w = Double.parseDouble(tokenizer.nextToken());

                    list.add(new Point4d(x, y, z, w));
                }
            }

            reader.close();
        } catch (Exception e) {
            e.printStackTrace(System.out);
        }
    }

    public void paint(String baseFileName, java.awt.Dimension d){
    }

    public void setParameters(Properties params, String prefix) {        
        cw = Double.parseDouble(params.getProperty(prefix + BrakePredictor.CW, String.valueOf(cw)));
        ca = Double.parseDouble(params.getProperty(prefix + BrakePredictor.CA, String.valueOf(ca)));
        mass = Double.parseDouble(params.getProperty(prefix + BrakePredictor.MASS, String.valueOf(mass)));
    }

    public void getParameters(Properties params, String prefix) {
        params.setProperty(prefix + BrakePredictor.CW, String.valueOf(cw));
        params.setProperty(prefix + BrakePredictor.CA, String.valueOf(ca));
        params.setProperty(prefix + BrakePredictor.MASS, String.valueOf(mass));
    }

    public final double calcBrakeDistance(double speed, double targetSpeed, double mu, double cc) {
        if (targetSpeed >= speed) {
            return 0.0;
        }

        return calcBrakeDistanceAnalytical(speed, targetSpeed, mu, cc);
    }

    public final double calcBrakeDistanceLUT(double speed, double targetSpeed, double cc) {
        if (targetSpeed >= speed) {
            return 0.0;
        }
        Point4d candidate = list.get(0);
        double dist = (candidate.x - speed) * (candidate.x - speed) + (candidate.y - targetSpeed) * (candidate.y - targetSpeed);
        int bestIdx = 0;

        for (int i = 1; i < list.size(); ++i) {
            candidate = list.get(i);
            double dist2 = (candidate.x - speed) * (candidate.x - speed) + (candidate.y - targetSpeed) * (candidate.y - targetSpeed);
            if (dist2 < dist) {
                bestIdx = i;
                dist = dist2;
            }
        }

//        System.out.println("calcBrake: "+Utils.dTS(speed)+"km/h "+Utils.dTS(targetSpeed)+"km/h");
//        System.out.println(list.get(bestIdx));
//        System.out.println("Result: "+Utils.dTS(list.get(bestIdx).w)+"m");

        return (list.get(bestIdx).w) / cc;
    }

    public final double calcBrakeDistanceAnalytical(final double speed, final double targetSpeed, final double MU, double cc) {
        if (targetSpeed >= speed) {
            return 0.0;
        }

        final double c = MU * G;
        final double d = (ca * MU + cw) / mass;
        final double v1sqr = (speed / 3.6) * (speed / 3.6);
        final double v2sqr = (targetSpeed / 3.6) * (targetSpeed / 3.6);
        double brakedist = -Math.log((c + v2sqr * d) / (c + v1sqr * d)) / (2.0 * d);

        return Math.max(0.0, brakedist / cc);
    }

    public static double calcBrakeDistanceSimple(double speed, double targetSpeed, double cc) {
        //System.out.println("calcBrakeDistance speed="+speed+", targetSpeed="+targetSpeed);

        speed = speed / 3.6;
        targetSpeed = targetSpeed / 3.6;

        // 1/2 * erdbeschleunigung * reibungskoeffizient
        double divisor = 2 * 9.81 * 1.1 * cc;

        double result = ((speed * speed) - (targetSpeed * targetSpeed)) / divisor;

        return Math.max(0, result);
    }

    public final double calcApproachSpeed(double targetSpeed, double distance, double mu, double cc) {
        return calcApproachSpeedAnalytical(targetSpeed, distance, mu, cc);
    }

    public final double calcApproachSpeedLUT(double targetSpeed, double distance, double cc) {
        Point4d candidate = list.get(0);
        double dist = (candidate.y - targetSpeed) * (candidate.y - targetSpeed) + (candidate.w - distance) * (candidate.w - distance);
        int bestIdx = 0;

        for (int i = 1; i < list.size(); ++i) {
            candidate = list.get(i);
            double dist2 = (candidate.y - targetSpeed) * (candidate.y - targetSpeed) + (candidate.w - distance) * (candidate.w - distance);
            if (dist2 < dist) {
                bestIdx = i;
                dist = dist2;
            }
        }

//        System.out.println("calcApp: "+Utils.dTS(targetSpeed)+"km/h "+Utils.dTS(distance)+"m");
//        System.out.println(list.get(bestIdx));
//        System.out.println("Result: "+Utils.dTS(list.get(bestIdx).x)+"km/h");

        return (list.get(bestIdx).x) * cc;
    }

    public final double calcApproachSpeedAnalytical(final double targetSpeed, final double distance, final double MU, double cc) {
        if (distance <= 0) {
            return targetSpeed;
        }

        final double c = MU * G;
        final double d = (ca * MU + cw) / mass;
        //final double v1sqr = (speed / 3.6) * (speed / 3.6);
        final double v2sqr = (targetSpeed / 3.6) * (targetSpeed / 3.6);
        // 1 / (e^((brakedist * (2.0 * d)) * -1.0)) / (c + v2sqr * d) = (c + v1sqr * d);

        double result = distance * (2.0 * d);
        result *= -1.0;
        result = Math.exp(result);
        result = result / (c + v2sqr * d);
        result = 1.0 / result;
        result = result - c;
        result = result / d;
        result = Math.sqrt(result);
        result = result * 3.6;

        return Math.max(targetSpeed, result * cc);
    }

    public static double calcApproachSpeedSimple(double targetSpeed, double distance, double cc) {
        double c = 2 * 9.81 * 1.1 * cc;

        targetSpeed = targetSpeed / 3.6;

        double result = distance * c + (targetSpeed * targetSpeed);

        result = Math.sqrt(result);

        return result * 3.6;
    }

    public static void main(String[] args) {
        double bestSqe = Double.POSITIVE_INFINITY;
        double bestCA = 0.0;

        for (double ca = 0.0; ca < 10.0; ca += 0.01) {
            Properties params = new Properties();
            params.setProperty(de.janquadflieg.mrracer.controller.MrRacer2012.PLAN + Plan2011.BRAKE_PREDICTOR + BrakePredictor.CW,
                    String.valueOf(0.645 * 0.345 * 0.345));
            params.setProperty(de.janquadflieg.mrracer.controller.MrRacer2012.PLAN + Plan2011.BRAKE_PREDICTOR + BrakePredictor.CA,
                    String.valueOf(ca));
            params.setProperty(de.janquadflieg.mrracer.controller.MrRacer2012.PLAN + Plan2011.BRAKE_PREDICTOR + BrakePredictor.MASS,
                    String.valueOf(1150.0));

            //System.out.println(Utils.list(params, "\n"));

            BrakePredictor predictor = new BrakePredictor();
            predictor.setParameters(params, de.janquadflieg.mrracer.controller.MrRacer2012.PLAN + Plan2011.BRAKE_PREDICTOR);
            double sqe = 0.0;

            int ctr = 0;

            for (Point4d point : predictor.list) {
                double ts1 = point.x;
                double ts2 = point.y;
                double d = point.w;

                //double v1 = predictor.calcBrakeDistanceLUT(ts1, ts2, 1.0);
                //double v2 = predictor.calcBrakeDistanceAnalytical(ts1, ts2, 1.1, 1.0);
                double v1 = predictor.calcApproachSpeedLUT(ts2, d, 1.0);
                double v2 = predictor.calcApproachSpeedAnalytical(ts2, d, 1.1, 1.0);
                sqe += (v1 - v2) * (v1 - v2);
                ++ctr;
            }

            sqe /= ctr;

            sqe = Math.sqrt(sqe);

            if (sqe < bestSqe) {
                bestSqe = sqe;
                bestCA = ca;
            }
        }

        System.out.println("Best: " + bestCA + ", " + bestSqe);
    }
}
