package regopoulos.elias.sim;

import regopoulos.elias.scenario.Map;
import regopoulos.elias.scenario.Scenario;

public class Simulation
{
	public static Simulation sim;
	private Scenario scenario;

	public void setScenario(Scenario scenario)
	{
		this.scenario = scenario;
	}

	public Scenario getScenario()
	{
		return this.scenario;
	}
}
