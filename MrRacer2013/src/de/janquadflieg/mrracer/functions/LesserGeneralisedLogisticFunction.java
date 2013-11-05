/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.janquadflieg.mrracer.functions;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;
import javax.imageio.ImageIO;
import javax.swing.JFileChooser;

import de.janquadflieg.mrracer.Utils;
import de.janquadflieg.mrracer.behaviour.AbstractDampedAccelerationBehaviour;
import de.janquadflieg.mrracer.controller.MrRacer2012;
import de.janquadflieg.mrracer.plan.Plan2011;

/**
 *
 * @author Jan Quadflieg
 */
public class LesserGeneralisedLogisticFunction {

    /** Lower asymptote. */
    private double a = 1;
    /** Identifier for the lower asymptote. */
    public static final String LOWER_ASYMPTOTE_A = "-LGLF.A-";
    /** Upper asymptote. */
    private double k = 2;
    /** Identifier for the Upper asymptote. */
    public static final String UPPER_ASYMPTOTE_K = "-LGLF.K-";
    /** Growth rate. */
    private double b = 10;
    /** Identifier for the growth rate. */
    public static final String GROWTH_RATE_B = "-LGLF.B-";
    /** v. */
    private double v = 1;
    /** Identifier for v. */
    public static final String V = "-LGLF.V-";
    /** The position of maximum growth M. */
    private double m = 0.5;
    /** Identifier for v. */
    public static final String M = "-LGLF.M-";
    /** Q. */
    private double q = 1;
    /** Y0. */
    private double y0 = 1.0;
    /** Ty - Translation on y-axis. */
    private double ty = -1.0;

    public void setParameters(Properties params, String prefix) {
        if (params.containsKey(prefix + GROWTH_RATE_B)) {
            this.b = Double.parseDouble(params.getProperty(prefix + GROWTH_RATE_B));
            //System.out.println("Setting ["+prefix+GROWTH_RATE_B+"] to "+b);
        }

        if (params.containsKey(prefix + LOWER_ASYMPTOTE_A)) {
            this.a = Double.parseDouble(params.getProperty(prefix + LOWER_ASYMPTOTE_A));
            //System.out.println("Setting ["+prefix+LOWER_ASYMPTOTE_A+"] to "+a);
        }

        if (params.containsKey(prefix + UPPER_ASYMPTOTE_K)) {
            this.k = Double.parseDouble(params.getProperty(prefix + UPPER_ASYMPTOTE_K));
            //System.out.println("Setting ["+prefix+UPPER_ASYMPTOTE_K+"] to "+k);
        }

        if (params.containsKey(prefix + M)) {
            this.m = Double.parseDouble(params.getProperty(prefix + M));
            //System.out.println("Setting ["+prefix+M+"] to "+m);
        }

        if (params.containsKey(prefix + V)) {
            this.v = Math.max(Double.parseDouble(params.getProperty(prefix + V)), 0.01);
            //System.out.println("Setting ["+prefix+V+"] to "+v);
        }

        // q = -1.0 + Math.pow((k/y0), v); falsch
        double d1 = -1.0 + Math.pow((1.0/(y0-a))*(k-a), v);
        double d2 = Math.exp(b*m);
        q = d1/d2;

        System.out.println(q);
    }

    public void getParameters(Properties params, String prefix) {
        params.setProperty(prefix + GROWTH_RATE_B, String.valueOf(b));
        params.setProperty(prefix + LOWER_ASYMPTOTE_A, String.valueOf(a));
        params.setProperty(prefix + UPPER_ASYMPTOTE_K, String.valueOf(k));
        params.setProperty(prefix + M, String.valueOf(m));
        params.setProperty(prefix + V, String.valueOf(v));
    }

    private double compute(double d) {
        double d1 = k - a;

        double d2 = Math.pow(1 + (q * Math.exp(-b * (d - m))), (1.0 / v));

        return d1 / d2;
    }

    public double getValue(double d) {
        return (a + compute(d))+ty;
    }

    public double getMirroredValue(double d) {
        return (k - compute(d));
    }

