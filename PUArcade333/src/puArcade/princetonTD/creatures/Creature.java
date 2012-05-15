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

package puArcade.princetonTD.creatures;

import java.util.ArrayList;
import java.util.Date;

import puArcade.princetonTD.game.Game;
import puArcade.princetonTD.players.Player;
import puArcade.princetonTD.players.Team;
import puArcade.princetonTD.towers.Tower;



import android.graphics.Point;
import android.graphics.Rect;

public abstract class Creature {

	// Identification
	private int ID;
	private static int currentID = 0;
	
	// top left corner
	private int x;
	private int y;
	// dimensions
	private int width;
	private int height;

	// real positions
	private double xReal, yReal;

	// type
	public static final int TYPE_LAND	= 0;
	public static final int TYPE_AIR	= 1;

	// Time before creature is removed (for multi-player)
	private static final long TIME_BEFORE_REMOVAL = 3000;

	// Owner
	private Player owner;

	// Name
	private final String NAME;

	// Type
	private final int TYPE;
	
	// Game
	private Game game;

	// Path of creature
	private ArrayList<Point> path;

	// Current position on path
	private int indexCurrentPath;

	// Health
	private long health;

	// Max health
	private long healthMax;

	// Reward
	private int reward;

	// Image
	protected String image;

	// Speed
	protected double speed; // pixel(s) / second

	// Slowed coefficient
	protected double coeffSlow; // 0.0 = normal, 1.0 = 100%

	// Allow death animation
	private boolean death;

	// Target Team
	private Team targetTeam;

	// Angle for next node
	private double angle = 0.0;

	// For multi-player
	private boolean invincible = false;

	private final double HALF_WIDTH;
	private final double HALF_HEIGHT;

	
	public Creature(int x, int y, int width, int height, 
			long healthMax, int reward, double speed, 
			int type, String image, String name)
	{
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;

		HALF_WIDTH = width / 2.0;
		HALF_HEIGHT = height / 2.0;

		xReal = x;
		yReal = y;

		this.ID				= ++currentID;
		this.reward			= reward;
		this.healthMax		= healthMax;
		health              = healthMax;
		this.speed			= speed;
		this.image			= image;
		TYPE				= type;
		NAME				= name;
	}

	// Copy creature
	abstract public Creature copy();
	
	public void setGame(Game game)
	{
		this.game = game;
	}

