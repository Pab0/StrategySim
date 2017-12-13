package regopoulos.elias.ui.gui;

import javafx.geometry.Orientation;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.ToolBar;

public class MenuBar extends ToolBar implements Updateable
{
	private Button newBtn, loadBtn, saveBtn;
	protected MenuBar(int width, int height)
	{
		createButtons();

		this.setPrefSize(width,height);
		//TODO add icons to labels (As resources)
		this.setOrientation(Orientation.HORIZONTAL);
		this.getItems().add(newBtn);
		this.getItems().add(loadBtn);
		this.getItems().add(saveBtn);
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

	private void createButtons()
	{
		newBtn = new Button("New");
		newBtn.setOnAction(e -> new OptionsStage());

		loadBtn = new Button("Load"	);
		saveBtn = new Button("Save"	);

	}

	@Override
	public void update()
	{
		//TODO speed & resources
	}
}
