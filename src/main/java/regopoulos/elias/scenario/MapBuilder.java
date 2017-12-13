package regopoulos.elias.scenario;

import javafx.geometry.Dimension2D;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;

class MapBuilder
{
	private final static String COMMENTED_LINE_START = "#";

	static Tile[][] getTileMap(File file)
	{
		Dimension2D dimension = getMapDimension(file);
		return fillMap(file, dimension);
	}

	private static Tile[][] fillMap(File file, Dimension2D dimension)
	{
		Tile[][] map = new Tile[(int)dimension.getHeight()][(int)dimension.getWidth()];
		try (Scanner scanner = new Scanner(file))
		{
			int height = 0;
			while (scanner.hasNext())
			{
				String line = scanner.nextLine();
				if (line.startsWith(MapBuilder.COMMENTED_LINE_START))
				{
					continue;
				}
				line = sanitizeLine(line, (int)dimension.getWidth());
				map[height] = fillLine(line, (int)dimension.getWidth());
				height++;
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return map;
	}

	/* Fills Tile[] according to char[] line. */
	private static Tile[] fillLine(String line, int maxWidth)
	{
		Tile[] tileLine = new Tile[maxWidth];
		char[] lineArr = line.toCharArray();
		for (int i=0; i<tileLine.length; i++)
		{
			TerrainType type = TerrainType.getTerrain(lineArr[i]);
			if (type==TerrainType.UNKNOWN)
			{
				type = TerrainType.GRASS;	//Can't have unknown tiles in the world itself
			}
			tileLine[i] = new Tile (type);
		}
		return tileLine;
	}

	private static String sanitizeLine(String line, int maxWidth)
	{
		//All characters are upper case
		line = line.toUpperCase();
		//All lines have equal length
		char[] appendage = new char[maxWidth - line.length()];
		Arrays.fill(appendage, TerrainType.GRASS.glyph);
		line += new String(appendage);
		//All unknown glyphs are converted to GRASS glyphs
		char[] lineArr = line.toCharArray();
		for (int i=0; i<lineArr.length; i++)
		{
			lineArr[i] = TerrainType.getTerrain(lineArr[i]).glyph;
		}
		return new String(lineArr);
	}

	private static Dimension2D getMapDimension(File file)
	{
		int width = 0;
		int height = 0;
		try (Scanner scanner = new Scanner(file))
		{
			while (scanner.hasNext())
			{
				String line = scanner.nextLine();
				if (line.startsWith(MapBuilder.COMMENTED_LINE_START))
				{
					continue;
				}
				width = Math.max(width, line.length());
				height++;
			}
		}
		catch (IOException e)
		{
			System.out.println("Problem reading map file.");
			e.printStackTrace();
		}
		return new Dimension2D(width, height);
	}

}
