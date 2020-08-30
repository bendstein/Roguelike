package creatureitem.items.behaviors.equipable.armor;

import creatureitem.effect.Effect;
import creatureitem.items.Item;
import creatureitem.items.behaviors.equipable.Equipable;

public class Armor extends Equipable {

    /**
     * The armor's damage reduction
     */
    int protection;

    public Armor(Item item, int protection, Effect... onEquip) {
        super(item, onEquip);
        this.protection = protection;
    }

    //<editor-fold desc="Getters and Setters">
    public int getProtection() {
        return protection;
    }

    public void setProtection(int protection) {
        this.protection = protection;
    }
    //</editor-fold>
}
