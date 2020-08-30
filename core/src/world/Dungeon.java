package world;

import actors.world.LevelActor;
import com.badlogic.gdx.graphics.Color;
import creatureitem.Creature;
import creatureitem.Player;
import creatureitem.generation.CreatureItemFactory;
import creatureitem.item.Inventory;
import creatureitem.item.Item;
import game.Main;
import org.jetbrains.annotations.NotNull;
import world.generation.LevelFactory;
import world.thing.*;

import java.util.ArrayList;
import java.util.Random;

public class Dungeon {

    //<editor-fold desc="Instance Variables">
    /**
     * Reference to the main game
     */
    protected Main game;

    /**
     * The name of the dungeon
     */
    protected String name;

    /**
     * The first floor of the dungeon
     */
    protected Level root;

    /**
     * The factory for generating floors
     */
    protected LevelFactory builder;

    /**
     * Reference to the player character
     */
    protected Player player;

    /**
     * Number of floors
     */
    protected int floors;

    /**
     * prng
     */
    protected Random random;

    /**
     * Actor for levels in the dungeon
     */
    protected LevelActor levelActor;

    /**
     * Type of dungeon to generate
     */
    protected int type;

    /**
     * Properties that the dungeon has
     */
    protected ArrayList<String> properties;

    /**
     * A rating for how dangerous the dungeon will be.
     * The danger level for levels in the dungeon will
     * by default be this value.
     */
    protected int dangerLevel;

    /**
     * Types for type
     */
    protected static final int TOWN = 0, CAVERNS = 1, DUNGEON = 2;

    //</editor-fold>

    public Dungeon(LevelFactory builder, Random random, Main game, int type, int floors, int dangerLevel, String ... properties) {
        this.builder = builder;
        this.random = random;
        this.type = type;
        this.floors = floors;
        this.game = game;
        this.dangerLevel = dangerLevel;
        this.properties = new ArrayList<>();
        for(String p : properties) addProperty(p);
    }

    public void generate() {
    }

    public Stairs generateNextFloor(Stairs s, Creature cr) {
        if(type != 1 && type != 2) return null;
        setPlayer((Player)cr);

        if(type == 1)
            return generateNextFloor0(s, cr);
        else if(type == 2)
            return generateNextFloor1(s, cr);
        /*
        for(int i = 0; i < floors; i++) {
            builder.clear();
            if(type == 1)
                current = builder.cellularAutomata().padWorldWith(2, 2, Tile.WALL).build();
            else if(type == 2)
                current = builder.generate().build();
            current.setDungeon(this);
            current.setFloor_number(i + 1);
            current.setDangerLevel(dangerLevel);
            if(i == 0) {
                root = current;
                first = connect(e, current, e.isUp()).getDestination();
                stairs.add(first);
                levelActor = new LevelActor(current);
                current.setActor(levelActor);
            }
            else
                stairs.add(connect(current, previous, true).getDestination());

            previous = current;
        }

        Light[] lights = new Light[]{
                new LightRandom(Tile.BRAZIER, true, 6, .5f, 3, true, random, 2000L, 0,
                        new int[][]{
                                {Light.RED, Light.BLUE_GREEN, Light.GREEN, Light.WHITE},
                                {Light.PURPLE, Light.WHITE},
                        }),
        };

        for(Stairs s : stairs) {
            builder.generateItems(s.getLevel());
            builder.generateLightThings(s.getLevel(), lights);
        }

         */


        return null;
    }

