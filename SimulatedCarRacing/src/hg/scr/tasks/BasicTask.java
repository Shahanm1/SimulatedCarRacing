package hg.scr.tasks;

import hg.scr.tools.ScrOptions;
import scr.Controller;

public class BasicTask implements Task{

	@Override
	public double evaluate(Controller controller) {
		// TODO Auto-generated method stub
		return Eval.eval();
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

}
