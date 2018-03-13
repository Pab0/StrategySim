package regopoulos.elias.scenario;

import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import regopoulos.elias.scenario.ai.Planner;

import java.util.EnumMap;

/**Holds values of options for current scenario.
 * Used for restarting scenarios.
 */
public class ScenarioOptions
{
	public static final int DEFAULT_WOOD_GOAL = 25;
	public static final int DEFAULT_STONE_GOAL = 10;
	public static final int DEFAULT_GOLD_GOAL = 10;

	public static final int DEFAULT_VILLAGER_COUNT = 5;
	public static final int DEFAULT_HUNTER_COUNT = 4;
	public static final int DEFAULT_GUARD_COUNT = 2;
	public static final int DEFAULT_KNIGHT_COUNT = 2;

	private Map map;
	private Team[] teams;
	private EnumMap<TerrainType, Integer> resourceGoals;
	private EnumMap<AgentType, Integer> agentNum;
	private EnumMap<TerrainType, Planner> planners;
	private EnumMap<TerrainType, String> netsToLoad;
	private EnumMap<TerrainType,MultiLayerNetwork> teamNets;

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

	Team[] getTeams()
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

	public void setNetsToLoad(EnumMap<TerrainType, String> netsToLoad)
	{
		this.netsToLoad = netsToLoad;
	}

	public EnumMap<TerrainType, String> getNetsToLoad()
	{
		return netsToLoad;
	}

	public void setTeamNets(EnumMap<TerrainType, MultiLayerNetwork> teamNets)
	{
		this.teamNets = teamNets;
	}

	public EnumMap<TerrainType, MultiLayerNetwork> getTeamNets()
	{
		return teamNets;
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
