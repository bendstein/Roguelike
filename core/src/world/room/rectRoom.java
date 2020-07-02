package world.room;

import world.geometry.Point;
import world.Tile;

/**
 * A simple rectangular room of tiles
 */
public class rectRoom extends Room {

    /**
     * Constructor for an w by h room
     * @param w X dimension
     * @param h Y dimension
     * @param x X coordinate of bottom left corner
     * @param y Y coordinate of bottom left corner
     */
    public rectRoom(int w, int h, int x, int y) {
        generateRoom(w, h);
        p = new Point(x, y);
    }

    //<editor-fold desc="Getters">
    @Override
    public int getHeight() {
        return tiles[0].length;
    }

    @Override
    public int getWidth() {
        return tiles.length;
    }

    @Override
    public Tile[][] getTiles() {
        return tiles;
    }

    @Override
    public Point getP() {
        return p;
    }

    @Override
    public void setP(Point p) {
        this.p = new Point(p.getX(), p.getY());
    }

    //</editor-fold>

    /**
     * @param r Room to compare with
     * @return true if this is overlapping with r
     */
    @Override
    public boolean overlap(Room r) {

        //If there are no intersecting x values, return false
        if(p.getX() > r.getP().getX() + r.getWidth() || p.getX() + getWidth() < r.getP().getX())
            return false;

        //If there are no intersecting y values, return false
        if(p.getY() > r.getP().getY() + r.getHeight() || p.getY() + getHeight() < r.getP().getY())
            return false;

        return true;
    }

    /**
     * @param r Room to compare with
     * @return The distance r must be shifted horizontally so that it doesn't overlap anymore
     */
    @Override
    public int xOverlap(Room r) {
        return Math.min(Math.abs(p.getX() - r.getP().getX() - r.getWidth()), Math.abs(p.getX() + getWidth() - r.getP().getX() - r.getWidth()))
                == Math.abs(p.getX() - r.getP().getX() - r.getWidth()) ? p.getX() - r.getP().getX() - r.getWidth() : p.getX() + getWidth() - r.getP().getX() - r.getWidth();
    }

    /**
     * @param r Room to compare with
     * @return The distance r must be shifted vertically so that it doesn't overlap anymore
     */
    @Override
    public int yOverlap(Room r) {
        return Math.min(Math.abs(p.getY() - (r.getP().getY() + r.getHeight())), Math.abs((p.getY() + getHeight()) - (r.getP().getY() + r.getHeight())))
                == Math.abs(p.getY() - r.getP().getY() - r.getHeight()) ? p.getY() - r.getP().getY() - r.getHeight() : p.getY() + getHeight() - r.getP().getY() - r.getHeight();
    }

    /**
     * Generate an w by h rectangular room
     * @param w The length of the room
     * @param h The width of the room
     */
    public void generateRoom(int w, int h) {
        this.tiles = new Tile[w][h];

        for(int i = 0; i < w; i++) {
            for(int j = 0; j < h; j++) {
                if(i == 0 || j == 0)
                    tiles[i][j] = Tile.WALL;
                else if(i == w - 2 || j == h - 2)
                    tiles[i][j] = Tile.WALL;
                else
                    tiles[i][j] = Tile.FLOOR;
            }
        }
    }

}
