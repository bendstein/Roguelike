package world;

public class Stairs {

    /**
     * The staircase that the stairs leads to
     */
    private Stairs destination;

    /**
     * The level this staircase is in
     */
    private Level level;

    /**
     * X coordinate of the stairs on their home level
     */
    private int x;

    /**
     * Y coordinate of the stairs on their home level
     */
    private int y;

    /**
     * True if the stairs go up.
     */
    private boolean up;

    /**
     * The staircase's tile
     */
    private Tile t;

    public Stairs(int x, int y, Stairs destination, Level level, boolean up) {
        this.x = x;
        this.y = y;
        this.destination = destination;
        this.level = level;
        this.up = up;
        if(up) t = Tile.STAIRS_UP;
        else t = Tile.STAIRS_DOWN;
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

    public Tile getT() {
        return t;
    }

    public void setT(Tile t) {
        this.t = t;
    }
}
