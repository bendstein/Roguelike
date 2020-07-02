package world.room;

import world.geometry.Point;
import world.Tile;

/**
 * Abstract class representing a Room object
 */
public abstract class Room {

    /**
     * 2D array of the tiles in the room
     */
    protected Tile[][] tiles;

    /**
     * Coordinates for bottom left corner
     */
    protected Point p;

    //<editor-fold desc="Getters and Setters">
    public abstract int getHeight();

    public abstract int getWidth();

    public abstract Tile[][] getTiles();

    public abstract Point getP();

    public abstract void setP(Point p);

    /**
     * @param xc The x coordinate
     * @param yc The y coordinate
     * @return The tile at tiles[xc - x][yc - y]
     */
    public Tile getTileAt(int xc, int yc) {
        return tiles[xc - p.getX()][yc - p.getY()];
    }

    /**
     * @param xc The x coordinate
     * @param yc The y coordinate
     * @return The tile at tiles[x + xc][y + yc]
     */
    public Tile getTileAtAdjusted(int xc, int yc) {
        return tiles[p.getX() + xc][p.getY() + yc];
    }

    public void setTileAt(int xc, int yc, Tile t) {
        if(xc >= getWidth() || yc >= getHeight())
            System.out.println();
        tiles[xc][yc] = t;
    }

    public void setTileAtAdjusted(int xc, int yc, Tile t) {
        tiles[p.getX() + xc][p.getY() + yc] = t;
    }
    //</editor-fold>

    /**
     * @param r Room to compare with
     * @return true if this is overlapping with r
     */
    public abstract boolean overlap(Room r);

    /**
     * @param r Room to compare with
     * @return The distance r must be shifted horizontally so that it doesn't overlap anymore
     */
    public abstract int xOverlap(Room r);

    /**
     * @param r Room to compare with
     * @return The distance r must be shifted vertically so that it doesn't overlap anymore
     */
    public abstract int yOverlap(Room r);

}
