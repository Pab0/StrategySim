package regopoulos.elias.scenario.ai;

import org.deeplearning4j.datasets.iterator.impl.ListDataSetIterator;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.Updater;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.lossfunctions.LossFunctions;
import regopoulos.elias.scenario.Agent;
import regopoulos.elias.scenario.Team;

import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class QLearning
{
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
	private static final double FINAL_EPSILON = 0.1;
	private static final double EPSILON_DECAY = 0.01;	//as percent of current epsilon
	private static final int EPSILON_INCREASE_THRESHOLD = 1000;	//Determines # of agent steps without action, after which epsilon is increasing instead of decaying
	private int stepsSinceLastAction;	//Counts agent steps since last action, to be used in epsilon value updates
	private double epsilon = INITIAL_EPSILON; 	//decays

	private MultiLayerNetwork net;
	private int nInputCount, nHiddenCount, nOutputCount;

	private State oldState, curState;

	QLearning(Team lnkTeam)
	{
		setNet(lnkTeam);
	}

	private void setNet(Team lnkTeam)	//TODO use existing net, if available, or load from file
	{
		setNodeCounts();
		net = NetStorage.getNet(lnkTeam.getTerrainType());
		if (net==null)
		{
			createNet();
			NetStorage.storeNet(net,lnkTeam.getTerrainType());
		}
	}

	private void createNet()
	{
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
		this.net.init();
	}

	private void setNodeCounts()
	{
		this.nInputCount = State.getNInputCount();
		this.nHiddenCount = this.nInputCount;
		this.nOutputCount = State.getNOutputCount();
	}

	/** @return action that maximizes Q, for given state */
	Action getMaxQAction(State state)
	{
		Action action;
		QLearning.rng.setSeed(state.hashCode());	//set random to specific value to avoid "flickering" between two actions (NN-chosen and epsilon-random)
		if (QLearning.rng.nextDouble()<this.epsilon)
		{
			action = getRandomAction(state);
		}
		else
		{
			double[] curState = state.getStateVector();
			Action[] actions = state.getActions();
			INDArray arr = getActionVector(curState, this.net);
			int maxIndex = QLearning.getMaxEligibleOutputIndex(arr,state);
			action = (maxIndex==-1)?null:actions[maxIndex];
		}
		return action;
	}

	/** @return output array from give state */
	private static INDArray getActionVector(double[] stateVector, MultiLayerNetwork net)
	{
		INDArray inputs = Nd4j.create(stateVector);
		return net.output(inputs);
	}

	//convenience method for public calls
	public INDArray getActionVector(double[] stateVector)
	{
		return QLearning.getActionVector(stateVector,this.net);
	}

	/** @return max output node value, only if its action is eligible */
	private static double getMaxEligibleOutput(INDArray actionVector, State state)
	{
		Action[] actions = state.getActions();
		double max = -Double.MAX_VALUE;
		for (int i=0; i<actionVector.length(); i++)
		{
			if (actionVector.getDouble(i)>max && actions[i]!=null) //all null actions are ignored
			{
				max = actionVector.getDouble(i);
			}
		}
		return max;
	}

	/** @return index of max eligible output node, or -1 if none is found */
	private static int getMaxEligibleOutputIndex(INDArray actionVector, State state)
	{
		Action[] actions = state.getActions();
		double max = -Double.MAX_VALUE;
		int maxIndex = -1;
		for (int i=0; i<actionVector.length(); i++)
		{
			if (actionVector.getDouble(i)>max && actions[i]!=null)
			{
				max = actionVector.getDouble(i);
				maxIndex = i;
			}
		}
		return maxIndex;
	}

	/** @return random eligible action.
	 * If no eligible actions are available, return null instead */
	private static Action getRandomAction(State state)
	{
		Action[] actions = state.getActions();
		Random random = new Random();
		int index = random.nextInt(actions.length);
		int initIndex = index;
		Action action = null;
		//Picks a random index and searches forward until a non-null action was found,
		//or the array has been fully searched.
		while (action==null)
		{
			action = actions[index++];
			index %= actions.length;
			if (index==initIndex)	//array fully searched, return null
			{
				break;
			}
		}
		return action;
	}

	/** Gets called after agent's action/movement has been carried out */
	public void updateQValue(Agent agent, Action action, boolean didAction)
	{
		setOldState(getCurState());
		setCurState(agent.getState());
		//r (reward)
		double reward = Reward.getReward(agent, action, didAction);
		//s (old State)
		double[] oldStateVector = getOldState().getStateVector();
		//s' (new State)
		double[] curStateVector = getCurState().getStateVector();
		//Q(s,a) (old output array)
		INDArray output = getActionVector(oldStateVector, net);
		//maxQ(s',a) (max ouput of new state array)
		double maxNewOutput = getMaxEligibleOutput(getActionVector(curStateVector, net),getCurState());
		//index of maxQ(s',a)
		int maxOutputIndex = getMaxEligibleOutputIndex(getActionVector(curStateVector,net),getCurState());

		if (maxOutputIndex>=0)	//don't train network if there is no eligible action
		{
			//new targetVector (aka updated output vector)
			INDArray targetVector = getTargetVector(output,maxNewOutput,maxOutputIndex,reward);

			DataSet dataSet = new DataSet(Nd4j.create(oldStateVector), targetVector);
			List<DataSet> listDs = dataSet.asList();
			DataSetIterator dsi = new ListDataSetIterator(listDs, 1);
			dsi.reset();
			net.fit(dsi);
		}
	}

	private INDArray getTargetVector(INDArray output, double maxNewOutput, int maxOutputIndex, double reward)
	{
		double newValue = reward + QLearning.GAMMA*maxNewOutput;	//  //`r+gamma*maxaQ(s',a')`
		output.putScalar(maxOutputIndex,newValue);
		return output;
	}

	/** Implements epsilon decay */
	public void updateEpsilon(boolean didAction)
	{
		double newEpsilon = epsilon;
		if (didAction)
		{
			this.stepsSinceLastAction = 0;
			newEpsilon = epsilon*(1-QLearning.EPSILON_DECAY);
		}
		else
		{
			this.stepsSinceLastAction++;
			if (stepsSinceLastAction>QLearning.EPSILON_INCREASE_THRESHOLD)
			{
				newEpsilon = epsilon*(1+QLearning.EPSILON_DECAY);
			}
		}
		epsilon = Math.max(newEpsilon, QLearning.FINAL_EPSILON);
		epsilon = Math.min(newEpsilon, QLearning.INITIAL_EPSILON);
	}

	public void setOldState(State oldState)
	{
		this.oldState = oldState;
	}

	public State getOldState()
	{
		return oldState;
	}

	public void setCurState(State curState)
	{
		this.curState = curState;
	}

	public State getCurState()
	{
		return curState;
	}
}
