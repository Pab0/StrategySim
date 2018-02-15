package regopoulos.elias.ui.gui;

import javafx.geometry.Dimension2D;
import javafx.scene.canvas.Canvas;
import regopoulos.elias.scenario.Map;
import regopoulos.elias.sim.Simulation;

public class Camera
{
	private static final byte PAN_SPEED = 5; //pan speed of camera, in pixels

	private final int MAX_OFFSET_Y;
	private final int MAX_OFFSET_X;
	private final int MIN_OFFSET_Y;
	private final int MIN_OFFSET_X;
	//Offset in pixels from starting position ( [0,0] on top-left corner)
	private int offSetY;
	private int offSetX;

	private boolean isMovingUp;
	private boolean isMovingDown;
	private boolean isMovingLeft;
	private boolean isMovingRight;


	Camera(Canvas canvas)
	{
		Map map = Simulation.sim.getScenario().getMap();
		int mapHeight = map.getHeight();
		int mapWidth = map.getWidth();
		int tileWidth = Renderer.TILE_WIDTH;
		int canvasHeight = (int)canvas.getHeight();
		int canvasWidth = (int)canvas.getWidth();
		this.MAX_OFFSET_Y = canvasHeight/2;
		this.MAX_OFFSET_X = canvasWidth/2;
		this.MIN_OFFSET_Y = -mapHeight*tileWidth + canvasHeight/2;
		this.MIN_OFFSET_X = -mapWidth*tileWidth + canvasWidth/2;
	}

	void setMovingUp(boolean movingUp)
	{
		isMovingUp = movingUp;
	}

	void setMovingDown(boolean movingDown)
	{
		isMovingDown = movingDown;
	}

	void setMovingLeft(boolean movingLeft)
	{
		isMovingLeft = movingLeft;
	}

	void setMovingRight(boolean movingRight)
	{
		isMovingRight = movingRight;
	}

	int getOffSetY()
	{
		return offSetY;
	}

	int getOffSetX()
	{
		return offSetX;
	}

	public void update()
	{
		if (isMovingUp)
		{
			this.offSetY-=Camera.PAN_SPEED;
		}
		if (isMovingDown)
		{
			this.offSetY+=Camera.PAN_SPEED;
		}
		if (isMovingLeft)
		{
			this.offSetX-=Camera.PAN_SPEED;
		}
		if (isMovingRight)
		{
			this.offSetX+=Camera.PAN_SPEED;
		}
		checkBounds();
	}

	/**Checks camera movement bounds. Offset should allow at most half the screen to be off-map */
	private void checkBounds()
	{
		this.offSetY = Math.max(this.offSetY, this.MIN_OFFSET_Y);
		this.offSetY = Math.min(this.offSetY, this.MAX_OFFSET_Y);
		this.offSetX = Math.max(this.offSetX, this.MIN_OFFSET_X);
		this.offSetX = Math.min(this.offSetX, this.MAX_OFFSET_X);
	}

	/**centers camera over tile at given coordinates */
	void centerOn(Dimension2D point)
	{
		SimWindow sw = (SimWindow)Simulation.sim.getSimUI();
		int canvasHeight = (int)sw.getRenderer().mapTileCapacity.getY();
		int canvasWidth = (int)sw.getRenderer().mapTileCapacity.getX();
		this.offSetY = (int)(canvasHeight/2 - point.getHeight())*Renderer.TILE_WIDTH;
		this.offSetX = (int)(canvasWidth/2 - point.getWidth())*Renderer.TILE_WIDTH;
		checkBounds();
		System.out.println("Centering on " + this.offSetY + ", " + this.offSetX);
	}
}
