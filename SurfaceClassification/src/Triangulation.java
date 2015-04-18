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

	public List<Triangle> getTriangles()
	{
		return triangles;
	}

	public void add(Triangle t)
	{
		triangles.add(t);
		points.add(t.getA());
		points.add(t.getB());
		points.add(t.getC());
	}

	private void findNeighbours()
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
					throw new SurfaceClassificationException("Podana sta dva identična trikotnika: " + t1 + " in " + t2);
				}
			}
		}
	}

	public boolean isOrientable()
	{
		findNeighbours();
		boolean isOrientable = true;

		Triangle firstTriangle = triangles.get(0);
		firstTriangle.setOrientation(true);
		firstTriangle.setParentToNeighbours();

		queue.addAll(firstTriangle.getNeighbours());

		while (!queue.isEmpty())
		{
			Triangle t = queue.pop();
			Triangle parent = t.getParent();
			boolean triangleAlreadyChecked = false;
			boolean equalDataOrientation = t.isDataOrientationEqual(parent);
			// trenutni trikotnik še nima nastavljene orientacije
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
				// če je pravilno
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
		return true;
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
