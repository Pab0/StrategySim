package regopoulos.elias.ui.gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Separator;
import javafx.scene.layout.VBox;
import org.nd4j.linalg.api.ndarray.INDArray;
import regopoulos.elias.scenario.Agent;
import regopoulos.elias.scenario.ai.Action;
import regopoulos.elias.scenario.ai.Planner;
import regopoulos.elias.scenario.ai.QLearning;
import regopoulos.elias.scenario.ai.WinterAI;
import regopoulos.elias.sim.Simulation;

import java.util.ArrayList;

public class ActionPane extends VBox implements Updateable
{
	ListView<Action> actionView;
	ListView<String> actionWorth;
	Label epsilonLabel;

	protected ActionPane(int width)
	{
		this.setSpacing(10);
		this.setPadding(new Insets(20,5,20,5));

		this.getChildren().add(new Label("Possible Actions:"));
		actionView = new ListView<>();
		actionView.setPrefHeight(200);
		this.getChildren().add(actionView);
		this.getChildren().add(new Label("Action Worth:"));
		actionWorth = new ListView<>();
		actionWorth.setPrefHeight(200);
		this.getChildren().add(actionWorth);
		epsilonLabel = new Label("Epsilon:");
		this.getChildren().add(epsilonLabel);
		this.setPrefWidth(width);
	}

	@Override
	public void update()
	{
		Agent selAgent = Simulation.sim.getSimUI().getSelectedAgent();
		if (selAgent.getPossibleActions()==null || selAgent.getPossibleActions().isEmpty() || !selAgent.isAlive())
		{
			actionView.setItems(FXCollections.observableArrayList());
			actionWorth.setItems(FXCollections.observableArrayList());
		}
		else
		{
			Planner selPlanner = selAgent.getTeam().getPlanner();
			selPlanner.updatePossibleActions(selAgent);
			actionView.setItems(FXCollections.observableArrayList(
					selAgent.getPossibleActions()));
			if (selPlanner.usesNeuralNet())
			{
				actionWorth.setItems(getActionWorth());
				WinterAI nnPlanner = (WinterAI)selPlanner;
				epsilonLabel.setText("Epsilon: " + nnPlanner.getQLearning().getEpsilon());
			}
			else
			{
				actionWorth.setItems(null);
				epsilonLabel.setText("");
			}
		}
	}

	private ObservableList<String> getActionWorth()
	{
		Agent selAgent = Simulation.sim.getSimUI().getSelectedAgent();
		Action[] actions = selAgent.getState().getActions();
		WinterAI winterAI = (WinterAI)(selAgent.getTeam().getPlanner());
		QLearning qLearning = winterAI.getQLearning();
		INDArray actionVector = qLearning.getActionVector(selAgent.getState().getStateVector());

		ArrayList<String> actionWorth = new ArrayList();
		for (int i=0; i<actions.length; i++)
		{
			if (actions[i]!=null)
			{
				actionWorth.add(actions[i].shortHand() + ": " + String.format("%.3f",actionVector.getDouble(i)));
			}
		}
		return FXCollections.observableArrayList(actionWorth);
	}

	@Override
	public void initOnSimLoad()
	{

	}
}
