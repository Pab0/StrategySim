package regopoulos.elias.scenario;

import regopoulos.elias.sim.Simulation;

/*
 *Resource of a single type gathered by a single team
 */
public class Resource
{
	final TerrainType type;
	final int goal;
	int current;

	Resource(TerrainType type)
	{
		this.type = type;
		this.current = 0;
		this.goal = Simulation.sim.getScenario().getResourceGoal(type);
	}

	public int getGoal()
	{
		return goal;
	}

	public int getCurrent()
	{
		return current;
	}

	public TerrainType getType()
	{
		return type;
	}

	/**Agent drops off resource at team's dropOffSite.
	 * Adds +1 to the resource's count.
	 */
	public void dropOff()
	{
		this.current++;
	}
}
