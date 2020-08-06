package world;

import actors.creatures.CreatureActor;
import actors.world.LevelActor;
import com.badlogic.gdx.graphics.Color;
import creatureitem.Creature;
import creatureitem.Player;
import creatureitem.item.Inventory;
import creatureitem.item.Item;
import game.Main;
import utility.Utility;
import world.geometry.AStarPoint;
import world.geometry.Point;
import world.geometry.floatPoint;
import world.thing.*;
import world.room.Room;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Stack;

public class Level extends LevelInterface {

    //<editor-fold desc="Instance Variables">
    /**
     * List of all creatures on the map
     */
    private ArrayList<Creature> creatures;

    /**
     * Reference to player
     */
    private Player player;

    /**
     * Tiles seen by player
     */
    private boolean[][] seen;

    private ArrayList<Light>[][] emitting;

    private boolean[][] staticLight;

    /**
     * A* Costs
     */
    private int[][] costs;

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

    //</editor-fold>

    public Level(Tile[][] tiles, Random random, String ... properties) {
        super(tiles);
        costs = Utility.toCostArray(tiles);
        this.random = random;
        creatures = new ArrayList<>();
        creatureQueue = new ArrayList<>();
        items = new Item[getWidth()][getHeight()];
        seen = new boolean[getWidth()][getHeight()];
        emitting = new ArrayList[getWidth()][getHeight()];
        staticLight = new boolean[getWidth()][getHeight()];
        rooms = new ArrayList<>();
        floor_number = 0;

        for(int i = 0; i < getWidth(); i++) {
            for(int j = 0; j < getHeight(); j++) {
                emitting[i][j] = new ArrayList<>();
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
        return items[x][y];
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

    public void addAtEmptyLocation(Thing t) {

        boolean placed = false;
        int x = 0, y = 0;

        while(!placed) {
            x = random.nextInt(getWidth() - 1);
            y = random.nextInt(getHeight() - 1);
            placed = addAt(x, y, t);
        }

        updateStaticLit();
    }

    /**
     * Add item i at (x, y)
     * @param x X coordinate
     * @param y Y coordinate
     * @param i The item
     * @return true if the item was successfully added at (x, y)
     */
    public boolean addAt(int x, int y, Item i) {
        if(!tiles[x][y].isGround() || items[x][y] != null)
            return false;

        items[x][y] = i;
        return true;
    }

    public boolean addAt(int x, int y, Thing t) {
        if(!tiles[x][y].isGround())
            return false;
        if(!t.isOpen() && (items[x][y] != null || getCreatureAt(x, y) != null))
            return false;

        things.add(t);
        addToCosts(t);
        t.setLocation(x, y);

        if(t instanceof Light) {
            Light l = (Light)t;
            l.determinePosition(this);
            for(int i = -l.getRange(); i <= l.getRange(); i++) {
                for(int j = -l.getRange(); j <= l.getRange(); j++) {
                    if(!isOutOfBounds(l.getY() + i, l.getY() + j) && l.canLight(l.getX() + i, l.getY() + j, this) != 0) {
                        setStaticLit(l.getX() + i, l.getY() + j, l);
                    }
                }
            }
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
            c.setLevel(this);
        }
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

        //creature.setAttack(0);
    }

    public Creature removeCreature(int x, int y) {
        Creature c = getCreatureAt(x, y);
        if(c == null) return null;
        remove(c);
        return c;
        //creature.setAttack(0);
    }

    /**
     * Remove an item from the level
     * @param item The item
     */
    public void remove(Item item) {
        for(int i = 0; i < items.length; i++) {
            for(int j = 0; j < items[0].length; j++) {
                if(items[i][j] == item) {
                    items[i][j] = null;
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
        Item i = items[x][y];
        items[x][y] = null;

        return i;
    }

    public void remove(Thing thing) {
        things.remove(thing);
    }

    public Thing removeThing(int x, int y) {
        Thing t = getThingAt(x, y);
        if(t == null) return null;
        remove(t);
        return t;
        //creature.setAttack(0);
    }


    /**
     * Let all creatures do their turn
     */
    public void update() {
        ArrayList<Creature> toUpdate = new ArrayList<>(creatures);
        for(Creature c : toUpdate) {
            //Don't update if the creature has been removed
            if(creatures.contains(c)) c.update();
            //costs = Utility.toCostArray(this);
        }

        /*
        for(Thing t : things) {
            if(t instanceof LightRandom && System.currentTimeMillis() - ((LightRandom) t).getLastChange() > ((LightRandom) t).getCurrentRate())
                ((LightRandom) t).changeColors();
        }


         */
        incrementTurn();
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
            /*
            int astardistance = p.astarDistanceFrom(t.getLocation(), Point.DISTANCE_MANHATTAN, calculateCosts(-1));
            if(astardistance != -1 && astardistance <= r) thingList.add(t);

             */
        }

        return thingList;
    }

    public Room getRoom(Point p) {
        return getRoom(p.getX(), p.getY());
    }

    public Room getRoom(int x, int y) {
        if(rooms.isEmpty() || rooms == null) return null;
        for(Room r : rooms) {
            int rx = r.parentXtoX(x), ry = r.parentYtoY(y);
            if(rx < 0 || ry < 0 || rx >= r.getWidth() || ry >= r.getHeight())
                continue;
            else if(r.getTileAt(rx, ry) == Tile.FLOOR)
                return r;
        }

        return null;
    }

    //<editor-fold desc="Getters and Setters">

    public ArrayList<Creature> getCreatures() {
        return creatures;
    }

    public void setCreatures(ArrayList<Creature> creatures) {
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
        for(int i = 0; i < seen.length; i++)
            Arrays.fill(seen[i], true);
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

    public ArrayList<Light>[][] getEmitting() {
        return emitting;
    }

    public ArrayList<Light> getEmitting(int x, int y) {
        return emitting[x][y];
    }

    public void setEmitting(ArrayList<Light>[][] emitting) {
        this.emitting = emitting;
    }

    public int[][] getCosts() {
        return costs;
    }

    public int[][] calculateCosts() {
        return calculateCosts(2);
    }

    public int[][] calculateCosts(int doorCost) {
        int[][] costs = Utility.toCostArray(tiles);

        for(Thing t : things) {
            if(!t.isOpen()) {
                if(t.getBehavior() instanceof DoorBehavior)
                    costs[t.getX()][t.getY()] = doorCost;
                else
                    costs[t.getX()][t.getY()] = -1;
            }
        }

        return costs;
    }

    public void addToCosts(Thing t) {
        int[][] costs = Utility.toCostArray(tiles);

        if(!t.isOpen()) {
            if(t.getBehavior() instanceof DoorBehavior)
                costs[t.getX()][t.getY()] += 1;
            else
                costs[t.getX()][t.getY()] = -1;
        }
    }

    public void setCosts(int[][] costs) {
        this.costs = costs;
    }

    public int getTurn() {
        return dungeon.getGame().getTurn();
    }

    public void setTurn(int turnNumber) {
        dungeon.getGame().setTurn(turnNumber);
    }

    public void incrementTurn() {
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
            else if(properties == null && dungeon.properties != null) return dungeon.hasProperty(property);
            else if(properties != null && dungeon.properties == null) return properties.contains(property);
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

    //</editor-fold>


}
