package creatureitem.item.behavior.equipable.weapon;

import creatureitem.Creature;
import creatureitem.effect.Effect;
import creatureitem.effect.damage.Damage;
import creatureitem.item.Item;
import creatureitem.item.behavior.equipable.Slot;
import creatureitem.spell.Spell;

import java.util.Objects;

public class RangedWeapon extends Weapon {

    /**
     * The type of ammo this weapon consumes
     */
    protected String ammoType;

    /**
     * Spell which the weapon casts when the weapon is shot
     */
    protected Spell onShoot;

    /**
     * The rate at which the weapon casts the above spell on impact.
     */
    protected float castRate;

    public RangedWeapon(Item item, Damage damage, int range, int toHitMod, String ammoType, Effect ... onEquip) {
        super(item, damage, new EquipSlot(new Slot(Slot.RANGED, 1)), toHitMod, onEquip);
        this.range = range;
        this.ammoType = ammoType;
        this.onShoot = null;
        this.castRate = 0;
    }

    public RangedWeapon(Item item, Damage damage, int range, int toHitMod, String ammoType, Spell onShoot, float castRate, Effect ... onEquip) {
        super(item, damage, new EquipSlot(new Slot(Slot.RANGED, 1)), toHitMod, onEquip);
        this.range = range;
        this.ammoType = ammoType;
        this.onShoot = onShoot;
        this.castRate = castRate;
    }

    public RangedWeapon(Item item, Damage damage, int range, EquipSlot slot, int toHitMod, String ammoType, Spell onShoot, float castRate, Effect ... onEquip) {
        super(item, damage, slot, toHitMod, onEquip);
        this.range = range;
        this.ammoType = ammoType;
        this.onShoot = onShoot;
        this.castRate = castRate;
    }

    //<editor-fold desc="Getters and Setters">
    public String getAmmoType() {
        return ammoType;
    }

    public void setAmmoType(String ammoType) {
        this.ammoType = ammoType;
    }

    public Spell getOnShoot() {
        return onShoot;
    }

    public void setOnShoot(Spell onShoot) {
        this.onShoot = onShoot;
    }

    public float getCastRate() {
        return castRate;
    }

    public void setCastRate(float castRate) {
        this.castRate = castRate;
    }

    //</editor-fold>

    public boolean canShoot(Item i) {
        if(i == null) return false;

        return (i.isAmmo() && i.getAmmoComponent().getType().equals(ammoType));
    }

    @Override
    public void attack(Creature wielder, Creature target) {
        if(wielder == null || target == null) return;

        /*
         * Make sure the wielder is the caster for the "on shoot" spell
         */
        assignCaster(wielder);

        /*
         * TODO: wielder shoots target
         */

    }

    @Override
    public void cast(Creature target) {
        if(onShoot == null || onShoot.getCaster() == null) return;

        if(onShoot.getCaster().getLevel().getRandom().nextFloat() < castRate) {
            onShoot.getCaster().cast(onShoot, target.getLocation());
        }

    }

    @Override
    public void assignCaster(Creature c) {
        super.assignCaster(c);

        if(onShoot != null) {
            onShoot.setCaster(c);
        }
    }

    @Override
    public RangedWeapon copy() {
        RangedWeapon a = new RangedWeapon(null, damage == null? null : damage.copy(), range,
                slot == null? null : slot.copy(), toHitMod, ammoType, onShoot == null? null : onShoot.copy(), castRate);

        Effect[] e = new Effect[onEquip.length];
        for(int i = 0; i < e.length; i++)
            if(onEquip[i] != null) e[i] = onEquip[i].makeCopy(onEquip[i]);
        a.setOnEquip(e);

        return a;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RangedWeapon)) return false;
        if (!super.equals(o)) return false;
        RangedWeapon that = (RangedWeapon) o;
        return Float.compare(that.castRate, castRate) == 0 &&
                Objects.equals(ammoType, that.ammoType) &&
                Objects.equals(onShoot, that.onShoot);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), ammoType, onShoot, castRate);
    }
}
