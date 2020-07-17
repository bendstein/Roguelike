package world.generation;

import utility.Utility;
import utility.WeightedRandom;
import world.Level;
import world.Tile;
import world.geometry.AStarPoint;
import world.geometry.Point;
import world.thing.DoorBehavior;
import world.thing.Thing;
import world.room.*;

import java.util.ArrayList;
import java.util.Random;
import java.util.Stack;
import java.util.TreeMap;

public class LevelFactory {

    //<editor-fold desc="Instance Variables">
    /**
     * A 2D array of tiles making up the level
     */
    private Tile[][] tiles;

    /**
     * List of rooms in the level
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

    /**
     * Weighted prng
     */
    private WeightedRandom wrandom;

    //</editor-fold>

    public LevelFactory(int width, int height, Random random) {
        this.width = width;
        this.height = height;
        this.tiles = new Tile[width][height];
        this.rooms = new ArrayList<>();
        this.random = random;
        wrandom = new WeightedRandom(random);
    }

    /**
     * @return A level built from the provided tile grid
     */
    public Level build() {
        ArrayList<Thing> things = new ArrayList<>();

        for(int i = 0; i < tiles.length; i++) {
            for(int j = 0; j < tiles[0].length; j++) {
                if(tiles[i][j] == Tile.DOOR) {
                    tiles[i][j] = Tile.FLOOR;
                    Thing door = new Thing(Tile.DOOR);
                    door.setLocation(i, j);
                    new DoorBehavior(door);
                    things.add(door);
                }
            }
        }

        Level l = new Level(tiles, random);
        l.setThings(things);
        return l;
    }

