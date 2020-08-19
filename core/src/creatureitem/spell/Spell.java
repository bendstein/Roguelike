package creatureitem.spell;

import creatureitem.Creature;
import creatureitem.effect.*;

import java.util.ArrayList;

/**
 * Parent class for all spells
 */
public abstract class Spell {

    /**
     * The effect associated with the spell
     */
    protected ArrayList<Effect> effects;

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

    /**
     * True if the spell requires a creature target
     */
    protected boolean requiresCreatureTarget;

    protected boolean ignoreObstacle;

    public Spell() {}

    public Spell(String name, int cost, boolean requiresCreatureTarget, boolean ignoreObstacle, Effect ... effects) {
        this.name = name;
        this.cost = cost;
        this.effects = new ArrayList<>();
        this.requiresCreatureTarget = requiresCreatureTarget;
        this.ignoreObstacle = ignoreObstacle;

        for(Effect e : effects)
            this.effects.add(e.makeCopy(e));
    }


    public Spell(Spell spell) {
        this.name = spell.name;
        this.cost = spell.cost;
        this.effects = new ArrayList<>();
        this.requiresCreatureTarget = spell.requiresCreatureTarget;
        this.ignoreObstacle = spell.ignoreObstacle;

        for(Effect e : spell.effects)
            this.effects.add(e.makeCopy(e));
    }

    /**
     * @param s The spell to copy
     * @return A deep copy of the given spell
     */
    public abstract Spell copyOf(Spell s);

    //<editor-fold desc="Getters and Setters">
    public boolean isIgnoreObstacle() {
        return ignoreObstacle;
    }

    public void setIgnoreObstacle(boolean ignoreObstacle) {
        this.ignoreObstacle = ignoreObstacle;
    }

    public boolean isRequiresCreatureTarget() {
        return requiresCreatureTarget;
    }

    public void setRequiresCreatureTarget(boolean requiresCreatureTarget) {
        this.requiresCreatureTarget = requiresCreatureTarget;
    }

    public ArrayList<Effect> getEffects() {
        return effects;
    }

    public void setEffects(ArrayList<Effect> effects) {
        this.effects = effects;
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
        for(Effect effect : effects)
            effect.setCaster(caster);
    }

    //</editor-fold>
}
