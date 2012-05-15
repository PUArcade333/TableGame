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

package puArcade.princetonTD.maps;

import java.util.ArrayList;
import java.util.Enumeration;

import puArcade.princetonTD.creatures.Creature;
import puArcade.princetonTD.creatures.CreatureWave;
import puArcade.princetonTD.exceptions.PathNotFoundException;
import puArcade.princetonTD.game.Game;
import puArcade.princetonTD.game.Mode;
import puArcade.princetonTD.grid.Grid;
import puArcade.princetonTD.grid.Grid_v1;
import puArcade.princetonTD.grid.Node;
import puArcade.princetonTD.players.Team;
import puArcade.princetonTD.towers.Tower;
import android.graphics.Point;
import android.graphics.Rect;

public class Map {

	transient public static final String FILE_EXT = "map";

	transient public static final String MAPS_SOLO = "maps/solo/";
	transient public static final String MAPS_MULTI = "maps/multi/";

	private String description;

	// lives
	private int initLives;

	// starting gold
	private int initGold;

	// size
	private int width, // in pixels
	height; // in pixels

	// unit
	private final int UNIT = 10; // pixels

	// grid
	transient private Grid GRID_LAND;
	transient private Grid GRID_AIR;

	// grid dimensions
	private int gridWidth, gridHeight;

	// grid offset
	private int gridX, gridY;

	// pathAir
	transient ArrayList<Point> pathAir;

	// background
	transient private String bgImage;
	private String bgIcon;

	// walls
	protected ArrayList<Rect> walls = new ArrayList<Rect>();

	// wall opacity
	protected float opacity = 1.0f;

	// game
	transient private Game game;

	// teams
	protected ArrayList<Team> teams = new ArrayList<Team>();

	// game mode
	private int mode;

	protected String filename;

	// Constructor
	public Map(Game game, int width, int height, int initGold,
			int initLives, int gridX, int gridY,
			int gridWidth, int gridHeight, int mode,
			String bgImage, String description)
	{
		this.game 		= game; 
		this.width 		= width;
		this.height 	= height;
		this.initGold 	= initGold;
		this.initLives	= initLives;
		this.bgImage    = bgImage;
		this.bgIcon		= bgImage;

		this.gridWidth  = gridWidth;
		this.gridHeight = gridHeight;
		this.gridX  	= gridX;
		this.gridY  	= gridY;

		this.description= description;
		this.mode       = mode;   
	}

	// Constructor default
	public Map(Game game)
	{
		this.game = game;

		width			= 500;
		height			= 500;
		initGold		= 100;
		initLives		= 20;
		description		= "";
		mode			= Mode.MODE_SOLO;   
	}

	// initialize
	public void initialize()
	{
		// create grid
		// TODO grid choice
		GRID_LAND = new Grid_v1(gridWidth, gridHeight,
				UNIT, gridX, gridY);

		GRID_AIR = new Grid_v1(gridWidth, gridHeight,
				UNIT, gridX, gridY);  


		// activate walls
		for(Rect wall : walls)
		{
			GRID_LAND.deactivateZone(wall,false);
			GRID_AIR.deactivateZone(wall,false);
		}

		Rect goal;
		for(Team team : teams)
		{
			goal = team.getEndZone();

			GRID_LAND.addExit((int) goal.centerX(), (int) goal.centerY());
			GRID_AIR.addExit((int) goal.centerX(), (int) goal.centerY());
		}
	}

	public void reset()
	{
		initialize();
	}


	// ------------------------------

	public int getWidth()
	{
		return width;
	}
	
	public int getHeight()
	{
		return height;
	}

	public String getBgImage()
	{      
		return bgImage;
	}

	public void setBgImage(String bgImage)
	{
		this.bgImage = bgImage;

		if(bgImage == null)
			bgIcon = null;
		else
			bgIcon = bgImage; 
	}

	public int getGold()
	{
		return initGold;
	}

	public int getLives()
	{
		return initLives;
	}

	public String getDescription()
	{
		return description;
	}

	public int getMode()
	{
		return mode;
	}

	// ----------------------
	// -- Walls --
	// ----------------------

	public void addWall(Rect wall)
	{
		if (wall == null)
			throw new IllegalArgumentException("Invalid Wall");

		if(GRID_LAND != null)
			GRID_LAND.deactivateZone(wall,false);

		if(GRID_AIR != null)
			GRID_AIR.deactivateZone(wall,false);

		walls.add(wall);
	}

	public ArrayList<Rect> getWalls()
	{
		return walls;
	}

	public ArrayList<Team> getTeams()
	{
		return teams;
	}

	public int getMaxPlayers()
	{
		int sum = 0;

		for(Team t : teams)
			sum += t.getNLocations();

		return sum;
	}

	// -----------------------
	// -- Towers --
	// -----------------------

	public boolean canPlaceTower(Tower tower)
	{
		if (tower == null)
			return false;

		if (tower.x() < 0 || tower.x() > width-tower.width()
				|| tower.y() < 0 || tower.y() > height-tower.height())
			return false;

		synchronized (walls)
		{
			for (Rect wall : walls)
				if (tower.intersects(wall))
					return false;
		}

		for(Team t : game.getTeams())
		{
			// start zones
			for(int i=0;i<t.getNStartZones();i++)
				if(tower.intersects(t.getStartZone(i)))
					return false;

			// end zones
			if (tower.intersects(t.getEndZone()))
				return false;
		}

		return true;
	}

