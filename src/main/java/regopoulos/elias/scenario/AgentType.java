package regopoulos.elias.scenario;

import javafx.scene.image.Image;

public enum AgentType
{
	//TODO give each one his own icon
	VILLAGER	(3,1,0,"agent.png"),
	HUNTER		(4,2,0,"agent.png"),
	GUARD		(4,1,2,"agent.png"),
	KNIGHT		(7,3,1,"agent.png");

	int maxHP;
	int attack;
	int defense;
	Image icon;

	private static final String ICON_FOLDER = "icons/";

	AgentType(int maxHP, int attack, int defense, String iconName)
	{
		this.maxHP = maxHP;
		this.attack = attack;
		this.defense = defense;
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
