package world.thing;

import creatureitem.Creature;

public class StairsBehavior extends ThingBehavior {


    public StairsBehavior(Stairs thing) {
        super(thing);
    }

    @Override
    public boolean onInteract(Creature c) {
        if(((Stairs)thing).getDestination() == null) {
            ((Stairs)thing).getLevel().getDungeon().getBuilder().clear();
            ((Stairs)thing).getLevel().getDungeon().getBuilder().setDimensions(65, 45);
            Stairs destination = ((Stairs)thing).getLevel().getDungeon().generateNextFloor((Stairs) thing, c);
            ((Stairs)thing).setDestination(destination);
        }
        return c.getAi().useStairs();
    }

    public ThingBehavior copy() {
        return new StairsBehavior((Stairs)thing);
    }
}
