package world.thing;

public class DoorBehavior extends ThingBehavior {

    public DoorBehavior(Thing door) {
        super(door);
    }

    @Override
    public void onInteract() {
        thing.setOpen(!thing.isOpen());
    }

}
