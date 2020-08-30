package creatureitem.ai.monster;

import actors.creatures.CreatureActor;
import creatureitem.Creature;
import creatureitem.ai.CreatureAi;
import game.Main;
import world.Tile;
import world.geometry.floatPoint;

public class BatAi extends CreatureAi {

    /**
     * Assign the AI to its creature
     *
     * @param creature The creature following this behavior set
     */
    public BatAi(creatureitem.Creature creature) {
        super(creature);
    }

    @Override
    public void onAct() {

        boolean act = false;

        //If there's an adjacent, enemy creature, attack it
        act = attackRandom();

        //If they didn't act, and there's an item on the ground, pick it up
        if(!act) {
            act = pickup();
        }

        //If the creature didn't act, move around
        if(!act) {
            wander(1);
        }

    }

    /**
     * Perform any actions the creature does on entering a new tile
     * @param x X coordinate
     * @param y Y coordinate
     * @param tile The tile they're trying to enter
     */
    public void onEnter(int x, int y, Tile tile) {

        if(Creature.canEnter(x, y, creature.getLevel())) {
            if(creature.getActor() != null)
                ((CreatureActor)creature.getActor()).setCurrentLocation(new floatPoint(creature.getX() * Main.getTileWidth(), creature.getY() * Main.getTileHeight()));
            creature.setCoordinates(x, y);

            if(creature.getActor() != null)
                ((CreatureActor)creature.getActor()).setDestination(new floatPoint(x * Main.getTileWidth(), y * Main.getTileHeight()));
        }
    }

    @Override
    public BatAi copy() {
        return new BatAi(creature);
    }
}
