package regopoulos.elias.scenario;

public class Tile
{
	private TerrainType terrainType;
	private int resourceLeft;

	Tile(TerrainType terrainType)
	{
		this.terrainType = terrainType;
		this.resourceLeft = terrainType.capacity;
	}

	//If resource is depleted, set terrainType to grass
	protected void checkTerrain()
	{
		if (resourceLeft==0)
		{
			this.terrainType = TerrainType.GRASS;
		}
	}

	public TerrainType getTerrainType()
	{
		return this.terrainType;
	}
}
