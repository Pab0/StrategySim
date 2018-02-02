package regopoulos.elias.scenario.pathfinding;

import javafx.geometry.Dimension2D;
import regopoulos.elias.scenario.Map;
import regopoulos.elias.scenario.Team;
import regopoulos.elias.sim.Simulation;
import java.util.ArrayList;
import java.util.stream.Collectors;

/* Performs various checks on tiles, needed for pathfinding
 * Utility class; static methods throughout.
 */
public class TileChecker
{
	/* Check if location is within bounds of map */
	public static boolean locationIsInBounds(int y, int x)
	{
		Map realMap = Simulation.sim.getScenario().getMap();
		boolean inBounds = (x>=0 &&
				x<realMap.getWidth() &&
				y>=0 &&
				y<realMap.getHeight());
//		System.out.println(y + "," + x + " is in bounds: " + inBounds);
		return inBounds;
	}
	//convenience method
	public static boolean locationIsInBounds(Dimension2D dim)
	{
		return TileChecker.locationIsInBounds((int)dim.getHeight(), (int)dim.getWidth());
	}

	/* Check if location is traversable */
	public static boolean locationIsTraversable(int y, int x, Team lnkTeam, boolean ignoreVisibility)
	{
		Map realMap = Simulation.sim.getScenario().getMap();
		boolean traversable = ignoreVisibility ?
				realMap.getTileMap()[y][x].getTerrainType().traversable :
				lnkTeam.getTeamTile(y,x).getTerrainType().traversable;
//		System.out.println(y + "," + x + " is traversable: " + traversable);
		return traversable;
	}
	//convenience method
	public static boolean locationIsTraversable(Dimension2D dim, Team lnkTeam, boolean ignoreVisibility)
	{
		return TileChecker.locationIsTraversable((int)dim.getHeight(), (int)dim.getWidth(), lnkTeam, ignoreVisibility);
	}

	/* Check if location is not already occupied */
	public static boolean locationIsNotOccupied(int y, int x)
	{
		boolean notOccupied = Simulation.sim.getScenario().getAgentAtPos(new Dimension2D(x,y))==null;
//		System.out.println(y + "," + x + " is not occupied: " + notOccupied);
		return notOccupied;
	}
	//convenience method
	public static boolean locationIsNotOccupied(Dimension2D dim)
	{
		return TileChecker.locationIsNotOccupied((int)dim.getHeight(), (int)dim.getWidth());
	}

	/* Returns all four cardinal direction's neighbours of tile y,x if within bounds */
	public static ArrayList<Dimension2D> getNeighbours(int y, int x)
	{
		ArrayList<Dimension2D> neighbours = new ArrayList<Dimension2D>();
		neighbours.add(new Dimension2D(x,y-1));
		neighbours.add(new Dimension2D(x,y+1));
		neighbours.add(new Dimension2D(x-1,y));
		neighbours.add(new Dimension2D(x+1,y));
		neighbours.removeIf(nb -> !TileChecker.locationIsInBounds(nb));
		return neighbours;
	}

}