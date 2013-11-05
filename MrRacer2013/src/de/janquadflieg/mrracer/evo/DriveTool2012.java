/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.janquadflieg.mrracer.evo;

import de.janquadflieg.mrracer.Utils;
import de.janquadflieg.mrracer.controller.*;
import scr.Controller;

import java.io.*;
import java.util.*;

/**
 *
 * @author nostromo
 */
public class DriveTool2012 {

    private static final String TORCS_IP_PORT = "Torcs.IP_PORT";
    private static final String TORCS_MAX_TICKS = "Torcs.MAX_TICKS";
    private static final String TORCS_MAX_LAPS = "Torcs.MAX_LAPS";
    private static final String TORCS_TRACK = "Torcs.TRACK";
    private static final String TORCS_REPEATS = "Torcs.REPEATS";
    private static final String TORCS_INDIVIDUALS = "Torcs.INDIVIDUALS";
    private static final String TORCS_INDS_FOLDER = "Torcs.INDIVIDUALS-FOLDER";
    private static final String FILENAME = "ind-id";
    private int port = 3001;
    private String host = "127.0.0.1";
    private String trackName = "some_track";
    private int maxTicks = de.janquadflieg.mrracer.controller.Evaluator.NO_MAXIMUM;
    private int maxLaps = de.janquadflieg.mrracer.controller.Evaluator.NO_MAXIMUM;
    private int repeats = 1;
    private String folder = ".";
    private ArrayList<File> inds = new ArrayList<>();

    public DriveTool2012(String paramFile) {
        try {
            FileInputStream is = new FileInputStream("." + File.separator + paramFile);
            Properties p = new Properties();
            p.load(is);

            // TORCS_IP_PORT
            String ipport = p.getProperty(TORCS_IP_PORT, host+":"+port);
            int idx = ipport.indexOf(':');
            host = ipport.substring(0, idx);
            port = Integer.parseInt(ipport.substring(idx + 1, ipport.length()));
            System.out.println("Host: "+host+", port: "+port);

            // TORCS_MAX_TICKS
            maxTicks = Integer.parseInt(p.getProperty(TORCS_MAX_TICKS, String.valueOf(maxTicks)));
            System.out.println("MaxTicks: "+maxTicks);

            // TORCS_MAX_LAPS
            maxLaps = Integer.parseInt(p.getProperty(TORCS_MAX_LAPS, String.valueOf(maxLaps)));
            System.out.println("MaxLaps: "+maxLaps);
            
            // TORCS_TRACK
            trackName = p.getProperty(TORCS_TRACK, trackName).trim();
            System.out.println("TrackName: "+trackName);
            
            // TORCS_REPEATS
            repeats = Integer.parseInt(p.getProperty(TORCS_REPEATS, String.valueOf(repeats)));
            System.out.println("Repeats: "+repeats);

            // TORCS_INDS_FOLDER
            folder = p.getProperty(TORCS_INDS_FOLDER, folder);
            System.out.println("Folder: "+folder);

            if (p.contains(TORCS_INDIVIDUALS)) {
                StringTokenizer tokens = new StringTokenizer(p.getProperty(TORCS_INDIVIDUALS), ",");

                while (tokens.hasMoreTokens()) {
                    inds.add(new File("." + File.separator + folder + File.separator + FILENAME + String.valueOf(Integer.parseInt(tokens.nextToken()))));
                }

            }/* else {
            File dir = new File("."+File.separator+folder);
            File[] files = dir.listFiles();
            }  */

        } catch (Exception e) {
            e.printStackTrace(System.out);
        }
    }

    public void run()
            throws Exception {
        if (inds.isEmpty()) {
            System.out.println("No individuals given, listing files");
            File dir = new File("." + File.separator + folder);
            File[] files = dir.listFiles();
            for (File f : files) {
                System.out.println(f.getAbsolutePath());
                inds.add(f);
            }
        }

        for (File f : inds) {
            System.out.println("Evaluating individual " + f.getName());

            BufferedWriter log = new BufferedWriter(new FileWriter(".\\" + trackName + "-log-" + f.getName() + ".txt"));

            // write header
            log.write("repeat time damage latS");
            log.write("\n");
            log.flush();

            for (int i = 0; i < repeats; ++i) {
                log.write((i + 1) + " ");
                evaluate(f, log);
            }

            log.flush();
            log.close();

            System.out.println("");
        }
    }

    private void evaluate(File file, BufferedWriter log)
            throws Exception {
        System.setProperty("EAMode", "");
        MrRacer2012 c = new MrRacer2012(null, file.getAbsolutePath(), false);

        Properties p = new Properties();
        p.load(new FileInputStream(file));
        //p.list(System.out);

        c.setStage(scr.Controller.Stage.QUALIFYING);
        c.setTrackName(trackName);
        c.setParameters(p);

        FitnessEvaluator fe = new FitnessEvaluator(host, port,
                new Evaluator(c, maxTicks), maxLaps);

        while (!fe.finished()) {
            try {
                Thread.sleep(1000);
            } catch (Exception e) {
                e.printStackTrace(System.out);
            }
        }
        log.write(fe.getOverallTime() + " " + fe.getResult().damage + " " + fe.getLatSpeedIntegral() + "\n");
        log.flush();
    }

    public static void main(String[] args) {
        try {
            System.out.println("DriveTool2012");
            for (String s : args) {
                System.out.println(s);
            }
            DriveTool2012 tool = new DriveTool2012(args[0]);
            tool.run();

        } catch (Exception e) {
            e.printStackTrace(System.out);
        }
    }
}
