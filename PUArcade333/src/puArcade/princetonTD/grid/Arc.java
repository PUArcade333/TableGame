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

import org.jgrapht.graph.DefaultWeightedEdge;

public class Arc extends DefaultWeightedEdge {
	
	private static final long serialVersionUID = -3342545915708624018L;

	private Node start, end;

	public Arc(Node start, Node end)
	{
		this.start = start;
		this.end = end;
	}

	public Node getStart()
	{
		return start;
	}

	public Node getEnd()
	{
		return end;
	}

}
