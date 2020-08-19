package world.thing;

import com.badlogic.gdx.physics.box2d.BodyDef;
import creatureitem.Creature;
import game.Main;
import world.Tile;
import world.geometry.Point;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

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

    protected ArrayList<String> properties;

    protected int x, y;

    protected int orientation;

    public Thing(Tile tile, String ... properties) {
        open = false;
        this.tile = tile;
        this.properties = new ArrayList<>(Arrays.asList(properties));
        x = y = 0;
        orientation = 0;
    }

    public Thing(Tile tile, boolean open, String ... properties) {
        this.open = open;
        this.tile = tile;
        this.properties = new ArrayList<>(Arrays.asList(properties));
        x = y = 0;
        orientation = 0;
    }

    public Thing(Thing thing) {
        this.open = thing.open;
        this.tile = thing.tile;
        this.properties = new ArrayList<>(thing.properties);
        orientation = thing.orientation;
    }

    public void interact() {
        behavior.onInteract();
    }

    public boolean interact(Creature c) {
        return behavior.onInteract(c);
    }

    //<editor-fold desc="Getters and Setters">

    public int getOrientation() {
        return orientation;
    }

    public void setOrientation(int orientation) {
        this.orientation = orientation;
    }

    public ArrayList<String> getProperties() {
        return properties;
    }

    public void setProperties(ArrayList<String> properties) {
        this.properties = properties;
    }

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


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Thing)) return false;
        Thing thing = (Thing) o;
        return open == thing.open &&
                x == thing.x &&
                y == thing.y &&
                tile == thing.tile;
    }

    @Override
    public int hashCode() {
        return Objects.hash(open, tile, x, y);
    }
}
