package world.room;

import world.Tile;
import world.geometry.Point;

public class tiledRoom extends Room {

    public tiledRoom(Tile[][] tiles) {
        this.tiles = tiles;
    }

    public tiledRoom(Tile[][] tiles, Point p) {
        this.tiles = tiles;
        this.p = p;
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

    @Override
    public boolean overlap(Room r) {
        return false;
    }

    @Override
    public int xOverlap(Room r) {
        return 0;
    }

    @Override
    public int yOverlap(Room r) {
        return 0;
    }
}
