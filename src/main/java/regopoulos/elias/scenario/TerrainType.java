package regopoulos.elias.scenario;

import javafx.scene.image.Image;

public enum TerrainType
{
	GRASS	('.',0,true,"grass.png"),
	TREE	('T',5,"tree.png"),
	STONE	('S',20,"stone.png"),
	GOLD	('G',10,"gold.png"),
	WATER	('W',0,"water.png"),
	TEAM1	('1',0, "dropOffSite.png"),
	TEAM2	('2',0, "dropOffSite.png"),
	TEAM3	('3',0, "dropOffSite.png"),
	TEAM4	('4',0, "dropOffSite.png"),
	UNKNOWN	('?',0,true, "unknown.png");

	char glyph;
	Image icon;
	int capacity;	//for resources; 0 for non-resources
	boolean isResource;
	public boolean traversable;
	boolean isDropOffSite;

	private static final String ICON_DIRECTORY = "icons/terrain/";

	TerrainType(char glyph, int capacity, String iconName)
	{
		this(glyph, capacity, false, iconName);
	}

	TerrainType(char glyph, int capacity, boolean traversable, String iconName)
	{
		this.glyph = glyph;
		try
		{
			this.icon = new Image(TerrainType.ICON_DIRECTORY + iconName);	//May or may not need "file:" prefix
		}
		catch (Exception e)
		{
			System.out.println("Tried to read " + ICON_DIRECTORY + iconName);
			e.printStackTrace();
		}
		this.traversable = traversable;
		this.capacity = capacity;
		this.isResource = (this.capacity!=0);
		this.isDropOffSite = Character.isDigit(this.glyph);
	}

	public boolean isResource()
	{
		return isResource;
	}

	public boolean isDropOffSite()
	{
		return isDropOffSite;
	}

	public Image getIcon()
	{
		return icon;
	}

	public char getGlyph()
	{
		return glyph;
	}

	static TerrainType getTerrain(char glyph)
	{
		TerrainType res = TerrainType.GRASS;
		for (TerrainType t : TerrainType.values())
		{
			if (glyph==t.glyph)
			{
				res = t;
				break;
			}
		}
		return res;
	}

}