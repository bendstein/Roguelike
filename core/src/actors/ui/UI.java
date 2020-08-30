package actors.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import game.Main;
import utility.Utility;
import world.Level;
import world.Tile;
import world.geometry.Point;
import world.thing.Stairs;

import java.util.Locale;

public class UI {
    private Stage stage;
    private ScreenViewport viewport;
    private Main game;

    private Table root;

    private ScrollPane log;
    private Table logTable;
    private Table outerLogTable;
    private final Skin skin = new Skin(Gdx.files.internal("skin/glassy-ui.json"));

    private Table table1;
    private Label hp;
    private Label mana;
    private Label hunger;
    private Label level;
    private Label armor;

    private Table outerMinimapTable;
    private ScrollPane minimap;
    private Table innerMinimapTable;
    private float minimapScale;
    private Point currentPlayerLocation;
    private MinimapStack[][] stacks;
    private MinimapStack currentPlayerStack;
    private boolean requestMinimapUpdate;

    public UI(Main game) {
        this.game = game;

        viewport = new ScreenViewport();
        viewport.setScreenPosition(0, Gdx.graphics.getHeight());
        stage = new Stage(viewport, game.getBatch());
        requestMinimapUpdate = false;
    }

    /**
     * Set up the initial HUD
     */
    public void init() {
        root = new Table();
        stage.addActor(root);
        root.setBackground(new TextureRegionDrawable(new Texture(Gdx.files.internal("data/Test_Background.png"))));
        root.setSize(viewport.getScreenWidth(), (1f * viewport.getScreenHeight()) / 5f);
        root.top().left();

        table1 = new Table();

        hp = new Label("", skin);
        hp.setWrap(false);
        hp.setFontScale(.8f);

        mana = new Label("", skin);
        mana.setWrap(true);
        mana.setFontScale(.8f);

        hunger = new Label("", skin);
        hunger.setWrap(false);
        hunger.setFontScale(.8f);

        armor = new Label("", skin);
        armor.setWrap(true);
        armor.setFontScale(.8f);

        level = new Label("", skin);
        level.setWrap(true);
        level.setFontScale(.8f);

        table1.top().left().padLeft(5).defaults().expandY().height((1f * viewport.getScreenHeight()) / 10f).width(.2f * viewport.getScreenWidth());

        table1.add(hp).width(.1f * viewport.getScreenWidth()).height((1f * viewport.getScreenHeight()) / 20f);
        table1.add(mana).width(.1f * viewport.getScreenWidth()).height((1f * viewport.getScreenHeight()) / 20f);
        table1.row();
        table1.add(hunger).width(.1f * viewport.getScreenWidth()).height((1f * viewport.getScreenHeight()) / 20f);
        table1.row();
        table1.add(armor).width(.1f * viewport.getScreenWidth()).height((1f * viewport.getScreenHeight()) / 20f);
        table1.row();
        table1.add(level).width(.1f * viewport.getScreenWidth()).height((1f * viewport.getScreenHeight()) / 20f);

        root.add(table1);

        table1.layout();

        outerMinimapTable = new Table();
        innerMinimapTable = new Table();
        innerMinimapTable.setSize(.3f * viewport.getScreenWidth(), (.2f * viewport.getScreenHeight()));
        minimap = new ScrollPane(innerMinimapTable);
        outerMinimapTable.add(minimap);
        minimapScale = .25f;
        currentPlayerLocation = new Point(-1, -1);
        currentPlayerStack = new MinimapStack(currentPlayerLocation);

        minimap.addListener(new InputListener() {

            @Override
            public boolean scrolled(InputEvent event, float x, float y, int amount) {
                switch (amount) {
                    case -1: {
                        if(minimapScale >= 1.5f) return true;
                        else if(minimapScale <= .20) {
                            minimapScale += 0.05f;
                            if(minimapScale == .15f) minimapScale = .2f;
                        }
                        else if(minimapScale % .25 == 0) {
                            minimapScale += 0.25f;
                        }
                        else {
                            minimapScale += minimapScale % .25f;
                            return scrolled(event, x, y, 1);
                        }
                        System.out.println(minimapScale);
                        requestMinimapUpdate = true;
                        return true;
                    }
                    case 1: {
                        if(minimapScale <= 0.15) return true;
                        else if(minimapScale <= .25) {
                            minimapScale -= 0.05f;
                            if(minimapScale == .15f) minimapScale = .1f;
                        }
                        else if(minimapScale % .25 == 0) {
                            minimapScale -= 0.25f;
                        }
                        else {
                            minimapScale -= minimapScale % .25f;
                            return scrolled(event, x, y, -1);
                        }
                        System.out.println(minimapScale);
                        requestMinimapUpdate = true;
                        return true;
                    }
                    default: {
                        return false;
                    }
                }
            }
        });

        initMinimap();

        root.add(outerMinimapTable).width(.3f * viewport.getScreenWidth());
        //Filler space
        root.add().width(.3f * viewport.getScreenWidth());

        outerLogTable = new Table();
        outerLogTable.pad(5).defaults().space(4);
        outerLogTable.setSize(viewport.getScreenWidth() / 5f, (1f * viewport.getScreenHeight()) / 5f);
        outerLogTable.layout();

        root.add(outerLogTable);

        logTable = new Table();
        logTable.setSize(viewport.getScreenWidth() / 5f, (1f * viewport.getScreenHeight()) / 5f);
        logTable.top().left();
        logTable.layout();

        log = new ScrollPane(logTable);
        log.setFadeScrollBars(false);
        log.setScrollingDisabled(true, false);
        outerLogTable.add(log).expandY();

        for(int i = 0; i < 15; i++) {
            Label l = new Label("", getSkin());
            l.setFontScale(0.8f);
            l.setWrap(true);
            l.pack();
            getLogTable().top().left().add(l).expandY().width(getViewport().getScreenWidth()/5f).height((1f * viewport.getScreenHeight()) / 30f);
            getLogTable().row();
            getLogTable().layout();
            getLog().scrollTo(0, 0, 0, 0);
        }

    }

