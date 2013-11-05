/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.janquadflieg.mrracer.track;

import de.janquadflieg.mrracer.Utils;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

/**
 *
 * @author quad
 */
public class TrackModelDebugger extends javax.swing.JPanel {

    private static final Color DARK_GREEN = new Color(0, 129, 36);
    private static final Color ORANGE = new Color(255, 154, 23);
    private TrackModel model;
    private double position = 0.0;

    private long lastUpdate = System.currentTimeMillis();
    private double lastPosition = 0.0;
    private int lastIndex = -1;

    public void update(double d){
        boolean repaint = false;        
        position = d;
        int idx = model.getIndex(d);

        if(model.complete()){
            
            repaint = Math.abs(lastPosition-position) > 5.0 || Math.abs(idx-lastIndex) != 0;            
            
        } else {
            long now = System.currentTimeMillis();
            repaint = now - lastUpdate > 1000;
        }
        if(repaint){
            lastUpdate = System.currentTimeMillis();
            lastPosition = position;
            lastIndex = idx;
            repaint();
        }
    }

    

    @Override
    public void paintComponent(Graphics graphics) {
        Graphics2D g = (Graphics2D) graphics;
        Dimension size = getSize();

        g.setColor(Color.WHITE);
        g.fillRect(0, 0, getWidth(), getHeight());

        g.setColor(Color.BLACK);
        g.translate(10, 10);

        if (model == null) {
            g.drawString("No trackmodel", 10, 10);
            return;
        }

        if (model.initialized()) {
            g.drawString("Trackmodel of " + model.getName() +
                    ", width: " + Utils.dTS(model.getWidth()) +
                    ", length: " + Utils.dTS(model.getLength()), 30, 30);

        } else {
            g.drawString("Trackmodel of " + model.getName() + ", width: " +
                    Utils.dTS(model.getWidth()) + ", not yet initialized", 30, 30);
            return;
        }

        g.translate(0, 35);

        if (!model.complete()) {
            g.drawString("Model not complete", 30, 30);
            g.translate(0, 35);
        }

        g.translate(0, 20);

        double ppm = (1.0 * (size.width - 20)) / model.getLength();
        int offset = 0;

        if (model.complete()) {
            int idx = model.getIndex(position);
            int nIdx = model.incrementIndex(idx);
            double length = model.getSegment(idx).getLength()+
                    model.getSegment(nIdx).getLength();
            ppm = (1.0 * (size.width - 20)) / length;

            Dimension d = TrackSegment.Painter.draw(model.getSegment(idx), g, ppm,
                    TrackSegment.Painter.DRAW_SUBSEGMENTS | TrackSegment.Painter.DRAW_START_END);
            //int stringWidth = g.getFontMetrics().stringWidth(label);
            offset += d.width;

            g.translate(offset, 0);
            d = TrackSegment.Painter.draw(model.getSegment(nIdx), g, ppm,
                    TrackSegment.Painter.DRAW_SUBSEGMENTS | TrackSegment.Painter.DRAW_START_END);
            g.translate(-offset, 0);
            
            //offset += width;

            //g.translate(offset, 0);
            //drawSegment(model.getSegment(model.incrementIndex(idx)), g, ppm, true);
            //g.translate(-offset, 0);

            g.setColor(Color.MAGENTA);
            offset = (int) Math.round((position-model.getSegment(idx).getStart()) * ppm);
            g.drawLine(offset, -10, offset, 110);

//            g.setColor(Color.BLACK);
//            g.drawString(Utils.dTS(model.getSegment(idx).getStart()), -10, 115);
//            offset = (int) Math.round(model.getSegment(idx).getLength() * ppm);
//            g.drawString(Utils.dTS(model.getSegment(idx).getEnd()), offset - 70, 115);
//            g.drawString(Utils.dTS(model.getSegment(nIdx).getEnd()), size.width - 100, 115);

            ppm = (1.0 * (size.width - 20)) / model.getLength();

            // rawData
            double[] data = model.getRawData();
            double max = 0.0;
            for(int i=0; i < data.length; ++i){
                max = Math.max(Math.abs(data[i]), max);
            }

            g.translate(0, 120);
            g.setColor(Color.BLACK);
            g.drawLine(0, 100, size.width, 100);
            g.setColor(Color.BLUE);
            for(int i=0; i < (int)Math.ceil(model.getLength()); ++i){
                int x = (int)Math.round((i*1.0)*ppm);
                double value = model.getRawData(i*1.0);
                int y = 100 + (int)Math.round(((-100.0/max))*value);
                g.fillRect(x, y, 1, 1);
                //System.out.println(x+" "+y+" "+value);
            }
            g.setColor(Color.MAGENTA);
            offset = (int) Math.round(position * ppm);
            g.drawLine(offset, 90, offset, 110);

            // erste Ableitung
            g.translate(0, 220);
            g.setColor(Color.BLACK);
            g.drawLine(0, 100, size.width, 100);
            g.setColor(Color.RED);

            data = model.rawDataDer2;
            max = 0.0;
            for(int i=0; data != null && i < data.length; ++i){
                max = Math.max(Math.abs(data[i]), max);
            }            
            for(int i=0; data != null && i < data.length; ++i){
                int x = (int)Math.round((i*1.0)*ppm);
                double value = model.rawDataDer1[i];
                int y = 100 + (int)Math.round(((-100.0/max))*value);
                if(y != 100){
                    g.fillRect(x, y, 1, 1);
                }
                
                //System.out.println(x+" "+y+" "+value);
            }
            g.setColor(Color.MAGENTA);
            offset = (int) Math.round(position * ppm);
            g.drawLine(offset, 90, offset, 110);

            // filtered
//            g.translate(0, 220);
//            g.setColor(Color.BLACK);
//            g.drawLine(0, 100, size.width, 100);
//            g.setColor(Color.BLUE);
//
//            double[] data = model.getRawData();
//
//            for(int i=0; i < data.length; ++i){
//                int x = (int)Math.round((i*1.0)*ppm);
//
//                int windowSize = 100;
//                double value = 0.0;
//                for(int k=0; k < windowSize; ++k){
//                    int index = (i-(windowSize/2))+k;
//                    index = (index+data.length)%data.length;
//                    value += data[index];
//                }
//                value /= windowSize;
//
//                int y = 100 + (int)Math.round(-1.0*value);
//                g.fillRect(x, y, 1, 1);
//                //System.out.println(x+" "+y+" "+value);
//            }
//            g.setColor(Color.MAGENTA);
//            offset = (int) Math.round(position * ppm);
//            g.drawLine(offset, 90, offset, 110);

            // integrale von kurven
            data = model.getRawData();
            g.translate(0, 220);
            g.setColor(Color.BLACK);
            g.drawLine(0, 100, size.width, 100);
            g.setColor(Color.BLUE);

            
            for(idx=0; idx < model.size(); ++idx){
                TrackSegment seg = model.getSegment(idx);

                if(seg.isStraight()){
                    continue;
                }
                double sum = 0.0;
                for(int i=(int)Math.floor(seg.getStart()); i < (int)Math.floor(seg.getEnd()) ;++i){
                    sum += data[i];
                }
                //sum /= seg.getLength();

                double value = 0.0;
                for(int i=(int)Math.floor(seg.getStart()); i < (int)Math.floor(seg.getEnd()) ;++i){
                    int x = (int)Math.round((i*1.0)*ppm);
                    value += data[i];
                    int y = 100 + (int)Math.round(-1.0*(value*100/sum));
                    g.fillRect(x, y, 1, 1);
                }
            }
            g.setColor(Color.MAGENTA);
            offset = (int) Math.round(position * ppm);
            g.drawLine(offset, 90, offset, 110);

            
        } else {
            for (int i = 0; i < model.size(); ++i) {
                TrackSegment e = model.getSegment(i);
                g.translate(offset, 0);
                Dimension d = TrackSegment.Painter.draw(e, g, ppm, 0);
                g.translate(-offset, 0);
                
                offset += d.width;
            }
            g.setColor(Color.MAGENTA);
            offset = (int) Math.round(position * ppm);
            g.drawLine(offset, -10, offset, 110);

            g.setColor(Color.BLACK);
            g.drawString("0.0", -10, 115);
            g.drawString(Utils.dTS(model.getLength()), size.width - 70, 115);

            g.translate(0, 120);
            g.setColor(Color.BLACK);
            g.drawLine(0, 100, size.width, 100);
            g.setColor(Color.BLUE);
            for(int i=0; i < (int)Math.ceil(model.getLength()); ++i){
                int x = (int)Math.round((i*1.0)*ppm);
                double value = model.getRawData(i*1.0);
                int y = 100 + (int)Math.round(-1.0*value);
                g.fillRect(x, y, 1, 1);
                //System.out.println(x+" "+y+" "+value);
            }
        }
    }

    

    public void setModel(TrackModel tm) {
        this.model = tm;
    }
}
