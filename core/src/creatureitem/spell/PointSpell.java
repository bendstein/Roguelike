package creatureitem.spell;

import creatureitem.Creature;
import creatureitem.effect.Effect;
import utility.Utility;
import world.geometry.Point;

import java.util.ArrayList;

/**
 * A spell which targets a specific point.
 */
public class PointSpell extends Spell {

    /**
     * Maximum distance between caster and spell point
     */
    protected int range;

    protected boolean ignoreRange;

    public PointSpell() {
        this.range = 0;
    }

    public PointSpell(String name, boolean requiresCreatureTarget, boolean ignoreObstacle, boolean ignoreCaster, int range, int cast_energy, boolean notify, Effect ... effects) {
        super(name, requiresCreatureTarget, ignoreObstacle, ignoreCaster, cast_energy, notify, effects);
        this.range = range;
    }

    public PointSpell(Effect effect) {
        super("", true, false, false, 0, false, effect);
        this.range = 1;
    }

    public PointSpell(PointSpell spell) {
        super(spell);
        this.range = spell.range;
    }

    public void cast(int x, int y) {
        if(!ignoreRange && Utility.getDistance(caster.getLocation(), new Point(x, y)) > range + 1) {
            if(notify) caster.doAction("attempt to invoke %s, but the target is too far away.", name);
            return;
        }

        /*
        if(caster.getMana() - cost < 0) {
            caster.doAction("invoke %s, but it does nothing.", name);
            return;
        }

         */

        if(!ignoreObstacle && !caster.getLevel().getTileAt(x, y).isPassable()) {
            if(notify) caster.doAction("invoke %s, but it does nothing.", name);
            return;
        }
        if(notify) caster.doAction("cast %s!", name);

        if(requiresCreatureTarget) {
            Creature target = caster.getLevel().getCreatureAt(x, y);

            if(target == null || (ignoreCaster && target.equals(caster))) {
                if (notify) caster.notify("...but nothing happens!");
            }
            else {
                for(Effect effect : effects)
                    target.applyEffect(effect.makeCopy(effect));
            }
        }
        else {
            for(Effect effect : effects) {
                effect.affect(x, y, caster.getLevel());
            }
        }

        //caster.modifyMana(-cost);
    }

    public void cast(Point p) {
        cast(p.getX(), p.getY());
    }

    public void cast(Creature c) {
        cast(c.getLocation());
    }

    /**
     * @return A deep copy of the given spell
     */
    @Override
    public PointSpell copy() {
        PointSpell copy = new PointSpell();
        copy.effects = new ArrayList<>();
        for(Effect e : effects)
            copy.effects.add(e.makeCopy(e));
        copy.requiresCreatureTarget = requiresCreatureTarget;
        copy.ignoreObstacle = ignoreObstacle;
        copy.ignoreCaster = ignoreCaster;
        copy.name = name;
        copy.notify = notify;
        copy.range = range;
        copy.ignoreRange = ignoreRange;

        return copy;
    }

    //<editor-fold desc="Getters and Setters">
    public int getRange() {
        return range;
    }

    public void setRange(int range) {
        this.range = range;
    }
    //</editor-fold>
}
