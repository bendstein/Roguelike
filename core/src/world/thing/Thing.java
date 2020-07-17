package world.thing;

import creatureitem.Creature;
import world.Tile;
import world.geometry.Point;

/**
 * An object that appears like a tile, but may have other properties.
 * Like a door or a staircase.
 */
public class Thing {

    /**
     * Whether the object is an obstacle
     */
    protected boolean open;

    /**
     * The tile representing this object
     */
    protected Tile tile;

    protected ThingBehavior behavior;

    protected int x, y;

    public Thing(Tile tile) {
        open = false;
        this.tile = tile;
        x = y = 0;
    }

    public Thing(Tile tile, boolean open) {
        this.open = open;
        this.tile = tile;
        x = y = 0;
    }

    public void interact() {
        behavior.onInteract();
    }

    public boolean interact(Creature c) {
        return behavior.onInteract(c);
    }

    //<editor-fold desc="Getters and Setters">
    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public Tile getTile() {
        return tile;
    }

    public void setTile(Tile tile) {
        this.tile = tile;
    }

    public ThingBehavior getBehavior() {
        return behavior;
    }

    public void setBehavior(ThingBehavior behavior) {
        this.behavior = behavior;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setLocation(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void setLocation(Point p) {
        this.x = p.getX();
        this.y = p.getY();
    }

    public Point getLocation() {
        return new Point(x, y);
    }

    //</editor-fold>
}
