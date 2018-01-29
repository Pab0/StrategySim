package regopoulos.elias.ui.gui;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Renderer
{
	private GraphicsContext gc;
	private Camera camera;

	protected Renderer(GraphicsContext gc)
	{
		this.gc = gc;
	}

	public void render()
	{
		//TODO
		gc.fillRect(0,0,SimWindow.WINDOW_WIDTH*0.7,SimWindow.WINDOW_HEIGHT*0.7);
		gc.setFill(Color.WHITE);
		gc.strokeRect(0+camera.getOffSetX(),0+camera.getOffSetY(),
				SimWindow.WINDOW_WIDTH*0.7,SimWindow.WINDOW_HEIGHT*0.7);
	}

	public void render(boolean splashScreen)
	{
		gc.setFill(Color.WHITE);
		gc.strokeRect(0,0,SimWindow.WINDOW_WIDTH*0.7,SimWindow.WINDOW_HEIGHT*0.7);
		gc.strokeText("Hello sim world", 100, 100);
	}

	public void setCamera(Camera camera)
	{
		this.camera = camera;
	}
}
