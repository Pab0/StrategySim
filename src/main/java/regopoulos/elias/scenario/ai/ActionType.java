package regopoulos.elias.scenario.ai;

import regopoulos.elias.scenario.TerrainType;

public enum ActionType
{
	EXPLORE		("Exploring", "XPLR", TerrainType.UNKNOWN, 3),
	GATHER_WOOD	("Cutting tree", "WOOD", TerrainType.TREE, 3),
	GATHER_STONE("Mining stone", "STNE", TerrainType.STONE, 2),
	GATHER_GOLD	("Mining gold", "GOLD", TerrainType.GOLD, 2),
	ATTACK	("Attacking", "ATTK", 3),
	DROP_OFF("Dropping off", "DROP", 2);

	String description;
	String shortHand;
	TerrainType terrainType;
	int amountToConsider;	//Closest X of ActionType to consider for each agent.

	ActionType(String description, String shortHand, int amountToConsider)
	{
		this.description = description;
		this.shortHand = shortHand;
		this.amountToConsider = amountToConsider;
	}

	ActionType(String description, String shortHand, TerrainType terrainType, int amountToConsider)
	{
		this(description, shortHand, amountToConsider);
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

	public String getShortHand()
	{
		return this.shortHand;
	}
}
