package regopoulos.elias.scenario;

import javafx.geometry.Dimension2D;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Map
{
	private String fileName;
	private int width, height;
	Tile[][] map;
	private Tile[][] originalMap; //Used for preserving initial map intact across multiple scenario runs.
	private ArrayList<TerrainType> resourcesInMap;
	private ArrayList<TerrainType> teamsInMap;
	private ArrayList<DropOffSite> dropOffSites;

	public Map(File file)
	{
		this.fileName = file.getName();
		this.map = MapBuilder.getTileMap(file);
		this.width = this.map[0].length;
		this.height = this.map.length;
		this.originalMap = new Tile[this.height][this.width];
		for (int i=0; i<this.height; i++)
		{
			for (int j=0; j<this.width; j++)
			{
				this.originalMap[i][j] = this.map[i][j].copy();
			}
		}
	}

	public Map(int height, int width)
	{
		this.height = height;
		this.width = width;
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

	public TerrainType getTerrainAt(Dimension2D dim)
	{
		return this.map[(int)dim.getHeight()][(int)dim.getWidth()].getTerrainType();
	}

	public TerrainType getTerrainAt(int y, int x)
	{
		return this.map[y][x].getTerrainType();
	}

	public Tile getTileAt(int y, int x)
	{
		return this.map[y][x];
	}

	public ArrayList<TerrainType> getTeamsInMap()
	{
		return teamsInMap;
	}

	public void setDropOffSites()
	{
		this.dropOffSites = MapAnalyzer.getDropOffSites(this);
	}

	List<DropOffSite> getDropOffSites()
	{
		return dropOffSites;
	}

	/**Gets dropOffSites of team */
	List<DropOffSite> getDropOffSites(Team team)
	{
		getDropOffSites();
		return getDropOffSites().stream().filter(dropOffSite -> dropOffSite.getLnkTeam().equals(team)).collect(Collectors.toList());
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

	public String getFileName()
	{
		return fileName;
	}

	/**Returns a clone of this map.
	 * Used for preserving initial map intact across multiple scenario runs.
	 * @return
	 */
	public Map clone()
	{
		Map map = new Map(this.height, this.width);
		map.map = new Tile[this.height][this.width];
		for (int i=0; i<this.height; i++)
		{
			for (int j=0; j<this.width; j++)
			{
				map.map[i][j] = this.originalMap[i][j].copy();
			}
		}
		map.originalMap = this.originalMap;
		map.dropOffSites = this.dropOffSites;
		map.fileName = this.fileName;
		return map;
	}

}
