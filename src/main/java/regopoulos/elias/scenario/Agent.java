package regopoulos.elias.scenario;

import javafx.geometry.Dimension2D;
import regopoulos.elias.sim.Simulation;

public class Agent
{
	private AgentType type;
	private Team lnkTeam;
	private int typeID;	//ID for respective type in team
	private String name;
	private TerrainType resouceCarrying;
	private boolean carriesResource;
	private int HP;
	private double speed;
	private int attack, defense;
	private double stepsToMake;		//accumulating steps according to speed

	public Dimension2D pos;	//agent's current position

	Agent(AgentType type, Team lnkTeam, int typeID)
	{
		this.type = type;
		this.lnkTeam = lnkTeam;
		this.typeID = typeID;
		this.name = this.type + " #" + this.typeID;
		this.HP = this.type.maxHP;
		this.speed = this.type.speed;
		this.attack = this.type.attack;
		this.defense = this.type.defense;
		this.carriesResource = false;
	}

	/* God agent, reserved for none other than Gaia itself */
	Agent(boolean isGaia)
	{
		this.name = "Agent of Gaia";
		//Nothing left to do here; this agent does not care about worldly attributes.
	}

	void setPos(int y, int x)
	{
		this.pos = new Dimension2D(x,y);
	}

	public void dropOffResource()
	{
		//TODO check if eligible, maybe do check elsewhere? -> Well, duh - make an Action class
		this.carriesResource = false;
		this.resouceCarrying = null;
	}

	public void gatherResource(TerrainType type)
	{
		//TODO check if resource, etc.
		this.carriesResource = true;
		this.resouceCarrying = type;
	}

	void initPosition(Map map)
	{
		Dimension2D dimension2D = getInitPosition(map);
		setPos((int)dimension2D.getHeight(), (int)dimension2D.getWidth());
	}

	private Dimension2D getInitPosition(Map map)
	{
		Dimension2D initialPosition = null;
		//Scanning rectangles gradually increasing in size until free tile is found
		for (int distance=1; distance<Math.min(map.getHeight(),map.getWidth()); distance++)
		{
			for (DropOffSite dropOffSite : map.getDropOffSites(this.lnkTeam))
			{
				Dimension2D dropOffSiteLoc = dropOffSite.getPosition();
				int y = (int)dropOffSiteLoc.getHeight();
				int x = (int)dropOffSiteLoc.getWidth();
				for (int i=-distance; i<distance+1; i++)
				{
					//Top
					if (this.canMoveTo(y-distance,x+i, true))
					{
						initialPosition = new Dimension2D(x+i,y-distance);
					}
					//Bottom
					else if (this.canMoveTo(y+distance,x+i,true))
					{
						initialPosition = new Dimension2D(x+i, y+distance);
					}
					//Left
					else if (this.canMoveTo(y+i,x-distance,true))
					{
						initialPosition = new Dimension2D(x-distance,y+i);
					}
					//Right
					else if (this.canMoveTo(y+i,x+distance,true))
					{
						initialPosition = new Dimension2D(x+distance,y+i);
					}
					if (initialPosition!=null)
					{
						return initialPosition;
					}
				}
			}
		}
		System.out.println("Nothing found");
		return null;	//No place found, may need to handle that more gracefully
	}

	private boolean canMoveTo(Dimension2D dimension2D)
	{
		int y = (int)dimension2D.getHeight();
		int x = (int)dimension2D.getWidth();
		return canMoveTo(y,x);
	}

	private boolean canMoveTo(int y, int x, boolean ignoreVisibility)
	{
		Map realMap = Simulation.sim.getScenario().getMap();
		return locationIsInBounds(y,x,realMap) &&
				locationIsTraversable(y,x,realMap,ignoreVisibility) &&
				locationIsNotOccupied(y,x);
	}
	/* Check if location is within bounds of map */
	private boolean locationIsInBounds(int y, int x, Map realMap)
	{
		boolean inBounds = (x>=0 &&
				x<realMap.getWidth() &&
				y>=0 &&
				y<realMap.getHeight());
		return inBounds;
	}

	/* Check if location is traversable */
	private boolean locationIsTraversable(int y, int x, Map realMap, boolean ignoreVisibility)
	{
		boolean traversable = ignoreVisibility ?
				realMap.getTileMap()[y][x].getTerrainType().traversable :
				lnkTeam.getTeamTile(y,x).getTerrainType().traversable;
		return traversable;
	}

	/* Check if location is not already occupied */
	private boolean locationIsNotOccupied(int y, int x)
	{
		boolean notOccupied = Simulation.sim.getScenario().getAgentAtPos(new Dimension2D(y,x))==null;
		return notOccupied;
	}


	/* Default behaviour is to respect map visibility; only ignore it on setup for initial positions */
	private boolean canMoveTo(int x, int y)
	{
		return canMoveTo(x,y,false);
	}

	public boolean isAlive()
	{
		return this.HP>0;
	}

	public void update()
	{
		//TODO
	}

	public String toString()
	{
		return this.name;
	}
}
