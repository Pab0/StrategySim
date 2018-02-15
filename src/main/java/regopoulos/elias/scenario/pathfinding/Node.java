package regopoulos.elias.scenario.pathfinding;

import javafx.geometry.Dimension2D;

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
	int gCost;		//accumulated pathfinding cost
	int hCost;		//estimated cost to goal

	Node(int y, int x)
	{
		this.y = y;
		this.x = x;
	}

	Node(Dimension2D dim)
	{
		this((int)dim.getHeight(), (int)dim.getWidth());
	}

	Node(int y, int x, Node parent)
	{
		this(y,x);
		this.parent = parent;
		this.gCost = this.parent.gCost+1;
//		this.hCost = calcHCost();	//TODO
	}

	Node(Dimension2D dim, Node parent)
	{
		this((int)dim.getHeight(), (int)dim.getWidth(), parent);
	}

	public Dimension2D getCoords()
	{
		return new Dimension2D(x,y);
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


	/**Returns overall cost.
	 * Depending on the pathfinding mode (sweeping vs pathfinding),
	 * gCost or gCost+hCost are returned, respectively.
	 *
	 * @param overallCost true for pathfinding, false for sweeping
	 * @return
	 */
	int getFCost(boolean overallCost)
	{
		return overallCost?gCost:(gCost+hCost);
	}

	/**Returns all cardinal neighbours of node.
	 *
	 * @return
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
