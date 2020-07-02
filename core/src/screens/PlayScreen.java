package screens;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import game.ApplicationMain;
import creature.Creature;
import world.geometry.floatPoint;

public class PlayScreen extends ScreenAdapter {

    private ApplicationMain game;

    //Game stage
    private Stage stage;
    private Table root;
    private Viewport viewport;
    private Camera camera;

    //UI
    private Stage uiStage;
    private Table uiRoot;
    private final Skin skin = new Skin(Gdx.files.internal("skin/glassy-ui.json"));

    private ScrollPane log;
    private Table logTable;
    private Table outerLogTable;

    public PlayScreen(ApplicationMain game) {
        this.game = game;

        //Set up game stage
        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        viewport = new ScreenViewport(camera);
        stage = new Stage(viewport, game.getBatch());

        root = new Table();
        root.setFillParent(true);
        stage.addActor(root);

        //Create ui
        uiStage = new Stage(viewport, game.getBatch());
        uiRoot = new Table();
        uiStage.addActor(uiRoot);
        uiRoot.setSize(viewport.getScreenWidth(), viewport.getScreenHeight()/5f);
        uiRoot.bottom().right();

        outerLogTable = new Table();
        outerLogTable.pad(15).defaults().space(4);
        outerLogTable.setSize(viewport.getScreenWidth()/5f, viewport.getScreenHeight()/5f);
        outerLogTable.layout();

        uiRoot.add(outerLogTable);

        logTable = new Table();
        logTable.setSize(viewport.getScreenWidth()/5f, viewport.getScreenHeight()/5f);
        logTable.top().left();
        logTable.layout();

        log = new ScrollPane(logTable, skin);
        outerLogTable.add(log);

        stage.addListener(new InputListener()  {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                switch (keycode) {
                    case Input.Keys.NUMPAD_7: {
                        game.getPlayer().moveBy(-1, 1);
                        game.getPlayer().setMoveDirection(keycode);
                        break;
                    }
                    case Input.Keys.NUMPAD_8: {
                        game.getPlayer().moveBy(0, 1);
                        game.getPlayer().setMoveDirection(keycode);
                        break;
                    }
                    case Input.Keys.NUMPAD_9: {
                        game.getPlayer().moveBy(1, 1);
                        game.getPlayer().setMoveDirection(keycode);
                        break;
                    }
                    case Input.Keys.NUMPAD_4: {
                        game.getPlayer().moveBy(-1, 0);
                        game.getPlayer().setMoveDirection(keycode);
                        break;
                    }
                    case Input.Keys.NUMPAD_5: {
                        game.getPlayer().moveBy(0, 0);
                        game.getPlayer().setMoveDirection(keycode);
                        break;
                    }
                    case Input.Keys.NUMPAD_6: {
                        game.getPlayer().moveBy(1, 0);
                        game.getPlayer().setMoveDirection(keycode);
                        break;
                    }
                    case Input.Keys.NUMPAD_1: {
                        game.getPlayer().moveBy(-1, -1);
                        game.getPlayer().setMoveDirection(keycode);
                        break;
                    }
                    case Input.Keys.NUMPAD_2: {
                        game.getPlayer().moveBy(0, -1);
                        game.getPlayer().setMoveDirection(keycode);
                        break;
                    }
                    case Input.Keys.NUMPAD_3: {
                        game.getPlayer().moveBy(1, -1);
                        game.getPlayer().setMoveDirection(keycode);
                        break;
                    }
                    case Input.Keys.ESCAPE: {
                        game.setScreen(game.getMainMenu());
                        break;
                    }
                    default:
                        return false;
                }

                return true;
            }

            @Override
            public boolean keyUp(InputEvent event, int keycode) {
                switch (keycode) {
                    case Input.Keys.NUMPAD_7:
                    case Input.Keys.NUMPAD_8:
                    case Input.Keys.NUMPAD_9:
                    case Input.Keys.NUMPAD_4:
                    case Input.Keys.NUMPAD_5:
                    case Input.Keys.NUMPAD_6:
                    case Input.Keys.NUMPAD_1:
                    case Input.Keys.NUMPAD_2:
                    case Input.Keys.NUMPAD_3: {
                        game.getPlayer().setMoveDirection(0);
                        break;
                    }
                    default:
                        return false;
                }

                return true;
            }

            @Override
            public boolean scrolled(InputEvent event, float x, float y, int amount) {
                if(y >= 4f * uiStage.getHeight()/5f && x >= 4f * uiStage.getWidth()/5f) {
                    if(amount == 1) {
                        log.setScrollY(log.getScrollY() + 20);
                        return true;
                    }
                    else if(amount == -1) {
                        log.setScrollY(log.getScrollY() - 20);
                        return true;
                    }

                }

                return false;
            }

        });

