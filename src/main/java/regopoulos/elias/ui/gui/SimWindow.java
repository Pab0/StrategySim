package regopoulos.elias.ui.gui;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import regopoulos.elias.StartSim;
import regopoulos.elias.scenario.Agent;
import regopoulos.elias.scenario.MapViewTeam;
import regopoulos.elias.sim.SimLoop;
import regopoulos.elias.sim.Simulation;
import regopoulos.elias.ui.SimulationUI;

import java.io.InputStream;
import java.util.Properties;

public class SimWindow extends Application implements SimulationUI
{
	//TODO: Move all properties to .properties file
	private static final int MENU_HEIGHT= 30;
	private static double CANVAS_WIDTH_SHARE;
	private static double CANVAS_HEIGHT_SHARE;
	private static String WINDOW_TITLE;
	static int WINDOW_WIDTH;
	static int WINDOW_HEIGHT;
	static Stage primaryStage;
	private MenuBar menuBar;
	private TeamPane teamPane;
	private Canvas canvas;
	private ActionPane actionPane;
	private LogBar logBar;

	private LogOutput logOutput;

	private Camera camera;
	private Renderer renderer;

	private MapViewTeam selectedTeam;
	private Agent selectedAgent;

	@Override
	public void start(String[] args)
	{
		loadProperties();
		launch(args);
	}

	public void start(Stage primaryStage)
	{
		SimWindow.primaryStage = primaryStage;
		primaryStage.setTitle(WINDOW_TITLE);

		Group root = populateUI();
		StartSim.simUI = this;
		logOutput = logBar;
		InputHandler inputHandler = new InputHandler(logOutput);

		renderer.render(true);

		Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
		scene.setOnKeyTyped(inputHandler::keyTyped);
		scene.setOnKeyPressed(inputHandler::keyPressed);
		scene.setOnKeyReleased(inputHandler::keyReleased);
		primaryStage.setScene(scene);
		canvas.requestFocus();
		primaryStage.show();
		System.out.println("Done");
	}

	private Group populateUI()
	{
		Group root = new Group();

		GridPane gridPane = new GridPane();
		//Menu
		this.menuBar = new MenuBar(WINDOW_WIDTH, MENU_HEIGHT);
		gridPane.add(menuBar, 0, 0,3,1);
		//Team pane, and agent selector
		this.teamPane = new TeamPane((int)(WINDOW_WIDTH*(1-CANVAS_WIDTH_SHARE)/2));
		gridPane.add(teamPane,0,1,1,1);
		//Canvas
		this.canvas = new Canvas();
		canvas.setWidth(WINDOW_WIDTH*CANVAS_WIDTH_SHARE);
		canvas.setHeight(WINDOW_HEIGHT*CANVAS_HEIGHT_SHARE);
		gridPane.add(canvas,1,1,1,1);
		GraphicsContext gc = canvas.getGraphicsContext2D();
		renderer = new Renderer(gc);
		//Pane with list of PoI and possible actions
		this.actionPane = new ActionPane((int)(WINDOW_WIDTH*(1-CANVAS_WIDTH_SHARE)/2));
		gridPane.add(actionPane,2,1,1,1);
		//Agent and log bar
		this.logBar = new LogBar((int)(WINDOW_HEIGHT*(1-CANVAS_HEIGHT_SHARE)-MENU_HEIGHT-10));
		gridPane.add(logBar,0,2,3,1);

		root.getChildren().add(gridPane);
		return root;
	}

	public void initOnSimLoad()
	{
		this.selectedTeam = Simulation.sim.getScenario().getTeams()[0];
		this.selectedAgent = this.selectedTeam.getAgents()[0];
		this.camera = new Camera(this.canvas);
		this.renderer.setCamera(camera);
		SimLoop simLoop = new SimLoop();
		Simulation.sim.setSimLoop(simLoop);
		this.teamPane.initOnSimLoad();
		this.menuBar.initOnSimLoad();

		simLoop.start();
	}

	@Override
	public void log(String string)
	{
		this.logBar.log(string);
	}

	@Override
	public MapViewTeam getSelectedTeam()
	{
		return selectedTeam;
	}

	@Override
	public void setSelectedTeam(MapViewTeam selectedTeam)
	{
		this.selectedTeam = selectedTeam;
		System.out.println("Selected team: " + this.selectedTeam);
		teamPane.changeSelectedTeam();
	}

	@Override
	public Agent getSelectedAgent()
	{
		return selectedAgent;
	}

	@Override
	public void setSelectedAgent(Agent selectedAgent)
	{
		this.selectedAgent = selectedAgent;
	}

	private static void loadProperties()
	{
		Properties prop = new Properties();
		try (InputStream fis = SimWindow.class.getClassLoader().getResourceAsStream("UI.properties"))
		{
			prop.loadFromXML(fis);
			WINDOW_TITLE = prop.getProperty("WindowTitle");
			WINDOW_WIDTH = Integer.parseInt(prop.getProperty("WindowWidth"));
			WINDOW_HEIGHT = Integer.parseInt(prop.getProperty("WindowHeight"));
			CANVAS_WIDTH_SHARE = Double.parseDouble(prop.getProperty("CanvasWidthShare"));
			CANVAS_HEIGHT_SHARE = Double.parseDouble(prop.getProperty("CanvasHeightShare"));
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public Camera getCamera()
	{
		return this.camera;
	}

	public Renderer getRenderer()
	{
		return renderer;
	}
}
