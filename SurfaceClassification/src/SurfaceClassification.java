import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class SurfaceClassification
{
	private static Triangulation triangulation;

	public static void main(String[] args)
	{
		File[] listFiles = new File(".").listFiles();
		for (File file : listFiles)
		{
			String fileName = file.getName();
			if (fileName.endsWith(".txt"))
			{
				parseData(fileName);
				classifySurface(fileName);
			}
		}
	}

	private static void classifySurface(String fileName)
	{
		// TODO DOMEN: preveri, da ta triangulacija sploh predstavlja ploskev

		boolean orientable = triangulation.isOrientable();
		// TODO ROK: poišči še eulerjevo karakteristiko in glede na ta dva
		// podatka klasificiraj ploskev

		// TODO DARKO: poišči robne komponente

		System.out.println("Orientation: " + orientable);

		// podvojeni trikotniki v XSurfaceSpaceStation.txt:
		// 6368 6367 6366
		// 6373 6374 6379
		// 6557 6546 6545
		// 6552 6551 6550
	}

	private static void parseData(String fileName)
	{
		triangulation = new Triangulation();
		try
		{
			File file = new File(fileName);
			Files.lines(file.toPath()).forEach(str ->
			{
				String trimmedString = str.trim();
				if (trimmedString.length() <= 0)
				{
					return;
				}
				String[] split = trimmedString.split(" ");
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
