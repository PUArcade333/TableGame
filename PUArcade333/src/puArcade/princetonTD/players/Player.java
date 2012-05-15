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

import puArcade.princetonTD.game.Score;

public class Player {
	
private static int currentID = 0;
    
    private int id;
    
    private String alias;
    
    private double gold = 0;
    
    private Team team;
   
    private Score score = new Score();
    
    private PlayerLocation location;
 
    // offside
    private boolean offside;

    public Player(String alias)
    {
        this.alias = alias;
        this.id = ++currentID;
    }

    public int getId()
    {
        return id;
    }
    
    public int getScore()
    {
        return score.getValue();
    }
    
    public boolean isOffside()
    {
        return offside;
    }

    public void setOffside()
    {
        offside = true;
    }
    
    public void setGold(double gold)
    {
        this.gold = gold;
    }

    public double getGold()
    {
        return gold;
    }

    public void setScore(int score)
    {
        this.score.setValue(score);
    }
    
    public Team getTeam()
    {
        return team;
    }
    
    public PlayerLocation getLocation()
    {
        return location;
    }
     
    public boolean hasLost()
    {
        return team.getLives() <= 0;
    }

    public String getAlias()
    {
        return alias;
    }
    
    public void setTeam(Team team)
    {
        if(this.team != null && this.team.contains(this))
            this.team.removePlayer(this);
        
        this.team = team;
    }
    
    public void setPlayerLocation(PlayerLocation playerLocation)
    {
        if(playerLocation != null 
        && playerLocation.getPlayer() != this)
            playerLocation.setPlayer(this);
        
        this.location = playerLocation;
    }

    public void withdrawPlayerLocation()
    {
        if(location != null && location.getPlayer() != this) 
            location.removePlayer();
        
        location = null;
    }

    public void setId(int id)
    {
        this.id = id; 
    }
	
}
