package regopoulos.elias.ui.gui;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import regopoulos.elias.sim.Simulation;

public class InputHandler
{
	private static final KeyCode MOVE_UP 	= KeyCode.S;
	private static final KeyCode MOVE_DOWN 	= KeyCode.W;
	private static final KeyCode MOVE_LEFT 	= KeyCode.D;
	private static final KeyCode MOVE_RIGHT	= KeyCode.A;

	private LogOutput logOutput;

	InputHandler(LogOutput logOutput)
	{
		this.logOutput = logOutput;
	}

	void keyTyped(KeyEvent event)
	{
		//Everything is covered by keyPressed/Released
	}

	void keyPressed(KeyEvent event)
	{
		keyPressedReleased(event.getCode(),true);
	}

	void keyReleased(KeyEvent event)
	{
		keyPressedReleased(event.getCode(),false);
	}

	/**Called on key pressed or released, to avoid code duplication.
	 * The `pressed` boolean determines whether the key has been pressed or released.
	 */
	private void keyPressedReleased(KeyCode keyCode, boolean pressed)
	{
		if (Simulation.sim.getScenario()==null)
		{
			return;	//Scenario, and thus Camera, might not have been instantiated
		}
		SimWindow sw = (SimWindow)Simulation.sim.getSimUI();
		Camera camera = sw.getCamera();
		if (keyCode==MOVE_UP)
		{
			camera.setMovingUp(pressed);
		}
		if (keyCode==MOVE_DOWN)
		{
			camera.setMovingDown(pressed);
		}
		if (keyCode==MOVE_LEFT)
		{
			camera.setMovingLeft(pressed);
		}
		if (keyCode==MOVE_RIGHT)
		{
			camera.setMovingRight(pressed);
		}
	}
}