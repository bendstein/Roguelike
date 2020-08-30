package creatureitem.items.behaviors.equipable;

import creatureitem.Creature;
import creatureitem.effect.Effect;
import creatureitem.items.Item;
import creatureitem.items.behaviors.ItemBehavior;

public abstract class Equipable extends ItemBehavior {

    /**
     * Effects that the item applies when equipped
     */
    protected Effect[] onEquip;

    /**
     * True if something has equipped this item
     */
    protected boolean isEquipped;

    public Equipable(Item item) {
        super(item);
        onEquip = null;
        isEquipped = false;
    }

    public Equipable(Item item, Effect ... onEquip) {
        super(item);
        this.onEquip = onEquip;
        isEquipped = false;
    }

    @Override
    public void assignCaster(Creature c) {
        if(onEquip != null) {
            for(Effect e : onEquip)
                e.setCaster(c);
        }

    }

    //<editor-fold desc="Getters and Setters">
    public Effect[] getOnEquip() {
        return onEquip;
    }

    public void setOnEquip(Effect[] onEquip) {
        this.onEquip = onEquip;
    }

    public boolean isEquipped() {
        return isEquipped;
    }

    public void setEquipped(boolean equipped) {
        isEquipped = equipped;
    }

    //</editor-fold>

}
