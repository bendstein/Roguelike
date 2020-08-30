package creatureitem.effect.effects;

import creatureitem.Creature;
import creatureitem.Player;
import creatureitem.effect.Effect;
import world.Level;

public class MagicMapping extends Effect {

    public MagicMapping() {
        super();
        duration = 0;
        this.infinite = false;
    }

    public MagicMapping(boolean infinite) {
        super();
        duration = 0;
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
        affect(l.getCreatureAt(x, y));
        l.calculateOrientations();
    }

    /**
     * What the effect does when it is active
     *
     * @param c The creature to apply the effect to
     */
    @Override
    public void affect(Creature c) {
        if(c instanceof Player) {
            ((Player) c).setSeenAllTiles();
            c.getLevel().getDungeon().getGame().getPlayScreen().getUi().setRequestMinimapUpdate(true);
            c.getLevel().calculateOrientations();
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
    }

    @Override
    public Effect makeCopy(Effect effect) {
        return new MagicMapping(infinite);
    }
}
