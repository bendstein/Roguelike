package prefabbuilder;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.Random;

public class PrefabBuilderMain extends Game {

    private SpriteBatch batch;
    private BitmapFont font;

    private PrefabBuilderScreen screen;

    private Random random;
    private long seed;

    private final int DEFAULT_LEVEL_WIDTH = 25, DEFAULT_LEVEL_HEIGHT = 25;

    @Override
    public void create() {
        batch = new SpriteBatch();
        font = new BitmapFont();
        font.setColor(Color.BLACK);
        this.seed = System.currentTimeMillis();
        random = new Random(seed);
        screen = new PrefabBuilderScreen(this);
        setScreen(screen);
        Gdx.graphics.setContinuousRendering(false);
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

    public PrefabBuilderScreen getPrefabBuilderScreen() {
        return screen;
    }

    public void setPrefabBuilderScreen(PrefabBuilderScreen screen) {
        this.screen = screen;
    }

    public Random getRandom() {
        return random;
    }

    public void setRandom(Random random) {
        this.random = random;
    }

    public long getSeed() {
        return seed;
    }

    public void setSeed(long seed) {
        this.seed = seed;
    }

    public int getDEFAULT_LEVEL_WIDTH() {
        return DEFAULT_LEVEL_WIDTH;
    }

    public int getDEFAULT_LEVEL_HEIGHT() {
        return DEFAULT_LEVEL_HEIGHT;
    }

//</editor-fold>
}
