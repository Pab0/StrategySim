package regopoulos.elias.scenario;

import javafx.geometry.Dimension2D;

import java.util.ArrayList;
import java.util.EnumMap;

public interface MapViewTeam
{
	boolean[][] getVisibleMap();	//Fog of war for teams, clear for Gaia

	void setVisibleMap(Dimension2D mapDimension);	//instantiates the visible map

	ArrayList<Agent> getAgents();

	EnumMap<TerrainType,Resource> getResources();

	Resource getResource(TerrainType resourceType);
}
