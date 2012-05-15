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

package puArcade.princetonTD.game;

import java.io.Serializable;
import java.util.Date;

public class Score implements Serializable, Comparable<Score> {

	private static final long serialVersionUID = 1L;

	private int value;
	private String playerName;
	private Date date;
	// duration
	private long duration;

	// Constructor
	public Score (String playerName, int value, long duration)
	{
		this.value = value;
		this.playerName = playerName;
		this.duration = duration;

		date = new Date();
	}

	public Score (Score score)
	{
		value		= score.value;
		playerName	= score.playerName;
		duration	= score.duration;
		date		= score.date;
	}

	public Score()
	{
		playerName = "";
	}

	public int getValue ()
	{
		return value;
	}

	public String getPlayerName ()
	{
		return playerName;
	}

	public long getDuration()
	{
		return duration;
	}

	public Date getDate()
	{
		return new Date(date.getTime());
	}

	public int compareTo (Score comp)
	{
		if (comp.value > value)
			return 1;
		else if (comp.value == value)
			return 0;
		else
			return -1;
	}

	@Override
	public String toString ()
	{
		return playerName + " - " + value + " - " + date;
	}

	public void setValue(int score)
	{
		value = score;   
	}

	private int getSeconds()
	{
		return (int) (duration) % 60;
	}

	private int getMinutes()
	{
		return (int) (duration / 60) % 60;
	}

	private int getHours()
	{
		return (int) (duration / 3600) % 24;
	}

	public String getHMS()
	{
		return String.format("%02d:%02d:%02d", getHours(), 
				getMinutes(), 
				getSeconds());
	}

}
