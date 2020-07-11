package world.room;

import world.Tile;

public class RectRoom extends Room {

    public RectRoom(int x, int y, int width, int height) {
        super(x, y, width, height);

        generate();
    }

    public RectRoom(int width, int height) {
        super(0, 0, width, height);

        generate();
    }

    public void generate() {
        fill(Tile.FLOOR);
    }

}
