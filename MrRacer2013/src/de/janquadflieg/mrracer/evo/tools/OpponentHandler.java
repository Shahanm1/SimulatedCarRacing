/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.janquadflieg.mrracer.evo.tools;

import java.io.*;

/**
 *
 * @author quad
 */
public class OpponentHandler {

    private static final String BASE_PATH = "F:\\Quad\\Experiments\\Opponents";    
    
    private OpponentData[] data = {
        new OpponentData(BASE_PATH + File.separator + "autopia","client.exe", 3002, true),
        new OpponentData(BASE_PATH + File.separator + "mariscal-fernandez","java champ2010client.Client champ2010client.SimpleDriver", 3003, false)
        //new OpponentData(BASE_PATH + File.separator + "Ready2Win","java -jar Ready2WinController.jar controller.Ready2WinController", 3004, false)
    };
    private String track;

    public OpponentHandler(String track) {
        this.track = track;
    }

    public void start() {
        for (OpponentData od : data) {
            od.start(track);
        }

    }

    public void stop() {
        for (OpponentData od : data) {
            od.stop();
        }
    }

    private static class OpponentData {
        private String path;
        private String cmd;
        private int port;
        private Process p;
        private boolean usePath;

        public OpponentData(String path, String s, int i, boolean b) {
            this.path = path;
            cmd = s;
            port = i;
            usePath = b;
        }

        public void start(String track) {
            try {
                String command = usePath ? path+File.separator : "";
                command += cmd+" host:127.0.0.1 port:"+port+" track:"+track+" id:SCR stage:2 verbose:on";
                File dir = new File(path);

                System.out.println(command);
                                
                p = Runtime.getRuntime().exec(command, null, dir);
                StreamGobbler sg = new StreamGobbler(p.getInputStream(), null);
                sg.start();
                sg = new StreamGobbler(p.getErrorStream(), null);
                sg.start();
            } catch (Exception e) {
                e.printStackTrace(System.out);
            }
        }

        public void stop(){
            p.destroy();
            try{
                p.waitFor();
            } catch(Exception e){
                e.printStackTrace(System.out);
            }
        }
    }

    private static class StreamGobbler extends Thread {

        InputStream is;
        FileOutputStream log = null;

        StreamGobbler(InputStream is, String file) {
            super("StreamGobbler");
            this.is = is;

            if (file != null) {
                try {
                    this.log = new FileOutputStream(file);
                } catch (Exception e) {
                    e.printStackTrace(System.out);
                }
            }
        }

        @Override
        public void run() {
            try {
                int b;
                while ((b = is.read()) != -1) {
                    if (log != null) {
                        log.write(b);
                    }
                    //System.out.println(((char)b));
                }
                if (log != null) {
                    log.close();
                }
            } catch (IOException ioe) {
                ioe.printStackTrace(System.out);
            }
        }
    }
}
