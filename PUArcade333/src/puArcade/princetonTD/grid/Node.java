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

import android.graphics.Point;

public class Node extends Point {

	private boolean active = false;

	protected final int NODE_WIDTH;

	Node(int x, int y, int pixel)
	{
		this.x = center(x, pixel);
		this.y = center(y, pixel);
		this.NODE_WIDTH = pixel;
	}

	Node(Node node)
	{
		this.x = node.x;
		this.y = node.y;
		this.NODE_WIDTH = node.NODE_WIDTH;
		this.active = node.active;
	}

	public boolean isActive()
	{
		return active;
	}

	public boolean equals(Node node)
	{
		if (node == null)
			throw new IllegalArgumentException("Null node");

		return x == node.x && y == node.y
				&& NODE_WIDTH == node.NODE_WIDTH;
	}

	@Override
	public String toString()
	{
		return super.toString() + " pixel : " + NODE_WIDTH + "active : "
				+ active;
	}

	void setActive(boolean active)
	{
		this.active = active;
	}

	public static int center(int i, int pixel)
	{
		return i - (i % pixel) + (pixel / 2);
	}

	public static int nodePixel(int x, int pixel)
	{
		return (center(x, pixel) - (pixel / 2)) / pixel;
	}

	public static int[] coordinates(Node node, int deltaX, int deltaY)
	{
		if (node == null)
			throw new IllegalArgumentException(
					"Null node parameters");

		int[] r = new int[2];
		r[0] = (node.x - deltaX) / node.NODE_WIDTH;
		r[1] = (node.y - deltaY) / node.NODE_WIDTH;
		return r;
	}

}
