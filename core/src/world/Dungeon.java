package world;

import actors.world.LevelActor;
import creatureitem.Player;
import creatureitem.generation.CreatureItemFactory;
import game.Main;
import org.jetbrains.annotations.NotNull;
import world.generation.LevelFactory;
import world.thing.Stairs;
import world.thing.StairsBehavior;

import java.util.ArrayList;
import java.util.Random;

public class Dungeon {

    //<editor-fold desc="Instance Variables">
    /**
     * Reference to the main game
     */
    private Main game;

    /**
     * The name of the dungeon
     */
    private String name;

    /**
     * The first floor of the dungeon
     */
    private Level root;

    /**
     * The factory for generating floors
     */
    private LevelFactory builder;

    /**
     * The factory for generating creatures
     */
    private CreatureItemFactory factory;

    /**
     * Reference to the player character
     */
    private Player player;

    /**
     * Number of floors
     */
    private int floors;

    /**
     * prng
     */
    private Random random;

    /**
     * Actor for levels in the dungeon
     */
    private LevelActor levelActor;

    //</editor-fold>

    public Dungeon(LevelFactory builder, CreatureItemFactory factory, Random random, Main game, int floors) {
        this.builder = builder;
        this.factory = factory;
        this.random = random;
        this.floors = floors;
        this.game = game;

        generate();
    }

    public void generate() {
        Level previous = null;
        Level current;

        for(int i = 0; i < floors; i++) {
            builder.clear();
            current = builder.generate().build();
            current.setDungeon(this);
            factory.setLevel(current);
            if(i == 0) {
                root = current;
                world.thing.Stairs first = connect(current, previous, true);
                this.player = factory.newPlayer(new ArrayList<>());
                player.setLevel(current);
                current.setPlayer(player);
                current.addAt(first.getX(), first.getY(), this.player);
                levelActor = new LevelActor(current);
                current.setActor(levelActor);
            }
            else
                connect(current, previous, true);
            for(int c = 0; c < 0; c++) {

                current.addAtEmptyLocation(factory.newFungus());
                current.addAtEmptyLocation(factory.newZombie());
                current.addAtEmptyLocation(factory.newGoblin());
                current.addAtEmptyLocation(factory.newBat());
                current.addAtEmptyLocation(factory.newRock());
                current.addAtEmptyLocation(factory.newLongsword());
                current.addAtEmptyLocation(factory.newShortbow());
                current.addAtEmptyLocation(factory.newArrow());
                current.addAtEmptyLocation(factory.newSling());
                current.addAtEmptyLocation(factory.newArmor());
                current.addAtEmptyLocation(factory.newHealthPotion());
                current.addAtEmptyLocation(factory.newRegenPotion());
                current.addAtEmptyLocation(factory.newPoisonPotion());
                current.addAtEmptyLocation(factory.newHeroismPotion());
            }

            previous = current;
        }

        factory.setLevel(root);
    }

    /**
     * Connect levels a and b by a staircase
     * @param a Level a (should not be null)
     * @param b Level b
     * @param below true if level a is below level b
     * @return the staircase generated on level a
     */
    public world.thing.Stairs connect(@NotNull Level a, Level b, boolean below) {

        int ax, ay, bx, by;

        do {
            ax = random.nextInt(a.getWidth());
            ay = random.nextInt(a.getHeight());
        } while(a.getTileAt(ax, ay) != Tile.FLOOR);

        world.thing.Stairs stair_a = new world.thing.Stairs(ax, ay, null, a, below);
        new StairsBehavior(stair_a);

        a.addStairs(stair_a);

        if (b != null) {
            do {
                bx = random.nextInt(b.getWidth());
                by = random.nextInt(b.getHeight());
            } while(b.getTileAt(bx, by) != Tile.FLOOR);

            world.thing.Stairs stair_b = new Stairs(bx, by, stair_a, b, !below);
            new StairsBehavior(stair_b);

            stair_a.setDestination(stair_b);
            b.addStairs(stair_b);
        }

        return stair_a;
    }

    //<editor-fold desc="Getters and Setters">

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Level getRoot() {
        return root;
    }

    public void setRoot(Level root) {
        this.root = root;
    }

    public LevelFactory getBuilder() {
        return builder;
    }

    public void setBuilder(LevelFactory builder) {
        this.builder = builder;
    }

    public CreatureItemFactory getFactory() {
        return factory;
    }

    public void setFactory(CreatureItemFactory factory) {
        this.factory = factory;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public int getFloors() {
        return floors;
    }

    public void setFloors(int floors) {
        this.floors = floors;
    }

    public Random getRandom() {
        return random;
    }

    public void setRandom(Random random) {
        this.random = random;
    }

    public LevelActor getLevelActor() {
        return levelActor;
    }

    public void setLevelActor(LevelActor levelActor) {
        this.levelActor = levelActor;
    }

    public Main getGame() {
        return game;
    }

    public void setGame(Main game) {
        this.game = game;
    }

    //</editor-fold>
}
