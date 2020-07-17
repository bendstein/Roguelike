package world.thing;

import world.Level;
import world.Tile;

public class Stairs extends Thing {

    /**
     * The staircase that the stairs leads to
     */
    private Stairs destination;

    /**
     * The level this staircase is in
     */
    private Level level;

    /**
     * True if the stairs go up.
     */
    private boolean up;

    public Stairs(int x, int y, Stairs destination, Level level, boolean up) {
        super(Tile.STAIRS, true);
        this.x = x;
        this.y = y;
        this.destination = destination;
        this.level = level;
        this.up = up;
    }

    public Stairs getDestination() {
        return destination;
    }

    public void setDestination(Stairs destination) {
        this.destination = destination;
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

    public Level getLevel() {
        return level;
    }

    public void setLevel(Level level) {
        this.level = level;
    }

    public boolean isUp() {
        return up;
    }

    public void setUp(boolean up) {
        this.up = up;
    }
}
