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

    public PointSpell() {

    }

    public PointSpell(String name, int cost, boolean requiresCreatureTarget, boolean ignoreObstacle, int range, Effect ... effects) {
        super(name, cost, requiresCreatureTarget, ignoreObstacle, effects);
        this.range = range;
    }

    public PointSpell(PointSpell spell) {
        super(spell);
        this.range = spell.range;
    }

    public void cast(int x, int y) {
        if(Utility.getDistance(caster.getLocation(), new Point(x, y)) > range + 1) {
            caster.doAction("attempt to invoke %s, but the target is too far away.", name);
            return;
        }

        if(caster.getMana() - cost < 0) {
            caster.doAction("invoke %s, but it does nothing.", name);
            return;
        }

        if(!ignoreObstacle && !caster.getLevel().getTileAt(x, y).isPassable()) {
            caster.doAction("invoke %s, but it does nothing.", name);
            return;
        }
        caster.doAction("cast %s!", name);

        if(requiresCreatureTarget) {
            Creature target = caster.getLevel().getCreatureAt(x, y);

            if(target == null)
                caster.notify("...but nothing happens!");
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

        caster.modifyMana(-cost);
    }

    public void cast(Point p) {
        cast(p.getX(), p.getY());
    }

    public void cast(Creature c) {
        cast(c.getLocation());
    }

    /**
     * @param s The spell to copy
     * @return A deep copy of the given spell
     */
    @Override
    public PointSpell copyOf(Spell s) {
        PointSpell copy = new PointSpell();
        copy.effects = new ArrayList<>();
        for(Effect e : s.effects)
            copy.effects.add(e.makeCopy(e));
        copy.requiresCreatureTarget = s.requiresCreatureTarget;
        copy.ignoreObstacle = s.ignoreObstacle;
        copy.name = s.name;
        copy.cost = s.cost;

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
