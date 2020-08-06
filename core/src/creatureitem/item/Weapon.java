package creatureitem.item;

import creatureitem.effect.Damage;

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

    public Weapon(char glyph, String texturePath, String name, int worth, Damage weaponDamage, int toHitMod) {
        super(glyph, texturePath, name, worth);
        this.weaponDamage = weaponDamage;
        this.toHitMod = toHitMod;
    }

    public Weapon(char glyph, String texturePath, String name, int worth, Damage weaponDamage, int toHitMod, String... properties) {
        super(glyph, texturePath, name, worth, properties);
        this.weaponDamage = weaponDamage;
        this.toHitMod = toHitMod;
    }

    public Weapon(char glyph, String texturePath, String name, int worth, Damage throwDamage, Damage weaponDamage, int toHitMod, String... properties) {
        super(glyph, texturePath, name, worth, throwDamage, properties);
        this.weaponDamage = weaponDamage;
        this.toHitMod = toHitMod;
    }

    public Weapon(Weapon weapon) {
        super(weapon);
        this.weaponDamage = weapon.weaponDamage;
        this.toHitMod = weapon.toHitMod;
    }

    public Weapon(String name, Damage weaponDamage, int toHitMod) {
        super(' ', "", name, 0);
        this.weaponDamage = new Damage(weaponDamage);
        this.toHitMod = toHitMod;
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
    //</editor-fold>

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Weapon weapon = (Weapon) o;
        return Objects.equals(weaponDamage, weapon.weaponDamage);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), weaponDamage);
    }

    @Override
    public String toString() {
        return String.format(Locale.getDefault(), "%s (%s)", name, weaponDamage.toString());
    }
}
