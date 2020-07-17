package creatureitem.spell;

import com.badlogic.gdx.Gdx;
import creatureitem.Creature;
import creatureitem.effect.Effect;
import game.Main;
import utility.Utility;
import world.geometry.Line;
import world.geometry.Point;

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

    public AOESpell(Effect effect, String name, int cost, int range, int radius) {
        super(effect, name, cost, range);
        this.radius = radius;
    }

    public AOESpell(AOESpell spell) {
        super(spell);
        this.radius = spell.radius;
    }

    @Override
    public void cast(int x, int y) {
        if(Utility.getDistance(caster.getLocation(), new Point(x, y)) > range + 1) {
            caster.doAction("attempt to invoke %s, but the target is too far away.", name);
            return;
        }

        if(caster.getMana() - cost < 0) {
            caster.doAction("invoke %s, but it does nothing.", name);
            return;
        }

        for(int i = -radius; i <= radius; i++) {
            for(int j = -radius; j <= radius; j++) {
                if(x + i < 0 || x + i >= Gdx.graphics.getWidth() || y + j < 0 || y + j >= Gdx.graphics.getHeight()) continue;
                if(Math.pow(i, 2) + Math.pow(j, 2) <= Math.pow(radius, 2)) {
                    boolean obstacle = false;
                    for(Point p : new Line(x, x + i, y, y + j)) {

                        if(!caster.getLevel().isPassable(p.getX(), p.getY()) || obstacle)
                            obstacle = true;
                        else {
                            Creature target = caster.getLevel().getCreatureAt(x + i, y + j);
                            if(target == null) continue;
                            target.applyEffect(effect.makeCopy(effect));
                        }
                    }
                }
            }
        }

        caster.modifyMana(-cost);
    }

    /**
     * @param s The spell to copy
     * @return A deep copy of the given spell
     */
    @Override
    public AOESpell copyOf(Spell s) {
        AOESpell copy = new AOESpell();
        copy.effect = s.effect.makeCopy(s.effect);
        copy.name = s.name;
        copy.cost = s.cost;
        copy.range = s instanceof PointSpell? ((PointSpell) s).range : 1;
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
