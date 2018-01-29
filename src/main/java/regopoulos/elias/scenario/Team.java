package regopoulos.elias.scenario;

import javafx.geometry.Dimension2D;
import javafx.scene.paint.Color;
import regopoulos.elias.sim.Simulation;

import java.util.ArrayList;
import java.util.EnumMap;

public class Team implements MapViewTeam
{
	private static final short HUE_DISTANCE = 60;	//Determines each team's color's Hue
	private static final double SATURATION = 1;	//Saturation and Brightness stay the same
	private static final double BRIGHTNESS = 1;	//at full value

	private Color color;
	private boolean[][] visibleMap;		//mask of visible portion of scenario's map
	private Agent[] agents;
	private int teamID;
	private EnumMap<TerrainType,Resource> resources;
	private boolean finishedGathering;

	public Team(TerrainType terrainType)
	{
		this.teamID = Character.getNumericValue(terrainType.glyph);
		this.color = Color.hsb((HUE_DISTANCE*teamID)%256,SATURATION,BRIGHTNESS);
	}

	public int getTeamID()
	{
		return teamID;
	}

	public void setVisibleMap(Dimension2D mapDim)
	{
		//TODO based on location of DropOffSite and Agents
		this.visibleMap = new boolean[(int)mapDim.getHeight()][(int)mapDim.getWidth()];
	}

	public void getDropOffSites()
	{
		//TODO return only this team's dropOffSites from Map
	}

	public void setAgents(EnumMap<AgentType, Integer> agentNum)
	{
		ArrayList<Agent> agentList = new ArrayList<Agent>();
		for (AgentType type : agentNum.keySet())
		{
			for (int i=0; i<agentNum.get(type); i++)
			{
				Agent agent = new Agent(type, this, i);
				agent.initPosition(Simulation.sim.getScenario().getMap());
				agentList.add(agent);
			}
		}
		this.agents = agentList.toArray(new Agent[agentList.size()]);
	}

	public Agent[] getAgents()
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

	@Override
	public boolean[][] getVisibleMap()
	{
		return this.visibleMap;
	}

	/* Returns tile as seen by team, aka true tile or unknown */
	protected Tile getTeamTile(int y, int x)
	{
		Tile tile = new Tile(TerrainType.UNKNOWN);
		if (this.visibleMap[y][x])
		{
			tile = Simulation.sim.getScenario().getMap().map[y][x];
		}
		return tile;
	}

	/* Returns next agent from team.
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
		return finishedGathering;
	}

	@Override
	public String toString()
	{
		return "Team #" + this.teamID;
	}
}
