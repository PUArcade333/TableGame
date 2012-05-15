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

package puArcade.princetonTD.towers;

import java.util.Enumeration;

import puArcade.princetonTD.creatures.Creature;
import puArcade.princetonTD.game.Game;
import puArcade.princetonTD.players.Player;


import android.graphics.Rect;

public abstract class Tower {

	public static final int TYPE_LAND_AND_AIR		= 0;
	public static final int TYPE_LAND				= 1;
	public static final int TYPE_AIR				= 2;

	private static final double COEFF_SELL_PRICE	= 0.6;

	public static final int TARGET_NEAREST			= 0;
	public static final int TARGET_FURTHEST			= 1;
	public static final int TARGET_WEAKEST			= 2;
	public static final int TARGET_STRONGEST		= 3;

	private int targetType = TARGET_WEAKEST;

	// Identification
	private int ID;
	private static int currentID = 0;

	// top left corner
	private int x;
	private int y;
	// dimensions
	private int width;
	private int height;

	// Color
	private final int BACKGROUND_COLOR;

	// Name
	private final String NAME;

	// Description
	protected String description;

	// Damage
	protected long damage;

	// Level
	protected int level = 1;

	// Price
	protected int price;

	// Total Price
	protected int priceTotal;

	// Range
	protected double range = 100;

	// Type
	protected int type;

	// Image
	protected String image;

	// Icon
	protected final String ICON;

	// Game
	protected Game game;

	// Active?
	protected boolean active;

	// Owner
	protected Player owner;

	// Rate of fire (times / sec)
	private double rate;

	// Rotation angle
	protected double angle = 0.0;

	// Initialization so tower can shoot straight
	private long timeElapsed;
	private long latency;

	public Tower(int x, int y, int width, int height, int color,
			String name, int price, long damage, double range, 
			double rate, int type, String image, String icon)
	{
		this.ID             = ++currentID;
		this.x              = x;
		this.y              = y;
		this.width          = width;
		this.height         = height;
		NAME                = name;
		BACKGROUND_COLOR    = color;
		this.price      	= price;
		priceTotal          = price;
		this.damage         = damage;
		this.range		    = range;
		this.rate         	= rate;
		this.type           = type;
		this.image          = image;
		ICON                = icon;

		latency = (long) (1000.0 / rate);
		timeElapsed = latency; 
	}
	
	public abstract void upgrade();
	
	protected abstract void attack(Creature c);

	public abstract Tower copy();

	public abstract boolean canUpgrade();

	public abstract long nextDamage();

	public abstract double nextRange();

	public abstract double nextRate();

	// return rate
	public double getRate()
	{
		return rate;
	}

	// set game
	public void setGame(Game game)
	{
		this.game = game;
	}

	// return ID
	public int getId()
	{
		return ID;
	}
	
	// return position & dimensions
	public int x()
	{
		return x;
	}
	public int y()
	{
		return y;
	}
	public int width()
	{
		return width;
	}
	public int height()
	{
		return height;
	}
	// MODIFICATION
	// added functions to return position of tower's center
	public int centerX()
	{
		return (int) x + width/2;
	}
	public int centerY()
	{
		return (int) y + height/2;
	}
	
	// set position
	public void setX(int x)
	{
		this.x = x;
	}
	public void setY(int y)
	{
		this.y = y;
	}

	// return background color
	public int getColor()
	{
		return BACKGROUND_COLOR;
	}

	// return name
	public String getName()
	{
		return NAME;
	}

	// return description
	public String getDescription()
	{
		return description;
	}

	// return price
	public int getPrice()
	{
		return price;
	}

	// return range
	public double getRange()
	{
		return range;
	}

	// return sell price
	public int getSellPrice()
	{
		return (int) (priceTotal * COEFF_SELL_PRICE);
	}

	// return total price
	public int getPriceTotal()
	{
		return priceTotal;
	}

	// return image
	public String getImage()
	{
		return image;
	}

	// return icon
	public String getIcon()
	{
		return ICON;
	}


	// return level
	public int getLevel()
	{
		return level;
	}

	// return damage
	public long getDamage()
	{
		return damage;
	}

	// return type of tower in text
	public String getTextType()
	{
		if(type == TYPE_LAND_AND_AIR)
			return "LAND + AIR";
		else if(type == TYPE_LAND)
			return "LAND";
		else
			return "AIR";
	}

	// return type
	public int getType()
	{
		return type;
	}

	// activate tower
	public void start()
	{
		active = true;
	}

	// deactivate tower
	public void stop()
	{
		active = false;
	}

	// is active?
	public boolean isActive()
	{
		return active;
	}

