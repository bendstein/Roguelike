package game;

import com.badlogic.gdx.*;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import creatureitem.generation.CreatureItemFactory;
import creatureitem.Player;
import screens.*;
import world.Dungeon;
import world.Level;
import world.Town;
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
    private SpellScreen spellScreen;

    private Town town;
    private Player player;
    private ArrayList<String> messages;

    private int turn;

    private Random random;

    private long seed;

    private static final int TILE_WIDTH = 24;
    private static final int TILE_HEIGHT = 28;

    @Override
    public void create() {
        batch = new SpriteBatch();
        font = new BitmapFont();
        font.setColor(Color.BLACK);
        mainMenu = new MainMenu(this);
        loseScreen = new LoseScreen(this);
        inventoryScreen = new InventoryScreen(this);
        spellScreen = new SpellScreen(this);
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

    public Town getTown() {
        return town;
    }

    public void setTown(Town town) {
        this.town = town;
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

    public static int getTileWidth() {
        return TILE_WIDTH;
    }

    public static int getTileHeight() {
        return TILE_HEIGHT;
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

    public SpellScreen getSpellScreen() {
        return spellScreen;
    }

    public void setSpellScreen(SpellScreen spellScreen) {
        this.spellScreen = spellScreen;
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
        LevelFactory builder = new LevelFactory(90, 45, random);
        this.town = new Town(builder, random, this);
        town.generate();
        this.player = town.getPlayer();
    }


}
