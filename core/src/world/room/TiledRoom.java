package world.room;

import world.Tile;

public class TiledRoom extends Room {

    public TiledRoom(Tile[][] tiles) {
        super(tiles);
    }

    public TiledRoom(int x, int y, Tile[][] tiles) {
        super(x, y, tiles);
    }

}
