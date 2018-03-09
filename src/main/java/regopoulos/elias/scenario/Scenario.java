package regopoulos.elias.scenario;

import javafx.geometry.Dimension2D;
import regopoulos.elias.scenario.ai.*;
import regopoulos.elias.scenario.pathfinding.NodeWeightSetter;
import regopoulos.elias.scenario.pathfinding.Pathfinder;
import regopoulos.elias.sim.SimLoop;
import regopoulos.elias.sim.Simulation;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashMap;

public class Scenario
{
	private ScenarioOptions scenarioOptions;
	private Map map;
	private Pathfinder pathfinder;
	private NodeWeightSetter nodeWeightSetter;
	private Team[] teams;
	private GaiaTeam gaia;
	private EnumMap<TerrainType, Integer> resourceGoals;
	private EnumMap<AgentType, Integer> agentNum;
	private HashMap<Dimension2D, Agent> agentPos;	//keeps a record of all positions occupied by an agent
	private int runCount;	//keeps track of how many times the current Scenario has already finished.

	public Scenario()
	{
		this.gaia = new GaiaTeam();	//Gaia is omnipresent
	}

	public void setOptions(ScenarioOptions options)
	{
		this.scenarioOptions = options;
	}

	public ScenarioOptions getScenarioOptions()
	{
		return scenarioOptions;
	}

	/**All operations needed when Scenario
	 * is initiliazed or restarted.
	 */
	public void init() throws NotEnoughTilesFoundException
	{
		scenarioOptions.setOptionsToScenario(this);
		NetStorage.init();
		this.agentPos = new HashMap<>();
		this.setPathfinder();
		this.setNodeWeightSetter();
		this.getMap().setDropOffSites();
		this.initTeams();
		State.initCalculations();
		Simulation.sim.getSimUI().initOnSimLoad();
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

	private void setPathfinder()
	{
		this.pathfinder = new Pathfinder();
	}

	public Pathfinder getPathfinder()
	{
		return pathfinder;
	}

	private void setNodeWeightSetter()
	{
		this.nodeWeightSetter = new NodeWeightSetter();
	}

	public NodeWeightSetter getNodeWeightSetter()
	{
		return nodeWeightSetter;
	}

	void setTeams(Team[] teams)
	{
		this.teams = teams;
	}

	public Team[] getTeams()
	{
		return teams;
	}

	private Team getTeamByID(int ID)
	{
		return Arrays.stream(this.scenarioOptions.getTeams()).filter(team -> team.getTeamID()==ID).findAny().get();
	}

	/**Convenience method for getTeamByID(int) */
	public Team getTeamByID(TerrainType type)
	{
		return getTeamByID(Character.getNumericValue(type.glyph));
	}

	void setResourceGoals(EnumMap<TerrainType, Integer> resourceGoals)
	{
		this.resourceGoals = resourceGoals;
	}

	int getResourceGoal(TerrainType terrainType)
	{
		return resourceGoals.get(terrainType);
	}

	public void setPlanners(EnumMap<TerrainType, Planner> map)
	{
		for (TerrainType type : map.keySet())
		{
			Team team = Simulation.sim.getScenario().getTeamByID(type);
			team.setPlanner(map.get(type));
			System.out.println("Assigned " + team + " to " + team.getPlanner());
		}
	}

	public GaiaTeam getGaia()
	{
		return gaia;
	}

	public void setAgentNum(EnumMap<AgentType, Integer> agentNum)
	{
		this.agentNum = agentNum;
	}

	public void initTeams() throws NotEnoughTilesFoundException
	{
		setDropOffSites();
		setVisibleMaps();
		setAgents();
		setResources();
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

	public int getRunCount()
	{
		return runCount;
	}

	public boolean hasWinner()
	{
		return Arrays.stream(teams).anyMatch(team -> team.hasWon());
	}

	public void finish()
	{
		this.runCount++;
		Simulation.sim.getLogger().logStats();
	}

	public void close()
	{
		if (Simulation.sim.getSimLoop()!=null)
		{
			Simulation.sim.getSimLoop().stop();	//if a Simloop is already running, stop it
		}
	}

	/** Restarts Scenario, really only calling init().
	 * finish() should have been called before restart(), to stop the SimLoop.
	 */
	public void restart()
	{
		try
		{
			init();
		}
		catch (NotEnoughTilesFoundException e)
		{
			e.printStackTrace();
			System.out.println("This should be impossible, since the map doesn't change between runs.");
		}
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