    /**
     * Player went up/down the stairs to a non-existing level
     * @param st Stairs the player went down
     * @param cr The player
     * @return The stairs the player arrives at
     */
    public Stairs generateNextFloor0(Stairs st, Creature cr) {
        Level previous = st.getLevel();
        setPlayer((Player)cr);
        Level next;
        Stairs arriving;

        do {
            builder.clear();
            next = builder.cellularAutomata().padWorldWith(2, 2, Tile.WALL).build();
            next.setDungeon(this);
            next.setFloor_number(previous.getFloor_number() + 1);
            next.setDangerLevel(dangerLevel + Math.max(0, previous.getFloor_number()/5));

            if(st instanceof Entrance) {
                root = next;
                next.setFloor_number(0);
            }
            arriving = connect(st, next, st.isUp()).getDestination();
        } while (!next.getThingAt(arriving.getX(), arriving.getY()).equals(arriving));

        levelActor = new LevelActor(next);
        next.setActor(levelActor);

        Light[] lights = new Light[]{
                new LightRandom(Tile.BRAZIER, true, 6, .5f, 3, true, random, 2000L, 0,
                        new int[][]{
                                {Light.RED, Light.BLUE_GREEN, Light.GREEN, Light.WHITE},
                                {Light.PURPLE, Light.WHITE},
                        }),
        };

        builder.generateCreatures(arriving.getLevel(), 1);
        builder.generateItems(arriving.getLevel(), 1);
        builder.generateLightThings(arriving.getLevel(), lights);

        if(next.getFloor_number() < floors)
            connect(next, null, st.isUp());

        return arriving;
    }

    public Stairs generateNextFloor1(Stairs st, Creature cr) {
        Level previous = st.getLevel();
        setPlayer((Player)cr);
        Level next;
        Stairs arriving;

        do {
            builder.clear();
            next = builder.generate().build();

            next.setDungeon(this);
            next.setFloor_number(previous.getFloor_number() + 1);
            next.setDangerLevel(dangerLevel + Math.max(0, previous.getFloor_number()/5));

            if(st instanceof Entrance) {
                root = next;
                next.setFloor_number(0);
            }
            arriving = connect(st, next, st.isUp()).getDestination();
        } while(!next.getThingAt(arriving.getX(), arriving.getY()).equals(arriving));

        levelActor = new LevelActor(next);
        next.setActor(levelActor);

        Light[] lights = new Light[]{
                new Light(Tile.BRAZIER, new int[]{Light.YELLOW, Light.WHITE}, Color.CORAL, 6, .8f, 3, true)
        };

        builder.generateCreatures(arriving.getLevel(), 2);
        builder.generateItems(arriving.getLevel(), 2);
        builder.generateLightThings(arriving.getLevel(), lights);

        if(next.getFloor_number() < floors)
            connect(next, null, st.isUp());

        return arriving;
    }

    /**
     * Connect levels a and b by a staircase
     * @param a Level a (should not be null)
     * @param b Level b
     * @param below true if level a is below level b
     * @return the staircase generated on level a
     */
    public Stairs connect(@NotNull Level a, Level b, boolean below) {

        int ax, ay, bx, by;

        do {
            ax = random.nextInt(a.getWidth());
            ay = random.nextInt(a.getHeight());
        } while(a.getTileAt(ax, ay) != Tile.FLOOR);

        Stairs stair_a = new Stairs(ax, ay, null, a, below);
        Stairs stair_b = null;
        new StairsBehavior(stair_a);

        a.addStairs(stair_a);

        if (b != null) {
            do {
                bx = random.nextInt(b.getWidth());
                by = random.nextInt(b.getHeight());
            } while(b.getTileAt(bx, by) != Tile.FLOOR);

            stair_b = new Stairs(bx, by, stair_a, b, !below);
            new StairsBehavior(stair_b);

            stair_a.setDestination(stair_b);
            b.addStairs(stair_b);
        }

        return stair_a;
    }

    public Stairs connect(@NotNull Stairs a, Level b, boolean below) {

        int bx, by;

        if (b != null) {
            do {
                bx = random.nextInt(b.getWidth());
                by = random.nextInt(b.getHeight());
            } while(b.getTileAt(bx, by) != Tile.FLOOR);

            Stairs stair_b = new Stairs(bx, by, a, b, !below);
            new StairsBehavior(stair_b);

            a.setDestination(stair_b);
            b.addStairs(stair_b);
        }

        return a;
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

    public int getDangerLevel() {
        return dangerLevel;
    }

    public void setDangerLevel(int dangerLevel) {
        this.dangerLevel = dangerLevel;
    }

    public ArrayList<String> getProperties() {
        return properties;
    }

    public void setProperties(ArrayList<String> properties) {
        this.properties = properties;
    }

    public boolean hasProperty(String property) {
        if(properties == null) return false;
        return properties.contains(property);
    }

    public void addProperty(String property) {
        if(properties == null) return;
        if(!hasProperty(property)) properties.add(property);
    }

    //</editor-fold>
}
