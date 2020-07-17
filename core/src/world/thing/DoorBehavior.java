package world.thing;

import world.Tile;

public class DoorBehavior extends ThingBehavior {

    //private Tile closedDoor, openDoor;

    public DoorBehavior(Thing door/*, Tile openDoor*/) {
        super(door);
        //closedDoor = door.getTile();
        //this.openDoor = openDoor;
    }

    @Override
    public void onInteract() {
        if(thing.isOpen()) {
            thing.setOpen(false);
            //thing.setTile(closedDoor);
        }
        else {
            thing.setOpen(true);
            //thing.setTile(openDoor);
        }
    }

    //<editor-fold desc="Getters and Setters">
    /*
    public Tile getClosedDoor() {
        return closedDoor;
    }

    public void setClosedDoor(Tile closedDoor) {
        this.closedDoor = closedDoor;
    }

    public Tile getOpenDoor() {
        return openDoor;
    }

    public void setOpenDoor(Tile openDoor) {
        this.openDoor = openDoor;
    }

     */
    //</editor-fold>
}
