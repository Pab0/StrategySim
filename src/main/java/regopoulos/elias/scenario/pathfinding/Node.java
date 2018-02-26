package regopoulos.elias.scenario.pathfinding;

import javafx.geometry.Dimension2D;
import regopoulos.elias.sim.Simulation;

import java.util.ArrayList;
import java.util.Objects;
import java.util.stream.Collectors;

/** Contains data used by the Pathfinder.
 * 	Each Node represents one Tile.
 */
public class Node
{
	static final Node NO_TILE_FOUND = new Node(-1,-1);	//used for returning values when nothing has been found

	int y,x;		//coordinates
	private Node parent;	//previous Node
	private int gCost;		//accumulated pathfinding cost
	private int hCost;		//estimated remaining cost to goal

	private Node(int y, int x)
	{
		this.y = y;
		this.x = x;
	}

	Node(Dimension2D dim)
	{
		this((int)dim.getHeight(), (int)dim.getWidth());
	}

	private Node(int y, int x, Node parent)
	{
		this(y,x);
		this.parent = parent;
	}

	/**Calculates form G and H costs.
	 * Should be called right after instantiation if Pathfinding is A*.
 	 * @param goal the pathfidning's goal.
	 */
	void calcCosts(Node goal)
	{
		int parentGCost = this.parent==null?0:this.parent.gCost;	//parent doesn't exist for root nodes
		this.gCost = parentGCost+1 + //each node is +1 removed from start compared to parent
				Simulation.sim.getScenario().getNodeWeightSetter().getNodeWeight(y,x);
		this.hCost = getHCost(goal);
	}

	private Node(Dimension2D dim, Node parent)
	{
		this((int)dim.getHeight(), (int)dim.getWidth(), parent);
	}

	public Dimension2D getCoords()
	{
		return new Dimension2D(x,y);
	}

	public int getY()
	{
		return y;
	}

	public int getX()
	{
		return this.x;
	}

	public ArrayList<Dimension2D> getPath()
	{
		ArrayList<Dimension2D> path = new ArrayList<Dimension2D>();
		Node curNode = this;
		path.add(curNode.getCoords());
		while (curNode.parent!=null)
		{
			curNode = curNode.parent;
			path.add(curNode.getCoords());
		}
		return path;
	}

	private int getHCost(Node goal)
	{
		return Math.abs(this.y-goal.y) + Math.abs(this.x-goal.x);
	}

	/**
	 * Depending on the pathfinding mode (sweeping vs pathfinding),
	 * gCost or gCost+hCost are returned, respectively.
	 *
	 * @param overallCost true for pathfinding, false for sweeping
	 * @return Overall cost.
	 */
	public int getFCost(boolean overallCost)
	{
		return overallCost?gCost:(gCost+hCost);
	}

	/**
	 * @return All cardinal neighbours of node.
	 */
	ArrayList<Node> getNeighbours()
	{
		return TileChecker.getNeighbours(y,x).
				stream().map(dim2D -> new Node(dim2D, this)).
				collect(Collectors.toCollection(ArrayList::new));
	}

	@Override
	public boolean equals(Object o)
	{
		if (o==this)
		{
			return true;
		}
		if (!(o instanceof  Node))
		{
			return false;
		}
		Node node = (Node) o;
		return this.y==node.y && this.x==node.x;
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(y,x);
	}

}
