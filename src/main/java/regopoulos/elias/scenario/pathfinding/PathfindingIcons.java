package regopoulos.elias.scenario.pathfinding;

import javafx.scene.image.Image;

import java.nio.file.Path;

public class PathfindingIcons
{
	private static final String ICON_FOLDER = "icons/path/";
	private static final String PATH_ICON = "path.png";
	private static final String GOAL_ICON = "goal.png";
	private static final String RISK_NAME = "risk";
	private static final String RISK_EXTENSION = ".png";


	private Image pathIcon, goalIcon;
	private Image[] riskIcons;

	public PathfindingIcons()
	{
		this.pathIcon = new Image(PathfindingIcons.ICON_FOLDER + PathfindingIcons.PATH_ICON);
		this.goalIcon = new Image(PathfindingIcons.ICON_FOLDER + PathfindingIcons.GOAL_ICON);
		this.riskIcons = new Image[Risk.values().length];
		for (int i=0; i<riskIcons.length; i++)
		{
			this.riskIcons[i] = new Image(PathfindingIcons.ICON_FOLDER +
					PathfindingIcons.RISK_NAME + i + PathfindingIcons.RISK_EXTENSION);
		}
	}

	public Image getPathIcon()
	{
		return this.pathIcon;
	}

	public Image getGoalIcon()
	{
		return this.goalIcon;
	}

	public Image getRiskIcon(int risk)
	{
		return this.riskIcons[Math.min(risk,riskIcons.length-1)];
	}
}
