package creature.ai.monster;

import creature.Creature;
import creature.ai.CreatureAi;
import world.Tile;

public class BatAi extends CreatureAi {

    /**
     * Assign the AI to its creature
     *
     * @param creature The creature following this behavior set
     */
    public BatAi(Creature creature) {
        super(creature);
    }

    @Override
    public void onUpdate() {
        wander();
        wander();
    }

    /**
     * Perform any actions the creature does on entering a new tile
     * @param x X coordinate
     * @param y Y coordinate
     * @param tile The tile they're trying to enter
     */
    public void onEnter(int x, int y, Tile tile) {

        if(Creature.canEnter(x, y, creature.getWorld()) || creature.getWorld().getTileAt(x, y) == Tile.BOUNDS) {
            creature.setCoordinates(x, y);
        }
    }
}
