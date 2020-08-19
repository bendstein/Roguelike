package world.thing;

import creatureitem.Creature;

public class ThingBehavior {

    /**
     * The thing associated with this behavior
     */
    protected Thing thing;

    public ThingBehavior(Thing thing) {
        this.thing = thing;
        if(thing != null) thing.setBehavior(this);
    }

    public void onInteract() {

    }

    public boolean onInteract(Creature c) {
        onInteract();
        return false;
    }

    public ThingBehavior copy() {
        return new ThingBehavior(thing);
    }

    //<editor-fold desc="Getters and Setters">
    public Thing getThing() {
        return thing;
    }

    public ThingBehavior setThing(Thing thing) {
        this.thing = thing;
        if(thing != null && (thing.getBehavior() == null || !thing.getBehavior().equals(this))) thing.setBehavior(this);
        return this;
    }
    //</editor-fold>
}
