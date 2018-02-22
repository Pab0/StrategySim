package regopoulos.elias.scenario.pathfinding;

import javafx.scene.image.Image;

public class PathfindingIcons
{
	private static final String ICON_FOLDER = "icons/path/";
	private static final String PATH_ICON = "path.png";
	private static final String GOAL_ICON = "goal.png";

	private Image pathIcon, goalIcon;

	public PathfindingIcons()
	{
		this.pathIcon = new Image(PathfindingIcons.ICON_FOLDER + PathfindingIcons.PATH_ICON);
		this.goalIcon = new Image(PathfindingIcons.ICON_FOLDER + PathfindingIcons.GOAL_ICON);
	}

	public Image getPathIcon()
	{
		return this.pathIcon;
	}

	public Image getGoalIcon()
	{
		return this.goalIcon;
	}
}
