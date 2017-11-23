package regopoulos.elias;

import regopoulos.elias.sim.Simulation;
import regopoulos.elias.ui.SimWindow;


public class StartSim
{
	private static boolean render = true;

	private static SimWindow simWindow;

	public static void main(String[] args)
	{
		Simulation sim = new Simulation();
		if (render)
		{
			simWindow = new SimWindow();
			simWindow.startWindow(args);
		}
	}
}
