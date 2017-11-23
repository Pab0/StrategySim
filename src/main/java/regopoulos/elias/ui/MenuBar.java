package regopoulos.elias.ui;

import javafx.geometry.Orientation;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.ToolBar;

public class MenuBar extends ToolBar implements Updateable
{
	protected MenuBar(int width, int height)
	{
		this.setPrefSize(width,height);
		//TODO add icons to labels (As resources)
		this.setOrientation(Orientation.HORIZONTAL);
		this.getItems().add(new Button("Open"));
		this.getItems().add(new Button("Load"));
		this.getItems().add(new Button("Save"));
		this.getItems().add(new Button("Options"));
		this.getItems().add(new Separator());
		this.getItems().add(new Label("Round X,"));
		this.getItems().add(new Label("Team X,"));
		this.getItems().add(new Label("Agent X,"));
		this.getItems().add(new Separator());
		this.getItems().add(new Label("Speed:"));
		this.getItems().add(new Button("Play/Pause"));
		this.getItems().add(new Button("Faster"));
		this.getItems().add(new Button("Slower"));
		this.getItems().add(new Separator());
		this.getItems().add(new Label("Steps:"));
		this.getItems().add(new Button("Next agent"));	//alternative way of playing the sim,
		this.getItems().add(new Button("Next team"));	//only if paused
		this.getItems().add(new Button("Next round"));
		this.getItems().add(new Separator());

	}

	@Override
	public void update()
	{
		//TODO speed & resources
	}
}
