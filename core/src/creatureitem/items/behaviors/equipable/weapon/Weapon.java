package creatureitem.items.behaviors.equipable.weapon;

import creatureitem.Creature;
import creatureitem.effect.Effect;
import creatureitem.effect.damage.Damage;
import creatureitem.items.Item;
import creatureitem.items.behaviors.equipable.Equipable;

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

    public Weapon(Item item, Damage damage, Effect... onEquip) {
        super(item, onEquip);
        this.damage = damage;
        this.range = 1;
        this.toHitMod = 0;
    }

    public Weapon(Item item, Damage damage, int toHitMod, Effect... onEquip) {
        super(item, onEquip);
        this.damage = damage;
        this.range = 1;
        this.toHitMod = toHitMod;
    }

    public abstract void attack(Creature wielder, Creature target);

    public abstract void cast(Creature target);

    //<editor-fold desc="Getters and Setters">
    public Damage getDamage() {
        return damage;
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
}
