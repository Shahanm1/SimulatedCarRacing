/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.janquadflieg.mrracer.evo.adapter;

import de.janquadflieg.mrracer.evo.FitnessEvaluator;
import de.janquadflieg.mrracer.evo.tools.OpponentHandler;
import de.janquadflieg.mrracer.controller.Evaluator;
import de.janquadflieg.mrracer.controller.MrRacer2012;
import de.janquadflieg.mrracer.opponents.Observer2012;
import de.janquadflieg.mrracer.plan.Plan2011;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;

import optimizer.ea.*;

/**
 * Interface betweeen Mike's ea lib and Torcs. This one is used to optimize
 * the recovery behaviours.
 *
 * @author quad
 */
public class TorcsObserverProblem
        extends Problem implements ParallelEvaluableProblem {

    private static final String TORCS_IP_PORT = "Torcs.IP_PORT";
    private static final String TORCS_MAX_TICKS = "Torcs.MAX_TICKS";
    private static final String TORCS_MAX_LAPS = "Torcs.MAX_LAPS";
    private static final String TORCS_TRACK = "Torcs.TRACK";
    private static final String TORCS_MULTI_HOSTS = "Torcs.MULTI_HOSTS";
    private static final String TORCS_EVALS_PER_HOST = "Torcs.EVALS_PER_HOST";
    private int port = 3001;
    private int evalsPerHost = 100;
    private String host = "127.0.0.1";
    private ArrayList<String> hosts = new ArrayList<>();
    private String trackName = "ea_run_noisy_wheel2";
    /** Dimensions. */
    private int dimensions = 5;
    private static final int DEFAULT_TICKS = 500;
    private int maxTicks = DEFAULT_TICKS;
    private int maxLaps = de.janquadflieg.mrracer.controller.Evaluator.NO_MAXIMUM;

    private OpponentHandler opponents;

    /** Creates a new instance of Torcs Problem. */
    public TorcsObserverProblem(Parameters par) {
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
            if (par.origConfig.containsKey(TORCS_MULTI_HOSTS)) {
                String hostList = par.origConfig.getProperty(TORCS_MULTI_HOSTS);
                String[] tokens = hostList.split("[;]");
                hosts.addAll(Arrays.asList(tokens));
            }
            if(par.origConfig.containsKey(TORCS_EVALS_PER_HOST)){
                evalsPerHost = Integer.parseInt(par.origConfig.getProperty(TORCS_EVALS_PER_HOST));
            }
        }
        System.out.println("Track name: " + trackName);
        System.out.println("IPPort: " + host + ":" + port);
        System.out.println("MaxTicks: " + maxTicks);
        System.out.println("MaxLaps: " + maxLaps);
        if (!hosts.isEmpty()) {
            System.out.println("Multihosts:");
            for (String s : hosts) {
                System.out.println("    " + s);
            }
            System.out.println("Evals per host: "+evalsPerHost);
        }
        opponents = new OpponentHandler(trackName);       
    }

    /** Number of objective functions defined in this problem */
    @Override
    public int objectiveFunctions() {
        return 3;
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

    /** If the problem uses a hidden (in the evaluate function) genotype-
     *  phenotype mapping, this function can be supplied to change the
     *  output for logging, it is supplied with the double representatives
     *  Variable.toDouble() of the whole genome in its default order. If
     *  new values are computed, the given field should be copied and returned,
     *  otherwise the genotype shall be returned again. */
    public double[] scaleup(double[] genotype) {

        double[] answer = new double[genotype.length];

        // "MSD"
        answer[0] = genotype[0] * 10.0;
        // "SIM"
        answer[1] = genotype[1] * 10.0;
        // "MDX"
        answer[2] = genotype[2] * 5.0;
        // "MDY"
        answer[3] = genotype[3] * 20.0;
        // CWI
        answer[4] = genotype[4] * 5.0;

        return answer;
    }

    public Evaluation evaluate(Individual variables, int offset) {
        Properties params = new Properties();

        double[] genotype = new double[dimensions];
        // "MSD"
        genotype[0] = ((RealVariable) variables.getAnyVariable("MSD")).toDouble();
        // "SIM"
        genotype[1] = ((RealVariable) variables.getAnyVariable("SIM")).toDouble();
        // "MDX"
        genotype[2] = ((RealVariable) variables.getAnyVariable("MDX")).toDouble();
        // "MDY"
        genotype[3] = ((RealVariable) variables.getAnyVariable("MDY")).toDouble();
        // CWI
        genotype[4] = ((RealVariable) variables.getAnyVariable("CWI")).toDouble();

        // genotype -> phenotype mapping
        double[] phenotype = scaleup(genotype);

        // parameters for the opponent observer
        // "MSD"
        params.setProperty(MrRacer2012.PLAN + Plan2011.OBSERVER + Observer2012.MIN_SWITCH_DISTANCE, String.valueOf(phenotype[0]));
        // "SIM"
        params.setProperty(MrRacer2012.PLAN + Plan2011.OBSERVER + Observer2012.SWITCH_INCREASE_MAX_FACTOR, String.valueOf(phenotype[1]));
        // "MDX"
        params.setProperty(MrRacer2012.PLAN + Plan2011.OBSERVER + Observer2012.MIN_DISTANCE_X, String.valueOf(phenotype[2]));
        // "MDY"
        params.setProperty(MrRacer2012.PLAN + Plan2011.OBSERVER + Observer2012.MIN_DISTANCE_Y, String.valueOf(phenotype[3]));
        // "CWI"
        params.setProperty(MrRacer2012.PLAN + Plan2011.OBSERVER + Observer2012.CRITICAL_WIDTH_INCREASE, String.valueOf(phenotype[4]));

        // wait a bit
        try {
            Thread.sleep(1000);
        } catch (Exception e) {
        }


        //System.out.println("Starting opponents");
        //opponents.start();

        

        MrRacer2012 controller = new MrRacer2012();


        controller.setParameters(params);
        controller.setStage(scr.Controller.Stage.RACE);
        controller.setTrackName(trackName);

        String theHost = host;
        if (!hosts.isEmpty()) {
            synchronized (this) {
                int index = ((int) evals / evalsPerHost) % hosts.size();
                theHost = hosts.get(index);
            }
        }

        FitnessEvaluator fe = new FitnessEvaluator(theHost, port + offset,
                new Evaluator(controller, maxTicks), maxLaps);

        while (!fe.finished()) {
            try {
                Thread.sleep(1000);
            } catch (Exception e) {
            }
        }

        //System.out.println("Killing opponents");
        //opponents.stop();

        synchronized (this) {
            ++evals;
            ++validEvals;
            System.out.println("*************    EVALS: " + evals);
        }

        variables.evaluated = true;

        double fitness = fe.getResult().distance * -1.0;
        //boolean valid = !fe.maxDamageReached();

        Fitness[] results = new Fitness[3];
        results[0] = new Fitness(fitness, "Distance", true, "Dist");
        results[1] = new Fitness(fe.getResult().damage, "Damage", true, "Dam");
        results[2] = new Fitness(fe.getOvertakingCtr(), "OTCounter", true, "Otc");

        //System.out.println("Fitness: "+fitness);

        //return new Evaluation(-fe.getResult().getFitness(), true);
        //return new Evaluation(fe.getFastestLap(), true);
        return new Evaluation(results);
    }

    public Individual getTemplate(Parameters par) {
        Individual result = new Individual();

        VariableGroup genotype = new VariableGroup("genotype");

        genotype.add(new RealVariable("MSD", 0, 1, true));
        genotype.add(new RealVariable("SIM", 0, 1, true));
        genotype.add(new RealVariable("MDX", 0, 1, true));
        genotype.add(new RealVariable("MDY", 0, 1, true));
        genotype.add(new RealVariable("CWI", 0, 1, true));

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
        System.out.println(paramName);
        Parameters par = new Parameters(args, paramName);

        BlackBoxProblem prob = new TorcsObserverProblem(par);
        Individual ind = prob.getTemplate(par);
        BatchEA ea = new BatchEA(ind, par, prob);
        ea.run();
    }
}
