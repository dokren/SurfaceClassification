package naloga2;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class Orientation
{
	private static Triangulation triangulation;

	public static void main(String[] args)
	{
		File[] listFiles = new File(".").listFiles();
		for (File file : listFiles)
		{
			String fileName = file.getName();
			if (fileName.startsWith("naloga2b") && fileName.endsWith(".txt"))
			{
				parseData(fileName);
				computeOrientation(fileName);
			}
		}
	}

	private static void computeOrientation(String fileName)
	{
		triangulation.findNeighbours();
		boolean orientable = triangulation.isOrientable();
		System.out.println("Orientabilnost triangulacije iz datoteke " + fileName + ": ");
		System.out.println("\t" + orientable);
	}

	private static void parseData(String fileName)
	{
		triangulation = new Triangulation();
		try
		{
			File file = new File(fileName);
			Files.lines(file.toPath()).forEach(str ->
			{
				String[] split = str.trim().split(" ");
				if (split.length == 3)
				{
					int a = Integer.parseInt(split[0]);
					int b = Integer.parseInt(split[1]);
					int c = Integer.parseInt(split[2]);
					triangulation.add(new Triangle(a, b, c));
				}
				else
				{
					throw new RuntimeException("Napačen format vrstice");
				}
			});
		}
		catch (IOException | NumberFormatException e)
		{
			throw new RuntimeException(e);
		}
	}
}
