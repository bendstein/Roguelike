package prefabbuilder;

import actors.creatures.CreatureActor;
import actors.world.LevelActor;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import creatureitem.Creature;
import creatureitem.generation.CreatureItemFactory;
import game.Main;
import utility.Utility;
import world.Level;
import world.Tile;
import world.generation.LevelFactory;
import world.geometry.Cursor;
import world.geometry.Point;
import world.geometry.floatPoint;
import world.thing.Thing;

public class PrefabBuilderScreen extends ScreenAdapter {

    private PrefabBuilderMain game;

    /**
     * The input multiplexer
     */
    private InputMultiplexer mux;

    private SpriteBatch batch;

    /**
     * Variables relating to the main stage
     */
    private Stage stage;
    private Table root;
    private Viewport viewport;
    private Camera camera;

    private PrefabUI ui;

    /**
     * Level for the prefab we're building
     */
    private Level level;
    private PrefabBuilderActor level_actor;

    /**
     * Level factory for generating levels
     */
    private LevelFactory factory;

    /**
     * If the shift key is held down, true
     */
    private boolean shift = false;

    private Cursor cursor;

    private int direction = 5;
    private long lastMove = System.currentTimeMillis();

    /**
     * The tile currently under the cursor
     */
    private Object current;
    private Object currentRight;
    private floatPoint currentPoint;

    private Point prevPoint;
    private boolean rightClick;
    private boolean middleClick;
    private boolean leftClick;
    private boolean fill;

