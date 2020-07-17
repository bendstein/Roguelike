package world.thing;

import creatureitem.Creature;

public class StairsBehavior extends ThingBehavior {


    public StairsBehavior(Stairs thing) {
        super(thing);
    }

    @Override
    public boolean onInteract(Creature c) {
        return c.getAi().useStairs();
    }
}
