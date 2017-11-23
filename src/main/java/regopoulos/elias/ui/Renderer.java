package regopoulos.elias.ui;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Renderer
{
	private GraphicsContext gc;

	protected Renderer(GraphicsContext gc)
	{
		this.gc = gc;
	}

	protected void render()
	{
		//TODO
		gc.fillRect(0,0,SimWindow.WINDOW_WIDTH*0.7,SimWindow.WINDOW_HEIGHT*0.7);
		gc.strokeText("Hello sim world", 100, 100);
		System.out.println("Rendering");
	}


}
