package regopoulos.elias.ui.gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import regopoulos.elias.scenario.*;
import regopoulos.elias.sim.Simulation;

import java.io.File;
import java.util.EnumMap;


public class OptionsStage extends Stage
{
	private static final int WIDTH = 600;
	private static final int HEIGHT = 800;
	private static final int CANVAS_WIDTH = WIDTH-100;
	private static final int CANVAS_HEIGHT = 800/2;

	private Canvas canvas;
	private Map map;

	//ObservableLists of corresponding Map fields:
	private ObservableList<TerrainType> resourcesInMap;
	private ObservableList<TerrainType> teamsInMap;

	//EnumMaps for the Scenario to be set to
	private EnumMap<TerrainType, Integer> resourceGoals;
	private EnumMap<AgentType, Integer> agentNum;

	OptionsStage()
	{
		this.resourcesInMap = FXCollections.observableArrayList();
		this.teamsInMap = FXCollections.observableArrayList();
		this.resourceGoals = new EnumMap<TerrainType, Integer>(TerrainType.class);
		this.agentNum = new EnumMap<AgentType, Integer>(AgentType.class);
		this.initModality(Modality.WINDOW_MODAL);
		this.initOwner(SimWindow.primaryStage);
		VBox vBox = populateOptionStage();
		Scene scene = new Scene(vBox);
		this.setScene(scene);
		this.show();
	}

	private VBox populateOptionStage()
	{
		VBox vBox = new VBox();
		vBox.setSpacing(20);
		vBox.setPadding(new Insets(20,5,20,5));
		vBox.setPrefSize(WIDTH, HEIGHT/2);

		//Map
		vBox.getChildren().add(mapHBox());
		vBox.getChildren().add(new Separator());

		//MapInfo
		vBox.getChildren().add(mapInfoHBox());
		vBox.getChildren().add(new Separator());

		//Needed Resources
		vBox.getChildren().add(resHBox());
		vBox.getChildren().add(new Separator());

		//Agent Number
		vBox.getChildren().add(agentHBox());
		vBox.getChildren().add(new Separator());

		//StartButton
		vBox.getChildren().add(startHBox());
		return vBox;
	}

	private HBox mapHBox()
	{
		HBox mapHBox = new HBox();
		mapHBox.setSpacing(10);
		Button ldMap = new Button("Load Map");
		ldMap.setOnAction(event -> loadMap());
		mapHBox.getChildren().add(ldMap);
		canvas = new Canvas(CANVAS_WIDTH, CANVAS_HEIGHT);
		canvas.getGraphicsContext2D().fillRect(0,0,canvas.getWidth(),canvas.getHeight());
		mapHBox.getChildren().add(canvas);
		return mapHBox;
	}

	private HBox mapInfoHBox()
	{
		HBox mapInfoHBox = new HBox();
		mapInfoHBox.setSpacing(10);
		mapInfoHBox.getChildren().add(new Label("Teams found: "));
		ChoiceBox<TerrainType> teamsView = new ChoiceBox<TerrainType>();
		teamsView.setItems(this.teamsInMap);
		mapInfoHBox.getChildren().add(teamsView);
		mapInfoHBox.getChildren().add(new Label("Resources found: "));
		ChoiceBox<TerrainType> resView = new ChoiceBox<TerrainType>();
		resView.setItems(this.resourcesInMap);
		mapInfoHBox.getChildren().add(resView);
		return mapInfoHBox;
	}

	private HBox resHBox()
	{
		HBox resHBox = new HBox();
		resHBox.setSpacing(10);
		resHBox.getChildren().add(new Label("Needed Resources:"));
		ChoiceBox<TerrainType> resView = new ChoiceBox<TerrainType>();
		resView.setItems(this.resourcesInMap);
		resHBox.getChildren().add(resView);
		TextField neededAmount = new TextField();
		resHBox.getChildren().add(neededAmount);
		Button neededResBtn = new Button("Set");
		neededResBtn.setOnAction(event -> this.resourceGoals.put(resView.getValue(), Integer.parseInt(neededAmount.getText())));
		resHBox.getChildren().add(neededResBtn);
		return resHBox;
	}

