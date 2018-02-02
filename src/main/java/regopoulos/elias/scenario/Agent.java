package regopoulos.elias.scenario;

import javafx.geometry.Dimension2D;
import regopoulos.elias.scenario.pathfinding.TileChecker;
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

	public AgentType getType()
	{
		return type;
	}

	void setPos(int y, int x)
	{
		this.pos = new Dimension2D(x,y);
		System.out.println("Set " + this + " to tile " + pos);
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

	void initPosition()
	{
		Dimension2D dimension2D = getInitPosition();
		setPos((int)dimension2D.getHeight(), (int)dimension2D.getWidth());
	}

	private Dimension2D getInitPosition()
	{
		return lnkTeam.getPathfinder().findNearestEmptyTile();
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
		return TileChecker.locationIsInBounds(y,x) &&
				TileChecker.locationIsTraversable(y,x,lnkTeam,ignoreVisibility) &&
				TileChecker.locationIsNotOccupied(y,x);
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
