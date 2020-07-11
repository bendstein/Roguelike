package world;

import actors.world.LevelActor;
import com.badlogic.gdx.scenes.scene2d.Actor;
import creatureitem.Creature;
import creatureitem.Player;
import creatureitem.item.Item;
import world.room.Room;

import java.util.ArrayList;
import java.util.Random;

public class Level extends World {

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
     * List of all staircases in the level
     */
    private ArrayList<Stairs> stairs;

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

    //</editor-fold>

    public Level(Tile[][] tiles, Random random) {
        super(tiles);
        this.random = random;
        creatures = new ArrayList<>();
        creatureQueue = new ArrayList<>();
        items = new Item[getWidth()][getHeight()];
        seen = new boolean[getWidth()][getHeight()];
        stairs = new ArrayList<>();
        rooms = new ArrayList<>();

        for(int i = 0; i < getWidth(); i++) {
            for(int j = 0; j < getHeight(); j++) {
                seen[i][j] = false;
            }
        }
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
            creatureQueue.add(c);
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
        creatures.addAll(creatureQueue);
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
    public Item removeItemAt(int x, int y) {
        Item i = items[x][y];
        items[x][y] = null;
        return i;
    }

    /**
     * Let all creatures do their turn
     */
    public void update() {
        ArrayList<Creature> toUpdate = new ArrayList<>(creatures);
        for(Creature c : toUpdate)
            c.update();
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
                        System.out.println(e.getMessage());
                    }
                else if(tiles[r.xToParentX(i)][r.yToParentY(j)] == null)
                    tiles[r.xToParentX(i)][r.yToParentY(j)] = Tile.BOUNDS;

            }
        }
    }

    /**
     * @param x X coord
     * @param y Y coord
     * @return Return the stairs at (x, y), or null if there is none
     */
    public Stairs getStairsAt(int x, int y) {
        for(Stairs s : stairs) if(s.getX() == x && s.getY() == y) return s;
        return null;
    }

    /**
     * Place the given staircase s at (x, y)
     * @param s A staircase
     */
    public void addStairs(Stairs s) {
        tiles[s.getX()][s.getY()] = s.getT();
        stairs.add(s);
        s.setLevel(this);
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

    public ArrayList<Stairs> getStairs() {
        return stairs;
    }

    public void setStairs(ArrayList<Stairs> stairs) {
        this.stairs = stairs;
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

    public boolean getSeen(int x, int y) {
        return seen[x][y];
    }

    //</editor-fold>
}
