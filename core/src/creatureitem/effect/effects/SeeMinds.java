package creatureitem.effect.effects;

import creatureitem.Creature;
import creatureitem.effect.Effect;
import world.Level;

public class SeeMinds extends Effect {

    boolean affected;

    public SeeMinds(int duration) {
        super();
        affected = false;
        this.duration = this.remainingDuration = duration;
    }

    public SeeMinds(boolean infinite) {
        super();
        affected = false;
        this.duration = this.remainingDuration = Integer.MAX_VALUE;
        this.infinite = infinite;
    }

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
        if(caster instanceof Creature && caster.equals(c)) {
            return;
        }

        if(affected)
            return;

        if(caster instanceof Creature) {
            if(!((Creature) caster).getExtra_sight().contains(c))
                ((Creature) caster).add_extra_sight(c);
            ((Creature) caster).setRequestVisionUpdate(true);
            affected = true;
        }

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
        ((Creature)caster).remove_extra_sight(c);
        affected = false;
    }

    @Override
    public Effect makeCopy(Effect effect) {
        Effect s = effect.isInfinite()? new SeeMinds(true) : new SeeMinds(duration);
        s.setCaster(caster);
        return s;
    }
}
