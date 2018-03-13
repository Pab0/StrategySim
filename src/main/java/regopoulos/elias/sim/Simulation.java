package regopoulos.elias.sim;

import regopoulos.elias.log.Logger;
import regopoulos.elias.scenario.Scenario;
import regopoulos.elias.scenario.ai.NetStorage;
import regopoulos.elias.ui.SimulationUI;
import regopoulos.elias.ui.gui.SimWindow;

public class Simulation
{
	private static final int DEFAULT_SCENARIO_RUN_NUMBER = 5;	//TODO set this to something higher
	public static Simulation sim;
	private Scenario scenario;
	private SimulationUI simUI;
	private Logger logger;
	private SimLoop simLoop;
	private int scenarioRunNumber;	//how many times should the scenario run before the simulation finishes?

	public Simulation()
	{
		this.scenarioRunNumber = DEFAULT_SCENARIO_RUN_NUMBER;
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
