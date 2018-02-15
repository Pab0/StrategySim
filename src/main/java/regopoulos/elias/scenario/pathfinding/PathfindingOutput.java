package regopoulos.elias.scenario.pathfinding;

import regopoulos.elias.scenario.Map;
import regopoulos.elias.sim.Simulation;

import java.util.ArrayList;
import java.util.List;

/**Outputs pathfinding info.
 * Only to be used for debugging.
 */
public class PathfindingOutput
{
	static void printOpenSet(List<Node> openSet)
	{
		System.out.println("OpenSet:");
		Map map = Simulation.sim.getScenario().getMap();
		char[][] openSetChar = new char[map.getHeight()][map.getWidth()];
		for (int i=0; i<openSetChar.length; i++)
		{
			for (int j=0; j<openSetChar[0].length; j++)
			{
				openSetChar[i][j] = '.';
			}
		}
		for (Node node : openSet)
		{
			openSetChar[node.y][node.x] = 'T';
		}
		for (int i=0; i<openSetChar.length; i++)
		{
			for (int j=0; j<openSetChar[0].length; j++)
			{
				System.out.print(openSetChar[i][j]);
			}
			System.out.println();
		}
	}
}
