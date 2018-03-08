package regopoulos.elias.scenario.ai;

import org.nd4j.linalg.api.ndarray.INDArray;
import regopoulos.elias.scenario.Agent;
import regopoulos.elias.scenario.Team;
import regopoulos.elias.sim.Simulation;

import java.util.ArrayList;

/**Winter-AI
 *
 */
public class WinterAI extends Planner
{
	static final String NAME = "WinterAI";
	private QLearning qLearning;

	public WinterAI(Team lnkTeam)
	{
		super(lnkTeam);
		this.qLearning = new QLearning(lnkTeam);
	}

	@Override
	boolean rolePermits(Agent agent, ActionType actionType)
	{
		return true;
	}

	@Override
	Action chooseBestAction(ArrayList<Action> possibleActions, Agent agent)
	{
		qLearning.setCurState(agent.getState());
		Action bestAction = qLearning.getMaxQAction(agent.getState());
		return bestAction;
	}

	@Override
	public boolean usesNeuralNet()
	{
		return true;
	}

	public QLearning getQLearning()
	{
		return qLearning;
	}

	@Override
	public String getPlannerName()
	{
		return WinterAI.NAME;
	}
}
