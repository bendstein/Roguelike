package creatureitem.item;

import creatureitem.effect.damage.Damage;

import java.util.Locale;

public class Ammo extends Equipable {

    /**
     * Damage applied by the ammo when used
     */
    Damage ammoDamage;

    public Ammo(char glyph, String texturePath, String name, int worth, Damage ammoDamage) {
        super(glyph, texturePath, name, worth);
        this.ammoDamage = ammoDamage;
    }

    public Ammo(char glyph, String texturePath, String name, int worth, Damage ammoDamage, String... properties) {
        super(glyph, texturePath, name, worth, properties);
        this.ammoDamage = ammoDamage;
    }

    public Ammo(char glyph, String texturePath, String name, int worth, Damage throwDamage, Damage ammoDamage, String... properties) {
        super(glyph, texturePath, name, worth, throwDamage, properties);
        this.ammoDamage = ammoDamage;
    }

    public Ammo(Ammo ammo) {
        super(ammo);
        ammoDamage = new Damage(ammo.ammoDamage);
    }

    public Damage getAmmoDamage() {
        return ammoDamage;
    }

    public void setAmmoDamage(Damage ammoDamage) {
        this.ammoDamage = ammoDamage;
    }

    @Override
    public String toString() {
        if(hasProperty("stack") && count > 1) return String.format(Locale.getDefault(), "%s (%s) (%d)", name, ammoDamage.toString(), count);
        else return String.format(Locale.getDefault(), "%s (%s)", name, ammoDamage.toString());
    }
}
