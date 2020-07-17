package creatureitem.item;

import creatureitem.effect.Damage;
import creatureitem.effect.Effect;

import java.util.Locale;
import java.util.Objects;

public class Potion extends Item {

    /**
     * The effect that this potion applies
     */
    Effect effect;

    public Potion(char glyph, String texturePath, String name, Effect effect) {
        super(glyph, texturePath, name, "quaff", "stack", "shatter");
        this.effect = effect.makeCopy(effect);
    }

    public Potion(char glyph, String texturePath, String name, Effect effect, String... properties) {
        super(glyph, texturePath, name, properties);
        this.properties.add("quaff");
        this.properties.add("stack");
        this.properties.add("shatter");
        this.effect = effect.makeCopy(effect);
    }

    public Potion(char glyph, String texturePath, String name, Damage throwDamage, Effect effect, String... properties) {
        super(glyph, texturePath, name, throwDamage, properties);
        this.properties.add("quaff");
        this.properties.add("stack");
        this.properties.add("shatter");
        this.effect = effect.makeCopy(effect);
    }

    public Potion(Potion item) {
        super(item);
        this.effect = item.effect.makeCopy(item.effect);
    }

    //<editor-fold desc="Getters and Setters">
    public Effect getEffect() {
        return effect;
    }

    public void setEffect(Effect effect) {
        this.effect = effect;
    }
    //</editor-fold>


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Potion potion = (Potion) o;
        return Objects.equals(effect, potion.effect);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), effect);
    }
}
