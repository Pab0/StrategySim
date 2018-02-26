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
		this.teamBox.valueProperty().addListener((observable, oldValue, newValue) -> {if (newValue!=null)Simulation.sim.getSimUI().setSelectedTeam((MapViewTeam)newValue);});
		this.getChildren().add(teamBox);

		this.getChildren().add(new Label("Resources:"));
		this.resourceLabel = new Label();
		this.getChildren().add(resourceLabel);

		this.getChildren().add(new Separator());

		this.getChildren().add(new Label("Team's Agents:"));
		this.agentBox = new ComboBox();
		this.agentBox.valueProperty().addListener((observable, oldValue, newValue) -> {if (newValue!=null)Simulation.sim.getSimUI().setSelectedAgent((Agent)newValue);});
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
		this.teamBox.getSelectionModel().selectFirst();
		update();
	}

	void changeSelectedTeam()
	{
		this.agentBox.setItems((FXCollections.observableArrayList(Simulation.sim.getSimUI().getSelectedTeam().getAgents())));
		this.agentBox.getSelectionModel().selectFirst();
	}

	private void updateResourceLabel()
	{
		String resourcesStr = "";
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
		Agent selectedAgent = Simulation.sim.getSimUI().getSelectedAgent();
		MapViewTeam selectedTeam = Simulation.sim.getSimUI().getSelectedTeam();
		if (selectedAgent.getType()==null)	//Knowledge about the agent of Gaia is scarce
		{
			return;
		}
		else if (!selectedAgent.getTeam().equals(selectedTeam))	//empty team, don't display last selected live agent
		{
			this.plannerLabel.setText("");
			return;
		}
		String str = "Action: " + "\n";
		str += selectedAgent.getAction() + "\n\n";
		str += "Path cost: " + ((selectedAgent.getAction()==null) ? 0 : selectedAgent.getAction().getPathCost()) + "\n";
		str += "Position: ";
		str += (int)selectedAgent.pos.getWidth() + ",";
		str += (int)selectedAgent.pos.getHeight() + "\n";
		str += "Health: " + selectedAgent.getHP() + "/" + selectedAgent.getType().getMaxHP() + "\n";
		str += "Attack: " + selectedAgent.getType().getAttack() + "\n";
		str += "Defense:" + selectedAgent.getType().getDefense() + "\n";
		str += "Carries: " + (selectedAgent.isCarryingResource()?selectedAgent.getResouceCarrying():"Nothing");
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