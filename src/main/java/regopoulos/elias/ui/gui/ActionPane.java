package regopoulos.elias.ui.gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Separator;
import javafx.scene.layout.VBox;

public class ActionPane extends VBox implements Updateable
{
	protected ActionPane(int width)
	{
		this.setSpacing(10);
		this.setPadding(new Insets(20,5,20,5));
		this.setPrefWidth(width);

		//TODO: Change to <PoI> and <Actions>
		this.getChildren().add(new Label("Points of Interest:"));
		ObservableList<String> poi = FXCollections.observableArrayList(
				"PoI 1", "PoI 2", "PoI 3", "PoI 4", "PoI 5", "PoI 6");
		ListView<String> poiView = new ListView<String>(poi);
		poiView.setPrefHeight(100);
		this.getChildren().add(poiView);

		this.getChildren().add(new Separator());

		this.getChildren().add(new Label("Neural Network goes here"));

		this.getChildren().add(new Separator());

		this.getChildren().add(new Label("Actions:"));
		ObservableList<String> actions = FXCollections.observableArrayList(
				"Action 1", "Action 2", "Action 3");
		ListView<String> actionView = new ListView<String>(actions);
		actionView.setPrefHeight(100);
		this.getChildren().add(actionView);
	}

	@Override
	public void update()
	{

	}

	@Override
	public void initOnSimLoad()
	{

	}
}
