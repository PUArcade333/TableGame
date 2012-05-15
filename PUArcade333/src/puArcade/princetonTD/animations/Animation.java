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

package puArcade.princetonTD.animations;

import android.graphics.Point;

public abstract class Animation extends Point {

	public static final int HEIGHT_LAND = 0;
	public static final int HEIGHT_AIR = 1;

	protected boolean finished;
	protected int height = HEIGHT_AIR;

	// Constructor
	public Animation(int x, int y)
	{
		this.x = x;
		this.y = y;
	}

	// Android does not allow subclasses to draw, so abstract draw method has been removed
	// abstract public void draw(Graphics2D g2);
	
	// Execute animation
	abstract public void animate(long t);
	
	// is animation finished?
	public boolean isFinished()
	{
		return finished;
	}
	
	// height of animation
	public int getHeight()
	{
		return height;
	}

}
