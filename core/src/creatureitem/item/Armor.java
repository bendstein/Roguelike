package creatureitem.item;

import creatureitem.effect.Effect;
import creatureitem.effect.damage.Damage;
import creatureitem.spell.Spell;

import java.util.Objects;

public class Armor extends Equipable {

    /**
     * Damage reduction of the armor
     */
    private int armor;

    public Armor(char glyph, String texturePath, int armor, String name, int worth) {
        super(glyph, texturePath, name, worth);
        this.armor = armor;
    }

    public Armor(char glyph, String texturePath, String name, int worth, int armor, String... properties) {
        super(glyph, texturePath, name, worth, properties);
        this.armor = armor;
    }

    public Armor(char glyph, String texturePath, String name, int worth, Damage throwDamage, int armor, String... properties) {
        super(glyph, texturePath, name, worth, throwDamage, properties);
        this.armor = armor;
    }

    public Armor(char glyph, String texturePath, int armor, String name, Spell[] spells, Effect onEquip, int worth) {
        super(glyph, texturePath, name, spells, onEquip, worth);
        this.armor = armor;
    }

    public Armor(char glyph, String texturePath, String name, int worth, int armor, Spell[] spells, Effect onEquip, String... properties) {
        super(glyph, texturePath, name, worth, spells, onEquip, properties);
        this.armor = armor;
    }

    public Armor(char glyph, String texturePath, String name, int worth, Damage throwDamage, int armor, Spell[] spells, Effect onEquip, String... properties) {
        super(glyph, texturePath, name, worth, throwDamage, spells, onEquip, properties);
        this.armor = armor;
    }

    public Armor(Armor a) {
        super(a);
        this.armor = a.armor;
    }

    /**
     * Create armor with no properties other than armor rating. Intended for natural armor, but may be used elsewhere.
     * @param armor The armor rating
     */
    public Armor(int armor) {
        super(' ', "", " ", 0);
        this.armor = armor;
    }

    //<editor-fold desc="Getters and Setters">
    public int getArmor() {
        return armor;
    }

    public void setArmor(int armor) {
        this.armor = armor;
    }
    //</editor-fold>


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Armor armor1 = (Armor) o;
        return armor == armor1.armor;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), armor);
    }
}
