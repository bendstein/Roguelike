package creatureitem.item;

import creatureitem.effect.Damage;

public class RangedWeapon extends Weapon {

    /**
     * The range of the weapon
     */
    protected int range;

    /**
     * The type of ammo the weapon accepts
     */
    protected String ammoType;

    public RangedWeapon(char glyph, String texturePath, String name, int worth, Damage weaponDamage, int toHitMod, int range, String ammoType) {
        super(glyph, texturePath, name, worth, weaponDamage, toHitMod);
        this.range = range;
        this.ammoType = ammoType;
    }

    public RangedWeapon(char glyph, String texturePath, String name, int worth, Damage weaponDamage, int toHitMod, int range, String ammoType, String... properties) {
        super(glyph, texturePath, name, worth, weaponDamage, toHitMod, properties);
        this.range = range;
        this.ammoType = ammoType;
    }

    public RangedWeapon(char glyph, String texturePath, String name, int worth, Damage throwDamage, Damage weaponDamage, int toHitMod, int range, String ammoType, String... properties) {
        super(glyph, texturePath, name, worth, throwDamage, weaponDamage, toHitMod, properties);
        this.range = range;
        this.ammoType = ammoType;
    }

    public RangedWeapon(RangedWeapon weapon) {
        super(weapon);
        this.range = weapon.range;
        this.ammoType = weapon.ammoType;
    }

    //<editor-fold desc="Getters and Setters">
    public int getRange() {
        return range;
    }

    public void setRange(int range) {
        this.range = range;
    }

    public String getAmmoType() {
        return ammoType;
    }

    public void setAmmoType(String ammoType) {
        this.ammoType = ammoType;
    }

    //</editor-fold>
}
