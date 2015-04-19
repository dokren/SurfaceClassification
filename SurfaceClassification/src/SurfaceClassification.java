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
				classifySurface(fileName);
			}
		}
	}

	private static void classifySurface(String fileName)
	{
		try
		{
			parseData(fileName);
			// TODO DOMEN: preveri, da ta triangulacija sploh predstavlja
			// ploskev
			if (!triangulation.isSurface())
			{
				System.out.println("Podana triangulacija ne predstavlja ploskve");
				return;
			}

			boolean orientable = triangulation.isOrientable();
			// TODO DARKO: poišči robne komponente

			// TODO ROK: poišči eulerjevo karakteristiko in klasificiraj ploskev
			int eulerCharacteristic = triangulation.getEulerCharacteristic();

			System.out.println("Orientation: " + orientable);
			System.out.println("Euler Characteristic: " + eulerCharacteristic + "\n");
		}
		catch (RuntimeException e)
		{
			System.out.println(e.getMessage());
		}
	}

	private static void parseData(String fileName)
	{
		// podvojeni trikotniki v XSurfaceSpaceStation.txt:
		// 6368 6367 6366
		// 6373 6374 6379
		// 6557 6546 6545
		// 6552 6551 6550
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
					triangulation.add(a, b, c);
				}
				else
				{
					throw new RuntimeException("Napačen format vrstice: \n\t" + str + "");
				}
			});
		}
		catch (IOException | NumberFormatException e)
		{
			throw new RuntimeException(e);
		}
	}
}
