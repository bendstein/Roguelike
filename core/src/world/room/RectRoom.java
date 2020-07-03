package world.room;

import world.Tile;

public class RectRoom extends Room {

    public RectRoom(int x, int y, int width, int height) {
        super(x, y, new Tile[width][height]);
    }

    public void generate() {
        for(int i = 0; i < getWidth(); i++) {
            for(int j = 0; j < getHeight(); j++) {
                if(i == 0 || i == getWidth() - 1 || j == 0 || j == getHeight() - 1)
                    tiles[i][j] = Tile.WALL;
                else
                    tiles[i][j] = Tile.FLOOR;
            }
        }
    }

}
