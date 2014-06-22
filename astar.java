import java.io.*;
import java.util.Random;

public class astar {
	public static class Point
	{	// each point of the map
		public int x;			// location of the point
		public int y;

		public Point parent;	// 

		public double f;		// f, g, h value
		public double g;
		public double h;

		public Point(){}

		public Point(int x, int y)
		{
			this.x = x;
			this.y = y;
		}
	}

	public static class pQueueNode
	{	// priority queue's element
		public Point p = new Point();	// it has a point, next element
		public pQueueNode next;

		public pQueueNode(){}

		public pQueueNode(Point p)
		{
			this.p = p;
		}
	}

	public static class pQueue
	{	// priority queue of points (priority: f value)
		private pQueueNode head = new pQueueNode();	// it has one head whose element is null

		public boolean isEmpty()
		{	// is this queue empty?
			return head.next == null;
		}

		public boolean contains(int x, int y)
		{	// does this queue contain point (x, y)?
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
		{	// insert point p
			if (this.contains(p.x, p.y))
			{
				System.out.println("ERROR: Point Already Exists in pQueue");
				return;
			}

			pQueueNode t = head;
			while (true)
			{
				if (t.next == null || t.next.p.f > p.f) // if reached right location
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
		{	// pop the first point
			if (head.next == null)
				return null;
			else
			{
				Point rp = head.next.p;
				head.next = head.next.next;	// delete the return point
				return rp;
			}
		}

		public Point pick(int x, int y)
		{	// pick point (x, y)
			pQueueNode t = head;
			while (true)	// search point (x, y)
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
	}

	/* main */
	public static void main(String args[])
	{
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		int row, column;	// length of row of the map, length of column of the map
		double possibility;	// possibility of that each point is an obstacle 
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

		int obsCount = 0;	// total number of obstacles
		double obsRate;		// rate of obstacles

		for (i = 0; i < row; i++) // random map construction (0: obstacle, 1: plain)
		{
			for (j = 0; j < column; j++)
			{
				ranNum = r.nextInt(10000);
				if (ranNum < rangeBoonza)
				{
					map[i][j] = 0;
					obsCount++;	// accumulate obsCount
				}
				else map[i][j] = 1;
			}
		}

		Point start = new Point();	// start point
		Point dest = new Point();	// destination point

		/* randomly spotting start and destination points */
		int startRanInt, destRanInt;

		startRanInt = r.nextInt(row * column);
		while (true)
		{
			destRanInt = r.nextInt(row * column);
			if (startRanInt != destRanInt)
				break;
		}
		start.x = startRanInt / column;
		start.y = startRanInt % column;
		dest.x = destRanInt / column;
		dest.y = destRanInt % column;

		if (map[start.x][start.y] == 0)
			obsCount--;
		if (map[dest.x][dest.y] == 0)
			obsCount--;

		map[start.x][start.y] = 2;
		map[dest.x][dest.y] = 3;

		obsRate = (double)obsCount / (double)(row * column);

		if (isLarge)	// if the map is too large to be shown in console
			System.out.println("Map (" + row + " * " + column + ") construction complete");

		else			// else show the entire map information
		{				// @: obstacle, (blank): plain, S: start, D: destination
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

		boolean oneStep = false;	// if '1' entered, show the map step by step
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		while (true)
		{
			try
			{
				if (isLarge)	// if the map is large, prohibit showing the map step by step 
				{
					br.readLine();
					break;
				}
				else
				{
					if (br.readLine().compareTo("1") == 0)
						oneStep = true;
					break;
				}
			}
			catch (Exception e)
			{
				System.out.println("입력이 잘못되었습니다. 오류 : " + e.toString());
			}
		}

		long t = System.currentTimeMillis();

		/* preprocessing of A* algorithm and start point process*/
		start.g = 0.0;
		start.h = (Math.abs(dest.x - start.x) + Math.abs(dest.y - start.y));
		start.f = start.h;

		pQueue openList = new pQueue();
		pQueue closedList = new pQueue(); // pop(): not be required

		openList.put(start);

		boolean pathExist = false;
		Point pathFind = new Point();

		while (true)	// major loop of A* algorithm
		{
			Point p = openList.pop();	// chosen point
			if (p == null)
			{
				System.out.println("ERROR: Empty pQueue");
				return;
			}

			closedList.put(p);

			int x = p.x;
			int y = p.y;
			if (map[x][y] != 2)
				map[x][y] = 5;

			int searchX, searchY;

			int[][] searchID = new int[8][2];	// locations of to-be-checked points
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

			for (i = 0; i < 8; i++)	// check each of to-be-checked points
			{
				searchX = searchID[i][0];
				searchY = searchID[i][1];

				if (searchX < 0 || searchY < 0 || searchX >= row || searchY >= column)	// is out of bound
					continue;

				if (map[searchX][searchY] == 0 || closedList.contains(searchX, searchY)) // is obstacle or contained by closedList
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

				double plusG;	// increasing amount of g value
				if (i <= 3)
					plusG = 1.0;
				else plusG = 1.414;

				if (map[searchX][searchY] == 3) // is destination
				{
					pathExist = true;	// mark that found the shortest path
					Point tempP = new Point(searchX, searchY);
					tempP.g = p.g + plusG;
					tempP.h = 0.0;
					tempP.f = tempP.g;
					tempP.parent = p;
					pathFind = tempP;
					break;	// and escape this for loop
				}

				else if (openList.contains(searchX, searchY))	// is contained by openList
				{
					Point tempP = openList.pick(searchX, searchY);

					if (p.g + plusG < tempP.g)
					{	// update
						tempP.g = p.g + plusG;
						tempP.f = tempP.g + tempP.h;
						tempP.parent = p;
						openList.put(tempP);
					}
					else openList.put(tempP);
					map[searchX][searchY] = 6;
				}

				else	// is newly detected plain point
				{
					Point tempP = new Point(searchX, searchY);
					tempP.g = p.g + plusG;
					tempP.h = (Math.abs(dest.x - searchX) + Math.abs(dest.y - searchY));
					tempP.f = tempP.g + tempP.h;
					tempP.parent = p;
					openList.put(tempP);
					map[searchX][searchY] = 6;
				}
			}

			if (pathExist || openList.isEmpty())	// pathfinding finished or no path exists
				break;

			if (oneStep)	// print the map step by step
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
							System.out.print('P');
							break;
						case 5:
							System.out.print('C');
							break;
						case 6:
							System.out.print('O');
							break;
						}
					}
					System.out.println('|');
				}
				for (j = 0; j < column + 2; j++)
					System.out.print('\'');
				System.out.println();

				BufferedReader br2 = new BufferedReader(new InputStreamReader(System.in));
				while (true)
				{
					try
					{
						if (br2.readLine().compareTo("1") == 0)	// enter "1"
							oneStep = true;
						else oneStep = false;	// enter any word(inclusive "") that is not "1"
						break;
					}
					catch (Exception e)
					{
						System.out.println("입력이 잘못되었습니다. 오류 : " + e.toString());
					}
				}
			}
		}

		if (!pathExist)
		{
			System.out.println("No path exists.");
			if (isLarge)
				System.out.println("A* algorithm took " + (System.currentTimeMillis() - t) + " ms");
			System.out.println();
			System.out.println();
			System.out.println("===========================================");
			return;
		}

		while (true)	// reversely tracking parent points
		{
			pathFind = pathFind.parent;
			if (pathFind.x == start.x && pathFind.y == start.y)
				break;
			map[pathFind.x][pathFind.y] = 4;
		}

		if (isLarge)	// not show the entire map but notify the execution time
		{
			System.out.println("Path is found successfully.");
			System.out.println("A* algorithm took " + (System.currentTimeMillis() - t) + " ms");
		}

		else	// show the entire map, not notify the execution time
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
					case 5:
					case 6:
						System.out.print(' ');
						break;
					case 2:
						System.out.print('S');
						break;
					case 3:
						System.out.print('D');
						break;
					case 4:
						System.out.print('P');
						break;
					}
				}
				System.out.println('|');
			}
			for (j = 0; j < column + 2; j++)
				System.out.print('\'');
		}
		System.out.println();
		System.out.println("Path finding finished");
		System.out.println();
		System.out.println("===========================================");
	}
}
