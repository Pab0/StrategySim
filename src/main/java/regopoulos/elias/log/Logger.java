package regopoulos.elias.log;

import regopoulos.elias.scenario.*;
import regopoulos.elias.sim.Simulation;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;

public class Logger implements LogOutput
{
	private static final String LOG_ROOT = "logs";	//directory where logs should be kept

	private BufferedWriter fullLogWriter, statsLogWriter;

	private Simulation lnkSim;

	@Override
	public void log(String logStr)
	{
		System.out.println(logStr);
		try
		{
			this.fullLogWriter.write(logStr + "\n");
		}
		catch (IOException ioe)
		{
			ioe.printStackTrace();
		}
	}

	public void logStats()
	{
		if (lnkSim==null)	//this should only be called after simulation and scenario have been established
		{
			return;
		}
		Scenario scenario = lnkSim.getScenario();
		StringBuilder sb = new StringBuilder();
		sb.append("\nScenario No " + lnkSim.getScenario().getRunCount() + ":\n");

		//Winner
		sb.append("Winner: " + Arrays.stream(scenario.getTeams()).filter(Team::hasWon).findAny().get() + "\n");

		//Statistics follow
		sb.append("Scenario statistics: \n");

		//Agents
		sb.append("Surviving agents: \n");
		for (Team team : scenario.getTeams())
		{
			sb.append("\t" + team + ":");
			for (AgentType type : AgentType.values())
			{
				sb.append(type + ":");
				sb.append(team.getAgents().stream().
						filter(agent -> agent.getType()==type).count() + " ");
			}
			sb.append('\n');
		}

		//Resources
		sb.append("Gathered Resources: \n");
		for (Team team : scenario.getTeams())
		{
			sb.append("\t" + team + ": ");
			for (Resource resource : team.getResources().values())
			{
				TerrainType type = resource.getType();
				sb.append(type + ":" + team.getResource(type).getCurrent()+" ");
			}
			sb.append('\n');
		}

		Simulation.sim.getSimUI().log(sb.toString());

		try
		{
			this.statsLogWriter.write(sb.toString());
		}
		catch (IOException ioe)
		{
			ioe.printStackTrace();
		}
	}

	public void start()
	{
		this.lnkSim = Simulation.sim;
		new File(Logger.LOG_ROOT).mkdir();
		String fullLogFilename = genFilename(true);
		String statsLogFilename = genFilename(false);
		try
		{
			BufferedWriter bw1 = new BufferedWriter(new FileWriter(fullLogFilename, true));
			BufferedWriter bw2 = new BufferedWriter(new FileWriter(statsLogFilename, true));
			this.fullLogWriter = bw1;
			this.statsLogWriter = bw2;
			logHeaders();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	/**Writes general info about the Simulation on th top of the log file */
	private void logHeaders() throws IOException
	{
		Scenario scenario = lnkSim.getScenario();
		StringBuilder sb = new StringBuilder();

		//Simulation info
		sb.append("Number of scenario simulations: " + lnkSim.getScenarioRunNumber() + "\n");

		//Map info
		Map map = scenario.getMap();
		sb.append("Selected map: " + map.getFileName() + ". ");
		sb.append("Height:" + map.getHeight() + ", Width:" + map.getWidth());
		sb.append('\n');

		//Resource info
		ScenarioOptions options = scenario.getScenarioOptions();
		sb.append("Resources to gather: ");
		sb.append(options.getResourceGoals());
		sb.append('\n');

		//Team info
		sb.append("Team planners: \n");
		for (TerrainType terrainType : options.getPlanners().keySet())
		{
			sb.append("\t" + scenario.getTeamByID(terrainType) + ": ");
			sb.append(options.getPlanners().get(terrainType).getPlannerName());
			sb.append('\n');
		}

		//Agent info
		sb.append("Agents in each team: " + options.getAgentNum());
		sb.append('\n');

		sb.append('\n');

		allLog(sb.toString());
	}

	/** Logs to both files concurrently */
	private void allLog(String str) throws IOException
	{
		this.fullLogWriter.write(str);
		this.statsLogWriter.write(str);
	}

	public void stop()
	{
		try
		{
			this.fullLogWriter.close();
			this.statsLogWriter.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}


	private static String genFilename(boolean fullLog)
	{
		String filename = LOG_ROOT + "/";
		filename += new Date().toString();
		filename += fullLog?".log":".stats.log";
		return filename;
	}
}
