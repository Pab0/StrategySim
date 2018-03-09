package regopoulos.elias.scenario;

import javafx.geometry.Dimension2D;
import regopoulos.elias.scenario.ai.Action;
import regopoulos.elias.scenario.ai.BadVisibilityException;
import regopoulos.elias.scenario.ai.State;
import regopoulos.elias.scenario.ai.WinterAI;
import regopoulos.elias.scenario.pathfinding.TileChecker;
import regopoulos.elias.sim.Simulation;

import java.util.ArrayList;
import java.util.HashMap;
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

	public Dimension2D pos;	//agent's current position
	private ArrayList<Action> possibleActions;	//list of actions to be examined, one of which will be chosen.
	private Action action;	//action to be carried out by agent
	private Agent killer;	//Killer of this agent - only set upon death
	private HashMap<Dimension2D, Integer> nodeRisks;
	private State state;

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
		this.state = new State(this);
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

	public int getHP()
	{
		return HP;
	}

	public TerrainType getResouceCarrying()
	{
		return resouceCarrying;
	}

	private void setPos(int y, int x)
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

	public void setAction(Action action)
	{
		this.action = action;
	}

	public Agent getKiller()
	{
		return killer;
	}

	public void setKiller(Agent killer)
	{
		this.killer = killer;
	}

	public void setNodeRisks(HashMap<Dimension2D, Integer> nodeRisks)
	{
		this.nodeRisks = nodeRisks;
	}

	public HashMap<Dimension2D, Integer> getNodeRisks()
	{
		return nodeRisks;
	}

	public State getState()
	{
		this.state.updateState();
		return state;
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
				e.printStackTrace();
			}
		}
		//sanity check, also used when setting the initial position
		lnkTeam.getVisibleMap()[(int)pos.getHeight()][(int)pos.getWidth()] = true;
	}

	private void moveOrDo() throws BadVisibilityException
	{
		boolean didAction = false;	//used for neural network reward
		if (action==null)	//No Zugzwang
		{

		}
		else if (this.action.getPath().size()>1)
		{
			move();
		}
		else if (this.action.getPath().size()==1)
		{
			doAction();
			didAction = true;
		}

		if (this.lnkTeam.getPlanner().usesNeuralNet())	//setting rewards and updating Q
		{
			WinterAI winterAI = (WinterAI)(this.lnkTeam.getPlanner());
			winterAI.getQLearning().updateQValue(this, this.action, didAction);
			winterAI.getQLearning().updateEpsilon(didAction);
		}
	}

	private void move()
	{
		ArrayList<Dimension2D> path = this.action.getPath();
		setPos(path.get(0));
		this.action.getPath().remove(0);
		Simulation.sim.log("Moved to [" + (int)this.pos.getHeight() + "," + (int)this.pos.getWidth() + "]");
	}

	private void doAction() throws BadVisibilityException
	{
		this.action.doAction(this);
		Simulation.sim.log("Did " + this.action);
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
			enemyAgent.setKiller(this);
			enemyAgent.die();
		}
		this.getAction().setEnemyAgent(enemyAgent);
	}

	public void dropOffResource()
	{
		this.lnkTeam.getResources().get(this.resouceCarrying).dropOff();
		this.getAction().setResourceDroppedOff(this.resouceCarrying);
		this.carriesResource = false;
		this.resouceCarrying = null;
	}

	public void stayPut()
	{
		//Doesn't act or move
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
		//Update Q value, applying death penalty (called "reward" for consistency and marketing purposes)
		if (this.lnkTeam.getPlanner().usesNeuralNet())
		{
			WinterAI winterAI = (WinterAI)(this.lnkTeam.getPlanner());
			winterAI.getQLearning().updateQValue(this, this.action, false);
		}

		this.lnkTeam.getAgents().remove(this);
		Simulation.sim.getScenario().getPositionsWithAgents().remove(pos);
		this.setPos(-1,-1);
		Simulation.sim.log("Requiescat in pace, " + this);
	}

	public void update() throws BadVisibilityException
	{
		lookAround();
		//Determine best possible action
		this.action = lnkTeam.getPlanner().getNextAction(this);
		Simulation.sim.log(this + " chose action " + action);
		//Ask Pathfinder for closest reachable goal, and path towards it
		//move on path, or carry out action (can't do both in same round)
		moveOrDo();
	}

	public String toString()
	{
		return this.name;
	}
}