    public void paint(Graphics2D g, Dimension size, Axis xA, Axis yA) {
        g.setColor(Color.white);
        g.fillRect(0, 0, size.width, size.height);

        final int cw = 65;
        final int ch = 20;
        int x, y;

        g.setColor(Color.BLACK);
        g.drawLine(cw, 0, cw, size.height - ch);

        for (double d = yA.lb; d <= yA.ub; d += yA.ticks) {
            y = (size.height - ch) - (int) Math.round((d / yA.ub) * (size.height - ch));
            g.drawLine(cw - 5, y, cw + 5, y);
            g.drawString(Utils.dTS(d) + yA.unit, cw - 60, y + 5);
        }

        g.drawLine(cw, size.height - ch, size.width, size.height - ch);

        for (double d = xA.lb; d < xA.ub; d += xA.ticks) {
            x = cw + (int) Math.round((d / xA.ub) * (size.width - cw));
            g.drawLine(x, size.height - (ch - 5), x, size.height - (ch + 5));
            g.drawString(Utils.dTS(d) + xA.unit, x - 10, size.height - (ch - 15));
        }

        Dimension d = new Dimension(size.width - cw, size.height - ch);
        g.translate(cw, 0);
        paintGraph(g, d, yA);

    }

    private void paintGraph(Graphics2D g, Dimension d, Axis yA) {
        g.setColor(Color.red);
        for (int x = 0; x < d.width; ++x) {
            double xv = ((x * 1.0) / d.width);

            double result;
            if (yA.mirror) {
                result = getMirroredValue(xv);

            } else {
                result = getValue(xv);
            }
            result *= (yA.ub - yA.lb);
            result += yA.lb;

            //System.out.print(xv+" "+result+" ");

            int y = d.height - (int) Math.round((result / yA.ub) * d.height);

            //System.out.println(y);

            g.fillRect(x - 1, y - 1, 3, 3);
        }
    }

    public static class Axis {
        public double lb = 0.0;
        public double ub = 1.0;
        public boolean mirror = false;
        public double ticks = 0.2;
        public String unit = "";
    }

    public static void main(String args[]) {
        try {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            fileChooser.setAcceptAllFileFilterUsed(true);
            fileChooser.setMultiSelectionEnabled(false);


            int result = fileChooser.showOpenDialog(null);
            if (result != JFileChooser.APPROVE_OPTION) {
                System.exit(-1);
            }

            File file = fileChooser.getSelectedFile();
            
            InputStream in = new FileInputStream(file);

            Properties p = new Properties();
            p.load(in);
            in.close();

            LesserGeneralisedLogisticFunction f = new LesserGeneralisedLogisticFunction();
            Axis x = new Axis();
            Axis y = new Axis();

            f.setParameters(p, MrRacer2012.PLAN + Plan2011.TARGET_SPEEDS);
            BufferedImage img = new BufferedImage(600, 400, BufferedImage.TYPE_INT_RGB);
            x.mirror = false;
            x.lb = 0.0;
            x.ub = 100.0;
            x.ticks = 10.0;
            x.unit = "°";

            y.mirror = true;
            y.lb = 50.0;
            y.ub = 330.0;
            y.ticks = 50.0;
            y.unit = "km/h";
            f.paint(img.createGraphics(), new Dimension(600, 400), x, y);
            ImageIO.write(img, "PNG", new File(file.getAbsoluteFile()+"-speeds.png"));

            f.setParameters(p, MrRacer2012.ACC + AbstractDampedAccelerationBehaviour.BRAKE_DAMP);
            img = new BufferedImage(600, 400, BufferedImage.TYPE_INT_RGB);
            x.mirror = false;
            x.lb = 0.0;
            x.ub = 45.0;
            x.ticks = 10.0;
            x.unit = "°";

            y.mirror = true;
            y.lb = 0.0;
            y.ub = 1.0;
            y.ticks = 0.2;
            y.unit = "";
            f.paint(img.createGraphics(), new Dimension(600, 400), x, y);
            ImageIO.write(img, "PNG", new File(file.getAbsoluteFile()+"-brake.png"));

            double integral = 0.0;
            for (double d = 0.01; d <= 1.0; d += 0.01) {
                integral += (0.01 * f.getMirroredValue(d));
            }

            System.out.println(integral);

            f.setParameters(p, MrRacer2012.ACC + AbstractDampedAccelerationBehaviour.ACC_DAMP);
            img = new BufferedImage(600, 400, BufferedImage.TYPE_INT_RGB);
            f.paint(img.createGraphics(), new Dimension(600, 400), x, y);
            ImageIO.write(img, "PNG", new File(file.getAbsoluteFile()+"-acc.png"));


        } catch (Exception e) {
        }
    }
}
