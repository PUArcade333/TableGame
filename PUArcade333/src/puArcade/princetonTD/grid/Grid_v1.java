/*
  Copyright (C) 2010 Aurelien Da Campo

  This program is free software; you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation; either version 3 of the License, or
  (at your option) any later version.

  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
  GNU General Public License for more details.

  You should have received a copy of the GNU General Public License
  along with this program; if not, write to the Free Software
  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
*/

/*
  Unless stated otherwise, all code below is from said above open 
  source project. Code variables have been translated from French to
  English to facilitate development. Everything else has been left intact
  from the original source.
  
  Modified portions are further commented detailing changes made.
*/

package puArcade.princetonTD.grid;

import java.util.ArrayList;
import java.util.Iterator;

import org.jgrapht.GraphPath;
import org.jgrapht.Graphs;
import org.jgrapht.alg.DijkstraShortestPath;
import org.jgrapht.graph.SimpleWeightedGraph;

import puArcade.princetonTD.exceptions.PathNotFoundException;


import android.graphics.Point;
import android.graphics.Rect;

public class Grid_v1 implements Grid {

	private final int NNODES;

	// node width
	private final int NODE_WIDTH;
	// arc weight
	private final int ARC_WEIGHT;
	// half distance between nodes
	private final int HALF_DIST;
	// width of grid
	private final int WIDTH;
	// height of grid
	private final int HEIGHT;
	// node dimensions
	private final int X_NODES, Y_NODES;

	// Attributes

	// Graph
	private SimpleWeightedGraph<Node, Arc> graph = new SimpleWeightedGraph<Node, Arc>(new ArcGenerator());
	// node matrix
	private Node[][] nodes;

	private int xOffset, yOffset;

	// Constructor
	public Grid_v1(final int pixelWidth, final int pixelHeight,
			final int nodeWidth, int xOffset, int yOffset)
					throws IllegalArgumentException
					{
		testInt(nodeWidth);
		testInt(pixelWidth);
		testInt(pixelHeight);

		NODE_WIDTH = nodeWidth;

		ARC_WEIGHT = (int) Math.sqrt(2 * NODE_WIDTH * NODE_WIDTH);

		HALF_DIST = NODE_WIDTH / 2;

		WIDTH = pixelWidth;
		HEIGHT = pixelHeight;

		X_NODES = (pixelWidth / NODE_WIDTH);
		Y_NODES = (pixelHeight / NODE_WIDTH);

		NNODES = X_NODES * Y_NODES;


		this.xOffset = xOffset;
		this.yOffset = yOffset;

		nodes = new Node[X_NODES][Y_NODES];

		constructGraph();
					}

	public Grid_v1(final int pixelWidth, final int pixelHeight,
			final int nodeWidth) throws IllegalArgumentException
			{
		this(pixelWidth, pixelHeight, nodeWidth, 0, 0);
			}

	public synchronized ArrayList<Point> shortestPath(int xStart, int yStart, int xEnd, int yEnd)
			throws PathNotFoundException, IllegalArgumentException
			{
		if (xStart >= WIDTH-xOffset || xEnd >= WIDTH-xOffset
				|| xStart-xOffset < 0 || xEnd-xOffset < 0)
			throw new IllegalArgumentException("Invalid x value");

		if (yStart-yOffset >= HEIGHT || yEnd-yOffset >= HEIGHT
				|| yStart-yOffset < 0 || yEnd-yOffset < 0)
			throw new IllegalArgumentException("Invalid y value");


		GraphPath<Node, Arc> dijkstraPath;
		try
		{
			dijkstraPath = (new DijkstraShortestPath<Node, Arc>(
					graph,
					nodeWithPoint(xStart - xOffset, yStart - yOffset),
					nodeWithPoint(xEnd - xOffset, yEnd - yOffset))).getPath();
		} catch (IllegalArgumentException e)
		{
			throw new PathNotFoundException("Not valid path");
		}

		if (dijkstraPath == null)
			throw new PathNotFoundException("No path exist");

		return new ArrayList<Point>(Graphs.getPathVertexList(dijkstraPath));
			}



	public void activateZone(Rect rect, boolean update)
			throws IllegalArgumentException {
		zoneActive(rect, true);
	}

