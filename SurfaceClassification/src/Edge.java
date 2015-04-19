public class Edge
{
	private int a;
	private int b;

	public Edge(int a, int b)
	{
		if (b > a)
		{
			this.a = a;
			this.b = b;
		}
		else
		{
			this.b = a;
			this.a = b;
		}
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + a;
		result = prime * result + b;
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Edge other = (Edge) obj;
		if (a != other.a)
			return false;
		if (b != other.b)
			return false;
		return true;
	}

	@Override
	public String toString()
	{
		return "Edge " + a + "-" + b;
	}
}
