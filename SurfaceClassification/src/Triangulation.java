import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class Triangulation
{
	private List<Triangle> triangles = new ArrayList<Triangle>();
	private LinkedList<Triangle> queue = new LinkedList<Triangle>();
	private Set<Integer> points = new HashSet<Integer>();
	private Set<Edge> edges = new HashSet<Edge>();

	public List<Triangle> getTriangles()
	{
		return triangles;
	}

	public void add(int a, int b, int c)
	{
		triangles.add(new Triangle(a, b, c));

		points.add(a);
		points.add(b);
		points.add(c);

		edges.add(new Edge(a, b));
		edges.add(new Edge(a, c));
		edges.add(new Edge(b, c));
	}

	public int getEulerCharacteristic()
	{
		return points.size() - edges.size() + triangles.size();
	}

	public void findNeighbours()
	{
		for (int i = 0; i < triangles.size(); i++)
		{
			Triangle t1 = triangles.get(i);
			for (int j = i + 1; j < triangles.size(); j++)
			{
				Triangle t2 = triangles.get(j);
				Set<Integer> pointsT1 = t1.createPointsSet();
				Set<Integer> pointsT2 = t2.createPointsSet();

				LinkedHashSet<Integer> intersection = new LinkedHashSet<Integer>(pointsT1);
				intersection.retainAll(pointsT2);
				if (intersection.size() == 2)
				{
					t1.addNeighbour(t2);
					t2.addNeighbour(t1);
				}
				if (intersection.size() == 3)
				{
					throw new RuntimeException("Podana sta dva identiÄŤna trikotnika: " + t1 + " in " + t2);
				}
			}
		}
	}

	public boolean isOrientable()
	{
		boolean isOrientable = true;

		Triangle firstTriangle = triangles.get(0);
		firstTriangle.setOrientation(true);
		firstTriangle.setParentToNeighbours();

		queue.clear();
		queue.addAll(firstTriangle.getNeighbours());

		while (!queue.isEmpty())
		{
			Triangle t = queue.pop();
			Triangle parent = t.getParent();
			boolean triangleAlreadyChecked = false;
			boolean equalDataOrientation = t.isDataOrientationEqual(parent);
			// trenutni trikotnik Ĺˇe nima nastavljene orientacije
			if (t.getOrientation() == null)
			{
				if (!equalDataOrientation)
				{
					t.setOrientation(!parent.getOrientation().booleanValue());
				}
				else
				{
					t.setOrientation(parent.getOrientation().booleanValue());
				}
			}
			else
			{
				// trenutni trikotnik ima nastavljeno orientacijo, preverimo
				// ÄŤe je pravilno
				boolean orientationCorrect;
				if (!equalDataOrientation)
				{
					orientationCorrect = t.getOrientation().booleanValue() != parent.getOrientation().booleanValue();
				}
				else
				{
					orientationCorrect = t.getOrientation().booleanValue() == parent.getOrientation().booleanValue();
				}

				if (orientationCorrect)
				{
					triangleAlreadyChecked = true;
				}
				else
				{
					isOrientable = false;
					break;
				}
			}

			if (!triangleAlreadyChecked)
			{
				t.setParentToNeighbours();
				queue.addAll(t.getNeighbours());
			}
		}
		return isOrientable;
	}

	public boolean isSurface()
	{
		int neighbours = 0;
		for (Edge edge : edges)
		{
			neighbours = 0;
			for (Triangle triangle : triangles)
			{
				if (triangle.containsEdge(edge))
				{
					neighbours++;
				}
			}
			if (!(neighbours == 1 || neighbours == 2))
			{
				return false;
			}
		}

		queue.clear();
		queue.add(triangles.get(0));

		int visited = 0;
		while (!queue.isEmpty())
		{
			Triangle t = queue.pop();
			if (t.isVisited())
			{
				continue;
			}
			t.setVisited(true);
			visited++;
			queue.addAll(t.getNeighbours());
		}

		if (!(visited == triangles.size()))
		{
			System.out.println("Triangulacija ima več komponent!");
			return false;
		}

		return true;
	}

	public int getEdgeComponents()
	{
		// TODO DARKO: poišči robne komponente
		return 0;
	}

	@Override
	public String toString()
	{
		String toString = "";
		for (Triangle t : triangles)
		{
			toString += "\t\t" + t + "\n";
		}
		return toString;
	}
}
