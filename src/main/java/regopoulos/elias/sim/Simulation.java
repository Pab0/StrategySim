package regopoulos.elias.sim;

import regopoulos.elias.scenario.Map;
import regopoulos.elias.scenario.Scenario;
import regopoulos.elias.ui.SimulationUI;
import regopoulos.elias.ui.gui.SimWindow;

public class Simulation
{
	public static Simulation sim;
	private Scenario scenario;
	private SimulationUI simUI;
	private SimLoop simLoop;

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

		return (SimWindow)this.simUI;
	}

	public void setSimLoop(SimLoop simLoop)
	{
		this.simLoop = simLoop;
	}

	public SimLoop getSimLoop()
	{
		return simLoop;
	}
}
