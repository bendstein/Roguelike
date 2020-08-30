package world;

import actors.creatures.CreatureActor;
import actors.world.LevelActor;
import com.badlogic.gdx.Gdx;
import creatureitem.Creature;
import creatureitem.Entity;
import creatureitem.Player;
import creatureitem.item.Inventory;
import creatureitem.item.Item;
import game.Main;
import utility.DynamicArray;
import utility.Utility;
import world.geometry.Point;
import world.geometry.floatPoint;
import world.thing.*;
import world.room.Room;

import java.util.*;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.PriorityBlockingQueue;

public class Level extends LevelInterface {

    //<editor-fold desc="Instance Variables">
    /**
     * List of all creatures on the map
     */
    private CopyOnWriteArrayList<Creature> creatures;

    /**
     * Reference to player
     */
    private Player player;

    /**
     * Tiles seen by player
     */
    private boolean[][] seen;

    private DynamicArray<Light>[][] emitting;

    private boolean[][] staticLight;

    /**
     * A* Costs
     */
    private int[][] costs;

    private int[][][] tile_orientations;

    /**
     * Reference to this level's actor
     */
    private LevelActor actor;

    /**
     * Queue of creatures to add to the level
     */
    private ArrayList<Creature> creatureQueue;

    /**
     * 2d array of items in the level
     */
    private Item[][] items;

    private Inventory[][] inventories;

    /**
     * List of all rooms in the level
     */
    private ArrayList<Room> rooms;

    /**
     * List of all things on the level
     */
    private ArrayList<Thing> things;

    /**
     * Name of the level
     */
    private String name;

    /**
     * prng
     */
    private Random random;

    /**
     * Reference to the dungeon this level is in
     */
    private Dungeon dungeon;

    /**
     * A rating of how dangerous the level is.
     * By default, will be the danger level of the dungeon, but can be set to be
     * higher or lower.
     */
    private int dangerLevel;

    private int floor_number;

    /**
     * Properties that the level has, separate from dungeon properties
     */
    private ArrayList<String> properties;

    private boolean requestLightingUpdate;

    private Entity turn;

    private PriorityBlockingQueue<Entity> turnQueue;

    private CopyOnWriteArrayList<Entity> acting;

    boolean queue_paused;

    private static final int ENERGY_PER_TURN = 100;

    private static final long ACT_RATE = 100L;

    //</editor-fold>

    public Level(Tile[][] tiles, Random random, String ... properties) {
        super(tiles);
        this.random = random;
        creatures = new CopyOnWriteArrayList<>();
        creatureQueue = new ArrayList<>();
        items = new Item[getWidth()][getHeight()];
        inventories = new Inventory[getWidth()][getHeight()];
        seen = new boolean[getWidth()][getHeight()];
        tile_orientations = new int[getWidth()][getHeight()][];
        emitting = new DynamicArray[getWidth()][getHeight()];
        staticLight = new boolean[getWidth()][getHeight()];
        rooms = new ArrayList<>();
        floor_number = 0;
        requestLightingUpdate = false;
        costs = calculateCosts();
        turn = new Entity(-1, -1, true);
        turn.setCurrent_energy(ENERGY_PER_TURN);
        acting = new CopyOnWriteArrayList<>();

        turnQueue = new PriorityBlockingQueue<>(1, (e0, e1) -> {

            /*
             * If both entities have the same priority:
             *  If either entity is the turn marker, prioritize it.
             *  It either entity is the player, prioritize it.
             *  Otherwise, prioritize based on their energy factor.
             */
            if(e0.getCurrent_energy() == e1.getCurrent_energy()) {
                if(turn.equals(e0)) return 1;
                else if(turn.equals(e1)) return -1;
                else if(player == null)
                    return Float.compare(e0.getEnergy_factor(), e1.getEnergy_factor());
                else if(player.equals(e0)) return 1;
                else if(player.equals(e1)) return -1;
                else
                    return Float.compare(e0.getEnergy_factor(), e1.getEnergy_factor());
            }
            return Integer.compare(e0.getCurrent_energy(), e1.getCurrent_energy());
        });
        turnQueue.add(turn);
        queue_paused = true;

        for(int i = 0; i < getWidth(); i++) {
            for(int j = 0; j < getHeight(); j++) {
                emitting[i][j] = new DynamicArray<>(new Light[]{null});
                inventories[i][j] = new Inventory();
                tile_orientations[i][j] = new int[]{tiles[i][j].getNeutral()};
            }
        }

        this.properties = new ArrayList<>();
        for(String p : properties) addProperty(p);
    }

    /**
     * @param x X coordinate
     * @param y Y coordinate
     * @return The creature at (x, y), or null if there is none
     */
    public Creature getCreatureAt(int x, int y) {
        for(Creature c : creatures) if(c.getX() == x && c.getY() == y) return c;
        return null;
    }

