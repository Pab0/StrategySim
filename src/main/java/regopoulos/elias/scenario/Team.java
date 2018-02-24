package regopoulos.elias.scenario;

import javafx.geometry.Dimension2D;
import javafx.scene.image.Image;
import regopoulos.elias.scenario.ai.Action;
import regopoulos.elias.scenario.ai.Planner;
import regopoulos.elias.sim.Simulation;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class Team implements MapViewTeam
{
	private static final String ICON_FOLDER = "icons/teams/";	//for the teamSpots

	private boolean[][] visibleMap;		//mask of visible portion of scenario's mapprivate HashMap<Dimension2D, Integer> nodeRisks;
	private Planner planner;
	private List<Dimension2D> dropOffSites;
	private ArrayList<Agent> agents;
	private int teamID;
	private Image teamSpotIcon;
	private TerrainType terrainType;	//used to denote the team's dropOffSites on the map
	private EnumMap<TerrainType,Resource> resources;

	public Team(TerrainType terrainType)
	{
		this.teamID = Character.getNumericValue(terrainType.glyph);
		this.teamSpotIcon = new Image(ICON_FOLDER + this.teamID + ".png");
		this.terrainType = terrainType;
	}

	public int getTeamID()
	{
		return teamID;
	}

	public void setVisibleMap(Dimension2D mapDim)
	{
		this.visibleMap = new boolean[(int)mapDim.getHeight()][(int)mapDim.getWidth()];
	}

	public Image getTeamSpotIcon()
	{
		return teamSpotIcon;
	}

	public TerrainType getTerrainType()
	{
		return terrainType;
	}

	/**Sets this team's dropOffSites from Map */
	public void setDropOffSites()
	{
		Map map = Simulation.sim.getScenario().getMap();
		this.dropOffSites = new ArrayList<Dimension2D>();
		for (int i=0; i<map.getHeight(); i++)
		{
			for (int j=0; j<map.getWidth(); j++)
			{
				if (map.getTileMap()[i][j].getTerrainType()==this.terrainType)
				{
					this.dropOffSites.add(new Dimension2D(j,i));
				}
			}
		}
	}


	public List<Dimension2D> getDropOffSites()
	{
		return this.dropOffSites;
	}

	public void setAgents(EnumMap<AgentType, Integer> agentNum) throws NotEnoughTilesFoundException
	{
		this.agents = new ArrayList<Agent>();
		for (AgentType type : agentNum.keySet())
		{
			for (int i=0; i<agentNum.get(type); i++)
			{
				Agent agent = new Agent(type, this, i);
				agents.add(agent);
			}
		}
		//set to initial positions
		ArrayList<Action> initAgentPositions = Simulation.sim.getScenario().getPathfinder().findNearestEmptyTiles(this, this.agents.size());
		for (int i=0; i<agents.size(); i++)
		{
			agents.get(i).setPos(initAgentPositions.get(i).getPoI());
			System.out.println("Set " + agents.get(i) + " to tile " + agents.get(i).pos);
		}
	}

	public ArrayList<Agent> getAgents()
	{
		return agents;
	}

	public void setResources(EnumMap<TerrainType,Integer> resourceGoals)
	{
		EnumMap<TerrainType,Resource> resources = new EnumMap<TerrainType,Resource>(TerrainType.class);
		for (TerrainType resourceType : resourceGoals.keySet())
		{
			resources.put(resourceType, new Resource(resourceType));
		}
		this.resources = resources;
	}

	public EnumMap<TerrainType, Resource> getResources()
	{
		return resources;
	}

	public Resource getResource(TerrainType resourceType)
	{
		return resources.get(resourceType);
	}

	public ArrayList<TerrainType> getResourcesStillNeeded()
	{
		ArrayList res =  resources.values().stream().
				filter(resource -> resource.getCurrent()<resource.getGoal()).
				map(resource -> resource.getType()).
				collect(Collectors.toCollection(ArrayList::new));
		return res;
	}

	public void setPlanner(Planner planner)
	{
		this.planner = planner;
	}

	@Override
	public boolean[][] getVisibleMap()
	{
		return this.visibleMap;
	}

	public boolean canSee(Dimension2D dim)
	{
		return this.visibleMap[(int)dim.getHeight()][(int)dim.getWidth()];
	}

	/**Returns tile as seen by team, aka true tile or unknown */
	public Tile getTeamTile(int y, int x)
	{
		Tile tile = new Tile(TerrainType.UNKNOWN);
		if (this.visibleMap[y][x])
		{
			tile = Simulation.sim.getScenario().getMap().map[y][x];
		}
		return tile;
	}

	public Planner getPlanner()
	{
		return planner;
	}

	/**Returns next agent from team.
	 * Only living agents are returned, since otherwise this would be Return of the Dead.
	 * If curAgent is null, return the first eligible agent.
	 * Returns null if there's no further eligible agent.
	 */
	public Agent getNextAgent(Agent curAgent)
	{
		boolean foundCurAgent = (curAgent==null);
		Agent newAgent = null;
		for (Agent agent : this.agents)
		{
			if (foundCurAgent && agent.isAlive())
			{
				newAgent = agent;
				break;			//No need to look for other agents
			}
			if (agent.equals(curAgent))
			{
				foundCurAgent = true;
			}
		}
		return newAgent;
	}

	public boolean hasNextAgent(Agent curAgent)
	{
		return (this.getNextAgent(curAgent)!=null);
	}

	public boolean hasWon()
	{
		return (getResourcesStillNeeded().size()==0);
	}

	public void win()
	{
		Simulation.sim.log(this + "has won.");
	}

	@Override
	public String toString()
	{
		return "Team #" + this.teamID;
	}
}
