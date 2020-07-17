package world.thing;

import creatureitem.Creature;

public class ThingBehavior {

    /**
     * The thing associated with this behavior
     */
    protected Thing thing;

    public ThingBehavior(Thing thing) {
        this.thing = thing;
        thing.setBehavior(this);
    }

    public void onInteract() {

    }

    public boolean onInteract(Creature c) {
        onInteract();
        return false;
    }

    //<editor-fold desc="Getters and Setters">
    public Thing getThing() {
        return thing;
    }

    public void setThing(Thing thing) {
        this.thing = thing;
    }
    //</editor-fold>
}
