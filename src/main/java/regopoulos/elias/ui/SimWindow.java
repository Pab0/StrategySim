package regopoulos.elias.ui;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

public class SimWindow extends Application
{
	//TODO: Move all properties to .properties file
	private static final int MENU_HEIGHT= 30;
	private static double CANVAS_WIDTH_SHARE;
	private static double CANVAS_HEIGHT_SHARE;
	private static String WINDOW_TITLE;
	static int WINDOW_WIDTH;
	static int WINDOW_HEIGHT;
	private MenuBar menuBar;
	private TeamPane teamPane;
	private Canvas canvas;
	private ActionPane actionPane;
	private LogBar logBar;

	private LogOutput logOutput;

	private Renderer renderer;

	public SimWindow()
	{
		loadProperties();
	}

	public void startWindow(String[] args)
	{
		launch(args);
	}

	public void start(Stage primaryStage)
	{
		primaryStage.setTitle(WINDOW_TITLE);

		Group root = populateUI();
		logOutput = logBar;
		InputHandler inputHandler = new InputHandler(logOutput);

		renderer.render();

		Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
		scene.setOnKeyTyped( (event) -> inputHandler.keyTyped(event));
		scene.setOnKeyPressed( (event) -> inputHandler.keyPressed(event));
		scene.setOnKeyReleased( (event) -> inputHandler.keyReleased(event));
		primaryStage.setScene(scene);
		canvas.requestFocus();
		primaryStage.show();
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
}
