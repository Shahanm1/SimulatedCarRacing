/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.janquadflieg.mrracer.evo.adapter;

import de.janquadflieg.mrracer.Utils;
import de.janquadflieg.mrracer.evo.FitnessEvaluator;
import de.janquadflieg.mrracer.behaviour.Clutch;
import de.janquadflieg.mrracer.behaviour.AbstractDampedAccelerationBehaviour;
import de.janquadflieg.mrracer.controller.Evaluator;
import de.janquadflieg.mrracer.controller.MrRacer2012;
import de.janquadflieg.mrracer.controller.MrSimpleRacer;
import de.janquadflieg.mrracer.functions.GeneralisedLogisticFunction;
import de.janquadflieg.mrracer.plan.Plan2011;
import de.janquadflieg.mrracer.plan.PlanCorner2012Flexible;

import java.io.*;
import java.util.*;

import optimizer.ea.*;

/**
 * Interface betweeen Mike's ea lib and Torcs.
 *
 * @author quad
 */
public class TorcsSimpleProblem
        extends Problem implements ParallelEvaluableProblem {

    private static final String TORCS_IP_PORT = "Torcs.IP_PORT";
    private static final String TORCS_MAX_TICKS = "Torcs.MAX_TICKS";
    private static final String TORCS_MAX_LAPS = "Torcs.MAX_LAPS";
    private static final String TORCS_TRACK = "Torcs.TRACK";
    private static final String TORCS_PARAM_FILE = "Torcs.PARAM_FILE";
    private static final String TORCS_OPTIMIZE_PLAN = "Torcs.OPTIMIZE_PLAN";
    private static final String TORCS_OPTIMIZE_ACC_DAMPING = "Torcs.OPTIMIZE_ACC_DAMPING";
    private static final String TORCS_OPTIMIZE_BRAKE_DAMPING = "Torcs.OPTIMIZE_BRAKE_DAMPING";
    private static final String TORCS_OPTIMIZE_CLUTCH = "Torcs.OPTIMIZE_CLUTCH";
    private static final String TORCS_INCLUDE_PLAN_BCC = "Torcs.INCLUDE_PLAN_BCC";
    private int port = 3001;
    private String host = "127.0.0.1";
    private String trackName = "ea_run_noisy_wheel2";
    private String[] paramFiles = {};
    private Properties defaultProperties = new Properties();
    /** Dimensions. */
    private int dimensions = 0;
    private boolean optimizePlan = true;
    private boolean includePlanBCC = true;
    private boolean optimizeACCDamp = true;
    private boolean optimizeBrakeDamp = true;
    private boolean optimizeClutch = true;
    private static final int DEFAULT_TICKS = 10000;
    private int maxTicks = DEFAULT_TICKS;
    private int maxLaps = de.janquadflieg.mrracer.controller.Evaluator.NO_MAXIMUM;
    private String experimentName = "";
    private int run = 0;

    /** Ip port for function evaluations. */
    /** Creates a new instance of Torcs Problem. */
    public TorcsSimpleProblem(Parameters par) {
        experimentName = par.origConfig.getProperty("experiment", "");
        run = 1 + (int) Double.parseDouble(par.origConfig.getProperty("runOffset", "0"));

        System.setProperty("EAMode", "");

        if (par != null) {
            if (par.origConfig.containsKey(TORCS_IP_PORT)) {
                String ipport = par.origConfig.getProperty(TORCS_IP_PORT);
                int idx = ipport.indexOf(':');
                host = ipport.substring(0, idx);
                port = Integer.parseInt(ipport.substring(idx + 1, ipport.length()));
            }
            if (par.origConfig.containsKey(TORCS_MAX_TICKS)) {
                maxTicks = Integer.parseInt(par.origConfig.getProperty(TORCS_MAX_TICKS));
            }
            if (par.origConfig.containsKey(TORCS_MAX_LAPS)) {
                maxLaps = Integer.parseInt(par.origConfig.getProperty(TORCS_MAX_LAPS));
            }
            if (par.origConfig.containsKey(TORCS_TRACK)) {
                trackName = par.origConfig.getProperty(TORCS_TRACK).trim();
            }
            if (par.origConfig.containsKey(TORCS_PARAM_FILE)) {
                StringTokenizer tokenizer = new StringTokenizer(par.origConfig.getProperty(TORCS_PARAM_FILE), ",");
                paramFiles = new String[tokenizer.countTokens()];
                for (int i = 0; tokenizer.hasMoreTokens(); ++i) {
                    paramFiles[i] = tokenizer.nextToken().trim();
                }
            }
            optimizePlan = Boolean.parseBoolean(par.origConfig.getProperty(TORCS_OPTIMIZE_PLAN, "true"));
            optimizeACCDamp = Boolean.parseBoolean(par.origConfig.getProperty(TORCS_OPTIMIZE_ACC_DAMPING, "true"));
            optimizeBrakeDamp = Boolean.parseBoolean(par.origConfig.getProperty(TORCS_OPTIMIZE_BRAKE_DAMPING, "true"));
            optimizeClutch = Boolean.parseBoolean(par.origConfig.getProperty(TORCS_OPTIMIZE_CLUTCH, "true"));
            includePlanBCC = Boolean.parseBoolean(par.origConfig.getProperty(TORCS_INCLUDE_PLAN_BCC, "true"));
        }

        if (optimizePlan) {
            dimensions += 6;
            if(!includePlanBCC){
                --dimensions;
            }
        }
        if (optimizeACCDamp) {
            dimensions += 4;
        }
        if (optimizeBrakeDamp) {
            dimensions += 4;
        }
        if (optimizeClutch) {
            dimensions += 5;
        }

        for (int i = 0; i < paramFiles.length; ++i) {
            if (new File(paramFiles[i]).exists()) {
                System.out.println("Loading default parameter set "+paramFiles[i]);

                try {
                    InputStream in = new FileInputStream(paramFiles[i]);

                    defaultProperties.load(in);

                    System.out.println(Utils.list(defaultProperties, "\n"));
                    System.out.println("--------------------------------------");

                    in.close();

                } catch (Exception e) {
                    e.printStackTrace(System.out);
                }
            } else {
                System.out.println("WARNING: "+paramFiles[i]+" not found!");
            }
        }
    }

    @Override
    public String problemRunHeader() {
        StringBuilder result = new StringBuilder();

        result.append("Dimensions: ").append(dimensions).append("\n");
        result.append("Optimize parameters of the planning module? ").append(optimizePlan).append("\n");
        result.append("Include the brake corner coefficient? ").append(includePlanBCC).append("\n");
        result.append("Optimize parameters for the acceleration damping? ").append(optimizeACCDamp).append("\n");
        result.append("Optimize parameters for the brake damping? ").append(optimizeBrakeDamp).append("\n");
        result.append("Optimize parameters for the clutch? ").append(optimizeClutch).append("\n");

        result.append("Default properties in use: \n");

        result.append(Utils.list(defaultProperties, "\n"));

        return result.toString();
    }

    /**
     * A method called before the next round of parallel evaluations start. This
     * can be used to setup some metadata used to evaluate the n individuals
     * (determine port numbers for network connections of the individuals, prepare
     * different temp directories for the individuals, etc).
     *
     * @param n Number of individuals to be evaluated in parallel
     * during the next batch.
     */
    public void preParallelEvaluations(int n) {
        // nothing to do here
    }

    /**
     * Method called after the parallel evaluation of a number of individuals
     * has finished. Can be used to clean up the stuff generated during the call
     * to preParallelEvaluation().
     */
    public void postParallelEvaluations() {
        // nothing to do here
    }

    /**
     * Method called to start the evaluation of one individual.
     * @param ind The individual to evaluate.
     * @param i The index of the individual, a number between 1 and n.
     * @return The results of the evaluation.
     */
    public Evaluation parallelEvaluation(Individual ind, int i) {
        return evaluate(ind, i - 1);
    }

    public Evaluation evaluate(Individual variables) {
        return evaluate(variables, 0);
    }

    public Evaluation evaluate(Individual variables, int offset) {
        Properties params = new Properties();

        RealVariable v;

        // parameters for the target speed function
        if (optimizePlan) {
            v = (RealVariable) variables.getAnyVariable("TS_B");
            params.setProperty(MrRacer2012.PLAN + Plan2011.TARGET_SPEEDS + GeneralisedLogisticFunction.GROWTH_RATE_B,
                    String.valueOf(Math.pow(10, v.toDouble())));

            v = (RealVariable) variables.getAnyVariable("TS_M");
            params.setProperty(MrRacer2012.PLAN + Plan2011.TARGET_SPEEDS + GeneralisedLogisticFunction.M,
                    String.valueOf(v.toDouble()));

            v = (RealVariable) variables.getAnyVariable("TS_V");
            params.setProperty(MrRacer2012.PLAN + Plan2011.TARGET_SPEEDS + GeneralisedLogisticFunction.V,
                    String.valueOf(v.toDouble()));

            v = (RealVariable) variables.getAnyVariable("TS_Q");
            params.setProperty(MrRacer2012.PLAN + Plan2011.TARGET_SPEEDS + GeneralisedLogisticFunction.Q,
                    String.valueOf(v.toDouble()));
        }

        // parameters for the acceleration damp function
        if (optimizeACCDamp) {
            v = (RealVariable) variables.getAnyVariable("AD_B");
            params.setProperty(MrRacer2012.ACC + AbstractDampedAccelerationBehaviour.ACC_DAMP + GeneralisedLogisticFunction.GROWTH_RATE_B,
                    String.valueOf(Math.pow(10, v.toDouble())));

            v = (RealVariable) variables.getAnyVariable("AD_M");
            params.setProperty(MrRacer2012.ACC + AbstractDampedAccelerationBehaviour.ACC_DAMP + GeneralisedLogisticFunction.M,
                    String.valueOf(v.toDouble()));

            v = (RealVariable) variables.getAnyVariable("AD_V");
            params.setProperty(MrRacer2012.ACC + AbstractDampedAccelerationBehaviour.ACC_DAMP + GeneralisedLogisticFunction.V,
                    String.valueOf(v.toDouble()));

            v = (RealVariable) variables.getAnyVariable("AD_Q");
            params.setProperty(MrRacer2012.ACC + AbstractDampedAccelerationBehaviour.ACC_DAMP + GeneralisedLogisticFunction.Q,
                    String.valueOf(v.toDouble()));
        }

        if (this.optimizeBrakeDamp) {
            // parameters for the brake damp function
            v = (RealVariable) variables.getAnyVariable("BD_B");
            params.setProperty(MrRacer2012.ACC + AbstractDampedAccelerationBehaviour.BRAKE_DAMP + GeneralisedLogisticFunction.GROWTH_RATE_B,
                    String.valueOf(Math.pow(10, v.toDouble())));

            v = (RealVariable) variables.getAnyVariable("BD_M");
            params.setProperty(MrRacer2012.ACC + AbstractDampedAccelerationBehaviour.BRAKE_DAMP + GeneralisedLogisticFunction.M,
                    String.valueOf(v.toDouble()));

            v = (RealVariable) variables.getAnyVariable("BD_V");
            params.setProperty(MrRacer2012.ACC + AbstractDampedAccelerationBehaviour.BRAKE_DAMP + GeneralisedLogisticFunction.V,
                    String.valueOf(v.toDouble()));

            v = (RealVariable) variables.getAnyVariable("BD_Q");
            params.setProperty(MrRacer2012.ACC + AbstractDampedAccelerationBehaviour.BRAKE_DAMP + GeneralisedLogisticFunction.Q,
                    String.valueOf(v.toDouble()));
        }

        // parameter for the brake coefficient in corners && flexible corner planning
        if (optimizePlan) {
            if(includePlanBCC){
                v = (RealVariable) variables.getAnyVariable("P_BCC");
                params.setProperty(MrRacer2012.PLAN + Plan2011.BRAKE_CORNER_COEFF,
                        String.valueOf(v.toDouble()));
            }

            v = (RealVariable) variables.getAnyVariable("P_CPF");
            params.setProperty(MrRacer2012.PLAN + Plan2011.MODULE_CORNER + PlanCorner2012Flexible.FRACTION,
                    String.valueOf(v.toDouble()));
        }

        // parameters for the clutch
        if (optimizeClutch) {
            v = (RealVariable) variables.getAnyVariable("CLUTCH_B");
            params.setProperty(MrRacer2012.CLUTCH + Clutch.F + GeneralisedLogisticFunction.GROWTH_RATE_B,
                    String.valueOf(Math.pow(10, v.toDouble())));

            v = (RealVariable) variables.getAnyVariable("CLUTCH_M");
            params.setProperty(MrRacer2012.CLUTCH + Clutch.F + GeneralisedLogisticFunction.M,
                    String.valueOf(v.toDouble()));

            v = (RealVariable) variables.getAnyVariable("CLUTCH_V");
            params.setProperty(MrRacer2012.CLUTCH + Clutch.F + GeneralisedLogisticFunction.V,
                    String.valueOf(v.toDouble()));

            v = (RealVariable) variables.getAnyVariable("CLUTCH_Q");
            params.setProperty(MrRacer2012.CLUTCH + Clutch.F + GeneralisedLogisticFunction.Q,
                    String.valueOf(v.toDouble()));

            v = (RealVariable) variables.getAnyVariable("CLUTCH_SPEED");
            params.setProperty(MrRacer2012.CLUTCH + Clutch.MS,
                    String.valueOf(v.toDouble() * 300.0));
        }

        // wait a bit
        try {
            Thread.sleep(1000);
        } catch (Exception e) {
        }

        MrSimpleRacer controller = new MrSimpleRacer();
        //ClutchTester controller = new ClutchTester();

        controller.setParameters(defaultProperties);
        controller.setParameters(params);
        controller.setStage(scr.Controller.Stage.QUALIFYING);
        controller.setTrackName(trackName);

        String dir = "." + File.separator + "inds" + (run < 10 ? "0" : "") + String.valueOf(run);
        String saveTo = dir + File.separator + "ind-id" + variables.id;
        String comment = "Individual " + variables.id + " from ea run " + experimentName + " ("
                + (new Date().toString().replace(':', '-').replace(' ', '-'))
                + "), before the evaluation";

        Properties realParams = new Properties();
        controller.getParameters(realParams);

        try {
            File f = new File(dir);
            f.mkdirs();
            FileWriter out = new FileWriter(new File(saveTo));
            out.write(comment + "\n");
            out.write(Utils.list(realParams, "\n"));
            out.close();
        } catch (Exception e) {
            e.printStackTrace(System.out);
        }

        FitnessEvaluator fe = new FitnessEvaluator(host, port + offset,
                new Evaluator(controller, maxTicks), maxLaps);

        try{
            fe.join();
            
        } catch(InterruptedException e){
            e.printStackTrace(System.out);
        }
//        while (!fe.finished()) {
//            try {
//                Thread.sleep(1000);
//            } catch (Exception e) {
//            }
//        }

        synchronized (this) {
            ++evals;
            ++validEvals;
        }

        variables.evaluated = true;

        Fitness[] results = new Fitness[2];
        if(optimizeClutch){
            results[0] = new Fitness(-fe.getResult().distance, "Distance", true, "Dist");
        } else {
            results[0] = new Fitness(fe.getOverallTime(), "Overalltime", true, "Time");
        }
        
        results[1] = new Fitness(fe.getResult().damage, "Damage", true, "Dam");
        //results[2] = new Fitness(fe.getLatSpeedIntegral(), "LatSpeed", true, "LatS");

        realParams = new Properties();
        controller.getParameters(realParams);

        comment = "Individual " + variables.id + " from ea run " + experimentName + " ("
                + (new Date().toString().replace(':', '-').replace(' ', '-'))
                + "), fitness " + results[0].fit;

        try {
            File f = new File(dir);
            f.mkdirs();
            FileWriter out = new FileWriter(new File(saveTo));
            out.write("# " + comment + "\n");
            out.write(Utils.list(realParams, "\n"));
            out.close();
        } catch (Exception e) {
            e.printStackTrace(System.out);
        }

        return new Evaluation(results);
    }

    public Individual getTemplate(Parameters par) {
        Individual result = new Individual();

        VariableGroup genotype = new VariableGroup("genotype");

        if (optimizePlan) {
            // growth rate b of the target speed
            genotype.add(new RealVariable("TS_B", 0, 1, true));
            // m of the target speed
            genotype.add(new RealVariable("TS_M", 0, 1, true));
            // v of the target speed
            genotype.add(new RealVariable("TS_V", 0.01, 1, true));
            // q of the target speed
            genotype.add(new RealVariable("TS_Q", 0.01, 1, true));
        }

        if (optimizeACCDamp) {
            // growth rate b of the acc damp
            genotype.add(new RealVariable("AD_B", 0, 1, true));
            // m of the acc damp
            genotype.add(new RealVariable("AD_M", 0, 1, true));
            // v of the acc damp
            genotype.add(new RealVariable("AD_V", 0.01, 1, true));
            // q of the acc damp
            genotype.add(new RealVariable("AD_Q", 0.01, 1, true));
        }

        if (optimizeBrakeDamp) {
            // growth rate b of the brake damp
            genotype.add(new RealVariable("BD_B", 0, 1, true));
            // m of the brake damp
            genotype.add(new RealVariable("BD_M", 0, 1, true));
            // v of the brake damp
            genotype.add(new RealVariable("BD_V", 0.01, 1, true));
            // q of the brake damp
            genotype.add(new RealVariable("BD_Q", 0.01, 1, true));
        }

        // brake corner coefficient && corner fraction
        if (optimizePlan) {
            if(includePlanBCC){
                genotype.add(new RealVariable("P_BCC", 0, 1, true));
            }
            genotype.add(new RealVariable("P_CPF", 0, 1, true));
        }

        if (optimizeClutch) {
            genotype.add(new RealVariable("CLUTCH_B", 0, 1, true));
            genotype.add(new RealVariable("CLUTCH_M", 0, 1, true));
            genotype.add(new RealVariable("CLUTCH_V", 0.01, 1, true));
            genotype.add(new RealVariable("CLUTCH_Q", 0.01, 1, true));
            genotype.add(new RealVariable("CLUTCH_SPEED", 0, 1, true));
        }

        result.importGroup(genotype);

        // one mutation strength:
        RealMetaVariable mutationStrength = new RealMetaVariable("mutationStrength", result.variables,
                Operators.CMUTATION_STRENGTH, par.minSigma, par.maxSigma);
        if (par.initSigmas > 0) {
            mutationStrength.setInitGaussian(par.initSigmas, par.initSigmaRange);
        }
        result.importMetadata(mutationStrength);

        return result;
    }

    public int dimensions() {
        return dimensions;
    }

    /** Number of objective functions defined in this problem */
    public int objectiveFunctions() {
        return 2;
    }

    public static void main(String[] args) {
        //String paramName = "F:\\Quad\\Experiments\\Torcs-Test\\torcs-config.properties";
        String paramName = null;
        if (args.length == 1) {
            paramName = args[ 0];
        }
        Parameters par = new Parameters(args, paramName);

        BlackBoxProblem prob = new TorcsSimpleProblem(par);
        Individual ind = prob.getTemplate(par);
        BatchEA ea = new BatchEA(ind, par, prob);
        ea.run();
    }
}
