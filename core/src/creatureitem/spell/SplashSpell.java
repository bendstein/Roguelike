package creatureitem.spell;

import com.badlogic.gdx.Gdx;
import creatureitem.Creature;
import creatureitem.effect.Effect;
import utility.Utility;
import world.geometry.Line;
import world.geometry.Point;

import java.util.ArrayDeque;
import java.util.EmptyStackException;
import java.util.HashSet;

/**
 * AOE Spells that flood out from the origin instead of stopping when obstructed
 */
public class SplashSpell extends AOESpell {

    public SplashSpell() {
        super();
    }

    public SplashSpell(String name, boolean requiresCreatureTarget, boolean ignoreObstacle, boolean ignoreCaster, int range, int radius, int cast_energy, boolean notify, Effect ... effects) {
        super(name, requiresCreatureTarget, ignoreObstacle, ignoreCaster, range, radius, cast_energy, notify, effects);
    }

    public SplashSpell(AOESpell spell) {
        super(spell);
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

        HashSet<Point> visited = new HashSet<>();
        ArrayDeque<Point> hit = new ArrayDeque<>();
        HashSet<Point> obstructed = new HashSet<>();
        int c = 0;

        /*
         * First, go through all points that would normally be in the area. Add them to visited.
         * If the spell can hit a tile, add it to the queue. Otherwise, add it to obstructed.
         */
        for(int i = -radius; i <= radius; i++) {
            for(int j = -radius; j <= radius; j++) {
                if(x + i < 0 || x + i >= Gdx.graphics.getWidth() || y + j < 0 || y + j >= Gdx.graphics.getHeight()) continue;
                if(Math.abs(i) + Math.abs(j) <= radius/*Math.pow(i, 2) + Math.pow(j, 2) <= Math.pow(radius, 2)*/) {
                    boolean obstacle = false;
                    for(Point p : new Line(x, x + i, y, y + j)) {
                        visited.add(p);

                        if(!ignoreObstacle) {
                            if(!caster.getLevel().isPassable(p.getX(), p.getY()) || obstacle) {
                                obstructed.add(p);
                                obstacle = true;
                            }
                            else if(!hit.contains(p))
                                hit.add(p);
                        }
                        else
                            hit.add(p);


                    }
                }
            }
        }

        /*
         * Count is equal to the number of tiles the spell would normally hit.
         */
        int count = visited.size();

        try {
            /*
             * While there are still tiles to check, and the spell hasn't touched all that the spell can reach, keep checking.
             */
            while(!hit.isEmpty() && count > 0) {
                Point p = hit.pop();

                if(requiresCreatureTarget) {
                    Creature target = caster.getLevel().getCreatureAt(p.getX(), p.getY());
                    if (target != null && (!ignoreCaster || !target.equals(caster))) {
                        c++;
                        for(Effect effect : effects)
                            target.applyEffect(effect.makeCopy(effect));
                    }
                }
                else {
                    for(Effect effect : effects)
                        effect.affect(p.getX(), p.getY(), caster.getLevel());
                }


                for(int i = -1; i <= 1; i++) {
                    for(int j = -1; j <= 1; j++) {
                        Point p1 = new Point(p.getX() + i, p.getY() + j);
                        if(p1.getX() + i < 0 || p1.getX() + i >= Gdx.graphics.getWidth() || p1.getY() + j < 0 || p1.getY() + j >= Gdx.graphics.getHeight()) continue;
                        if(visited.contains(p1) || (!ignoreObstacle && !caster.getLevel().isPassable(p1.getX(), p1.getY()))) continue;

                        visited.add(p1);
                        hit.add(p1);
                    }
                }

                count--;
            }
        } catch (EmptyStackException ignored) {

        } finally {
            if(c == 0 && requiresCreatureTarget)
                if(notify) caster.notify("...but nothing happens!");
            //caster.modifyMana(-cost);
        }

    }

    /**
     * @return A deep copy of the given spell
     */
    @Override
    public SplashSpell copy() {
        return new SplashSpell(super.copy());
    }
}
