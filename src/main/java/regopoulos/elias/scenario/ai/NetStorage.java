package regopoulos.elias.scenario.ai;

import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import regopoulos.elias.scenario.TerrainType;

import java.util.HashMap;

/** Stores and retrieves networks across simulations, based on Team's TerrainType (aka number) */
public class NetStorage
{
	private static HashMap<TerrainType,MultiLayerNetwork> teamNets;	//stores nets used on previous rounds


	/**Gets called on new Scenario start */
	public static void init()
	{
		teamNets = new HashMap<TerrainType,MultiLayerNetwork>();
	}

	public static MultiLayerNetwork getNet(TerrainType teamType)
	{
		if (teamNets==null)
		{
			init();
		}
		return teamNets.get(teamType);
	}

	static void storeNet(MultiLayerNetwork net, TerrainType teamType)
	{
		if (teamNets==null)
		{
			init();
		}
		teamNets.put(teamType,net);
	}
}
