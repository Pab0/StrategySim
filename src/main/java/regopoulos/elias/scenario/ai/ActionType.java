package regopoulos.elias.scenario.ai;

import regopoulos.elias.scenario.TerrainType;

public enum ActionType
{
	EXPLORE		("Exploring", TerrainType.UNKNOWN, 3),
	GATHER_WOOD	("Chopping trees", TerrainType.TREE, 3),
	GATHER_STONE("Mining stone", TerrainType.STONE, 2),
	GATHER_GOLD	("Mining gold", TerrainType.GOLD, 2),
	ATTACK	("Attacking", 3),
	DROP_OFF("Dropping off", 2);

	String description;
	TerrainType terrainType;
	int amountToConsider;	//Closest X of ActionType to consider for each agent.

	ActionType(String description, int amountToConsider)
	{
		this.description = description;
		this.amountToConsider = amountToConsider;
	}

	ActionType(String description, TerrainType terrainType, int amountToConsider)
	{
		this(description, amountToConsider);
		this.terrainType = terrainType;
	}

	public boolean isGatheringAction()
	{
		return (this.terrainType!=null && this.terrainType.isResource());
	}

	public int getAmountToConsider()
	{
		return amountToConsider;
	}

	public TerrainType getTerrainType()
	{
		return this.terrainType;
	}
}
