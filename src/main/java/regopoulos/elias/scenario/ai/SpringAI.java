package regopoulos.elias.scenario.ai;

import regopoulos.elias.scenario.Agent;
import regopoulos.elias.scenario.Team;

import java.util.ArrayList;
import java.util.Arrays;

public class SpringAI extends SummerAI
{
	static final String NAME = "SpringAI";

	private static final ActionType[] VILLAGER_ACTIONS = {
			ActionType.EXPLORE,
			ActionType.DROP_OFF,
			ActionType.GATHER_WOOD,
			ActionType.GATHER_STONE,
			ActionType.GATHER_GOLD};
	private static final ActionType[] HUNTER_ACTIONS = {
			ActionType.EXPLORE,
			ActionType.DROP_OFF,
			ActionType.GATHER_WOOD,
			ActionType.ATTACK};
	private static final ActionType[] GUARD_ACTIONS = {
			ActionType.ATTACK};
	private static final ActionType[] KNIGHT_ACTIONS = {
			ActionType.EXPLORE,
			ActionType.ATTACK};

	private ArrayList<ActionType> villagerActions, hunterActions, guardActions, knightActions;

	SpringAI(Team lnkTeam)
	{
		super(lnkTeam);
		this.villagerActions = new ArrayList<>(Arrays.asList(VILLAGER_ACTIONS));
		this.hunterActions = new ArrayList<>(Arrays.asList(HUNTER_ACTIONS));
		this.guardActions = new ArrayList<>(Arrays.asList(GUARD_ACTIONS));
		this.knightActions = new ArrayList<>(Arrays.asList(KNIGHT_ACTIONS));
	}

	@Override
	public boolean isElligibleForAction(Agent agent, ActionType type)
	{
		boolean eligible = false;
		switch (agent.getType())
		{
			case VILLAGER:
				eligible = villagerActions.contains(type);
				break;
			case HUNTER:
				eligible = hunterActions.contains(type);
				break;
			case GUARD:
				eligible = guardActions.contains(type);
				break;
			case KNIGHT:
				eligible = knightActions.contains(type);
		}
		return eligible;
	}

	@Override
	public String getPlannerName()
	{
		return SpringAI.NAME;
	}
}
