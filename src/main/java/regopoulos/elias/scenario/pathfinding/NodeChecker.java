package regopoulos.elias.scenario.pathfinding;

import regopoulos.elias.scenario.Team;
import regopoulos.elias.sim.Simulation;

public class NodeChecker
{
	public static boolean nodeIsTraversable(Node node, boolean[][] visibleMap)
	{
		boolean isTraversable = false;
		if (!visibleMap[node.y][node.x])	//UKNOWN is assumed to be traversable until proven otherwise
		{
			isTraversable = true;
		}
		else if (Simulation.sim.getScenario().getMap().getTerrainAt(node.y, node.x).traversable)
		{
			isTraversable = true;
		}
		return isTraversable;
	}

	public static boolean isNotOccupied(Node node, boolean[][] visibleMap)
	{
		boolean isNotOccupied = false;
		if (!visibleMap[node.y][node.x])
		{
			isNotOccupied = true;
		}
		else if (Simulation.sim.getScenario().getAgentAtPos(node.y, node.x)==null)
		{
			isNotOccupied = true;
		}
		return isNotOccupied;
	}
}