    public void initMinimap() {
        stacks = new MinimapStack[game.getLevel().getWidth()][game.getLevel().getHeight()];
        Level level = game.getLevel();
        innerMinimapTable.clear();
        requestMinimapUpdate = true;

        for(int j = level.getHeight() - 1; j >= 0 ; j--) {
            for(int i = 0; i < level.getWidth() ; i++) {
                MinimapStack s = new MinimapStack(i, j);
                stacks[i][j] = s;
                s.addListener(new InputListener() {

                    @Override
                    public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                        setMinimapCenter(s.getX(), s.getY());

                        if(game.getPlayer().getCursor().isActive()) {
                            if(game.getPlayer().getCursor().getX() == s.getPX() &&
                                game.getPlayer().getCursor().getY() == s.getPY()) {
                                if(game.getPlayer().getSeen(game.getPlayer().getCursor().getX(), game.getPlayer().getCursor().getY())) {
                                    if(game.getPlayer().getCursor().getPurpose().equals("look") ||
                                            game.getPlayer().getCursor().getPurpose().equals("move")) {
                                        game.getPlayer().getCursor().setActive(false);
                                        game.getPlayer().setCurrentDestination(s.getP());
                                    }
                                }
                            }
                            else {
                                game.getPlayer().getCursor().setLocation(s.getPX(), s.getPY());

                                if(game.getPlayer().getCursor().getPurpose().equals("move")) {
                                    if(game.getPlayer().canSee(s.getP()) || game.getPlayer().getSeen(s.getPX(), s.getPY())) {
                                        game.getPlayer().getCursor().setHasLine(true);
                                        game.getPlayer().getCursor()
                                                .setPath(Utility.aStarPathToLine(Utility.aStarWithVision(game.getLevel().getCosts(), game.getPlayer(), Point.DISTANCE_MANHATTAN, game.getPlayer().getLocation(), game.getPlayer().getCursor())).getPoints());
                                    }
                                }
                            }

                        }
                        else {
                            game.getPlayer().setCurrentDestination(null);
                            game.getPlayer().getDestinationQueue().clear();
                            game.getPlayer().prepLook();
                            game.getPlayer().getCursor().setLocation(s.getPX(), s.getPY());
                        }

                        return true;
                    }
                });

            }
            innerMinimapTable.row();
        }

