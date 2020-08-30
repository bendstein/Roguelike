package creatureitem.spell;

import com.badlogic.gdx.Gdx;
import creatureitem.Creature;
import creatureitem.effect.Effect;
import game.Main;
import utility.Utility;
import world.geometry.Line;
import world.geometry.Point;

import java.util.ArrayList;

/**
 * A spell that targets all points within a certain distance of a specific point.
 */
public class AOESpell extends PointSpell {

    /**
     * The radius of the spell area
     */
    protected int radius;

    public AOESpell() {
        super();
    }

    public AOESpell(String name, boolean requiresCreatureTarget, boolean ignoreObstacle, boolean ignoreCaster, int range, int radius, int cast_energy, boolean notify, Effect ... effects) {
        super(name, requiresCreatureTarget, ignoreObstacle, ignoreCaster, range, cast_energy, notify, effects);
        this.radius = radius;
    }

    public AOESpell(AOESpell spell) {
        super(spell);
        this.radius = spell.radius;
    }

    @Override
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
        if(notify) caster.doAction("cast %s!", name);
        int c = 0;

        for(int i = -radius; i <= radius; i++) {
            for(int j = -radius; j <= radius; j++) {
                if(x + i < 0 || x + i >= Gdx.graphics.getWidth() || y + j < 0 || y + j >= Gdx.graphics.getHeight()) continue;
                if(Math.pow(i, 2) + Math.pow(j, 2) <= Math.pow(radius, 2)) {
                    boolean obstacle = false;
                    for (Point p : new Line(x, x + i, y, y + j)) {

                        if (!ignoreObstacle && (!caster.getLevel().isPassable(p.getX(), p.getY()) || obstacle))
                            obstacle = true;

                        if (requiresCreatureTarget) {
                            Creature target = caster.getLevel().getCreatureAt(x + i, y + j);
                            if (target == null || (ignoreCaster && target.equals(caster))) continue;
                            c++;
                            for (Effect effect : effects)
                                target.applyEffect(effect.makeCopy(effect));
                        } else {
                            for (Effect effect : effects)
                                effect.affect(p.getX(), p.getY(), caster.getLevel());
                        }

                    }
                }

            }
        }

        if(c == 0 && requiresCreatureTarget) if(notify) caster.notify("...but nothing happens!");
        //caster.modifyMana(-cost);
    }

    /**
     * @param s The spell to copy
     * @return A deep copy of the given spell
     */
    @Override
    public AOESpell copyOf(Spell s) {
        AOESpell copy = (AOESpell)super.copyOf(s);
        copy.effects = new ArrayList<>();

        copy.radius = s instanceof AOESpell? ((AOESpell) s).radius : 1;

        return copy;
    }

    //<editor-fold desc="Getters and Setters">
    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }
    //</editor-fold>
}
