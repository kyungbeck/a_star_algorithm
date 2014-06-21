import java.io.*;
import java.util.Random;

public class astar {
	public static class Point
	{
		public int x;
		public int y;

		public Point parent;

		public int f;
		public int g;
		public int h;

		public Point(){}

		public Point(int x, int y)
		{
			this.x = x;
			this.y = y;
		}
	}

	public static class pQueueNode
	{
		public Point p = new Point();
		public pQueueNode next;

		public pQueueNode(){}

		public pQueueNode(Point p)
		{
			this.p = p;
		}
	}

	// Heap으로 바꾸기
	public static class pQueue
	{
		private pQueueNode head = new pQueueNode();

		public boolean isEmpty()
		{
			return head.next == null;
		}

		public boolean isContain(int x, int y) // Point (x, y)
		{
			pQueueNode t = head;
			while (true)
			{
				if (t.next == null)
					return false;
				t = t.next;
				if (t.p.x == x && t.p.y == y)
					return true;
			}
		}

		public void put(Point p)
		{
			if (this.isContain(p.x, p.y))
			{
				System.out.println("ERROR: Point Already Exists in pQueue");
				return;
			}

			pQueueNode t = head;
			while (true)
			{
				if (t.next == null || t.next.p.f > p.f)
				{
					pQueueNode n = new pQueueNode(p);
					n.next = t.next;
					t.next = n;
					return;
				}
				else t = t.next;
			}
		}

		public Point pop()
		{
			if (head.next == null)
				return null;
			else
			{
				Point rp = head.next.p;
				head.next = head.next.next;
				return rp;
			}
		}

		public Point pick(int x, int y)
		{
			pQueueNode t = head;
			while (true)
			{
				if (t.next == null)
					return null;
				if (t.next.p.x == x && t.next.p.y == y)
				{
					Point rp = t.next.p;
					t.next = t.next.next;
					return rp;
				}
				t = t.next;
			}
		}

		public void print()
		{
			pQueueNode t = head;
			while (true)
			{
				if (t.next == null)
					return;
				t = t.next;
				Point pp = t.p;
				System.out.println("(" + pp.x + ", " + pp.y + ") " + pp.f + "=" + pp.g + "+" + pp.h);
			}
		}
	}

	public static void main(String args[])
	{
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		int row, column;
		double possibility;
		while (true)
		{
			try
			{
				String input = br.readLine();

				if (input.compareTo("quit") == 0)
				{
					System.out.println("Bye");
					return;
				}

				String s[] = input.split(" ");
				if (s.length != 3)
				{
					System.out.println("Three inputs must be inserted.");
					System.out.println("row(int), column(int), possibility of being an obstacle(double)");
					continue;
				}
				else
				{
					row = Integer.parseInt(s[0]);
					column = Integer.parseInt(s[1]);
					possibility = Double.parseDouble(s[2]);
					command(row, column, possibility);
				}
			}
			catch (Exception e)
			{
				System.out.println("입력이 잘못되었습니다. 오류 : " + e.toString());
			}
		}
	}

