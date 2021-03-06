package regopoulos.elias.scenario.pathfinding;

import javafx.geometry.Dimension2D;
import regopoulos.elias.scenario.Agent;
import regopoulos.elias.scenario.NotEnoughTilesFoundException;
import regopoulos.elias.scenario.Team;
import regopoulos.elias.scenario.TerrainType;
import regopoulos.elias.scenario.ai.Action;
import regopoulos.elias.sim.Simulation;

import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

/**Main pathfinding class.
 * Implements A*.
 * Two modi of operation:
 * a) Finds path from A to B
 * b) Sweeps area from point A until XYZ objects are found, as indicated in the PathfinderGoals object.
 * [b] is done so that all objects can be found in a single sweep.
 */
public class Pathfinder
{
	private static int MAX_CLOSED_SET;	//max size of closed set, before giving up searching for goals

	private Set<Node> openSet,closedSet;
	private PathfinderGoals goals;

	public Pathfinder()
	{
		Pathfinder.loadProperties();
		this.closedSet = new HashSet<>();
		this.openSet = new HashSet<>();
		this.goals = new PathfinderGoals();
	}

	private static void loadProperties()
	{
		Properties prop = new Properties();
		try (InputStream fis = Pathfinder.class.getClassLoader().getResourceAsStream("Pathfinding.properties"))
		{
			prop.loadFromXML(fis);
			Pathfinder.MAX_CLOSED_SET = Integer.parseInt(prop.getProperty("MaxGoalSearchingArea"));
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	private void setPathfinderGoals(PathfinderGoals pathfinderGoals)
	{
		this.goals = pathfinderGoals;
	}

	/**
	 * @return All possible Actions for this agent
	 */
	public PathfinderGoals getPossibleActions(PathfinderGoals pathfinderGoals, Agent lnkAgent)
	{
		this.closedSet.clear();
		this.openSet.clear();
		this.setPathfinderGoals(pathfinderGoals);
		Node rootNode = new Node(lnkAgent.pos);
		this.openSet.add(rootNode);	//adding agent's current position to openSet
		try
		{
			sweepForGoals(lnkAgent.getTeam());
		}
		catch (NotEnoughTilesFoundException e)
		{
//			System.out.println("Not enough tiles found");
		}
		return this.goals;
	}

	/**A* implementation.
	 * Finds shortest path from agent to goal,
	 * using weighted nodes according to risk
	 *
	 * @param action The goal pathing to.
	 */
	public ArrayList<Dimension2D> getWeightedPathToGoal(Agent lnkAgent, Action action)
	{
		this.closedSet.clear();
		this.openSet.clear();
		Node rootNode = new Node(lnkAgent.pos);
		Node goalNode = action.getNode();
		rootNode.calcCosts(goalNode);
		this.openSet.add(rootNode);						//adding agent's current position to openSet
		this.openSet.addAll(rootNode.getNeighbours());	//also adding all immediate neighbours
		for (Node node : this.openSet)
		{
			node.calcCosts(goalNode);
		}

		ArrayList<Node> neighbours = new ArrayList<>();
		boolean isFinished = false;
		Node closestNode;
		while (!isFinished)
		{
			//Checking if goal is reached
			closestNode = openSet.stream().
					min(Comparator.comparingInt(Node::getFCost)).get();

			if (closestNode.equals(goalNode))
			{
				action.setNode(closestNode);
				isFinished = true;
			}
			else
			{
				//Move examined node to closedSet
				this.openSet.remove(closestNode);
				this.closedSet.add(closestNode);

				//Add closestNode's neighbours to openSet, if closestNode is eligible for neighbours
				if (NodeChecker.nodeIsTraversable(closestNode, lnkAgent.getTeam().getVisibleMap()) &&
							NodeChecker.isNotOccupied(closestNode, lnkAgent.getTeam().getVisibleMap()))
				{
					neighbours = closestNode.getNeighbours();
				}

				for (Node neighbour : neighbours)
				{
					neighbour.calcCosts(goalNode);
				}
				openSet.addAll(neighbours.stream().distinct().
						filter(node -> !openSet.contains(node)).
						filter(node -> !closedSet.contains(node)).
						collect(Collectors.toList()));
			}
		}
		action.setPathCost();
		return action.getNode().getPath();
	}

	/** Only used for setting agen't initial position
	 * @param lnkTeam - the team whose agents are being set
	 * @param amount - number of agents
	 * @return non-occupied Grass tiles closest to lnkTeam's DropOffSites
	 */
	public ArrayList<Action> findNearestEmptyTiles(Team lnkTeam, int amount) throws NotEnoughTilesFoundException
	{
		this.closedSet.clear();
		this.openSet.clear();
		this.openSet.addAll(lnkTeam.getDropOffSites().	//Setting openSet to team's dropOffsites
				stream().map(Node::new).collect(Collectors.toList()));
		PathfinderGoals pfg = new PathfinderGoals();
		pfg.addObjectsToFind(TerrainType.GRASS, amount);
		setPathfinderGoals(pfg);
		sweepForGoals(Simulation.sim.getScenario().getGaia().getVisibleMap(), lnkTeam, true);
		return this.goals.getObjectsFound().get(TerrainType.GRASS);
	}

	/**Find goals, as set by PathfinderGoals.
	 * Populates PathfinderGoals with locations (which are then turned into Actions).
	 * @param visibleMap passes the team's visibilityMap which should be used while scanning.
	 *                   Pass Gaia's map for full visibility (such as when initiating positions).
	 * @param lnkTeam	Used for telling apart friend and foe.
	 * @param ignoreOccupants Whether the sweep should treat occupied tiles as obstacles everywhere,
	 *                         or only in the final destination (ignoring path-blocking agents).
	 *                        Used for setting the initial positions.
	 */
	private void sweepForGoals(boolean[][] visibleMap, Team lnkTeam, boolean ignoreOccupants) throws NotEnoughTilesFoundException
	{
		this.openSet.addAll(openSet.stream().findFirst().get().getNeighbours());
		while (!this.goals.isFinished())
		{
			if (openSet.isEmpty())	//no tiles on map
			{
				throw new NotEnoughTilesFoundException();
			}
			if (closedSet.size()>MAX_CLOSED_SET) //no tiles (relatively) nearby
			{
				throw new NotEnoughTilesFoundException();
			}

			//Checking tiles
			for (Node node : openSet)
			{
				this.goals.checkIfGoal(node, lnkTeam, visibleMap);
				if (this.goals.isFinished())	//only loop as long as there are unsatisfied goals
				{
					break;
				}
			}

			//Adding elligible neighbouring tiles for next sweep
			ArrayList<Node> neighbours = new ArrayList<>();	//basically the (superset of the) new openSet
			closedSet.addAll(openSet);
			openSet = openSet.stream().
					filter(node -> NodeChecker.nodeIsTraversable(node, visibleMap)).
					collect(Collectors.toCollection(HashSet::new));
			if (!ignoreOccupants)
			{
				openSet.removeIf(node -> !NodeChecker.isNotOccupied(node, visibleMap));
			}
			for (Node node : openSet)
			{
				neighbours.addAll(node.getNeighbours());
			}

			openSet.clear();
			openSet = neighbours.stream().
					distinct().
					filter(node -> !closedSet.contains(node)).
					collect(Collectors.toCollection(HashSet::new));
		}
	}

	/**Convenience method.
	 * Default is to not ignore occupied tiles (they're only ignored when setting initial positions).
	 * @param lnkTeam	Gives team to determine own/enemy agents, and provides visibility map.
	 */
	private void sweepForGoals(Team lnkTeam) throws NotEnoughTilesFoundException
	{
		sweepForGoals(lnkTeam.getVisibleMap(), lnkTeam, false);
	}
}
