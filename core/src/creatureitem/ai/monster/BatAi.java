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
    public void onUpdate() {
        super.onUpdate();
        //If there's an adjacent, enemy creature, attack it
        boolean attack = false;
        boolean pickup = false;
        Creature foe = null;
        for(int i = -1; i < 2; i++) {
            for(int j = -1; j < 2; j++) {
                foe = creature.getLevel().getCreatureAt(creature.getX() + i, creature.getY() + j);
                if(foe != null && foe.canSee(creature.getX() + i, creature.getY() + j) && foe.getTeam() != creature.getTeam()) {

                    attack = true;
                }
                else if(i == 0 && j == 0 && creature.getLevel().getItemAt(creature.getX() + i, creature.getY() + j) != null && creature.getLevel().getItemAt(creature.getX() + i, creature.getY() + j).hasProperty("eat"))
                    if((float)creature.getHunger()/creature.getHungerMax() < .5) {
                        pickup = true;
                    }

            }
        }

        //If the creature didn't act, move around twice
        if(!attack && !pickup) {
            wander(2);
        }
        //Else, act then wander just once
        else {
            if(attack) {
                if(foe != null) creature.attack(foe);
            }
            if(pickup)
                creature.pickUp();

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
    public CreatureAi copy() {
        return new BatAi(creature);
    }
}
