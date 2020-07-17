package creatureitem.spell;

import creatureitem.Creature;
import creatureitem.effect.Effect;
import utility.Utility;
import world.geometry.Point;

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

    public PointSpell(Effect effect, String name, int cost, int range) {
        super(effect, name, cost);
        this.range = range;

    }

    public PointSpell(PointSpell spell) {
        super(spell);
        this.range = spell.range;
    }

    public void cast(int x, int y) {
        Creature target = caster.getLevel().getCreatureAt(x, y);

        if(Utility.getDistance(caster.getLocation(), new Point(x, y)) > range + 1) {
            caster.doAction("attempt to invoke %s, but the target is too far away.", name);
            return;
        }

        if(target == null || caster.getMana() - cost < 0)
            caster.doAction("invoke %s, but it does nothing.", name);
        else {
            target.applyEffect(effect.makeCopy(effect));
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
        copy.effect = s.effect.makeCopy(s.effect);
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
