package world.generation;

import actors.world.WorldActor;
import world.Tile;
import world.World;
import world.geometry.Point;
import world.room.Room;
import world.room.rectRoom;
import world.room.tiledRoom;

import java.util.ArrayList;
import java.util.Random;

public class WorldBuilder {

    //<editor-fold desc="Instance Variables">
    /**
     * A 2D array of tiles making up the world
     */
    private Tile[][] tiles;

    /**
     * List of rooms in the world
     */
    private ArrayList<Room> rooms;

    /**
     * The width of the map
     */
    private int width;

    /**
     * The height of the map
     */
    private int height;

    /**
     * prng
     */
    private Random random;
    //</editor-fold>

    public WorldBuilder(int width, int height) {
        this.width = width;
        this.height = height;
        this.tiles = new Tile[width][height];
        this.rooms = new ArrayList<>();
        random = new Random(System.currentTimeMillis());
    }

    /**
     * @return A world built from the provided tile grid
     */
    public World build() {
        World w = new World(tiles);
        new WorldActor(w);
        return w;
    }

    private WorldBuilder randomizeTiles() {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                tiles[x][y] = Math.random() < 0.5 ? Tile.FLOOR : Tile.WALL;
            }
        }
        return this;
    }

    public WorldBuilder fill(Tile t) {
        for(int i = 0; i < width; i++) {
            for(int j = 0; j < height; j++) {
                tiles[i][j] = t;
            }
        }
        return this;
    }

    public WorldBuilder makeCaves() {
        return randomizeTiles().smooth(8);
    }

    public WorldBuilder smooth(int times) {
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
        return this;
    }

    public WorldBuilder makeRooms() {

        rectRoom r;
        int x, y, w, h;

        //First, make everything impassible
        for(int i = 0; i < tiles.length; i++)
            for(int j = 0; j < tiles[0].length; j++)
                tiles[i][j] = Tile.BOUNDS;

        /*
         * Generate random rooms. If it overlaps with an existing one, try to move it out of the way.
         * If it still overlaps after that, delete it.
         */
        for(int i = 0; i < 15; i++) {
            w = random.nextInt(18) + 9;
            h = random.nextInt(18) + 9;

            x = random.nextInt(width - w - 3) + 4;
            y = random.nextInt(height - h - 3) + 4;

            r = new rectRoom(w, h, x, y);

            boolean overlap = false;
            for (int t = 0; t < 2; ) {
                for (Room room : rooms) {
                    if (room.overlap(r)) {

                        if (t == 0) {
                            int overlapX = room.xOverlap(r);
                            int overlapY = room.yOverlap(r);

                            r.getP().setX(r.getP().getX() + overlapX);
                            r.getP().setY(r.getP().getY() + overlapY);
                        }

                        overlap = true;
                        t++;
                        break;
                    }
                    else overlap = false;
                }

                if(!overlap) break;
            }

            if(!overlap) rooms.add(r);
        }

        for(Room room : rooms) {
            for(int i = room.getP().getX(); i < room.getP().getX() + room.getWidth() - 1; i++) {
                for(int j = room.getP().getY(); j < room.getP().getY() + room.getHeight() - 1; j++) {
                    if(room.getTileAt(i, j) != null) tiles[i][j] = room.getTileAt(i, j);
                }
            }
        }

        return this;
    }

    public WorldBuilder makeBSPRooms() {

        Tile[][] roomTiles = new Tile[width][height];

        for(int i = 0; i < width; i++) {
            for(int j = 0; j < height; j++) {
                roomTiles[i][j] = Tile.WALL;
            }
        }

        tiledRoom w = new tiledRoom(roomTiles, new Point(0, 0));

        this.tiles = new BSPNode(w, true, 0, 0.66, 0.4, random).getRoom().getTiles();
        return this;
    }

    public WorldBuilder padRooms(int padx, int pady, Tile tile) {
        Tile[][] tilesPadded = new Tile[width + 2 * padx][height + 2 * pady];

        for(int i = 0; i < width + 2 * padx; i++) {
            for(int j = 0; j < height + 2 * pady; j++) {
                if(i < padx || i >= width + padx || j < pady || j >= height + pady)
                    tilesPadded[i][j] = tile;
                else
                    tilesPadded[i][j] = tiles[i - padx][j - pady];

            }
        }

        this.tiles = tilesPadded;
        return this;
    }

}
