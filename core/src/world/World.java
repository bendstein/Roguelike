package world;

import com.badlogic.gdx.scenes.scene2d.Actor;
import creatureitem.Creature;
import creatureitem.Player;
import creatureitem.item.Item;

import java.util.ArrayList;
import java.util.Random;

public class World extends Level {

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
     * Reference to this world's actor
     */
    private Actor actor;

    /**
     * Queue of creatures to add to the world
     */
    private ArrayList<Creature> creatureQueue;

    /**
     * 2d array of items in the world
     */
    private Item[][] items;

    /**
     * prng
     */
    private Random random;
    //</editor-fold>

    public World(Tile[][] tiles) {
        super(tiles);
        random = new Random(System.currentTimeMillis());
        creatures = new ArrayList<>();
        creatureQueue = new ArrayList<>();
        items = new Item[getWidth()][getHeight()];
    }

    public World(Tile[][] tiles, Random random) {
        super(tiles);
        this.random = random;
        creatures = new ArrayList<>();
        creatureQueue = new ArrayList<>();
        items = new Item[getWidth()][getHeight()];
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
     * Add all creatures in the queue to the world
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
     * Remove a creature from the world
     * @param creature The creature
     */
    public void remove(Creature creature) {
        creatures.remove(creature);
        creature.getActor().remove();
        creature.setAttack(0);
    }

    /**
     * Remove an item from the world
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
     * Remove an item at (x, y) from the world
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

    public Actor getActor() {
        return actor;
    }

    public void setActor(Actor actor) {
        this.actor = actor;
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

    public Random getRandom() {
        return random;
    }

    public void setRandom(Random random) {
        this.random = random;
    }
    //</editor-fold>
}
