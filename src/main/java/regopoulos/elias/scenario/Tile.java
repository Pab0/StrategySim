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
	private void checkTerrain()
	{
		if (resourceLeft==0)
		{
			this.terrainType = TerrainType.GRASS;
		}
	}

	/**Somebody gathers the resource of the tile.
	 * Decreases the resource left by 1.
	 */
	public void gatherResource()
	{
		this.resourceLeft--;
		checkTerrain();
	}

	public TerrainType getTerrainType()
	{
		return this.terrainType;
	}
}