    /**
     * @param x X coordinate
     * @param y Y coordinate
     * @return The item at (x, y)
     */
    public Item getItemAt(int x, int y) {
        if(inventories[x][y].isEmpty()) return null;
        return inventories[x][y].top();
        //return items[x][y];
    }

    public Inventory getInventoryAt(int x, int y) {
        return inventories[x][y];
    }

    public Thing getThingAt(int x, int y) {
        for(Thing thing : things) {
            if(thing.getX() == x && thing.getY() == y)
                return thing;
        }

        return null;
    }

    /**
     * Add creature c at (x, y)
     * @param x X coordinate
     * @param y Y coordinate
     * @param c The Creature
     * @return true if the creature was successfully added at (x, y)
     */
    public boolean addAt(int x, int y, Creature c) {
        if(Creature.canEnter(x, y, this)) {
            c.setCoordinates(x, y);
            if(c.getActor() != null) {
                ((CreatureActor)c.getActor()).setCurrentLocation(new floatPoint(x * Main.getTileWidth(), y * Main.getTileHeight()));
            }
            c.setLevel(this);

            //Calculate creature level now that the ai has been created
            c.setExp(c.getExp(), false);
            creatureQueue.add(c);

            return true;
        }

        return false;
    }

    public boolean addAtIgnoreQueue(int x, int y, Creature c) {
        if(Creature.canEnter(x, y, this)) {
            c.setCoordinates(x, y);
            if(c.getActor() != null) {
                ((CreatureActor)c.getActor()).setCurrentLocation(new floatPoint(x * Main.getTileWidth(), y * Main.getTileHeight()));
            }
            c.setLevel(this);

            //Calculate creature level now that the ai has been created
            c.setExp(c.getExp(), false);
            creatures.add(c);
            c.setLevel(this);
            c.updateVision();

            if(c.isCan_act()) {
                c.setEnergy_used(0);
                c.setCurrent_energy(0);
                if(!turnQueue.contains(c)) turnQueue.add(c);
            }

            return true;
        }

        return false;
    }

    /**
     * Add creature c at a random unoccupied location
     * @param c The creature
     */
    public void addAtEmptyLocation(Creature c) {

        boolean placed = false;
        int x = 0, y = 0;

        while(!placed) {
            x = random.nextInt(getWidth() - 1);
            y = random.nextInt(getHeight() - 1);
            placed = addAt(x, y, c);
        }
    }

    /**
     * Add item i at a random unoccupied location
     * @param i The item
     */
    public void addAtEmptyLocation(Item i) {

        boolean placed = false;
        int x = 0, y = 0;

        while(!placed) {
            x = random.nextInt(getWidth() - 1);
            y = random.nextInt(getHeight() - 1);
            placed = addAt(x, y, i);
        }
    }

    public void addAtEmptyLocation(Inventory i) {

        boolean placed = false;
        int x = 0, y = 0;

        while(!placed) {
            x = random.nextInt(getWidth() - 1);
            y = random.nextInt(getHeight() - 1);
            placed = addAt(x, y, i);
        }
    }

    public void addAtEmptyLocation(Thing t) {

        boolean placed = false;
        int x = 0, y = 0;

        while(!placed) {
            x = random.nextInt(getWidth() - 1);
            y = random.nextInt(getHeight() - 1);
            placed = addAt(x, y, t);
        }

        updateStaticLit();
        calculateOrientations();
    }

    /**
     * Add item i at (x, y)
     * @param x X coordinate
     * @param y Y coordinate
     * @param i The item
     * @return true if the item was successfully added at (x, y)
     */
    public boolean addAt(int x, int y, Item i) {
        if(!tiles[x][y].isGround()) return false;
        inventories[x][y].add(i);
        return true;
        /*
        if(!tiles[x][y].isGround() || items[x][y] != null)
            return false;

        items[x][y] = i;
        return true;

         */
    }

    public boolean addAt(int x, int y, Inventory i) {
        if(!tiles[x][y].isGround()) return false;
        inventories[x][y].addAll(i);
        return true;
    }

    public boolean addAt(int x, int y, Thing t) {
        if(!tiles[x][y].isGround())
            return false;
        if(!t.isOpen() && (items[x][y] != null || getCreatureAt(x, y) != null))
            return false;

        things.add(t);
        costs = calculateCosts();
        t.setLocation(x, y);

        if(t.isCan_act()) {
            t.setEnergy_used(0);
            t.setCurrent_energy(0);
            if(!turnQueue.contains(t)) turnQueue.add(t);
        }

        if(t instanceof Light) {
            requestLightingUpdate = true;
            /*
            Light l = (Light)t;
            l.determinePosition(this);
            for(int i = -l.getRange(); i <= l.getRange(); i++) {
                for(int j = -l.getRange(); j <= l.getRange(); j++) {
                    if(!isOutOfBounds(l.getY() + i, l.getY() + j) && l.canLight(l.getX() + i, l.getY() + j, this) != 0) {
                        setStaticLit(l.getX() + i, l.getY() + j, l);
                    }
                }
            }

             */
        }


        return true;
    }

