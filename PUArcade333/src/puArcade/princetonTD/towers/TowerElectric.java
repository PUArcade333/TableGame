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

import puArcade.princetonTD.animations.Electricity;
import puArcade.princetonTD.creatures.Creature;
import android.graphics.Color;

public class TowerElectric extends Tower {
	
	public static final int COLOR;
	public static final String IMAGE;
	public static final String ICON;
	public static final int MAX_LEVEL = 5;
	public static final int PRICE = 120;
	private static final String DESCRIPTION = ""; 
	
	static
	{
		COLOR 	= Color.MAGENTA;
		IMAGE   = "drawable/towerelect";
		ICON    = "drawable/icontowerelect";
	}
	
	public TowerElectric()
	{
		super( 	0,
				0,
				20,
				20,
				COLOR,
				"Electricity Tower",
				PRICE,
				120,
				70,
				1,
				Tower.TYPE_LAND,
				IMAGE,
				ICON);                 

		description = DESCRIPTION;
	}
	
	@Override
	public void upgrade() {
		if(canUpgrade())
		{
			priceTotal += price;

			price *= 2;

			damage = nextDamage();

			range = nextRange();

			setRate(nextRate());

			level++;
		}
	}

	@Override
	protected void attack(Creature creature) {
		game.addAnimation(new Electricity(game,this,creature,damage));
	}

	@Override
	public Tower copy() {
		return new TowerElectric();
	}

	@Override
	public boolean canUpgrade() {
		return level < MAX_LEVEL;
	}

	@Override
	public long nextDamage() {
		return (long) (damage * 1.5);
	}

	@Override
	public double nextRange() {
		return range + 10;
	}

	@Override
	public double nextRate() {
		return getRate() * 1.2;
	}

}
