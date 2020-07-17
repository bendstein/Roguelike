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
    private final double SPREAD_PROBABILITY = 0.005;

    /**
     * Maximum number of spreads this fungus can do
     */
    private final int MAX_SPREAD = 3;

    /**
     * Minimum distance the fungus has to be from its spawn
     */
    private final int MIN_DIST = 1;

    /**
     * Minimum distance the fungus has to be from its spawn
     */
    private final int MAX_DIST = 3;
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
        super.onUpdate();
        //Low chance for the fungus to spread to a nearby tile
        if(creature.getLevel().getRandom().nextDouble() <= SPREAD_PROBABILITY && spread < MAX_SPREAD)
            spread();

        //If there's an adjacent creature, attack it
        for(int i = -1; i < 2; i++) {
            for(int j = -1; j < 2; j++) {
                Creature foe = creature.getLevel().getCreatureAt(creature.getX() + i, creature.getY() + j);
                if(foe != null && foe.canSee(creature.getX() + i, creature.getY() + j) && foe.getTeam() != creature.getTeam()) {
                    creature.attack(foe);
                    return;
                }
            }
        }
    }

    /**
     * Create a new Fungus creature in a nearby, random tile.
     */
    private void spread() {
        int x = creature.getX() + (creature.getLevel().getRandom().nextBoolean()? -1 : 1) * creature.getLevel().getRandom().nextInt(MAX_DIST - MIN_DIST) + MIN_DIST;
        int y = creature.getY() + (creature.getLevel().getRandom().nextBoolean()? -1 : 1) * creature.getLevel().getRandom().nextInt(MAX_DIST - MIN_DIST) + MIN_DIST;

        if(!Creature.canEnter(x, y, creature.getLevel()))
            return;

        Creature child = factory.newFungus();
        creature.getLevel().addAt(x, y, child);
        spread++;

        creature.doAction("spread to a nearby tile!");
    }
}
