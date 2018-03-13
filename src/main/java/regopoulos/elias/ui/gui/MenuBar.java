package regopoulos.elias.ui.gui;

import javafx.geometry.Orientation;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.ToolBar;
import javafx.stage.FileChooser;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.util.ModelSerializer;
import regopoulos.elias.scenario.ai.NetStorage;
import regopoulos.elias.sim.Simulation;
import regopoulos.elias.sim.TimerGranularity;

import java.io.File;

public class MenuBar extends ToolBar implements Updateable
{
	private Button newBtn, dbgBtn, pauseBtn, fasterBtn, slowerBtn, agentBtn, teamBtn, roundBtn;
	private Label roundIndicatorLabel, simSpeedLabel;
	MenuBar(int width, int height)
	{
		createButtons();

		this.roundIndicatorLabel = new Label("Rounds are indicated here");
		this.simSpeedLabel = new Label("Sim tick frequency");

		this.setPrefSize(width,height);
		this.setOrientation(Orientation.HORIZONTAL);
		this.getItems().add(newBtn);
		this.getItems().add(dbgBtn);
		this.getItems().add(new Separator());

		this.getItems().add(this.roundIndicatorLabel);
		this.roundIndicatorLabel.setPrefSize(240.0,10);
		this.getItems().add(new Separator());

		this.getItems().add(pauseBtn);
		this.getItems().add(simSpeedLabel);
		this.simSpeedLabel.setPrefSize(145,10);
		this.getItems().add(fasterBtn);
		this.getItems().add(slowerBtn);

		this.getItems().add(new Separator());

		this.getItems().add(new Label("Steps:"));
		this.getItems().add(agentBtn);
		this.getItems().add(teamBtn);
		this.getItems().add(roundBtn);
		this.getItems().add(new Separator());

	}

	private void createButtons()
	{
		newBtn = new Button("New");
		newBtn.setOnAction(e -> new OptionsStage());
		dbgBtn = new Button("Debug");
		dbgBtn.setOnAction(e -> debug());

		pauseBtn = new Button("Pause");
		pauseBtn.setOnAction(e -> flipPaused());
		fasterBtn = new Button("Faster");
		fasterBtn.setOnAction(e -> Simulation.sim.getSimLoop().faster());
		slowerBtn = new Button("Slower");
		slowerBtn.setOnAction(e -> Simulation.sim.getSimLoop().slower());

		agentBtn = new Button("Agent");
		agentBtn.setOnAction(e -> Simulation.sim.getSimLoop().setGranularity(TimerGranularity.AGENT));
		teamBtn = new Button("Team");
		teamBtn.setOnAction(e -> Simulation.sim.getSimLoop().setGranularity(TimerGranularity.TEAM));
		roundBtn = new Button("Round");
		roundBtn.setOnAction(e -> Simulation.sim.getSimLoop().setGranularity(TimerGranularity.ROUND));
	}

	private void flipPaused()
	{
		Simulation.sim.getSimLoop().flipPaused();
		String btnText = Simulation.sim.getSimLoop().isPaused()?"Play":"Pause";
		pauseBtn.setText(btnText);
	}

	/** Meant only for random debugging */
	private void debug()
	{
		FileChooser fc = new FileChooser();
		fc.setTitle("Choose existing neural network");
		File path = new File(NetStorage.NET_DIRECTORY);
		path.mkdirs();
		fc.setInitialDirectory(new File(NetStorage.NET_DIRECTORY));
		fc.getExtensionFilters().add(
				new FileChooser.ExtensionFilter("Neural network created with DL4J", "*.zip"));
		File netFile = fc.showOpenDialog(null);
		try
		{
			MultiLayerNetwork net = ModelSerializer.restoreMultiLayerNetwork(netFile);
//				this.netsToLoad.put(teamType, netFile.getName());
//				this.teamNets.put(teamType, net);
		}
		catch( Exception e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void update()
	{
		//Nothing to update on the menu bar, round indicator gets updated by its listener
	}

	@Override
	public void initOnSimLoad()
	{
		this.roundIndicatorLabel.textProperty().bind(Simulation.sim.getSimLoop().roundIndicator);
		this.simSpeedLabel.textProperty().bind(Simulation.sim.getSimLoop().simTickTimeIndicator);
		update();
	}
}