    private LevelFactory randomizeTiles() {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                tiles[x][y] = random.nextDouble() < 0.5 ? Tile.FLOOR : Tile.WALL;
            }
        }
        return this;
    }

    /**
     * Fill the whole map with tile t
     * @param t The tile
     * @return this
     */
    public LevelFactory fill(Tile t) {
        for(int i = 0; i < width; i++) {
            for(int j = 0; j < height; j++) {
                tiles[i][j] = t;
            }
        }
        return this;
    }

    /**
     * Replace all tiles of type original with tile t
     * @param original The tile to replace
     * @param t The tile replacing the original
     * @return this
     */
    public LevelFactory replaceAll(Tile original, Tile t) {
        for(int i = 0; i < width; i++) {
            for(int j = 0; j < height; j++) {
                if(tiles[i][j] == original) tiles[i][j] = t;
            }
        }
        return this;
    }

    public LevelFactory makeCaves() {
        return randomizeTiles().smooth(8);
    }

    public LevelFactory smooth(int times) {
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

    /**
     * A level generation algorithm.
     * @return this
     */
    public LevelFactory generate() {

        //First, fill the map completely with wall.
        fill(Tile.WALL);

        Room root;
        int w, wi;
        int h, hi;

        int minx, maxx, miny, maxy;

        //Randomly select a room type with the given weights
        TreeMap<Integer, Float> roomTypeWeights = new TreeMap<Integer, Float>() {
            {
                put(0, 1f);
                put(1, 1f);
                put(2, 1f);
                put(3, 1f);
                put(4, 1f);
                put(5, 1f);
            }
        };
        TreeMap<Integer, Float> rectWeights = new TreeMap<Integer, Float>() {
            {
                put(3,0.5f);
                put(4,0.75f);
                put(5,1f);
                put(6,1f);
                put(7,1f);
                put(8,0.75f);
                put(9,0.5f);
                put(10,0.3f);
                put(11,0.2f);
            }
        };
        TreeMap<Integer, Float> ellipseWeights = new TreeMap<Integer, Float>() {
            {
                put(5,1f);
                put(6,1f);
                put(7,1f);
                put(8,0.75f);
                put(9,0.75f);
                put(10,0.5f);
                put(11,0.5f);
                put(12,0.5f);
                put(13,0.4f);
                put(14,0.4f);
                put(15,0.2f);
                put(16,0.2f);
                put(17,0.1f);
                put(18,0.1f);
                put(19,0.05f);
                put(20,0.05f);
            }
        };
        TreeMap<Integer, Float> caWeights = new TreeMap<Integer, Float>() {
            {
                put(5,1f);
                put(6,1f);
                put(7,1f);
                put(8,0.75f);
                put(9,0.75f);
                put(10,0.5f);
                put(11,0.5f);
                put(12,0.5f);
                put(13,0.4f);
                put(14,0.4f);
                put(15,0.2f);
                put(16,0.2f);
                put(17,0.1f);
                put(18,0.1f);
                put(19,0.05f);
                put(20,0.05f);
            }
        };

        int choice = wrandom.next(roomTypeWeights);

        switch (choice) {
            case 1: {
                w = random.nextInt(8) + 3;
                h = random.nextInt(8) + 3;
                root = new LRoom(w, h, random.nextInt(w - 2) + 2, random.nextInt(h - 2) + 2, random.nextInt(7));
                break;
            }
            case 2: {
                w = random.nextInt(15) + 5;
                h = random.nextInt(15) + 5;
                root = new CellularAutomataRoom(w, h, random);
                break;
            }
            case 3: {
                w = random.nextInt(12) + 8;
                w = w % 2 == 0 ? w - 1 : w;
                h = random.nextInt(12) + 8;
                h = h % 2 == 0 ? h - 1 : h;
                root = new EllipseRoom(w, h);
                break;
            }
            case 4: {
                w = random.nextInt(12) + 8;
                h = random.nextInt(12) + 8;
                wi = random.nextInt(w - 4) + 2;
                hi = random.nextInt(h - 4) + 2;
                root = new RectLoopRoom(w, h, wi, hi);
                break;
            }
            case 5: {
                w = random.nextInt(12) + 8;
                w = w % 2 == 0 ? w - 1 : w;
                h = random.nextInt(12) + 8;
                h = h % 2 == 0 ? h - 1 : h;
                wi = random.nextInt(w - 4) + 3;
                wi = wi % 2 == 0 ? wi - 1 : wi;
                hi = random.nextInt(h - 4) + 3;
                hi = hi % 2 == 0 ? hi - 1 : hi;
                root = new DonutRoom(0, 0, w, h, wi, hi);
                break;
            }
            default: {
                w = random.nextInt(12) + 8;
                h = random.nextInt(12) + 8;
                root = new RectRoom(w, h);
                break;
            }
        }


        int x = width/2;
        int y = height/2;

        //Add the root to the map
        rooms.add(root);
        cutOutRoom(root, x, y);
        minx = x;
        miny = y;
        maxx = x + root.getWidth();
        maxy = y + root.getHeight();

        /*
         * For the rest of the rooms, generate a random room like before, but now, also:
         * Generate a hallway sometimes, with a door at the end. If we don't, place a random door.
         * Then, slide the room around the map until we find a place where the door has floor on both
         * sides, and the room doesn't overlap with any others.
         */


        Room next;

        for(int r = 0; r < 22; r++) {

            choice = wrandom.next(roomTypeWeights);

            switch (choice) {
                case 1: {
                    w = wrandom.next(rectWeights);
                    h = wrandom.next(rectWeights);
                    next = new LRoom(w, h, random.nextInt(w - 2) + 2, random.nextInt(h - 2) + 2, random.nextInt(7));
                    break;
                }
                case 2: {
                    w = wrandom.next(caWeights);
                    h = wrandom.next(caWeights);
                    next = new CellularAutomataRoom(w, h, random);
                    break;
                }
                case 3: {
                    w = wrandom.next(ellipseWeights);
                    w = w % 2 == 0 ? w - 1 : w;
                    h = wrandom.next(ellipseWeights);
                    h = h % 2 == 0 ? h - 1 : h;
                    next = new EllipseRoom(w, h);
                    break;
                }
                case 4: {
                    w = wrandom.next(ellipseWeights);
                    h = wrandom.next(ellipseWeights);
                    wi = random.nextInt(w - 3) + 2;
                    hi = random.nextInt(h - 3) + 2;
                    next = new RectLoopRoom(w, h, wi, hi);
                    break;
                }
                case 5: {
                    w = wrandom.next(ellipseWeights);
                    w = w % 2 == 0 ? w - 1 : w;
                    h = wrandom.next(ellipseWeights);
                    h = h % 2 == 0 ? h - 1 : h;
                    wi = random.nextInt(w - 2) + 3;
                    wi = wi % 2 == 0 ? wi - 1 : wi;
                    hi = random.nextInt(h - 2) + 3;
                    hi = hi % 2 == 0 ? hi - 1 : hi;
                    next = new DonutRoom(0, 0, w, h, wi, hi);
                    break;
                }
                default: {
                    w = wrandom.next(rectWeights);
                    h = wrandom.next(rectWeights);
                    next = new RectRoom(w, h);
                    break;
                }
            }

            ArrayList<Point> points = new ArrayList<>();

            boolean corridor = random.nextDouble() < .35;
            if(corridor) points.addAll(next.placeRandomCorridor(random));
            else if(!corridor || points.isEmpty())
                points.addAll(next.getDoorLocations(random));

            boolean placed = false;
            boolean overlap;

            for(int i = Math.max(0, minx - (2 * w) - 1); i < Math.min(width, (2 * w) + maxx + 1); i++) {
                for(int j = Math.max(0, miny - (2 * h) - 1); j < Math.min(height, (2 * h) + maxy + 1); j++) {
                    if(tiles[i][j] == Tile.WALL) {
                        Tile[][] adj = Utility.getAdjacentTiles(tiles, i, j);
                        int count = 0;

                        for(int i1 = 0; i1 < adj.length; i1++) {
                            for(int j1 = 0; j1 < adj[0].length; j1++) {
                                if(adj[i1][j1] == Tile.FLOOR)
                                    count++;
                            }
                        }

                        if(count > 1) {

                            for(Point p : points) {
                                next.setTileAt(p.getX(), p.getY(), Tile.DOOR);
                                next.coordinatesCenteredAt(i, j, p.getX(), p.getY());

                                overlap = overlap(next, minx, maxx, miny, maxy);

                                if(!overlap) {
                                    placed = true;
                                    rooms.add(next);
                                    cutOutRoom(next, next.getX(), next.getY());
                                    break;
                                }
                                else
                                    next.setTileAt(p.getX(), p.getY(), null);

                            }

                        }
                    }

                    if(placed) break;
                }

                if(placed) break;
            }

            if(!placed) r--;
        }

        int[][] costs = Utility.toCostArray(tiles);

        Stack<AStarPoint> path;
        for(int i = 0; i < width; i++) {
            for(int j = 0; j < height; j++) {
                if(tiles[i][j] == Tile.WALL) {
                    if(Utility.isAdjacentTo(tiles, Tile.FLOOR, i, j, true) > 2) continue;
                    if(Utility.isAdjacentTo(tiles, Tile.DOOR, i, j, false) > 0) continue;
                    boolean door = false;
                    if(i + 1 < width && i - 1 >= 0) {
                        if(tiles[i + 1][j] == Tile.FLOOR && tiles[i - 1][j] == Tile.FLOOR) {
                            path = Utility.aStar(costs, new Point(i + 1, j), new Point(i - 1, j));
                            if(path.size() > 15) {
                                tiles[i][j] = Tile.DOOR;
                                costs[i][j] = 1;
                                door = true;
                            }
                        }
                    }

                    if(!door && j + 1 < height && j - 1 >= 0) {
                        if(tiles[i][j + 1] == Tile.FLOOR && tiles[i][j - 1] == Tile.FLOOR) {
                            path = Utility.aStar(costs, new Point(i, j + 1), new Point(i, j - 1));
                            if(path.size() > 15) {
                                tiles[i][j] = Tile.DOOR;
                                costs[i][j] = 1;
                            }
                        }
                    }
                }
            }
        }

        return this;
    }

    public LevelFactory padWorldWith(int padx, int pady, Tile tile) {
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

    public LevelFactory cutOutRoom(Room r, int x, int y) {
        r.setX(x);
        r.setY(y);
        for(int i = x; i < Math.min(x + r.getWidth(), width); i++) {
            for(int j = y; j < Math.min(y + r.getHeight(), height); j++) {
                Tile t = r.getTileAtParent(i, j);
                if(t != null)
                    tiles[i][j] = t;
            }
        }

        return this;
    }

    public void clear() {
        this.tiles = new Tile[width][height];
        this.rooms = new ArrayList<>();
    }

    public boolean overlap(Room r, int minx, int maxx, int miny, int maxy) {

        int x = r.getX();
        int y = r.getY();
        int w = r.getWidth();
        int h = r.getHeight();

        if(x >= 0 && x + w < width && y >= 0 && y + h < height && x > maxx && y > maxy && x + w < minx && y + h < miny)
            return false;
        if(x < 0 || x + w >= width || y < 0 || y + h >= height)
            return true;

        for(int i = x; i < Math.min(x + w, width); i++) {
            for(int j = y; j < Math.min(y + h, height); j++) {
                Tile t = r.getTileAtParent(i, j);
                Tile e = tiles[i][j];
                if(t != null && e != Tile.WALL) return true;
                if(t != null && t != Tile.DOOR)
                    if(Utility.isAdjacentTo(tiles, Tile.FLOOR, i, j, false) > 0) return true;
                if(t == Tile.DOOR)
                    if(Utility.isAdjacentTo(tiles, Tile.DOOR, i, j, false) > 0) return true;
            }
        }

        return false;
    }

}
