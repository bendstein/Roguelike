package creatureitem.item;

import creatureitem.effect.Damage;

import java.util.Objects;

public class Armor extends Equipable {

    /**
     * Damage reduction of the armor
     */
    private int armor;

    public Armor(char glyph, String texturePath, int armor, String name) {
        super(glyph, texturePath, name);
        this.armor = armor;
    }

    public Armor(char glyph, String texturePath, String name, int armor, String... properties) {
        super(glyph, texturePath, name, properties);
        this.armor = armor;
    }

    public Armor(char glyph, String texturePath, String name, Damage throwDamage, int armor, String... properties) {
        super(glyph, texturePath, name, throwDamage, properties);
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
        super(' ', "", " ");
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
