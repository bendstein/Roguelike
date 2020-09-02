package creatureitem.item.behavior.equipable.armor;

import creatureitem.effect.Effect;
import creatureitem.item.Item;
import creatureitem.item.behavior.equipable.Equipable;

import java.util.Objects;

public class Armor extends Equipable {

    /**
     * The armor's damage reduction
     */
    int protection;

    public Armor(Item item, int protection, EquipSlot slot, Effect ... onEquip) {
        super(item, slot, onEquip);
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

    @Override
    public Armor copy() {
        Armor a = new Armor(null, protection, slot == null? null : slot.copy());
        Effect[] e = new Effect[onEquip.length];
        for(int i = 0; i < e.length; i++)
            if(onEquip[i] != null) e[i] = onEquip[i].makeCopy(onEquip[i]);
        a.setOnEquip(e);

        return a;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Armor)) return false;
        if (!super.equals(o)) return false;
        Armor armor = (Armor) o;
        return protection == armor.protection;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), protection);
    }
}
