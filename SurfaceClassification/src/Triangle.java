import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class Triangle
{
	private int a;
	private int b;
	private int c;
	private Boolean orientation;
	private List<Triangle> neighbours = new ArrayList<Triangle>(3);
	private Triangle parent;
	private boolean visited;

	public Triangle(int a, int b, int c)
	{
		setPoints(a, b, c);
		visited = false;
	}

	public boolean isVisited()
	{
		return visited;
	}

	public void setVisited(boolean visited)
	{
		this.visited = visited;
	}

	public void setPoints(int a, int b, int c)
	{
		this.a = a;
		this.b = b;
		this.c = c;
	}

	public Set<Integer> createPointsSet()
	{
		Set<Integer> pointsSet = new LinkedHashSet<Integer>(3);
		pointsSet.add(a);
		pointsSet.add(b);
		pointsSet.add(c);
		return pointsSet;
	}

	public void setParentToNeighbours()
	{
		for (Triangle t : neighbours)
		{
			t.setParent(this);
		}
	}

	public boolean containsEdge(Edge e)
	{
		return (e.getA() == a && (e.getB() == b || e.getB() == c)) || (e.getA() == b && (e.getB() == a || e.getB() == c)) || (e.getA() == c && (e.getB() == a || e.getB() == b));
	}

	public boolean isDataOrientationEqual(Triangle t)
	{
		// iskanje enakega zaporedja podniza
		// algoritmično bi bilo bolj zapleteno
		if (a == t.getA())
		{
			if (b == t.getB())
			{
				return false;
			}
			if (c == t.getC())
			{
				return false;
			}
		}
		if (a == t.getB())
		{
			if (b == t.getC())
			{
				return false;
			}
			if (c == t.getA())
			{
				return false;
			}
		}
		if (a == t.getC())
		{
			if (b == t.getA())
			{
				return false;
			}
			if (c == t.getB())
			{
				return false;
			}
		}
		if (b == t.getA() && c == t.getB())
		{
			return false;
		}
		if (b == t.getB() && c == t.getC())
		{
			return false;
		}
		if (b == t.getC() && c == t.getA())
		{
			return false;
		}
		return true;
	}

	/**
	 * ========================================================================
	 * Getterji in setterji
	 * ========================================================================
	 */

	public Triangle getParent()
	{
		return parent;
	}

	public void setParent(Triangle parent)
	{
		this.parent = parent;
	}

	public int getA()
	{
		return a;
	}

	public void setA(int a)
	{
		this.a = a;
	}

	public int getB()
	{
		return b;
	}

	public void setB(int b)
	{
		this.b = b;
	}

	public int getC()
	{
		return c;
	}

	public void setC(int c)
	{
		this.c = c;
	}

	public Boolean getOrientation()
	{
		return orientation;
	}

	public void setOrientation(Boolean orientation)
	{
		this.orientation = orientation;
	}

	public List<Triangle> getNeighbours()
	{
		return neighbours;
	}

	public void addNeighbour(Triangle t)
	{
		neighbours.add(t);
	}

	@Override
	public String toString()
	{
		return "Trikotnik: (" + a + ", " + b + ", " + c + ")";
	}
}