	// return path
	public ArrayList<Point> getPath()
	{
		return path;
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
	// added functions to return position of creature's center
	public int centerX()
	{
		return (int) x + width/2;
	}
	public int centerY()
	{
		return (int) y + height/2;
	}

	// set owner
	public void setOwner(Player owner)
	{
		this.owner = owner;
	}

	// return owner
	public Player getOwner()
	{
		return owner;
	}

	// return type
	public int getType()
	{
		return TYPE;
	}

	// return health
	public long getHealth()
	{
		return health;
	}

	// return max health
	public long getHealthMax()
	{
		return healthMax;
	}

	// return reward
	public int getReward()
	{
		return reward;
	}

	// return speed
	public double getSpeed()
	{
		return speed;
	}

	// return slowed speed
	public double getSpeedSlowed()
	{
		return speed - speed * coeffSlow;
	}

	// return slow coefficient
	public double getCoeffSlow()
	{
		return coeffSlow;
	}

	// set invincible
	public void setInvincible(boolean invincible)
	{
		this.invincible = invincible;
	}

	// set slow coefficient
	public void setCoeffSlow(double coeffSlow)
	{
		if(coeffSlow > 1.0)
			coeffSlow = 1.0;
		else if(coeffSlow < 0.0)
			coeffSlow = 0.0;
		else  
			this.coeffSlow = coeffSlow;
	}

	// return image
	public String getImage()
	{
		return image;
	}

	// return type in text
	public String getNameType()
	{
		if(TYPE == TYPE_LAND)
			return "LAND";
		else
			return "AIR";
	}

	// return name
	public String getName()
	{
		return NAME;
	}

	// return angle
	public double getAngle()
	{
		return angle;
	}

	// set x
	public void setX(int x)
	{
		this.x = x;
		this.xReal = x;
	}

	// set y
	public void setY(int y)
	{
		this.y = y;
		this.yReal = y;
	}

	// set path
	public void setPath(ArrayList<Point> path)
	{
		indexCurrentPath = 1; 

		this.path = path;
	}

	// get index of current path
	public int getIndexCurrentPath()
	{
		return indexCurrentPath;
	}

	// Action of creature
	public void action(long dt)
	{
		// move
		moveOnPath(dt);

		// if reach goal
		if(path != null && indexCurrentPath == path.size() 
				&& !death && !isDead())
		{
			death = true;
			
			game.reachedCreature(this);
		}
	}

	// move along path
	protected void moveOnPath(long dt)
	{
		if(path != null && indexCurrentPath < path.size())
		{   
			double distance = getSpeedSlowed() * ((double) dt / 1000.0);

			while(distance > 0 && indexCurrentPath < path.size())
			{
				double centerX = xReal + HALF_WIDTH;
				double centerY = yReal + HALF_HEIGHT;

				Point pNext   = path.get(indexCurrentPath);
				
				angle = Math.atan2(centerY - pNext.y,centerX - pNext.x);

				double distanceToNext = Math.sqrt((centerX-pNext.x)*(centerX-pNext.x)+
													(centerY-pNext.y)*(centerY-pNext.y));

				if(distance >= distanceToNext)
				{
					xReal = pNext.x - HALF_WIDTH;
					yReal = pNext.y - HALF_HEIGHT;

					indexCurrentPath++;

					distance -= distanceToNext;
				}
				else
				{
					xReal -= Math.cos(angle)*distance; // x
					yReal -= Math.sin(angle)*distance; // y
					distance = 0;
				}
			}

			x = (int) Math.round(xReal);
			y = (int) Math.round(yReal);
		}
	}

	// Receive damage
	synchronized public void damaged(long damage, Player player)
	{
		if(!isDead() && player.getTeam() == targetTeam)
		{
			if(!invincible)
				health -= damage;

			// is dead ?
			if(isDead())
				killedBy(player);
		}
	}

	// is dead?
	public boolean isDead()
	{
		return health <= 0;
	}

	// permanently kill creature
	public void killedBy(Player player)
	{ 
		health = 0;
		
		game.killedCreature(this,player);
		
		death = true;
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

	// return death?
	public boolean death()
	{
		return death;
	}

	// return target team
	public Team getTargetTeam()
	{
		return targetTeam;
	}

	// set target team
	public void setTargetTeam(Team targetTeam)
	{
		this.targetTeam = targetTeam;
	}

	// set ID
	public void setId(int id)
	{
		this.ID = id;
	}

	// set health
	public void setHealth(int health)
	{
		this.health = health;
	}

	// set angle
	public void setAngle(double angle)
	{
		this.angle = angle;
	}

	// can be attacked (by a tower)
	public boolean canBeAttacked(Tower tower)
	{
		if(tower.getOwner().getTeam() != targetTeam)
			return false;

		int typeTower = tower.getType();
		return typeTower == Tower.TYPE_LAND_AND_AIR 
				|| (typeTower == Tower.TYPE_LAND && TYPE == Creature.TYPE_LAND) 
				|| (typeTower == Tower.TYPE_AIR && TYPE == Creature.TYPE_AIR);
	}

	// set max health
	public void setHealthMax(long healthMax)
	{
		this.healthMax = healthMax;
	}

	// set reward
	public void setReward(int reward)
	{
		this.reward = reward;
	}

	// set speed
	public void setSpeed(double speed)
	{
		this.speed = speed;
	}

	long lastUpdate = 0;
	public void update()
	{
		lastUpdate = new Date().getTime();   
	}

	// remove if not updated (multi-player)
	public void erase()
	{
		long now = new Date().getTime(); 

		if(lastUpdate != 0)
		{
			if(now - lastUpdate > TIME_BEFORE_REMOVAL)
			{
				System.out.println("CREATURE KILLED AFTER "+
						(TIME_BEFORE_REMOVAL / 1000.0)+
						" SEC OF INACTIVITY ! (id : "+ID+" )");

				killedBy(null);
			}
		}
	}
	
	// MODIFICATION
	// does creature intersect with another rectangle?
	public boolean intersects(Rect rectangle) {
		return rectangle.intersects(x, y, x+width, y+height);
	}

}
