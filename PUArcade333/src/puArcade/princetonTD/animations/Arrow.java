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

import android.graphics.Color;
import puArcade.princetonTD.creatures.Creature;
import puArcade.princetonTD.game.Game;
import puArcade.princetonTD.towers.Tower;

public class Arrow extends Attack {

	private static final int LENGTH   = 10;
    private static final int COLOR  = Color.RED;
    
    private double speed = 0.2; // px / ms
    private double distHeadTower;
    
    private double xHead, yHead;
    private double xTail, yTail;
    

    public Arrow(Game game, Tower attacker, Creature target, long damage)
    {
        super((int) attacker.centerX(),(int) attacker.centerY(), game, attacker, target);
        
        this.damage = damage;
    }
    
 // Android does not allow subclasses to draw, so draw method has been removed

//    @Override
//    public void draw(Graphics2D g2)
//    {
//        double xAttacker = attacker.centerX();
//        double yAttacker = attacker.centerY();
//         
//        double angle = Math.atan2(
//                        target.centerY() - yAttacker,
//                        target.centerX() - xAttacker); 
//        
//        xHead = Math.cos(angle)*distHeadTower + xAttacker; // x
//        yHead = Math.sin(angle)*distHeadTower + yAttacker; // y
//        
//        xTail = Math.cos(angle)*(distHeadTower - LENGTH) + xAttacker; // x
//        yTail = Math.sin(angle)*(distHeadTower - LENGTH) + yAttacker; // y
//        
//        
//        
//        
//        int widthPoint = 2;
//        
//        Polygon p = new Polygon();
//       
//        Point vector = new Point((int) (xHead - xTail), (int) (yHead - yTail));
//        
//       
//        Point v = new Point((int)(vector.x * 0.6), 
//                                      (int)(vector.y * 0.6));
//
//        
//        g2.setColor(COLOR);
//        g2.drawLine((int) xTail + v.x,(int) yTail + v.y,(int) xTail,(int) yTail);
//        
//        
//        
//        
//        Point vRight = new Point((int)(vector.y / Math.sqrt(vector.x*vector.x+vector.y*vector.y) * widthPoint),
//                                  (int)( -vector.x / Math.sqrt(vector.x*vector.x+vector.y*vector.y) * widthPoint));
//        
//        Point vLeft = new Point((int)(vRight.x * -1),
//                                  (int)(vRight.y * -1));
//        
//        
//        p.addPoint((int)xHead,(int)yHead);
//        p.addPoint((int)(xTail + v.x + vRight.x), (int)(yTail + v.y + vRight.y));
//        p.addPoint((int)(xTail + v.x + vLeft.x), (int)(yTail + v.y + vLeft.y));
//        
//        // draw
//        g2.setColor(Color.GRAY);
//        g2.fillPolygon(p);
//        
//    }
    
    // execute attack
    @Override
    public void animate(long t)
    {
        if(!finished)
        {
            distHeadTower += t * speed;
            
            double diffX       = target.centerX() - attacker.centerX();
            double diffY       = target.centerY() - attacker.centerY();  
            double distanceMax = Math.sqrt(diffX * diffX + diffY * diffY);
            
            if (distHeadTower >= distanceMax)
            {
                endAttack();
                finished = true;
                
                targets();
                finished = true;
            }
        }
    }

}
