package world.thing;

import creatureitem.Creature;

public class EntranceBehavior extends StairsBehavior {

    public EntranceBehavior(Entrance thing) {
        super(thing);
    }

    @Override
    public boolean onInteract(Creature c) {
        ((Entrance)thing).getDungeon().getBuilder().clear();
        ((Entrance)thing).getDungeon().getBuilder().setDimensions(65, 45);
        Stairs top = ((Entrance)thing).getDungeon().generateNextFloor((Entrance)thing, c);
        ((Entrance)thing).setDestination(top);
        return c.getAi().useStairs();
    }

    public ThingBehavior copy() {
        return new EntranceBehavior((Entrance)thing);
    }

}
