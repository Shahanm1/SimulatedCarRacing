package hg.scr.tasks;


import hg.scr.tools.EvaluationInfo;
import hg.scr.tools.ScrOptions;
import scr.Controller;

public class BasicTask implements Task{

	protected ScrOptions options = new ScrOptions();
	private EvaluationInfo evaluationInfo;
	
	@Override
	public int evaluate(Controller controller) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	public boolean runSingleEpisode(final int repetitionsOfSingleEpisode){
	    long c = System.currentTimeMillis();
	    for (int r = 0; r < repetitionsOfSingleEpisode; ++r)
	    {
	        this.reset();
	        RunTask rt = new RunTask(options.getControler(),options.getParms());
	        rt.eval();
	       	this.evaluationInfo = rt.getEvaluationInfo().clone();
	    }

	    return true;
	}

	@Override
	public void setOptionsAndReset(ScrOptions options) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setOptionsAndReset(String options) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void doEpisodes(int amount, boolean verbose,
			int repetitionsOfSingleEpisode) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isFinished() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void printStatistics() {
		// TODO Auto-generated method stub
		
	}

	public ScrOptions getOptions() {
		return options;
	}

	public void setOptions(ScrOptions options) {
		this.options = options;
	}

	public EvaluationInfo getEvaluationInfo() {
		return evaluationInfo;
	}

	public void setEvaluationInfo(EvaluationInfo evaluationInfo) {
		this.evaluationInfo = evaluationInfo;
	}

	@Override
	public int evaluate(String controlerName) {
		// TODO Auto-generated method stub
		return 0;
	}

}
