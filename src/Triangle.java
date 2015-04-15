package naloga2;

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

	public Triangle(int a, int b, int c)
	{
		setPoints(a, b, c);
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

	/**
	 * ========================================================================
	 * Ta del pripada prvi točki naloge - iskanju Delaunayeve triangulacije
	 * ========================================================================
	 */

	public Integer getThirdPoint(Set<Integer> intersection)
	{
		Set<Integer> pointsSet = createPointsSet();
		pointsSet.removeAll(intersection);
		return new ArrayList<Integer>(pointsSet).get(0);
	}

	public Orientation checkOrientation(List<Point> points)
	{
		double det = 0;
		Point pointA = points.get(a);
		Point pointB = points.get(b);
		Point pointC = points.get(c);
		det += (pointB.getX() - pointA.getX()) * (pointC.getY() - pointA.getY());
		det -= (pointB.getY() - pointA.getY()) * (pointC.getX() - pointA.getX());
		if (det < 0)
		{
			return Orientation.CLOCKWISE;
		}
		else if (det > 0)
		{
			return Orientation.COUNER_CLOCKWISE;
		}
		return Orientation.COLINEAR;
	}

	public boolean isInCircumcircle(int d, List<Point> points)
	{
		double det = 0;
		Point pointA = points.get(a);
		Point pointB = points.get(b);
		Point pointC = points.get(c);
		Point pointD = points.get(d);

		double sqA = Math.pow(pointA.getX(), 2) - Math.pow(pointD.getX(), 2) + Math.pow(pointA.getY(), 2) - Math.pow(pointD.getY(), 2);
		double sqB = Math.pow(pointB.getX(), 2) - Math.pow(pointD.getX(), 2) + Math.pow(pointB.getY(), 2) - Math.pow(pointD.getY(), 2);
		double sqC = Math.pow(pointC.getX(), 2) - Math.pow(pointD.getX(), 2) + Math.pow(pointC.getY(), 2) - Math.pow(pointD.getY(), 2);

		det += (pointA.getX() - pointD.getX()) * (pointB.getY() - pointD.getY()) * sqC;
		det += (pointA.getY() - pointD.getY()) * sqB * (pointC.getX() - pointD.getX());
		det += sqA * (pointB.getX() - pointD.getX()) * (pointC.getY() - pointD.getY());

		det -= (pointC.getX() - pointD.getX()) * (pointB.getY() - pointD.getY()) * sqA;
		det -= (pointC.getY() - pointD.getY()) * sqB * (pointA.getX() - pointD.getX());
		det -= sqC * (pointB.getX() - pointD.getX()) * (pointA.getY() - pointD.getY());

		Orientation orientation = checkOrientation(points);
		if (orientation.equals(Orientation.COUNER_CLOCKWISE))
		{
			return det > 0;
		}
		else if (orientation.equals(Orientation.CLOCKWISE))
		{
			return det < 0;
		}
		return false;
	}

	/**
	 * =========================================================================
	 * Ta del pripada drugi točki naloge - preverjanju orientacije triangulacije
	 * =========================================================================
	 */

	public void setParentToNeighbours()
	{
		for (Triangle t : neighbours)
		{
			t.setParent(this);
		}
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

	public static enum Orientation
	{
		CLOCKWISE, COUNER_CLOCKWISE, COLINEAR
	}
}
