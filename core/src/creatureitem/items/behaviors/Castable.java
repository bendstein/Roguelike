package creatureitem.items.behaviors;

import creatureitem.Creature;
import creatureitem.items.Item;
import creatureitem.spell.Spell;

public class Castable extends ItemBehavior {

    /**
     * The spell this item casts when casted
     */
    protected Spell onCast;

    public Castable(Item item, Spell onCast) {
        super(item);
        this.onCast = onCast;
    }

    @Override
    public void assignCaster(Creature c) {
        if(onCast != null)
            onCast.setCaster(c);
    }

    //<editor-fold desc="Getters and Setters">
    public Spell getOnCast() {
        return onCast;
    }

    public void setOnCast(Spell onCast) {
        this.onCast = onCast;
    }
    //</editor-fold>
}
