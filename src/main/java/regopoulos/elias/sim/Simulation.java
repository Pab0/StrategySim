package regopoulos.elias.sim;

import regopoulos.elias.log.Logger;
import regopoulos.elias.scenario.Scenario;
import regopoulos.elias.scenario.ai.NetStorage;
import regopoulos.elias.scenario.ai.QLearning;
import regopoulos.elias.scenario.ai.Reward;
import regopoulos.elias.ui.SimulationUI;
//import regopoulos.elias.ui.gui.SimWindow;

import java.io.InputStream;
import java.util.Properties;

public class Simulation
{
	public static Simulation sim;
	private Scenario scenario;
	private SimulationUI simUI;
	private Logger logger;
	private SimLoop simLoop;
	private int scenarioRunNumber;	//how many times should the scenario run before the simulation finishes?

	public Simulation()
	{
		loadProperties();
		Reward.loadProperties();
		QLearning.loadProperties();
	}

	private void loadProperties()
	{
		Properties prop = new Properties();
		try (InputStream fis = Simulation.class.getClassLoader().getResourceAsStream("SimulationLoop.properties"))
		{
			prop.loadFromXML(fis);
			this.scenarioRunNumber = Integer.parseInt(prop.getProperty("ScenarioRunAmount"));
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public void setScenario(Scenario scenario)
	{
		this.scenario = scenario;
	}

	public Scenario getScenario()
	{
		return this.scenario;
	}

	public void setSimUI(SimulationUI simUI)
	{
		this.simUI = simUI;
	}

	public SimulationUI getSimUI()
	{
		return this.simUI;
	}

	public void setSimLoop(SimLoop simLoop)
	{
		this.simLoop = simLoop;
	}

	public SimLoop getSimLoop()
	{
		return simLoop;
	}

	public int getScenarioRunNumber()
	{
		return scenarioRunNumber;
	}

	public void setLogger(Logger logger)
	{
		this.logger = logger;
	}

	public Logger getLogger()
	{
		return logger;
	}

	public void log(String logStr)
	{
		this.logger.log(logStr);
	}

	boolean hasFinished()
	{
		return this.scenario.getRunCount()>=this.scenarioRunNumber;
	}

	void finish()
	{
		this.getSimLoop().finish();
		Simulation.sim.log("Simulation finished");
		NetStorage.lockAway();
		this.logger.stop();
	}
}
