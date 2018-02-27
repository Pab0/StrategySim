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
		this.pathCost = this.node.getParent().getGCost();	//Gets cost of path from agent to goal node (exclusive)
	}

	public int getPathCost()
	{
		return this.pathCost;
	}

	public ActionType getType()
	{
		return type;
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

	@Override
	public String toString()
	{
		return this.type.description + " @ " + this.node.getX() + "," + this.node.getY();
	}
}