        innerMinimapTable.defaults().width(Main.getTileWidth() * minimapScale).height(Main.getTileHeight() * minimapScale).pad(0);
        for(int j = level.getHeight() - 1; j >= 0 ; j--) {
            for(int i = 0; i < level.getWidth() ; i++) {
                innerMinimapTable.add(stacks[i][j]);
            }
            innerMinimapTable.row();
        }
    }

    /**
     * Dispose of the stage
     */
    public void dispose() {
        stage.dispose();
    }

    //<editor-fold desc="Getters and Setters">

    public Point getCurrentPlayerLocation() {
        return currentPlayerLocation;
    }

    public void setCurrentPlayerLocation(Point currentPlayerLocation) {
        this.currentPlayerLocation = currentPlayerLocation;
    }

    public Stack[][] getStacks() {
        return stacks;
    }

    public void setStacks(MinimapStack[][] stacks) {
        this.stacks = stacks;
    }

    public Stack getCurrentPlayerStack() {
        return currentPlayerStack;
    }

    public void setCurrentPlayerStack(MinimapStack currentPlayerStack) {
        this.currentPlayerStack = currentPlayerStack;
    }

    public boolean isRequestMinimapUpdate() {
        return requestMinimapUpdate;
    }

    public void setRequestMinimapUpdate(boolean requestMinimapUpdate) {
        this.requestMinimapUpdate = requestMinimapUpdate;
    }

    public Table getTable1() {
        return table1;
    }

    public void setTable1(Table table1) {
        this.table1 = table1;
    }

    public Label getMana() {
        return mana;
    }

    public void setMana(Label mana) {
        this.mana = mana;
    }

    public Label getHunger() {
        return hunger;
    }

    public void setHunger(Label hunger) {
        this.hunger = hunger;
    }

    public Label getLevel() {
        return level;
    }

    public void setLevel(Label level) {
        this.level = level;
    }

    public Label getArmor() {
        return armor;
    }

    public void setArmor(Label armor) {
        this.armor = armor;
    }

    public Table getOuterMinimapTable() {
        return outerMinimapTable;
    }

    public void setOuterMinimapTable(Table outerMinimapTable) {
        this.outerMinimapTable = outerMinimapTable;
    }

    public ScrollPane getMinimap() {
        return minimap;
    }

    public void setMinimap(ScrollPane minimap) {
        this.minimap = minimap;
    }

    public Table getInnerMinimapTable() {
        return innerMinimapTable;
    }

    public void setInnerMinimapTable(Table innerMinimapTable) {
        this.innerMinimapTable = innerMinimapTable;
    }

    public float getMinimapScale() {
        return minimapScale;
    }

    public void setMinimapScale(float minimapScale) {
        this.minimapScale = minimapScale;
    }

    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public ScreenViewport getViewport() {
        return viewport;
    }

    public void setViewport(ScreenViewport viewport) {
        this.viewport = viewport;
    }

    public Main getGame() {
        return game;
    }

    public void setGame(Main game) {
        this.game = game;
    }

    public Table getRoot() {
        return root;
    }

    public void setRoot(Table root) {
        this.root = root;
    }

    public ScrollPane getLog() {
        return log;
    }

    public void setLog(ScrollPane log) {
        this.log = log;
    }

    public Table getLogTable() {
        return logTable;
    }

    public void setLogTable(Table logTable) {
        this.logTable = logTable;
    }

    public Table getOuterLogTable() {
        return outerLogTable;
    }

    public void setOuterLogTable(Table outerLogTable) {
        this.outerLogTable = outerLogTable;
    }

    public Skin getSkin() {
        return skin;
    }

    public Label getHp() {
        return hp;
    }

    public void setHp(Label hp) {
        this.hp = hp;
    }
    //</editor-fold>

    public void act(float delta) {

        stage.act(delta);

        //Update location
        float menux = viewport.getCamera().position.x - viewport.getScreenWidth()/2f;
        float menuy = viewport.getCamera().position.y + (2f * viewport.getScreenHeight()/5f) - (float)Math.pow(Main.getTileHeight(), 2)/10f;

        root.setPosition(menux, menuy);

        //Update content
        hp.setText(String.format(Locale.getDefault(), "%d/%d hp", game.getPlayer().getHp(), game.getPlayer().getHpMax()));
        //mana.setText(String.format(Locale.getDefault(), "%d/%d mana", game.getPlayer().getMana(), game.getPlayer().getManaMax()));
        hunger.setText(game.getPlayer().hungerToString() + " (" + game.getPlayer().getHunger() + ")");
        armor.setText(game.getPlayer().getArmor() + " defense");
        level.setText(String.format(Locale.getDefault(), "Level %d (%d to next)\n%d FPS", game.getPlayer().getExpLevel(), game.getPlayer().getAi().expToNextLevel(), Gdx.graphics.getFramesPerSecond()));
        displayOutput();

        if(requestMinimapUpdate) {

            Level level = game.getLevel();
            if(game != null && game.getPlayer() != null && game.getLevel() != null) {
                MinimapStack s;

                for(int j = level.getHeight() - 1; j >= 0 ; j--) {

                    for(int i = 0; i < level.getWidth() ; i++) {
                        s = stacks[i][j];
                        s.clearChildren();
                        boolean canSee;
                        boolean seen;

                        if(level.getActor() != null) {
                            canSee = level.getActor().playerSees(i, j);
                            seen = level.getActor().playerSeen(i, j);
                        }
                        else {
                            canSee = level.getPlayer().canSee(i, j);
                            seen = level.getSeen(i, j);
                        }

                        if (canSee || seen) {
                            Tile t = level.getTileAt(i, j);
                            if(t == null || t.getTexture() == null) {
                                Image im = new Image(Tile.BOUNDS.getSprite(Tile.BOUNDS.getNeutral()));
                                im.setScaling(Scaling.fill);
                                s.add(im);
                            }
                            else {
                                Image im = new Image(t.getSprite(t.getNeutral()));
                                im.setScaling(Scaling.fill);
                                s.add(im);
                            }

                            if(level.getThingAt(i, j) != null && level.getThingAt(i, j).getTile() != null &&
                                level.getThingAt(i, j).getTile().getTexture() != null) {
                                if(!level.getThingAt(i, j).isOpen()) {
                                    t = level.getThingAt(i, j).getTile();
                                    Image im = new Image(t.getSprite(t.getNeutral()));
                                    im.setScaling(Scaling.fill);
                                    s.add(im);
                                }
                                else if(level.getThingAt(i, j) instanceof Stairs) {
                                    t = level.getThingAt(i, j).getTile();
                                    Image im =
                                            new Image(t.getSprite(((Stairs) level.getThingAt(i, j)).isUp()? 1 : 0));
                                    im.setScaling(Scaling.fill);
                                    s.add(im);
                                }
                            }

                            if(level.getItemAt(i, j) != null && level.getItemAt(i, j).getTexture() != null) {
                                Image im = new Image(level.getItemAt(i, j).getTexture());
                                im.setScaling(Scaling.fill);
                                s.add(im);
                            }

                            if(level.getCreatureAt(i, j) != null && level.getCreatureAt(i, j).getTexture() != null && (level.getPlayer() == null || level.getPlayer().canSee(i, j))) {
                                if(level.getCreatureAt(i, j).equals(level.getPlayer()) && !level.getPlayer().getCursor().isActive()) {
                                    currentPlayerStack = s;
                                }
                                Image im = new Image(level.getCreatureAt(i, j).getTexture());
                                im.setScaling(Scaling.fill);
                                s.add(im);
                            }
                        }
                        else {
                            Image im = new Image(Tile.BOUNDS.getSprite(Tile.BOUNDS.getNeutral()));
                            im.setColor(Color.BLACK);
                            im.setScaling(Scaling.fill);
                            s.add(im);
                        }

                        if(level.getPlayer().getCursor().isActive() && level.getPlayer().getCursor().getX() == i &&
                            level.getPlayer().getCursor().getY() == j) {
                            currentPlayerStack = s;
                        }
                    }
                }
            }

            requestMinimapUpdate = false;
        }

        if(game.getPlayer() != null) {
            if(game.getPlayer().getCursor().isActive()) {
                if(currentPlayerLocation.equals(new Point(-1, -1)) || !currentPlayerLocation.equals(game.getPlayer().getCursor())) {
                    currentPlayerLocation = game.getPlayer().getCursor().point();
                    setMinimapCenter();
                }
            }
            else {
                if(currentPlayerLocation.equals(new Point(-1, -1)) || !currentPlayerLocation.equals(game.getPlayer().getLocation())) {
                    currentPlayerLocation = game.getPlayer().getLocation();
                    setMinimapCenter();
                }
            }
        }

    }

    public void setMinimapCenter() {
        setMinimapCenter(currentPlayerStack.getX(), currentPlayerStack.getY());
    }

    public void setMinimapCenter(float x, float y) {
        minimap.setScrollX(x);
        minimap.setScrollY((innerMinimapTable.getHeight() - Main.getTileHeight()) - y);
    }

    /**
     * Write output to the log
     */
    public void displayOutput() {

        for(String s : game.getPlayer().getAi().getMessages()) {
            System.out.println(s);
            Label l = new Label(s, getSkin());
            l.setFontScale(0.7f);
            l.setWrap(true);
            l.pack();
            getLogTable().top().left().add(l).expandY().width(getViewport().getScreenWidth()/6f)
                    .height(Math.max((1f * viewport.getScreenHeight()) / 30f, l.getPrefHeight())).padTop(1.5f).padBottom(1.5f);
            getLogTable().row();
            getLogTable().layout();
            getLog().scrollTo(0, 0, 0, 0);
        }

        game.getPlayer().getAi().getMessages().clear();
    }


    /**
     * A LibGDX Actor Stack where each stack has a reference to its location in the 2D array,
     * for the minimap.
     */
    public static class MinimapStack extends Stack {

        private Point p;

        public MinimapStack(int x, int y) {
            super();
            p = new Point(x, y);
        }

        public MinimapStack(Point p) {
            super();
            this.p = p;
        }

        public MinimapStack() {
            super();
            p = new Point(-1, -1);
        }

        //<editor-fold desc="Getters and Setters">
        public Point getP() {
            return p;
        }

        public int getPX() {
            return p == null? -1 : p.getX();
        }

        public int getPY() {
            return p == null? -1 : p.getY();
        }

        public void setP(Point p) {
            this.p = p;
        }

        public void setPX(int x) {
            if(p == null) p = new Point(x, -1);
            else p.setX(x);
        }

        public void setPY(int y) {
            if(p == null) p = new Point(-1, y);
            else p.setY(y);
        }
        //</editor-fold>
    }
}