    /**
     * @param x X coordinate
     * @param y Y Coordinate
     * @return true if there is a creature queued to be placed at (x, y)
     */
    public boolean queuedCreatureAt(int x, int y) {
        for(Creature c : creatureQueue)
            if(c.getX() == x && c.getY() == y) return true;
        return false;
    }

    /**
     * Add all creatures in the queue to the level
     * @return The creature queue
     */
    public ArrayList<Creature> addCreatureQueue() {
        for(Creature c : creatureQueue) {
            creatures.add(c);

            if(c.isCan_act()) {
                c.setEnergy_used(0);
                c.setCurrent_energy(0);
                if(!turnQueue.contains(c)) turnQueue.add(c);
            }

            c.setLevel(this);
            c.updateVision();
        }

        costs = calculateCosts();
        return creatureQueue;
    }

    /**
     * Empty the creature queue
     */
    public void clearCreatureQueue() {
        creatureQueue.clear();
    }

    /**
     * Remove a creature from the level
     * @param creature The creature
     */
    public void remove(Creature creature) {
        creatures.remove(creature);
        if(creature != player) creature.getActor().remove();

        turnQueue.remove(creature);
        //creature.setAttack(0);
    }

    public Creature removeCreature(int x, int y) {
        Creature c = getCreatureAt(x, y);
        if(c == null) return null;
        remove(c);

        turnQueue.remove(c);
        return c;
        //creature.setAttack(0);
    }

    /**
     * Remove an item from the level
     * @param item The item
     */
    public void remove(Item item) {
        for(int i = 0; i < inventories.length; i++) {
            for(int j = 0; j < inventories[0].length; i++) {
                if(inventories[i][j].contains(item) != -1) {
                    inventories[i][j].remove(item);
                    return;
                }
            }
        }

    }

    /**
     * Remove an item at (x, y) from the level
     * @param x X coord
     * @param y Y coord
     * @return The item that was at (x, y)
     */
    public Item removeItem(int x, int y) {
        Item i = inventories[x][y].top();
        inventories[x][y].remove(i);

        return i;
    }

    public Inventory removeInventory(int x, int y) {
        Inventory i = inventories[x][y];
        inventories[x][y] = new Inventory();
        return i;
    }

    public void remove(Thing thing) {
        things.remove(thing);

        if(thing instanceof Light) {
            ((Light) thing).setActive(false);
            requestLightingUpdate = true;
        }

        turnQueue.remove(thing);

        calculateOrientations();
    }

    public Thing removeThing(int x, int y) {
        Thing t = getThingAt(x, y);
        if(t == null) return null;
        remove(t);

        if(t instanceof Light) {
            ((Light) t).setActive(false);
            requestLightingUpdate = true;
        }

        turnQueue.remove(t);

        calculateOrientations();

        return t;
        //creature.setAttack(0);
    }

    /**
     * Update everything that must be updated after every action
     */
    public void update() {

        ArrayList<Creature> toUpdate = new ArrayList<>(creatures);
        for(Creature c : toUpdate)
            if(creatures.contains(c)) {
                if(c.isRequestVisionUpdate())
                    c.updateVision();
            }

        /*
         * Update minimap
         */
        dungeon.getGame().getPlayScreen().getUi().setRequestMinimapUpdate(true);
    }

    /**
     * Update everything that must be updated after every turn
     */
    public void nextTurn() {

        incrementTurnNumber();
        acting.clear();


        /*
         * Update creature statuses, health, hunger, etc
         */
        ArrayList<Creature> toUpdate = new ArrayList<>(creatures);
        for(Creature c : toUpdate)
            if(creatures.contains(c)) {
                c.update();
            }

        /*
         * Update all entities' priorities
         */
        ArrayList<Entity> updated = new ArrayList<>();

        while(!turnQueue.isEmpty())
            if(!updated.contains(turnQueue.peek())) updated.add(turnQueue.poll());

        for(Entity entity1 : updated) {
            entity1.setCurrent_energy(Math.max(0, entity1.getCurrent_energy() - ENERGY_PER_TURN));
            if(!turnQueue.contains(entity1)) turnQueue.add(entity1);
        }

        advance(turn);
    }

    /**
     * Advance the turn queue after an entity acts
     * @param entity The entity that just acted
     */
    public void advance(Entity entity) {
        /*
         * If the entity is already in the queue for whatever reason, remove it.
         */
        acting.remove(entity);
        turnQueue.remove(entity);

        if(acting.isEmpty()) queue_paused = true;

        /*
         * Commit the energy used and insert the entity back into the queue.
         */
        entity.commitEnergy();
        turnQueue.add(entity);

        /*
         * Update everything that must be updated after every action.
         */
        update();

        if(queue_paused && turnQueue.contains(player)) {
            try {
                nextAct();
            } catch (Exception e) {
                queue_paused = true;
                System.err.println(e.getMessage());
            }
        }

    }

