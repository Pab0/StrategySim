package regopoulos.elias.scenario;

import javafx.geometry.Dimension2D;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Map
{
	private int width, height;
	Tile[][] map;
	private ArrayList<TerrainType> resourcesInMap;
	private ArrayList<TerrainType> teamsInMap;
	private ArrayList<DropOffSite> dropOffSites;

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

	public TerrainType getTerrainAt(Dimension2D dim)
	{
		return this.map[(int)dim.getHeight()][(int)dim.getWidth()].getTerrainType();
	}

	public TerrainType getTerrainAt(int y, int x)
	{
		return this.map[y][x].getTerrainType();
	}

	public Tile getTileAt(Dimension2D dim)
	{
		return this.map[(int)dim.getHeight()][(int)dim.getWidth()];
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
}
