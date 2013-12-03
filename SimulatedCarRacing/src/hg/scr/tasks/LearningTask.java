package hg.scr.tasks;

import scr.Controller;


public class LearningTask extends BasicTask {

	public int evaluate(Controller controler)
	{
		options.setControler(controler);
		options.setParms(new String[]{});
//	    options.setControlerName("");
//	    options.reset(options);
//	    fitnessEvaluations++; // TODO : remove either or two currentEvaluation or fitnessEvaluations
	    this.runSingleEpisode(1);
	    return this.getEvaluationInfo().computeWeightedFitness();
	}
	
	public int evaluate(String controlerName)
	{
	   
	    options.setControlerName(controlerName);
//	    options.reset(options);
//	    fitnessEvaluations++; // TODO : remove either or two currentEvaluation or fitnessEvaluations
	    this.runSingleEpisode(1);
	    return this.getEvaluationInfo().computeWeightedFitness();
	}
	
}