    public void nextAct() throws NullPointerException {

        if(!queue_paused) return;
        queue_paused = false;

        if(turnQueue.peek().equals(turn)) {
            acting.add(turnQueue.poll());
        }
        else {
            /*
             * All creatures that act before the end of the turn will act
             */
            while(!turnQueue.isEmpty()) {
                Entity e = turnQueue.peek();
                if(e.equals(turn)) break;
                acting.add(turnQueue.poll());
            }
        }


        for(Entity entity : acting) {
            Level l = this;

            Gdx.app.postRunnable(() -> entity.act(l));

        }

    }

    /**
     * Place the room r on the map, and add to rooms
     * @param r The room
     */
    public void placeRoom(Room r) {
        rooms.add(r);

        for(int i = 0; i < r.getWidth(); i++) {
            for(int j = 0; j < r.getHeight(); j++) {
                if(r.getTileAt(i, j) != null)
                    try {
                        tiles[r.xToParentX(i)][r.yToParentY(j)] = r.getTileAt(i, j);
                    } catch (Exception e) {
                        System.err.println(e.getMessage());
                    }
                else if(tiles[r.xToParentX(i)][r.yToParentY(j)] == null)
                    tiles[r.xToParentX(i)][r.yToParentY(j)] = Tile.BOUNDS;

            }
        }
    }

    /**
     * Place the given staircase s at (x, y)
     * @param s A staircase
     */
    public void addStairs(world.thing.Stairs s) {
        addAt(s.getX(), s.getY(), s);
        s.setLevel(this);
        calculateOrientations();
    }

    public boolean isPassable(int x, int y) {
        if(!tiles[x][y].isPassable()) return false;
        if(getThingAt(x, y) != null && !getThingAt(x, y).isOpen()) return false;

        return true;
    }

    public ArrayList<Thing> getAdjThings(int x, int y, boolean ignoreCenter) {
        return getAdjThings(x, y, 1, ignoreCenter);
    }

    public ArrayList<Thing> getAdjThings(int x, int y, int r, boolean ignoreCenter) {
        return getAdjThings(x, y, r, ignoreCenter, false);
    }

    public ArrayList<Thing> getAdjThings(int x, int y, int r, boolean ignoreCenter, boolean sameRoom) {
        Point p = new Point(x, y);
        ArrayList<Thing> thingList = new ArrayList<>();

        Room rp = null;

        if(sameRoom) rp = getRoom(p);

        for(Thing t : things) {
            if(ignoreCenter && t.getX() == x && t.getY() == y) continue;

            if(p.chebychevDistanceFrom(t.getLocation()) <= r) {
                //If only checking things in same room, only add if thing is in same room as p
                if(sameRoom) {
                    Room rt = getRoom(t.getLocation());
                    if(rt == null && rp == null)
                        thingList.add(t);
                    else if(rt != null && rp != null) {
                        if(rt.equals(rp))
                            thingList.add(t);
                    }
                }

                else
                    thingList.add(t);

            }

        }

        return thingList;
    }

    public ArrayList<Creature> getAdjCreatures(int x, int y, int r, int heuristic) {
        Point p = new Point(x, y);
        ArrayList<Creature> creatureList = new ArrayList<>();

        for(Creature c : creatures) {

            double distance;

            switch (heuristic) {
                case Point.DISTANCE_CHEBYCHEV: {
                    distance = p.chebychevDistanceFrom(c.getLocation());
                    break;
                }
                case Point.DISTANCE_EUCLIDEAN: {
                    distance = p.euclideanDistanceFrom(c.getLocation());
                    break;
                }
                case Point.DISTANCE_MANHATTAN: {
                    distance = p.manhattanDistanceFrom(c.getLocation());
                    break;
                }
                default: {
                    distance = Double.MAX_VALUE;
                }
            }

            if(distance <= r)
                creatureList.add(c);

        }

        return creatureList;
    }

    public ArrayList<Creature> getAdjCreatures(Creature cr, int r, int heuristic) {
        Point p = cr.getLocation();
        ArrayList<Creature> creatureList = new ArrayList<>();

        for(Creature c : creatures) {

            if(cr.equals(c)) continue;

            double distance;

            switch (heuristic) {
                case Point.DISTANCE_CHEBYCHEV: {
                    distance = p.chebychevDistanceFrom(c.getLocation());
                    break;
                }
                case Point.DISTANCE_EUCLIDEAN: {
                    distance = p.euclideanDistanceFrom(c.getLocation());
                    break;
                }
                case Point.DISTANCE_MANHATTAN: {
                    distance = p.manhattanDistanceFrom(c.getLocation());
                    break;
                }
                default: {
                    distance = Double.MAX_VALUE;
                }
            }

            if(distance <= r)
                creatureList.add(c);

        }

        return creatureList;
    }

