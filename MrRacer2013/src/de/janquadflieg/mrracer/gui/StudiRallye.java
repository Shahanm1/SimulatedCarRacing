/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * GFLDialog.java
 *
 * Created on 09.01.2012, 12:15:10
 */
package de.janquadflieg.mrracer.gui;

import java.awt.*;
import java.awt.geom.Point2D;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import de.janquadflieg.mrracer.Utils;
import de.janquadflieg.mrracer.controller.Evaluator;
import de.janquadflieg.mrracer.controller.MrRacer2012;
import de.janquadflieg.mrracer.evo.FEListener;
import de.janquadflieg.mrracer.evo.FitnessEvaluator;
import de.janquadflieg.mrracer.functions.GeneralisedLogisticFunction;
import de.janquadflieg.mrracer.functions.GeneralisedLogisticFunction.XAxis;
import de.janquadflieg.mrracer.functions.GeneralisedLogisticFunction.YAxis;
import de.janquadflieg.mrracer.plan.Plan2011;

/**
 *
 * @author quad
 */
public class StudiRallye extends javax.swing.JDialog implements FEListener {

    /** Granularity of the sliders. */
    private static final double GRANULARITY = 10000.0;
    /** Normalize the functions? */
    private boolean normalize = false;
    /** Properties object. */
    private Properties p;
    /** Default values. */
    private final double DEFAULT_B = 10.0, DEFAULT_M = 0.5, DEFAULT_V = 1.0,
            DEFAULT_Q = 0.01, DEFAULT_BCC = 0.0;
    /** Out of time? */
    private boolean outOfTime = false;
    /** Best ever time. */
    private double bestEverTime = Utils.NO_DATA_D;
    /** Best ever points. */
    private double bestEverPoints = Utils.NO_DATA_D;
    private GLFPainter painter = new GLFPainter();
    private String prefix = "";
    private static final Point2D[] POINTS = {        
        new Point2D.Double(240.0, 1.0),     // 4 Minuten
        new Point2D.Double(180.0, 2.0),     // 3 Minuten
        new Point2D.Double(150.0, 3.0),     // 2:30 Minuten
        new Point2D.Double(140.0, 4.0),     // 2:20
        new Point2D.Double(135.0, 5.0),     // 2:15
        new Point2D.Double(130.0, 6.0),     // 2:10
        new Point2D.Double(126.0, 7.0),
        new Point2D.Double(125.0, 8.0),
        new Point2D.Double(124.0, 9.0),
        new Point2D.Double(123.0, 10.0)};
    private FitnessEvaluator evaluator = null;

    public StudiRallye(java.awt.Frame parent, boolean modal, Properties p) {
        this(parent, modal, p, "-MrRacer2012.Plan--PLAN.targetSpeeds-");
    }

