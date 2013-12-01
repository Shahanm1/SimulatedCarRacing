package hg.scr.agents;

import hg.ai.evolution.Evolvable;
import hg.ai.evolution.MLP;
import hg.scr.ScrState;
import scr.Action;
import scr.Controller;
import scr.SensorModel;

public class SmallMLPAgent extends Controller implements Evolvable {

	private MLP mlp;
	final int numberOfOutputs = 5;
	final int numberOfInputs = 7;
	static private final String name = "SmallMLPAgent";

	public SmallMLPAgent() {
		// super(name);
		mlp = new MLP(numberOfInputs, 10, numberOfOutputs);
	}

	private SmallMLPAgent(MLP mlp) {
		// super(name);
		this.mlp = mlp;
	}

	public Evolvable getNewInstance() {
		return new SmallMLPAgent(mlp.getNewInstance());
	}

	public Evolvable copy() {
		return new SmallMLPAgent(mlp.copy());
	}

	public void reset() {
		mlp.reset();
	}

	public void mutate() {
		mlp.mutate();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
	}

	@Override
	public Action control(SensorModel sensors) {
		// TODO Auto-generated method stub
		ScrState state = new ScrState(sensors);
		double[] inputs = new double[] { state.getAngle(),
				state.getCurLapTime(), state.getDamage(), state.getFocus(),
				state.getFuel(), state.getGear(), state.getSpeed() };
		double[] outputs = mlp.propagate(inputs);

		// build a CarControl variable and return it
		Action action = new Action();
		action.gear = (int) outputs[0];
		action.steering = outputs[1];
		action.accelerate = outputs[2];
		action.brake = outputs[3];
		action.clutch = outputs[4];
		return action;

	}

	@Override
	public void shutdown() {
		// TODO Auto-generated method stub

	}

}
