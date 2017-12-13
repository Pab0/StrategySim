package regopoulos.elias.scenario;

import java.util.ArrayList;
import java.util.EnumMap;

class MapAnalyzer
{
	static ArrayList<TerrainType> getResourcesInMap(Map map)
	{
		ArrayList<TerrainType> resourcesInMap = new ArrayList<TerrainType>();
		EnumMap<TerrainType, Boolean> resourceContainedInMap = scanMap(map);
		for (TerrainType terrainType : resourceContainedInMap.keySet())
		{
			if (terrainType.isResource &&
				resourceContainedInMap.get(terrainType).booleanValue())
			{
				resourcesInMap.add(terrainType);
			}
		}
		return resourcesInMap;
	}

	static ArrayList<TerrainType> getTeamsInMap(Map map)
	{
		ArrayList<TerrainType> teamsInMap = new ArrayList<TerrainType>();
		EnumMap<TerrainType, Boolean> resourceContainedInMap = scanMap(map);
		for (TerrainType terrainType : resourceContainedInMap.keySet())
		{
			if (terrainType.isDropOffSite &&
					resourceContainedInMap.get(terrainType).booleanValue())
			{
				teamsInMap.add(terrainType);
			}
		}
		return teamsInMap;
	}

	/* Scans map's tiles and marks all terrain types it comes across as true */
	private static EnumMap<TerrainType, Boolean> scanMap(Map map)
	{
		EnumMap<TerrainType, Boolean> resourceContainedInMap = new EnumMap<TerrainType, Boolean>(TerrainType.class);
		for (Tile[] line : map.map)
		{
			for (Tile tile : line)
			{
				resourceContainedInMap.put(tile.getTerrainType(), true);
			}
		}
		return resourceContainedInMap;
	}
}
