package regopoulos.elias.scenario;

import regopoulos.elias.scenario.ai.Planner;

import java.util.EnumMap;

//TODO: Refactoring: Move some of the calculations in OptionsStage in here
/**Holds values of options for current scenario.
 * Used for restarting scenarios.
 */
public class ScenarioOptions
{
	public static final int DEFAULT_WOOD_GOAL = 5;
	public static final int DEFAULT_STONE_GOAL = 2;
	public static final int DEFAULT_GOLD_GOAL = 0;

	public static final int DEFAULT_VILLAGER_COUNT = 5;

	private Map map;
	private Team[] teams;
	private EnumMap<TerrainType, Integer> resourceGoals;
	private EnumMap<AgentType, Integer> agentNum;
	private EnumMap<TerrainType, Planner> planners;

	public void setMap(Map map)
	{
		this.map = map;
	}

	public void setResourceGoals(EnumMap<TerrainType, Integer> resourceGoals)
	{
		this.resourceGoals = resourceGoals;
	}

	public EnumMap<TerrainType, Integer> getResourceGoals()
	{
		return resourceGoals;
	}

	public void setTeams(Team[] teams)
	{
		this.teams = teams;
	}

	public Team[] getTeams()
	{
		return teams;
	}

	public void setAgentNum(EnumMap<AgentType, Integer> agentNum)
	{
		this.agentNum = agentNum;
	}

	public EnumMap<AgentType, Integer> getAgentNum()
	{
		return agentNum;
	}

	public void setPlanners(EnumMap<TerrainType, Planner> planners)
	{
		this.planners = planners;
	}

	public EnumMap<TerrainType, Planner> getPlanners()
	{
		return planners;
	}

	void setOptionsToScenario(Scenario scenario)
	{
		scenario.setMap(this.map.clone());
		scenario.setResourceGoals(this.resourceGoals);
		scenario.setTeams(this.teams);
		scenario.setAgentNum(this.agentNum);
		scenario.setPlanners(this.planners);
	}
}