	public boolean blockedPath(Tower tower)
	{
		if (tower == null)
			return false;

		deactivateZone(new Rect(tower.x(),tower.y(),tower.x()+tower.width(),tower.y()+tower.height()), false);

		try {

			Team team = tower.getOwner().getTeam();
			
			// TODO manage multiple starting zones
			Rect startZone = team.getStartZone(0);
			Rect endZone = team.getEndZone();

			// is there path?
			ArrayList<Point> path = getShortestPath((int) startZone.centerX(),
					(int) startZone.centerY(), (int) endZone
					.centerX(), (int) endZone.centerY(),
					Creature.TYPE_LAND);

			double length = GRID_LAND.getPathLength(path);


			// update path
			team.setPathLength(length);


			activateZone(new Rect(tower.x(),tower.y(),tower.x()+tower.width(),tower.y()+tower.height()), false);
			return false;

		} 
		catch (PathNotFoundException e) {
			activateZone(new Rect(tower.x(),tower.y(),tower.x()+tower.width(),tower.y()+tower.height()), false);
			return true;
		}
	}

	// ---------------------------
	// -- Creatures --
	// ---------------------------

	public CreatureWave getCreatureWave(int wave)
	{
		return CreatureWave.generateWave(wave);
	}

	public String getWaveDescription(int wave)
	{   
		CreatureWave nextWave = getCreatureWave(wave);

		String descriptionWave = nextWave.getDescription();
		if (!descriptionWave.isEmpty())
			return descriptionWave;

		return nextWave.toString();
	}


	// -------------------------
	// -- Grid --
	// -------------------------

	public void activateZone(Rect zone,
			boolean updatePath)
	{
		if(GRID_LAND != null)
		{
			try
			{
				GRID_LAND.activateZone(zone, true);
			}
			catch(IllegalArgumentException e)
			{}

			if (updatePath)
				updatePath();
		}
	}

	public void deactivateZone(Rect zone,
			boolean updatePath)
	{
		if(GRID_LAND != null)
			GRID_LAND.deactivateZone(zone, true);

		if (updatePath)
			updatePath();
	}

	synchronized private void updatePath()
	{
		Creature creature;
		Enumeration<Creature> eCreatures = game.getCreatures().elements();
		while(eCreatures.hasMoreElements())
		{
			creature = eCreatures.nextElement();

			if (creature.getType() == Creature.TYPE_LAND)   
			{
				Rect endZone = creature.getTargetTeam().getEndZone();

				try
				{ 
					creature.setPath(getShortestPath((int) creature.centerX(), (int) creature.centerY(),
							(int) endZone.centerX(), (int) endZone.centerY(), creature.getType()));
				}
				catch (PathNotFoundException e)
				{
					 try
					{
						 ArrayList<Point> path = creature.getPath();

						 if(path != null)
						 {
							 Point previous;

							 if(creature.getIndexCurrentPath() > 0)
								 previous = path.get(creature.getIndexCurrentPath()-1);
							 else
								 previous = new Point(endZone.left, endZone.top);

							 // new path
							 creature.setPath(getShortestPath(
									 (int) previous.x, 
									 (int) previous.y,
									 (int) endZone.centerX(),
									 (int) endZone.centerY(), 
									 creature.getType())); 
						 } 
					}
					 catch (PathNotFoundException e2)
					 {
						 // keep old
					 }
				}
			}
		}
	}

	public ArrayList<Point> getShortestPath(int xStart, int yStart,
			int xEnd, int yEnd, int typeCreature)
					throws IllegalArgumentException, PathNotFoundException
					{
		if (typeCreature == Creature.TYPE_LAND)
			return GRID_LAND.shortestPath(xStart, yStart,
					xEnd, yEnd);
		else
			return GRID_AIR.shortestPath(xStart, yStart,
					xEnd, yEnd);
					}

	// grid nodes
	public Node[] getNodes()
	{
		return GRID_LAND.getNodes();
	}

	//-------------------------

	public void setGame(Game game)
	{
		this.game = game;
	}

	public void setGridWidth(int gridWidth)
	{
		if(gridWidth <= 0)
			throw new IllegalArgumentException("Width must be positive");

		this.gridWidth = gridWidth;
	}

	public void setGridHeight(int gridHeight)
	{
		if(gridHeight <= 0)
			throw new IllegalArgumentException("Height must be positive");

		this.gridHeight = gridHeight;
	}

	public void removeWall(Rect wall)
	{
		walls.remove(wall);
	}

	public String getFilename()
	{
		return filename;
	}

	public void setFilename(String filename)
	{
		this.filename = filename;
	}

	public void setDescription(String description)
	{
		this.description = description;   
	}

	public void setWidth(int width)
	{
		if(width <= 0)
			throw new IllegalArgumentException("Width must be positive");

		this.width = width;
	}

	public void setHeight(int height)
	{
		if(height <= 0)
			throw new IllegalArgumentException("Height must be positive");

		this.height = height;
	}

	public void setGold(int initGold)
	{
		if(initGold < 0)
			throw new IllegalArgumentException("Must have nonnegative gold");

		this.initGold = initGold;
	}

	public void setLives(int initLives)
	{
		if(initLives <= 0)
			throw new IllegalArgumentException("Must have nonnegative lives");

		this.initLives = initLives;
	}

	public float getOpacity()
	{
		return opacity;
	}

	public void setOpaciteMurs(float opacity)
	{
		this.opacity = opacity;
	}

	public void setModeDeGame(int mode)
	{
		this.mode = mode;
	}

}
