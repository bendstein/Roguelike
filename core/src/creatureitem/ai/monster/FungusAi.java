package creatureitem.ai.monster;

import creatureitem.Creature;
import creatureitem.ai.CreatureAi;
import creatureitem.generation.CreatureItemFactory;

public class FungusAi extends CreatureAi {


    //<editor-fold desc="Instance Variables">
    /**
     * Factory for creating more fungi
     */
    private CreatureItemFactory factory;

    /**
     * Number of spreads this fungus has done
     */
    private int spread;

    /**
     * Probability for the fungus to spread on a turn
     */
    private final double SPREAD_PROBABILITY = 0.018;

    /**
     * Maximum number of spreads this fungus can do
     */
    private final int MAX_SPREAD = 3;
    //</editor-fold>

    public FungusAi(Creature creature, CreatureItemFactory factory) {
        super(creature);
        this.factory = factory;
        spread = 0;
    }


    //<editor-fold desc="Getters and Setters">
    public int getSpread() {
        return spread;
    }

    public void setSpread(int spread) {
        this.spread = spread;
    }
    //</editor-fold>

    public void onUpdate() {
        if(creature.getWorld().getRandom().nextInt(1000)/1000d <= SPREAD_PROBABILITY && spread < MAX_SPREAD)
            spread();
        for(int i = -1; i < 2; i++) {
            for(int j = -1; j < 2; j++) {
                Creature foe = creature.getWorld().getCreatureAt(creature.getX() + i, creature.getY() + j);
                if(foe != null && foe.getTeam() != creature.getTeam()) {
                    creature.attack(foe);
                    return;
                }
            }
        }
    }

    private void spread() {
        int x = creature.getX() + creature.getWorld().getRandom().nextInt(5) - 3;
        int y = creature.getY() + creature.getWorld().getRandom().nextInt(5) - 3;

        if(!Creature.canEnter(x, y, creature.getWorld()))
            return;

        Creature child = factory.newFungus();
        creature.getWorld().addAt(x, y, child);
        spread++;

        creature.doAction("spread to a nearby tile!");
    }
}
