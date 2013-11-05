/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.janquadflieg.mrracer.network;

import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import java.util.ArrayList;

import scr.Action;
import scr.Controller;
import scr.MessageBasedSensorModel;
import scr.MessageParser;
import scr.SensorModel;

/**
 *
 * @author Jan Quadflieg
 */
public class UDPConnection
        implements Connection, Runnable {

    private static final String CLIENT_ID = "SCR";
    private InetSocketAddress server;
    private Controller controller;
    private boolean keepRunning = true;
    private Thread thread;
    private DatagramSocket socket;
    private byte[] rcvBuffer = new byte[4096];
    private ArrayList<ConnectionListener> listeners = new ArrayList<>();
    private int minLat = Integer.MAX_VALUE;
    private int maxLat = Integer.MIN_VALUE;
    private int packetCounter = 0;
    private long lastUpdate = 0;
    private long lastPacket = 0;

    public UDPConnection(String host, int port, Controller c)
            throws Exception {
        socket = new DatagramSocket();
        server = new InetSocketAddress(host, port);
        this.controller = c;

        thread = new Thread(this, "UDP Connection " + host + ":" + port);
        thread.start();
    }

    @Override
    public void addConnectionListener(ConnectionListener l) {
        listeners.add(l);
    }

    @Override
    public void removeConnectionListener(ConnectionListener l) {
        listeners.remove(l);
    }

    private void notifyListeners(boolean requested) {
        ArrayList<ConnectionListener> toNotify = new ArrayList<>(listeners);
        for (ConnectionListener l : toNotify) {
            l.stopped(requested);
        }
    }

    private void notifyListeners(final ConnectionStatistics data) {
        ArrayList<ConnectionListener> toNotify = new ArrayList<>(listeners);
        for (ConnectionListener l : toNotify) {
            l.newStatistics(data);
        }
    }

    private void handShake()
            throws Exception {
        boolean success = false;
        int i = 0;

        /* Build init string */
        float[] angles = controller.initAngles();
        String initStr = CLIENT_ID + "(init";
        for (int j = 0; j < angles.length; j++) {
            initStr = initStr + " " + angles[j];
        }
        initStr = initStr + ")";

        for (; !success && i < 100; ++i) {
            try {
                socket.setSoTimeout(1000);
                send(initStr);
                String reply = receive();
                success = reply.contains("identified");
                socket.setSoTimeout(0);
            } catch (java.net.SocketTimeoutException e) {
                //System.out.println("Time out");
            }
        }

        if (success) {
            //System.out.println("Connected, after "+i+" packets");
        } else {
            throw new Exception("Connection failed");
        }
    }

    @Override
    public void run() {
        Action action = new Action();

        String reply;
        try {
            handShake();

        } catch (Exception e) {
            System.out.println(e.getMessage());
            if (keepRunning != false) {
                e.printStackTrace(System.out);
            }
            this.notifyListeners(keepRunning == false);
            return;
        }

        // wait a bit
        try {
            Thread.sleep(1000);
        } catch (Exception e) {
        }

        SensorModel model = null;
        packetCounter = 1;
        lastUpdate = System.currentTimeMillis();
        lastPacket = lastUpdate;

        while (keepRunning) {
            try {
                //System.out.println(action.toString());
                reply = receive();

                //System.out.println(reply);

                long now = System.currentTimeMillis();
                long delta = now - lastUpdate;

                if (delta <= 1000) {
                    ++packetCounter;
                    minLat = (int) Math.min(minLat, (now - lastPacket));
                    maxLat = (int) Math.max(maxLat, (now - lastPacket));

                } else {
                    this.notifyListeners(new ConnectionStatistics(minLat, maxLat, 1000 / packetCounter, packetCounter));

                    minLat = Integer.MAX_VALUE;
                    maxLat = Integer.MIN_VALUE;
                    packetCounter = 1;
                    lastUpdate = now;
                }
                lastPacket = now;

            } catch (Exception e) {
                if (keepRunning != false) {
                    e.printStackTrace(System.out);
                }
                controller.reset();
                this.notifyListeners(keepRunning == false);
                return;
            }

            if (reply.contains("shutdown") || reply.contains("restart")) {
                //System.out.println("Got a shutdown or restart message from server, ending trial...");
                if (reply.contains("shutdown")) {
                    controller.shutdown();
                }
                if (reply.contains("restart")) {
                    controller.reset();
                }
                keepRunning = false;
                try {
                    socket.close();
                } catch (Exception e) {
                }
                this.notifyListeners(keepRunning == false);
                return;
            }

            MessageParser message = new MessageParser(reply);
            model = new MessageBasedSensorModel(message);
            action = controller.control(model);
            try {
                send(action.toString());
                
            } catch (Exception e) {
                if (keepRunning != false) {
                    e.printStackTrace(System.out);
                }
                controller.reset();
                this.notifyListeners(keepRunning == false);
                return;
            }
        }
    }

    private String receive()
            throws Exception {
        DatagramPacket packet = new DatagramPacket(rcvBuffer, rcvBuffer.length);
        socket.receive(packet);

        return new String(packet.getData(), 0, packet.getLength(), "US-ASCII");
    }

    private void send(String toSend)
            throws Exception {
        byte[] outBuffer = toSend.getBytes("US-ASCII");
        socket.send(new DatagramPacket(outBuffer, outBuffer.length, server));
    }

    /*private String sendAndReceive(String toSend)
    throws Exception {

    byte[] outBuffer = toSend.getBytes("US-ASCII");
    socket.send(new DatagramPacket(outBuffer, outBuffer.length, server));

    DatagramPacket packet = new DatagramPacket(rcvBuffer, rcvBuffer.length);
    socket.receive(packet);

    return new String(packet.getData(), 0, packet.getLength(), "US-ASCII");
    }*/
    @Override
    public void stop() {
        try {
            if (socket != null) {
                keepRunning = false;
                socket.close();
            }
        } catch (Exception e) {
        }
    }
}
