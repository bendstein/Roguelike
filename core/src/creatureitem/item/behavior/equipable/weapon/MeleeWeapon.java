package creatureitem.item.behavior.equipable.weapon;

import creatureitem.Creature;
import creatureitem.effect.Effect;
import creatureitem.effect.damage.Damage;
import creatureitem.item.Item;
import creatureitem.spell.Spell;

import java.util.Objects;

public class MeleeWeapon extends Weapon {

    /**
     * Spell which the item applies on contact
     */
    protected Spell onHit;

    /**
     * The rate at which the item applies the onHit spell
     */
    protected float castRate;

    public MeleeWeapon(Item item, Damage damage, EquipSlot slot, int toHitMod, Effect ... onEquip) {
        super(item, damage, slot, toHitMod, onEquip);
        onHit = null;
        castRate = 0;
    }

    public MeleeWeapon(Item item, Damage damage, EquipSlot slot, int toHitMod, int range, Effect ... onEquip) {
        super(item, damage, slot, toHitMod, onEquip);
        this.range = range;
        onHit = null;
        castRate = 0;
    }

    public MeleeWeapon(Item item, Damage damage, EquipSlot slot, int toHitMod, Spell onHit, float castRate, Effect ... onEquip) {
        super(item, damage, slot, toHitMod, onEquip);
        this.onHit = onHit;
        this.castRate = castRate;
    }

    public MeleeWeapon(Item item, Damage damage, EquipSlot slot, int toHitMod, int range, Spell onHit, float castRate, Effect ... onEquip) {
        super(item, damage, slot, toHitMod, onEquip);
        this.range = range;
        this.onHit = onHit;
        this.castRate = castRate;
    }

    //<editor-fold desc="Getters and Setters">
    public Spell getOnHit() {
        return onHit;
    }

    public void setOnHit(Spell onHit) {
        this.onHit = onHit;
    }

    public float getCastRate() {
        return castRate;
    }

    public void setCastRate(float castRate) {
        this.castRate = castRate;
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

        if(onHit.getCaster().getLevel().getRandom().nextFloat() < castRate) {
            onHit.getCaster().cast(onHit, target.getLocation());
        }

    }

    @Override
    public void assignCaster(Creature c) {
        super.assignCaster(c);

        if(onHit != null) {
            onHit.setCaster(c);
        }
    }

    @Override
    public MeleeWeapon copy() {
        MeleeWeapon a = new MeleeWeapon(null, damage == null? null : damage.copy(),
                slot == null? null : slot.copy(), range, toHitMod, onHit == null? null : onHit.copy(), castRate, onEquip);

        Effect[] e = new Effect[onEquip.length];
        for(int i = 0; i < e.length; i++)
            if(onEquip[i] != null) e[i] = onEquip[i].makeCopy(onEquip[i]);
        a.setOnEquip(e);

        return a;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MeleeWeapon)) return false;
        if (!super.equals(o)) return false;
        MeleeWeapon that = (MeleeWeapon) o;
        return Float.compare(that.castRate, castRate) == 0 &&
                Objects.equals(onHit, that.onHit);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), onHit, castRate);
    }
}
