package regopoulos.elias.scenario;

import java.util.EnumMap;

public class Scenario
{
	private Map map;
	private Team[] teams;
	private EnumMap<TerrainType, Integer> resourceGoals;
	private EnumMap<AgentType, Integer> agentNum;

	public void setMap(Map map)
	{
		this.map = map;
	}

	public void setTeams(Team[] teams)
	{
		this.teams = teams;
	}

	public void setResourceGoals(EnumMap<TerrainType, Integer> resourceGoals)
	{
		this.resourceGoals = resourceGoals;
		System.out.println(this.resourceGoals);
	}

	int getResourceGoal(TerrainType terrainType)
	{
		return resourceGoals.get(terrainType);
	}

	public void setAgentNum(EnumMap<AgentType, Integer> agentNum)
	{
		this.agentNum = agentNum;
		System.out.println(this.agentNum);
	}
}
