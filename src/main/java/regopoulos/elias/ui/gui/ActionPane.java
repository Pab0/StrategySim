package regopoulos.elias.ui.gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Separator;
import javafx.scene.layout.VBox;
import regopoulos.elias.scenario.Agent;
import regopoulos.elias.scenario.ai.Action;
import regopoulos.elias.sim.Simulation;

public class ActionPane extends VBox implements Updateable
{
	ListView<Action> actionView;

	protected ActionPane(int width)
	{
		this.setSpacing(10);
		this.setPadding(new Insets(20,5,20,5));

		this.getChildren().add(new Label("Possible Actions:"));
		actionView = new ListView<Action>();
		actionView.setPrefHeight(200);
		this.getChildren().add(actionView);
		this.setPrefWidth(width);

		this.getChildren().add(new Separator());

		this.getChildren().add(new Label("Neural Network goes here"));
	}

	@Override
	public void update()
	{
		Agent selAgent = Simulation.sim.getSimUI().getSelectedAgent();
		if (selAgent.getPossibleActions()==null || selAgent.getPossibleActions().isEmpty())
		{
			actionView.setItems(FXCollections.observableArrayList());
		}
		else
		{
			actionView.setItems(FXCollections.observableArrayList(
					Simulation.sim.getSimUI().getSelectedAgent().getPossibleActions()));

		}
	}

	@Override
	public void initOnSimLoad()
	{

	}
}
