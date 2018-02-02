package regopoulos.elias.ui.gui;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import regopoulos.elias.scenario.Agent;
import regopoulos.elias.scenario.Map;
import regopoulos.elias.scenario.Team;
import regopoulos.elias.sim.Simulation;



public class Renderer
{
	static final short TILE_WIDTH = 20;

	private Point2D mapTileCapacity;	//how many tiles fit onto the canvas
	private Point2D tileOffset;			//how many tiles one should shift
	private Point2D subTileOffset;		//how much tiles are shifted

	private GraphicsContext gc;
	private Camera camera;

	Renderer(GraphicsContext gc)
	{
		this.gc = gc;
		this.mapTileCapacity = new Point2D(gc.getCanvas().getWidth()/TILE_WIDTH,
												gc.getCanvas().getHeight()/TILE_WIDTH);
		this.tileOffset = new Point2D(0,0);
		this.subTileOffset = new Point2D(0,0);
	}

	public void render()
	{
		//TODO
		gc.setFill(Color.DARKGREY);
		gc.fillRect(0,0,gc.getCanvas().getWidth(),gc.getCanvas().getHeight());
		calcRenderOffset();
		renderTiles();
		renderGrid();
		renderAgents();
	}

	private void renderAgents()
	{
		//TODO
		for (Team team : Simulation.sim.getScenario().getTeams())
		{
			for (Agent agent : team.getAgents())
			{
				renderMapItem(agent.getType().getIcon(), (int)agent.pos.getHeight(), (int)agent.pos.getWidth());
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

	/* Based on the camera's offset, calculates
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

		log("tileOffset: " + tileOffset + ", subTileOffset: " + subTileOffset);
	}

	/* Only render tiles visible on canvas	 */
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

//		log("Rendering from " + upperLeftTile + " to " + lowerRightTile);

		for (int i=(int)upperLeftTile.getY(); i<lowerRightTile.getY(); i++)
		{
			for (int j=(int)upperLeftTile.getX(); j<lowerRightTile.getX(); j++)
			{
				renderMapItem(lnkMap.getTileMap()[i][j].getTerrainType().getIcon(), i, j);
			}
		}
	}

	/* Renders item with position y,x on map.
	 * Automatically shifts render position according to camera offset.
	 */
	private void renderMapItem(Image image, int y, int x)
	{
		gc.drawImage(image,
				(x+tileOffset.getX())*TILE_WIDTH + subTileOffset.getX(),
//				(x)*TILE_WIDTH + subTileOffset.getX(),
				(y+tileOffset.getY())*TILE_WIDTH + subTileOffset.getY(),
//				(y)*TILE_WIDTH + subTileOffset.getY(),
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