	private HBox agentHBox()
	{
		HBox agentHBox = new HBox();
		agentHBox.setSpacing(10);
		agentHBox.getChildren().add(new Label("Number of Agents for each team:"));
		ChoiceBox<AgentType> agentsView = new ChoiceBox<AgentType>();
		agentsView.getItems().setAll(AgentType.values());
		agentHBox.getChildren().add(agentsView);
		TextField agentNum = new TextField();
		agentHBox.getChildren().add(agentNum);
		Button agentNumBtn = new Button("Set");
		agentNumBtn.setOnAction(event -> this.agentNum.put(agentsView.getValue(), Integer.parseInt(agentNum.getText())));
		agentHBox.getChildren().add(agentNumBtn);
		return agentHBox;
	}

	private HBox startHBox()
	{
		HBox startHBox = new HBox();
		startHBox.setAlignment(Pos.BOTTOM_RIGHT);
		Button startBtn = new Button("Start");
		startBtn.setStyle("-fx-font-weight: bold");
		startBtn.setOnAction(event -> setOptions());
		startHBox.getChildren().add(startBtn);
		return startHBox;
	}

	private void drawMap()
	{
		GraphicsContext gc = canvas.getGraphicsContext2D();
		gc.setFill(Color.WHITE);
		gc.fillRect(0,0,canvas.getWidth(),canvas.getHeight());
		int tileWidth = (int)this.canvas.getWidth()/this.map.getWidth();
		int tileHeight = (int)this.canvas.getHeight()/this.map.getHeight();
		if (tileWidth<2 || tileHeight<2)
		{
			gc.strokeText("Map dimensions " +
					"(" + this.map.getHeight() + "," + this.map.getWidth() + ")" +
					" too big to render preview.",
					this.canvas.getWidth()/2-200, this.canvas.getHeight()/2);
			return;
		}
		//Draw tiles
		for (int i=0; i<this.map.getHeight(); i++)
		{
			for (int j=0; j<this.map.getWidth(); j++)
			{
				gc.drawImage(this.map.getTileMap()[i][j].getTerrainType().getIcon(),
						j*tileWidth,
						i*tileHeight,
						tileWidth, tileHeight);
			}
		}
		//Draw grid
		for (int i=0; i<=this.map.getHeight(); i++)
		{
			gc.strokeLine(0,i*tileHeight,this.map.getWidth()*tileWidth,i*tileHeight);
		}
		for (int j=0; j<=this.map.getWidth(); j++)
		{
			gc.strokeLine(j*tileWidth,0,j*tileWidth,this.map.getHeight()*tileHeight);
		}
	}

	private void loadMap()
	{
		FileChooser fc = new FileChooser();
		fc.setTitle("Choose map file");
		File mapFile = fc.showOpenDialog(OptionsStage.this);
		System.out.println("Chose file " + mapFile.getName());
		this.map = new Map(mapFile);
		this.map.analyzeMap();
		this.resourcesInMap.clear();
		this.resourcesInMap.addAll(this.map.getResourcesInMap());
		this.teamsInMap.clear();
		this.teamsInMap.addAll(this.map.getTeamsInMap());
		drawMap();
		System.out.println("Found resources: " + this.resourcesInMap);
	}

	/* Gets called on "Start" button click */
	private void setOptions()
	{
		System.out.println("Setting options");
		Scenario scenario = new Scenario();
		scenario.setMap(this.setMap());
		scenario.setTeams(this.setTeams());
		scenario.setResourceGoals(this.setResourceGoals());
		scenario.setAgentNum(this.setAgentNum());
		Simulation.sim.setScenario(scenario);
		this.close();
	}

	private Map setMap()
	{
		return this.map;
	}

	private Team[] setTeams()
	{
		Team[] teams = new Team[this.teamsInMap.size()];
		for (int i=0; i<this.teamsInMap.size(); i++)
		{
			Team team = new Team(this.teamsInMap.get(i));
			teams[i] = team;
		}
		setMapViewTeams(teams);
		return teams;
	}

	/* To be used by the renderer */ //TODO
	public MapViewTeam[] setMapViewTeams(Team[] realTeams)
	{
		MapViewTeam[] mapViewTeams = new MapViewTeam[realTeams.length+1];
		MapViewTeam gaia = new GaiaTeam();
		mapViewTeams[0] = gaia;
		for (int i=1; i<mapViewTeams.length; i++)
		{
			mapViewTeams[i] = realTeams[i-1];
		}
		return mapViewTeams;
	}

	private EnumMap<TerrainType, Integer> setResourceGoals()
	{
		return this.resourceGoals;
	}

	private EnumMap<AgentType, Integer> setAgentNum()
	{
		return this.agentNum;
	}
}
