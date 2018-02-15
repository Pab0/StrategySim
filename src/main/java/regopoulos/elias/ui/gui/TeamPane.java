package regopoulos.elias.ui.gui;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.VBox;
import regopoulos.elias.scenario.Agent;
import regopoulos.elias.scenario.MapViewTeam;
import regopoulos.elias.scenario.Resource;
import regopoulos.elias.scenario.Team;
import regopoulos.elias.sim.Simulation;

public class TeamPane extends VBox implements Updateable
{
	private MapViewTeam[] mapViewTeams;
	private ComboBox teamBox;
	private ComboBox agentBox;
	private Label resourceLabel;
	private Label plannerLabel;

	TeamPane(int width)
	{
		this.setSpacing(10);
		this.setPadding(new Insets(20,5,20,5));
		this.setPrefWidth(width);

		this.getChildren().add(new Label("Team:"));
		this.teamBox = new ComboBox();
		this.getChildren().add(teamBox);

		this.getChildren().add(new Label("Resources:"));
		this.resourceLabel = new Label();
		this.getChildren().add(resourceLabel);

		this.getChildren().add(new Separator());

		this.getChildren().add(new Label("Team's Agents:"));
		this.agentBox = new ComboBox();
		this.getChildren().add(agentBox);

		this.getChildren().add(new Separator());

		this.getChildren().add(new Label("Agent Status:"));
		this.plannerLabel = new Label();
		this.getChildren().add(plannerLabel);

	}

	@Override
	public void update()
	{
		updateResourceLabel();
		updatePlannerLabel();
	}

	@Override
	public void initOnSimLoad()
	{
		this.mapViewTeams = setMapViewTeams(Simulation.sim.getScenario().getTeams());
		this.teamBox.setItems(FXCollections.observableArrayList(this.mapViewTeams));
		this.teamBox.valueProperty().addListener((observable, oldValue, newValue) -> {if (newValue!=null)Simulation.sim.getSimUI().setSelectedTeam((MapViewTeam)newValue);});
		this.teamBox.getSelectionModel().selectFirst();
		update();
	}

	public void changeSelectedTeam()
	{
		this.agentBox.setItems((FXCollections.observableArrayList(Simulation.sim.getSimUI().getSelectedTeam().getAgents())));
		this.agentBox.valueProperty().addListener((observable, oldValue, newValue) -> {if (newValue!=null)Simulation.sim.getSimUI().setSelectedAgent((Agent)newValue);});
		this.agentBox.getSelectionModel().selectFirst();
	}

	private void updateResourceLabel()
	{
		String resourcesStr = new String();
		//No resources for Gaia team
		if (Simulation.sim.getSimUI().getSelectedTeam().equals(Simulation.sim.getScenario().getGaia()))
		{
			resourcesStr = "Select a team to \n" +
					"display its resources.";
		}
		else
		{

			MapViewTeam team = Simulation.sim.getSimUI().getSelectedTeam();
			for (Resource resource : Simulation.sim.getSimUI().getSelectedTeam().getResources().values())
			{
				resourcesStr += "\n" + resource.getType() + ": " +
						team.getResource(resource.getType()).getCurrent() + "/" +
						team.getResource(resource.getType()).getGoal();
			}
		}
		this.resourceLabel.setText(resourcesStr);
	}

	/**Contains info about agent's position, goal etc */
	private void updatePlannerLabel()
	{
		//TODO
		Agent curAgent = Simulation.sim.getSimUI().getSelectedAgent();
		if (curAgent.getType()==null)	//Knowledge about the agent of Gaia is scarce
		{
			return;
		}
		String str = "Position: ";
		str += (int)curAgent.pos.getWidth() + ",";
		str += (int)curAgent.pos.getHeight() + "\n";
		str += "Action: " + "\n";
		str += curAgent.getAction();
		this.plannerLabel.setText(str);
	}

	/**To be used by the renderer */
	private MapViewTeam[] setMapViewTeams(Team[] realTeams)
	{
		MapViewTeam[] mapViewTeams = new MapViewTeam[realTeams.length+1];
		MapViewTeam gaia = Simulation.sim.getScenario().getGaia();
		mapViewTeams[0] = gaia;
		for (int i=1; i<mapViewTeams.length; i++)
		{
			mapViewTeams[i] = realTeams[i-1];
		}
		return mapViewTeams;
	}
}