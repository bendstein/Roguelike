package creatureitem.item;

import creatureitem.Creature;
import creatureitem.effect.damage.Damage;
import creatureitem.effect.Effect;
import creatureitem.spell.PointSpell;
import creatureitem.spell.Spell;

import java.util.Objects;

public class Potion extends Item {

    protected Spell onDrink;

    public Potion(char glyph, String texturePath, String name, int worth, Spell[] spells, Spell onDrink) {
        super(glyph, texturePath, name, worth, spells, "quaff", "stack", "shatter");
        this.onDrink = onDrink;

        if(this.onDrink != null) {
            this.onDrink.setIgnoreRange(true);
        }

        if(this.onThrow == null) {
            this.onThrow = this.onDrink;
        }
    }

    public Potion(char glyph, String texturePath, String name, int worth, Spell[] spells, Spell onDrink, String... properties) {
        super(glyph, texturePath, name, worth, spells, properties);
        this.properties.add("quaff");
        this.properties.add("stack");
        this.properties.add("shatter");
        this.onDrink = onDrink;

        if(this.onDrink != null) {
            this.onDrink.setIgnoreRange(true);
        }


        if(this.onThrow == null) {
            this.onThrow = this.onDrink;
        }
    }

    public Potion(char glyph, String texturePath, String name, int worth, Damage throwDamage, Spell[] spells, Spell onDrink, String... properties) {
        super(glyph, texturePath, name, worth, throwDamage, spells, properties);
        this.properties.add("quaff");
        this.properties.add("stack");
        this.properties.add("shatter");
        this.onDrink = onDrink;

        if(this.onDrink != null) {
            this.onDrink.setIgnoreRange(true);
        }


        if(this.onThrow == null) {
            this.onThrow = this.onDrink;
        }
    }

    public Potion(char glyph, String texturePath, String name, int worth, Effect onDrink) {
        super(glyph, texturePath, name, worth, new Spell[]{null, new PointSpell(onDrink)}, "quaff", "stack", "shatter");
        this.onDrink = onDrink == null? null : new PointSpell(onDrink);

        if(this.onDrink != null) {
            this.onDrink.setIgnoreRange(true);
        }

        if(this.onThrow == null) {
            this.onThrow = this.onDrink;
        }
    }

    public Potion(char glyph, String texturePath, String name, int worth, Effect onDrink, String... properties) {
        super(glyph, texturePath, name, worth, new Spell[]{null, new PointSpell(onDrink)}, properties);
        this.properties.add("quaff");
        this.properties.add("stack");
        this.properties.add("shatter");
        this.onDrink = onDrink == null? null : new PointSpell(onDrink);

        if(this.onDrink != null) {
            this.onDrink.setIgnoreRange(true);
        }

        if(this.onThrow == null) {
            this.onThrow = this.onDrink;
        }
    }

    public Potion(char glyph, String texturePath, String name, int worth, Damage throwDamage, Effect onDrink, String... properties) {
        super(glyph, texturePath, name, worth, throwDamage, new Spell[]{null, new PointSpell(onDrink)}, properties);
        this.properties.add("quaff");
        this.properties.add("stack");
        this.properties.add("shatter");
        this.onDrink = onDrink == null? null : new PointSpell(onDrink);

        if(this.onDrink != null) {
            this.onDrink.setIgnoreRange(true);
        }

        if(this.onThrow == null) {
            this.onThrow = this.onDrink;
        }
    }

    public Potion(Potion item) {
        super(item);
        this.onDrink = item.onDrink == null? null : item.onDrink.copyOf(item.onDrink);
    }

    public Spell getOnDrink() {
        return onDrink;
    }

    public void setOnDrink(Spell onDrink) {
        this.onDrink = onDrink;
    }

    @Override
    public void assignCaster(Creature c) {
        super.assignCaster(c);
        if(onDrink != null) onDrink.setCaster(c);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Potion)) return false;
        if (!super.equals(o)) return false;
        Potion potion = (Potion) o;
        return Objects.equals(onDrink, potion.onDrink);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), onDrink);
    }
}
