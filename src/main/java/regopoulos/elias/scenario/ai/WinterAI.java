package regopoulos.elias.scenario.ai;

import regopoulos.elias.scenario.Agent;
import regopoulos.elias.scenario.Team;

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
	public Action getNextAction(Agent agent)
	{
		//TODO
		return null;
	}

	@Override
	public String getPlannerName()
	{
		return WinterAI.NAME;
	}
}
