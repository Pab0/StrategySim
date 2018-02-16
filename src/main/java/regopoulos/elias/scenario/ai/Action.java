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
	Dimension2D poI;	//location of Point of Interest on map
	ActionType type;
	ArrayList<Dimension2D> path; //calculated path from agent to goal

	/* Since there's a 1:1 relation between ActionType and TerrainType,
	 * we can infer the former based solely on the latter.
	 *
	 * One exception in this relation is on setting the initial positions,
	 * which is treated as an ATTACK since it's on GRASS,
	 * but this doesn't affect the outcome.
	 */
	public Action(Node node, TerrainType terrainType)
	{
		this.poI = node.getCoords();
		this.path = node.getPath();
		Collections.reverse(this.path);	//path order was initially from goal to agent
		this.path.remove(0);	//first element is agent's position
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
		//TODO: Path should already be set by pathfinder - actually no, proper pathfinding must still happen
	}

	public Dimension2D getPoI()
	{
		return poI;
	}

	public ArrayList<Dimension2D> getPath()
	{
		return path;
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
				lnkAgent.attack(Simulation.sim.getScenario().getAgentAtPos(this.poI));
				break;
			case DROP_OFF:
				lnkAgent.dropOffResource();
				break;
			case GATHER_WOOD:
			case GATHER_STONE:
			case GATHER_GOLD:
				lnkAgent.gatherResource(Simulation.sim.getScenario().getMap().getTileAt(this.poI));
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
		return this.type.description + " @ " + (int)this.poI.getWidth() + "," + (int)this.poI.getHeight();
	}
}