    public PrefabBuilderScreen(PrefabBuilderMain game) {
        this.game = game;
        this.factory = new LevelFactory(game.getDEFAULT_LEVEL_WIDTH(), game.getDEFAULT_LEVEL_HEIGHT(), game.getRandom());
        this.batch = game.getBatch();
        //Set up game stage
        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        viewport = new ScreenViewport(camera);
        stage = new Stage(viewport, game.getBatch());

        root = new Table();
        root.setFillParent(true);
        stage.addActor(root);

        ui = new PrefabUI(game);
        ui.init();

        rightClick = leftClick = middleClick = false;
        fill = false;

        stage.addListener(new InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                switch (keycode) {
                    case Input.Keys.NUMPAD_7: {
                        moveCursor(-1, 1);
                        lastMove = System.currentTimeMillis();
                        direction = keycode;
                        return true;
                    }
                    case Input.Keys.NUMPAD_8: {
                        moveCursor(0, 1);
                        lastMove = System.currentTimeMillis();
                        direction = keycode;
                        return true;
                    }
                    case Input.Keys.NUMPAD_9: {
                        moveCursor(1, 1);
                        lastMove = System.currentTimeMillis();
                        direction = keycode;
                        return true;
                    }
                    case Input.Keys.NUMPAD_4: {
                        moveCursor(-1, 0);
                        lastMove = System.currentTimeMillis();
                        direction = keycode;
                        return true;
                    }
                    case Input.Keys.NUMPAD_6: {
                        moveCursor(1, 0);
                        lastMove = System.currentTimeMillis();
                        direction = keycode;
                        return true;
                    }
                    case Input.Keys.NUMPAD_1: {
                        moveCursor(-1, -1);
                        lastMove = System.currentTimeMillis();
                        direction = keycode;
                        return true;
                    }
                    case Input.Keys.NUMPAD_2: {
                        moveCursor(0, -1);
                        lastMove = System.currentTimeMillis();
                        direction = keycode;
                        return true;
                    }
                    case Input.Keys.NUMPAD_3: {
                        moveCursor(1, -1);
                        lastMove = System.currentTimeMillis();
                        direction = keycode;
                        return true;
                    }
                    default: {
                        return false;
                    }
                }
            }

            @Override
            public boolean keyUp(InputEvent event, int keycode) {
                switch (keycode) {
                    case Input.Keys.NUMPAD_7:
                    case Input.Keys.NUMPAD_8:
                    case Input.Keys.NUMPAD_9:
                    case Input.Keys.NUMPAD_4:
                    case Input.Keys.NUMPAD_6:
                    case Input.Keys.NUMPAD_1:
                    case Input.Keys.NUMPAD_2:
                    case Input.Keys.NUMPAD_3: {
                        direction = Input.Keys.NUMPAD_5;
                        return true;
                    }
                    case Input.Keys.PLUS: {
                        if(((OrthographicCamera)stage.getCamera()).zoom - 0.05 >= 0)
                            ((OrthographicCamera)stage.getCamera()).zoom -= 0.05f;
                        //System.out.println(((OrthographicCamera)stage.getCamera()).zoom);
                        return true;
                    }
                    case Input.Keys.MINUS: {
                        if(((OrthographicCamera)stage.getCamera()).zoom + 0.05 <= 4)
                            ((OrthographicCamera)stage.getCamera()).zoom += 0.05f;
                        //System.out.println(((OrthographicCamera)stage.getCamera()).zoom);
                        return true;
                    }
                    default: {
                        return false;
                    }
                }
            }

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if(viewport.project(new Vector2(x, y)).y > 0.8 * viewport.getScreenHeight()) return false;
                switch (button) {
                    case Input.Buttons.LEFT: {
                        Point c = Utility.roundCursor(x, y);
                        if(level.isOutOfBounds(c.getX(), c.getY()))
                            return true;
                        leftClick = true;
                        placeCurrentAt(c.getX(), c.getY());
                        return true;
                    }
                    case Input.Buttons.MIDDLE: {
                        prevPoint = Utility.roundCursor(x, y);
                        middleClick = true;
                        fill = true;
                        return true;
                    }
                    case Input.Buttons.RIGHT: {
                        prevPoint = Utility.roundCursor(x, y);
                        rightClick = true;
                        fill = false;
                        return true;
                    }
                    default: {
                        return false;
                    }
                }
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                if(viewport.project(new Vector2(x, y)).y > 0.8 * viewport.getScreenHeight()) return;
                switch (button) {
                    case Input.Buttons.LEFT: {
                        leftClick = false;
                        break;
                    }
                    case Input.Buttons.MIDDLE: {
                        middleClick = false;

                        Point c = Utility.roundCursor(currentPoint.getX(), currentPoint.getY());
                        Point c1 = Utility.roundCursor(currentPoint.getX(), currentPoint.getY());

                        if(c.equals(prevPoint)) {
                            break;
                        }

                        int x0 = c.getX(), x1 = prevPoint.getX(), y0 = c.getY(), y1 = prevPoint.getY();
                        c1.setLocation(Math.min(x0, x1), Math.min(y0, y1));
                        c.setLocation(Math.max(x0, x1), Math.max(y0, y1));

                        int maxx = Math.min(level.getWidth() - 1, c.getX()), maxy = Math.min(level.getHeight() - 1, c.getY());
                        int minx = Math.max(0, c1.getX()), miny = Math.max(0, c1.getY());
                        for(int i = minx; i <= maxx; i++) {
                            for(int j = miny; j <= maxy; j++) {
                                if(fill)
                                    placeCurrentAt(i, j);
                                else if(i == minx || i == maxx || j == miny || j == maxy)
                                    placeCurrentAt(i, j);
                            }
                        }

                        fill = false;
                        break;
                    }
                    case Input.Buttons.RIGHT: {
                        rightClick = false;

                        Point c = Utility.roundCursor(currentPoint.getX(), currentPoint.getY());
                        Point c1 = Utility.roundCursor(currentPoint.getX(), currentPoint.getY());

                        if(c.equals(prevPoint)) {
                            erase(c.getX(), c.getY());
                            break;
                        }

                        int x0 = c.getX(), x1 = prevPoint.getX(), y0 = c.getY(), y1 = prevPoint.getY();
                        c1.setLocation(Math.min(x0, x1), Math.min(y0, y1));
                        c.setLocation(Math.max(x0, x1), Math.max(y0, y1));

                        int maxx = Math.min(level.getWidth() - 1, c.getX()), maxy = Math.min(level.getHeight() - 1, c.getY());
                        int minx = Math.max(0, c1.getX()), miny = Math.max(0, c1.getY());
                        for(int i = minx; i <= maxx; i++) {
                            for(int j = miny; j <= maxy; j++) {
                                if(fill)
                                    placeCurrentAt(i, j);
                                else if(i == minx || i == maxx || j == miny || j == maxy)
                                    placeCurrentAt(i, j);
                            }
                        }

                        break;
                    }
                    default: {
                        break;
                    }
                }
            }

