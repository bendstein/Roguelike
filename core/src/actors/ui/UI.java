package actors.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import game.Main;

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
    private Label hunger;
    private Label level;
    private Label armor;


    public UI(Main game) {
        this.game = game;

        viewport = new ScreenViewport();
        viewport.setScreenPosition(0, Gdx.graphics.getHeight());
        stage = new Stage(viewport, game.getBatch());

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

        hunger = new Label("", skin);
        hunger.setWrap(false);

        armor = new Label("", skin);
        hunger.setWrap(true);

        level = new Label("", skin);
        level.setWrap(true);

        table1.top().left().padLeft(5).defaults().expandY().height((1f * viewport.getScreenHeight()) / 10f).width(.1f * viewport.getScreenWidth());

        table1.add(hp).width(.1f * viewport.getScreenWidth()).height((1f * viewport.getScreenHeight()) / 20f);
        table1.row();
        table1.add(hunger).width(.1f * viewport.getScreenWidth()).height((1f * viewport.getScreenHeight()) / 20f);
        table1.row();
        table1.add(armor).width(.1f * viewport.getScreenWidth()).height((1f * viewport.getScreenHeight()) / 20f);
        table1.row();
        table1.add(level).width(.1f * viewport.getScreenWidth()).height((1f * viewport.getScreenHeight()) / 20f);

        root.add(table1);

        table1.layout();

        //Filler space
        root.add().width(.7f * viewport.getScreenWidth());

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

    /**
     * Dispose of the stage
     */
    public void dispose() {
        stage.dispose();
    }

    //<editor-fold desc="Getters and Setters">
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
        float menuy = viewport.getCamera().position.y + (2f * viewport.getScreenHeight()/5f) - 2.4f * Main.getTILE_SIZE();

        root.setPosition(menux, menuy);

        //Update content
        hp.setText(String.format(Locale.getDefault(), "%d/%d hp", game.getPlayer().getHP(), game.getPlayer().getMaxHP()));
        hunger.setText(game.getPlayer().hungerToString() + " (" + game.getPlayer().getHunger() + ")");
        armor.setText(game.getPlayer().getArmor() + " defence");
        level.setText(String.format(Locale.getDefault(), "Level %d (%d to next)", game.getPlayer().getExpLevel(), game.getPlayer().getAi().expToNextLevel()));
        displayOutput();

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
}
