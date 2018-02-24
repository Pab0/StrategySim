package regopoulos.elias.ui.gui;

import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import regopoulos.elias.log.LogOutput;

public class LogBar extends VBox implements Updateable, LogOutput
{
	TextArea logArea;

	LogBar(int height)
	{
		logArea = new TextArea();
		logArea.setEditable(false);
		logArea.setFocusTraversable(false);
		logArea.setPrefHeight(height);
		this.getChildren().add(logArea);
	}

	@Override
	public void update()
	{

	}

	@Override
	public void initOnSimLoad()
	{

	}

	@Override
	public void log(String logStr)
	{
		logArea.appendText(logStr + "\n");
	}
}
