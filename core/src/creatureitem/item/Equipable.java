package creatureitem.item;

import creatureitem.Creature;
import creatureitem.effect.Effect;
import creatureitem.effect.damage.Damage;
import creatureitem.spell.Spell;

import java.util.Objects;

public class Equipable extends Item {

    /**
     * Whether or not the item is equipped
     */
    protected boolean isEquipped;

    protected Effect onEquip;

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

    public Equipable(char glyph, String texturePath, String name, Spell[] spells, Effect onEquip, int worth) {
        super(glyph, texturePath, name, worth, spells);
        isEquipped = false;
        this.onEquip = onEquip == null? null : onEquip.makeCopy(onEquip);
        addProperty("equip");
    }

    public Equipable(char glyph, String texturePath, String name, int worth, Spell[] spells, Effect onEquip, String... properties) {
        super(glyph, texturePath, name, worth, spells, properties);
        isEquipped = false;
        this.onEquip = onEquip == null? null : onEquip.makeCopy(onEquip);
        addProperty("equip");
    }

    public Equipable(char glyph, String texturePath, String name, int worth, Damage throwDamage, Spell[] spells, Effect onEquip, String... properties) {
        super(glyph, texturePath, name, worth, throwDamage, spells, properties);
        isEquipped = false;
        this.onEquip = onEquip == null? null : onEquip.makeCopy(onEquip);
        addProperty("equip");
    }

    public Equipable(Equipable item) {
        super(item);
        isEquipped = false;
        this.onEquip = item.onEquip == null? null : item.onEquip;
        addProperty("equip");
    }

    //<editor-fold desc="Getters and Setters">
    public boolean isEquipped() {
        return isEquipped;
    }

    public void setEquipped(boolean equipped) {
        isEquipped = equipped;
    }

    public Effect getOnEquip() {
        return onEquip;
    }

    public void setOnEquip(Effect onEquip) {
        this.onEquip = onEquip;
    }

    //</editor-fold>

    @Override
    public void assignCaster(Creature c) {
        super.assignCaster(c);
        if(onEquip != null) onEquip.setCaster(c);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Equipable)) return false;
        if (!super.equals(o)) return false;
        Equipable equipable = (Equipable) o;
        return Objects.equals(onEquip, equipable.onEquip);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), onEquip);
    }
}
