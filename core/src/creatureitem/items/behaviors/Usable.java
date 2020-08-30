package creatureitem.items.behaviors;

import creatureitem.Creature;
import creatureitem.items.Item;
import creatureitem.spell.Spell;

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

    @Override
    public void assignCaster(Creature c) {
        if(onUse != null) {
            onUse.setCaster(c);
        }
    }

    //<editor-fold desc="Getters and Setters">
    public Spell getOnUse() {
        return onUse;
    }

    public void setOnUse(Spell onUse) {
        this.onUse = onUse;
    }
    //</editor-fold>
}
