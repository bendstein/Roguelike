package creatureitem.item;

import creatureitem.effect.Damage;

public class Equipable extends Item {

    /**
     * Whether or not the item is equipped
     */
    protected boolean isEquipped;

    public Equipable(char glyph, String texturePath, String name, int worth) {
        super(glyph, texturePath, name, worth);
        isEquipped = false;
        addProperty("equip");
    }

    public Equipable(char glyph, String texturePath, String name, int worth, String... properties) {
        super(glyph, texturePath, name, worth, properties);
        isEquipped = false;
        addProperty("equip");
    }

    public Equipable(char glyph, String texturePath, String name, int worth, Damage throwDamage, String... properties) {
        super(glyph, texturePath, name, worth, throwDamage, properties);
        isEquipped = false;
        addProperty("equip");
    }

    public Equipable(Item item) {
        super(item);
        isEquipped = false;
        addProperty("equip");
    }

    //<editor-fold desc="Getters and Setters">
    public boolean isEquipped() {
        return isEquipped;
    }

    public void setEquipped(boolean equipped) {
        isEquipped = equipped;
    }
    //</editor-fold>
}
