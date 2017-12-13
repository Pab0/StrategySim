package regopoulos.elias.scenario;

import java.io.File;
import java.util.ArrayList;

/* Holds two layers of map:
	1. The terrain layer
	2. The team/agent layer

 */
public class Map
{
	int width, height;
	Tile[][] map;
	private ArrayList<TerrainType> resourcesInMap;
	private ArrayList<TerrainType> teamsInMap;

	public Map(File file)
	{
		this.map = MapBuilder.getTileMap(file);
		this.width = this.map[0].length;
		this.height = this.map.length;
	}

	public void analyzeMap()
	{
		this.resourcesInMap = MapAnalyzer.getResourcesInMap(this);
		this.teamsInMap = MapAnalyzer.getTeamsInMap(this);
	}

	public ArrayList<TerrainType> getResourcesInMap()
	{
		return resourcesInMap;
	}

	public ArrayList<TerrainType> getTeamsInMap()
	{
		return teamsInMap;
	}

	public Tile[][] getTileMap()
	{
		return this.map;
	}

	public int getWidth()
	{
		return this.width;
	}

	public int getHeight()
	{
		return this.height;
	}
}
