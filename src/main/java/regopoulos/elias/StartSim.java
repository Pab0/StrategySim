package regopoulos.elias;

import regopoulos.elias.sim.Simulation;
import regopoulos.elias.ui.clui.CLUI;
import regopoulos.elias.ui.gui.SimWindow;
import regopoulos.elias.ui.SimulationUI;


public class StartSim
{
	private static boolean render = true;

	public static SimulationUI simUI;

	public static void main(String[] args)
	{
		Simulation sim = new Simulation();
		Simulation.sim = sim;
		simUI = render ? new SimWindow() : new CLUI();
		simUI.start(args);
	}
}