    /** Creates new form GFLDialog */
    public StudiRallye(java.awt.Frame parent, boolean modal, Properties p, String prefix) {
        super(parent, modal);

        this.p = p;
        this.prefix = prefix;

        try {
            javax.swing.UIManager.setLookAndFeel(
                    javax.swing.UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace(System.out);
        }

        initComponents();
        initCustomComponents();

        p.setProperty(prefix + GeneralisedLogisticFunction.GROWTH_RATE_B, String.valueOf(DEFAULT_B));
        p.setProperty(prefix + GeneralisedLogisticFunction.M, String.valueOf(DEFAULT_M));
        p.setProperty(prefix + GeneralisedLogisticFunction.V, String.valueOf(DEFAULT_V));
        p.setProperty(prefix + GeneralisedLogisticFunction.Q, String.valueOf(DEFAULT_Q));
        p.setProperty(MrRacer2012.PLAN + Plan2011.BRAKE_CORNER_COEFF, String.valueOf(DEFAULT_BCC));

        p.setProperty(prefix + GeneralisedLogisticFunction.F, String.valueOf(1.0));
        painter.f.setParameters(p, prefix);
        if (normalize) {
            double f0 = 1.0 / painter.f.getValue(0.0);
            p.setProperty(prefix + GeneralisedLogisticFunction.F, String.valueOf(f0));
            painter.f.setParameters(p, prefix);
        }

        double d = Double.parseDouble(p.getProperty(prefix + GeneralisedLogisticFunction.GROWTH_RATE_B));
        jslB.setValue((int) Math.round(((d - 1.0) / 9.0) * GRANULARITY));
        jtfB.setText(Utils.dTS(d));

        d = Double.parseDouble(p.getProperty(prefix + GeneralisedLogisticFunction.M));
        jslM.setValue((int) Math.round(d * GRANULARITY));
        jtfM.setText(Utils.dTS(d));

        d = Double.parseDouble(p.getProperty(prefix + GeneralisedLogisticFunction.V));
        jslV.setValue((int) Math.round(d * GRANULARITY));
        jtfV.setText(Utils.dTS(d));

        d = Double.parseDouble(p.getProperty(prefix + GeneralisedLogisticFunction.Q));
        jslQ.setValue((int) Math.round(d * GRANULARITY));
        jtfQ.setText(Utils.dTS(d));

        d = Double.parseDouble(p.getProperty(MrRacer2012.PLAN + Plan2011.BRAKE_CORNER_COEFF));
        jslBCC.setValue((int) Math.round(d * GRANULARITY));        

        setSize(800, 600);
        doLayout();
        pack();

        if (parent == null) {
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            GraphicsDevice[] gs = ge.getScreenDevices();

            if (gs.length > 1) {
                Rectangle r = gs[1].getDefaultConfiguration().getBounds();
                System.out.println(r);
                Point location = new Point();
                location.x = r.x + (r.width - getWidth()) / 2;
                location.y = r.y + (r.height - getHeight()) / 2;
                setLocation(location);
            }
        }

        Timer t = new Timer(this);
    }

    private void initCustomComponents() {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1.0;
        c.weighty = 1.0;

        jpGraph.add(painter, c);
    }

    private int getPoints(double d) {
        int result = 0;

        for (int i = 0; i < POINTS.length; ++i) {
            if (d < POINTS[i].getX()) {
                result = (int) Math.round(POINTS[i].getY());
            }
        }

        return result;
    }

    public void finished(final FitnessEvaluator e) {
        System.out.println("FINISHED");
        System.out.println(e.getDistanceRaced());
        System.out.println(e.getFastestLap());
        System.out.println(e.getLapCtr());

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                finished(e.getFastestLap(), e.getLapCtr());
            }
        });
    }

    private void finished(double time, int laps) {
        toggleStopButton(false);
        evaluator = null;
        synchronized (this) {
            if (!outOfTime) {
                toggleGui(true);
            }
        }
        if (laps >= 1) {
            jpbLapProgress.setValue(100000);

            String timeString = "Zeit: " + Utils.timeToExactString(time);
            if (bestEverTime == Utils.NO_DATA_D || time < bestEverTime) {
                timeString += " - neue Bestzeit!";
                bestEverTime = time;
                jlBestTime.setText("Bestzeit: " + Utils.timeToExactString(time));
            }
            jlEvalTime.setText(timeString);

            int points = getPoints(time);
            String pointsString = "Punkte: " + points;
            if (points > bestEverPoints) {
                pointsString += " - Verbesserung!";
                bestEverPoints = points;
                jlPoints.setText("Punkte: " + points);
            }
            jlEvalPoints.setText(pointsString);

        } else {
            jlEvalTime.setText("Zeit: ??? - Versuch abgebrochen");
            jlEvalPoints.setText("Punkte: 0");
        }
    }

    public void setProgress(final double d) {
        try {
            SwingUtilities.invokeLater(new Runnable() {

                public void run() {
                    int v = (int) Math.round((d / 6210.44) * 10000.0);
                    //System.out.println(v);
                    jpbLapProgress.setValue(v);
                    jpbLapProgress.repaint();
                }
            });
        } catch (Exception e) {
            e.printStackTrace(System.out);
        }
    }

    public void setRemainingTime(final double d) {
        if (d > 0) {
            SwingUtilities.invokeLater(new Runnable() {

                public void run() {
                    setTitle("StudiRallye - CarOptimizer - Verbleibende Zeit: " + Utils.timeToString(d));
                    jlRemainingTime.setText("Verbleibende Zeit: " + Utils.timeToString(d));
                }
            });
        } else {
            synchronized (this) {
                outOfTime = true;
                /*if(evaluator != null){
                    evaluator.stop();
                }*/
            }
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    toggleGui(false);
                    setTitle("StudiRallye - CarOptimizer - Zeit abgelaufen");
                    jlRemainingTime.setText("Zeit abgelaufen");
                }
            });
        }
    }

    private void toggleGui(boolean b) {
        jslB.setEnabled(b);
        jslM.setEnabled(b);
        jslQ.setEnabled(b);
        jslV.setEnabled(b);
        jslBCC.setEnabled(b);
        jbOk.setEnabled(b);
        jbReset.setEnabled(b);
    }

    private void toggleStopButton(boolean b){
        jbCancel.setEnabled(b);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jpButtons = new javax.swing.JPanel();
        jbOk = new javax.swing.JButton();
        jbReset = new javax.swing.JButton();
        jbCancel = new javax.swing.JButton();
        jpBCC = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        jslBCC = new javax.swing.JSlider();
        jLabel9 = new javax.swing.JLabel();
        jtfBCC = new javax.swing.JTextField();
        jpGLF = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jpGraph = new javax.swing.JPanel();
        jslB = new javax.swing.JSlider();
        jtfB = new javax.swing.JTextField();
        jslM = new javax.swing.JSlider();
        jtfM = new javax.swing.JTextField();
        jslV = new javax.swing.JSlider();
        jtfV = new javax.swing.JTextField();
        jslQ = new javax.swing.JSlider();
        jtfQ = new javax.swing.JTextField();
        jpResult = new javax.swing.JPanel();
        jpbLapProgress = new javax.swing.JProgressBar();
        jlEvalTime = new javax.swing.JLabel();
        jlEvalPoints = new javax.swing.JLabel();
        jpOverallResult = new javax.swing.JPanel();
        jlPoints = new javax.swing.JLabel();
        jlBestTime = new javax.swing.JLabel();
        jlRemainingTime = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Versuch");
        setName("Parameters"); // NOI18N
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                StudiRallye.this.windowClosing(evt);
            }
        });
        getContentPane().setLayout(new java.awt.GridBagLayout());

        jpButtons.setLayout(new java.awt.GridBagLayout());

        jbOk.setText("Start");
        jbOk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ok(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        jpButtons.add(jbOk, gridBagConstraints);

        jbReset.setText("Reset");
        jbReset.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                reset(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        jpButtons.add(jbReset, gridBagConstraints);

        jbCancel.setText("Stop");
        jbCancel.setEnabled(false);
        jbCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stop(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        jpButtons.add(jbCancel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 5);
        getContentPane().add(jpButtons, gridBagConstraints);

        jpBCC.setBorder(javax.swing.BorderFactory.createTitledBorder("In der Kurve bremsen?"));
        jpBCC.setLayout(new java.awt.GridBagLayout());

        jLabel8.setText("Nein");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        jpBCC.add(jLabel8, gridBagConstraints);

        jslBCC.setMaximum(10000);
        jslBCC.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jslBCCStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        jpBCC.add(jslBCC, gridBagConstraints);

        jLabel9.setText("Ja");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        jpBCC.add(jLabel9, gridBagConstraints);

        jtfBCC.setColumns(5);
        jtfBCC.setEditable(false);
        jtfBCC.setText("value");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        jpBCC.add(jtfBCC, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 5);
        getContentPane().add(jpBCC, gridBagConstraints);

        jpGLF.setBorder(javax.swing.BorderFactory.createTitledBorder("Zielgeschwindigkeit in Kurven"));
        jpGLF.setLayout(new java.awt.GridBagLayout());

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel1.setText("B:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        jpGLF.add(jLabel1, gridBagConstraints);

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel2.setText("M:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        jpGLF.add(jLabel2, gridBagConstraints);

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel3.setText("V:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        jpGLF.add(jLabel3, gridBagConstraints);

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel4.setText("Q:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        jpGLF.add(jLabel4, gridBagConstraints);

        jpGraph.setMinimumSize(new java.awt.Dimension(300, 200));
        jpGraph.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jpGLF.add(jpGraph, gridBagConstraints);

        jslB.setMaximum(10000);
        jslB.setDoubleBuffered(true);
        jslB.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jslBStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.5;
        jpGLF.add(jslB, gridBagConstraints);

        jtfB.setColumns(5);
        jtfB.setEditable(false);
        jtfB.setText("value");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        jpGLF.add(jtfB, gridBagConstraints);

        jslM.setMaximum(10000);
        jslM.setDoubleBuffered(true);
        jslM.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jslMStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.5;
        jpGLF.add(jslM, gridBagConstraints);

        jtfM.setColumns(5);
        jtfM.setEditable(false);
        jtfM.setText("value");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        jpGLF.add(jtfM, gridBagConstraints);

        jslV.setMaximum(10000);
        jslV.setMinimum(100);
        jslV.setDoubleBuffered(true);
        jslV.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jslVStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.5;
        jpGLF.add(jslV, gridBagConstraints);

        jtfV.setColumns(5);
        jtfV.setEditable(false);
        jtfV.setText("value");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        jpGLF.add(jtfV, gridBagConstraints);

        jslQ.setMaximum(10000);
        jslQ.setMinimum(100);
        jslQ.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jslQStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.5;
        jpGLF.add(jslQ, gridBagConstraints);

        jtfQ.setColumns(5);
        jtfQ.setEditable(false);
        jtfQ.setText("value");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 4;
        jpGLF.add(jtfQ, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 5);
        getContentPane().add(jpGLF, gridBagConstraints);

        jpResult.setBorder(javax.swing.BorderFactory.createTitledBorder("Auswertung"));
        jpResult.setLayout(new java.awt.GridBagLayout());

        jpbLapProgress.setMaximum(10000);
        jpbLapProgress.setToolTipText("");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        jpResult.add(jpbLapProgress, gridBagConstraints);

        jlEvalTime.setFont(new java.awt.Font("Tahoma", 1, 14));
        jlEvalTime.setText("Zeit: ???");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jpResult.add(jlEvalTime, gridBagConstraints);

        jlEvalPoints.setFont(new java.awt.Font("Tahoma", 1, 14));
        jlEvalPoints.setText("Punkte: 0");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jpResult.add(jlEvalPoints, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 5);
        getContentPane().add(jpResult, gridBagConstraints);

        jpOverallResult.setBorder(javax.swing.BorderFactory.createTitledBorder("Ergebnis"));
        jpOverallResult.setLayout(new java.awt.GridBagLayout());

        jlPoints.setFont(new java.awt.Font("Tahoma", 1, 24));
        jlPoints.setText("Punkte: 0");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jpOverallResult.add(jlPoints, gridBagConstraints);

        jlBestTime.setFont(new java.awt.Font("Tahoma", 1, 24));
        jlBestTime.setText("Bestzeit: ???");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jpOverallResult.add(jlBestTime, gridBagConstraints);

        jlRemainingTime.setFont(new java.awt.Font("Tahoma", 1, 24));
        jlRemainingTime.setText("Verbleibende Zeit:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jpOverallResult.add(jlRemainingTime, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        getContentPane().add(jpOverallResult, gridBagConstraints);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jslBStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jslBStateChanged
        double d = 1.0 + (((jslB.getValue() * 1.0) / GRANULARITY) * 9.0);
        p.setProperty(prefix + GeneralisedLogisticFunction.GROWTH_RATE_B, String.valueOf(d));
        p.setProperty(prefix + GeneralisedLogisticFunction.F, String.valueOf(1.0));
        painter.f.setParameters(p, prefix);
        if (normalize) {
            double f0 = 1.0 / painter.f.getMirroredValue(0.0);
            p.setProperty(prefix + GeneralisedLogisticFunction.F, String.valueOf(f0));
            painter.f.setParameters(p, prefix);
        }
        jtfB.setText(Utils.dTS(d));
        painter.repaint();
    }//GEN-LAST:event_jslBStateChanged

    private void jslMStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jslMStateChanged
        double d = jslM.getValue() / GRANULARITY;
        p.setProperty(prefix + GeneralisedLogisticFunction.M, String.valueOf(d));
        p.setProperty(prefix + GeneralisedLogisticFunction.F, String.valueOf(1.0));
        painter.f.setParameters(p, prefix);
        if (normalize) {
            double f0 = 1.0 / painter.f.getMirroredValue(0.0);
            p.setProperty(prefix + GeneralisedLogisticFunction.F, String.valueOf(f0));
            painter.f.setParameters(p, prefix);
        }
        jtfM.setText(Utils.dTS(d));
        painter.repaint();
    }//GEN-LAST:event_jslMStateChanged

    private void jslVStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jslVStateChanged
        double d = jslV.getValue() / GRANULARITY;
        p.setProperty(prefix + GeneralisedLogisticFunction.V, String.valueOf(d));
        p.setProperty(prefix + GeneralisedLogisticFunction.F, String.valueOf(1.0));
        painter.f.setParameters(p, prefix);
        if (normalize) {
            double f0 = 1.0 / painter.f.getMirroredValue(0.0);
            p.setProperty(prefix + GeneralisedLogisticFunction.F, String.valueOf(f0));
            painter.f.setParameters(p, prefix);
        }
        painter.f.getParameters(p, prefix);
        d = Double.parseDouble(p.getProperty(prefix + GeneralisedLogisticFunction.V));
        //System.out.println(d);
        jtfV.setText(Utils.dTS(d));
        painter.repaint();
    }//GEN-LAST:event_jslVStateChanged

    private void jslQStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jslQStateChanged
        double d = jslQ.getValue() / GRANULARITY;
        p.setProperty(prefix + GeneralisedLogisticFunction.Q, String.valueOf(d));
        p.setProperty(prefix + GeneralisedLogisticFunction.F, String.valueOf(1.0));
        painter.f.setParameters(p, prefix);
        if (normalize) {
            double f0 = 1.0 / painter.f.getMirroredValue(0.0);
            p.setProperty(prefix + GeneralisedLogisticFunction.F, String.valueOf(f0));
            painter.f.setParameters(p, prefix);
        }
        painter.f.getParameters(p, prefix);
        d = Double.parseDouble(p.getProperty(prefix + GeneralisedLogisticFunction.Q));
        //System.out.println(d);
        jtfQ.setText(Utils.dTS(d));
        painter.repaint();
    }//GEN-LAST:event_jslQStateChanged

    private void ok(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ok
        toggleGui(false);
        jpbLapProgress.setValue(0);

        new Thread(new Runnable() {

            public void run() {
                startController();
            }
        }).start();

    }//GEN-LAST:event_ok

    private void startController() {
        System.setProperty("EAMode", "");

        MrRacer2012 mrracer = new MrRacer2012();

        mrracer.setParameters(p);
        mrracer.setStage(MrRacer2012.Stage.QUALIFYING);
        mrracer.setTrackName("Wheel2");
        mrracer.resetFull();

        evaluator = new FitnessEvaluator("127.0.0.1", 3001,
                new Evaluator(mrracer, 100000), 1, false, this);

        Updater u = new Updater(this, evaluator);

        SwingUtilities.invokeLater(new Runnable(){
            public void run(){
                toggleStopButton(true);
            }
        });
    }

    private void reset(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_reset
        int result = JOptionPane.showConfirmDialog(this, "Alle 5 Einstellungen auf die langsamen Standardwerte zurücksetzen?", "Parameter zurücksetzen", JOptionPane.YES_NO_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            p.setProperty(prefix + GeneralisedLogisticFunction.GROWTH_RATE_B, String.valueOf(DEFAULT_B));
            jtfB.setText(Utils.dTS(DEFAULT_B));
            jslB.setValue((int) Math.round(((DEFAULT_B - 1.0) / 9.0) * GRANULARITY));

            p.setProperty(prefix + GeneralisedLogisticFunction.M, String.valueOf(DEFAULT_M));
            jtfM.setText(Utils.dTS(DEFAULT_M));
            jslM.setValue((int) Math.round(DEFAULT_M * GRANULARITY));

            p.setProperty(prefix + GeneralisedLogisticFunction.V, String.valueOf(DEFAULT_V));
            jtfV.setText(Utils.dTS(DEFAULT_V));
            jslV.setValue((int) Math.round(DEFAULT_V * GRANULARITY));

            p.setProperty(prefix + GeneralisedLogisticFunction.Q, String.valueOf(DEFAULT_Q));
            jtfQ.setText(Utils.dTS(DEFAULT_Q));
            jslQ.setValue((int) Math.round(DEFAULT_Q * GRANULARITY));
            painter.f.setParameters(p, MrRacer2012.PLAN + Plan2011.TARGET_SPEEDS);
            painter.repaint();

            p.setProperty(MrRacer2012.PLAN + Plan2011.BRAKE_CORNER_COEFF, String.valueOf(DEFAULT_BCC));
            jslBCC.setValue((int) Math.round(DEFAULT_BCC * GRANULARITY));
        }
    }//GEN-LAST:event_reset

    private void jslBCCStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jslBCCStateChanged
        double d = jslBCC.getValue() / GRANULARITY;
        p.setProperty(MrRacer2012.PLAN + Plan2011.BRAKE_CORNER_COEFF, String.valueOf(d));
        jtfBCC.setText(Utils.dTS(d));
    }//GEN-LAST:event_jslBCCStateChanged

    private void windowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_windowClosing
        int result = JOptionPane.showConfirmDialog(this, "Beenden? Alle Ergebnisse gehen verloren!", "Beenden", JOptionPane.YES_NO_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            System.exit(0);
        }
    }//GEN-LAST:event_windowClosing

    private void stop(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stop
        if(evaluator != null){
            evaluator.stop();
        }
    }//GEN-LAST:event_stop

    private static class Timer extends Thread {

        private static final double MAX_TIME = 25 * 60;
        private double remainingTime;
        private long lastUpdate;
        private StudiRallye dialog;

        public Timer(StudiRallye d) {
            remainingTime = MAX_TIME;
            dialog = d;
            lastUpdate = System.currentTimeMillis();
            start();
        }

        public void run() {
            while (remainingTime > 0) {
                dialog.setRemainingTime(Math.max(remainingTime, 1.0));

                try {
                    Thread.sleep(200);
                } catch (Exception e) {
                }

                long now = System.currentTimeMillis();
                remainingTime -= (now - lastUpdate) / 1000.0;
                lastUpdate = now;
            }
            dialog.setRemainingTime(0);
        }
    }

    private static class Updater extends Thread {

        private FitnessEvaluator eval;
        private StudiRallye dialog;

        public Updater(StudiRallye d, FitnessEvaluator e) {
            dialog = d;
            eval = e;
            start();
        }

        public void run() {
            while (!eval.finished()) {
                dialog.setProgress(eval.getDistanceRaced());

                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                    e.printStackTrace(System.out);
                }
            }
        }
    }

    private static class GLFPainter extends javax.swing.JPanel {

        GeneralisedLogisticFunction f;

        public GLFPainter() {
            super();
            f = new GeneralisedLogisticFunction();

            setPreferredSize(new Dimension(800, 400));
        }

        @Override
        public void paintComponent(Graphics graphics) {
            Graphics2D g = (Graphics2D) graphics;

            XAxis x = new XAxis();
            YAxis y = new YAxis();

            x.labelMin = 0.0;
            x.labelMax = 100.0;
            x.ticks = 25.0;
            x.xmin = 0.0;
            x.xmax = 100.0;
            x.unit = "°";
            x.labels = new String[]{"Gerade", "Schnell", "Mittel", "Langsam", "Haarnadel"};

            y.mirror = true;
            y.labelMin = 0.0;
            y.y0 = 50.0;
            y.labelMax = 330.0;
            y.y1 = 330.0;
            y.ticks = 50.0;
            y.unit = "km/h";

            f.paint(g, getSize(), x, y);
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        try {
            Properties p = new Properties();
            InputStream in = new FileInputStream("rallyedefault.params");
            p.load(in);
            in.close();

            StudiRallye dialog = new StudiRallye(null, false, p);
            dialog.setVisible(true);

        } catch (Exception e) {
            e.printStackTrace(System.out);
        }
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JButton jbCancel;
    private javax.swing.JButton jbOk;
    private javax.swing.JButton jbReset;
    private javax.swing.JLabel jlBestTime;
    private javax.swing.JLabel jlEvalPoints;
    private javax.swing.JLabel jlEvalTime;
    private javax.swing.JLabel jlPoints;
    private javax.swing.JLabel jlRemainingTime;
    private javax.swing.JPanel jpBCC;
    private javax.swing.JPanel jpButtons;
    private javax.swing.JPanel jpGLF;
    private javax.swing.JPanel jpGraph;
    private javax.swing.JPanel jpOverallResult;
    private javax.swing.JPanel jpResult;
    private javax.swing.JProgressBar jpbLapProgress;
    private javax.swing.JSlider jslB;
    private javax.swing.JSlider jslBCC;
    private javax.swing.JSlider jslM;
    private javax.swing.JSlider jslQ;
    private javax.swing.JSlider jslV;
    private javax.swing.JTextField jtfB;
    private javax.swing.JTextField jtfBCC;
    private javax.swing.JTextField jtfM;
    private javax.swing.JTextField jtfQ;
    private javax.swing.JTextField jtfV;
    // End of variables declaration//GEN-END:variables
}