	public static void command(int row, int column, double possibility)
	{
		int[][] map = new int[row][column];
		boolean isLarge = (row * column) > 900;

		int i, j;
		Random r = new Random();
		int rangeBoonza = (int)(possibility * 10000.0);
		int ranNum;

		int obsCount = 0;
		double obsRate;
		for (i = 0; i < row; i++)
		{
			for (j = 0; j < column; j++)
			{
				ranNum = r.nextInt(10000);
				if (ranNum < rangeBoonza)
				{
					map[i][j] = 0;
					obsCount++;
				}
				else map[i][j] = 1;
			}
		}
		obsRate = (double)obsCount / (double)(row * column);

		Point start = new Point();
		Point dest = new Point();

		start.x = 0;
		start.y = 0;
		dest.x = row - 1;
		dest.y = column - 1;

		map[start.x][start.y] = 2;
		map[dest.x][dest.y] = 3;

		if (isLarge)
			System.out.println("Map (" + row + " * " + column + ") construction complete");

		else
		{
			for (j = 0; j < column + 2; j++)
				System.out.print('.');
			System.out.println();
			for (i = 0; i < row; i++)
			{
				System.out.print('|');
				for (j = 0; j < column; j++)
				{
					switch (map[i][j])
					{
					case 0:
						System.out.print('@');
						break;
					case 1:
						System.out.print(' ');
						break;
					case 2:
						System.out.print('S');
						break;
					case 3:
						System.out.print('D');
						break;
					}
				}
				System.out.println('|');
			}
			for (j = 0; j < column + 2; j++)
				System.out.print('\'');
		}
		System.out.println();
		System.out.println("Obstacles: " + obsCount + ", Obstacles/Entire Nodes: " + obsRate);


		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		while (true)
		{
			try
			{
				br.readLine();
				break;
			}
			catch (Exception e)
			{
				System.out.println("입력이 잘못되었습니다. 오류 : " + e.toString());
			}
		}

		long t = System.currentTimeMillis();

		start.g = 0;
		start.h = (Math.abs(dest.x - start.x) + Math.abs(dest.y - start.y)) * 10;
		start.f = start.h;

		pQueue openList = new pQueue();
		pQueue closedList = new pQueue(); // pop(): not be required

		openList.put(start);

		boolean pathExist = false;
		Point pathFind = new Point();

		while (true)
		{
			Point p = openList.pop();
			if (p == null)
			{
				System.out.println("ERROR: Empty pQueue");
				return;
			}

			closedList.put(p);

			int x = p.x;
			int y = p.y;

			int searchX, searchY;

			int[][] searchID = new int[8][2];
			searchID[0][0] = x-1;
			searchID[0][1] = y;

			searchID[1][0] = x;
			searchID[1][1] = y+1;

			searchID[2][0] = x+1;
			searchID[2][1] = y;

			searchID[3][0] = x;
			searchID[3][1] = y-1;

			searchID[4][0] = x-1;
			searchID[4][1] = y-1;

			searchID[5][0] = x-1;
			searchID[5][1] = y+1;

			searchID[6][0] = x+1;
			searchID[6][1] = y+1;

			searchID[7][0] = x+1;
			searchID[7][1] = y-1;


			for (i = 0; i < 8; i++)
			{
				searchX = searchID[i][0];
				searchY = searchID[i][1];

				if (searchX < 0 || searchY < 0 || searchX >= row || searchY >= column)
					continue;

				if (map[searchX][searchY] == 0 || closedList.isContain(searchX, searchY))
					continue;

				if (i > 3)
				{
					if (i == 4)
					{
						if (map[searchX+1][searchY] == 0 || map[searchX][searchY+1] == 0)
							continue;
					}
					else if (i == 5)
					{		
						if (map[searchX+1][searchY] == 0 || map[searchX][searchY-1] == 0)
							continue;
					}
					else if (i == 6)
					{
						if (map[searchX-1][searchY] == 0 || map[searchX][searchY-1] == 0)
							continue;
					}
					else //if (i == 7)
					{
						if (map[searchX-1][searchY] == 0 || map[searchX][searchY+1] == 0)
							continue;
					}
				}

				int plusG;
				if (i < 3)
					plusG = 10;
				else plusG = 14;

				if (map[searchX][searchY] == 3) // destination
				{
					pathExist = true;	
					Point tempP = new Point(searchX, searchY);
					tempP.g = p.g + plusG;
					tempP.h = 0;
					tempP.f = tempP.g;
					tempP.parent = p;
					pathFind = tempP;
					break;
				}

				else if (openList.isContain(searchX, searchY))
				{
					Point tempP = openList.pick(searchX, searchY);

					if (p.g + plusG < tempP.g)
					{
						tempP.g = p.g + plusG;
						tempP.f = tempP.g + tempP.h;
						tempP.parent = p;
						openList.put(tempP);
					}
					else openList.put(tempP);
				}
				else
				{
					Point tempP = new Point(searchX, searchY);
					tempP.g = p.g + plusG;
					tempP.h = (Math.abs(dest.x - searchX) + Math.abs(dest.y - searchY)) * 10;
					tempP.f = tempP.g + tempP.h;
					tempP.parent = p;
					openList.put(tempP);
				}
			}

			if (pathExist || openList.isEmpty())
				break;
		}

		if (!pathExist)
		{
			System.out.println("No path exists.");
			if (isLarge)
				System.out.println("A* algorithm took " + (System.currentTimeMillis() - t) + " ms");
			System.out.println();
			System.out.println();
			return;
		}

		while (true)
		{
			pathFind = pathFind.parent;
			if (pathFind.x == start.x && pathFind.y == start.y)
				break;
			map[pathFind.x][pathFind.y] = 4;
		}

		if (isLarge)
		{
			System.out.println("Path is found successfully.");
			System.out.println("A* algorithm took " + (System.currentTimeMillis() - t) + " ms");
		}

		else
		{
			for (j = 0; j < column + 2; j++)
				System.out.print('.');
			System.out.println();
			for (i = 0; i < row; i++)
			{
				System.out.print('|');
				for (j = 0; j < column; j++)
				{
					switch (map[i][j])
					{
					case 0:
						System.out.print('@');
						break;
					case 1:
						System.out.print(' ');
						break;
					case 2:
						System.out.print('S');
						break;
					case 3:
						System.out.print('D');
						break;
					case 4:
						System.out.print('O');
						break;
					}
				}
				System.out.println('|');
			}
			for (j = 0; j < column + 2; j++)
				System.out.print('\'');
		}
		System.out.println();
		System.out.println();
	}
}
