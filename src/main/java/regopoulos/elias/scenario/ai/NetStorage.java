package regopoulos.elias.scenario.ai;

import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.util.ModelSerializer;
import regopoulos.elias.scenario.ScenarioOptions;
import regopoulos.elias.scenario.Team;
import regopoulos.elias.scenario.TerrainType;
import regopoulos.elias.sim.Simulation;

import java.io.File;
import java.io.IOException;
import java.util.EnumMap;

/** Stores and retrieves networks across simulations, based on Team's TerrainType (aka number) */
public class NetStorage
{
	public static final String NET_DIRECTORY = "nets/";
	private static final short HASH_CHAR_NUMBER = 3;	// # of chars to be used for creating the filename

	private static EnumMap<TerrainType, String> netsToLoad;	//stores filenames of nets loaded
	private static EnumMap<TerrainType,MultiLayerNetwork> teamNets;	//stores nets used on previous rounds

	/**Gets called on new Scenario start */
	public static void init()
	{
		if (teamNets==null)	//only load the nets on first run
		{
			ScenarioOptions options = Simulation.sim.getScenario().getScenarioOptions();
			teamNets = options.getTeamNets();
			netsToLoad = options.getNetsToLoad();
		}
	}

	public static MultiLayerNetwork getNet(TerrainType teamType)
	{
		if (teamNets==null)
		{
			init();
		}
		return teamNets.get(teamType);
	}

	/** Stores the net in the static EnumMap teamNets  for later retrieval */
	static void storeNet(MultiLayerNetwork net, TerrainType teamType)
	{
		if (teamNets==null)
		{
			init();
		}
		teamNets.put(teamType,net);
	}

	/** Saves nets to disks and resets nets held in storage */
	public static void lockAway()
	{
		saveNets();
		netsToLoad = new EnumMap<TerrainType, String>(TerrainType.class);
		teamNets = new EnumMap<TerrainType, MultiLayerNetwork>(TerrainType.class);
	}

	public static void saveNets()
	{
		for (Team team : Simulation.sim.getScenario().getTeams())
		{
			if (team.getPlanner().usesNeuralNet())
			{
				saveNet(team.getTerrainType());
			}
		}
	}

	/** Saves the net of team teamType to disk */
	private static void saveNet(TerrainType teamType)
	{
		String filename = generateFilename(teamType);
		MultiLayerNetwork net = teamNets.get(teamType);
		File locationToSave = new File(NET_DIRECTORY + filename);
		try
		{
			ModelSerializer.writeModel(net, locationToSave, true);
		}
		catch (IOException ioe)
		{
			System.out.println("Could not write net to disk.");
			ioe.printStackTrace();
		}
		Simulation.sim.log("Saved net of team " + teamType.getGlyph() + " to " + filename);
	}

	/** Generates String to save the net as, according to following formula:
	 * filename = previousFileName (sans "zip") +
	 * 	first HASH_CHAR_NUMBER of (map+numberOfRounds) +
	 * 	first char of AI name +
	 * 	TerrainType of team +
	 * 	".zip" .
	 */
	private static String generateFilename(TerrainType teamType)
	{
		String mapDescriptor = "" + Simulation.sim.getScenario().getMap().hashCode() + "," + Simulation.sim.getScenarioRunNumber();
		mapDescriptor = ("" + mapDescriptor.hashCode()).substring(0,NetStorage.HASH_CHAR_NUMBER);
		String oldNetFilename = "";
		if (netsToLoad.get(teamType)!=null)
		{
			oldNetFilename = netsToLoad.get(teamType).replaceAll("zip$", "");
		}

		String filename = "";
		filename += oldNetFilename;
		filename +=	mapDescriptor;
		filename += Simulation.sim.getScenario().getTeamByID(teamType).getPlanner().getPlannerName().substring(0,1);
		filename += teamType.getGlyph();
		filename += ".zip";
		return filename;
	}
}
