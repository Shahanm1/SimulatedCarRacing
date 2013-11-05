/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.janquadflieg.mrracer.functions;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.util.Properties;
import java.awt.Insets;

import de.janquadflieg.mrracer.Utils;

/**
 *
 * @author Jan Quadflieg
 */
public class GeneralisedLogisticFunction {

    /** Lower asymptote. */
    private double a = 0;
    /** Identifier for the lower asymptote. */
    public static final String LOWER_ASYMPTOTE_A = "-GLF.A-";
    /** Upper asymptote. */
    private double k = 1;
    /** Identifier for the Upper asymptote. */
    public static final String UPPER_ASYMPTOTE_K = "-GLF.K-";
    /** Growth rate. */
    private double b = 10;
    /** Identifier for the growth rate. */
    public static final String GROWTH_RATE_B = "-GLF.B-";
    /** v. */
    private double v = 1;
    /** Identifier for v. */
    public static final String V = "-GLF.V-";
    /** The position of maximum growth M. */
    private double m = 0.5;
    /** Identifier for v. */
    public static final String M = "-GLF.M-";
    /** Q. */
    private double q = 1;
    public static final String Q = "-GLF.Q-";
    /** Scaling Factor. */
    private double f = 1.0;
    public static final String F = "-GLF.F-";

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
            // this value should not be zero!
            this.v = Math.max(Double.parseDouble(params.getProperty(prefix + V)), 0.01);
            //System.out.println("Setting ["+prefix+V+"] to "+v);
        }

        if (params.containsKey(prefix + Q)) {
            // this value should not be zero!
            this.q = Math.max(Double.parseDouble(params.getProperty(prefix + Q)), 0.01);
            //System.out.println("Setting ["+prefix+Q+"] to "+q);
        }

        if (params.containsKey(prefix + F)) {
            this.f = Double.parseDouble(params.getProperty(prefix + F));
        }
    }

    public void getParameters(Properties params, String prefix) {
        params.setProperty(prefix + GROWTH_RATE_B, String.valueOf(b));
        params.setProperty(prefix + LOWER_ASYMPTOTE_A, String.valueOf(a));
        params.setProperty(prefix + UPPER_ASYMPTOTE_K, String.valueOf(k));
        params.setProperty(prefix + M, String.valueOf(m));
        params.setProperty(prefix + V, String.valueOf(v));
        params.setProperty(prefix + Q, String.valueOf(q));
        params.setProperty(prefix + F, String.valueOf(f));
    }

    private double compute(double d) {
        double d1 = k - a;

        double d2 = Math.pow(1 + (q * Math.exp(-b * (d - m))), (1.0 / v));

        return (d1 / d2);
    }

    public double getValue(double d) {
        return (a + compute(d)) * f;
    }

    public double getMirroredValue(double d) {
        return (k - compute(d)) * f;
    }

    public void paint(Graphics2D g, Dimension dim, XAxis xA, YAxis yA) {
        Dimension size = new Dimension(dim);
        g.setColor(Color.white);
        g.fillRect(0, 0, size.width, size.height);

        final Insets insets = new Insets(10, 10, 10, 10);

        size.width -= (insets.left + insets.right);
        size.height -= (insets.top + insets.bottom);

        g.translate(insets.left, insets.top);

        final int cw = 80;
        final int ch = 25;
        int x, y;

        g.setColor(Color.BLACK);
        g.drawLine(cw, 0, cw, size.height - ch);

        int i = 0;
        for (double d = yA.labelMin; d <= yA.labelMax; d += yA.ticks, ++i) {
            y = (size.height - ch) - (int) Math.round((d / (yA.labelMax - yA.labelMin)) * (size.height - ch));
            g.drawLine(cw - 5, y, cw + 5, y);
            if (yA.labels != null) {
                g.drawString(yA.labels[i], 5, y + 5);
            } else {
                g.drawString(Utils.dTS(d) + yA.unit, 5, y + 5);
            }
        }

        g.drawLine(cw, size.height - ch, size.width, size.height - ch);

        i = 0;
        for (double d = xA.labelMin; d <= xA.labelMax; d += xA.ticks, ++i) {
            x = cw + (int) Math.round((d / (xA.labelMax - xA.labelMin)) * (size.width - cw));
            g.drawLine(x, size.height - (ch - 5), x, size.height - (ch + 5));

            String label;
            if (xA.labels != null) {
                label = xA.labels[i];
            } else {
                label = Utils.dTS(d) + xA.unit;
            }

            if (d == xA.labelMin) {
                x = 1 * x;

            } else {
                int stringWidth = g.getFontMetrics().stringWidth(label);
                if (d == xA.labelMax) {
                    x -= stringWidth;

                } else {
                    x -= stringWidth/2;
                }
            }
            g.drawString(label, x, size.height - (ch - 20));
        }

        Dimension d = new Dimension(size.width - cw, size.height - ch);
        g.translate(cw, 0);
        paintGraph(g, d, xA, yA);

    }

    private void paintGraph(Graphics2D g, Dimension d, XAxis xA, YAxis yA) {
        g.setColor(Color.red);
        for (int x = 0; x < d.width; ++x) {
            double xv = ((x * 1.0) / d.width);
            xv = xv * (xA.labelMax - xA.labelMin);

            double result;

            if (xv < xA.xmin) {
                result = xA.xminY;

            } else if (xv > xA.xmax) {
                result = xA.xmaxY;

            } else {
                xv = (xv - xA.xmin) / (xA.xmax - xA.xmin);

                if (yA.mirror) {
                    result = getMirroredValue(xv);

                } else {
                    result = getValue(xv);
                }
                if (Double.isNaN(result)) {
                    System.out.println("NAN");
                } else if (Double.isInfinite(result)) {
                    System.out.println("Infinite");
                }
                result *= (yA.y1 - yA.y0);
                result += yA.y0;
            }

            //System.out.print(xv+" "+result+" ");

            int y = d.height - (int) Math.round((result / (yA.labelMax - yA.labelMin)) * d.height);

            //System.out.println(y);

            g.fillRect(x - 1, y - 1, 3, 3);
        }
    }

    public static class Axis {

        public double labelMin = 0.0;    // labels
        public double labelMax = 1.0;    // labels
        public String[] labels = null;   // alternative labels
        public double ticks = 0.2;
        public String unit = "";
    }

    public static class XAxis
            extends Axis {

        public double xmin = 0.0;
        public double xminY = 0.0;
        public double xmax = 1.0;
        public double xmaxY = 1.0;
    }

    public static class YAxis
            extends Axis {

        public boolean mirror = false;
        public double y0 = 0.0;
        public double y1 = 1.0;
    }

    public static void main(String args[]) {
        /*try {
        JFileChooser fileChooser = new JFileChooser("f:\\quad\\experiments");
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

        GeneralisedLogisticFunction f = new GeneralisedLogisticFunction();
        Axis x = new Axis();
        Axis y = new Axis();

        if (p.containsKey(MrRacer2012.PLAN + Plan2011.TARGET_SPEEDS + GeneralisedLogisticFunction.GROWTH_RATE_B)) {
        f.setParameters(p, MrRacer2012.PLAN + Plan2011.TARGET_SPEEDS);
        BufferedImage img = new BufferedImage(600, 400, BufferedImage.TYPE_INT_RGB);
        x.mirror = false;
        x.lbL = 0.0;
        x.ubL = 100.0;
        x.ticks = 10.0;
        x.unit = "°";

        y.mirror = true;
        y.lbL = 50.0;
        y.ubL = 330.0;
        y.ticks = 50.0;
        y.unit = "km/h";
        f.paint(img.createGraphics(), new Dimension(600, 400), x, y);
        ImageIO.write(img, "PNG", new File(file.getAbsoluteFile() + "-speeds.png"));
        }

        if (p.containsKey(MrRacer2012.ACC + DampedAccelerationBehaviour.BRAKE_DAMP + GeneralisedLogisticFunction.GROWTH_RATE_B)) {
        f.setParameters(p, MrRacer2012.ACC + DampedAccelerationBehaviour.BRAKE_DAMP);
        BufferedImage img = new BufferedImage(600, 400, BufferedImage.TYPE_INT_RGB);
        x.mirror = false;
        x.lbL = 0.0;
        x.ubL = 45.0;
        x.ticks = 10.0;
        x.unit = "°";

        y.mirror = true;
        y.lbL = 0.0;
        y.ubL = 1.0;
        y.ticks = 0.2;
        y.unit = "";
        f.paint(img.createGraphics(), new Dimension(600, 400), x, y);
        ImageIO.write(img, "PNG", new File(file.getAbsoluteFile() + "-brake.png"));
        }

        if (p.containsKey(MrRacer2012.ACC + DampedAccelerationBehaviour.ACC_DAMP + GeneralisedLogisticFunction.GROWTH_RATE_B)) {
        f.setParameters(p, MrRacer2012.ACC + DampedAccelerationBehaviour.ACC_DAMP);
        BufferedImage img = new BufferedImage(600, 400, BufferedImage.TYPE_INT_RGB);
        x.mirror = false;
        x.lbL = 0.0;
        x.ubL = 45.0;
        x.ticks = 10.0;
        x.unit = "°";

        y.mirror = true;
        y.lbL = 0.0;
        y.ubL = 1.0;
        y.ticks = 0.2;
        y.unit = "";
        f.paint(img.createGraphics(), new Dimension(600, 400), x, y);
        ImageIO.write(img, "PNG", new File(file.getAbsoluteFile() + "-acc.png"));
        }

        if (p.containsKey(MrRacer2012.CLUTCH + Clutch.MS)) {
        f.setParameters(p, MrRacer2012.CLUTCH + Clutch.F);
        double maxSpeed = Double.parseDouble(p.getProperty(MrRacer2012.CLUTCH + Clutch.MS));
        BufferedImage img = new BufferedImage(600, 400, BufferedImage.TYPE_INT_RGB);
        x.mirror = false;
        x.lbL = 0.0;
        x.ubL = maxSpeed;
        x.ticks = 100.0;
        x.unit = "km/h";

        y.mirror = true;
        y.lbL = 0.0;
        y.ubL = 1.0;
        y.ticks = 0.2;
        y.unit = "";
        f.paint(img.createGraphics(), new Dimension(600, 400), x, y);
        ImageIO.write(img, "PNG", new File(file.getAbsoluteFile() + "-clutch.png"));
        }
        } catch (Exception e) {
        e.printStackTrace(System.out);
        }*/
    }
}
