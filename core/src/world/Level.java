package world;

public abstract class Level {

    /**
     * The tiles making up this level
     */
    protected Tile[][] tiles;

    public Level(Tile[][] tiles) {
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
                adj[i + 1][j + 1] = tiles[x + i][y + j];
            }
        }

        return adj;
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
}
