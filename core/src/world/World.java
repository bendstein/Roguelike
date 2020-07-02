package world;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import creature.Creature;
import creature.Player;

import java.util.ArrayList;
import java.util.Random;

public class World {

    /**
     * A 2D array of tiles making up the world
     */
    private Tile[][] tiles;

    /**
     * The width of the map
     */
    private int width;

    /**
     * The height of the map
     */
    private int height;

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

    private ArrayList<Creature> creatureQueue;

    /**
     * prng
     */
    private Random random;

    public World(Tile[][] tiles) {
        this.tiles = tiles;
        this.width = tiles.length;
        this.height = tiles[0].length;
        random = new Random(System.currentTimeMillis());
        creatures = new ArrayList<>();
        creatureQueue = new ArrayList<>();
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public ArrayList<Creature> getCreatures() {
        return creatures;
    }

    /**
     * @param x The x coord of the tile
     * @param y The y coord of the tile
     * @return The type of tile at (x, y)
     */
    public Tile getTileAt(int x, int y) {
        if (x < 0 || x >= width || y < 0 || y >= height)
            return Tile.BOUNDS;
        else
            return tiles[x][y];
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

    public Tile[][] getTiles() {
        return tiles;
    }

    public void setTiles(Tile[][] tiles) {
        this.tiles = tiles;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setCreatures(ArrayList<Creature> creatures) {
        this.creatures = creatures;
    }

    public Actor getActor() {
        return actor;
    }

    public void setActor(Actor actor) {
        this.actor = actor;
    }

    public Random getRandom() {
        return random;
    }

    public void setRandom(Random random) {
        this.random = random;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public ArrayList<Creature> getCreatureQueue() {
        return creatureQueue;
    }

    public void setCreatureQueue(ArrayList<Creature> creatureQueue) {
        this.creatureQueue = creatureQueue;
    }

    public Tile[][] getAdjacentTiles(int x, int y) {
        Tile[][] adj = new Tile[3][3];

        for(int i = -1; i <= 1; i++) {
            for(int j = -1; j <= 1; j++) {
                adj[i + 1][j + 1] = getTileAt(x + i, y + j);
            }
        }

        return adj;
    }

    public void dig(int x, int y) {
        if(getTileAt(x, y).isDiggable())
            tiles[x][y] = Tile.FLOOR;
    }

    /**
     * Place a creature at a random, unoccupied floor tile
     * @param creature The creature to place
     */
    public void addAtEmptyLocation(Creature creature) {
        int x, y;

        do {
            x = random.nextInt(width - 1);
            y = random.nextInt(height - 1);

        } while (!Creature.canEnter(x, y, this));

        creature.setCoordinates(x, y);

        creatureQueue.add(creature);
    }

    public void addCreatureAt(Creature creature, int x, int y) {
        creature.setCoordinates(x, y);
        creatureQueue.add(creature);
    }

    public ArrayList<Creature> addCreatureQueue() {
        creatures.addAll(creatureQueue);
        return creatureQueue;
    }

    public void clearCreatureQueue() {
        creatureQueue.clear();
    }

    /**
     * Remove a creature from the world
     * @param creature The creature to remove
     */
    public void remove(Creature creature) {
        creatures.remove(creature);
        creature.getActor().remove();
        creature.setAttack(0);
    }

    /**
     * Do everyone else's turn
     */
    public void update() {
        ArrayList<Creature> toUpdate = new ArrayList<>(creatures);
        for(Creature c : toUpdate)
            c.update();
    }

}
