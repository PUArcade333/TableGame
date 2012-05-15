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

package puArcade.princetonTD.players;

import java.io.Serializable;

import android.graphics.Color;
import android.graphics.Rect;

public class PlayerLocation implements Serializable {

	private static final long serialVersionUID = 1L;

	private int id;

	transient Player player;

	Rect zone;

	private int color;

	// Constructor
	public PlayerLocation(int id, Rect zone)
	{
		this(id,zone,Color.BLACK);
	}

	public PlayerLocation(int id,Rect zone, int color)
	{
		this.zone = zone;
		this.color = color;
		this.id = id;
	}

	public void setPlayer(Player player)
	{
		if(player == null)
		{
			if(this.player != null)
			{
				this.player.setPlayerLocation(null);
				this.player = null;
			}
		}

		else
		{
			if(this.player != null)
				throw new IllegalArgumentException("Occupied Location");
			else
			{ 
				if(player.getLocation() != null)
					player.getLocation().setPlayer(null);

				this.player = player;
				player.setPlayerLocation(this);
			}
		}   
	}

	public int getColor()
	{
		return color;
	}

	public Rect getZone()
	{
		return zone;
	}

	public Player getPlayer()
	{
		return player;
	}

	public int getId()
	{
		return id;
	}

	@Override
	public String toString()
	{
		return "Zone "+id;
	}

	public void removePlayer()
	{
		if(this.player != null)
		{
			this.player.withdrawPlayerLocation();
			this.player = null;
		}
	}

	public void setColor(int color)
	{
		this.color = color;
	}

}
