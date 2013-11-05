/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.janquadflieg.mrracer.evo.adapter;

import de.janquadflieg.mrracer.Utils;
import de.janquadflieg.mrracer.controller.MrRacer2012;
import de.janquadflieg.mrracer.plan.BrakePredictor;
import de.janquadflieg.mrracer.plan.Plan2011;

import java.io.*;
import java.util.*;

import optimizer.ea.*;

/**
 * Interface betweeen Mike's ea lib and Torcs.
 *
 * @author quad
 */
public class TorcsBrakePredictor
        extends Problem implements ParallelEvaluableProblem {

    /** Dimensions. */
    private int dimensions = 3;

    /** Ip port for function evaluations. */
    /** Creates a new instance of Torcs Problem. */
    public TorcsBrakePredictor(Parameters par) {
    }

    @Override
    public String problemRunHeader() {
        StringBuilder result = new StringBuilder();

        result.append("Dimensions: ").append(dimensions).append("\n");

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

        v = (RealVariable) variables.getAnyVariable("BP_CW");
        params.setProperty(MrRacer2012.PLAN + Plan2011.BRAKE_PREDICTOR + BrakePredictor.CW,
                String.valueOf(v.toDouble() * 100.0));

        v = (RealVariable) variables.getAnyVariable("BP_CA");
        params.setProperty(MrRacer2012.PLAN + Plan2011.BRAKE_PREDICTOR + BrakePredictor.CA,
                String.valueOf(v.toDouble() * 100.0));

        v = (RealVariable) variables.getAnyVariable("BP_MASS");
        params.setProperty(MrRacer2012.PLAN + Plan2011.BRAKE_PREDICTOR + BrakePredictor.MASS,
                String.valueOf(v.toDouble() * 10000.0));

        BrakePredictor predictor = new BrakePredictor();
        predictor.setParameters(params, MrRacer2012.PLAN + Plan2011.BRAKE_PREDICTOR);

        double sqe = 0.0;

        for (double ts1 = 60.0; ts1 < 300.0; ts1 += 10.0) {
            for (double ts2 = 50.0; ts2 < ts1; ts2 += 10.0) {
                double v1 = predictor.calcBrakeDistanceLUT(ts1, ts2, 1.0);
                double v2 = predictor.calcBrakeDistanceAnalytical(ts1, ts2, 1.1, 1.0);
                sqe += (v1-v2)*(v1-v2);
            }
        }

        synchronized (this) {
            ++evals;
            ++validEvals;
        }

        variables.evaluated = true;

        Properties realParams = new Properties();
        predictor.getParameters(realParams, MrRacer2012.PLAN + Plan2011.BRAKE_PREDICTOR);

        String saveTo = "./inds/ind-id" + variables.id;
        String comment = "Individual from ea run, fitness " + sqe;

        try {
            File f = new File("./inds");
            f.mkdirs();
            FileOutputStream out = new FileOutputStream(new File(saveTo));
            realParams.store(out, comment);
            out.close();
        } catch (Exception e) {
            e.printStackTrace(System.out);
        }

        Fitness[] results = new Fitness[1];
        results[0] = new Fitness(sqe, "SquaredError", true, "Error");
        
        //System.out.println("Fitness: "+fitness);

        //return new Evaluation(-fe.getResult().getFitness(), true);
        //return new Evaluation(fe.getFastestLap(), true);
        return new Evaluation(results);

        //return new Evaluation(-fe.getResult().getFitness(), true);
        //return new Evaluation(fe.getFastestLap(), true);
        //return new Evaluation(fe.getOverallTime(), true);
    }

    public Individual getTemplate(Parameters par) {
        Individual result = new Individual();

        VariableGroup genotype = new VariableGroup("genotype");

        genotype.add(new RealVariable("BP_CW", 0, 1, true));
        genotype.add(new RealVariable("BP_CA", 0, 1, true));
        genotype.add(new RealVariable("BP_MASS", 0, 1, true));

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
        return 1;
    }

    public static void main(String[] args) {
        //String paramName = "F:\\Quad\\Experiments\\Torcs-Test\\torcs-config.properties";
        String paramName = null;
        if (args.length == 1) {
            paramName = args[ 0];
        }
        Parameters par = new Parameters(args, paramName);

        BlackBoxProblem prob = new TorcsBrakePredictor(par);
        Individual ind = prob.getTemplate(par);
        BatchEA ea = new BatchEA(ind, par, prob);
        ea.run();
    }
}
