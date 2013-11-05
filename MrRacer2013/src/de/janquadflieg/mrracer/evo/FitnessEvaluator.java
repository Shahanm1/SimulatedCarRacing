/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.janquadflieg.mrracer.evo;

import de.janquadflieg.mrracer.controller.CrashController;
import de.janquadflieg.mrracer.controller.Evaluator;
import de.janquadflieg.mrracer.network.*;

import java.io.*;
import java.util.*;

/**
 *
 * @author Jan Quadflieg
 */
public class FitnessEvaluator
        implements Runnable, ConnectionListener {

    private final Object mutex = new Object();
    private Thread thread;
    private boolean finished = false;
    private Evaluator controller;
    private String host;
    private int port;
    private EvoResults result = new EvoResults();
    private FEListener listener = null;
    private UDPConnection connection = null;

    public FitnessEvaluator(String host, int port, Evaluator c) {
        this(host, port, c, Evaluator.NO_MAXIMUM, true, null);
    }

    public FitnessEvaluator(String host, int port, Evaluator c, int maxLaps) {
        this(host, port, c, maxLaps, true, null);
    }

    public FitnessEvaluator(String host, int port, Evaluator c, int maxLaps, boolean checkDamage, FEListener l) {
        listener = l;
        controller = c;
        controller.setMaxLaps(maxLaps);
        this.host = host;
        this.port = port;

        thread = new Thread(this);
        thread.setName("FitnessEvaluator - " + host + ":" + port);
        thread.start();
    }

    public void join() throws InterruptedException{
        thread.join();
    }

    public void run() {
        try {
            controller.resetFull();

            connection = new UDPConnection(host, port, controller);
            connection.addConnectionListener(this);

            synchronized (mutex) {
                mutex.wait();
            }
            controller.shutdown();

            result.damage = controller.getDamage();
            result.distance = controller.getDistanceRaced();
            result.offTrack = controller.getOffTrackCtr();

        } catch (Exception e) {
            e.printStackTrace(System.out);
            result.damage = 10000;
            result.distance = 0;
            result.offTrack = 10000;
        }

        finished = true;
        if (listener != null) {
            listener.finished(this);
        }
    }

    public boolean aborted() {
        return controller.aborted();
    }

    public boolean finished() {
        return finished;
    }

    public EvoResults getResult() {
        return result;
    }

    public int getLapCtr() {
        return controller.getLapCtr();
    }

    public double getDistanceRaced() {
        return controller.getDistanceRaced();
    }

    public double getFastestLap() {
        return controller.getFastestLap();
    }

    public double getOverallTime() {
        return controller.getOverallTime();
    }

    public int getOvertakingCtr() {
        return controller.getOvertakingCtr();
    }

    public double getLatSpeedIntegral() {
        return controller.getLatSpeedIntegral();
    }

    public boolean maxDamageReached() {
        return controller.maxDamageReached();
    }

    public void newStatistics(ConnectionStatistics data) {
    }

    public void stop() {
        controller.stop();
        //if (connection != null) {
        //    connection.stop();
        //}
    }

    public void stopped(boolean requested) {
        synchronized (mutex) {
            mutex.notifyAll();
        }
    }

    public static void fileEvaluation(String[] args, int repeat) {
        System.setProperty("EAMode", "");

        int port = 3001;
        String host = "localhost";
        int maxSteps = 0;
        int maxLaps = 3;
        String trackName = "unknown";
        String resultFile = "results";
        String paramFile = null;
        Properties p = new Properties();
        boolean recovery = false;

        for (int i = 0; i < args.length; i++) {
            StringTokenizer st = new StringTokenizer(args[i], ":");
            String entity = st.nextToken();
            String value = "";
            if (st.hasMoreTokens()) {
                value = st.nextToken();
            }
            if (entity.equals("recovery")) {
                recovery = true;
            }
            if (entity.equals("port")) {
                port = Integer.parseInt(value);
            }
            if (entity.equals("host")) {
                host = value;
            }
            if (entity.equals("trackName")) {
                trackName = value;
            }
            if (entity.equals("results")) {
                resultFile = value;
            }
            if (entity.equals("maxSteps")) {
                maxSteps = Integer.parseInt(value);
                if (maxSteps < 0) {
                    System.out.println(entity + ":" + value
                            + " is not a valid option");
                    System.exit(0);
                }
            }
            if (entity.equals("maxLaps")) {
                maxLaps = Integer.parseInt(value);
                if (maxLaps < 1) {
                    System.out.println(entity + ":" + value
                            + " is not a valid option");
                    System.exit(0);
                }
            }
            if (entity.equals("params")) {
                paramFile = value;
            }
        }

        if (paramFile != null) {            
            try {
                InputStream in = new FileInputStream(paramFile);
                p.load(in);
                in.close();

            } catch (Exception e) {
                e.printStackTrace(System.out);
                return;
            }
        }

        System.out.println("Host: " + host + ":" + port);
        System.out.println("MaxSteps: " + maxSteps);
        System.out.println("MaxLaps: " + maxLaps);
        System.out.println("TrackName: " + trackName);
        System.out.println("Result file: " + resultFile);
        System.out.println("Using crash controller: " + recovery);

        de.janquadflieg.mrracer.controller.MrRacer2012 controller =
                new de.janquadflieg.mrracer.controller.MrRacer2012();
        controller.setStage(scr.Controller.Stage.RACE);
        controller.setTrackName(trackName);
        controller.setParameters(p);

        FitnessEvaluator fe;

        if (recovery) {
            CrashController crashController = new CrashController(controller);
            crashController.setStage(scr.Controller.Stage.RACE);
            crashController.setTrackName(trackName);

            fe = new FitnessEvaluator(host, port,
                    new Evaluator(crashController, maxSteps), maxLaps);

        } else {
            fe = new FitnessEvaluator(host, port,
                    new Evaluator(controller, maxSteps), maxLaps);
        }

        while (!fe.finished()) {
            try {
                Thread.sleep(1000);
            } catch (Exception e) {
            }
        }

        EvoResults r = fe.getResult();

        try {
            String file;
            if (resultFile.lastIndexOf(".") != -1) {
                file = resultFile.substring(0, resultFile.lastIndexOf("."))
                        + String.valueOf(repeat) + resultFile.substring(resultFile.lastIndexOf("."));
            } else {
                file = resultFile + String.valueOf(repeat);
            }
            FileWriter writer = new FileWriter(file);

            writer.write("distance=" + String.valueOf(r.distance) + "\n");
            writer.write("damage=" + String.valueOf(r.damage) + "\n");
            writer.write("offtrack=" + r.offTrack + "\n");
            writer.write("time=" + String.valueOf(fe.getOverallTime()) + "\n");
            writer.write("fastestTime=" + String.valueOf(fe.getFastestLap()) + "\n");
            writer.write("aborted=" + String.valueOf(fe.aborted()));

            writer.flush();
            writer.close();

        } catch (Exception e) {
            e.printStackTrace(System.out);
        }
    }

    public static void main(String[] args) {
        for (int i = 0; i < 1; ++i) {
            FitnessEvaluator.fileEvaluation(args, i + 1);
        }
    }
}
