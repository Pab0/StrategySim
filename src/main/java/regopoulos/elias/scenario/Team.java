package regopoulos.elias.scenario;

import javafx.geometry.Dimension2D;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.EnumMap;

public class Team implements MapViewTeam
{
	private static final short HUE_DISTANCE = 60;	//Determines each team's color's Hue
	private static final double SATURATION = 1;	//Saturation and Brightness stay the same
	private static final double BRIGHTNESS = 1;	//at full value

	public Color color;
	private boolean[][] visibleMap;
	protected Tile[] dropOffSite;
	private Agent[] agents;
	private int teamID;

	public Team(TerrainType terrainType)
	{
		this.teamID = Character.getNumericValue(terrainType.glyph);
		this.color = Color.hsb((HUE_DISTANCE*teamID)%256,SATURATION,BRIGHTNESS);
	}

	public void initVisibleMap(Dimension2D mapDim)
	{
		//TODO based on location of DropOffSite and Agents
		this.visibleMap = new boolean[(int)mapDim.getHeight()][(int)mapDim.getWidth()];
	}

	public void setDropOffSite(Tile[] dropOffSite)
	{
		this.dropOffSite = dropOffSite;
	}

	public void setAgents(EnumMap<AgentType, Integer> agentNum)
	{
		ArrayList<Agent> agentList = new ArrayList<>();
		for (AgentType type : agentNum.keySet())
		{
			for (int i=0; i<agentNum.get(type); i++)
			{
				agentList.add(new Agent(type));
			}
		}
		agentList.trimToSize();
		this.agents = new Agent[agentList.size()];
	}

	@Override
	public boolean[][] getVisibleMap()
	{
		//TODO
		return null;
	}
}
