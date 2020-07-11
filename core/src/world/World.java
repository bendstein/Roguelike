package world;

public abstract class World {

    /**
     * The tiles making up this level
     */
    protected Tile[][] tiles;

    public World(Tile[][] tiles) {
        this.tiles = tiles;
    }

    public Tile[][] getTiles() {
        return tiles;
    }

    /**
     * @param x X coordinate
     * @param y Y coordinate
     * @return Return the tile at (x, y) in the level's coordinate system.
     */
    public Tile getTileAt(int x, int y) {
        return tiles[x][y];
    }

    /**
     * @param x X coordinate
     * @param y Y coordinate
     * @return A 2d array of tiles who are adjacent to (x, y) in the level's coordinate system.
     */
    public Tile[][] getAdjacentTiles(int x, int y) {
        Tile[][] adj = new Tile[3][3];

        for(int i = -1; i <= 1; i++) {
            for(int j = -1; j <= 1; j++) {
                if(x + i > 0 && x + i < getWidth() && y + j > 0 && y + j < getHeight())
                    adj[i + 1][j + 1] = tiles[x + i][y + j];
                else
                    adj[i + 1][j + 1] = null;
            }
        }

        return adj;
    }

    public int getNumAdj(int x, int y) {
        Tile[][] ts = getAdjacentTiles(x, y);
        int count = 0;
        for(int i = 0; i < ts.length; i++) {
            for(int j = 0; j < ts[0].length; j++) {
                if(i == j) continue;
                else if(ts[i][j] == null) continue;
                else count++;
            }
        }

        return count;
    }

    public void setTiles(Tile[][] tiles) {
        this.tiles = tiles;
    }

    /**
     * Set the tile at (x, y) in the level's coordinate system to be tile t
     * @param x X coordinate
     * @param y Y coordinate
     */
    public void setTileAt(int x, int y, Tile t) {
        tiles[x][y] = t;
    }

    public int getHeight() {
        return tiles[0].length;
    }

    public int getWidth() {
        return tiles.length;
    }

    /**
     * If the tile at (x, y) in the level's coordinate system is diggable, dig it out.
     * @param x X coordinate
     * @param y Y coordinate
     */
    public void dig(int x, int y) {
        if(tiles[x][y].isDiggable())
            tiles[x][y] = Tile.FLOOR;
    }

    public boolean isOutOfBounds(int x, int y) {
        return x < 0 || x >= getWidth() || y < 0 || y >= getHeight();
    }

    public void smooth(int times) {
        int width = tiles.length;
        int height = tiles[0].length;
        Tile[][] tiles2 = new Tile[width][height];
        for (int time = 0; time < times; time++) {

            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    int floors = 0;
                    int rocks = 0;

                    for (int ox = -1; ox < 2; ox++) {
                        for (int oy = -1; oy < 2; oy++) {
                            if (x + ox < 0 || x + ox >= width || y + oy < 0
                                    || y + oy >= height)
                                continue;

                            if (tiles[x + ox][y + oy] == Tile.FLOOR)
                                floors++;
                            else
                                rocks++;
                        }
                    }
                    tiles2[x][y] = floors >= rocks ? Tile.FLOOR : Tile.WALL;
                }
            }
            tiles = tiles2;
        }
    }

    /**
     * @param tiles A 2d array of tiles
     * @param cw Direction to rotate
     * @param r Number of times to rotate
     * @return tiles, rotated i * 90 degrees in the cw or ccw direction
     */
    public static Tile[][] rotate(Tile[][] tiles, boolean cw, int r) {
        if(r > 0) {
            Tile[][] tilesNew = new Tile[tiles[0].length][tiles.length];

            if(cw) {
                for(int i = 0; i < tiles.length; i++) {
                    for(int j = 0; j < tiles[0].length; j++) {
                        tilesNew[j][tiles.length - i - 1] = tiles[i][j];
                    }
                }
            }
            else {
                for(int i = 0; i < tiles.length; i++) {
                    for(int j = 0; j < tiles[0].length; j++) {
                        tilesNew[tiles[0].length - j - 1][i] = tiles[i][j];
                    }
                }
            }

            return rotate(tilesNew, cw, r - 1);
        }

        return tiles;
    }

    /**
     * @param tiles A 2d array of tiles
     * @param axis Which axis to mirror over. True if x.
     * @return The tiles mirrored across x or y axis.
     */
    public static Tile[][] mirror(Tile[][] tiles, boolean axis) {
        Tile[][] tilesNew = new Tile[tiles.length][tiles[0].length];
        if(axis) {
            for(int i = 0; i < tiles.length; i++) {
                for(int j = 0; j < tiles[0].length; j++) {
                    tilesNew[i][tiles[0].length - 1 - j] = tiles[i][j];
                }
            }
        }
        else {
            for(int i = 0; i < tiles.length; i++) {
                for(int j = 0; j < tiles[0].length; j++) {
                    tilesNew[tiles.length - 1 - i][j] = tiles[i][j];
                }
            }
        }

        return tilesNew;
    }

    /**
     * Fill the tile array with a given tile
     * @param t The tile given
     */
    public void fill(Tile t) {
        for(int i = 0; i < tiles.length; i++) {
            for(int j = 0; j < tiles[0].length; j++) {
                tiles[i][j] = t;
            }
        }
    }
}
