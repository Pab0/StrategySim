package regopoulos.elias.scenario.ai;

import regopoulos.elias.scenario.Agent;
import regopoulos.elias.scenario.Team;

import java.util.ArrayList;

/**Winter-AI
 *
 */
public class WinterAI extends Planner
{
	private static final String NAME = "WinterAI";

	public WinterAI(Team lnkTeam)
	{
		super(lnkTeam);
	}

	@Override
	boolean isElligibleForAction(Agent agent, ActionType actionType)
	{
		return true;
	}

	@Override
	Action chooseBestAction(ArrayList<Action> possibleActions, Agent agent)
	{
		return null;
	}

	@Override
	public String getPlannerName()
	{
		return WinterAI.NAME;
	}
}
