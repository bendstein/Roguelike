package game;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import creatureitem.generation.CreatureItemFactory;
import creatureitem.Player;
import screens.InventoryScreen;
import screens.LoseScreen;
import screens.MainMenu;
import screens.PlayScreen;
import world.Dungeon;
import world.Level;
import world.Stairs;
import world.generation.LevelFactory;

import java.util.ArrayList;
import java.util.Random;

public class Main extends Game {

    private SpriteBatch batch;
    private BitmapFont font;

    private PlayScreen playScreen;
    private MainMenu mainMenu;
    private LoseScreen loseScreen;
    private InventoryScreen inventoryScreen;

    private Dungeon dungeon;
    private Player player;
    private ArrayList<String> messages;

    private int turn;

    private Random random;

    private long seed;

    private static final int TILE_SIZE = 32;

    @Override
    public void create() {
        batch = new SpriteBatch();
        font = new BitmapFont();
        font.setColor(Color.BLACK);
        mainMenu = new MainMenu(this);
        loseScreen = new LoseScreen(this);
        inventoryScreen = new InventoryScreen(this);
        seed = System.currentTimeMillis();
        random = new Random(seed);
        turn = 0;
        setScreen(mainMenu);
    }

    @Override
    public void dispose() {
        batch.dispose();
        font.dispose();
    }

    //<editor-fold desc="Getters and Setters">
    public SpriteBatch getBatch() {
        return batch;
    }

    public void setBatch(SpriteBatch batch) {
        this.batch = batch;
    }

    public BitmapFont getFont() {
        return font;
    }

    public void setFont(BitmapFont font) {
        this.font = font;
    }

    public Dungeon getDungeon() {
        return dungeon;
    }

    public void setDungeon(Dungeon dungeon) {
        this.dungeon = dungeon;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public PlayScreen getPlayScreen() {
        return playScreen;
    }

    public void setPlayScreen(PlayScreen playScreen) {
        this.playScreen = playScreen;
    }

    public MainMenu getMainMenu() {
        return mainMenu;
    }

    public void setMainMenu(MainMenu mainMenu) {
        this.mainMenu = mainMenu;
    }

    public ArrayList<String> getMessages() {
        return messages;
    }

    public void setMessages(ArrayList<String> messages) {
        this.messages = messages;
    }

    public static int getTILE_SIZE() {
        return TILE_SIZE;
    }

    public LoseScreen getLoseScreen() {
        return loseScreen;
    }

    public void setLoseScreen(LoseScreen loseScreen) {
        this.loseScreen = loseScreen;
    }

    public Random getRandom() {
        return random;
    }

    public void setRandom(Random random) {
        this.random = random;
    }

    public InventoryScreen getInventoryScreen() {
        return inventoryScreen;
    }

    public void setInventoryScreen(InventoryScreen inventoryScreen) {
        this.inventoryScreen = inventoryScreen;
    }

    public long getSeed() {
        return seed;
    }

    public Level getLevel() {
        return player.getLevel();
    }

    public int getTurn() {
        return turn;
    }

    public void setTurn(int turn) {
        this.turn = turn;
    }

    public void incrementTurn() {
        turn++;
    }

    //</editor-fold>

    public void start() {
        LevelFactory builder = new LevelFactory(100, 100, random);
        CreatureItemFactory factory = new CreatureItemFactory();
        this.dungeon = new Dungeon(builder, factory, random, this,10);
        this.player = dungeon.getPlayer();
    }


}
