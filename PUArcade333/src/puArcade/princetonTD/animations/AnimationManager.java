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
import java.util.NoSuchElementException;
import java.util.Vector;

import puArcade.princetonTD.game.Game;

public class AnimationManager implements Runnable {
	
	private static final long WAIT_TIME = 50;
    private Vector<Animation> animations = new Vector<Animation>();
    private Thread thread;
    private boolean inManagement;
    private boolean paused = false;
    private Object pause = new Object();
    private Game game;
    private long timeLeft = 0;
    
    // Constructor
    public AnimationManager(Game game)
    {
        this.game = game;
    }
    
    // Start
    public void start()
    {
        thread = new Thread(this);
        thread.start();
    }
    
    // add animation
    public void addAnimation(Animation animation)
    {
        animations.add(animation);
    }
    
    // Android does not allow subclasses to draw, so draw method has been removed
    
    // draw animations
//    public void drawAnimations(Graphics2D g2, int height)
//    {
//        try
//        {
//            Enumeration<Animation> eAnimations = animations.elements();
//            Animation animation;
//            while(eAnimations.hasMoreElements())
//            {
//                animation = eAnimations.nextElement();
//                
//                if(animation.getHeight() == height)
//                    animation.draw(g2);
//            }
//        }
//        
//        catch(NoSuchElementException nse)
//        {
//            System.err.println("Cannot find animation");
//        }
//    }
    
    // run manager
    public void run()
    {
       inManagement = true;

       ArrayList<Animation> deleteAnimations = new ArrayList<Animation>();
       Animation animation;
       
       while(inManagement)
       {
           timeLeft -= WAIT_TIME;
           
           if(timeLeft < 0)
           { 

               timeLeft = this.random(10000, 20000);
           }

           try
           {
               Enumeration<Animation> eAnimations = animations.elements();
               while(eAnimations.hasMoreElements())
               {
                   animation = eAnimations.nextElement();

                   if(animation.isFinished())
                       deleteAnimations.add(animation);
                   else
                       animation.animate((long)(WAIT_TIME*game.getCoeffSpeed()));
               }
           }
           catch(NoSuchElementException nse)
           {
               System.err.println("Cannot find animation");
           }
 
           for(Animation deleteAnimation : deleteAnimations)
               animations.remove(deleteAnimation);
           deleteAnimations.clear();
         
           // pause
           try
           {
               synchronized (pause)
               {
                   if(paused)
                       pause.wait();
               } 
           } 
           catch (InterruptedException e1)
           {
               e1.printStackTrace();
           }
           
           try{
                Thread.sleep(WAIT_TIME);
           } 
           catch (InterruptedException e){
                e.printStackTrace();
           }
       }
    }
    
    private long random(int min, int max) {
    	return min + (int) Math.round(Math.random() * (max - min)); 
	}

	// Stop management
    public void stopAnimations()
    {
        inManagement = false;
    }

    // pause
    public void pause()
    {
        paused = true;
    }
    
    // unpause
    public void unpause()
    { 
        synchronized (pause)
        {
            paused = false;
            pause.notify();
        }
    }
    
    // remove manager
    public void destroy()
    {
        stopAnimations();
        animations.clear();
    }
	
}
