package creatureitem.items.behaviors.equipable.weapon;

import creatureitem.Creature;
import creatureitem.effect.Effect;
import creatureitem.effect.damage.Damage;
import creatureitem.items.Item;
import creatureitem.spell.Spell;
import world.geometry.Cursor;

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
        super(item, damage, toHitMod, onEquip);
        this.range = range;
        this.ammoType = ammoType;
        this.onShoot = null;
        this.castRate = 0;
    }

    public RangedWeapon(Item item, Damage damage, int range, int toHitMod, String ammoType, Spell onShoot, float castRate, Effect ... onEquip) {
        super(item, damage, toHitMod, onEquip);
        this.range = range;
        this.ammoType = ammoType;
        this.onShoot = onShoot;
        this.castRate = castRate;
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
}
