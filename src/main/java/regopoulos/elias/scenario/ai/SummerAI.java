package regopoulos.elias.scenario.ai;

import org.apache.commons.lang3.ObjectUtils;
import regopoulos.elias.scenario.Agent;
import regopoulos.elias.scenario.Team;
import regopoulos.elias.scenario.TerrainType;
import regopoulos.elias.scenario.pathfinding.Pathfinder;
import regopoulos.elias.scenario.pathfinding.PathfinderGoals;
import regopoulos.elias.sim.Simulation;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Random;

/**SummerAI features free roles for all agents,
 * and custom rules for planning.
 * Rules are naive and greedy.
 */
public class SummerAI extends Planner
{
	private static final String NAME = "SummerAI";

	private static final double DEFAULT_AGGRO = 0.5;
	private static final int RANDOM_SEED = 0;
	private Random random;
	private double aggressiveness;	//likelihood of attacking: [0,1]

	public SummerAI(Team lnkTeam, double aggressiveness)
	{
		super(lnkTeam);
		this.aggressiveness = aggressiveness;
		this.random = new Random(SummerAI.RANDOM_SEED);
	}

	public SummerAI(Team lnkTeam)
	{
		this(lnkTeam, SummerAI.DEFAULT_AGGRO);
	}

	@Override
	public Action getNextAction(Agent agent)
	{
		ArrayList<Action> possibleActions = getPossibleActions(agent);
		agent.setPossibleActions(possibleActions);
		return chooseBestAction(possibleActions, agent);
	}

	/**Returns all possible actions for this agent */
	private ArrayList<Action> getPossibleActions(Agent agent)
	{
		PathfinderGoals pfg = new PathfinderGoals();
		for (ActionType actionType : ActionType.values())
		{
			if (isElligibleForAction(agent, actionType))
			{
				pfg.addGoal(actionType, agent.getTeam());
			}
		}
		Pathfinder pathfinder = Simulation.sim.getScenario().getPathfinder();
		return pathfinder.getPossibleActions(pfg, agent).toArrayList();
	}

	/**All SummerAI agents are free to do all actions,
	 * so this always returns true.
	 */
	//TODO: SpringAI has terrainType.canBeDoneBy(agent.getType()) instead
	public boolean isElligibleForAction(Agent agent, ActionType type)
	{
		return true;
	}

	/**Chooses best, according to AI's criteria, action among possible ones*/
	private Action chooseBestAction(ArrayList<Action> possibleActions, Agent lnkAgent)
	{
		Action bestAction = null;

		//if randomDouble<aggressiveness attack closest enemy (if available)
		if (random.nextDouble()<aggressiveness && possibleActions.stream().anyMatch(action -> action.type==ActionType.ATTACK))
		{
			bestAction = possibleActions.stream().
					filter(action -> action.type==ActionType.ATTACK).
					min(Comparator.comparing(action -> action.path.size())).get();
		}
		//if carrying resource, return it to closest dropOffSite (if found path to it)
		else if (lnkAgent.isCarryingResource() && possibleActions.stream().anyMatch(action -> action.type==ActionType.DROP_OFF))
		{
			bestAction = possibleActions.stream().
				filter(action -> action.type==ActionType.DROP_OFF).
				min(Comparator.comparing(action -> action.path.size())).get();
		}
		//if not carrying, go for closest resource still needed
		else if (possibleActions.stream().anyMatch(action -> lnkTeam.getResourcesStillNeeded().contains(action.type.terrainType)))
		{
			bestAction = possibleActions.stream().
					filter(action -> lnkTeam.getResourcesStillNeeded().contains(action.type.terrainType)).
					min(Comparator.comparing(action -> action.path.size())).get();
		}
		//if no such (reachable) resource, explore closest unknown tile
		else if (possibleActions.stream().anyMatch(action -> action.type.terrainType== TerrainType.UNKNOWN))
		{
			bestAction = possibleActions.stream().
					filter(action -> action.type==ActionType.EXPLORE).
					min(Comparator.comparing(action -> action.path.size())).get();
		}
		//if no (reachable) unknown tile, gather closest resource regardless if needed or not
		else if (possibleActions.stream().anyMatch(action -> action.type.isGatheringAction()))
		{
			bestAction = possibleActions.stream().
					filter(action -> action.type.isGatheringAction()).
					min(Comparator.comparing(action -> action.path.size())).get();
		}
		else
		{
			//No (path to) action found - most likely surrounded by teammates
		}
		return bestAction;
	}

	@Override
	public String getPlannerName()
	{
		return SummerAI.NAME;
	}
}
