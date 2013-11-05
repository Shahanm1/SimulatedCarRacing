/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.janquadflieg.mrracer.evo.adapter;

import de.janquadflieg.mrracer.Utils;
import de.janquadflieg.mrracer.evo.FitnessEvaluator;
import de.janquadflieg.mrracer.behaviour.Clutch;
import de.janquadflieg.mrracer.controller.Evaluator;
import de.janquadflieg.mrracer.controller.ClutchTester;
import de.janquadflieg.mrracer.controller.MrRacer2012;
import de.janquadflieg.mrracer.functions.GeneralisedLogisticFunction;
import de.janquadflieg.mrracer.plan.Plan2011;


import java.io.*;
import java.util.Properties;

import optimizer.ea.*;

/**
 * Interface betweeen Mike's ea lib and Torcs. This one is used to optimize
 * the recovery behaviours.
 *
 * @author quad
 */
public class TorcsClutchProblem
extends Problem implements ParallelEvaluableProblem{

    private static final String TORCS_IP_PORT = "Torcs.IP_PORT";

    private static final String TORCS_MAX_TICKS = "Torcs.MAX_TICKS";

    private static final String TORCS_MAX_LAPS = "Torcs.MAX_LAPS";

    private static final String TORCS_TRACK = "Torcs.TRACK";

    private static final String TORCS_PARAM_FILE = "Torcs.PARAM_FILE";

    private String paramFile = "ea_run.params";

    private Properties defaultProperties = new Properties();
    
    private int port = 3001;

    private String host = "127.0.0.1";

    private String trackName = "ea_run_noisy_wheel2";
    
    /** Dimensions. */
    private int dimensions = 5;

    private static final int DEFAULT_TICKS = 500;

    private int maxTicks = DEFAULT_TICKS;
    private int maxLaps = de.janquadflieg.mrracer.controller.Evaluator.NO_MAXIMUM;
    

    /** Creates a new instance of Torcs Problem. */
    public TorcsClutchProblem(Parameters par) {

        if (par != null) {
            if(par.origConfig.containsKey(TORCS_IP_PORT)){
                String ipport = par.origConfig.getProperty(TORCS_IP_PORT);
                int idx = ipport.indexOf(':');
                host = ipport.substring(0, idx);
                port = Integer.parseInt(ipport.substring(idx+1, ipport.length()));
            }
            if(par.origConfig.containsKey(TORCS_MAX_TICKS)){
                maxTicks = Integer.parseInt(par.origConfig.getProperty(TORCS_MAX_TICKS));
            }
            if(par.origConfig.containsKey(TORCS_MAX_LAPS)){
                maxLaps = Integer.parseInt(par.origConfig.getProperty(TORCS_MAX_LAPS));
            }
            if(par.origConfig.containsKey(TORCS_TRACK)){
                trackName = par.origConfig.getProperty(TORCS_TRACK).trim();
            }
            if (par.origConfig.containsKey(TORCS_PARAM_FILE)) {
                paramFile = par.origConfig.getProperty(TORCS_PARAM_FILE);
            }
        }

        if (new File(paramFile).exists()) {
            System.out.print("Loading default parameter set ");

            try {
                InputStream in = new FileInputStream(paramFile);

                defaultProperties.load(in);

                System.out.println(Utils.list(defaultProperties, "\n"));

                in.close();

            } catch (Exception e) {
                e.printStackTrace(System.out);
            }
        }
    }

    @Override
    public String problemRunHeader() {
        StringBuilder result = new StringBuilder();

        result.append("Dimensions: ").append(dimensions).append("\n");        

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
    public void preParallelEvaluations(int n){
        // nothing to do here
    }

    /**
     * Method called after the parallel evaluation of a number of individuals
     * has finished. Can be used to clean up the stuff generated during the call
     * to preParallelEvaluation().
     */
    public void postParallelEvaluations(){
        // nothing to do here
    }

    /**
     * Method called to start the evaluation of one individual.
     * @param ind The individual to evaluate.
     * @param i The index of the individual, a number between 1 and n.
     * @return The results of the evaluation.
     */
    public Evaluation parallelEvaluation(Individual ind, int i){
        return evaluate(ind, i-1);
    }

    public Evaluation evaluate(Individual variables) {
        return evaluate(variables, 0);
    }

    public Evaluation evaluate(Individual variables, int offset) {
        Properties params = new Properties();

        RealVariable v;

        // parameters for the clutch
        v = (RealVariable)variables.getAnyVariable("CLUTCH_B");
        params.setProperty(MrRacer2012.CLUTCH+Clutch.F+GeneralisedLogisticFunction.GROWTH_RATE_B,
                String.valueOf(Math.pow(10, v.toDouble())));

        v = (RealVariable)variables.getAnyVariable("CLUTCH_M");
        params.setProperty(MrRacer2012.CLUTCH+Clutch.F+GeneralisedLogisticFunction.M,
                String.valueOf(v.toDouble()));

        v = (RealVariable)variables.getAnyVariable("CLUTCH_V");
        params.setProperty(MrRacer2012.CLUTCH+Clutch.F+GeneralisedLogisticFunction.V,
                String.valueOf(v.toDouble()));

        v = (RealVariable)variables.getAnyVariable("CLUTCH_Q");
        params.setProperty(MrRacer2012.CLUTCH+Clutch.F+GeneralisedLogisticFunction.Q,
                String.valueOf(v.toDouble()));

        v = (RealVariable)variables.getAnyVariable("CLUTCH_SPEED");
        params.setProperty(MrRacer2012.CLUTCH+Clutch.MS,
                String.valueOf(v.toDouble()*Plan2011.MAX_SPEED));
        
        // wait a bit
        try {
            Thread.sleep(1000);
        } catch (Exception e) {
        }        

        ClutchTester controller = new ClutchTester();

        
        controller.setParameters(params);
        controller.setStage(scr.Controller.Stage.QUALIFYING);
        controller.setTrackName(trackName);        

        FitnessEvaluator fe = new FitnessEvaluator(host, port+offset,
                new Evaluator(controller, maxTicks), maxLaps);

        while (!fe.finished()) {
            try {
                Thread.sleep(1000);
            } catch (Exception e) {
            }
        }

        synchronized(this){
            ++evals;
            ++validEvals;
        }

        variables.evaluated = true;

        double fitness = fe.getResult().distance * -1.0;
        boolean valid = !fe.maxDamageReached();

        //System.out.println("Fitness: "+fitness);

        //return new Evaluation(-fe.getResult().getFitness(), true);
        //return new Evaluation(fe.getFastestLap(), true);
        return new Evaluation(fitness, valid);
    }

    public Individual getTemplate(Parameters par) {
        Individual result = new Individual();

        VariableGroup genotype = new VariableGroup("genotype");
        
        genotype.add(new RealVariable("CLUTCH_B", 0, 1, true));
        genotype.add(new RealVariable("CLUTCH_M", 0, 1, true));
        genotype.add(new RealVariable("CLUTCH_V", 0, 1, true));
        genotype.add(new RealVariable("CLUTCH_Q", 0, 1, true));
        genotype.add(new RealVariable("CLUTCH_SPEED", 0, 1, true));

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

    public static void main(String[] args) {
        //String paramName = "F:\\Quad\\Experiments\\Torcs-Test\\torcs-config.properties";
        String paramName = null;
        if (args.length == 1) {
            paramName = args[ 0];
        }
        Parameters par = new Parameters(args, paramName);

        BlackBoxProblem prob = new TorcsClutchProblem(par);
        Individual ind = prob.getTemplate(par);
        BatchEA ea = new BatchEA(ind, par, prob);
        ea.run();
    }
}
