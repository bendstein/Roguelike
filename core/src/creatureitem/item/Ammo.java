package creatureitem.item;

import creatureitem.effect.damage.Damage;
import creatureitem.spell.Spell;

import java.util.Locale;
import java.util.Objects;

public class Ammo extends Equipable {

    /**
     * Damage applied by the ammo when used
     */
    Damage ammoDamage;

    public Ammo(char glyph, String texturePath, String name, int worth, Damage ammoDamage, String... properties) {
        super(glyph, texturePath, name, worth, properties);
        this.ammoDamage = ammoDamage;
    }

    public Ammo(char glyph, String texturePath, String name, int worth, Damage throwDamage, Damage ammoDamage, String... properties) {
        super(glyph, texturePath, name, worth, throwDamage, properties);
        this.ammoDamage = ammoDamage;
    }

    public Ammo(char glyph, String texturePath, String name, int worth, Damage ammoDamage, Spell[] spells, String... properties) {
        super(glyph, texturePath, name, worth, spells, null, properties);
        this.ammoDamage = ammoDamage;
    }

    public Ammo(char glyph, String texturePath, String name, int worth, Damage throwDamage, Damage ammoDamage, Spell[] spells, String... properties) {
        super(glyph, texturePath, name, worth, throwDamage, spells, null, properties);
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Ammo)) return false;
        if (!super.equals(o)) return false;
        Ammo ammo = (Ammo) o;
        return Objects.equals(ammoDamage, ammo.ammoDamage);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), ammoDamage);
    }
}
