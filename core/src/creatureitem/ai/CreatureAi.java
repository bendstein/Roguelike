package creatureitem.ai;

import creatureitem.Creature;
import world.Tile;
import world.geometry.Line;
import world.geometry.Point;

import java.util.ArrayList;

public class CreatureAi {

    //<editor-fold desc="Instance Variables">
    /**
     * The creature following this behavior set
     */
    protected Creature creature;
    //</editor-fold>

    /**
     * Assign the AI to its creature
     * @param creature The creature following this behavior set
     */
    public CreatureAi(Creature creature) {
        this.creature = creature;
        this.creature.setAi(this);
    }

    /**
     * Perform any actions the creature does on entering a new tile
     * @param x X coordinate
     * @param y Y coordinate
     * @param tile The tile they're trying to enter
     */
    public void onEnter(int x, int y, Tile tile) {

        if(Creature.canEnter(x, y, creature.getLevel())) {
            creature.setCoordinates(x, y);
            if(creature.getLevel().getItemAt(x, y) != null) {
                creature.doAction("step on %s.", creature.getLevel().getItemAt(x, y).getName());
            }
        }
    }

    /**
     * Perform any actions the creature does when it's time to update
     */
    public void onUpdate() {

    }

    /**
     * Perform any actions the creature does when a new message appears
     * @param message The message being sent
     */
    public void onNotify(String message) {

    }

    public boolean canSee(int x, int y) {

        if(Math.pow(creature.getX() - x, 2) + Math.pow(creature.getY() - y, 2) > Math.pow(creature.getVisionRadius(), 2))
            return false;

        for(Point p : new Line(creature.getX(), x, creature.getY(), y)) {
            if(creature.getLevel().getTileAt(p.getX(), p.getY()) == Tile.BOUNDS)
                continue;
            else if(p.getX() == x && p.getY() == y)
                continue;
            else if(creature.getLevel().getTileAt(creature.getX(), creature.getY()) == Tile.DOOR) {
                if(creature.getLevel().getTileAt(p.getX(), p.getY()).isGround() &&
                        (creature.getLevel().getTileAt(p.getX(), p.getY()) != Tile.DOOR ||
                                (p.getX() == creature.getX() && p.getY() == creature.getY())))
                    continue;
            }
            else if(creature.getLevel().getTileAt(p.getX(), p.getY()).isGround() &&
                    creature.getLevel().getTileAt(p.getX(), p.getY()) != Tile.DOOR)
                continue;


            return false;
        }

        return true;
    }

    public void wander() {
        int mx = creature.getLevel().getRandom().nextInt(3) - 1;
        int my = creature.getLevel().getRandom().nextInt(3) - 1;
        creature.moveBy(mx, my);
    }

    public ArrayList<String> getMessages() {
        return new ArrayList<String>();
    }



}
