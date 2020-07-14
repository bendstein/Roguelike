package screens;

import actors.ui.UI;
import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import creatureitem.ai.PlayerAi;
import game.Main;
import utility.Utility;
import world.geometry.Point;
import world.geometry.floatPoint;

import java.util.Locale;

public class PlayScreen extends ScreenAdapter {

    /**
     * Reference to the main application
     */
    private Main game;

    /**
     * The input multiplexer
     */
    private InputMultiplexer mux;

    /**
     * Variables relating to the main stage
     */
    private Stage stage;
    private Table root;
    private Viewport viewport;
    private Camera camera;

    /**
     * Variables relating to the UI
     */
    private UI ui;
    private InventoryScreen inventoryScreen;

    /**
     * If the shift key is held down, true
     */
    private boolean shift = false;

    public PlayScreen(Main game) {
        this.game = game;

        ui = new UI(game);
        ui.init();

        //Set up game stage
        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        viewport = new ScreenViewport(camera);
        stage = new Stage(viewport, game.getBatch());

        root = new Table();
        root.setFillParent(true);
        stage.addActor(root);

        stage.addListener(new InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                switch (keycode) {
                    case Input.Keys.NUMPAD_7: {
                        game.getPlayer().setCurrentDestination(null);
                        game.getPlayer().getDestinationQueue().clear();
                        game.getPlayer().moveBy(-1, 1);
                        game.getPlayer().setMoveDirection(keycode);
                        return true;
                    }
                    case Input.Keys.NUMPAD_8: {
                        game.getPlayer().setCurrentDestination(null);
                        game.getPlayer().getDestinationQueue().clear();
                        game.getPlayer().moveBy(0, 1);
                        game.getPlayer().setMoveDirection(keycode);
                        return true;
                    }
                    case Input.Keys.NUMPAD_9: {
                        game.getPlayer().setCurrentDestination(null);
                        game.getPlayer().getDestinationQueue().clear();
                        game.getPlayer().moveBy(1, 1);
                        game.getPlayer().setMoveDirection(keycode);
                        return true;
                    }
                    case Input.Keys.NUMPAD_4: {
                        game.getPlayer().setCurrentDestination(null);
                        game.getPlayer().getDestinationQueue().clear();
                        game.getPlayer().moveBy(-1, 0);
                        game.getPlayer().setMoveDirection(keycode);
                        return true;
                    }
                    case Input.Keys.NUMPAD_5: {
                        game.getPlayer().setCurrentDestination(null);
                        game.getPlayer().getDestinationQueue().clear();
                        game.getPlayer().moveBy(0, 0);
                        game.getPlayer().setMoveDirection(keycode);
                        return true;
                    }
                    case Input.Keys.NUMPAD_6: {
                        game.getPlayer().setCurrentDestination(null);
                        game.getPlayer().getDestinationQueue().clear();
                        game.getPlayer().moveBy(1, 0);
                        game.getPlayer().setMoveDirection(keycode);
                        return true;
                    }
                    case Input.Keys.NUMPAD_1: {
                        game.getPlayer().setCurrentDestination(null);
                        game.getPlayer().getDestinationQueue().clear();
                        game.getPlayer().moveBy(-1, -1);
                        game.getPlayer().setMoveDirection(keycode);
                        return true;
                    }
                    case Input.Keys.NUMPAD_2: {
                        game.getPlayer().setCurrentDestination(null);
                        game.getPlayer().getDestinationQueue().clear();
                        game.getPlayer().moveBy(0, -1);
                        game.getPlayer().setMoveDirection(keycode);
                        return true;
                    }
                    case Input.Keys.NUMPAD_3: {
                        game.getPlayer().setCurrentDestination(null);
                        game.getPlayer().getDestinationQueue().clear();
                        game.getPlayer().moveBy(1, -1);
                        game.getPlayer().setMoveDirection(keycode);
                        return true;
                    }
                    case Input.Keys.ESCAPE: {
                        if(game.getPlayer().getCursor().isActive())
                            game.getPlayer().getCursor().setActive(false);
                        else game.setScreen(game.getMainMenu());
                        return true;
                    }
                    case Input.Keys.COMMA: {
                        game.getPlayer().setCurrentDestination(null);
                        game.getPlayer().getDestinationQueue().clear();
                        if(!shift) game.getPlayer().pickUp();
                        else {
                            shift = false;
                            boolean exit = ((PlayerAi) game.getPlayer().getAi()).useStairs('>');
                            if(exit) game.setScreen(game.getMainMenu());
                        }
                        return true;
                    }
                    case Input.Keys.PERIOD: {
                        game.getPlayer().setCurrentDestination(null);
                        game.getPlayer().getDestinationQueue().clear();
                        if(!shift){}
                        else {
                            shift = false;
                            boolean exit = ((PlayerAi) game.getPlayer().getAi()).useStairs('<');
                            if(exit) game.setScreen(game.getMainMenu());
                        }

                    }
                    case Input.Keys.SHIFT_RIGHT:
                    case Input.Keys.SHIFT_LEFT: {
                        shift = true;
                        return true;
                    }
                    case Input.Keys.L: {
                        if(!shift) {
                            game.getPlayer().getCursor().setPurpose("look");
                            game.getPlayer().getCursor().setActive(true);
                            game.getPlayer().getCursor().setFollow(true);
                            game.getPlayer().getCursor().setHasRange(false);
                            game.getPlayer().getCursor().setHasLine(false);
                            game.getPlayer().getCursor().setConsiderObstacle(false);
                            game.getPlayer().getCursor().setLocation(game.getPlayer().getX(), game.getPlayer().getY());
                        }
                        return true;
                    }
                    case Input.Keys.I: {
                        if(!shift) {
                            game.getInventoryScreen().setCurrentVerb("");
                            game.getInventoryScreen().setCurrentFilter("");
                            game.setScreen(game.getInventoryScreen());
                        }

                        return true;
                    }
                    case Input.Keys.D: {
                        if(!shift) {
                            game.getInventoryScreen().setCurrentVerb("drop");
                            game.getInventoryScreen().setCurrentFilter("");
                            game.setScreen(game.getInventoryScreen());
                        }

                        return true;
                    }
                    case Input.Keys.T: {
                        if(!shift) {
                            if(!game.getPlayer().getCursor().isActive()) {
                                game.getInventoryScreen().setCurrentVerb("throw");
                                game.getInventoryScreen().setCurrentFilter("");
                                game.setScreen(game.getInventoryScreen());
                            }
                            else if(game.getPlayer().getCursor().getPurpose().equals("throw")) {
                                game.getPlayer().throwItem();
                                game.getPlayer().getCursor().setPurpose("");
                                game.getPlayer().getCursor().setActive(false);
                            }
                            else if(game.getPlayer().getCursor().getPurpose().equals("shoot")) {
                                game.getPlayer().shoot();
                                game.getPlayer().getCursor().setPurpose("");
                                game.getPlayer().getCursor().setActive(false);
                            }

                        }
                        else {

                            shift = false;

                            if(!game.getPlayer().getCursor().isActive()) {
                                if(game.getPlayer().getQuiver() == null && game.getPlayer().getRangedWeapon() != null) {
                                    game.getInventoryScreen().setCurrentVerb("equip");
                                    game.getInventoryScreen().setCurrentFilter(game.getPlayer().getRangedWeapon().getAmmoType());
                                    game.setScreen(game.getInventoryScreen());
                                }
                                game.getPlayer().prepShoot();
                            }
                            else if(game.getPlayer().getCursor().getPurpose().equals("shoot")) {
                                game.getPlayer().shoot();
                                game.getPlayer().getCursor().setPurpose("");
                                game.getPlayer().getCursor().setActive(false);
                            }
                            else if(game.getPlayer().getCursor().getPurpose().equals("throw")) {
                                game.getPlayer().throwItem();
                                game.getPlayer().getCursor().setPurpose("");
                                game.getPlayer().getCursor().setActive(false);
                            }
                        }

                        return true;
                    }
                    case Input.Keys.Q: {
                        if(!shift) {
                            game.getInventoryScreen().setCurrentVerb("quaff");
                            game.getInventoryScreen().setCurrentFilter("quaff");
                            game.setScreen(game.getInventoryScreen());
                        }

                        return true;
                    }
                    case Input.Keys.E: {
                        if(!shift) {
                            game.getInventoryScreen().setCurrentVerb("eat");
                            game.getInventoryScreen().setCurrentFilter("eat");
                        }
                        else {
                            shift = false;
                            game.getInventoryScreen().setCurrentVerb("equip");
                            game.getInventoryScreen().setCurrentFilter("equip");
                        }

                        game.setScreen(game.getInventoryScreen());
                        return true;
                    }
                    case Input.Keys.U: {
                        if(!shift) {
                            game.getInventoryScreen().setCurrentVerb("use");
                            game.getInventoryScreen().setCurrentFilter("use");
                            game.setScreen(game.getInventoryScreen());
                        }
                        return true;
                    }
                    case Input.Keys.R: {

                        if(!shift) {
                            game.getInventoryScreen().setCurrentVerb("read");
                            game.getInventoryScreen().setCurrentFilter("read");
                            game.setScreen(game.getInventoryScreen());
                        }
                        return true;
                    }
                    default:
                        return false;
                }
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
                        return true;
                    }
                    case Input.Keys.SHIFT_RIGHT:
                    case Input.Keys.SHIFT_LEFT: {
                        shift = false;
                        return true;
                    }
                    default:
                        return false;
                }

            }

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
             switch (button) {
                 case Input.Buttons.LEFT: {
                     if(!shift) game.getPlayer().setCurrentDestination(Utility.roundCursor(x, y));
                     else game.getPlayer().enqueueDestination(Utility.roundCursor(x, y));
                     return true;
                 }
                 default: {
                     return false;
                 }
             }
            }
        });

        //<editor-fold desc="ui listener">
        ui.getStage().addListener(new InputListener() {
            @Override
            public boolean scrolled(InputEvent event, float x, float y, int amount) {
                if(amount == 1) {
                    ui.getLog().setScrollY(ui.getLog().getScrollY() + 20);
                    return true;
                }
                else if(amount == -1) {
                    ui.getLog().setScrollY(ui.getLog().getScrollY() - 20);
                    return true;
                }

                return false;
            }
        });
        //</editor-fold>


        mux = new InputMultiplexer(ui.getStage(), stage);

        start();

    }

    public void start() {

        game.start();

        //Add actors to stage
        root.add(game.getLevel().getActor()).width(stage.getWidth()).height(game.getLevel().getHeight() * Main.getTILE_SIZE());

        ui.getHp().setText(String.format(Locale.getDefault(), "%d/%d", game.getPlayer().getHP(), game.getPlayer().getMaxHP()));

    }

    @Override
    public void render(float delta) {

        super.render(delta);
        Gdx.gl.glClearColor(0, 0, 0, 255);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(Gdx.graphics.getDeltaTime());
        ui.act(Gdx.graphics.getDeltaTime());

        if(game.getPlayer().isDead()) game.setScreen(game.getLoseScreen());

        //Set the camera to the player. If the player is too close to the edge of the screen, stop following
        floatPoint cameraLocation = getCameraLocation();
        stage.getCamera().position.set(cameraLocation.getX() * Main.getTILE_SIZE(), (cameraLocation.getY() * Main.getTILE_SIZE()), 0);
        stage.getCamera().update();

        game.getBatch().setProjectionMatrix(camera.combined);
        stage.draw();

        game.getBatch().setProjectionMatrix(ui.getViewport().getCamera().combined);

        ui.getStage().draw();
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        viewport.update(width, height);
    }

    @Override
    public void show() {
        if(game.getPlayer().getTurnsToProcess() > 0) game.getPlayer().processTurns();
        Gdx.input.setInputProcessor(mux);
    }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }

    public floatPoint getCameraLocation() {

        float width = viewport.getScreenWidth();
        float height = viewport.getScreenHeight();

        floatPoint p;
        if(game.getPlayer().getCursor().isActive() && game.getPlayer().getCursor().isFollow())
            p = new floatPoint(game.getPlayer().getCursor().getX(), game.getPlayer().getCursor().getY() + ((height/ Main.getTILE_SIZE())/5f));
        else
            p = new floatPoint(game.getPlayer().getX(), game.getPlayer().getY() + ((height/ Main.getTILE_SIZE())/5f));

        //If player's location isn't at least half a screen from the left edge, set the camera to half a screen from the left edge
        if(p.getX() * Main.getTILE_SIZE() < width/2)
            p.setX(width/(2 * Main.getTILE_SIZE()));
        //If player's location isn't at least half a screen from the right edge, set the camera to half a screen from the right edge
        else if((game.getLevel().getWidth() - p.getX()) * Main.getTILE_SIZE() < width/2d)
            p.setX((game.getLevel().getWidth()) - width/(2 * Main.getTILE_SIZE()));

        //If player's location isn't at least half a screen from the bottom edge, set the camera to half a screen from the bottom edge
        if(p.getY() * Main.getTILE_SIZE() < (height/2 - 2))
            p.setY(height/(2 * Main.getTILE_SIZE()));
        //If player's location isn't at least half a screen from the top edge, set the camera to half a screen from the top edge
        else if((game.getLevel().getHeight() - p.getY()) * Main.getTILE_SIZE() < (3f * height/5f)/2d)
            p.setY((game.getLevel().getHeight()) - (3 * height/5)/(2 * Main.getTILE_SIZE()));

        return p;
    }

    //<editor-fold desc="Getters and Setters">
    public Main getGame() {
        return game;
    }

    public void setGame(Main game) {
        this.game = game;
    }

    public InputMultiplexer getMux() {
        return mux;
    }

    public void setMux(InputMultiplexer mux) {
        this.mux = mux;
    }

    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public Table getRoot() {
        return root;
    }

    public void setRoot(Table root) {
        this.root = root;
    }

    public Viewport getViewport() {
        return viewport;
    }

    public void setViewport(Viewport viewport) {
        this.viewport = viewport;
    }

    public Camera getCamera() {
        return camera;
    }

    public void setCamera(Camera camera) {
        this.camera = camera;
    }

    public UI getUi() {
        return ui;
    }

    public void setUi(UI ui) {
        this.ui = ui;
    }

    public InventoryScreen getInventoryScreen() {
        return inventoryScreen;
    }

    public void setInventoryScreen(InventoryScreen inventoryScreen) {
        this.inventoryScreen = inventoryScreen;
    }

    public boolean isShift() {
        return shift;
    }

    public void setShift(boolean shift) {
        this.shift = shift;
    }

    //</editor-fold>
}
