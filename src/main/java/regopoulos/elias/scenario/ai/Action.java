package regopoulos.elias.scenario.ai;

import javafx.geometry.Dimension2D;
import regopoulos.elias.scenario.Agent;
import regopoulos.elias.scenario.TerrainType;
import regopoulos.elias.scenario.pathfinding.Node;
import regopoulos.elias.sim.Simulation;

import java.util.ArrayList;
import java.util.Collections;


public class Action
{
	private Node node;			//location of Point of Interest on map
	ActionType type;
	boolean selectedRandomly;		//whether epsilon-greedy chose it randomly among actions
	private TerrainType resourceDroppedOff;	//last dropped off resource - only used for rewarding
	private Agent enemyAgent;				//last attacked enemy agent - only used for rewarding
	ArrayList<Dimension2D> path;	//calculated path from agent to goal
	private int pathCost;			//Weighted cost of path

	/* Since there's a 1:1 relation between ActionType and TerrainType,
	 * we can infer the former based solely on the latter.
	 *
	 * One exception in this relation is on setting the initial positions,
	 * which is treated as an ATTACK since it's on GRASS,
	 * but this doesn't affect the outcome.
	 */
	public Action(Node node, TerrainType terrainType)
	{
		setNode(node);
		switch(terrainType)
		{
			case UNKNOWN:
				this.type = ActionType.EXPLORE;
				break;
			case TREE:
				this.type = ActionType.GATHER_WOOD;
				break;
			case STONE:
				this.type = ActionType.GATHER_STONE;
				break;
			case GOLD:
				this.type = ActionType.GATHER_GOLD;
				break;
			case GRASS:
				this.type = ActionType.ATTACK;
				break;
			case TEAM1:
			case TEAM2:
			case TEAM3:
			case TEAM4:
				this.type = ActionType.DROP_OFF;
				break;
		}
	}

	public Dimension2D getPoI()
	{
		return node.getCoords();
	}

	public Node getNode()
	{
		return node;
	}

	public void setNode(Node node)
	{
		this.node = node;
		this.setPath(node.getPath());
	}

	public ArrayList<Dimension2D> getPath()
	{
		return path;
	}

	/** Paths are sent in reversed, since they backtrack from goal (inclusive) to agent.
	 * This method fixes the order and removes the agent's position.
	 * @param path
	 */
	public void setPath(ArrayList<Dimension2D> path)
	{
		this.path = path;
		Collections.reverse(this.path);	//path order was initially from goal to agent
		this.path.remove(0);	//first element is agent's position
	}

	public void setPathCost()
	{
		if (this.node.getParent()!=null)
		{
			this.pathCost = this.node.getParent().getGCost();	//Gets cost of path from agent to goal node (exclusive)
		}
		else
		{
			this.pathCost = 0;
		}
	}

	public int getPathCost()
	{
		return this.pathCost;
	}

	public ActionType getType()
	{
		return type;
	}

	public void setResourceDroppedOff(TerrainType terrainType)
	{
		this.resourceDroppedOff = terrainType;
	}

	public TerrainType getResourceDroppedOff()
	{
		return resourceDroppedOff;
	}

	public void setEnemyAgent(Agent enemyAgent)
	{
		this.enemyAgent = enemyAgent;
	}

	public Agent getEnemyAgent()
	{
		return enemyAgent;
	}

	void setSelectedRandomly(boolean selectedRandomly)
	{
		this.selectedRandomly = selectedRandomly;
	}

	public boolean getSelectedRandomly()
	{
		return this.selectedRandomly;
	}

	public void doAction(Agent lnkAgent) throws BadVisibilityException
	{
		switch (this.getType())
		{
			case ATTACK:
				lnkAgent.attack(Simulation.sim.getScenario().getAgentAtPos(this.node.getCoords()));
				break;
			case DROP_OFF:
				lnkAgent.dropOffResource();
				break;
			case GATHER_WOOD:
			case GATHER_STONE:
			case GATHER_GOLD:
				lnkAgent.gatherResource(Simulation.sim.getScenario().getMap().getTileAt(this.node.getY(), this.node.getX()));
				break;
			case EXPLORE:
				//adjacent tiles should always be visible,
				//and thus never be candidates for exploration
				throw new BadVisibilityException();
			default:
				lnkAgent.stayPut();
				break;
		}
	}

	/** @return a shorter version of its toString() method.
	 * Useful when screen estate is limited. */
	public String shortHand()
	{
		return this.type.getShortHand() + "@" + this.node.getY() + "," + this.node.getX();
	}

	@Override
	public String toString()
	{
		return this.type.description + " @ [" + this.node.getY() + "," + this.node.getX() + "]";
	}
}