    public Room getRoom(Point p) {
        return getRoom(p.getX(), p.getY());
    }

    public Room getRoom(int x, int y) {
        if(rooms == null || rooms.isEmpty()) return null;
        for(Room r : rooms) {
            int rx = r.parentXtoX(x), ry = r.parentYtoY(y);
            if(rx < 0 || ry < 0 || rx >= r.getWidth() || ry >= r.getHeight()) {
            }
            else if(r.getTileAt(rx, ry) == Tile.FLOOR)
                return r;
        }

        return null;
    }

    //<editor-fold desc="Getters and Setters">

    public boolean isQueue_paused() {
        return queue_paused;
    }

    public void setQueue_paused(boolean queue_paused) {
        this.queue_paused = queue_paused;
    }

    public static int getEnergyPerTurn() {
        return ENERGY_PER_TURN;
    }

    public void setTurn(Entity turn) {
        this.turn = turn;
    }

    public Entity getTurn() {
        return turn;
    }

    public PriorityBlockingQueue<Entity> getTurnQueue() {
        return turnQueue;
    }

    public void setTurnQueue(PriorityBlockingQueue<Entity> turnQueue) {
        this.turnQueue = turnQueue;
    }

    public void addToTurnQueue(Entity e) {
        turnQueue.add(e);
    }

    public Inventory[][] getInventories() {
        return inventories;
    }

    public void setInventories(Inventory[][] inventories) {
        this.inventories = inventories;
    }

    public boolean isRequestLightingUpdate() {
        return requestLightingUpdate;
    }

    public void setRequestLightingUpdate(boolean requestLightingUpdate) {
        this.requestLightingUpdate = requestLightingUpdate;
    }

    public CopyOnWriteArrayList<Creature> getCreatures() {
        return creatures;
    }

