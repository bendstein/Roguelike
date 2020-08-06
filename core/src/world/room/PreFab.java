package world.room;

import creatureitem.Creature;
import creatureitem.item.Item;
import world.Tile;
import world.geometry.Point;
import world.thing.Thing;

import java.util.ArrayList;

/**
 * A room where all tiles, creatures and things are defined separate from level generation
 */
public class PreFab extends Room {

    private ArrayList<Creature> creatures;

    private Item[][] items;

    private ArrayList<Thing> things;

    public PreFab(Tile[][] tiles) {
        super(tiles);
        creatures = new ArrayList<>();
        items = new Item[tiles.length][tiles[0].length];
        things = new ArrayList<>();
    }

    public PreFab(int x, int y, Tile[][] tiles) {
        super(x, y, tiles);
        creatures = new ArrayList<>();
        items = new Item[tiles.length][tiles[0].length];
        things = new ArrayList<>();
    }

    public PreFab(Tile[][] tiles, ArrayList<Creature> creatures, Item[][] items, ArrayList<Thing> things) {
        super(tiles);
        this.creatures = creatures;
        this.items = items;
        this.things = things;
    }

    public PreFab(int x, int y, Tile[][] tiles, ArrayList<Creature> creatures, Item[][] items, ArrayList<Thing> things) {
        super(x, y, tiles);
        this.creatures = creatures;
        this.items = items;
        this.things = things;
    }

    //<editor-fold desc="Getters and Setters">
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

    public Item getItemAt(int x, int y) {
        return items[x][y];
    }

    public Item getItemAt(Point p) {
        return items[p.getX()][p.getY()];
    }

    public ArrayList<Thing> getThings() {
        return things;
    }

    public void setThings(ArrayList<Thing> things) {
        this.things = things;
    }
    //</editor-fold>
}
