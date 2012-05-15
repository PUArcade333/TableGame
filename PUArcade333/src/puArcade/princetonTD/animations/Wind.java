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

import java.util.ArrayList;
import java.util.Vector;

import puArcade.princetonTD.creatures.Creature;
import puArcade.princetonTD.game.Game;
import puArcade.princetonTD.towers.Tower;

public class Wind extends Attack {

	private static final int DIAMETER = 20;
	private static final String IMAGE;

	private ArrayList<Creature> touched = new ArrayList<Creature>();

	private double speed = 0.1; // px / ms
	private final double MAX_DISTANCE;

	private double distSource = 0;
	private final double ANGLE;
	private float alpha;
	
	// image for attack
	static
	{
		IMAGE = "drawable/wind";
	}

	// constructor
	public Wind(Game game, Tower attacker, Creature target, long damage)
	{
		super((int) attacker.centerX(), (int) attacker.centerY(), game,
				attacker, target);

		this.damage = damage;

		ANGLE = Math.atan2(target.centerY() - attacker.centerY(), 
				target.centerX() - attacker.centerX());

		MAX_DISTANCE = attacker.getRange();

	}

	// Android does not allow subclasses to draw, so draw method has been removed
	
//	@Override
//	public void draw(Graphics2D g2)
//	{ 
//		AffineTransform tx = new AffineTransform();
//		tx.translate((int) x, (int) y);
//		tx.rotate(ANGLE);
//		tx.translate((int) -DIAMETER / 2.0, (int) -DIAMETER / 2.0);
//
//		g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
//		g2.drawImage(IMAGE, tx, null);
//		g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.f));  
//	}
	
	// execute attack
	@Override
	public void animate(long t)
	{
		if(!finished)
		{
			if(distSource >= MAX_DISTANCE)
			{
				endAttack();
				finished = true;
			}
			else
			{
				distSource += t * speed;

				alpha = (float) (1.f - distSource / MAX_DISTANCE);

				if(alpha < 0.0)
					alpha = 0.0f;

				x = (int) (Math.cos(ANGLE) * distSource + attacker.centerX()); // x
				y = (int) (Math.sin(ANGLE) * distSource + attacker.centerY()); // y


				Vector<Creature> creatures = game.getCreaturesInCircle((int)x,(int)y,DIAMETER);
				Creature creature;
				for(int i=0;i<creatures.size();i++)
				{
					creature = creatures.get(i);

					if(creature.getType() == Creature.TYPE_AIR && !touched.contains(creature))
					{
						creature.damaged((long)(damage * alpha), attacker.getOwner());
						touched.add(creature);
					}
				}
			}
		}
	}

}
