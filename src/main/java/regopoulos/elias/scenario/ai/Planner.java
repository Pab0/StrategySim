package regopoulos.elias.scenario.ai;

import regopoulos.elias.scenario.Agent;
import regopoulos.elias.scenario.Team;
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
	public static final String DEFAULT_AI = "SummerAI";
	//TODO: Add methods, implement Spring-, Summer-, AutumnAI- and Winter-AI
	Team lnkTeam;

	public Planner(Team lnkTeam)
	{
		this.lnkTeam = lnkTeam;
	}
	public Action getNextAction(Agent agent)
	{
		ArrayList<Action> possibleActions = getPossibleActions(agent);
		calcPaths(possibleActions, agent);
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

	/**Uses A* and weighted pathfinding to find a short yet safe path to goal. */
	private void calcPaths(ArrayList<Action> possibleActions, Agent agent)
	{
		Simulation.sim.getScenario().getNodeWeightSetter().update(agent);	//Update risk map of world
		for (Action action : possibleActions)
		{
			action.setPath(Simulation.sim.getScenario().getPathfinder().getWeightedPathToGoal(agent, action));
		}
	}

	abstract boolean isElligibleForAction(Agent agent, ActionType actionType);

	abstract Action chooseBestAction(ArrayList<Action> possibleActions, Agent agent);

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
			case "SummerAI":
				planner = new SummerAI(lnkTeam);
				break;
			case "SpringAI":
				planner = new SpringAI(lnkTeam);
				break;
				//TODO
		}
		return planner;
	}

	public static ArrayList<String> getPlannerTypes()
	{
		ArrayList<String> plannerTypes = new ArrayList<>();
		plannerTypes.add("SummerAI");
		plannerTypes.add("SpringAI");
		//TODO add the other two as well
		return plannerTypes;
	}

	abstract public String getPlannerName();
}
