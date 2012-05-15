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
import java.util.Enumeration;

import puArcade.princetonTD.creatures.Creature;
import puArcade.princetonTD.game.Game;
import puArcade.princetonTD.towers.Tower;


import android.graphics.Point;

abstract public class Attack extends Animation
{

    protected Creature target;
    protected Tower attacker;

    protected long damage;
    protected double radius;
    protected double coeffSlow;
   
    protected Game game;
    private ArrayList<AttackState> attackStates = new ArrayList<AttackState>();
   
    
    // constructor
    public Attack(int x, int y, Game game, Tower attacker, Creature target)
    {
        super(x, y);
        this.game       = game;
        this.attacker  	= attacker;
        this.target     = target;
    }
    
    // array of targets to attack
    public ArrayList<Creature> targets()
    {
        if(radius > 0.0)
            return damagedCreatures();
        else
        {
            if(target.canBeAttacked(attacker))
                target.damaged(damage,attacker.getOwner());
           
            ArrayList<Creature> a = new ArrayList<Creature>();
            a.add(target);
            return a;
        }
    }
    
   // array of creatures with damage from attack
    synchronized public ArrayList<Creature> damagedCreatures()
    {
        ArrayList<Creature> a = new ArrayList<Creature>();
       
        Point impact = new Point((int) target.centerX(), (int) target.centerY());
        long finalDamage;
        double distanceImpact;
       
        Enumeration<Creature> eCreatures = game.getCreatures().elements();
        Creature tmpCreature;
        while(eCreatures.hasMoreElements())
        {
            tmpCreature = eCreatures.nextElement();
           
            if(tmpCreature.canBeAttacked(attacker))
            {
                distanceImpact = Math.sqrt((tmpCreature.centerX()-impact.x)*(tmpCreature.centerX()-impact.x)+
                                           (tmpCreature.centerY()-impact.y)*(tmpCreature.centerY()-impact.y));
               
                if(distanceImpact <= radius)
                {
                    finalDamage = (long) (damage - (distanceImpact / radius * damage));
                    tmpCreature.damaged(finalDamage, attacker.getOwner());
                   
                    a.add(tmpCreature);
                }
            }
        }
       
        return a;
    }
    
    // add attack to attacks
    public void addAttackState(AttackState as)
    {
        attackStates.add(as);
    }
    
    // end attack
    protected void endAttack()
    {
        for(AttackState as : attackStates)
            as.endAttack(attacker, target);
    }
}
