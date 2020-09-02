package creatureitem.item.behavior;

import creatureitem.Creature;
import creatureitem.item.Item;
import creatureitem.spell.Spell;

import java.util.Objects;

public class Usable extends ItemBehavior {

    /**
     * The spell that it cast when this item is used
     */
    protected Spell onUse;

    public Usable(Item item) {
        super(item);
        this.onUse = null;
    }

    public Usable(Item item, Spell onUse) {
        super(item);
        this.onUse = onUse;
    }

    //<editor-fold desc="Getters and Setters">
    public Spell getOnUse() {
        return onUse;
    }

    public void setOnUse(Spell onUse) {
        this.onUse = onUse;
    }
    //</editor-fold>

    @Override
    public void assignCaster(Creature c) {
        if(onUse != null) {
            onUse.setCaster(c);
        }
    }

    @Override
    public Usable copy() {
        return new Usable(item, onUse == null? null : onUse.copy());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Usable)) return false;
        Usable usable = (Usable) o;
        return Objects.equals(onUse, usable.onUse);
    }

    @Override
    public int hashCode() {
        return Objects.hash(onUse);
    }
}
