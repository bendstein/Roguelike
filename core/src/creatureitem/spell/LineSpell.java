package creatureitem.spell;

import creatureitem.Creature;
import creatureitem.effect.Effect;
import world.geometry.Line;
import world.geometry.Point;

/**
 * A spell that targets all points between two points
 */
public class LineSpell extends PointSpell {

    public LineSpell() {

    }

    public LineSpell(String name, int cost, boolean requiresCreatureTarget, boolean ignoreObstacle, int range, Effect ... effects) {
        super(name, cost, requiresCreatureTarget, ignoreObstacle, range, effects);
    }

    public LineSpell(LineSpell spell) {
        super(spell);
    }

    @Override
    public void cast(int x, int y) {

    }

    public void cast(int x, int y, int x1, int y1) {

        if(caster.getMana() - cost < 0) {
            caster.doAction("invoke %s, but it does nothing.", name);
            return;
        }

        caster.doAction("cast %s!", name);

        int c = 0;

        Line l = new Line(x, x1, y, y1);

        for(Point p : l) {
            if(p.equals(caster.getLocation())) continue;

            else if(!ignoreObstacle && !caster.getLevel().isPassable(p.getX(), p.getY()))
                break;

            if(requiresCreatureTarget) {
                Creature target = caster.getLevel().getCreatureAt(p.getX(), p.getY());
                if(target != null) {
                    c++;
                    for(Effect effect : effects)
                        target.applyEffect(effect.makeCopy(effect));
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

        if(c == 0 && requiresCreatureTarget) caster.notify("...but nothing happens!");
        caster.modifyMana(-cost);

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

}
