package creatureitem.item.behavior.equipable.weapon;

import creatureitem.Creature;
import creatureitem.effect.Effect;
import creatureitem.effect.damage.Damage;
import creatureitem.item.Item;
import creatureitem.item.behavior.equipable.Slot;
import creatureitem.spell.Spell;

import java.util.Objects;

public class Ammo extends Weapon {

    /**
     * The type of ammo this item is
     */
    String type;

    /**
     * The spell this ammo casts on impact
     */
    Spell onHit;

    public Ammo(Item item, Damage damage, String type, Effect... onEquip) {
        super(item, damage, new EquipSlot(new Slot(Slot.QUIVER, 1)), onEquip);
        this.type = type;
    }

    public Ammo(Item item, Damage damage, String type, Spell onHit, Effect... onEquip) {
        super(item, damage, new EquipSlot(new Slot(Slot.QUIVER, 1)), onEquip);
        this.type = type;
        this.onHit = onHit;
    }

    public Ammo(Item item, Damage damage, String type, int toHitMod, Effect... onEquip) {
        super(item, damage, new EquipSlot(new Slot(Slot.QUIVER, 1)), toHitMod, onEquip);
        this.type = type;
    }

    public Ammo(Item item, Damage damage, String type, int toHitMod, Spell onHit, Effect... onEquip) {
        super(item, damage, new EquipSlot(new Slot(Slot.QUIVER, 1)), toHitMod, onEquip);
        this.type = type;
        this.onHit = onHit;
    }

    public Ammo(Item item, Damage damage, String type, EquipSlot slot, int toHitMod, Spell onHit, Effect... onEquip) {
        super(item, damage, slot, toHitMod, onEquip);
        this.type = type;
        this.onHit = onHit;
    }

    //<editor-fold desc="Getters and Setters">
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Spell getOnHit() {
        return onHit;
    }

    public void setOnHit(Spell onHit) {
        this.onHit = onHit;
    }
    //</editor-fold>

    @Override
    public void attack(Creature wielder, Creature target) {
        if(wielder == null || target == null) return;

        /*
         * Make sure the wielder is the caster for the "on hit" spell
         */
        assignCaster(wielder);

        /*
         * TODO: wielder attacks target
         */

    }

    @Override
    public void cast(Creature target) {
        if(onHit == null || onHit.getCaster() == null) return;

        onHit.getCaster().cast(onHit, target.getLocation());
    }

    @Override
    public void assignCaster(Creature c) {
        super.assignCaster(c);

        if(onHit != null) {
            onHit.setCaster(c);
        }
    }

    @Override
    public Ammo copy() {
        Ammo a = new Ammo(null, damage == null? null : damage.copy(), type, slot == null? slot : slot.copy(), toHitMod, onHit == null? null : onHit.copy());
        Effect[] e = new Effect[onEquip.length];
        for(int i = 0; i < e.length; i++) {
            if(onEquip[i] != null) e[i] = onEquip[i].makeCopy(onEquip[i]);
        }
        a.setOnEquip(e);
        return a;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Ammo)) return false;
        if (!super.equals(o)) return false;
        Ammo ammo = (Ammo) o;
        return Objects.equals(type, ammo.type) &&
                Objects.equals(onHit, ammo.onHit);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), type, onHit);
    }
}
