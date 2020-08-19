package world.generation;

import creatureitem.Creature;
import creatureitem.generation.CreatureItemFactory;
import creatureitem.item.*;
import utility.Utility;
import utility.WeightedRandom;
import world.Level;
import world.Tile;
import world.geometry.AStarPoint;
import world.geometry.Point;
import world.thing.*;
import world.room.*;

import java.util.*;
import java.util.stream.Collectors;

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

    private ArrayList<Creature> creatures;

    private Item[][] items;

    private ArrayList<Thing> things;

    //</editor-fold>

    public LevelFactory(int width, int height, Random random) {
        this.width = width;
        this.height = height;
        this.tiles = new Tile[width][height];
        this.rooms = new ArrayList<>();
        this.random = random;
        wrandom = new WeightedRandom(random);
        creatures = new ArrayList<>();
        items = new Item[width][height];
        things = new ArrayList<>();
    }

    /**
     * @return A level built from the provided tile grid
     */
    public Level build() {

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
        l.setCreatureQueue(creatures);
        l.setItems(items);
        l.setRooms(rooms);

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

    public LevelFactory cellularAutomata() {
        fill(Tile.WALL);
        Room r = new CellularAutomataRoom(width, height, random, .5f, 2, 4, 5, 8, 8, .4f);
        return cutOutRoom(r, 0, 0);
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

    public LevelFactory test() {
        fill(Tile.WALL);
        cutOutRoom(prefabs.get("Test"), 5, 5);
        return this;
    }

    /**
     * A level generation algorithm.
     * @return this
     */
    public LevelFactory generate() {

        boolean try_again;
        do {
            try_again = false;
            //First, fill the map completely with wall.
            fill(Tile.WALL);

            Room root;
            int w, wi;
            int h, hi;

            int minx, maxx, miny, maxy;

            //Randomly select a room type with the given weights
            TreeMap<Integer, Float> roomTypeWeights = new TreeMap<Integer, Float>() {
                {
                    put(0, 1f); //Rectangle
                    put(1, 1f); //L-Shaped
                    put(2, 1f); //Cellular Automata
                    put(3, 1f); //Ellipse
                    put(4, 1f); //Rectangular donut
                    put(5, 1f); //Elliptical donut
                    put(6, 0f); //T-shaped
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
                    w = wrandom.next(rectWeights);
                    h = wrandom.next(rectWeights);
                    root = new LRoom(w, h, random.nextInt(w - 2) + 2, random.nextInt(h - 2) + 2, random.nextInt(7));
                    break;
                }
                case 2: {
                    w = wrandom.next(caWeights);
                    h = wrandom.next(caWeights);
                    root = new CellularAutomataRoom(w, h, random);
                    break;
                }
                case 3: {
                    w = wrandom.next(ellipseWeights);
                    w = w % 2 == 0 ? w - 1 : w;
                    h = wrandom.next(ellipseWeights);
                    h = h % 2 == 0 ? h - 1 : h;
                    root = new EllipseRoom(w, h);
                    break;
                }
                case 4: {
                    w = wrandom.next(ellipseWeights);
                    h = wrandom.next(ellipseWeights);
                    wi = random.nextInt(w - 3) + 2;
                    hi = random.nextInt(h - 3) + 2;
                    root = new RectLoopRoom(w, h, wi, hi);
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
                    root = new DonutRoom(0, 0, w, h, wi, hi);
                    break;
                }
                case 6: {
                    wi = wrandom.next(rectWeights);
                    w = random.nextInt(wi - 2) + 2;
                    h = wrandom.next(rectWeights);
                    hi = random.nextInt(h - 2) + 2;
                    root = new TRoom(w, h, wi, hi, random.nextInt(7));
                    break;
                }
                default: {
                    w = wrandom.next(rectWeights);
                    h = wrandom.next(rectWeights);
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
            int tries = 0;

            do {
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
                    case 6: {
                        wi = wrandom.next(rectWeights);
                        w = random.nextInt(wi - 2) + 2;
                        h = wrandom.next(rectWeights);
                        hi = random.nextInt(h - 2) + 2;
                        next = new TRoom(w, h, wi, hi, random.nextInt(7));
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

                if(!placed) tries++;
                else tries = 0;
            } while(getArea()/((double)width * height) < 0.4 && tries < 10);

            if(tries >= 10) try_again = true;
        } while(try_again);


        /*
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

         */

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
                            path = Utility.aStar(costs, Point.DISTANCE_CHEBYCHEV, new Point(i + 1, j), new Point(i - 1, j));
                            if(path.size() > 15) {
                                tiles[i][j] = Tile.DOOR;
                                costs[i][j] = 1;
                                door = true;
                            }
                        }
                    }

                    if(!door && j + 1 < height && j - 1 >= 0) {
                        if(tiles[i][j + 1] == Tile.FLOOR && tiles[i][j - 1] == Tile.FLOOR) {
                            path = Utility.aStar(costs, Point.DISTANCE_CHEBYCHEV, new Point(i, j + 1), new Point(i, j - 1));
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
        Item[][] itemsPadded = new Item[width + 2 * padx][height + 2 * pady];

        for(int i = 0; i < width + 2 * padx; i++) {
            for(int j = 0; j < height + 2 * pady; j++) {
                if(i < padx || i >= width + padx || j < pady || j >= height + pady) {
                    tilesPadded[i][j] = tile;
                    itemsPadded[i][j] = null;
                }
                else {
                    tilesPadded[i][j] = tiles[i - padx][j - pady];
                    itemsPadded[i][j] = items[i - padx][j - pady];
                }

            }
        }

        this.tiles = tilesPadded;
        this.items = itemsPadded;
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

                if(r instanceof PreFab) {
                    Item item = ((PreFab) r).getItemAt(r.getPointAtParent(i, j));
                    if(item != null)
                        items[i][j] = item;
                }
            }
        }

        if(r instanceof PreFab) {
            for(Thing t : ((PreFab) r).getThings()) {
                t.setX(r.xToParentX(t.getX()));
                t.setY(r.yToParentY(t.getY()));
                things.add(t);
            }
            for(Creature c : ((PreFab) r).getCreatures()) {
                c.setX(r.xToParentX(c.getX()));
                c.setY(r.yToParentY(c.getY()));
                creatures.add(c);
            }
        }

        return this;
    }

    public void generateItems(Level l, int dangerRange) {
        Inventory pool = getItemPool(l, dangerRange);

        double maxRarity = Double.MIN_VALUE;
        double minRarity = Double.MAX_VALUE;
        for(Item i : pool.asList()) {
            if(i == null) continue;
            maxRarity = Math.max(maxRarity, i.getRarity());
            minRarity = Math.min(minRarity, i.getRarity());
        }


        final double maxRarityFinal = maxRarity;

        /*
         * If nothing to generate, stop.
         */
        if(pool == null || pool.isEmpty() || maxRarity == Double.MIN_VALUE) return;

        /*
         * Get the shortest path between every permutation of staircases.
         */
        //ArrayList<Line> stairPaths = new ArrayList<>();
        ArrayList<Point> stairList = new ArrayList<>();
        //int[][] costs = l.calculateCosts();
        for(Thing t : l.getThings())
            if(t instanceof Stairs) stairList.add(t.getLocation());

        Point[] stairray = new Point[stairList.size()];
        for(int i = 0; i < stairList.size(); i++)
            stairray[i] = stairList.get(i);
            /*
        for(Stairs s : stairList)
            for(Stairs s2 : stairList)
                if(stairList.indexOf(s) > stairList.indexOf(s2))
                    stairPaths.add(Utility.aStarPathToLine(Utility.aStar(costs, Point.DISTANCE_CHEBYCHEV, s.getLocation(), s2.getLocation())));

             */

        for(int i = 0; i < l.getWidth(); i++) {
            for(int j = 0; j < l.getHeight(); j++) {
                if(!l.isPassable(i, j) || items[i][j] != null || l.getThingAt(i, j) != null) continue;
                /*
                 * Get the distance from the closest path to the next staircase
                 */

                /*
                 * Distance to the closest path between staircases on the level.
                 * The further this distance is, the higher the chances of generating an item,
                 * and the higher the chances that the item is rarer.
                 */

                /*
                Line[] lines = new Line[stairPaths.size()];
                for(int index = 0; index < stairPaths.size(); index++)
                    lines[index] = stairPaths.get(index);
                double d = new Point(i, j).minDistanceFrom(Point.DISTANCE_CHEBYCHEV, lines);

                 */

                double d = new Point(i, j).minDistanceFrom(Point.DISTANCE_CHEBYCHEV, stairray);

                /*
                 * Logistic CDF, mean 15, sd 3 and max .01.
                 */
                double probability = .015 * 1/((Math.pow(Math.E, (-(d - 15)/3))) + 1);


                /*
                 * probability * 100% chance to generate an item in this tile.
                 */
                if(random.nextDouble() < probability) {

                    /*
                     * Weight probability of item rarity by rarity, danger level, floor number, and distance.
                     */
                    TreeMap<Double, Float> probabilities = new TreeMap<Double, Float>() {
                        {
                            for(double i = 0; i <= maxRarityFinal; i += .1) {
                                /*
                                double prob = Math.pow(4, -1 * d * i *
                                        (Math.log(l.getDangerLevel() == 0? 1 : l.getDangerLevel())
                                                + (Math.log(l.getFloor_number() == 0? 1 : l.getFloor_number()) * 1.5) + 1));


                                 */
                                /*
                                int f = l.getFloor_number();
                                int dl = l.getDangerLevel();
                                double fir = (i - (Math.log(d == 0? 1 : d)/Math.log(4)));
                                double sec = ((f/20d) + (dl/16d) + 1);
                                double prob = Math.pow(Math.E, -1 *
                                        Math.pow(fir / sec, 2));

                                 */

                                double prob = Math.exp(-(i + 2)/Math.log(Math.max(1, d) + l.getDangerLevel() + l.getFloor_number()));
                                put(i, (float)Math.max(0, prob));
                            }
                                //put(i, (float)(1 / (Math.pow(4, (4/d) * i))));
                        }
                    };

                    double rarityMax = Math.min(Math.max(wrandom.next(probabilities), minRarity), maxRarity);
                    double rarityMin = Math.floor(rarityMax);

                    Item[] potentialItems;

                    do {
                        potentialItems = pool.filterRarity(rarityMin, rarityMax);

                        if(potentialItems.length == 0) {
                            if (rarityMin == 0)
                                break;
                            else
                                rarityMin--;
                        }
                    } while (potentialItems.length == 0);

                    //If there are no potential items, don't place anything on this tile
                    if(potentialItems.length == 0)
                        continue;

                    //Now, choose a random item from what is left.

                    int index = random.nextInt(potentialItems.length);

                    if(potentialItems[index] instanceof Equipable) {
                        if(potentialItems[index] instanceof Ammo) {
                            Ammo a = new Ammo((Ammo)potentialItems[index]);
                            a.setCount(random.nextInt(11) + 1);
                            l.addAt(i, j, a);
                        }
                        else if(potentialItems[index] instanceof Armor) {
                            Armor a = new Armor((Armor)potentialItems[index]);
                            l.addAt(i, j, a);
                        }
                        else if(potentialItems[index] instanceof Weapon) {
                            if(potentialItems[index] instanceof RangedWeapon) {
                                RangedWeapon a = new RangedWeapon((RangedWeapon)potentialItems[index]);
                                l.addAt(i, j, a);
                            }
                            else {
                                Weapon a = new Weapon((Weapon)potentialItems[index]);
                                l.addAt(i, j, a);
                            }
                        }
                        else {
                            Equipable a = new Equipable(potentialItems[index]);
                            l.addAt(i, j, a);
                        }
                    }
                    else if(potentialItems[index] instanceof Potion) {
                        Potion a = new Potion((Potion)potentialItems[index]);
                        l.addAt(i, j, a);
                    }
                    else if(potentialItems[index] instanceof Food) {
                        Food a = new Food(potentialItems[index]);
                        l.addAt(i, j, a);
                    }
                    else {
                        Item a = new Item(potentialItems[index]);
                        l.addAt(i, j, a);
                    }

                }

            }
        }

    }

    public void generateCreatures(Level l, int dangerRange) {
        ArrayList<Creature> pool = getCreaturePool(l, dangerRange);

        double maxRarity = Double.MIN_VALUE;
        double minRarity = Double.MAX_VALUE;
        for(Creature c : pool) {
            if(c == null) continue;
            maxRarity = Math.max(maxRarity, c.getRarity());
            minRarity = Math.min(minRarity, c.getRarity());
        }
        final double maxRarityFinal = maxRarity;

        if(pool == null || pool.isEmpty() || maxRarity == Double.MIN_VALUE) return;

        for(int i = 0; i < l.getWidth(); i++) {
            for(int j = 0; j < l.getHeight(); j++) {
                if(!Creature.canEnter(i, j, l)) continue;
                /*
                 * Get the distance from the closest path to the next staircase
                 */

                /*
                 * Distance to the closest path between staircases on the level.
                 * The further this distance is, the higher the chances of generating an item,
                 * and the higher the chances that the item is rarer.
                 */

                /*
                Line[] lines = new Line[stairPaths.size()];
                for(int index = 0; index < stairPaths.size(); index++)
                    lines[index] = stairPaths.get(index);
                double d = new Point(i, j).minDistanceFrom(Point.DISTANCE_CHEBYCHEV, lines);

                 */

                //double d = new Point(i, j).minDistanceFrom(Point.DISTANCE_CHEBYCHEV, stairray);

                /*
                 * Logistic CDF, mean 15, sd 3 and max .01.
                 */
                //double probability = .015 * 1/((Math.pow(Math.E, (-(d - 15)/3))) + 1);
                double probability = 0.015;


                /*
                 * probability * 100% chance to generate an item in this tile.
                 */
                if(random.nextDouble() < probability) {

                    /*
                     * Weight probability of item rarity by rarity, danger level, floor number, and distance.
                     */

                    TreeMap<Double, Float> probabilities = new TreeMap<Double, Float>() {
                        {
                            for(double i = 0; i <= maxRarityFinal; i += .1) {
                                /*
                                double prob = Math.pow(4, -1 * d * i *
                                        (Math.log(l.getDangerLevel() == 0? 1 : l.getDangerLevel())
                                                + (Math.log(l.getFloor_number() == 0? 1 : l.getFloor_number()) * 1.5) + 1));


                                 */
                                /*
                                int f = l.getFloor_number();
                                int dl = l.getDangerLevel();
                                double fir = (i - (Math.log(d == 0? 1 : d)/Math.log(4)));
                                double sec = ((f/20d) + (dl/16d) + 1);
                                double prob = Math.pow(Math.E, -1 *
                                        Math.pow(fir / sec, 2));

                                 */

                                double prob = 1;//Math.exp(-(i + 2)/Math.log(Math.max(1, d) + l.getDangerLevel() + l.getFloor_number()));
                                put(i, (float)Math.max(0, prob));
                            }
                            //put(i, (float)(1 / (Math.pow(4, (4/d) * i))));
                        }
                    };

                    double rarityMax = maxRarity;
                    double rarityMin = minRarity;

                    ArrayList<Creature> potentialCreatures;

                    do {
                        final double rarityMinFinal = rarityMin;
                        //potentialItems = pool.filterRarity(rarityMin, rarityMax);
                        potentialCreatures = new ArrayList<>(pool.stream()
                                .filter(c -> (c.getRarity() >= rarityMinFinal && c.getRarity() <= rarityMax))
                                .collect(Collectors.toList()));

                        if(potentialCreatures.size() == 0) {
                            if (rarityMin == 0)
                                break;
                            else
                                rarityMin--;
                        }
                    } while (potentialCreatures.size() == 0);

                    //If there are no potential items, don't place anything on this tile
                    if(potentialCreatures.size() == 0)
                        continue;

                    //Now, choose a random item from what is left.

                    int index = random.nextInt(potentialCreatures.size());
                    Creature cr = new Creature(potentialCreatures.get(index));
                    l.addAt(i, j, cr);

                }

            }
        }


    }

    public void generateLightThings(Level l, Light[] lights) {

        int maxRange = 0;
        for(Light light : lights)
            maxRange = Math.max(maxRange, light.getRange());

        for(int i = 0; i < l.getWidth(); i++) {
            for(int j = 0; j < l.getHeight(); j++) {
                //Don't place torches/braziers/whatever in walls

                ///Utility.printLevel(l, new ArrayList<>(Collections.singletonList(new Point(i, j))));
                //System.out.println("");

                if(l.getTileAt(i, j) == Tile.WALL) continue;

                //Utility.printLevel(l, new ArrayList<>(Collections.singletonList(new Point(i, j))));
                ///System.out.println("");

                if(l.getThingAt(i, j) != null) continue;

                //Only place a light if there's a wall to mount it on
                boolean adjWall = false;
                Tile[][] adjTiles = l.getAdjacentTiles(i, j);
                for(int it = 0; it < adjTiles.length; it++) {
                    for(int jt = 0; jt < adjTiles.length; jt++) {
                        if(Math.abs(it - 1) == Math.abs(jt - 1)) continue;
                        if(adjTiles[it][jt] == Tile.WALL) {
                            adjWall = true;
                            break;
                        }
                    }
                    if(adjWall) break;
                }

                if(!adjWall)
                    continue;

                Light light = lights[random.nextInt(lights.length)] instanceof LightRandom?
                        (LightRandom)lights[random.nextInt(lights.length)] : lights[random.nextInt(lights.length)];

                //Don't place if there is another close-by light
                ArrayList<Thing> adjThings = l.getAdjThings(i, j, (int) Math.ceil(5d/4 * maxRange), false, true);
                boolean found = false;
                for(Thing t : adjThings) {
                    if(t instanceof Light) {
                        found = true;
                        break;
                    }
                }

                if(found) {
                    //Utility.printLevel(l, new ArrayList<>(Collections.singletonList(new Point(i, j))));
                    //System.out.println("");
                    continue;
                }

                Light light1 = light instanceof LightRandom? new LightRandom((LightRandom)light) : new Light(light);
                new LightBehavior(light1);
                l.addAt(i, j, light1);

                //Utility.printLevel(l);
                //System.out.println("");

            }
        }

        //System.out.println();

    }

    public void clear() {
        this.tiles = new Tile[width][height];
        this.rooms = new ArrayList<>();
        this.creatures = new ArrayList<>();
        this.things = new ArrayList<>();
        this.items = new Item[width][height];
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

    //<editor-fold desc="Getters and Setters">
    public Tile[][] getTiles() {
        return tiles;
    }

    public void setTiles(Tile[][] tiles) {
        this.tiles = tiles;
    }

    public ArrayList<Room> getRooms() {
        return rooms;
    }

    public void setRooms(ArrayList<Room> rooms) {
        this.rooms = rooms;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
        tiles = new Tile[width][height];
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
        tiles = new Tile[width][height];
    }

    public void setDimensions(int width, int height) {
        this.width = width;
        this.height = height;
        tiles = new Tile[width][height];
    }

    public int getArea() {
        if(tiles == null) return 0;

        int count = 0;
        for(int i = 0; i < width; i++) {
            for(int j = 0; j < height; j++) {
                if(tiles[i][j] != null && tiles[i][j] != Tile.WALL && tiles[i][j] != Tile.BOUNDS)
                    count++;
            }
        }

        return count;
    }

    public Random getRandom() {
        return random;
    }

    public void setRandom(Random random) {
        this.random = random;
    }

    public WeightedRandom getWrandom() {
        return wrandom;
    }

    public void setWrandom(WeightedRandom wrandom) {
        this.wrandom = wrandom;
    }

    public ArrayList<Creature> getCreatures() {
        return creatures;
    }

    public void setCreatures(ArrayList<Creature> creatures) {
        this.creatures = creatures;
    }

    public Item[][] getItems() {
        return items;
    }

    public void setItems(Item[][] items) {
        this.items = items;
    }

    public ArrayList<Thing> getThings() {
        return things;
    }

    public void setThings(ArrayList<Thing> things) {
        this.things = things;
    }

    public HashMap<String, PreFab> getPrefabs() {
        return prefabs;
    }

    public static HashMap<String, ArrayList<Inventory>> getItemPools() {
        return itemPools;
    }

    public static HashMap<String, ArrayList<ArrayList<Creature>>> getCreaturePools() {
        return creaturePools;
    }

    public static Inventory getItemPool(String s, int lvl) {
        Inventory i;

        try {
            i = itemPools.getOrDefault(s, new ArrayList<>()).get(lvl);
            System.out.println();
        } catch (Exception e) {
            i = new Inventory();
        }

        return i;
    }

    public static Inventory getItemPool(String[] properties, int[] lvls) {
        Inventory i = new Inventory();
        for(String p : properties)
            for(int l : lvls)
                for(Item it : getItemPool(p, l))
                    i.add(it);
                //i.addAll(getItemPool(p, l));

        return i;
    }

    public static Inventory getItemPool(Level l, int dangerRange) {
        return getItemPool(l, true, dangerRange);
    }

    public static Inventory getItemPool(Level l, boolean withDungeon, int dangerRange) {
        ArrayList<String> properties = l.getProperties(withDungeon);
        String[] propetiesArray = new String[properties.size()];
        for(int i = 0; i < properties.size(); i++)
            propetiesArray[i] = properties.get(i);
        int[] lvls;
        if(dangerRange < 0)
            lvls = new int[]{l.getDangerLevel()};
        else {
            lvls = new int[dangerRange + 1];
            for (int i = 0; i <= dangerRange; i++) {
                lvls[i] = l.getDangerLevel() - i;
            }
        }

        return getItemPool(propetiesArray, lvls);

    }

    public static ArrayList<Creature> getCreaturePool(String s, int lvl) {
        ArrayList<Creature> c;

        try {
            c = creaturePools.getOrDefault(s, new ArrayList<>()).get(lvl);
        } catch (Exception e) {
            c = new ArrayList<>();
        }

        return c;
    }

    public static ArrayList<Creature> getCreaturePool(String[] properties, int[] lvls) {
        ArrayList<Creature> creatures = new ArrayList<>();
        for(String p : properties) {
            ArrayList<ArrayList<Creature>> current = creaturePools.getOrDefault(p, new ArrayList<>());
            if(current.isEmpty()) continue;
            for(int l : lvls) {
                if(l >= current.size()) continue;
                for(Creature c : current.get(Math.max(0, l)))
                    if(!creatures.contains(c)) creatures.add(c);
            }
        }



        return creatures;
    }

    public static ArrayList<Creature> getCreaturePool(Level l, int dangerRange) {
        return getCreaturePool(l, true, dangerRange);
    }

    public static ArrayList<Creature> getCreaturePool(Level l, boolean withDungeon, int dangerRange) {
        ArrayList<String> properties = l.getProperties(withDungeon);
        String[] propetiesArray = new String[properties.size()];
        for(int i = 0; i < properties.size(); i++)
            propetiesArray[i] = properties.get(i);
        int[] lvls;
        if(dangerRange < 0)
            lvls = new int[]{l.getDangerLevel()};
        else {
            lvls = new int[dangerRange + 1];
            for (int i = 0; i <= dangerRange; i++) {
                lvls[i] = l.getDangerLevel() - i;
            }
        }

        return getCreaturePool(propetiesArray, lvls);
    }

    //</editor-fold>

    private final HashMap<String, PreFab> prefabs = new HashMap<String, PreFab>() {
        {

        }
    };

    /**
     * A hashmap from the dungeon's/level's property to an arraylist of a pool of items.
     * Given the level's theme and difficulty, will retrieve the appropriate item pool
     */
    private static final HashMap<String, ArrayList<Inventory>> itemPools = new HashMap<String, ArrayList<Inventory>>() {
        {
            //Template
            {
                Inventory[] pools = new Inventory[] {
                        new Inventory() {
                            {
                                HashMap<String, Double> s = new HashMap<String, Double>() {
                                    {

                                    }
                                };

                                for(String item : s.keySet()) {

                                    if(CreatureItemFactory.newItem(item) instanceof Equipable) {
                                        if(CreatureItemFactory.newItem(item) instanceof Ammo) {
                                            Ammo i = new Ammo((Ammo)CreatureItemFactory.newItem(item));
                                            i.setRarity(s.get(item));
                                            add(i);
                                        }
                                        else if(CreatureItemFactory.newItem(item) instanceof Armor) {
                                            Armor i = new Armor((Armor)CreatureItemFactory.newItem(item));
                                            i.setRarity(s.get(item));
                                            add(i);
                                        }
                                        else if(CreatureItemFactory.newItem(item) instanceof Weapon) {
                                            if(CreatureItemFactory.newItem(item) instanceof RangedWeapon) {
                                                RangedWeapon i = new RangedWeapon((RangedWeapon)CreatureItemFactory.newItem(item));
                                                i.setRarity(s.get(item));
                                                add(i);
                                            }
                                            else {
                                                Weapon i = new Weapon((Weapon)CreatureItemFactory.newItem(item));
                                                i.setRarity(s.get(item));
                                                add(i);
                                            }
                                        }
                                        else {
                                            Equipable i = new Equipable(CreatureItemFactory.newItem(item));
                                            i.setRarity(s.get(item));
                                            add(i);
                                        }
                                    }
                                    else if(CreatureItemFactory.newItem(item) instanceof Potion) {
                                        Potion i = new Potion((Potion)CreatureItemFactory.newItem(item));
                                        i.setRarity(s.get(item));
                                        add(i);
                                    }
                                    else if(CreatureItemFactory.newItem(item) instanceof Food) {
                                        Food i = new Food((Food)CreatureItemFactory.newItem(item));
                                        i.setRarity(s.get(item));
                                        add(i);
                                    }
                                    else {
                                        Item i = new Item(CreatureItemFactory.newItem(item));
                                        i.setRarity(s.get(item));
                                        add(i);
                                    }


                                }
                            }
                        }
                };

                put("Template Pool", new ArrayList<Inventory>() {
                    {
                        this.addAll(Arrays.asList(pools));
                    }
                });
            }

            //Cave
            {
                Inventory[] pools = new Inventory[] {
                        new Inventory() {
                            {
                                HashMap<String, Double> s = new HashMap<String, Double>() {
                                    {
                                        put("Dagger", Item.COMMON);
                                        put("Rock", Item.SUPER_COMMON);
                                        put("Sling", Item.COMMON);
                                        put("Cloth Armor", Item.AVERAGE);
                                        put("Poison Potion", Item.AVERAGE + .8);
                                    }
                                };

                                for(String item : s.keySet()) {
                                    Item i;

                                    if(CreatureItemFactory.newItem(item) instanceof Equipable) {
                                        if(CreatureItemFactory.newItem(item) instanceof Ammo) {
                                            i = new Ammo((Ammo)CreatureItemFactory.newItem(item));
                                        }
                                        else if(CreatureItemFactory.newItem(item) instanceof Armor) {
                                            i = new Armor((Armor)CreatureItemFactory.newItem(item));
                                        }
                                        else if(CreatureItemFactory.newItem(item) instanceof Weapon) {
                                            if(CreatureItemFactory.newItem(item) instanceof RangedWeapon) {
                                                i = new RangedWeapon((RangedWeapon)CreatureItemFactory.newItem(item));
                                            }
                                            else {
                                                i = new Weapon((Weapon)CreatureItemFactory.newItem(item));
                                            }
                                        }
                                        else {
                                            i = new Equipable(CreatureItemFactory.newItem(item));
                                        }
                                    }
                                    else if(CreatureItemFactory.newItem(item) instanceof Potion) {
                                        i = new Potion((Potion)CreatureItemFactory.newItem(item));
                                    }
                                    else if(CreatureItemFactory.newItem(item) instanceof Food) {
                                        i = new Food((Food)CreatureItemFactory.newItem(item));
                                    }
                                    else {
                                        i = new Item(CreatureItemFactory.newItem(item));
                                    }

                                    i.setRarity(s.get(item));
                                    add(i);
                                }
                            }
                        },
                        new Inventory() {
                            {
                                HashMap<String, Double> s = new HashMap<String, Double>() {
                                    {
                                        put("Longsword", Item.AVERAGE + .8);
                                        put("Shortbow", Item.AVERAGE + .8);
                                        put("Arrow", Item.AVERAGE);
                                        put("Leather Armor", Item.RARE);
                                        put("Regeneration Potion", Item.RARE);
                                        put("Poison Potion", Item.RARE);
                                        put("Heroism Potion", Item.RARE + .5);
                                        put("Health Potion", Item.RARE);

                                    }
                                };

                                for(String item : s.keySet()) {
                                    Item i;

                                    if(CreatureItemFactory.newItem(item) instanceof Equipable) {
                                        if(CreatureItemFactory.newItem(item) instanceof Ammo) {
                                            i = new Ammo((Ammo)CreatureItemFactory.newItem(item));
                                        }
                                        else if(CreatureItemFactory.newItem(item) instanceof Armor) {
                                            i = new Armor((Armor)CreatureItemFactory.newItem(item));
                                        }
                                        else if(CreatureItemFactory.newItem(item) instanceof Weapon) {
                                            if(CreatureItemFactory.newItem(item) instanceof RangedWeapon) {
                                                i = new RangedWeapon((RangedWeapon)CreatureItemFactory.newItem(item));
                                            }
                                            else {
                                                i = new Weapon((Weapon)CreatureItemFactory.newItem(item));
                                            }
                                        }
                                        else {
                                            i = new Equipable(CreatureItemFactory.newItem(item));
                                        }
                                    }
                                    else if(CreatureItemFactory.newItem(item) instanceof Potion) {
                                        i = new Potion((Potion)CreatureItemFactory.newItem(item));
                                    }
                                    else if(CreatureItemFactory.newItem(item) instanceof Food) {
                                        i = new Food((Food)CreatureItemFactory.newItem(item));
                                    }
                                    else {
                                        i = new Item(CreatureItemFactory.newItem(item));
                                    }

                                    i.setRarity(s.get(item));
                                    add(i);
                                }
                            }
                        }
                };

                put("Cave", new ArrayList<Inventory>() {
                    {
                        this.addAll(Arrays.asList(pools));
                    }
                });
            }

            //Dungeon
            {
                Inventory[] pools = new Inventory[] {
                        new Inventory() {
                            {
                                HashMap<String, Double> s = new HashMap<String, Double>() {
                                    {
                                        put("Dagger", Item.COMMON);
                                        put("Sling", Item.COMMON);
                                        put("Cloth Armor", Item.AVERAGE);
                                        put("Poison Potion", Item.AVERAGE + .8);
                                    }
                                };

                                for(String item : s.keySet()) {
                                    Item i;

                                    if(CreatureItemFactory.newItem(item) instanceof Equipable) {
                                        if(CreatureItemFactory.newItem(item) instanceof Ammo) {
                                            i = new Ammo((Ammo)CreatureItemFactory.newItem(item));
                                        }
                                        else if(CreatureItemFactory.newItem(item) instanceof Armor) {
                                            i = new Armor((Armor)CreatureItemFactory.newItem(item));
                                        }
                                        else if(CreatureItemFactory.newItem(item) instanceof Weapon) {
                                            if(CreatureItemFactory.newItem(item) instanceof RangedWeapon) {
                                                i = new RangedWeapon((RangedWeapon)CreatureItemFactory.newItem(item));
                                            }
                                            else {
                                                i = new Weapon((Weapon)CreatureItemFactory.newItem(item));
                                            }
                                        }
                                        else {
                                            i = new Equipable(CreatureItemFactory.newItem(item));
                                        }
                                    }
                                    else if(CreatureItemFactory.newItem(item) instanceof Potion) {
                                        i = new Potion((Potion)CreatureItemFactory.newItem(item));
                                    }
                                    else if(CreatureItemFactory.newItem(item) instanceof Food) {
                                        i = new Food((Food)CreatureItemFactory.newItem(item));
                                    }
                                    else {
                                        i = new Item(CreatureItemFactory.newItem(item));
                                    }

                                    i.setRarity(s.get(item));
                                    add(i);
                                }
                            }
                        },
                        new Inventory() {
                            {
                                HashMap<String, Double> s = new HashMap<String, Double>() {
                                    {
                                        put("Longsword", Item.AVERAGE + .8);
                                        put("Shortbow", Item.AVERAGE + .8);
                                        put("Arrow", Item.AVERAGE);
                                        put("Leather Armor", Item.RARE);
                                        put("Regeneration Potion", Item.RARE);
                                        put("Poison Potion", Item.RARE);
                                        put("Heroism Potion", Item.RARE + .5);
                                        put("Health Potion", Item.RARE);

                                    }
                                };

                                for(String item : s.keySet()) {
                                    Item i;

                                    if(CreatureItemFactory.newItem(item) instanceof Equipable) {
                                        if(CreatureItemFactory.newItem(item) instanceof Ammo) {
                                            i = new Ammo((Ammo)CreatureItemFactory.newItem(item));
                                        }
                                        else if(CreatureItemFactory.newItem(item) instanceof Armor) {
                                            i = new Armor((Armor)CreatureItemFactory.newItem(item));
                                        }
                                        else if(CreatureItemFactory.newItem(item) instanceof Weapon) {
                                            if(CreatureItemFactory.newItem(item) instanceof RangedWeapon) {
                                                i = new RangedWeapon((RangedWeapon)CreatureItemFactory.newItem(item));
                                            }
                                            else {
                                                i = new Weapon((Weapon)CreatureItemFactory.newItem(item));
                                            }
                                        }
                                        else {
                                            i = new Equipable(CreatureItemFactory.newItem(item));
                                        }
                                    }
                                    else if(CreatureItemFactory.newItem(item) instanceof Potion) {
                                        i = new Potion((Potion)CreatureItemFactory.newItem(item));
                                    }
                                    else if(CreatureItemFactory.newItem(item) instanceof Food) {
                                        i = new Food((Food)CreatureItemFactory.newItem(item));
                                    }
                                    else {
                                        i = new Item(CreatureItemFactory.newItem(item));
                                    }

                                    i.setRarity(s.get(item));
                                    add(i);
                                }
                            }
                        }
                };

                put("Dungeon", new ArrayList<Inventory>() {
                    {
                        this.addAll(Arrays.asList(pools));
                    }
                });
            }

        }
    };

    /**
     * A hashmap from the dungeon's/level's property to an array list of a pool of creatures.
     * Given the level's theme and difficulty, will retrieve the appropriate creature pool
     */
    private static final HashMap<String, ArrayList<ArrayList<Creature>>> creaturePools = new HashMap<String, ArrayList<ArrayList<Creature>>>() {
        {
            //Template
            {
                ArrayList<ArrayList<Creature>> pools = new ArrayList<ArrayList<Creature>>() {
                    {
                        add(new ArrayList<Creature>() {
                            {
                                HashMap<String, Double> s = new HashMap<String, Double>() {
                                    {

                                    }
                                };

                                for(String c : s.keySet()) {
                                    Creature cr = new Creature(CreatureItemFactory.newCreature(c));
                                    cr.setAi(CreatureItemFactory.newAi(c));
                                    cr.setActor(CreatureItemFactory.newActor(c));
                                    cr.setRarity(s.get(c));
                                    add(cr);
                                }
                            }
                        });
                    }
                };
                put("Template pool", new ArrayList<ArrayList<Creature>>() {
                    {
                        this.addAll(pools);
                    }
                });
            }

            //Cave
            {
                ArrayList<ArrayList<Creature>> pools = new ArrayList<ArrayList<Creature>>() {
                    {
                        add(new ArrayList<Creature>() {
                            {
                                HashMap<String, Double> s = new HashMap<String, Double>() {
                                    {
                                        put("Fungus", Creature.COMMON);
                                        put("Bat", Creature.COMMON);
                                    }
                                };

                                for(String c : s.keySet()) {
                                    Creature cr = new Creature(CreatureItemFactory.newCreature(c));
                                    cr.setAi(CreatureItemFactory.newAi(c));
                                    cr.setActor(CreatureItemFactory.newActor(c));
                                    cr.setRarity(s.get(c));
                                    add(cr);
                                }
                            }
                        });

                        add(new ArrayList<Creature>() {
                            {
                                HashMap<String, Double> s = new HashMap<String, Double>() {
                                    {
                                        put("Fungus", Creature.COMMON);
                                        put("Bat", Creature.COMMON);
                                        put("Goblin", Creature.AVERAGE);
                                    }
                                };

                                for(String c : s.keySet()) {
                                    Creature cr = new Creature(CreatureItemFactory.newCreature(c));
                                    cr.setAi(CreatureItemFactory.newAi(c));
                                    cr.setActor(CreatureItemFactory.newActor(c));
                                    cr.setRarity(s.get(c));
                                    add(cr);
                                }
                            }
                        });
                    }
                };
                put("Cave", new ArrayList<ArrayList<Creature>>() {
                    {
                        this.addAll(pools);
                    }
                });
            }

            //Dungeon
            {
                ArrayList<ArrayList<Creature>> pools = new ArrayList<ArrayList<Creature>>() {
                    {
                        add(new ArrayList<Creature>() {
                            {
                                HashMap<String, Double> s = new HashMap<String, Double>() {
                                    {
                                        put("Looter", Creature.COMMON);
                                    }
                                };

                                for(String c : s.keySet()) {
                                    Creature cr = new Creature(CreatureItemFactory.newCreature(c));
                                    cr.setAi(CreatureItemFactory.newAi(c));
                                    cr.setActor(CreatureItemFactory.newActor(c));
                                    cr.setRarity(s.get(c));
                                    add(cr);
                                }
                            }
                        });
                    }
                };
                put("Dungeon", new ArrayList<ArrayList<Creature>>() {
                    {
                        this.addAll(pools);
                    }
                });
            }
        }
    };
}
