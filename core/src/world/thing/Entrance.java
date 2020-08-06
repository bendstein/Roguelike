package world.thing;

import world.Dungeon;
import world.Level;
import world.Tile;

public class Entrance extends Stairs {

    /**
     * The dungeon this entrance leads to
     */
    private Dungeon dungeon;

    public Entrance(int x, int y, Dungeon dungeon, Level level) {
        super(x, y, null, level, false);
        this.dungeon = dungeon;
    }

    public Entrance(Dungeon dungeon, Level level) {
        super(0, 0, null, level, false);
        this.dungeon = dungeon;
    }

    public Entrance(Tile t, int x, int y, Dungeon dungeon, Level level) {
        super(t, x, y, null, level, false);
        this.dungeon = dungeon;
    }

    public Entrance(Tile t, Dungeon dungeon, Level level) {
        super(t, 0, 0, null, level, false);
        this.dungeon = dungeon;
    }

    public Dungeon getDungeon() {
        return dungeon;
    }

    public void setDungeon(Dungeon dungeon) {
        this.dungeon = dungeon;
    }
}
