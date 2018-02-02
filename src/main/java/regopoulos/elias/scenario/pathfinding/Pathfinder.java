package regopoulos.elias.scenario.pathfinding;

import javafx.geometry.Dimension2D;
import regopoulos.elias.scenario.Team;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/* Main pathfinding class.
 * Implements A* //TODO
 */
public class Pathfinder
{
	public static final Dimension2D NO_TILE_FOUND = new Dimension2D(-1,-1);	//used for returning values when nothing has been found

	Team lnkTeam;
	List<Dimension2D> closedSet, openSet;

	public Pathfinder(Team lnkTeam)
	{
		this.lnkTeam = lnkTeam;
		this.closedSet = new ArrayList<Dimension2D>();
		this.openSet = new ArrayList<Dimension2D>();
	}

	//Returns all traversible neighbours of tile @ y,x not already in closedSet
	public ArrayList<Dimension2D> getTraversibleNeighbours(int y, int x, boolean ignoreVisibility)
	{
		ArrayList<Dimension2D> neighbours = TileChecker.getNeighbours(y,x);
		neighbours = neighbours.stream().
				filter(TileChecker::locationIsInBounds).
				filter(nb -> TileChecker.locationIsTraversable(nb,lnkTeam,ignoreVisibility)).
//				filter(TileChecker::locationIsNotOccupied).
				filter(nb -> !closedSet.contains(nb)).
				collect(Collectors.toCollection(ArrayList::new));
		return neighbours;
	}
	//convenience method
	private ArrayList<Dimension2D> getTraversibleNeighbours(Dimension2D dim, boolean ignoreVisibility)
	{
		return getTraversibleNeighbours((int)dim.getHeight(), (int)dim.getWidth(), ignoreVisibility);
	}

	/* Only used for setting agent's initial position */
	public Dimension2D findNearestEmptyTile()
	{
		Dimension2D pos = Pathfinder.NO_TILE_FOUND;
		this.closedSet.clear();
		this.openSet.clear();
		this.openSet.addAll(lnkTeam.getDropOffSites());
		while (pos==Pathfinder.NO_TILE_FOUND)
		{
			pos = openSet.stream().
					filter(TileChecker::locationIsInBounds).
					filter(dim -> TileChecker.locationIsTraversable(dim, lnkTeam, true)).
					filter(TileChecker::locationIsNotOccupied).
					filter(dim -> !closedSet.contains(dim)).
					findAny().orElse(Pathfinder.NO_TILE_FOUND);

			//If whole openSet is already occupied, set openSet to empty neighbours
			do
			{
				ArrayList<Dimension2D> newOpenSet = new ArrayList<Dimension2D>();
				for (Dimension2D closedDim : openSet)
				{
					newOpenSet.addAll(getTraversibleNeighbours(closedDim,true));
				}
				closedSet.addAll(openSet);
				openSet.clear();
				openSet = newOpenSet;
			}while (!openSet.stream().
					filter(TileChecker::locationIsNotOccupied).findAny().isPresent());
		}
		openSet.clear();
		closedSet.clear();
		return pos;
	}

}
