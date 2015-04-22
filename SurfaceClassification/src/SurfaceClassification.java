import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class SurfaceClassification
{
	private static Triangulation triangulation;

	public static void main(String[] args)
	{
		File[] listFiles = new File("./data").listFiles();
		for (File file : listFiles)
		{
			String fileName = file.getAbsolutePath();
			if (fileName.endsWith("txt"))
			{
				classifySurface(fileName, file.getName());
			}
		}
	}

	private static void classifySurface(String filePath, String fileName)
	{
		try
		{
			parseData(filePath);
			System.out.print("Klasifikacija triangulacije iz datoteke " + fileName + "\n\t");
			triangulation.findNeighbours();
			if (!triangulation.isSurface())
			{
				System.out.print("\tPodana triangulacija ne predstavlja ploskve\n\n");
				return;
			}

			boolean orientable = triangulation.isOrientable();

			int eulerCharacteristic = triangulation.getEulerCharacteristic();
			int numberOfBoundaryComponents = triangulation.getNumberOfBoundaryComponents();
			int realEuler = eulerCharacteristic + numberOfBoundaryComponents;

			if (realEuler == 2)
			{
				System.out.print("Podana ploskev je sfera s/z " + numberOfBoundaryComponents + " luknjami\n\n");
			}
			else
			{
				if (orientable)
				{
					int n = (2 - realEuler) / 2;
					System.out.print("Podana ploskev je " + n + " torusov s/z " + numberOfBoundaryComponents + " luknjami\n\n");
				}
				else
				{
					int n = 2 - realEuler;
					System.out.print("Podana ploskev je " + n + " projektivnih ravnin s/z " + numberOfBoundaryComponents + " luknjami\n\n");
				}
			}
		}
		catch (RuntimeException e)
		{
			System.out.println(e.getMessage());
		}
	}

	private static void parseData(String fileName)
	{
		// podvojeni trikotniki v XSurfaceSpaceStation:
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
