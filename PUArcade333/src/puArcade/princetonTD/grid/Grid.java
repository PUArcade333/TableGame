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

import puArcade.princetonTD.exceptions.PathNotFoundException;


import android.graphics.Point;
import android.graphics.Rect;

public interface Grid {
	
	// calculate shortest path
	public abstract ArrayList<Point> shortestPath(int xStart, int yStart,
            int xEnd, int yEnd) throws PathNotFoundException,
            IllegalArgumentException;

    // activate zone
    public abstract void activateZone(Rect rect, boolean update)
            throws IllegalArgumentException;

    // deactivate zone
    public abstract void deactivateZone(Rect rect, boolean update)
            throws IllegalArgumentException;

    // width in pixels
    public abstract int getWidth();

    // height in pixels
    public abstract int getHeight();

    // grid nodes
    public abstract Node[] getNodes();

    // calculate path length
    public abstract double getPathLength(ArrayList<Point> path);

    // return number of nodes
    public abstract int getNodesN();
    
    // Exit point
    public void addExit(int x, int y);
    
    // update
    public void update();
	
}