	// set rate of fire
	public void setRate(double rate) 
	{
		this.rate = rate;
		latency = (long) (1000.0 / rate);
	}

	// Actions of tower, i.e. Shoot
	public void action(long dt)
	{  
		// time since last shot
		timeElapsed += dt;

		if(timeElapsed >= latency)
		{
			// Target selection
			Creature c = null;
			switch(targetType)
			{
			case TARGET_NEAREST :
				c = getNearestCreature(true);
				break;
			case TARGET_FURTHEST :
				c = getNearestCreature(false);
				break;
			case TARGET_WEAKEST :
				c = getWeakestCreature(true);
				break;
			case TARGET_STRONGEST :
				c = getWeakestCreature(false);
				break;
			default :
				System.err.println("Unknown target type");
			}

			// if there is c
			if (c != null)
			{
				// attack
				attack(c);

				// reset time since last shot
				timeElapsed -= timeElapsed;
			}
			else 
				timeElapsed = latency;
		}
	}

	// return nearest c that can be attacked in range
	private Creature getNearestCreature(boolean nearest)
	{
		// game set?
		if (game == null)
			return null;

		Creature nearestCreature = null;

		double distanceMinMax = -1;

		if(nearest)
			distanceMinMax = java.lang.Double.MAX_VALUE ;

		double tmpDistance = 0;

		// set of cs
		Creature c;
		Enumeration<Creature> eCreatures = game.getCreatures().elements();
		while(eCreatures.hasMoreElements())
		{
			try {
				c = eCreatures.nextElement();

				// can be attacked?
				if (c.canBeAttacked(this))
				{
					tmpDistance = getDistance(c);

					// is in range?
					if (tmpDistance <= range)
					{
						if (nearest && tmpDistance < distanceMinMax
								|| !nearest && tmpDistance > distanceMinMax)
						{ 
							nearestCreature = c;
							distanceMinMax = tmpDistance;
						}
					}
				}
			}
			catch(java.util.NoSuchElementException nsee)
			{
				nsee.printStackTrace();
			}
		}

		return nearestCreature;
	}

	// return weakest c that can be attacked in range
	private Creature getWeakestCreature(boolean weakest)
	{
		// game set?
		if (game == null)
			return null;

		Creature weakestCreature = null;

		long healthMinMax = 0;
		if(weakest) 
			healthMinMax = Long.MAX_VALUE;

		// set of cs
		Creature c;
		Enumeration<Creature> eCreatures = game.getCreatures().elements();
		while(eCreatures.hasMoreElements())
		{
			try {
				c = eCreatures.nextElement();

				// can be attacked?
				if (c.canBeAttacked(this))
				{
					// is in range?
					if (getDistance(c) <= range)
					{
						if (weakest && c.getHealth() < healthMinMax
								|| !weakest && c.getHealth() > healthMinMax)
						{
							weakestCreature = c;
							healthMinMax = c.getHealth();
						}
					}
				}
			}
			catch(java.util.NoSuchElementException nsee)
			{
				nsee.printStackTrace();
			}
		}

		return weakestCreature;
	}


	// return distance to a c
	protected double getDistance(Creature c)
	{
		return Math.sqrt((x-c.x())*(x-c.x())+(y-c.y())*(y-c.y()));
	}

	// return time in milliseconds since last call of this function.
	protected long getTimeLastCall()
	{
		long now = System.currentTimeMillis(); 

		// first call
		if(timeLastCall == 0)
		{
			timeLastCall = now;
			return 0;
		}

		// time passed since last call
		long timePassed = now - timeLastCall;
		timeLastCall = now;
		return timePassed;
	}
	private long timeLastCall;


	// return owner
	public Player getOwner()
	{
		return owner;
	}

	// set owner
	public void setOwner(Player owner)
	{
		this.owner = owner;
	}

	// return angle (orientation of tower)
	public double getAngle()
	{
		return angle;
	}

	public void setId(int id)
	{
		this.ID = id;
	}

	public void setTargetType(int targetType)
	{
		this.targetType = targetType;
	}

	public int getTargetType()
	{ 
		return targetType;
	}
	
	// MODIFICATIONS
	// added functions to determine intersections
	public boolean intersects(Rect wall) {
		return wall.intersects(x, y, x+width, y+height);
	}
	public boolean intersects(Tower t) {
		Rect rect = new Rect(t.x(),t.y(),t.x()+t.width(),t.y()+t.height());
		return rect.intersects(x, y, x+width, y+height);
	}
	public boolean intersects(Creature c) {
		Rect rect = new Rect(c.x(),c.y(),c.x()+c.width(),c.y()+c.height());
		return rect.intersects(x, y, x+width, y+height);
	}
}

