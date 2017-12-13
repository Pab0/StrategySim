package regopoulos.elias.ui.gui;

import javafx.scene.input.KeyEvent;

public class InputHandler
{
	private LogOutput logOutput;

	InputHandler(LogOutput logOutput)
	{
		this.logOutput = logOutput;
	}

	void keyTyped(KeyEvent event)
	{
		System.out.println("Key typed: " + event.getCharacter());
		logOutput.log("Key typed: " + event.getCharacter());
	}
	void keyPressed(KeyEvent event)
	{
		System.out.println("Key pressed: " + event.getCharacter());
	}
	void keyReleased(KeyEvent event)
	{

	}
}