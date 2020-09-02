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
     * A reference to the creature casting the spell
     */
    protected Creature caster;

    /**
     * True if the spell requires a creature target
     */
    protected boolean requiresCreatureTarget;

    /**
     * Whether or not the spell affects obstructed tiles
     */
    protected boolean ignoreObstacle;

    protected boolean ignoreCaster;

    /**
     * Amount of energy casting the spell takes
     */
    protected int cast_energy;

    protected boolean notify;

    protected boolean ignoreRange;

    public Spell() {
        this.name = null;
        this.effects = new ArrayList<>();
        this.requiresCreatureTarget = false;
        this.ignoreObstacle = false;
        this.cast_energy = 0;
        this.ignoreCaster = false;
        this.caster = null;
        this.notify = false;
        ignoreRange = false;
    }

    public Spell(String name, boolean requiresCreatureTarget, boolean ignoreObstacle, boolean ignoreCaster, int cast_energy, boolean notify, Effect ... effects) {
        this.name = name;
        this.effects = new ArrayList<>();
        this.requiresCreatureTarget = requiresCreatureTarget;
        this.ignoreObstacle = ignoreObstacle;
        this.cast_energy = cast_energy;
        this.ignoreCaster = ignoreCaster;
        this.caster = null;
        this.notify = notify;
        ignoreRange = false;

        for(Effect e : effects)
            this.effects.add(e.makeCopy(e));
    }

    public Spell(Spell spell) {
        this.name = spell.name;
        this.effects = new ArrayList<>();
        this.requiresCreatureTarget = spell.requiresCreatureTarget;
        this.ignoreObstacle = spell.ignoreObstacle;
        this.cast_energy = spell.cast_energy;
        this.ignoreCaster = spell.ignoreObstacle;
        this.notify = spell.notify;
        ignoreRange = spell.ignoreRange;

        for(Effect e : spell.effects)
            this.effects.add(e.makeCopy(e));
    }

    /**
     * @return A deep copy of the given spell
     */
    public abstract Spell copy();

    public void cast() {

    }

    //<editor-fold desc="Getters and Setters">

    public boolean isIgnoreCaster() {
        return ignoreCaster;
    }

    public void setIgnoreCaster(boolean ignoreCaster) {
        this.ignoreCaster = ignoreCaster;
    }

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

    public Creature getCaster() {
        return caster;
    }

    public void setCaster(Creature caster) {
        this.caster = caster;
        for(Effect effect : effects)
            effect.setCaster(caster);
    }

    public int getCast_energy() {
        return cast_energy;
    }

    public void setCast_energy(int cast_energy) {
        this.cast_energy = cast_energy;
    }

    public boolean isNotify() {
        return notify;
    }

    public void setNotify(boolean notify) {
        this.notify = notify;
    }

    public boolean isIgnoreRange() {
        return ignoreRange;
    }

    public void setIgnoreRange(boolean ignoreRange) {
        this.ignoreRange = ignoreRange;
    }

    //</editor-fold>
}
