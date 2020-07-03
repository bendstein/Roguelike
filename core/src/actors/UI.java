package actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import game.ApplicationMain;

import java.util.Locale;

public class UI {
    private Stage stage;
    private ScreenViewport viewport;
    private ApplicationMain game;

    private Table root;

    private ScrollPane log;
    private Table logTable;
    private Table outerLogTable;
    private final Skin skin = new Skin(Gdx.files.internal("skin/glassy-ui.json"));

    private Label hp;


    public UI(ApplicationMain game) {
        this.game = game;

        viewport = new ScreenViewport();
        viewport.setScreenPosition(0, Gdx.graphics.getHeight());
        stage = new Stage(viewport, game.getBatch());

    }

    public void init() {
        root = new Table();
        stage.addActor(root);
        root.setBackground(new TextureRegionDrawable(new Texture(Gdx.files.internal("data/Test_Background.png"))));
        root.setSize(viewport.getScreenWidth(), (1.01f * viewport.getScreenHeight()) / 5f);
        root.top().left();

        hp = new Label("", skin);
        hp.setWrap(false);
        root.add(hp).width(.1f * viewport.getScreenWidth()).top().padLeft(1f).padTop(0.5f);

        root.add().width(.7f * viewport.getScreenWidth());

        outerLogTable = new Table();
        outerLogTable.pad(5).defaults().space(4);
        outerLogTable.setSize(viewport.getScreenWidth() / 5f, (1.01f * viewport.getScreenHeight()) / 5f);
        outerLogTable.layout();

        root.add(outerLogTable);

        logTable = new Table();
        logTable.setSize(viewport.getScreenWidth() / 5f, (1.01f * viewport.getScreenHeight()) / 5f);
        logTable.top().left();
        logTable.layout();

        log = new ScrollPane(logTable, skin);
        outerLogTable.add(log);

    }

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

    public ApplicationMain getGame() {
        return game;
    }

    public void setGame(ApplicationMain game) {
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
        float menuy = viewport.getCamera().position.y + (2f * viewport.getScreenHeight()/5f) - 2.4f * ApplicationMain.getTILE_SIZE();

        root.setPosition(menux, menuy);

        //Update content
        hp.setText(String.format(Locale.getDefault(), "%d/%d hp", game.getPlayer().getHP(), game.getPlayer().getMaxHP()));
        displayOutput();


    }

    public void displayOutput() {

        for(String s : game.getPlayer().getAi().getMessages()) {
            System.out.println(s);
            Label l = new Label(s, getSkin());
            l.setFontScale(0.8f);
            l.setWrap(true);
            l.pack();
            getLogTable().add(l).width(getViewport().getScreenWidth()/5f);
            getLogTable().row();
            getLog().setScrollY(getLog().getMaxY());
            getLogTable().pack();
        }

        game.getPlayer().getAi().getMessages().clear();
    }
}
