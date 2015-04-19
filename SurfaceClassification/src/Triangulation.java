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
		int h = countHoles(edg);
		return h;
	}

	public int countHoles (List<Edge> edges) {
		if (edges.size() < 3) {
			return 0;
		}
		Edge first = edges.get(0);
		Edge second = edges.get(1);
		if (first.getA() != second.getA()) {
			return 0;
		}
		// find last two the same
		// sort by B point

		Collections.sort(edges, new ComparatorB());

		Edge last = null;
		Edge beforelast = null;
		boolean found = false;

		int holes = 0;
		for (int i = 1; i < edges.size(); i+=2) {
			found = false;
			last = edges.get(i);
			if (beforelast != null && last.getB() == beforelast.getB()) {
				// we hit end, try to get to first and second from these
				found = goBack(edges, first, second, last, beforelast, false, false);
			}
			if (i < edges.size() -1) {
				beforelast = edges.get(i + 1);
			}

			if (beforelast != null && last.getB() == beforelast.getB()) {
				// we hit end, try to get to first and second from these
				found = goBack(edges, first, second, last, beforelast, false, false);
			}
			if (found) {
				holes++;
			}

		}

//		Collections.sort(edges);
		return holes + countHoles(edges.subList(edges.indexOf(first), edges.indexOf(last)));
	}

	public boolean goBack (List<Edge> edges, Edge first, Edge second, Edge last, Edge beforelast, boolean firstOk, boolean secondOk) {
		if (last.getA() == first.getA() || beforelast.getA() == first.getA()) {
			firstOk = true;
		}
		if (last.getA() == second.getA() || beforelast.getA() == second.getA() ) {
			secondOk = true;
		}
		// find previous
		if (firstOk && secondOk) {
			return true;
		} else {
			if (secondOk) {
				last = find(edges, last.getA(), 'b');
				if (last != null)
					return goBack(edges, first, second, last, beforelast, firstOk, secondOk);
			} else if (firstOk) {
				beforelast = find(edges, beforelast.getA(), 'b');
				if (beforelast != null)
					return goBack(edges, first, second, last, beforelast, firstOk, secondOk);
			} else {
				last = find(edges, last.getA(), 'b');
				beforelast = find(edges, beforelast.getA(), 'b');
				if (last != null && beforelast != null)
					return goBack(edges, first, second, last, beforelast, firstOk, secondOk);
			}
		}
		return firstOk && secondOk;
	}

	public Edge find (List<Edge> list, int v, char point) {


		if (point == 'a') {
			for (Edge e : list) {
				if (e.getA() == v) {


					return e;
				}
			}
		} else {
			for (Edge e : list) {
				if (e.getB() == v) {

					return e;
				}
			}
		}

		return null;
	}

	public int getNumberOfTriangles (List<Edge> edges) {
		Edge[] e = new Edge[edges.size()];
		List<Edge[]> potentialTriangles = new ArrayList<>();
		potentialTriangles = kcomb(potentialTriangles, edges.toArray(e), 0, 3, new Edge[3]);

		potentialTriangles = potentialTriangles.stream()
				.filter(triangle -> isTriangle(triangle))
				.collect(Collectors.toList());

		return potentialTriangles.size();
	}

	/**
	 * @param edges array of 3 Edges
	 * @return true if they represent a triangle
	 */
	public boolean isTriangle(Edge[] edges) {
		Arrays.sort(edges);
		return (edges[0].getA() == edges[1].getA() && edges[0].getB() == edges[2].getA() &&
					edges[1].getB() == edges[2].getB());
	}

	// http://stackoverflow.com/questions/2599499/k-combinations-of-a-set-of-integers-in-ascending-size-order
	/**
	 * @param potential
	 * @param items
	 * @param n
	 * @param k
	 * @param arr
	 * @return 3-combinations of potential triangles
	 */
	public List<Edge[]> kcomb(List<Edge[]> potential, Edge[] items, int n, int k, Edge[] arr) {
		if (k == 0) {
			Edge[] e = new Edge[arr.length];
			e[0] = new Edge(arr[0].getA(), arr[0].getB());
			e[1] = new Edge(arr[1].getA(), arr[1].getB());
			e[2] = new Edge(arr[2].getA(), arr[2].getB());
			potential.add(e);
			return potential;
		} else {
			for (int i = n; i <= items.length - k; i++) {
				arr[arr.length - k] = items[i];
				kcomb(potential, items, i + 1, k - 1, arr);
			}
			return potential;
		}
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