    public void setCreatures(CopyOnWriteArrayList<Creature> creatures) {
        this.creatures = creatures;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public LevelActor getActor() {
        return actor;
    }

    public void setActor(LevelActor actor) {
        this.actor = actor;
        if(actor != null) actor.setLevel(this);
    }

    public ArrayList<Creature> getCreatureQueue() {
        return creatureQueue;
    }

    public void setCreatureQueue(ArrayList<Creature> creatureQueue) {
        this.creatureQueue = creatureQueue;
    }

    public Item[][] getItems() {
        return items;
    }

    public void setItems(Item[][] items) {
        this.items = items;
    }

    public ArrayList<Room> getRooms() {
        return rooms;
    }

    public Random getRandom() {
        return random;
    }

    public void setRandom(Random random) {
        this.random = random;
    }

    public void setRooms(ArrayList<Room> rooms) {
        this.rooms = rooms;
    }

    public ArrayList<Thing> getThings() {
        return things;
    }

    public void setThings(ArrayList<Thing> things) {
        this.things = things;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Dungeon getDungeon() {
        return dungeon;
    }

    public void setDungeon(Dungeon dungeon) {
        this.dungeon = dungeon;
    }

    public boolean[][] getSeen() {
        return seen;
    }

    public void setSeen(boolean[][] seen) {
        this.seen = seen;
    }

    public void setSeen(int x, int y) {
        seen[x][y] = true;
    }

    public void setSeenAll() {
        for (boolean[] booleans : seen) Arrays.fill(booleans, true);
    }

    public boolean getSeen(int x, int y) {
        if(isOutOfBounds(x, y)) return false;
        return seen[x][y];
    }

    public boolean[][] getStaticLight() {
        return staticLight;
    }

    public boolean getStaticLit(int x, int y) {
        if(isOutOfBounds(x, y)) return false;
        return staticLight[x][y];
    }

    public void setStaticLight(boolean[][] staticLight) {
        this.staticLight = staticLight;
    }

    public void setStaticLit(int x, int y, Light l) {
        staticLight[x][y] = true;
        if(!emitting[x][y].contains(l))
            emitting[x][y].add(l);
    }

    public DynamicArray<Light>[][] getEmitting() {
        return emitting;
    }

    public DynamicArray<Light> getEmitting(int x, int y) {
        return emitting[x][y];
    }

    public void setEmitting(DynamicArray<Light>[][] emitting) {
        this.emitting = emitting;
    }

    public int[][] getCosts() {
        return costs;
    }

    public int[][] calculateCosts() {
        return calculateCosts(1, 1);
    }

    public int[][] calculateCosts(int doorCost, int creatureCost) {
        int[][] costs = Utility.toCostArray(tiles);

        if(things != null && !things.isEmpty()) {
            for(Thing t : things) {
                if(!t.isOpen()) {
                    if(costs[t.getX()][t.getY()] >= 0) {
                        if(t.getBehavior() instanceof DoorBehavior)
                            costs[t.getX()][t.getY()] += doorCost;
                        else
                            costs[t.getX()][t.getY()] = -1;
                    }

                }
            }
        }


        if(creatures != null && !creatures.isEmpty()) {
            for(Creature c : creatures) {
                if(costs[c.getX()][c.getY()] >= 0)
                    costs[c.getX()][c.getY()] += creatureCost;
            }
        }

        return costs;
    }

    @Override
    public void setTiles(Tile[][] tiles) {
        this.tiles = tiles;
        costs = calculateCosts();
    }

    /**
     * Set the tile at (x, y) in the level's coordinate system to be tile t
     * @param x X coordinate
     * @param y Y coordinate
     */
    @Override
    public void setTileAt(int x, int y, Tile t) {
        tiles[x][y] = t;
        costs = calculateCosts();
        calculateOrientations();
    }

    /**
     * If the tile at (x, y) in the level's coordinate system is diggable, dig it out.
     * @param x X coordinate
     * @param y Y coordinate
     */
    @Override
    public void dig(int x, int y) {
        if(tiles[x][y].isDiggable())
            setTileAt(x, y, Tile.FLOOR);
    }

    public void setCosts(int[][] costs) {
        this.costs = costs;
    }

    public int getTurnNumber() {
        return dungeon.getGame().getTurn();
    }

    public void setTurnNumber(int turnNumber) {
        dungeon.getGame().setTurn(turnNumber);
    }

    public void incrementTurnNumber() {
        dungeon.getGame().incrementTurn();
    }

    public void updateStaticLit() {
        for(int i = 0; i < getWidth(); i++)
            Arrays.fill(staticLight[i], false);
        for(Thing t : things) {
            if(t instanceof Light && ((Light)t).isActive()) {
                Light l = (Light)t;
                for(int i = -l.getRange(); i <= l.getRange(); i++) {
                    for(int j = -l.getRange(); j <= l.getRange(); j++) {
                        if(!isOutOfBounds(l.getY() + i, l.getY() + j) && l.canLight(l.getX() + i, l.getY() + j, this) != 0) {
                            setStaticLit(l.getX() + i, l.getY() + j, l);
                        }
                    }
                }
            }
        }

        for(Creature c : creatures) {
            c.updateVision();
        }

        calculateOrientations();
        requestLightingUpdate = false;
    }

    public int getDangerLevel() {
        return dangerLevel;
    }

    public void setDangerLevel(int dangerLevel) {
        this.dangerLevel = dangerLevel;
    }

    public int getFloor_number() {
        return floor_number;
    }

    public void setFloor_number(int floor_number) {
        this.floor_number = floor_number;
    }

    public ArrayList<String> getProperties() {
        return getProperties(true);
    }

    public ArrayList<String> getProperties(boolean withDungeon) {
        ArrayList<String> propertyList = new ArrayList<>();
        if(withDungeon) propertyList.addAll(dungeon.getProperties());
        propertyList.addAll(properties);
        return propertyList;
    }

    public void setProperties(ArrayList<String> properties) {
        this.properties = properties;
    }

    public boolean hasProperty(String property) {
        return hasProperty(property, false);
    }

    public boolean hasProperty(String property, boolean withDungeon) {
        if(withDungeon) {
            if(properties == null && dungeon.properties == null) return false;
            else if(properties == null) return dungeon.hasProperty(property);
            else if(dungeon.properties == null) return properties.contains(property);
            else return properties.contains(property) || dungeon.hasProperty(property);
        }
        else {
            if(properties == null) return false;
            return properties.contains(property);
        }
    }

    public void addProperty(String property) {
        if(properties == null) return;
        if(!hasProperty(property, true)) properties.add(property);
    }

    public int[][][] getTile_orientations() {
        return tile_orientations;
    }

    public void setTile_orientations(int[][][] tile_orientations) {
        this.tile_orientations = tile_orientations;
    }

    public int[] getOrientation(int x, int y) {
        if(isOutOfBounds(x, y)) return new int[]{0};
        return tile_orientations[x][y];
    }

    public void calculateOrientations() {

        for(int i = 0; i < getWidth(); i++) {
            for(int j = 0; j < getHeight(); j++) {

                Tile t = getTileAt(i, j);

                if(t == Tile.WALL) {
                    Tile[][] adj = Utility.getAdjacentTiles(tiles, i, j);
                    boolean[][] toChange = new boolean[adj.length][adj[0].length];
                    //For the intents of this method, treat null as if it was the same tile as t.
                    //Same with walls the player can't see
                    for(int indexi = 0; indexi < adj.length; indexi++) {
                        for(int indexj = 0; indexj < adj[0].length; indexj++) {
                            if(adj[indexi][indexj] == null) toChange[indexi][indexj] = true;
                            else if(!getSeen(i + indexi - 1, j + indexj - 1)) adj[indexi][indexj] = t;
                            else toChange[indexi][indexj] = false;
                        }
                    }

                    for(int indexi = 0; indexi < adj.length; indexi++) {
                        for(int indexj = 0; indexj < adj[0].length; indexj++) {
                            if(toChange[indexi][indexj]) adj[indexi][indexj] = t;
                        }
                    }

                    //Note: (0, 0) is bottom left corner, not top left.
                    //No walls
                    if(adj[0][1] != t && adj[1][0] != t && adj[1][2] != t && adj[2][1] != t) {
                        tile_orientations[i][j] = new int[] {15};
                    }
                    //No walls on: bottom, left, right
                    else if(adj[0][1] != t && adj[1][0] != t && adj[1][2] != t && adj[2][1] == t) {
                        tile_orientations[i][j] = new int[] {12};
                    }
                    //No walls on: bottom, left, top
                    else if(adj[0][1] != t && adj[1][0] != t && adj[1][2] == t && adj[2][1] != t) {
                        tile_orientations[i][j] = new int[] {11};
                    }
                    //No walls on: bottom, left
                    else if(adj[0][1] != t && adj[1][0] != t && adj[1][2] == t && adj[2][1] == t) {
                        tile_orientations[i][j] = new int[] {8};

                        if(adj[2][2] != t) {
                            tile_orientations[i][j] = Arrays.copyOf(tile_orientations[i][j], tile_orientations[i][j].length + 1);
                            tile_orientations[i][j][tile_orientations[i][j].length - 1] = 18;
                        }
                    }
                    //No walls on: bottom, right, top
                    else if(adj[0][1] != t && adj[1][0] == t && adj[1][2] != t && adj[2][1] != t) {
                        tile_orientations[i][j] = new int[] {3};

                        if(adj[0][0] != t) {
                            tile_orientations[i][j] = Arrays.copyOf(tile_orientations[i][j], tile_orientations[i][j].length + 1);
                            tile_orientations[i][j][tile_orientations[i][j].length - 1] = 17;
                        }
                    }
                    //No walls on: bottom, right
                    else if(adj[0][1] != t && adj[1][0] == t && adj[1][2] != t && adj[2][1] == t) {
                        tile_orientations[i][j] = new int[] {0};
                        if(adj[2][0] != t) {
                            tile_orientations[i][j] = Arrays.copyOf(tile_orientations[i][j], tile_orientations[i][j].length + 1);
                            tile_orientations[i][j][tile_orientations[i][j].length - 1] = 19;
                        }
                    }
                    //No walls on: bottom, top
                    else if(adj[0][1] != t && adj[1][0] == t && adj[1][2] == t && adj[2][1] != t) {
                        tile_orientations[i][j] = new int[] {7};
                    }
                    //No walls on: bottom
                    else if(adj[0][1] != t && adj[1][0] == t && adj[1][2] == t && adj[2][1] == t) {
                        tile_orientations[i][j] = new int[] {4};

                        if(adj[2][0] != t) {
                            tile_orientations[i][j] = Arrays.copyOf(tile_orientations[i][j], tile_orientations[i][j].length + 1);
                            tile_orientations[i][j][tile_orientations[i][j].length - 1] = 19;
                        }
                        if(adj[2][2] != t) {
                            tile_orientations[i][j] = Arrays.copyOf(tile_orientations[i][j], tile_orientations[i][j].length + 1);
                            tile_orientations[i][j][tile_orientations[i][j].length - 1] = 18;
                        }
                    }
                    //No walls on: left, right, top
                    else if(adj[0][1] == t && adj[1][0] != t && adj[1][2] != t && adj[2][1] != t) {
                        tile_orientations[i][j] = new int[] {14};
                    }
                    //No walls on: left, right
                    else if(adj[0][1] == t && adj[1][0] != t && adj[1][2] != t && adj[2][1] == t) {
                        tile_orientations[i][j] = new int[] {13};
                    }
                    //No walls on: left, top
                    else if(adj[0][1] == t && adj[1][0] != t && adj[1][2] == t && adj[2][1] != t) {
                        tile_orientations[i][j] = new int[] {10};

                        if(adj[0][2] != t) {
                            tile_orientations[i][j] = Arrays.copyOf(tile_orientations[i][j], tile_orientations[i][j].length + 1);
                            tile_orientations[i][j][tile_orientations[i][j].length - 1] = 16;
                        }
                    }
                    //No walls on: left
                    else if(adj[0][1] == t && adj[1][0] != t && adj[1][2] == t && adj[2][1] == t) {
                        tile_orientations[i][j] = new int[] {9};
                    }
                    //No walls on: right, top
                    else if(adj[0][1] == t && adj[1][0] == t && adj[1][2] != t && adj[2][1] != t) {
                        tile_orientations[i][j] = new int[] {2};

                        if(adj[0][0] != t) {
                            tile_orientations[i][j] = Arrays.copyOf(tile_orientations[i][j], tile_orientations[i][j].length + 1);
                            tile_orientations[i][j][tile_orientations[i][j].length - 1] = 17;
                        }
                    }
                    //No walls on: right
                    else if(adj[0][1] == t && adj[1][0] == t && adj[1][2] != t && adj[2][1] == t) {
                        tile_orientations[i][j] = new int[] {1};

                        if(adj[0][0] != t) {
                            tile_orientations[i][j] = Arrays.copyOf(tile_orientations[i][j], tile_orientations[i][j].length + 1);
                            tile_orientations[i][j][tile_orientations[i][j].length - 1] = 17;
                        }
                        if(adj[2][0] != t) {
                            tile_orientations[i][j] = Arrays.copyOf(tile_orientations[i][j], tile_orientations[i][j].length + 1);
                            tile_orientations[i][j][tile_orientations[i][j].length - 1] = 19;
                        }
                    }
                    //No walls on: top
                    else if(adj[0][1] == t && adj[1][0] == t && adj[1][2] == t && adj[2][1] != t) {
                        tile_orientations[i][j] = new int[] {6};
                        if(adj[0][0] != t) {
                            tile_orientations[i][j] = Arrays.copyOf(tile_orientations[i][j], tile_orientations[i][j].length + 1);
                            tile_orientations[i][j][tile_orientations[i][j].length - 1] = 17;
                        }
                        if(adj[0][2] != t) {
                            tile_orientations[i][j] = Arrays.copyOf(tile_orientations[i][j], tile_orientations[i][j].length + 1);
                            tile_orientations[i][j][tile_orientations[i][j].length - 1] = 16;
                        }
                    }
                    //All walls
                    else if(adj[0][1] == t && adj[1][0] == t && adj[1][2] == t && adj[2][1] == t) {
                        tile_orientations[i][j] = new int[] {5};

                        if(adj[0][0] != t) {
                            tile_orientations[i][j] = Arrays.copyOf(tile_orientations[i][j], tile_orientations[i][j].length + 1);
                            tile_orientations[i][j][tile_orientations[i][j].length - 1] = 17;
                        }
                        if(adj[2][0] != t) {
                            tile_orientations[i][j] = Arrays.copyOf(tile_orientations[i][j], tile_orientations[i][j].length + 1);
                            tile_orientations[i][j][tile_orientations[i][j].length - 1] = 19;
                        }
                        if(adj[0][2] != t) {
                            tile_orientations[i][j] = Arrays.copyOf(tile_orientations[i][j], tile_orientations[i][j].length + 1);
                            tile_orientations[i][j][tile_orientations[i][j].length - 1] = 16;
                        }
                        if(adj[2][2] != t) {
                            tile_orientations[i][j] = Arrays.copyOf(tile_orientations[i][j], tile_orientations[i][j].length + 1);
                            tile_orientations[i][j][tile_orientations[i][j].length - 1] = 18;
                        }
                    }

                }
                else
                    tile_orientations[i][j] = new int[] {getTileAt(i, j).getNeutral()};
            }
        }

        for(Thing th : things) {
                if(th instanceof Stairs) {
                    if(((Stairs) th).isUp())
                        th.setOrientation(1);
                    else
                        th.setOrientation(0);
                }
                else if(th.getBehavior() instanceof DoorBehavior) {
                    Tile[][] adj = Utility.getAdjacentTiles(getTiles(), th.getX(), th.getY());
                    if(adj[0][1] == Tile.WALL) {
                        if(th.isOpen())
                            th.setOrientation(4);
                        else
                            th.setOrientation(0);
                    }
                    else {
                        if(th.isOpen())
                            th.setOrientation(5);
                        else
                            th.setOrientation(1);
                    }
                }
                else if(th instanceof Light) {
                    if(th instanceof LightRandom && System.currentTimeMillis() - ((LightRandom) th).getLastChange() > ((LightRandom) th).getCurrentRate())
                        ((LightRandom) th).changeColors();
                    switch (((Light) th).getPosition()) {
                        case Light.LEFT: {
                            if(((Light) th).isActive()) {
                                th.setOrientation(0);
                            }
                            else {
                                th.setOrientation(4);
                            }
                            break;
                        }
                        case Light.TOP: {
                            if(((Light) th).isActive()) {
                                th.setOrientation(2);
                            }
                            else {
                                th.setOrientation(6);
                            }
                            break;
                        }
                        case Light.RIGHT: {
                            if(((Light) th).isActive()) {
                                th.setOrientation(1);
                            }
                            else {
                                th.setOrientation(5);
                            }
                            break;
                        }
                        case Light.BOTTOM: {
                            if(((Light) th).isActive()) {
                                th.setOrientation(3);
                            }
                            else {
                                th.setOrientation(7);
                            }
                            break;
                        }
                        default: {
                            th.setOrientation(-1);
                            break;
                        }
                    }

                }
                else
                    th.setOrientation(th.getTile().getNeutral());
        }
    }

    public static long getActRate() {
        return ACT_RATE;
    }

    //</editor-fold>

}
