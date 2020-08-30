package creatureitem.item;

import creatureitem.Creature;
import creatureitem.effect.Effect;
import creatureitem.effect.damage.Damage;
import creatureitem.spell.Spell;

import java.util.Locale;
import java.util.Objects;

public class Weapon extends Equipable {

    /**
     * Amount of damage the weapon does
     */
    protected Damage weaponDamage;

    /**
     * Modifier to any hit rolls
     */
    protected int toHitMod;

    /**
     * The distance from which this weapon can attack from
     */
    protected int reach;

    protected Spell onHit;

    protected double impactSpellProbability;

    public Weapon(char glyph, String texturePath, String name, int worth, Damage weaponDamage, int toHitMod) {
        super(glyph, texturePath, name, worth);
        this.weaponDamage = weaponDamage;
        this.toHitMod = toHitMod;
        this.reach = 1;
        this.impactSpellProbability = 0d;
    }

    public Weapon(char glyph, String texturePath, String name, int worth, Damage weaponDamage, int toHitMod, String... properties) {
        super(glyph, texturePath, name, worth, properties);
        this.weaponDamage = weaponDamage;
        this.toHitMod = toHitMod;
        this.reach = 1;
        this.impactSpellProbability = 0d;
    }

    public Weapon(char glyph, String texturePath, String name, int worth, Damage throwDamage, Damage weaponDamage, int toHitMod, String... properties) {
        super(glyph, texturePath, name, worth, throwDamage, properties);
        this.weaponDamage = weaponDamage;
        this.toHitMod = toHitMod;
        this.reach = 1;
        this.impactSpellProbability = 0d;
    }

    public Weapon(String name, Damage weaponDamage, int toHitMod) {
        super(' ', "", name, 0);
        this.weaponDamage = new Damage(weaponDamage);
        this.toHitMod = toHitMod;
        this.reach = 1;
        this.impactSpellProbability = 0d;
    }

    public Weapon(char glyph, String texturePath, String name, int worth, Damage weaponDamage, Spell[] spells, Effect onEquip, Spell onHit, double impactSpellProbability, int toHitMod, int reach) {
        super(glyph, texturePath, name, worth, spells, onEquip);
        this.weaponDamage = weaponDamage;
        this.toHitMod = toHitMod;
        this.reach = reach;
        this.impactSpellProbability = impactSpellProbability;
        this.onHit = onHit == null? null : onHit.copyOf(onHit);

        if(this.onHit != null) {
            this.onHit.setIgnoreRange(true);
        }
    }

    public Weapon(char glyph, String texturePath, String name, int worth, Damage weaponDamage, Spell[] spells, Effect onEquip, Spell onHit, double impactSpellProbability, int toHitMod, int reach, String... properties) {
        super(glyph, texturePath, name, worth, spells, onEquip, properties);
        this.weaponDamage = weaponDamage;
        this.toHitMod = toHitMod;
        this.reach = reach;
        this.impactSpellProbability = impactSpellProbability;
        this.onHit = onHit == null? null : onHit.copyOf(onHit);

        if(this.onHit != null) {
            this.onHit.setIgnoreRange(true);
        }
    }

    public Weapon(char glyph, String texturePath, String name, int worth, Damage throwDamage, Damage weaponDamage, Spell[] spells, Effect onEquip, Spell onHit, double impactSpellProbability, int toHitMod, int reach, String... properties) {
        super(glyph, texturePath, name, worth, throwDamage, spells, onEquip, properties);
        this.weaponDamage = weaponDamage;
        this.toHitMod = toHitMod;
        this.reach = reach;
        this.impactSpellProbability = impactSpellProbability;
        this.onHit = onHit == null? null : onHit.copyOf(onHit);

        if(this.onHit != null) {
            this.onHit.setIgnoreRange(true);
        }
    }

    public Weapon(String name, Damage weaponDamage, Spell[] spells, Effect onEquip, Spell onHit, double impactSpellProbability, int toHitMod, int reach) {
        super(' ', "", name, 0, spells, onEquip);
        this.weaponDamage = new Damage(weaponDamage);
        this.toHitMod = toHitMod;
        this.reach = reach;
        this.impactSpellProbability = impactSpellProbability;
        this.onHit = onHit == null? null : onHit.copyOf(onHit);

        if(this.onHit != null) {
            this.onHit.setIgnoreRange(true);
        }
    }

    public Weapon(Weapon weapon) {
        super(weapon);
        this.weaponDamage = weapon.weaponDamage;
        this.toHitMod = weapon.toHitMod;
        this.reach = weapon.reach;
        this.onHit = weapon.onHit == null? null : weapon.onHit.copyOf(weapon.onHit);
        this.impactSpellProbability = weapon.impactSpellProbability;

        if(this.onHit != null) {
            this.onHit.setIgnoreRange(true);
        }
    }

    //<editor-fold desc="Getters and Setters">
    public Damage getWeaponDamage() {
        return weaponDamage;
    }

    public void setWeaponDamage(Damage weaponDamage) {
        this.weaponDamage = weaponDamage;
    }

    public int getToHitMod() {
        return toHitMod;
    }

    public void setToHitMod(int toHitMod) {
        this.toHitMod = toHitMod;
    }

    public int getReach() {
        return reach;
    }

    public void setReach(int reach) {
        this.reach = reach;
    }

    public Spell getOnHit() {
        return onHit;
    }

    public void setOnHit(Spell onHit) {
        this.onHit = onHit;
    }

    public double getImpactSpellProbability() {
        return impactSpellProbability;
    }

    public void setImpactSpellProbability(double impactSpellProbability) {
        this.impactSpellProbability = impactSpellProbability;
    }

    //</editor-fold>

    @Override
    public void assignCaster(Creature c) {
        super.assignCaster(c);
        if(onHit != null) onHit.setCaster(c);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Weapon)) return false;
        if (!super.equals(o)) return false;
        Weapon weapon = (Weapon) o;
        return toHitMod == weapon.toHitMod &&
                reach == weapon.reach &&
                Double.compare(weapon.impactSpellProbability, impactSpellProbability) == 0 &&
                Objects.equals(weaponDamage, weapon.weaponDamage) &&
                Objects.equals(onHit, weapon.onHit);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), weaponDamage, toHitMod, reach, onHit, impactSpellProbability);
    }

    @Override
    public String toString() {
        return String.format(Locale.getDefault(), "%s (%s)", name, weaponDamage.toString());
    }
}
