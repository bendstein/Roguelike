package world.thing;

import creatureitem.Creature;

public class DoorBehavior extends ThingBehavior {

    public DoorBehavior(Thing door) {
        super(door);
    }

    @Override
    public boolean onInteract(Creature c) {
        if(c.getLevel().getCreatureAt(thing.getX(), thing.getY()) != null) {
            c.notify(thing.isOpen()? "You cannot close the door; %s is in the way!" :
                    "You cannot open the door; %s is in the way!", c.getLevel().getCreatureAt(thing.getX(), thing.getY()).getName());
            return false;
        }
        thing.setOpen(!thing.isOpen());

        c.getLevel().calculateOrientations();

        return true;
    }

    public ThingBehavior copy() {
        return new DoorBehavior(thing);
    }

}
