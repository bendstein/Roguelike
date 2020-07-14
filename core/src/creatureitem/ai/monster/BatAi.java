package creatureitem.ai.monster;

import creatureitem.Creature;
import creatureitem.ai.CreatureAi;
import world.Tile;

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
    public void onUpdate() {

        //Update hunger
        if(creature.getLevel().getTurn() % 5 == 0) creature.modifyHunger(-1);

        //Move around
        wander();

        //If there's an adjacent, enemy creature, attack it
        boolean attack = false;
        for(int i = -1; i < 2; i++) {
            for(int j = -1; j < 2; j++) {
                Creature foe = creature.getLevel().getCreatureAt(creature.getX() + i, creature.getY() + j);
                if(foe != null && foe.canSee(creature.getX() + i, creature.getY() + j) && foe.getTeam() != creature.getTeam()) {
                    creature.attack(foe);
                    attack = true;
                }
                if(i == 0 && j == 0 && creature.getLevel().getItemAt(creature.getX() + i, creature.getY() + j) != null && creature.getLevel().getItemAt(creature.getX() + i, creature.getY() + j).hasProperty("eat"))
                    if((float)creature.getHunger()/creature.getHungerMax() < .5) {
                        creature.pickUp();
                    }

            }
        }

        //If the creature didn't attack, move around some more
        if(!attack)
            wander();
    }

    /**
     * Perform any actions the creature does on entering a new tile
     * @param x X coordinate
     * @param y Y coordinate
     * @param tile The tile they're trying to enter
     */
    public void onEnter(int x, int y, Tile tile) {

        if(Creature.canEnter(x, y, creature.getLevel())) {
            creature.setCoordinates(x, y);
        }
    }
}
