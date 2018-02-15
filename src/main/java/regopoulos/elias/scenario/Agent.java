package regopoulos.elias.scenario;

import javafx.geometry.Dimension2D;
import regopoulos.elias.scenario.ai.Action;
import regopoulos.elias.scenario.ai.ActionType;
import regopoulos.elias.scenario.ai.BadVisibilityException;
import regopoulos.elias.scenario.pathfinding.Node;
import regopoulos.elias.scenario.pathfinding.TileChecker;
import regopoulos.elias.sim.Simulation;

import java.util.ArrayList;
import java.util.List;

public class Agent
{
	private AgentType type;
	private Team lnkTeam;
	private int typeID;	//ID for respective type in team
	private String name;
	private TerrainType resouceCarrying;
	private boolean carriesResource;
	private int HP;
	private int attack, defense;
	private double stepsToMake;		//accumulating steps according to speed

	public Dimension2D pos;	//agent's current position
	private ArrayList<Action> possibleActions;	//list of actions to be examined, one of which will be chosen.
	private Action action;	//action to be carried out by agent

	Agent(AgentType type, Team lnkTeam, int typeID)
	{
		this.type = type;
		this.lnkTeam = lnkTeam;
		this.typeID = typeID;
		this.name = this.type + " #" + this.typeID;
		this.HP = this.type.maxHP;
		this.attack = this.type.attack;
		this.defense = this.type.defense;
		this.carriesResource = false;
	}

	/**God agent, reserved for none other than Gaia itself */
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
		Simulation.sim.getScenario().getPositionsWithAgents().remove(pos);
		this.pos = new Dimension2D(x,y);
		Simulation.sim.getScenario().getPositionsWithAgents().put(pos, this);
		try
		{
			lookAround();
		}
		catch (ArrayIndexOutOfBoundsException e)
		{
			//agent died, has gone to [-1,-1]
		}
		System.out.println("Set " + this + " to tile " + pos);
	}

	void setPos(Dimension2D dim)
	{
		this.setPos((int)dim.getHeight(), (int)dim.getWidth());
	}

	public Team getTeam()
	{
		return lnkTeam;
	}

	public void setPossibleActions(ArrayList<Action> possibleActions)
	{
		this.possibleActions = possibleActions;
	}

	public ArrayList<Action> getPossibleActions()
	{
		return possibleActions;
	}

	public Action getAction()
	{
		return action;
	}

	/**Adds neighbouring tiles to team's visible map */
	private void lookAround()
	{
		List<Dimension2D> neighbours = TileChecker.getNeighbours(this.pos);
		for (Dimension2D dim : neighbours)
		{
			try
			{
				lnkTeam.getVisibleMap()[(int)dim.getHeight()][(int)dim.getWidth()] = true;
			}
			catch (Exception e)
			{
				System.out.println("Oopsie daisy");
			}
		}
		//sanity check, also used when setting the initial position
		lnkTeam.getVisibleMap()[(int)pos.getHeight()][(int)pos.getWidth()] = true;
	}

	private void moveOrDo() throws BadVisibilityException
	{
		if (action==null)	//No Zugzwang
		{
			return;
		}
		if (this.action.getPath().size()>1)
		{
			move();
		}
		else if (this.action.getPath().size()==1)
		{
			doAction();
		}
	}

	private void move()
	{
		ArrayList<Dimension2D> path = this.action.getPath();
		setPos(path.get(0));
		this.action.getPath().remove(0);
		System.out.println("Moved to " + this.pos);
	}

	private void doAction() throws BadVisibilityException
	{
		this.action.doAction(this);
		System.out.println("Did " + this.action);
	}

	public boolean isCarryingResource()
	{
		return this.carriesResource;
	}

	public void attack(Agent enemyAgent)
	{
		double damage = Math.max(0, this.attack-enemyAgent.defense);	//compiler denied me the golden opportunity of writing `double damage *=2`
		enemyAgent.HP -= damage;
		if (!enemyAgent.isAlive())
		{
			enemyAgent.die();
		}
	}

	public void dropOffResource()
	{
		this.lnkTeam.getResources().get(this.resouceCarrying).dropOff();
		this.carriesResource = false;
		this.resouceCarrying = null;
	}

	public void gatherResource(Tile lnkTile)
	{
		this.resouceCarrying = lnkTile.getTerrainType();
		this.carriesResource = true;
		lnkTile.gatherResource();
	}

	public boolean isAlive()
	{
		return this.HP>0;
	}

	private void die()	//the "void" type feels oddly fitting for this mortal function
	{
		this.HP = 0;
		this.setPos(-1,-1);
		Simulation.sim.getScenario().getPositionsWithAgents().remove(pos);
		this.lnkTeam.getAgents().remove(this);
		System.out.println("Requiescat in pace, " + this);
	}

	public void update() throws BadVisibilityException
	{
		lookAround();
		//Plan new Action
		this.action = lnkTeam.getPlanner().getNextAction(this);
		System.out.println(this + " chose action " + action);
		//Ask Pathfinder for closest adjacent goal, and path towards it
		//move on path, or carry out action (can't do both in same round)
		moveOrDo();
	}

	public String toString()
	{
		return this.name;
	}
}
