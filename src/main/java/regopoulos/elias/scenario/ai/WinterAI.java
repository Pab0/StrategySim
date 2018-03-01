package regopoulos.elias.scenario.ai;

import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.Updater;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.lossfunctions.LossFunctions;
import regopoulos.elias.scenario.Agent;
import regopoulos.elias.scenario.Team;
import regopoulos.elias.sim.Simulation;
import sun.nio.ch.SelectorImpl;

import java.util.ArrayList;
import java.util.Random;

/**Winter-AI
 *
 */
public class WinterAI extends Planner
{
	static final String NAME = "WinterAI";

	//Random number generator seed, for reproducibility
	private static final int seed = 123456;
	//Number of iterations per minibatch
	private static final int iterations = 1;
	//Network learning rate
	private static double learningRate = 0.001; //was 0.0001
	private static final Random rng = new Random(seed);
	private static final String FILE_NAME= "RLStrategyNet.zip";

	private static final double GAMMA = 0.99;
	private static final double INITIAL_EPSILON = 0.5;
	private static double epsilon = INITIAL_EPSILON; 	//decays

	private MultiLayerNetwork net;
	private int nInputCount, nHiddenCount, nOutputCount;

	public WinterAI(Team lnkTeam)
	{
		super(lnkTeam);
		setNet();
	}

	@Override
	boolean isElligibleForAction(Agent agent, ActionType actionType)
	{
		return true;
	}

	@Override
	Action chooseBestAction(ArrayList<Action> possibleActions, Agent agent)
	{
		//TODO for each action get state, Q-Learning magic, get best action
		System.out.println("Foo");
		Simulation.sim.log(agent.getState().toString());
		return null;
	}

	private void setNet()
	{
		setNodeCounts();
		this.net = new MultiLayerNetwork(new NeuralNetConfiguration.Builder()
			.seed(seed)
			.iterations(iterations)
			.optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
			.learningRate(learningRate)
			.weightInit(WeightInit.XAVIER)
			.updater(Updater.NESTEROVS)     //To configure: .updater(new Nesterovs(0.9))
			.list()
			.layer(0, new DenseLayer.Builder().nIn(nInputCount).nOut(nHiddenCount)
					.activation(Activation.RELU) 	//was: TANH
					.build())
			.layer(1, new OutputLayer.Builder(LossFunctions.LossFunction.MSE)
					.activation(Activation.IDENTITY) //was: don't remember
					.nIn(nHiddenCount).nOut(nOutputCount).build())
			.pretrain(false).backprop(true).build());
	}

	private void setNodeCounts()
	{
		this.nInputCount = State.getNInputCount();
		this.nHiddenCount = this.nInputCount;
		this.nOutputCount = State.getNOutputCount();
	}

	@Override
	public String getPlannerName()
	{
		return WinterAI.NAME;
	}
}
