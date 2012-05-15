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
import java.util.Random;

import puArcade.princetonTD.creatures.Creature;
import puArcade.princetonTD.game.Game;
import puArcade.princetonTD.towers.Tower;



import android.graphics.Color;
import android.graphics.Point;

public class Electricity extends Attack {

	private ArrayList<ArrayList<Point>> arcs = new ArrayList<ArrayList<Point>>();

	public Electricity(Game game, Tower attacker, Creature target, long damage)
	{
		super((int) attacker.centerX(),(int) attacker.centerY(), game, attacker, target);

		this.damage = damage;

		arcs.add(createArc());
		arcs.add(createArc());
		arcs.add(createArc());
		arcs.add(createArc());
		arcs.add(createArc());
	}


	public Electricity(Game game, Tower attacker, int xStart, int yStart, Creature target, long damage)
	{
		super((int) attacker.centerX(),(int) attacker.centerY(), game, attacker, target);

		this.damage = damage;

		arcs.add(createArc());
		arcs.add(createArc());
		arcs.add(createArc());
		arcs.add(createArc());
		arcs.add(createArc());
	}

	private ArrayList<Point> createArc()
	{
		Point creature = new Point((int)target.centerX(),(int)target.centerY());
		Point tower = new Point((int)attacker.centerX(),(int)attacker.centerY());

		int N = (int) (Math.sqrt((creature.x-tower.x)*(creature.x-tower.x)+(creature.y-tower.y)*(creature.y-tower.y)) / 10);


		ArrayList<Point> arc = new ArrayList<Point>();


		arc.add(new Point((int)attacker.centerX(),
				(int)attacker.centerY()));

		Point vector = new Point(creature.x - tower.x, creature.y - tower.y);
		double nx = vector.y / Math.sqrt(vector.x*vector.x+vector.y*vector.y);
		double ny = - vector.x / Math.sqrt(vector.x*vector.x+vector.y*vector.y);

		Random ran = new Random();

		for(int i = 0;i < N - 1; i++)
		{
			tower.x += vector.x / N;
			tower.y += vector.y / N;

			int rand = ran.nextInt(20)-10;

			arc.add(new Point((int) (tower.x + rand * nx ),
					(int) (tower.y + rand * ny)));
		}

		arc.add(new Point((int)target.centerX(),
				(int)target.centerY()));

		return arc;
	}


	// Android does not allow subclasses to draw, so draw methods has been removed

	//    private static final BasicStroke PEN  = new BasicStroke(3.0f,BasicStroke.CAP_BUTT,BasicStroke.JOIN_ROUND);
	//    
	//    
	//    public void drawArc(Graphics2D g2, ArrayList<Point> arc, float alpha)
	//    {    
		//        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
		//
		//        Point p = arc.get(0);
	//        for(int i=1;i < arc.size(); i++)
	//        {
	//            g2.drawLine((int) p.x,(int) p.y,
	//                    (int) arc.get(i).x,(int) arc.get(i).y);
	//            
	//            p = arc.get(i);
	//        }
	//    }
	//    
	//    
	//    @Override
	//    public void draw(Graphics2D g2)
	//    {    
	//        BasicStroke old = (BasicStroke) g2.getStroke();
	//        
	//        
	//        float alpha = 0.6f;
	//        
	//        boolean premier = true;
	//        
	//        for(ArrayList<Point> arc : arcs)
	//        {
	//            if(premier)
	//            {
	//                g2.setStroke(PEN);
	//                
	//                g2.setColor(Color.BLUE);
	//                dessinerArc(g2, arc, alpha);
	//                
	//                g2.setColor(Color.WHITE);
	//                g2.setStroke(old);
	//
	//                premier = false;
	//            }
	//            else
	//                g2.setColor(Color.BLUE);
	//            
	//            dessinerArc(g2, arc, alpha);
	//            
	//            alpha -= 0.1f;
	//        }
	//        
	//        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.f));   
	//        g2.setStroke(old);
	//    }

	// execute attack
	@Override
	public void animate(long t)
	{   
		target.damaged(damage, attacker.getOwner());
		finished = true;
	}

}
