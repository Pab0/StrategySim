package regopoulos.elias.scenario;

import javafx.scene.image.Image;

public enum AgentType
{
	//TODO give each one his own icon
	VILLAGER	(3,1,0,1, "agent.png"),
	HUNTER		(4,2,0,2, "agent.png"),
	GUARD		(6,2,1,0.6, "agent.png"),
	KNIGHT		(8,3,2,2.5, "agent.png");

	int maxHP;
	int attack;
	int defense;
	double speed;
	Image icon;

	private static final String ICON_FOLDER = "icons/";

	AgentType(int maxHP, int attack, int defense, double speed, String iconName)
	{
		this.maxHP = maxHP;
		this.attack = attack;
		this.defense = defense;
		this.speed = speed;
		try
		{
			this.icon = new Image(AgentType.ICON_FOLDER + iconName);	//May or may not need "file:" prefix
		}
		catch (Exception e)
		{
			System.out.println("Tried to read " + ICON_FOLDER + iconName);
			e.printStackTrace();
		}
	}

	public Image getIcon()
	{
		return icon;
	}
}
