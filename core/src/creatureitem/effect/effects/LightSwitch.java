package creatureitem.effect.effects;

import creatureitem.Creature;
import creatureitem.effect.Effect;
import world.Level;
import world.thing.Light;
import world.thing.Thing;

public class LightSwitch extends Effect {
    /**
     * What the effect does when it is active
     */
    @Override
    public void affect() {

    }

    /**
     * What the effect does when it is active
     *
     * @param x X location
     * @param y Y location
     * @param l The level to effect
     */
    @Override
    public void affect(int x, int y, Level l) {
    }

    /**
     * What the effect does when it is active
     *
     * @param c The creature to apply the effect to
     */
    @Override
    public void affect(Creature c) {
        if(c == null) return;
        Level l = c.getLevel();
        if(l == null) return;

        for(Thing t : l.getThings()) {
            if(t instanceof Light) {
                t.interact();
            }
        }

        l.updateStaticLit();
    }

    /**
     * What the effect does when it is over
     */
    @Override
    public void done() {

    }

    /**
     * What the effect does when it is over
     *
     * @param c The creature which the effect was applied to
     */
    @Override
    public void done(Creature c) {

    }

    @Override
    public Effect makeCopy(Effect effect) {
        return new LightSwitch();
    }
}
