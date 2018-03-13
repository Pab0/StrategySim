package regopoulos.elias.scenario.ai;

import regopoulos.elias.scenario.Agent;
import regopoulos.elias.scenario.Team;
import regopoulos.elias.scenario.TerrainType;
import regopoulos.elias.scenario.pathfinding.Pathfinder;
import regopoulos.elias.scenario.pathfinding.PathfinderGoals;
import regopoulos.elias.sim.Simulation;

import java.util.ArrayList;

/**Responsible for selecting Actions and setting goals,
 * which are then fed to the pathfinder.
 * Each team's agents share the same planner.
 *
 * The four AIs are the permutation of:
 * {Neural Net, Custom Rules} and {Distinct roles, Free roles},
 * and are named after the year's seasons.
 *
 * 					Neural Net:	|Custom Rules:
 * Free roles:		|WinterAI	|SummerAI
 * Distinct roles:	|AutumnAI	|SpringAI
 */
public abstract class Planner
{
	public static final String DEFAULT_AI = "WinterAI";
	Team lnkTeam;

	public Planner(Team lnkTeam)
	{
		this.lnkTeam = lnkTeam;
	}

	public Action getNextAction(Agent agent)
	{
		updatePossibleActions(agent);
		return chooseBestAction(agent.getPossibleActions(), agent);
	}

	public void updatePossibleActions(Agent agent)
	{
		ArrayList<Action> possibleActions = getPossibleActions(agent);
		calcPaths(possibleActions, agent);
		agent.setPossibleActions(possibleActions);
	}

	/**Returns all possible actions for this agent */
	private ArrayList<Action> getPossibleActions(Agent agent)
	{
		PathfinderGoals pfg = new PathfinderGoals();
		for (ActionType actionType : ActionType.values())
		{
			if (isEligibleForAction(agent, actionType))
			{
				pfg.addGoal(actionType, agent.getTeam());
			}
		}
		Pathfinder pathfinder = Simulation.sim.getScenario().getPathfinder();
		return pathfinder.getPossibleActions(pfg, agent).toArrayList();
	}

	/**Uses A* and weighted pathfinding to find a short yet safe path to goal. */
	private void calcPaths(ArrayList<Action> possibleActions, Agent agent)
	{
		Simulation.sim.getScenario().getNodeWeightSetter().update(agent);	//Update risk map of world
		for (Action action : possibleActions)
		{
			action.setPath(Simulation.sim.getScenario().getPathfinder().getWeightedPathToGoal(agent, action));
		}
	}

	private boolean isEligibleForAction(Agent agent, ActionType actionType)
	{
		boolean eligible = true;
		if (actionType==ActionType.DROP_OFF && !agent.isCarryingResource())	//Agents can't just appear at the DropOffSite empty-handed
		{
			eligible = false;
		}
		if (actionType.isGatheringAction() && agent.isCarryingResource())	//Agents can't just drop the precious resources they gathered
		{
			eligible = false;
		}

		boolean rolePermits = rolePermits(agent, actionType);
		return eligible && rolePermits;
	}

	abstract boolean rolePermits(Agent agent, ActionType actionType);

	abstract Action chooseBestAction(ArrayList<Action> possibleActions, Agent agent);

	public abstract boolean usesNeuralNet();

	/**Creates and returns Planner implementation named `name`
	 *
	 * @param name The name of the class. Has to implement Planner.
	 * @return
	 */
	public static Planner createPlannerByName(String name, Team lnkTeam)
	{
		Planner planner = null;
		switch (name)
		{
			case SummerAI.NAME:
				planner = new SummerAI(lnkTeam);
				break;
			case SpringAI.NAME:
				planner = new SpringAI(lnkTeam);
				break;
			case WinterAI.NAME:
				planner = new WinterAI(lnkTeam);
				break;
			case AutumnAI.NAME:
				planner = new AutumnAI(lnkTeam);
				break;
		}
		return planner;
	}

	public static ArrayList<String> getPlannerTypes()
	{
		ArrayList<String> plannerTypes = new ArrayList<>();
		plannerTypes.add("SummerAI");
		plannerTypes.add("SpringAI");
		plannerTypes.add("WinterAI");
		plannerTypes.add("AutumnAI");
		return plannerTypes;
	}

	abstract public String getPlannerName();

	/** Only info, doesn't create any persisting objects.
	 * @return whether the planner uses a neural network.
	 * @param plannerName String describing the planner's name.
	 */
	public static boolean usesNet(String plannerName)
	{
		boolean usesNet = false;
		if (plannerName==null)
		{
			plannerName = Planner.DEFAULT_AI;
		}
		switch (plannerName)
		{
			case "SummerAI":
			case "SpringAI":
				usesNet = false;
				break;
			case "WinterAI":
			case "AutumnAI":
				usesNet = true;
				break;
		}
		return usesNet;
	}
}
