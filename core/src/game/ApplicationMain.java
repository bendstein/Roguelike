package game;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import creatureitem.generation.CreatureItemFactory;
import creatureitem.Player;
import screens.MainMenu;
import screens.PlayScreen;
import world.World;
import world.generation.WorldBuilder;

import java.util.ArrayList;

public class ApplicationMain extends Game {

    private SpriteBatch batch;
    private BitmapFont font;

    private PlayScreen playScreen;
    private MainMenu mainMenu;

    private World world;
    private WorldBuilder builder;
    private CreatureItemFactory factory;
    private Player player;
    private ArrayList<String> messages;

    private static final int TILE_SIZE = 16;

    @Override
    public void create() {
        batch = new SpriteBatch();
        font = new BitmapFont();
        font.setColor(Color.BLACK);
        mainMenu = new MainMenu(this);
        playScreen = new PlayScreen(this);
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

    public World getWorld() {
        return world;
    }

    public void setWorld(World world) {
        this.world = world;
    }

    public WorldBuilder getBuilder() {
        return builder;
    }

    public void setBuilder(WorldBuilder builder) {
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

    //</editor-fold>

    public void start() {
        //Create the level
        builder = new WorldBuilder(50, 50);
        world = builder.makeBSPRooms().build();

        //Create the player
        factory = new CreatureItemFactory(world);
        messages = new ArrayList<>();
        player = factory.newPlayer(messages);
        world.addAtEmptyLocation(player);

        //Create creatures
        for(int i = 0; i < 11; i++) world.addAtEmptyLocation(factory.newFungus());

        for(int i = 0; i < 6; i++) world.addAtEmptyLocation(factory.newBat());

        //Create items
        for(int i = 0; i < 11; i++) world.addAtEmptyLocation(factory.newRock());
    }

}
