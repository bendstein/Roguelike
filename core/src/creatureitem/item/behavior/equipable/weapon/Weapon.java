package creatureitem.item.behavior.equipable.weapon;

import creatureitem.Creature;
import creatureitem.effect.Effect;
import creatureitem.effect.damage.Damage;
import creatureitem.item.Item;
import creatureitem.item.behavior.equipable.Equipable;

import java.util.Objects;
import java.util.Random;

public abstract class Weapon extends Equipable {

    /**
     * The weapon's damage
     */
    Damage damage;

    /**
     * Modifier to any hit rolls
     */
    protected int toHitMod;

    /**
     * The weapon's attack range
     */
    int range;

    public Weapon(Item item, Damage damage, EquipSlot slot, Effect... onEquip) {
        super(item, slot, onEquip);
        this.damage = damage;
        this.range = 1;
        this.toHitMod = 0;
    }

    public Weapon(Item item, Damage damage, EquipSlot slot, int toHitMod, Effect... onEquip) {
        super(item, slot, onEquip);
        this.damage = damage;
        this.range = 1;
        this.toHitMod = toHitMod;
    }

    //<editor-fold desc="Getters and Setters">
    public Damage getDamage() {
        return damage;
    }

    public int getDamage(Random r) {
        if(damage == null) return 0;
        return damage.getDamage(r);
    }

    public void setDamage(Damage damage) {
        this.damage = damage;
    }

    public int getRange() {
        return range;
    }

    public void setRange(int range) {
        this.range = range;
    }

    public int getToHitMod() {
        return toHitMod;
    }

    public void setToHitMod(int toHitMod) {
        this.toHitMod = toHitMod;
    }

    //</editor-fold>

    public abstract void attack(Creature wielder, Creature target);

    public abstract void cast(Creature target);

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Weapon)) return false;
        if (!super.equals(o)) return false;
        Weapon weapon = (Weapon) o;
        return toHitMod == weapon.toHitMod &&
                range == weapon.range &&
                Objects.equals(damage, weapon.damage);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), damage, toHitMod, range);
    }
}
