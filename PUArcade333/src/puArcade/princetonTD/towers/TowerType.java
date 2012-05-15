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

public class TowerType {

	private static final int TOWER_ARCHER       = 1;
	private static final int TOWER_CANON        = 2;
	private static final int TOWER_AA           = 3;
	private static final int TOWER_ICE      	= 4;
	private static final int TOWER_ELECTRIC     = 5;
	private static final int TOWER_FIRE         = 6;
	private static final int TOWER_AIR          = 7;
	private static final int TOWER_EARTH        = 8;
	private static final int N					= 8;


	public static int getTowerType(Tower tower)
	{
		if (tower instanceof TowerArcher)
			return TOWER_ARCHER;
		else if (tower instanceof TowerCanon)
			return TOWER_CANON;
		else if (tower instanceof TowerAA)
			return TOWER_AA;
		else if (tower instanceof TowerIce)
			return TOWER_ICE;
		else if (tower instanceof TowerElectric)
			return TOWER_ELECTRIC;
		else if (tower instanceof TowerFire)
			return TOWER_FIRE;
		else if (tower instanceof TowerAir)
			return TOWER_AIR;
		else if (tower instanceof TowerEarth)
			return TOWER_EARTH;

		return -1;
	}

	public static Tower getTower(int type)
			{
		switch(type)
		{
		case TOWER_ARCHER : 
			return new TowerArcher();
		case TOWER_CANON :
			return new TowerCanon();
		case TOWER_AA : 
			return new TowerAA();
		case TOWER_ICE : 
			return new TowerIce();
		case TOWER_ELECTRIC :
			return new TowerElectric();
		case TOWER_FIRE : 
			return new TowerFire();
		case TOWER_AIR :
			return new TowerAir();
		case TOWER_EARTH :
			return new TowerEarth();
		default : 
			return null;
		}
			}
	
	// MODIFICATION
	// added function to return number of tower types in game
	public static int getN()
	{
		return N;
	}

}
