package regopoulos.elias.ui.gui;

import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;

public class LogBar extends VBox implements Updateable, LogOutput
{
	TextArea logArea;

	protected LogBar(int height)
	{
		logArea = new TextArea();
		logArea.setEditable(false);
		logArea.setFocusTraversable(false);
		logArea.setPrefHeight(height);
		logArea.appendText("Foo0\n");
		logArea.appendText("Foo1\n");
		logArea.appendText("Foo2\n");
		logArea.appendText("Foo3\n");
		logArea.appendText("Foo4\n");
		logArea.appendText("Foo5\n");
		logArea.appendText("Foo6\n");
		logArea.appendText("Foo7\n");
		logArea.appendText("Foo8\n");
		logArea.appendText("Foo9\n");
		logArea.appendText("Foo10\n");
		logArea.appendText("Foo11\n");
		logArea.appendText("Foo12\n");
		logArea.appendText("Foo13\n");
		this.getChildren().add(logArea);
	}

	@Override
	public void update()
	{

	}

	@Override
	public void log(String logStr)
	{
		logArea.appendText(logStr + "\n");
	}
}
