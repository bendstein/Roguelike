package creatureitem.spell;

import creatureitem.Creature;
import creatureitem.effect.*;

/**
 * Parent class for all spells
 */
public abstract class Spell {

    /**
     * The effect associated with the spell
     */
    protected Effect effect;

    /**
     * The name of the spell
     */
    protected String name;

    /**
     * The mana cost of the spell
     */
    protected int cost;

    /**
     * A reference to the creature casting the spell
     */
    protected Creature caster;

    public Spell() {}

    public Spell(Effect effect, String name, int cost) {
        this.effect = effect.makeCopy(effect);
        this.name = name;
        this.cost = cost;
    }

    public Spell(Spell spell) {
        this.effect = spell.effect.makeCopy(spell.effect);
        this.name = spell.name;
        this.cost = spell.cost;
    }

    /**
     * @param s The spell to copy
     * @return A deep copy of the given spell
     */
    public abstract Spell copyOf(Spell s);

    //<editor-fold desc="Getters and Setters">
    public Effect getEffect() {
        return effect;
    }

    public void setEffect(Effect effect) {
        this.effect = effect;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public Creature getCaster() {
        return caster;
    }

    public void setCaster(Creature caster) {
        this.caster = caster;
        effect.setCaster(caster);
    }

    //</editor-fold>
}
