package regopoulos.elias.scenario;

import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import regopoulos.elias.scenario.ai.Planner;

import java.io.InputStream;
import java.util.EnumMap;
import java.util.Properties;

/**Holds values of options for current scenario.
 * Used for restarting scenarios.
 */
public class ScenarioOptions
{
	public static int DEFAULT_WOOD_GOAL;
	public static int DEFAULT_STONE_GOAL;
	public static int DEFAULT_GOLD_GOAL;

	public static int DEFAULT_VILLAGER_COUNT;
	public static int DEFAULT_HUNTER_COUNT;
	public static int DEFAULT_GUARD_COUNT;
	public static int DEFAULT_KNIGHT_COUNT;

	private Map map;
	private Team[] teams;
	private EnumMap<TerrainType, Integer> resourceGoals;
	private EnumMap<AgentType, Integer> agentNum;
	private EnumMap<TerrainType, Planner> planners;
	private EnumMap<TerrainType, String> netsToLoad;
	private EnumMap<TerrainType,MultiLayerNetwork> teamNets;

	public static void loadProperties()
	{
		Properties prop = new Properties();
		try (InputStream fis = ScenarioOptions.class.getClassLoader().getResourceAsStream("ScenarioOptions.properties"))
		{
			prop.loadFromXML(fis);
			ScenarioOptions.DEFAULT_WOOD_GOAL = Integer.parseInt(prop.getProperty("DefaultWoodGoal"));
			ScenarioOptions.DEFAULT_STONE_GOAL = Integer.parseInt(prop.getProperty("DefaultStoneGoal"));
			ScenarioOptions.DEFAULT_GOLD_GOAL = Integer.parseInt(prop.getProperty("DefaultGoldGoal"));

			ScenarioOptions.DEFAULT_VILLAGER_COUNT = Integer.parseInt(prop.getProperty("DefaultVillagerCount"));
			ScenarioOptions.DEFAULT_HUNTER_COUNT = Integer.parseInt(prop.getProperty("DefaultHunterCount"));
			ScenarioOptions.DEFAULT_GUARD_COUNT = Integer.parseInt(prop.getProperty("DefaultGuardCount"));
			ScenarioOptions.DEFAULT_KNIGHT_COUNT = Integer.parseInt(prop.getProperty("DefaultKnightCount"));
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

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
