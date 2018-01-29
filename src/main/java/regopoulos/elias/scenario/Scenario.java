package regopoulos.elias.scenario;

import javafx.geometry.Dimension2D;
import regopoulos.elias.sim.Simulation;

import java.util.Arrays;
import java.util.EnumMap;

public class Scenario
{

	public static final int DEFAULT_WOOD_GOAL = 5;
	public static final int DEFAULT_GOLD_GOAL = 2;

	public static final int DEFAULT_VILLAGER_COUNT = 5;

	private Map map;
	private Team[] teams;
	private GaiaTeam gaia;
	private EnumMap<TerrainType, Integer> resourceGoals;
	private EnumMap<AgentType, Integer> agentNum;

	public Scenario()
	{
		this.gaia = new GaiaTeam();	//Gaia is omnipresent
	}

	public void setMap(Map map)
	{
		this.map = map;
	}

	public Map getMap()
	{
		return map;
	}

	public void setTeams(Team[] teams)
	{
		this.teams = teams;
	}

	public Team[] getTeams()
	{
		return teams;
	}

	Team getTeamByID(int ID)
	{
		return Arrays.stream(this.teams).filter(team -> team.getTeamID()==ID).findAny().get();
	}

	/* Convenience method for getTeamByID(int) */
	Team getTeamByID(TerrainType type)
	{
		return getTeamByID(Character.getNumericValue(type.glyph));
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

	public GaiaTeam getGaia()
	{
		return gaia;
	}

	public void setAgentNum(EnumMap<AgentType, Integer> agentNum)
	{
		this.agentNum = agentNum;
		System.out.println(this.agentNum);
	}

	public void initTeams()
	{
		setAgents();
		setVisibleMaps();
		setResources();
	}

	private void setAgents()
	{
		for (Team team : teams)
		{
			team.setAgents(agentNum);
		}
	}

	private void setVisibleMaps()
	{
		Dimension2D mapDimension = new Dimension2D(this.map.getWidth(), this.map.getHeight());
		this.gaia.setVisibleMap(mapDimension);
		for (Team team : this.teams)
		{
			team.setVisibleMap(mapDimension);
		}
	}

	private void setDropOffSites()
	{
		//TODO, or call somewhere else
		for (Team team : this.teams)
		{

		}
	}

	private void setResources()
	{
		for (Team team : this.teams)
		{
			team.setResources(resourceGoals);
		}
	}

	/* Returns next team from scenario.
	 * If curTeam is null, return the first eligible team.
 	 * Returns null if there's no other team.
 	 */
	public Team getNextTeam(Team curTeam)
	{
		boolean foundCurTeam = (curTeam==null);
		Team newTeam = null;
		for (Team team : this.teams)
		{
			if (foundCurTeam)
			{
				newTeam = team;
				break;		//No need to look for other teams
			}
			if (team.equals(curTeam))
			{
				foundCurTeam = true;
			}
		}
		return newTeam;
	}

	public boolean hasNextTeam(Team curTeam)
	{
		return (this.getNextTeam(curTeam)!=null);
	}

	public boolean hasWinner()
	{
		return Arrays.stream(teams).anyMatch(team -> team.hasWon());
	}

	public Team getWinner()
	{
		Team winner = null;
		if (hasWinner())
		{
			winner = Arrays.stream(teams).filter(team -> team.hasWon()).findFirst().get();
		}
		return winner;
	}

	/* We have to choose between storing either the position of each agent
	 * or the agents in each tile, in order to have a Single Source of Truth.
	 * Seeing as agents<<tiles in every scenario, the former is computationally cheaper,
	 * and we can loop through the agents whenever we need the latter.
	 */
	public Agent getAgentAtPos(Dimension2D position)
	{
		return getAgentAtPos((int)position.getHeight(), (int)position.getWidth());
	}


	public Agent getAgentAtPos(int y, int x)
	{
		Agent agentAtPos = null;
		for (Team team : teams)
		{
			if (team.getAgents()==null || team.getAgents()[team.getAgents().length-1]==null)
			{
				continue; 	//this team's agents haven't been initialized yet
			}
			for (Agent agent : team.getAgents())
			{
				if (agent.pos.getHeight()==y &&
						agent.pos.getWidth()==x)
				{
					agentAtPos = agent;
				}
			}
		}
		return agentAtPos;
	}
}
