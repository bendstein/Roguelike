package creatureitem.item.behavior;

import creatureitem.Creature;
import creatureitem.item.Item;
import creatureitem.spell.Spell;

import java.util.Objects;

public class Castable extends ItemBehavior {

    /**
     * The spell this item casts when casted
     */
    protected Spell onCast;

    public Castable(Item item, Spell onCast) {
        super(item);
        this.onCast = onCast;
    }

    //<editor-fold desc="Getters and Setters">
    public Spell getOnCast() {
        return onCast;
    }

    public void setOnCast(Spell onCast) {
        this.onCast = onCast;
    }
    //</editor-fold>

    @Override
    public void assignCaster(Creature c) {
        if(onCast != null)
            onCast.setCaster(c);
    }

    @Override
    public Castable copy() {
        return new Castable(item, onCast);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Castable)) return false;
        Castable castable = (Castable) o;
        return Objects.equals(onCast, castable.onCast);
    }

    @Override
    public int hashCode() {
        return Objects.hash(onCast);
    }
}
