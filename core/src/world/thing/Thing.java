package world.thing;

import com.badlogic.gdx.physics.box2d.BodyDef;
import creatureitem.Creature;
import creatureitem.Entity;
import game.Main;
import world.Level;
import world.Tile;
import world.geometry.Point;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

/**
 * An object that appears like a tile, but may have other properties.
 * Like a door or a staircase.
 */
public class Thing extends Entity {

    /**
     * Whether the object is an obstacle
     */
    protected boolean open;

    /**
     * The tile representing this object
     */
    protected Tile tile;

    protected ThingBehavior behavior;

    protected int orientation;

    public Thing(Tile tile, String ... properties) {
        super(0, 0, false, properties);
        open = false;
        this.tile = tile;
        orientation = 0;
    }

    public Thing(Tile tile, boolean open, String ... properties) {
        super(0, 0, false, properties);
        this.open = open;
        this.tile = tile;
        orientation = 0;
    }

    public Thing(Tile tile, boolean open, boolean can_act, String ... properties) {
        super(0, 0, can_act, properties);
        this.open = open;
        this.tile = tile;
        orientation = 0;
    }

    public Thing(Thing thing) {
        super(thing);
        this.open = thing.open;
        this.tile = thing.tile;
        orientation = thing.orientation;
    }

    public void interact() {
        behavior.onInteract();
    }

    public boolean interact(Creature c) {
        return behavior.onInteract(c);
    }

    @Override
    public void act(Level l) {
        if(behavior != null) behavior.onAction(l);
    }

    @Override
    public void process(Level l) {
        l.advance(this);
    }

    //<editor-fold desc="Getters and Setters">

    public int getOrientation() {
        return orientation;
    }

    public void setOrientation(int orientation) {
        this.orientation = orientation;
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

    //</editor-fold>

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Thing)) return false;
        Thing thing = (Thing) o;
        return  x == thing.x &&
                y == thing.y &&
                open == thing.open &&
                tile == thing.tile;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, open, tile);
    }
}
