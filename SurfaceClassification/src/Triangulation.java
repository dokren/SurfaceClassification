import java.util.*;
import java.util.stream.Collectors;

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


	public int getNumberOfBoundaryComponents() {
		List<Edge> edgeList = getAllEdges();
		List<Edge> edg = new ArrayList<>();
		List<Edge> repeated = new ArrayList<>();
		int size = edgeList.size();
		boolean isUnique;

		for (int i = 0; i < size; i++) {
			Edge currentEdge = edgeList.get(i);
			isUnique = true;
			for (int j = i + 1; j < size; j++) {
				if (currentEdge.equals(edgeList.get(j))) {
					isUnique = false;
					repeated.add(currentEdge);
				}
			}
			if (isUnique && !repeated.contains(currentEdge)) {
				edg.add(currentEdge);
			}
		}

		Collections.sort(edg);

		int c=0;
		boolean isOneCycle = false;
		List<Edge> visited = new ArrayList<>();
		for (int k =0; k < edg.size();k++) {
			if (isOneCycle) {
				break;
			}
			if ((k < edg.size() - 1) && edg.get(k).getA() == edg.get(k+1).getA()) {
				visited.add(edg.get(k));
				int[] cc = count(edg, edg.get(k), null, 0, visited);
				if (cc[1] == edg.size() - 1) {
					isOneCycle=true;
				}
				c += cc[0];
			}
		}
		return c;
	}


	public int[] count (List<Edge> edges, Edge ref, Edge next, int count, List<Edge> visited) {
		if (next != null)
			visited.add(next);
		if (next != null && ref.getA() == next.getA()) {
			if (count < 2) {
				return new int[] {0, count};
			}
			return new int[] {1, count};
		}
		if (next != null) {
			next = getNext(edges, next, visited);
		} else {
			next = getNext1(ref, visited, edges);
		}

		return count(edges, ref, next, count+1, visited);
	}

	// next edge is not yet known
	public Edge getNext1 (Edge ref, List<Edge> visited, List<Edge> edges) {
		Edge next;
		next = findNotEqual(edges, ref.getB(), 'b', ref, visited);
		if (next == null) {
			next = findNotEqual(edges, ref.getA(), 'b', ref, visited);
		}
		if (next == null) {
			next = findNotEqual(edges, ref.getB(), 'a', ref, visited);
		}

		if (next == null) {
			next = findNotEqual(edges, ref.getA(), 'a', ref, visited);
		}

		next = new Edge(next.getA(), next.getB());

		return next;
	}

	// next edge is already known
	public Edge getNext (List<Edge> edges, Edge next, List<Edge> visited) {
		Edge tmp = next;
		next = findNotEqual(edges, next.getB(), 'b', next, visited);
		if (next == null) {
			next = findNotEqual(edges, tmp.getA(), 'b', tmp, visited);
		}
		if (next == null) {
			next = findNotEqual(edges, tmp.getB(), 'a', tmp, visited);
		}
		if (next == null) {
			next = findNotEqual(edges, tmp.getA(), 'a', tmp, visited);
		}

		next = new Edge(next.getA(), next.getB());

		return next;
	}

	public Edge findNotEqual (List<Edge> list, int v, char point, Edge edge, List<Edge> visited) {
		if (point == 'a') {
			for (Edge e : list) {
				if (e.getA() == v && !edge.equals(e) && !visited.contains(e)) {
					return e;
				}
			}
		} else {
			for (Edge e : list) {
				if (e.getB() == v && !edge.equals(e) && !visited.contains(e)) {
					return e;
				}
			}
		}

		return null;
	}

	/**
	 * @return _all_ edges contained in the triangulation
	 */
	public List<Edge> getAllEdges() {
		List<Edge> edgeList = new ArrayList<>();
		triangles.forEach(triangle -> {
			edgeList.add(new Edge(triangle.getA(), triangle.getB()));
			edgeList.add(new Edge(triangle.getA(), triangle.getC()));
			edgeList.add(new Edge(triangle.getB(), triangle.getC()));
		});

		return edgeList;
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

	class ComparatorB implements Comparator<Edge> {
		@Override
		public int compare (Edge e1, Edge e2) {
			if (e1.getB() < e2.getB()) {
				return -1;
			} else if (e1.getB() == e2.getB()) {
				return 0;
			}
			return 1;
		}
	}
}
