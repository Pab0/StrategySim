package regopoulos.elias.scenario;

import javafx.geometry.Dimension2D;
import regopoulos.elias.scenario.ai.SpringAI;
import regopoulos.elias.scenario.ai.SummerAI;
import regopoulos.elias.scenario.pathfinding.Pathfinder;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashMap;

public class Scenario
{

	public static final int DEFAULT_WOOD_GOAL = 5;
	public static final int DEFAULT_GOLD_GOAL = 2;

	public static final int DEFAULT_VILLAGER_COUNT = 5;

	private Map map;
	private Pathfinder pathfinder;
	private Team[] teams;
	private GaiaTeam gaia;
	private EnumMap<TerrainType, Integer> resourceGoals;
	private EnumMap<AgentType, Integer> agentNum;
	private HashMap<Dimension2D, Agent> agentPos;	//keeps a record of all positions occupied by an agent
	// TODO update this whenever an Agent moves

	public Scenario()
	{
		this.gaia = new GaiaTeam();	//Gaia is omnipresent
		this.agentPos = new HashMap<Dimension2D, Agent>();
	}

	public HashMap<Dimension2D, Agent> getPositionsWithAgents()
	{
		return agentPos;
	}

	public void setMap(Map map)
	{
		this.map = map;
	}

	public Map getMap()
	{
		return map;
	}

	public void setPathfinder()
	{
		this.pathfinder = new Pathfinder();
	}

	public Pathfinder getPathfinder()
	{
		return pathfinder;
	}

	public void setTeams(Team[] teams)
	{
		this.teams = teams;
	}

	public Team[] getTeams()
	{
		return teams;
	}

	private Team getTeamByID(int ID)
	{
		return Arrays.stream(this.teams).filter(team -> team.getTeamID()==ID).findAny().get();
	}

	/**Convenience method for getTeamByID(int) */
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

	public void initTeams() throws NotEnoughTilesFoundException
	{
		setPlanners();
		setDropOffSites();
		setVisibleMaps();
		setAgents();
		setResources();
	}

	private void setPlanners()
	{
		for (Team team : teams)
		{
			//TODO set correct planner for each team
			team.setPlanner(new SummerAI(team, 0.1));	//TODO set aggressiveness back to ~0.5
		}
	}

	private void setAgents() throws NotEnoughTilesFoundException
	{
		for (Team team : teams)
		{
			team.setAgents(agentNum);
			System.out.println("Set agents of " + team);
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
		for (Team team : this.teams)
		{
			team.setDropOffSites();
		}
	}

	private void setResources()
	{
		for (Team team : this.teams)
		{
			team.setResources(resourceGoals);
		}
	}

	/**Returns next team from scenario.
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

	/**We have to choose between storing either the position of each agent
	 * or the agents in each tile, in order to have a Single Source of Truth.
	 * Seeing as agents<<tiles in every scenario, the former is computationally cheaper,
	 * and we can instantly look the latter up on our HashMap.
	 */
	public Agent getAgentAtPos(Dimension2D position)
	{
		return this.agentPos.get(position);
	}


	public Agent getAgentAtPos(int y, int x)
	{
		return getAgentAtPos((new Dimension2D(x,y)));
	}


}
