package regopoulos.elias.ui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.VBox;

public class TeamPane extends VBox implements Updateable
{
	protected TeamPane(int width)
	{
		this.setSpacing(10);
		this.setPadding(new Insets(20,5,20,5));
		this.setPrefWidth(width);

		this.getChildren().add(new Label("Team:"));
		ObservableList<String> teams =
				FXCollections.observableArrayList(
						"Gaia",
						"Team 1",
						"Team 2",
						"Team 3......................"
				);
		final ComboBox teamBox = new ComboBox(teams);
		this.getChildren().add(teamBox);

		this.getChildren().add(new Label("Res1: X/10"));
		this.getChildren().add(new Label("Res2: X/10"));
		this.getChildren().add(new Label("Res3: X/10"));

		this.getChildren().add(new Separator());

		this.getChildren().add(new Label("Team's Agents:"));
		ObservableList<String> teamAgents =
				FXCollections.observableArrayList(
						"Agent 1",
						"Agent 2",
						"Agent 3"
				);
		final ComboBox agentBox = new ComboBox(teamAgents);
		this.getChildren().add(agentBox);

		this.getChildren().add(new Separator());

		this.getChildren().add(new Label("Agent Status:"));
		this.getChildren().add(new Label("Status goes here"));

	}

	@Override
	public void update()
	{

	}
}