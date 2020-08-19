package creatureitem.effect.effects;

import creatureitem.Creature;
import creatureitem.effect.Effect;
import world.Level;
import world.Tile;

public class ObliterateWall extends Effect {

    public ObliterateWall() {
        super();
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

        if(x < 0 || y < 0 || x >= l.getWidth() || y >= l.getHeight()) return;

        Tile floor = Tile.BOUNDS;
        l.removeThing(x, y);

        /*
         * Find the closest floor tile, and replace the wall with that.
         */
        boolean found = false;
        for(int i = 0; i <= l.getWidth(); i++) {
            for(int j = 0; j <= l.getHeight(); j++) {

                for(int i1 = 0; i1 <= 1; i1++) {
                    for(int j1 = 0; j1 <= 1; j1++) {
                        int x1 = x + (i * (i1 == 0? 1 : -1)), y1 = y + (j * (j1 == 0? 1 : -1));
                        if(x1 >= 0 && y1 >= 0 && x1 < l.getWidth() && y1 < l.getHeight()) {
                            if(l.getTileAt(x1, y1).isGround()) {
                                floor = l.getTileAt(x1, y1);
                                found = true;
                                break;
                            }
                        }
                    }

                    if(found) break;
                }

                if(found) break;
            }

            if(found) break;
        }

        l.setTileAt(x, y, floor);
        l.setRequestLightingUpdate(true);
        
    }

    /**
     * What the effect does when it is active
     *
     * @param c The creature to apply the effect to
     */
    @Override
    public void affect(Creature c) {

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
        return new ObliterateWall();
    }
}
