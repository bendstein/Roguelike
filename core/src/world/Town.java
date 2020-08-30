package world;

import actors.world.LevelActor;
import com.badlogic.gdx.graphics.Color;
import creatureitem.Creature;
import creatureitem.generation.CreatureItemFactory;
import game.Main;
import world.generation.LevelFactory;
import world.thing.*;

import java.util.ArrayList;
import java.util.Random;

public class Town extends Dungeon {

    public Town(LevelFactory builder, Random random, Main game) {
        super(builder, random, game, 0, 1, 0, "");
    }

    @Override
    public void generate() {
        Level t = builder.fill(Tile.FLOOR).build();
        t.setTileAt(10, 5, Tile.WALL);
        t.setDungeon(this);
        root = t;

        this.player = CreatureItemFactory.newPlayer(new ArrayList<>());
        CreatureItemFactory.stockCreature(player, 1, random);
        player.setLevel(root);
        //player.setCanSeeEverything(true);
        root.setPlayer(player);
        root.addAtEmptyLocation(player);
        levelActor = new LevelActor(root);
        root.setActor(levelActor);

        Creature v = CreatureItemFactory.newCreature("Villager");
        CreatureItemFactory.newAi("Villager", v);
        CreatureItemFactory.newActor("Villager", v);
        root.addAtEmptyLocation(v);

        Light l1, l2;
        l1 = new LightRandom(Tile.BRAZIER, true, 6, 1f, 5, true, random, 1000L, 0,
                new int[][]{
                        {Light.YELLOW, Light.WHITE},
                        {Light.WHITE, Light.ORANGE},
                }, Color.FIREBRICK);
        l2 = new LightRandom(Tile.BRAZIER, true, 6, 1f, 5, true, random, 1000L, 0,
                new int[][]{
                        {Light.YELLOW, Light.WHITE},
                        {Light.WHITE, Light.ORANGE},
                }, Color.FIREBRICK);

        root.addAt(6, 12, l1);
        root.addAt(13, 13, l2);
        new LightBehavior(l1);
        new LightBehavior(l2);

        Entrance e = new Entrance(new Dungeon(builder, random, game, Dungeon.CAVERNS, 10, 0, "Cave"), root);
        new EntranceBehavior(e);
        root.addAtEmptyLocation(e);

        Entrance e2 = new Entrance(new Dungeon(builder, random, game, Dungeon.DUNGEON, 10, 0, "Dungeon"), root);
        new EntranceBehavior(e2);
        root.addAtEmptyLocation(e2);

    }
}
