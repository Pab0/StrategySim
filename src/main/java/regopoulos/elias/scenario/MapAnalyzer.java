package regopoulos.elias.scenario;


import javafx.geometry.Dimension2D;
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

	/**Scans map's tiles and marks all terrain types it comes across as true */
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

	static ArrayList<DropOffSite> getDropOffSites(Map map)
	{
		ArrayList<DropOffSite> dropOffSitesList = new ArrayList<DropOffSite>();
		for (int i=0; i<map.getHeight(); i++)
		{
			for (int j=0; j<map.getWidth(); j++)
			{
				TerrainType type = map.map[i][j].getTerrainType();
				if (type.isDropOffSite)
				{
					dropOffSitesList.add(new DropOffSite(type, new Dimension2D(j,i)));
				}
			}
		}
		return dropOffSitesList;
	}
}
