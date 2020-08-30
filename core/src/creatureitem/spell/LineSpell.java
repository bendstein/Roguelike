package creatureitem.spell;

import creatureitem.Creature;
import creatureitem.effect.Effect;
import world.geometry.Line;
import world.geometry.Point;

import java.util.ArrayList;

/**
 * A spell that targets all points between two points
 */
public class LineSpell extends PointSpell {

    private boolean effectOne;

    public LineSpell() {

    }

    public LineSpell(String name, boolean requiresCreatureTarget, boolean effectOne, boolean ignoreObstacle, boolean ignoreCaster, int range, int cast_energy, boolean notify, Effect ... effects) {
        super(name, requiresCreatureTarget, ignoreObstacle, ignoreCaster, range, cast_energy, notify, effects);
        this.effectOne = effectOne;
    }

    public LineSpell(LineSpell spell) {
        super(spell);
        this.effectOne = spell.effectOne;
    }

    @Override
    public void cast(int x, int y) {

    }

    public void cast(int x, int y, int x1, int y1) {

        /*
        if(caster.getMana() - cost < 0) {
            caster.doAction("invoke %s, but it does nothing.", name);
            return;
        }

         */

        if(notify) caster.doAction("cast %s!", name);

        int c = 0;

        Line l = new Line(x, x1, y, y1);

        for(Point p : l) {
            if(p.equals(caster.getLocation())) continue;

            else if(!ignoreObstacle && !caster.getLevel().isPassable(p.getX(), p.getY()))
                break;

            if(requiresCreatureTarget) {
                Creature target = caster.getLevel().getCreatureAt(p.getX(), p.getY());
                if(target != null && (!ignoreCaster || !target.equals(caster))) {
                    c++;
                    for(Effect effect : effects)
                        target.applyEffect(effect.makeCopy(effect));

                    if(effectOne) {
                        return;
                    }
                }
                else if(!ignoreObstacle && !caster.getLevel().isPassable(p.getX(), p.getY()))
                    break;
            }
            else {
                for(Effect effect : effects) {
                    effect.affect(p.getX(), p.getY(), caster.getLevel());
                }
            }


        }

        if(c == 0 && requiresCreatureTarget) if(notify) caster.notify("...but nothing happens!");
        //caster.modifyMana(-cost);

    }

    public void cast(Point p, Point p1) {
        cast(p.getX(), p.getY(), p1.getX(), p1.getY());
    }

    public void cast(Creature c, Creature c1) {
        cast(c.getLocation(), c1.getLocation());
    }

    public void cast(Point p, Creature c) {
        cast(p, c.getLocation());
    }

    /**
     * @param s The spell to copy
     * @return A deep copy of the given spell
     */
    @Override
    public LineSpell copyOf(Spell s) {
        LineSpell copy = (LineSpell) super.copyOf(s);

        copy.effectOne = !(s instanceof LineSpell) || ((LineSpell) s).effectOne;

        return copy;
    }

    public boolean isEffectOne() {
        return effectOne;
    }

    public void setEffectOne(boolean effectOne) {
        this.effectOne = effectOne;
    }
}
