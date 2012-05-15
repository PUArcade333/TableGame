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

import puArcade.princetonTD.creatures.Creature;
import puArcade.princetonTD.game.Game;
import puArcade.princetonTD.towers.Tower;

public class Ice extends Attack {

	private static final String IMAGE;
	private final long LENGTH_SLOW;
	private long t = 0L;

	// image for attack
	static
	{
		IMAGE = "drawable/ice";
	}

	// constructor
	public Ice(Game game, Tower attacker, Creature target, double coeffSlow,  long lengthSlow)
	{
		super((int)attacker.x(), (int) attacker.y(), game, attacker, target);

		target.setCoeffSlow(coeffSlow);

		LENGTH_SLOW = lengthSlow;
	}

	// Android does not allow subclasses to draw, so draw method has been moved to game thread

	// execute attack
	@Override
	public void animate(long t)
	{ 
		this.t += t;

		if(target.isDead())
			finished = true;
		else if(this.t > LENGTH_SLOW)
		{
			target.setCoeffSlow(0.0);
			finished = true;
		}
	}

}
