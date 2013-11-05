/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.janquadflieg.mrracer.plan;

import java.awt.geom.Point2D;
import java.io.*;
import java.util.*;

/**
 *
 * @author quad
 */
public class SpeedPredictor {

    ArrayList<Point2D> list = new ArrayList<>();

    //ArrayList<ArrayList<Point2D>> lists = new ArrayList<>();
    //ArrayList<Point2D> speedBounds = new ArrayList<>();
    //ArrayList<Point2D> distanceBounds = new ArrayList<>();
    public SpeedPredictor() {
        try {
            InputStream in = getClass().getResourceAsStream("/de/janquadflieg/mrracer/data/SpeedPrediction");

            BufferedReader reader = new BufferedReader(new InputStreamReader(in));

            String line;

            while ((line = reader.readLine()) != null) {
                line.trim();

                if (!line.isEmpty()) {
                    double distance = Double.parseDouble(line.substring(0, line.indexOf(";")));
                    double speed = Double.parseDouble(line.substring(line.indexOf(";") + 1, line.length()));

                    list.add(new Point2D.Double(distance, speed));
                }
            }

            /*for(int i=1; i < list.size(); ++i){
                if(list.get(i).getY() <= list.get(i-1).getY()){
                    System.out.println(list.get(i-1));
                    System.out.println(list.get(i));
                    System.exit(1);
                }
            }

            for (Point2D p : list) {
                System.out.println(p);
            }*/

            reader.close();
        } catch (Exception e) {
            e.printStackTrace(System.out);
        }
    }

    /*private static double predictSpeedOld(double currentSpeed, double accDistance) {
        final double[] SPEED = {0.0443872, 46.2205, 61.2405, 75.9582,
            89.3808, 101.969, 113.35, 122.522, 125.621, 131.856, 138.126, 143.926, 149.735,
            155.024, 160.068, 164.839, 168.601, 169.251, 172.511, 175.752, 178.972, 181.884,
            184.779, 187.658, 190.522, 193.368, 196.198, 198.967, 201.38, 203.72, 205.99,
            208.192, 210.329, 212.401, 214.412, 216.133, 215.407, 216.995, 218.598,
            220.205, 221.813, 223.419, 225.022, 226.441, 227.849, 229.244, 230.624, 231.984,
            233.337, 234.678, 236.005, 237.321, 238.631, 239.932, 241.223, 242.491,
            243.731, 244.944, 245.747, 246.048, 247.099, 248.182, 249.269, 250.356, 251.444,
            252.516, 253.563, 254.508, 255.46, 256.402, 257.323, 258.222, 259.097, 259.944,
            260.758, 261.544, 262.315, 263.066, 263.798, 264.513, 265.213, 264.408,
            264.308, 264.882, 265.456, 266.032, 266.6, 267.156, 267.696, 268.213,
            268.69, 269.125, 269.532, 269.926, 270.286};

        accDistance = Math.max(0.0, accDistance);

        double delta = Math.abs(SPEED[0] - currentSpeed);

        int i = 1;
        for (; i < SPEED.length; ++i) {
            double delta2 = Math.abs(SPEED[i] - currentSpeed);
            if (delta2 < delta) {
                delta = delta2;
            } else {
                break;
            }
        }

        i += (int) Math.floor(accDistance / 10.0);

        if (i >= SPEED.length) {
            //System.out.println("Ui, can't tell...");
            return 275.0;
        } else {
            i = Math.max(0, i);
            return Math.max(SPEED[i], currentSpeed);
        }
    }*/

    public final double predictSpeed(final double currentSpeed, double accDistance) {
        //System.out.println("predictSpeed: current="+currentSpeed+", accDist="+accDistance);
        accDistance = Math.max(0.0, accDistance);

        if(accDistance == 0.0){
            return currentSpeed;
        }

        int bestIdx = 0;
        double delta = Math.abs(list.get(0).getY() - currentSpeed);

        for (int i = 1; i < list.size(); ++i) {
            double delta2 = Math.abs(list.get(i).getY() - currentSpeed);
            if (delta2 < delta) {
                delta = delta2;
                bestIdx = i;
            } else {
                break;
            }
        }

        //System.out.println(list.get(bestIdx));

        double distance = list.get(bestIdx).getX() + accDistance;
        delta = Double.POSITIVE_INFINITY;

        if (distance > list.get(list.size() - 1).getX()) {
            return 299.0;
        }

        for (int i = bestIdx; i < list.size(); ++i) {
            double delta2 = Math.abs(list.get(i).getX() - distance);



            if (delta2 < delta) {
                delta = delta2;
                bestIdx = i;
            } else {
                break;
            }
        }

        //System.out.println(list.get(bestIdx));

        return list.get(bestIdx).getY();
    }

    /*public SpeedPredictor() {
    try {
    InputStream in = getClass().getResourceAsStream("/de/janquadflieg/mrracer/data/SpeedPrediction");

    BufferedReader reader = new BufferedReader(new InputStreamReader(in));

    String line;
    ArrayList<Point2D> list = null;
    lists.add(new ArrayList<Point2D>());

    while ((line = reader.readLine()) != null) {
    line.trim();

    list = lists.get(lists.size() - 1);

    if (!line.isEmpty()) {
    double distance = Double.parseDouble(line.substring(0, line.indexOf(";")));
    double speed = Double.parseDouble(line.substring(line.indexOf(";") + 1, line.length()));

    list.add(new Point2D.Double(distance, speed));

    } else {
    speedBounds.add(new Point2D.Double(
    list.get(0).getY(), list.get(list.size() - 1).getY()));
    distanceBounds.add(new Point2D.Double(
    list.get(0).getX(), list.get(list.size() - 1).getX()));

    lists.add(new ArrayList<Point2D>());
    }
    }

    speedBounds.add(new Point2D.Double(
    list.get(0).getY(), list.get(list.size() - 1).getY()));
    distanceBounds.add(new Point2D.Double(
    list.get(0).getX(), list.get(list.size() - 1).getX()));

    System.out.println(lists.size() + " Listen");
    for(int i=0; i < lists.size(); ++i){
    System.out.println((i+1)+".: ["+Utils.dTS(distanceBounds.get(i).getX())+"m, "+
    Utils.dTS(distanceBounds.get(i).getY())+"m]");
    }


    reader.close();
    } catch (Exception e) {
    e.printStackTrace(System.out);
    }
    }*/
    /*public void testOld() {
        for (double speed = 3.1; speed < 299; speed = speed + 1.4) {
            for (double distance = 10.0; distance < 1000; distance += 13.7) {
                double d = predictSpeed(speed, distance);
            }
        }
    }

    public void testNew() {
        for (double speed = 3.1; speed < 299; speed = speed + 1.4) {
            for (double distance = 10.0; distance < 1000; distance += 13.7) {
                double d = predictSpeed2(speed, distance);
            }
        }
    }

    public static void main(String[] args) {
        SpeedPredictor p = new SpeedPredictor();

        long start = System.currentTimeMillis();

        for (int i = 0; i < 1000; ++i) {
            p.testOld();
        }

        long time = System.currentTimeMillis() - start;

        System.out.println("Old: " + time);

        start = System.currentTimeMillis();

        for (int i = 0; i < 1000; ++i) {
            p.testNew();
        }

        time = System.currentTimeMillis() - start;

        System.out.println("New: " + time);

    }*/
}