        start();

    }

    public void start() {

        game.start();

        //Add actors to stage
        root.add(game.getWorld().getActor()).width(stage.getWidth()).height(game.getWorld().getHeight() * ApplicationMain.getTILE_SIZE());

        //for(Creature c : game.getWorld().getCreatures())
            //root.add(c.getActor());

    }

    public void displayOutput() {

        for(String s : game.getPlayer().getAi().getMessages()) {
            System.out.println(s);
            Label l = new Label(s, skin);
            l.setWrap(true);
            l.pack();
            logTable.add(l).width(viewport.getScreenWidth()/5f);
            logTable.row();
            log.setScrollY(log.getMaxY());
            logTable.pack();
        }
        game.getPlayer().getAi().getMessages().clear();
    }

    @Override
    public void render(float delta) {

        super.render(delta);
        Gdx.gl.glClearColor(0, 0, 0, 255);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(Gdx.graphics.getDeltaTime());
        uiStage.act(Gdx.graphics.getDeltaTime());

        displayOutput();

        if(game.getPlayer().isDead()) game.setScreen(new MainMenu(game));

        //Set the camera to the player. If the player is too close to the edge of the screen, stop following
        floatPoint cameraLocation = getCameraLocation();
        stage.getCamera().position.set(cameraLocation.getX() * ApplicationMain.getTILE_SIZE(), (cameraLocation.getY() * ApplicationMain.getTILE_SIZE()), 0);
        stage.getCamera().update();

        //Keep the UI at the top of the screen

        float menux = camera.position.x - viewport.getScreenWidth()/2f;
        float menuy = camera.position.y + (2f * viewport.getScreenHeight()/5f) - 2.5f * ApplicationMain.getTILE_SIZE();

        uiRoot.setPosition(menux, menuy);

        //Draw the game, then the UI
        stage.draw();
        uiStage.draw();
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        viewport.update(width, height);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {

    }

    public floatPoint getCameraLocation() {

        float width = viewport.getScreenWidth();
        float height = viewport.getScreenHeight();
        floatPoint p = new floatPoint(game.getPlayer().getX(), game.getPlayer().getY() + ((height/ApplicationMain.getTILE_SIZE())/5f));

        //If player's location isn't at least half a screen from the left edge, set the camera to half a screen from the left edge
        if(p.getX() * ApplicationMain.getTILE_SIZE() < width/2)
            p.setX(width/(2 * ApplicationMain.getTILE_SIZE()));
        //If player's location isn't at least half a screen from the right edge, set the camera to half a screen from the right edge
        else if((game.getWorld().getWidth() - p.getX()) * ApplicationMain.getTILE_SIZE() < width/2d)
            p.setX((game.getWorld().getWidth()) - width/(2 * ApplicationMain.getTILE_SIZE()));

        //If player's location isn't at least half a screen from the bottom edge, set the camera to half a screen from the bottom edge
        if(p.getY() * ApplicationMain.getTILE_SIZE() < (height/2 - 2))
            p.setY(height/(2 * ApplicationMain.getTILE_SIZE()));
        //If player's location isn't at least half a screen from the top edge, set the camera to half a screen from the top edge
        else if((game.getWorld().getHeight() - p.getY()) * ApplicationMain.getTILE_SIZE() < (3f * height/5f)/2d)
            p.setY((game.getWorld().getHeight()) - (3 * height/5)/(2 * ApplicationMain.getTILE_SIZE()));

        return p;
    }

}
