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

package puArcade.princetonTD.creatures;

public class CreatureWave {
	
	// number of creatures in wave
    private final int N;

    // creature type
    private final Creature CREATURE_TYPE;

    // description
    private final String DESCRIPTION;
    
    // Constructor
    public CreatureWave(int n, Creature creatureType,
            long interval, String description)
    {
        N = n;
        CREATURE_TYPE = creatureType;
        DESCRIPTION = description;
    }

    // Constructor w/o description
    public CreatureWave(int n, Creature creatureType,
            long interval)
    {
        this(n, creatureType, interval, "");
    }

    // return number
    public int getN()
    {
        return N;
    }

    // return copy of creature
    public Creature getCopy()
    {
        return CREATURE_TYPE.copy();
    }

    // return description
    public String getDescription()
    {
        return DESCRIPTION;
    }

    // return wave as String
    public String toString()
    {
        String type;
        
        if(CREATURE_TYPE.getType() == Creature.TYPE_AIR)
            type = "AIR";
        else
            type = "LAND";

        return N + "x " +CREATURE_TYPE.getName() + " ("
                + type + ") - "
                + "Health : " + CREATURE_TYPE.getHealthMax() + ", "
                + "Drop : " + CREATURE_TYPE.getReward();
    }

    public static final double SLOW = 30.0;
    public static final double FAST = 60.0;
    public static final double NORMAL = 50.0;
    public static final double HEALTH_TOUGH = 1.5;
    public static final double HEALTH_WEAK = 0.8;
    public static final double HEALTH_AIR = 0.8;
    public static final double HEALTH_MINIBOSS = 4.0;
    public static final double HEALTH_BOSS = 8.0;
    
    
    // Generate wave
    public static CreatureWave generateWave(int currentIndex)
    {
        int waveNumber = currentIndex;
        int waveUnit = waveNumber % 10;
        
        final long HEALTH_NORMAL = fHealth(waveNumber);
        final long WAVE_GAIN = fWaveGain2(waveNumber)/*fWaveGain(HEALTH_NORMAL)*/;

        switch (waveUnit)
        {
        
        case 1: // 5 normal
            
            return new CreatureWave(5, new Sheep(HEALTH_NORMAL,
                    (int) Math.ceil(WAVE_GAIN / 15.0), NORMAL),
                    getInterval(NORMAL));

        case 2: // 10 normal
            return new CreatureWave(10, new BlackSheep(HEALTH_NORMAL,
                    (int) Math.ceil(WAVE_GAIN / 10.0), NORMAL),
                    getInterval(NORMAL));

        case 3: // 10 flying
            return new CreatureWave(10, new Eagle((int) (HEALTH_NORMAL * HEALTH_AIR),
                    (int) Math.ceil(WAVE_GAIN / 10.0), NORMAL),
                    getInterval(NORMAL));

        case 4: // 10 tough
            return new CreatureWave(10, new Rhinoceros((int) (HEALTH_NORMAL * HEALTH_TOUGH),
                    (int) Math.ceil(WAVE_GAIN / 10.0), SLOW),
                    getInterval(SLOW));

        case 5: // 10 fast
            return new CreatureWave(10, new Spider((int) (HEALTH_NORMAL * HEALTH_WEAK),
                    (int) Math.ceil(WAVE_GAIN / 10.0), FAST),
                    getInterval(FAST));

        case 6: // 15 normal
            return new CreatureWave(15, new Peasant(HEALTH_NORMAL,
                    (int) Math.ceil(WAVE_GAIN / 15.0), NORMAL),
                    getInterval(NORMAL));

        case 7: // 15 tough
            return new CreatureWave(15, new Elephant((int) (HEALTH_NORMAL * HEALTH_TOUGH),
                    (int) Math.ceil(WAVE_GAIN / 15.0), SLOW),
                    getInterval(SLOW));

        case 8: // 30 flying
            return new CreatureWave(30, new Pigeon((int) (HEALTH_NORMAL * HEALTH_AIR),
                    (int) Math.ceil(WAVE_GAIN / 30.0), NORMAL),
                    getInterval(NORMAL));

        case 9: // 3 mini-boss
            return new CreatureWave(3, new Spider((int) (HEALTH_NORMAL * HEALTH_MINIBOSS),
                    (int) Math.ceil(WAVE_GAIN / 3.0), SLOW),
                    getInterval(SLOW));

        default: // boss
            return new CreatureWave(1, new LargeSpider((int) (HEALTH_NORMAL * HEALTH_BOSS),
                    (int) Math.ceil(WAVE_GAIN), SLOW),
                    getInterval(SLOW));
        }
    }

    // Intervals of wave
    public static long getInterval(double speed)
    {
        return (long) (1000.0 / (speed / 40.0));
    }
    
    
    // Increase in health
    public static long fHealth(int waveNumber)
    {
        // 0.01 * waveNumber^4 + 0.25 * waveNumber + 70
        // [3] (long) (waveNumber * waveNumber / 0.8 + 5);
        return (long) (waveNumber * waveNumber * waveNumber / 20 + 30);
    }

    // Increase in rewards
    /*
    private static long fWaveGain(long healthCreature)
    {
        return (long) (0.08 * healthCreature) + 30;
    } */
    private static long fWaveGain2(long waveNumber)
    {
        return (long) (2.5 * waveNumber) + 1;
    }
	
}
