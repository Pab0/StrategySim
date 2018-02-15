package regopoulos.elias.scenario;

import javafx.geometry.Dimension2D;

import java.util.ArrayList;
import java.util.EnumMap;

/*
 * Gaia is the omnipresent and omniscient team, Nature.
 */
public class GaiaTeam implements MapViewTeam
{
	private static final String NAME = "Gaia";
	private boolean[][] visibleMap;
	private ArrayList<Agent> agents;

	public GaiaTeam()
	{
		this.agents = new ArrayList<Agent>();
		agents.add(new Agent(true));
	}

	@Override
	public boolean[][] getVisibleMap()
	{
		return this.visibleMap;
	}

	@Override
	public void setVisibleMap(Dimension2D mapDimension)
	{
		this.visibleMap = new boolean[(int)mapDimension.getHeight()][(int)mapDimension.getWidth()];
		for (int i=0; i<mapDimension.getHeight(); i++)
		{
			for (int j=0; j<mapDimension.getWidth(); j++)
			{
				this.visibleMap[i][j] = true;	//Gaia sees all, for Gaia is the World itself.
			}
		}
	}

	@Override
	public ArrayList<Agent> getAgents()
	{
		return agents;
	}

	@Override
	public EnumMap<TerrainType, Resource> getResources()
	{
		return null;
	}

	@Override
	public Resource getResource(TerrainType resourceType)
	{
		return null;
	}

	@Override
	public String toString()
	{
		return GaiaTeam.NAME;
	}
}
