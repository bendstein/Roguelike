package world.room;

import world.Level;
import world.Tile;

public abstract class Room extends Level {

    /**
     * The level this room belongs to
     */
    protected Level parent;

    /**
     * The x coordinate of the bottom left corner of the room in the parent's coordinate system.
     */
    protected int x;

    /**
     * The y coordinate of the bottom left corner of the room in the parent's coordinate system.
     */
    protected int y;

    public Room(Tile[][] tiles) {
        super(tiles);
        x = y = 0;
    }

    public Room(int x, int y, Tile[][] tiles) {
        super(tiles);
        this.x = x;
        this.y = y;
    }

    /**
     * @param xp X coordinate
     * @param yp Y coordinate
     * @return The tile at (xp, yp) in the parent's coordinate system
     */
    public Tile getTileAtParent(int xp, int yp) {
        return tiles[xp - x][yp - y];
    }

    /**
     * Set the tile at (xp, yp) in the parent's coordinate system to be x
     * @param xp X coordinate
     * @param yp Y coordinate
     * @param t The tile
     */
    public void setTileAtParent(int xp, int yp, Tile t) {
        tiles[xp - x][yp - y] = t;
    }

}