	public void deactivateZone(Rect rect, boolean update)
			throws IllegalArgumentException {
		zoneActive(rect, false);

	}

	public int getWidth() {
		return WIDTH;
	}

	public int getHeight() {
		return HEIGHT;
	}
	
	public double getPathLength(ArrayList<Point> path) {
		double length = 0.0;

		Point p;
		Iterator<Point> i = path.iterator();

		if(i.hasNext())
			p = i.next();
		else
			return 0.0;

		Point next;
		while(i.hasNext())
		{
			next = i.next();
			length += Math.sqrt((p.x-next.x)*(p.x-next.x)+(p.y-next.y)*(p.y-next.y));
			p = next;
		}

		return length;
	}

	public Node[] getNodes() {
		Node[] temp = new Node[NNODES];

		int i = 0;
		for (Node[] row : nodes)
			for (Node node : row)
				temp[i++] = new Node(node);

		return temp;
	}

	private void zoneActive(final Rect rect, final boolean active)
			throws IllegalArgumentException
			{
		rectInGrid(rect);

		for (Node[] row : nodes)
			for (Node node : row)
			{
				if (rect.intersects(node.x - HALF_DIST, node.y - HALF_DIST, node.x + HALF_DIST, node.y + HALF_DIST))
					if (active)
						activate(node);
					else if (node.isActive())
						deactivate(node);
			}
			}

	private void activate(Node node) throws IllegalArgumentException
	{
		if (node == null)
			throw new IllegalArgumentException(
					"Invalid node");
		if (node.isActive())
			throw new IllegalArgumentException(
					"Already active node");

		node.setActive(true);

		graph.addVertex(node);

		int[] xy = Node.coordinates(node, xOffset, yOffset);
		int x, y;
		Node target;
		Arc arc;
		for (int i = -1; i <= 1; i++)
		{
			x = xy[0] + i;
			if (x < 0 || x >= nodes.length)
				continue;
			for (int j = -1; j <= 1; j++)
			{
				y = xy[1] + j;
				if (y < 0 || y >= nodes[x].length)
					continue;

				target = nodes[x][y];
				if (target == null)
					throw new IllegalArgumentException(
							"Invalid target node");

				if (!target.isActive() || target.equals(node))
					continue;

				graph.addVertex(target);

				arc = graph.addEdge(node, target);

				graph.setEdgeWeight(arc,
						(Math.abs(i) != Math.abs(j)) ? NODE_WIDTH
								: ARC_WEIGHT);
			}
		}
	}

	private void deactivate(Node node) throws IllegalArgumentException
	{
		if (node == null)
			throw new IllegalArgumentException(
					"Invalid node");
		if (!node.isActive())
			throw new IllegalArgumentException(
					"Already inactive node");

		node.setActive(false);
		graph.removeVertex(node);
	}

	private void constructGraph()
	{
		for (int x = 0; x < X_NODES; x++)
			for (int y = 0; y < Y_NODES; y++)
				nodes[x][y] = new Node((x * NODE_WIDTH) + xOffset,
						(y * NODE_WIDTH) + yOffset, NODE_WIDTH);

		for (Node[] row : nodes)
			for (Node node : row)
				activate(node);
	}

	private Node nodeWithPoint(int x, int y)
	{
		int x_node = Node.nodePixel(x, NODE_WIDTH);
		int y_node = Node.nodePixel(y, NODE_WIDTH);

		if (x_node < 0 || x_node > nodes.length)
			throw new IllegalArgumentException(
					"Invalid x-coordinate: " + x);
		if (y_node < 0 || y_node > nodes[x_node].length)
			throw new IllegalArgumentException(
					"Invalid y-coordinate : " + y);

		return nodes[x_node][y_node];
	}

	private void rectInGrid(Rect rect)
			throws IllegalArgumentException
			{
		if (rect == null)
			throw new IllegalArgumentException("Null argument");
			}

	private void testInt(int value) throws IllegalArgumentException
	{
		if (value < 0)
			throw new IllegalArgumentException("Invalid value : " + value);
	}

	public int getNodesN()
    {
        return NNODES;
    }

	public void addExit(int x, int y) {
		
	}

	public void update() {

	}

}