            @Override
            public boolean mouseMoved(InputEvent event, float x, float y) {
                Gdx.graphics.requestRendering();
                return true;
            }
        });
        ui.getStage().addListener(new InputListener() {

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return super.touchDown(event, x, y, pointer, button);
            }

            @Override
            public boolean mouseMoved(InputEvent event, float x, float y) {
                return super.mouseMoved(event, x, y);

            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                super.exit(event, x, y, pointer, toActor);
                if(toActor == null) ui.getStage().setScrollFocus(null);
                else if((toActor.equals(ui.getTileSelect()) || toActor.equals(ui.getInnerTileSelectTable()) ||
                        toActor.equals(ui.getOuterTileSelectTable())) &&
                        y > (4f * ui.getViewport().getScreenHeight()/5f))
                    ui.getStage().setScrollFocus(ui.getTileSelect());
                else if((toActor.equals(ui.getThingSelect()) || toActor.equals(ui.getInnerThingSelectTable()) ||
                        toActor.equals(ui.getOuterThingSelectTable())) &&
                        y > (4f * ui.getViewport().getScreenHeight()/5f))
                    ui.getStage().setScrollFocus(ui.getThingSelect());
                else if((toActor.equals(ui.getCreatureSelect()) || toActor.equals(ui.getInnerCreatureSelectTable()) ||
                        toActor.equals(ui.getOuterCreatureSelectTable())) &&
                        y > (4f * ui.getViewport().getScreenHeight()/5f))
                    ui.getStage().setScrollFocus(ui.getCreatureSelect());
            }

            @Override
            public boolean scrolled(InputEvent event, float x, float y, int amount) {
                return super.scrolled(event, x, y, amount);
            }

            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                return super.keyDown(event, keycode);
            }

            @Override
            public boolean keyUp(InputEvent event, int keycode) {
                return super.keyUp(event, keycode);
            }
        });

        mux = new InputMultiplexer(stage, ui.getStage());

        start();

    }

    public void start() {
        this.level = factory.fill(Tile.BOUNDS).build();
        level.setSeenAll();
        this.level_actor = new PrefabBuilderActor(level);
        root.add(level_actor)
                .width(stage.getWidth())
                .height(level.getHeight() * Main.getTileHeight());

        this.cursor = new Cursor(level.getWidth() / Main.getTileWidth(),level.getHeight() / Main.getTileHeight());
        cursor.setActive(true);

        switchCurrent(Tile.WALL);
        switchCurrentRight(Tile.FLOOR);
        currentPoint = new floatPoint(0, 0);
        prevPoint = new Point(0, 0);

        Gdx.graphics.requestRendering();
    }

    public void act(float delta) {

        /*
         * Get mouse cursor
         */

        Vector3 pos = viewport.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
        currentPoint.setX(pos.x);
        currentPoint.setY(pos.y);

        /*
         * Move keyboard cursor
         */
        if(System.currentTimeMillis() - lastMove > 50L && direction != Input.Keys.NUMPAD_5) {
            switch (direction) {
                case Input.Keys.NUMPAD_7: {
                    moveCursor(-1, 1);
                    lastMove = System.currentTimeMillis();
                    break;
                }
                case Input.Keys.NUMPAD_8: {
                    moveCursor(0, 1);
                    lastMove = System.currentTimeMillis();
                    break;
                }
                case Input.Keys.NUMPAD_9: {
                    moveCursor(1, 1);
                    lastMove = System.currentTimeMillis();
                    break;
                }
                case Input.Keys.NUMPAD_4: {
                    moveCursor(-1, 0);
                    lastMove = System.currentTimeMillis();
                    break;
                }
                case Input.Keys.NUMPAD_6: {
                    moveCursor(1, 0);
                    lastMove = System.currentTimeMillis();
                    break;
                }
                case Input.Keys.NUMPAD_1: {
                    moveCursor(-1, -1);
                    lastMove = System.currentTimeMillis();
                    break;
                }
                case Input.Keys.NUMPAD_2: {
                    moveCursor(0, -1);
                    lastMove = System.currentTimeMillis();
                    break;
                }
                case Input.Keys.NUMPAD_3: {
                    moveCursor(1, -1);
                    lastMove = System.currentTimeMillis();
                    break;
                }
                default: {
                    break;
                }
            }
        }

        if(leftClick) {
            Point c = Utility.roundCursor(pos.x, pos.y);
            if(!level.isOutOfBounds(c.getX(), c.getY())) {
                placeCurrentAt(c.getX(), c.getY());
            }
        }

        ui.act(delta);

        if(ui.getSelected() != null && !current.equals(ui.getSelected())) {
            switchCurrent(ui.getSelected());
            ui.setSelected(null);
        }

        if(ui.getSelectedRight() != null && !currentRight.equals(ui.getSelectedRight())) {
            switchCurrent(ui.getSelectedRight());
            ui.setSelectedRight(null);
        }
    }

    public void draw() {
        batch.begin();
        if(current != null && Gdx.input.getY() > viewport.getScreenHeight()/5f) {
            if(!rightClick) {

                if(current instanceof Tile)
                    batch.draw(((Tile) current).getSprite(((Tile) current).getNeutral()), Gdx.input.getX() - Main.getTileWidth()/2f,
                            (viewport.getScreenHeight() - Main.getTileHeight()/2f) - Gdx.input.getY());
                else if(current instanceof Creature)
                    batch.draw(((Creature) current).getTexture(), Gdx.input.getX() - Main.getTileWidth()/2f,
                            (viewport.getScreenHeight() - Main.getTileHeight()/2f) - Gdx.input.getY());
                else if(current instanceof Thing)
                    batch.draw(((Thing)current).getTile().getSprite(((Thing) current).getTile().getNeutral()), (currentPoint.getX() * Main.getTileWidth()) - Main.getTileWidth()/2f,
                            (viewport.getScreenHeight() - Main.getTileHeight()/2f) - (currentPoint.getY() * Main.getTileHeight()));
            }
            else {
                Point c = Utility.roundCursor(currentPoint.getX(), currentPoint.getY());
                Point c1 = Utility.roundCursor(currentPoint.getX(), currentPoint.getY());

                int x0 = c.getX(), x1 = prevPoint.getX(), y0 = c.getY(), y1 = prevPoint.getY();
                c1.setLocation(Math.min(x0, x1), Math.min(y0, y1));
                c.setLocation(Math.max(x0, x1), Math.max(y0, y1));

                int maxx = Math.min(level.getWidth() - 1, c.getX()), maxy = Math.min(level.getHeight() - 1, c.getY());
                int minx = Math.max(0, c1.getX()), miny = Math.max(0, c1.getY());

                for(int i = minx; i <= maxx; i++) {
                    for(int j = miny; j <= maxy; j++) {
                        if(fill)
                            if(current instanceof Tile)
                                batch.draw(((Tile) current).getSprite(((Tile) current).getNeutral()), (i * Main.getTileWidth() + (28 * Main.getTileWidth())),
                                        (j * Main.getTileHeight()) + (8 * Main.getTileHeight()));
                            else if(current instanceof Creature)
                                batch.draw(((Creature) current).getTexture(), (i * Main.getTileWidth() + (28 * Main.getTileWidth())),
                                        (j * Main.getTileHeight()) + (8 * Main.getTileHeight()));
                            else if(current instanceof Thing)
                                batch.draw(((Thing)current).getTile().getSprite(((Thing) current).getTile().getNeutral()), (i * Main.getTileWidth() + (28 * Main.getTileWidth())),
                                        (j * Main.getTileHeight()) + (8 * Main.getTileHeight()));
                        else if(i == minx || i == maxx || j == miny || j == maxy)
                                if(current instanceof Tile)
                                    batch.draw(((Tile) current).getSprite(((Tile) current).getNeutral()), (i * Main.getTileWidth() + (28 * Main.getTileWidth())),
                                            (j * Main.getTileHeight()) + (8 * Main.getTileHeight()));
                                else if(current instanceof Creature)
                                    batch.draw(((Creature) current).getTexture(), (i * Main.getTileWidth() + (28 * Main.getTileWidth())),
                                            (j * Main.getTileHeight()) + (8 * Main.getTileHeight()));
                                else if(current instanceof Thing)
                                    batch.draw(((Thing)current).getTile().getSprite(((Thing) current).getTile().getNeutral()), (i * Main.getTileWidth() + (28 * Main.getTileWidth())),
                                            (j * Main.getTileHeight()) + (8 * Main.getTileHeight()));
                    }

                }

            }
        }

        batch.end();
    }

    @Override
    public void render(float delta) {
        super.render(delta);
        Gdx.gl.glClearColor(0, 0, 0, 255);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        act(delta);

        floatPoint cameraLocation = getCameraLocation();
        stage.getCamera().position.set(cameraLocation.getX() * Main.getTileWidth(), (cameraLocation.getY() * Main.getTileHeight()), 0);
        stage.getCamera().update();

        batch.setProjectionMatrix(camera.combined);

        stage.draw();

        game.getBatch().setProjectionMatrix(ui.getViewport().getCamera().combined);

        ui.getStage().draw();

        draw();

    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        viewport.update(width, height);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(mux);
        Gdx.graphics.requestRendering();
    }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }

    public void switchListener(String s) {
        switch (s) {
            default: {
                Gdx.input.setInputProcessor(mux);
                break;
            }
        }
    }

    public void moveCursor(int mx, int my) {
        if(cursor.getX() + mx < 0 || cursor.getX() + mx >= level.getWidth() ||
                cursor.getY() + my < 0 || cursor.getY() + my >= level.getHeight()) return;
        cursor.moveBy(mx, my);
        Gdx.graphics.requestRendering();
    }

    public void placeCurrentAt(int x, int y) {
        placeAt(current, x, y);
    }

    public void erase(int x, int y) {
        Object o = null;
        if(currentRight instanceof Creature) {
            o = level.removeCreature(x, y);
        }
        if(o == null || currentRight instanceof Thing) {
            o = level.removeThing(x, y);
        }
        if(o == null || currentRight instanceof Tile) {
            placeAt(currentRight, x, y);
        }

        Gdx.graphics.requestRendering();
    }

    public void switchCurrent(Object o) {
        current = o;
        Gdx.graphics.requestRendering();
    }

    public void switchCurrentRight(Object o) {
        currentRight = o;
        Gdx.graphics.requestRendering();
    }

    public void placeAt(Object o, int x, int y) {
        if(o instanceof Tile) {
            level.setTileAt(x, y, (Tile)o);

            if(!level.isPassable(x, y)) {
                level.removeCreature(x, y);
                level.removeThing(x, y);
            }
        }
        else if(o instanceof Creature) {
            level.removeCreature(x, y);
            Creature c = (Creature)o;

            CreatureItemFactory.newActor(c).copy();
            CreatureItemFactory.newAi(c).copy();
            level.addAtIgnoreQueue(x, y, new Creature(c));
        }
        else if(o instanceof Thing) {
            level.removeThing(x, y);
            level.addAt(x, y, (Thing)o);
        }

        Gdx.graphics.requestRendering();
    }

    public floatPoint getCameraLocation() {
        return new floatPoint(cursor.getX(), cursor.getY() + ((viewport.getScreenHeight()/(float)Main.getTileHeight())/5f));
    }
}
