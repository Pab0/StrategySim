package regopoulos.elias.scenario.pathfinding;

import regopoulos.elias.scenario.Team;
import regopoulos.elias.scenario.TerrainType;
import regopoulos.elias.scenario.ai.Action;
import regopoulos.elias.scenario.ai.ActionType;
import regopoulos.elias.sim.Simulation;

import java.util.ArrayList;
import java.util.HashMap;

/**Contains the goals the Pathfinder has to find a path to.
 * Each goal is an Action.
 * These can be TerrainTypes (objects), or enemy agent positions.
 */
public class PathfinderGoals
{
	private HashMap<TerrainType, Integer> objectsToFind;			//amount of Actions belonging to TerrainType to find
	private HashMap<TerrainType, ArrayList<Action>> objectsFound;	//contains `x` nearest Actions of `TerrainType`
	private int	enemiesToFind;										//amount of enemies to find
	private ArrayList<Action> enemiesFound;							//contains `x` nearest enemy Agents

	public PathfinderGoals()
	{
		this.objectsToFind = new HashMap<TerrainType, Integer>();
		this.objectsFound = new HashMap<TerrainType, ArrayList<Action>>();
		this.enemiesFound = new ArrayList<Action>();
	}

	public void addGoal(ActionType type, Team lnkTeam)
	{
		switch (type)
		{
			case ATTACK:
				addEnemiesToFind(type.getAmountToConsider());
				break;
			case DROP_OFF:
				addObjectsToFind(lnkTeam.getTerrainType(), type.getAmountToConsider());
				break;
			default:
				addObjectsToFind(type.getTerrainType(), type.getAmountToConsider());
				break;
		}
	}

	void addObjectsToFind(TerrainType type, int amount)
	{
		this.objectsToFind.put(type, amount);
		this.objectsFound.put(type, new ArrayList<Action>());
	}

	private void addEnemiesToFind(int amount)
	{
		this.enemiesToFind = amount;
	}

	void checkIfGoal(Node node, Team lnkTeam, boolean[][] visibleMap)
	{
		TerrainType type = Simulation.sim.getScenario().getMap().getTerrainAt(node.y, node.x);
		if (!visibleMap[node.y][node.x])	//Exploring the unknown
		{
			foundObjectGoal(TerrainType.UNKNOWN, node);
		}
		else if (type.equals(TerrainType.GRASS) &&														//The only interesting thing about grass are
				Simulation.sim.getScenario().getAgentAtPos(node.y, node.x)!=null &&						//the agents on it -
				!Simulation.sim.getScenario().getAgentAtPos(node.y, node.x).getTeam().equals(lnkTeam))	//enemy agents, to be precise
		{
			foundEnemy(node);
		}
		else
		{
			for (TerrainType terrainType : objectsToFind.keySet())
			{
				if (type.equals(terrainType))
				{
					foundObjectGoal(terrainType, node);
				}
			}
		}
	}

	private void foundObjectGoal(TerrainType type, Node node)
	{
		if (this.objectsFound.get(type).size()<this.objectsToFind.get(type))
		{
			Action action = new Action(node, type);
			this.objectsFound.get(type).add(action);
		}
	}

	private void foundEnemy(Node node)
	{
		if (this.enemiesFound.size()<this.enemiesToFind)
		{
			this.enemiesFound.add(new Action(node, TerrainType.GRASS));
		}
	}

	boolean isFinished()
	{
		int missingObjects = 0;
		for (TerrainType terrainType : objectsToFind.keySet())
		{
			missingObjects += objectsToFind.get(terrainType).intValue() - objectsFound.get(terrainType).size();
		}
		int missingEnemies = enemiesToFind - enemiesFound.size();
		return (missingObjects+missingEnemies<=0);
	}

	HashMap<TerrainType, ArrayList<Action>> getObjectsFound()
	{
		return objectsFound;
	}

	public ArrayList<Action> getEnemiesFound()
	{
		return enemiesFound;
	}

	public ArrayList<Action> toArrayList()
	{
		ArrayList<Action> actions = new ArrayList<Action>();
		for (ArrayList<Action> actionList : this.objectsFound.values())
		{
			actions.addAll(actionList);
		}
		actions.addAll(this.enemiesFound);
		return actions;
	}
}
