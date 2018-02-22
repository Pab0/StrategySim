package regopoulos.elias.ui.gui;

import javafx.geometry.Dimension2D;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import regopoulos.elias.scenario.Agent;
import regopoulos.elias.scenario.Map;
import regopoulos.elias.scenario.Team;
import regopoulos.elias.scenario.TerrainType;
import regopoulos.elias.scenario.pathfinding.PathfindingIcons;
import regopoulos.elias.scenario.pathfinding.TileChecker;
import regopoulos.elias.sim.Simulation;

import java.util.ArrayList;


public class Renderer
{
	static final short TILE_WIDTH = 30;

	public Point2D mapTileCapacity;	//how many tiles fit onto the canvas
	private Point2D tileOffset;			//how many tiles one should shift
	private Point2D subTileOffset;		//how much tiles are shifted

	private GraphicsContext gc;
	private Camera camera;

	private PathfindingIcons pathfindingIcons;

	Renderer(GraphicsContext gc)
	{
		this.gc = gc;
		this.mapTileCapacity = new Point2D(gc.getCanvas().getWidth()/TILE_WIDTH,
												gc.getCanvas().getHeight()/TILE_WIDTH);
		this.tileOffset = new Point2D(0,0);
		this.subTileOffset = new Point2D(0,0);

		this.pathfindingIcons = new PathfindingIcons();
	}

	public void render()
	{
		gc.setFill(Color.DARKGREY);
		gc.fillRect(0,0,gc.getCanvas().getWidth(),gc.getCanvas().getHeight());
		calcRenderOffset();
		renderTiles();
		renderGrid();
		renderAgents();
		renderSelectedAgent();
	}

	private void renderAgents()
	{
		for (Team team : Simulation.sim.getScenario().getTeams())
		{
			for (Agent agent : team.getAgents())
			{
				int y = (int)agent.pos.getHeight();
				int x = (int)agent.pos.getWidth();
				if (TileChecker.isVisibleTile(y,x))
				{
					renderMapItem(agent.getType().getIcon(), y,x);
					renderMapItem(team.getTeamSpotIcon(),y,x);
				}
			}
		}
	}

	private void renderGrid()
	{
		Map lnkMap = Simulation.sim.getScenario().getMap();
		//horizontal lines
		for (int i=0; i<lnkMap.getHeight()+1; i++)
		{
			gc.strokeLine(
					0+camera.getOffSetX(),
					i*TILE_WIDTH+camera.getOffSetY(),
					lnkMap.getWidth()*TILE_WIDTH+camera.getOffSetX(),
					i*TILE_WIDTH+camera.getOffSetY()
			);
		}
		//vertical lines
		for (int i=0; i<lnkMap.getWidth()+1; i++)
		{
			gc.strokeLine(
					i*TILE_WIDTH+camera.getOffSetX(),
					0+camera.getOffSetY(),
					i*TILE_WIDTH+camera.getOffSetX(),
					lnkMap.getHeight()*TILE_WIDTH+camera.getOffSetY()
			);
		}
		//map borders
		gc.rect(camera.getOffSetX(), camera.getOffSetY(),
				lnkMap.getWidth()*TILE_WIDTH+camera.getOffSetX(), lnkMap.getHeight()*TILE_WIDTH+camera.getOffSetY());
	}

	/**Based on the camera's offset, calculates
	 * a) The tiles to be rendered.
	 * b) The sub-tile shift.
	 */
	private void calcRenderOffset()
	{
		int camOffsetX = this.camera.getOffSetX();
		int camOffsetY = this.camera.getOffSetY();

		this.tileOffset = new Point2D(camOffsetX/TILE_WIDTH,
										camOffsetY/TILE_WIDTH);
		this.subTileOffset = new Point2D(camOffsetX%TILE_WIDTH,
											camOffsetY%TILE_WIDTH);

//		log("tileOffset: " + tileOffset + ", subTileOffset: " + subTileOffset);
	}

	/**Only render tiles visible on canvas	 */
	private void renderTiles()
	{
		Map lnkMap = Simulation.sim.getScenario().getMap();
		//Coordinates of map tiles inside canvas viewpoint - adding an extra tile layer to display half tiles as well
		Point2D upperLeftTile = new Point2D(
				(int)Math.max(-tileOffset.getX()-1,0),
				(int)Math.max(-tileOffset.getY()-1,0));
		Point2D lowerRightTile = new Point2D(
				(int)Math.min(-tileOffset.getX()+mapTileCapacity.getX()+2,lnkMap.getWidth()),
				(int)Math.min(-tileOffset.getY()+mapTileCapacity.getY()+2,lnkMap.getHeight()));

		for (int i=(int)upperLeftTile.getY(); i<lowerRightTile.getY(); i++)
		{
			for (int j=(int)upperLeftTile.getX(); j<lowerRightTile.getX(); j++)
			{
				TerrainType type = TileChecker.currentlyVisibleTerrain(i,j);
				renderMapItem(type.getIcon(),i,j);
				if (type.isDropOffSite())
				{
					renderMapItem(Simulation.sim.getScenario().getTeamByID(type).getTeamSpotIcon(),i,j);
				}
			}
		}
	}

	/**Renders a circle around currently selected Agent */
	private void renderSelectedAgent()
	{
		Agent selAgent = Simulation.sim.getSimUI().getSelectedAgent();
		if (selAgent.getType()!=null)	//Gaia's agent is intangible
		{
			int y = (int)selAgent.pos.getHeight();
			int x = (int)selAgent.pos.getWidth();
			gc.strokeOval(
					(x+tileOffset.getX())*TILE_WIDTH + subTileOffset.getX(),
					(y+tileOffset.getY())*TILE_WIDTH + subTileOffset.getY(),
					TILE_WIDTH,TILE_WIDTH);
			if (selAgent.getAction()!=null)
			{
				renderPath(selAgent.getAction().getPath());
				renderGoal(selAgent.getAction().getPoI());
			}
		}
	}

	/** Renders the path from the agent to its chosen goal*/
	private void renderPath(ArrayList<Dimension2D> path)
	{
		Image pathIcon = this.pathfindingIcons.getPathIcon();
		for (Dimension2D node : path)
		{
			if (!node.equals(path.get(path.size()-1)))	//don't render goal as part of path
			{
				renderMapItem(pathIcon, (int)node.getHeight(), (int)node.getWidth());
			}
		}
	}

	/** Marks the agent's goal on the map */
	private void renderGoal(Dimension2D goal)
	{
		renderMapItem(this.pathfindingIcons.getGoalIcon(), (int)goal.getHeight(), (int)goal.getWidth());
	}

	/**Renders item with position y,x on map.
	 * Automatically shifts render position according to camera offset.
	 */
	private void renderMapItem(Image image, int y, int x)
	{
		gc.drawImage(image,
				(x+tileOffset.getX())*TILE_WIDTH + subTileOffset.getX(),
				(y+tileOffset.getY())*TILE_WIDTH + subTileOffset.getY(),
				TILE_WIDTH,TILE_WIDTH);
	}

	void render(boolean splashScreen)
	{
		if (splashScreen)
		{
			renderSplashScreen();
		}
		else
		{
			render();
		}
	}

	private void renderSplashScreen()
	{
		gc.setFill(Color.WHITE);
		gc.strokeRect(0,0,SimWindow.WINDOW_WIDTH*0.7,SimWindow.WINDOW_HEIGHT*0.7);
		gc.strokeText("Hello sim world", 100, 100);
	}

	void setCamera(Camera camera)
	{
		this.camera = camera;
	}

	private void log(String logString)
	{
		Simulation.sim.getSimUI().log(logString);
	}
}
