package screens;

import actors.creatures.CreatureActor;
import actors.ui.DialogueUI;
import actors.ui.UI;
import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import creatureitem.Creature;
import creatureitem.ai.types.NPCAi;
import creatureitem.ai.PlayerAi;
import creatureitem.item.Inventory;
import game.Main;
import utility.Utility;
import world.Level;
import world.geometry.Point;
import world.geometry.floatPoint;
import world.thing.*;

import java.util.ArrayList;
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
    private DialogueUI dialogueUI;

    /**
     * If the shift key is held down, true
     */
    private boolean shift = false;

    /**
     * If the player currently can see dialogue, should be true
     */
    private boolean dialogueVisible = false;

    /**
     * 1 if scrolling up, 0 if not scrolling, -1 if scrolling down
     */
    private int scrolling = 0;

    /**
     * Rate at which we accept continuous scrolling
     */
    private long scrollRate;

    /**
     * Time of last scroll
     */
    private long lastScroll;

    public PlayScreen(Main game) {
        this.game = game;

        ui = new UI(game);

        dialogueUI = new DialogueUI(game);

        //Set up game stage
        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        viewport = new ScreenViewport(camera);
        stage = new Stage(viewport, game.getBatch());

        root = new Table();
        root.setFillParent(true);
        stage.addActor(root);

        lastScroll = System.currentTimeMillis();
        scrollRate = 100L;

        stage.addListener(new InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                try {
                    switch (keycode) {
                        case Input.Keys.NUMPAD_7: {
                            game.getPlayer().setCurrentDestination(null);
                            game.getPlayer().getDestinationQueue().clear();
                            game.getPlayer().setMoveDirection(keycode);
                            if(game.getPlayer().getCursor().isActive() || game.getPlayer().isAllowed_to_act()) game.getPlayer().moveBy(-1, 1);
                            return true;
                        }
                        case Input.Keys.NUMPAD_8: {
                            game.getPlayer().setCurrentDestination(null);
                            game.getPlayer().getDestinationQueue().clear();
                            game.getPlayer().setMoveDirection(keycode);
                            if(game.getPlayer().getCursor().isActive() || game.getPlayer().isAllowed_to_act()) game.getPlayer().moveBy(0, 1);
                            return true;
                        }
                        case Input.Keys.NUMPAD_9: {
                            game.getPlayer().setCurrentDestination(null);
                            game.getPlayer().getDestinationQueue().clear();
                            game.getPlayer().setMoveDirection(keycode);
                            if(game.getPlayer().getCursor().isActive() || game.getPlayer().isAllowed_to_act()) game.getPlayer().moveBy(1, 1);
                            return true;
                        }
                        case Input.Keys.NUMPAD_4: {
                            game.getPlayer().setCurrentDestination(null);
                            game.getPlayer().getDestinationQueue().clear();
                            game.getPlayer().setMoveDirection(keycode);
                            if(game.getPlayer().getCursor().isActive() || game.getPlayer().isAllowed_to_act()) game.getPlayer().moveBy(-1, 0);
                            return true;
                        }
                        case Input.Keys.NUMPAD_5: {
                            game.getPlayer().setCurrentDestination(null);
                            game.getPlayer().getDestinationQueue().clear();
                            game.getPlayer().setMoveDirection(keycode);
                            if(game.getPlayer().getCursor().isActive() || game.getPlayer().isAllowed_to_act()) game.getPlayer().moveBy(0, 0);
                            return true;
                        }
                        case Input.Keys.NUMPAD_6: {
                            game.getPlayer().setCurrentDestination(null);
                            game.getPlayer().getDestinationQueue().clear();
                            game.getPlayer().setMoveDirection(keycode);
                            if(game.getPlayer().getCursor().isActive() || game.getPlayer().isAllowed_to_act()) game.getPlayer().moveBy(1, 0);
                            return true;
                        }
                        case Input.Keys.NUMPAD_1: {
                            game.getPlayer().setCurrentDestination(null);
                            game.getPlayer().getDestinationQueue().clear();
                            game.getPlayer().setMoveDirection(keycode);
                            if(game.getPlayer().getCursor().isActive() || game.getPlayer().isAllowed_to_act()) game.getPlayer().moveBy(-1, -1);
                            return true;
                        }
                        case Input.Keys.NUMPAD_2: {
                            game.getPlayer().setCurrentDestination(null);
                            game.getPlayer().getDestinationQueue().clear();
                            game.getPlayer().setMoveDirection(keycode);
                            if(game.getPlayer().getCursor().isActive() || game.getPlayer().isAllowed_to_act()) game.getPlayer().moveBy(0, -1);
                            return true;
                        }
                        case Input.Keys.NUMPAD_3: {
                            game.getPlayer().setCurrentDestination(null);
                            game.getPlayer().getDestinationQueue().clear();
                            game.getPlayer().setMoveDirection(keycode);
                            if(game.getPlayer().getCursor().isActive() || game.getPlayer().isAllowed_to_act()) game.getPlayer().moveBy(1, -1);
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

                            if(!game.getPlayer().isAllowed_to_act()) return true;

                            if(!shift) {
                                Inventory in = game.getPlayer().getLevel().getInventoryAt(game.getPlayer().getX(), game.getPlayer().getY());
                                //if(in == null) return true;
                                if(in.getCount() <= 1) game.getPlayer().pickUp();
                                else {
                                    game.getInventoryScreen().setInventory(in);
                                    game.getInventoryScreen().setCurrentVerb("pickup");
                                    game.getInventoryScreen().setCurrentFilter("");
                                    game.setScreen(game.getInventoryScreen());
                                }

                            }
                            else {
                                shift = false;
                                Thing t = game.getLevel().getThingAt(game.getPlayer().getX(), game.getPlayer().getY());
                                if(t instanceof Stairs && ((Stairs) t).isUp()) {
                                    boolean stairsUsed = t.interact(game.getPlayer());
                                    if(stairsUsed) {
                                        ui.initMinimap();
                                    }
                                }
                            }
                            return true;
                        }
                        case Input.Keys.PERIOD: {
                            game.getPlayer().setCurrentDestination(null);
                            game.getPlayer().getDestinationQueue().clear();

                            if(!game.getPlayer().isAllowed_to_act()) return true;

                            if(!shift){}
                            else {
                                shift = false;
                                Thing t = game.getLevel().getThingAt(game.getPlayer().getX(), game.getPlayer().getY());
                                if(t instanceof Stairs && !((Stairs) t).isUp()) {
                                    boolean stairsUsed = t.interact(game.getPlayer());
                                    if(stairsUsed) {
                                        ui.initMinimap();
                                    }
                                }
                            }
                            return true;
                        }
                        case Input.Keys.SHIFT_RIGHT:
                        case Input.Keys.SHIFT_LEFT: {
                            shift = true;
                            return true;
                        }
                        case Input.Keys.L: {
                            if(!shift) {
                                game.getPlayer().setCurrentDestination(null);
                                game.getPlayer().getDestinationQueue().clear();
                                game.getPlayer().prepLook();
                            }
                            return true;
                        }
                        case Input.Keys.M: {
                            if(!shift) {
                                if(game.getPlayer().getCursor().isActive()) {

                                    if(game.getPlayer().getCursor().getPurpose().equals("look") || game.getPlayer().getCursor().getPurpose().equals("move")) {
                                        if(game.getPlayer().getSeen(game.getPlayer().getCursor().getX(), game.getPlayer().getCursor().getY())) {
                                            //game.getPlayer().getCursor().setActive(false);
                                            game.getPlayer().performCursorAction();
                                            //game.getPlayer().enqueueDestination(game.getPlayer().getCursor());
                                        }
                                    }
                                }
                                else {
                                    game.getPlayer().setCurrentDestination(null);
                                    game.getPlayer().getDestinationQueue().clear();
                                    game.getPlayer().prepMove();
                                }
                            }

                            return true;
                        }
                        case Input.Keys.I: {

                            if(!game.getPlayer().isAllowed_to_act()) return true;

                            if(!shift) {
                                game.getPlayer().setCurrentDestination(null);
                                game.getPlayer().getDestinationQueue().clear();
                                game.getInventoryScreen().setInventory(game.getPlayer().getInventory());
                                game.getInventoryScreen().setCurrentVerb("");
                                game.getInventoryScreen().setCurrentFilter("");
                                game.setScreen(game.getInventoryScreen());
                            }

                            return true;
                        }
                        case Input.Keys.D: {

                            if(!game.getPlayer().isAllowed_to_act()) return true;

                            if(!shift) {
                                game.getPlayer().setCurrentDestination(null);
                                game.getPlayer().getDestinationQueue().clear();
                                game.getInventoryScreen().setInventory(game.getPlayer().getInventory());
                                game.getInventoryScreen().setCurrentVerb("drop");
                                game.getInventoryScreen().setCurrentFilter("");
                                game.setScreen(game.getInventoryScreen());
                            }

                            return true;
                        }
                        case Input.Keys.T: {
                            if(!shift) {
                                game.getPlayer().setCurrentDestination(null);
                                game.getPlayer().getDestinationQueue().clear();

                                if(!game.getPlayer().isAllowed_to_act()) return true;

                                if(!game.getPlayer().getCursor().isActive()) {
                                    game.getInventoryScreen().setInventory(game.getPlayer().getInventory());
                                    game.getInventoryScreen().setCurrentVerb("throw");
                                    game.getInventoryScreen().setCurrentFilter("");
                                    game.setScreen(game.getInventoryScreen());
                                }
                                else if(game.getPlayer().getCursor().getPurpose().equals("throw")) {
                                    game.getPlayer().throwItem();
                                    game.getPlayer().deactivateCursor();
                                }
                                else if(game.getPlayer().getCursor().getPurpose().equals("shoot")) {
                                    game.getPlayer().shoot();
                                    game.getPlayer().deactivateCursor();
                                }

                            }
                            else {
                                game.getPlayer().setCurrentDestination(null);
                                game.getPlayer().getDestinationQueue().clear();
                                shift = false;

                                if(!game.getPlayer().getCursor().isActive()) {
                                    if(game.getPlayer().getRangedWeaponSlot().isEmpty()) {

                                        if(!game.getPlayer().isAllowed_to_act()) return true;

                                        game.getInventoryScreen().setInventory(game.getPlayer().getInventory());
                                        game.getInventoryScreen().setCurrentVerb("equip");
                                        game.getInventoryScreen().setCurrentFilter(String.format(Locale.getDefault(), "%d", game.getPlayer().getRangedWeaponSlot().getSlot().getSlot()));
                                        game.getInventoryScreen().setToEquip(game.getPlayer().getRangedWeaponSlot());
                                        game.getInventoryScreen().setReturnScreen(null);
                                        game.setScreen(game.getInventoryScreen());
                                    }
                                    else if(game.getPlayer().getQuiverSlot().isEmpty()) {

                                        if(!game.getPlayer().isAllowed_to_act()) return true;

                                        game.getInventoryScreen().setInventory(game.getPlayer().getInventory());
                                        game.getInventoryScreen().setCurrentVerb("equip");
                                        game.getInventoryScreen().setCurrentFilter(String.format(Locale.getDefault(), "%d", game.getPlayer().getQuiverSlot().getSlot().getSlot()));
                                        game.getInventoryScreen().setToEquip(game.getPlayer().getQuiverSlot());
                                        game.getInventoryScreen().setReturnScreen(null);
                                        game.setScreen(game.getInventoryScreen());
                                    }
                                    if(game.getPlayer().getQuiverSlot().isEquipped()) game.getPlayer().prepShoot();
                                }
                                else if(game.getPlayer().getCursor().getPurpose().equals("shoot") ||
                                        game.getPlayer().getCursor().getPurpose().equals("throw")) {

                                    if(!game.getPlayer().isAllowed_to_act()) return true;

                                    game.getPlayer().performCursorAction();
                                }

                            }

                            return true;
                        }
                        case Input.Keys.A: {
                            if(!shift) {
                                game.getPlayer().setCurrentDestination(null);
                                game.getPlayer().getDestinationQueue().clear();

                                if(!game.getPlayer().isAllowed_to_act()) return true;

                                if(!game.getPlayer().getCursor().isActive()) {
                                    game.getPlayer().prepAttack();
                                }

                                else {
                                    game.getPlayer().performCursorAction();
                                }

                                return true;
                            }

                            return true;
                        }
                        case Input.Keys.Q: {

                            game.getPlayer().setCurrentDestination(null);
                            game.getPlayer().getDestinationQueue().clear();

                            if(!game.getPlayer().isAllowed_to_act()) return true;

                            if(!shift) {
                                game.getPlayer().setCurrentDestination(null);
                                game.getPlayer().getDestinationQueue().clear();
                                game.getInventoryScreen().setInventory(game.getPlayer().getInventory());
                                game.getInventoryScreen().setCurrentVerb("quaff");
                                game.getInventoryScreen().setCurrentFilter("quaff");
                                game.setScreen(game.getInventoryScreen());
                            }

                            return true;
                        }
                        case Input.Keys.E: {

                            game.getPlayer().setCurrentDestination(null);
                            game.getPlayer().getDestinationQueue().clear();

                            if(!game.getPlayer().isAllowed_to_act()) return true;

                            if(!shift) {
                                game.getInventoryScreen().setInventory(game.getPlayer().getInventory());
                                game.getInventoryScreen().setCurrentVerb("eat");
                                game.getInventoryScreen().setCurrentFilter("eat");
                                game.setScreen(game.getInventoryScreen());
                            }
                            else {
                                shift = false;
                                game.getEquipScreen().setSlots(game.getPlayer().getSlots());
                                game.setScreen(game.getEquipScreen());
                            }


                            return true;
                        }
                        case Input.Keys.U: {

                            game.getPlayer().setCurrentDestination(null);
                            game.getPlayer().getDestinationQueue().clear();

                            if(!game.getPlayer().isAllowed_to_act()) return true;

                            if(!shift) {
                                game.getPlayer().setCurrentDestination(null);
                                game.getPlayer().getDestinationQueue().clear();
                                game.getInventoryScreen().setInventory(game.getPlayer().getInventory());
                                game.getInventoryScreen().setCurrentVerb("use");
                                game.getInventoryScreen().setCurrentFilter("use");
                                game.setScreen(game.getInventoryScreen());
                            }
                            return true;
                        }
                        case Input.Keys.R: {

                            game.getPlayer().setCurrentDestination(null);
                            game.getPlayer().getDestinationQueue().clear();

                            if(!game.getPlayer().isAllowed_to_act()) return true;

                            if(!shift) {
                                game.getPlayer().setCurrentDestination(null);
                                game.getPlayer().getDestinationQueue().clear();
                                game.getInventoryScreen().setInventory(game.getPlayer().getInventory());
                                game.getInventoryScreen().setCurrentVerb("read");
                                game.getInventoryScreen().setCurrentFilter("read");
                                game.setScreen(game.getInventoryScreen());
                            }
                            return true;
                        }
                        case Input.Keys.O: {

                            if(!game.getPlayer().isAllowed_to_act()) return true;

                            if(!shift) {
                                game.getPlayer().setCurrentDestination(null);
                                game.getPlayer().getDestinationQueue().clear();
                                boolean opened = false;
                                for(int i = -1; i <= 1; i++) {
                                    for(int j = -1; j <= 1; j++) {
                                        Thing t = game.getLevel().getThingAt(game.getPlayer().getX() + i, game.getPlayer().getY() + j);
                                        if(t != null && t.getBehavior() instanceof DoorBehavior && !t.isOpen()) {
                                            if(i == 0 && j == 0) continue;
                                            t.interact(game.getPlayer());
                                            opened = true;
                                            break;
                                        }
                                        else if(game.getLevel().getThingAt(game.getPlayer().getX() + i, game.getPlayer().getY() + j) instanceof Light &&
                                                !((Light) game.getLevel().getThingAt(game.getPlayer().getX() + i, game.getPlayer().getY() + j)).isActive()) {
                                            game.getLevel().getThingAt(game.getPlayer().getX() + i, game.getPlayer().getY() + j).interact(game.getPlayer());
                                            opened = true;
                                            break;
                                        }
                                    }

                                    if(opened) break;
                                }
                            }
                            return true;
                        }
                        case Input.Keys.C: {

                            if(!game.getPlayer().isAllowed_to_act()) return true;

                            if(!shift) {
                                game.getPlayer().setCurrentDestination(null);
                                game.getPlayer().getDestinationQueue().clear();
                                boolean opened = false;
                                for(int i = -1; i <= 1; i++) {
                                    for(int j = -1; j <= 1; j++) {
                                        Thing t = game.getLevel().getThingAt(game.getPlayer().getX() + i, game.getPlayer().getY() + j);
                                        if(t != null && t.getBehavior() instanceof DoorBehavior && t.isOpen()) {
                                            if(i == 0 && j == 0) continue;
                                            t.interact(game.getPlayer());
                                            opened = true;
                                            break;
                                        }
                                        else if(game.getLevel().getThingAt(game.getPlayer().getX() + i, game.getPlayer().getY() + j) instanceof Light &&
                                                ((Light) game.getLevel().getThingAt(game.getPlayer().getX() + i, game.getPlayer().getY() + j)).isActive()) {
                                            game.getLevel().getThingAt(game.getPlayer().getX() + i, game.getPlayer().getY() + j).interact(game.getPlayer());
                                            opened = true;
                                            break;
                                        }
                                    }

                                    if(opened) break;
                                }
                            }
                            else {
                                game.getPlayer().setCurrentDestination(null);
                                game.getPlayer().getDestinationQueue().clear();
                                shift = false;
                                boolean found = false;

                                for(int i = -1; i <= 1; i++) {
                                    for(int j = -1; j <= 1; j++) {
                                        Creature c = game.getLevel().getCreatureAt(game.getPlayer().getX() + i, game.getPlayer().getY() + j);
                                        if(c != null && c.getAi() instanceof NPCAi && ((NPCAi)c.getAi()).getDialogueRoot() != null) {
                                            if(i == 0 && j == 0) continue;
                                            game.getPlayer().talkTo(c);
                                            found = true;
                                            break;
                                        }
                                    }

                                    if(found) break;
                                }

                                if(found)
                                    startDialogue(((NPCAi)game.getPlayer().getTalkingTo().getAi()).getDialogueRoot());
                                else
                                    game.getPlayer().notify("You're not *that* lonely!");
                            }

                            return true;
                        }
                        case Input.Keys.Z: {

                            if(!game.getPlayer().isAllowed_to_act()) return true;

                            if(!shift) {
                                game.getPlayer().setCurrentDestination(null);
                                game.getPlayer().getDestinationQueue().clear();
                                if(game.getPlayer().getCursor().isActive() && game.getPlayer().getCursor().getPurpose().equals("zap")) {
                                    game.getPlayer().performCursorAction();
                                }
                            }
                            else {
                                game.getPlayer().setCurrentDestination(null);
                                game.getPlayer().getDestinationQueue().clear();
                                shift = false;
                                if(game.getPlayer().getCursor().isActive() && game.getPlayer().getCursor().getPurpose().equals("zap")) {
                                    game.getPlayer().performCursorAction();
                                }
                                else game.setScreen(game.getSpellScreen());
                            }

                            return true;
                        }
                        case Input.Keys.PLUS: {
                            if(((OrthographicCamera)stage.getCamera()).zoom - 0.05 >= 0)
                                ((OrthographicCamera)stage.getCamera()).zoom -= 0.05f;
                            return true;
                        }
                        case Input.Keys.MINUS: {
                            if(((OrthographicCamera)stage.getCamera()).zoom + 0.05 <= 4)
                                ((OrthographicCamera)stage.getCamera()).zoom += 0.05f;
                            return true;
                        }
                        case Input.Keys.ENTER: {
                            if(game.getPlayer().getCursor().isActive()) {
                                game.getPlayer().performCursorAction();
                            }
                            return true;
                        }
                    /*
                    case Input.Keys.Y: {

                        Dialogue d1;
                        Dialogue d0 = new Dialogue("Testing.", true, new String[]{"a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a",}, new Dialogue[]{});
                        Dialogue d = new Dialogue("Testing.", true, new String[]{"v", "Bye."}, new Dialogue[]{d0});
                        d1 = new Dialogue("B\nBj\nBjo\nBjor\nBjork", true, new String[]{"Bjork.", "Bjorky.", "Bye."}, new Dialogue[]{});

                        d1.setBranches(new Dialogue[]{d, d1});

                        startDialogue(d1);
                        return true;
                    }

                     */
                        default:
                            return false;
                    }
                } catch (Exception e) {
                    return true;
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
                     Point c = Utility.roundCursor(x, y);
                     if(!game.getPlayer().getCursor().isActive()) {

                         if(game.getPlayer().canSee(c) || game.getPlayer().getSeen(c.getX(), c.getY())) {
                             if(!shift) {
                                 game.getPlayer().setCurrentDestination(null);
                                 game.getPlayer().getDestinationQueue().clear();
                             }
                             game.getPlayer().enqueueDestination(c);
                         }

                     }
                     else {
                         if(c.equals(game.getPlayer().getCursor().point())) {
                             game.getPlayer().performCursorAction();
                             /*
                             if(game.getPlayer().getCursor().getPurpose().equals("look") ||
                                     game.getPlayer().getCursor().getPurpose().equals("move")) {
                                 if(game.getPlayer().canSee(c) || game.getPlayer().getSeen(c.getX(), c.getY())) {
                                     game.getPlayer().deactivateCursor();

                                     if(!shift) {
                                         game.getPlayer().setCurrentDestination(null);
                                         game.getPlayer().getDestinationQueue().clear();
                                     }
                                     game.getPlayer().enqueueDestination(c);
                                 }
                             }
                             else if(game.getPlayer().getCursor().getPurpose().equals("throw")) {
                                 game.getPlayer().throwItem();
                                 game.getPlayer().deactivateCursor();
                             }
                             else if(game.getPlayer().getCursor().getPurpose().equals("shoot")) {
                                 game.getPlayer().shoot();
                                 game.getPlayer().deactivateCursor();
                             }
                             else if(game.getPlayer().getCursor().getPurpose().equals("zap")) {
                                 game.getPlayer().cast();
                                 game.getPlayer().deactivateCursor();
                             }

                              */

                         }
                         else {
                             game.getPlayer().getCursor().setLocation(c.getX(), c.getY());

                             if(game.getPlayer().getCursor().getPurpose().equals("move")) {
                                 if(game.getPlayer().canSee(c) || game.getPlayer().getSeen(c.getX(), c.getY())) {
                                     game.getPlayer().getCursor().setHasLine(true);
                                     game.getPlayer().getCursor()
                                             .setPath(Utility.aStarPathToLine(Utility.aStarWithVision(game.getLevel().getCosts(), game.getPlayer(), Point.DISTANCE_MANHATTAN, game.getPlayer().getLocation(), game.getPlayer().getCursor())).getPoints());
                                 }
                             }
                         }

                     }
                     return true;
                 }
                 default: {
                     return false;
                 }
             }
            }
        });

        //<editor-fold desc="ui listeners">
        ui.getStage().addListener(new InputListener() {

            @Override
            public boolean keyUp(InputEvent event, int keycode) {
                switch(keycode) {
                    case Input.Keys.UP:
                    case Input.Keys.DOWN: {
                        scrolling = 0;
                        return true;
                    }
                    default: {
                        return false;
                    }
                }
            }

            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                switch(keycode) {
                    case Input.Keys.UP: {
                        if(ui.getStage().getScrollFocus() == null) ui.getStage().setScrollFocus(ui.getLog());
                        scrolling = 1;
                        return true;
                    }
                    case Input.Keys.DOWN: {
                        if(ui.getStage().getScrollFocus() == null) ui.getStage().setScrollFocus(ui.getLog());
                        scrolling = -1;
                        return true;
                    }
                    case Input.Keys.LEFT: {
                        if(ui.getStage().getScrollFocus() == null || ui.getStage().getScrollFocus().equals(ui.getLog())) ui.getStage().setScrollFocus(ui.getMinimap());
                        return true;
                    }
                    case Input.Keys.RIGHT: {
                        if(ui.getStage().getScrollFocus() == null || ui.getStage().getScrollFocus().equals(ui.getMinimap())) ui.getStage().setScrollFocus(ui.getLog());
                        return true;
                    }
                    default: {
                        return false;
                    }
                }

            }

            @Override
            public boolean mouseMoved(InputEvent event, float x, float y) {
                if(x > (4f * ui.getViewport().getScreenWidth()/5f) && (y > (4f * ui.getViewport().getScreenHeight()/5f)))
                    ui.getStage().setScrollFocus(ui.getLog());
                else if(x <= (4f * ui.getViewport().getScreenWidth()/5f) && (y > (4f * ui.getViewport().getScreenHeight()/5f)))
                    ui.getStage().setScrollFocus(ui.getMinimap());
                else
                    ui.getStage().setScrollFocus(null);
                return true;
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                super.exit(event, x, y, pointer, toActor);
                if(toActor == null) ui.getStage().setScrollFocus(null);
                else if(toActor.equals(ui.getLog()) || toActor.equals(ui.getOuterLogTable()) ||
                        toActor.equals(ui.getLogTable()) ||
                        (x > (4f * ui.getViewport().getScreenWidth()/5f) && (y > (4f * ui.getViewport().getScreenHeight()/5f))))
                    ui.getStage().setScrollFocus(ui.getLog());
                else if(toActor.equals(ui.getMinimap()) || toActor.equals(ui.getOuterMinimapTable()) ||
                        toActor.equals(ui.getInnerMinimapTable()) ||
                        (x > (4f * ui.getViewport().getScreenWidth()/5f) && (y < (4f * ui.getViewport().getScreenHeight()/5f))))
                    ui.getStage().setScrollFocus(ui.getMinimap());
            }
        });

        dialogueUI.getStage().addListener(new InputListener() {

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                super.exit(event, x, y, pointer, toActor);
                if(toActor == null) return;
                if(toActor.equals(dialogueUI.getDialogueOut()) || toActor.equals(dialogueUI.getOuterDialogueTable()) ||
                        toActor.equals(dialogueUI.getInnerDialogueTable()) || x < dialogueUI.getDialogueWidthPercent() * dialogueUI.getViewport().getScreenWidth())
                    dialogueUI.getStage().setScrollFocus(dialogueUI.getDialogueOut());
                else if(toActor.equals(dialogueUI.getOptionsOut()) || toActor.equals(dialogueUI.getOuterOptionsTable()) ||
                        toActor.equals(dialogueUI.getInnerOptionsTable()) || x >= dialogueUI.getDialogueWidthPercent() * dialogueUI.getViewport().getScreenWidth())
                    dialogueUI.getStage().setScrollFocus(dialogueUI.getOptionsOut());
            }

            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                switch (keycode) {
                    case Input.Keys.NUMPAD_8: {
                        scrolling = -1;
                        return true;
                    }
                    case Input.Keys.NUMPAD_2: {
                        scrolling = 1;
                        return true;
                    }
                    case Input.Keys.NUMPAD_4: {
                        if(dialogueUI.getStage().getScrollFocus().equals(dialogueUI.getOptionsOut())) {
                            dialogueUI.getStage().setScrollFocus(dialogueUI.getDialogueOut());
                        }
                        return true;
                    }
                    case Input.Keys.NUMPAD_6: {
                        if(dialogueUI.getStage().getScrollFocus().equals(dialogueUI.getDialogueOut())) {
                            dialogueUI.getStage().setScrollFocus(dialogueUI.getOptionsOut());
                        }
                        return true;
                    }
                    case Input.Keys.ESCAPE: {
                        dialogueUI.getDialogue().setSelection("");
                        return true;
                    }
                    case Input.Keys.SHIFT_LEFT:
                    case Input.Keys.SHIFT_RIGHT: {
                        shift = true;
                        return true;
                    }
                }

                if(Input.Keys.toString(keycode).length() == 1) {
                    char in = Input.Keys.toString(keycode).charAt(0);
                    dialogueUI.getDialogue().acceptInput(shift? Character.toUpperCase(in) : Character.toLowerCase(in));
                    boolean found = dialogueUI.getDialogue().responseFound();
                    if(found) {
                        String s = dialogueUI.getDialogue().getResponseAtSelection();
                        Dialogue next = dialogueUI.getDialogue().getBranchAt(s);
                        if(next == null)
                            endDialogue();
                        else
                            dialogueUI.setDialogue(next);
                    }

                    return true;
                }

                return false;
            }

            @Override
            public boolean keyUp(InputEvent event, int keycode) {
                switch (keycode) {
                    case Input.Keys.NUMPAD_8:
                    case Input.Keys.NUMPAD_2: {
                        scrolling = 0;
                        return true;
                    }
                    case Input.Keys.SHIFT_LEFT:
                    case Input.Keys.SHIFT_RIGHT: {
                        shift = false;
                        return true;
                    }
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
        root.add(game.getLevel().getActor()).width(stage.getWidth()).height(game.getLevel().getHeight() * Main.getTileHeight());
        ui.init();
        dialogueUI.init();
        ui.getHp().setText(String.format(Locale.getDefault(), "%d/%d", game.getPlayer().getHp(), game.getPlayer().getHpMax()));


    }

    public void act(float delta) {

        if(scrolling != 0) {
            if(System.currentTimeMillis() - lastScroll >= scrollRate) {
                if(scrolling == 1) {
                    if(Gdx.input.getInputProcessor().equals(mux)) {
                        mux.scrolled(-1);
                    }
                    else if(Gdx.input.getInputProcessor().equals(dialogueUI.getStage()))
                        dialogueUI.getStage().scrolled(1);
                }
                else if(scrolling == -1) {
                    if(Gdx.input.getInputProcessor().equals(mux)) {
                        mux.scrolled(1);
                    }
                    else if(Gdx.input.getInputProcessor().equals(dialogueUI.getStage()))
                        dialogueUI.getStage().scrolled(-1);
                }
                lastScroll = System.currentTimeMillis();
            }
        }

        process();

        stage.act(delta);
        ui.act(delta);
        if(dialogueVisible) dialogueUI.act(delta);
    }

    @Override
    public void render(float delta) {

        super.render(delta);
        Gdx.gl.glClearColor(0, 0, 0, 255);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        act(delta);

        if(game.getPlayer().isDead()) game.setScreen(game.getLoseScreen());

        //Set the camera to the player. If the player is too close to the edge of the screen, stop following
        floatPoint cameraLocation = getCameraLocation();
        stage.getCamera().position.set(cameraLocation.getX() * Main.getTileWidth(), (cameraLocation.getY() * Main.getTileHeight()), 0);
        stage.getCamera().update();

        game.getBatch().setProjectionMatrix(camera.combined);
        stage.draw();

        game.getBatch().setProjectionMatrix(ui.getViewport().getCamera().combined);

        ui.getStage().draw();

        if(dialogueVisible) {
            game.getBatch().setProjectionMatrix(dialogueUI.getViewport().getCamera().combined);
            dialogueUI.getStage().draw();
        }

    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        viewport.update(width, height);
        camera.update();
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(mux);

        process();
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
            p = new floatPoint(game.getPlayer().getCursor().getX(), game.getPlayer().getCursor().getY() + ((height/ Main.getTileHeight())/5f));
        else
            p = new floatPoint(((CreatureActor)game.getPlayer().getActor()).getCurrentLocation().getX()/Main.getTileWidth(),
                    ((CreatureActor)game.getPlayer().getActor()).getCurrentLocation().getY()/Main.getTileHeight() + ((height/ Main.getTileHeight())/5f));

        //If player's location isn't at least half a screen from the left edge, set the camera to half a screen from the left edge
        if(p.getX() * Main.getTileWidth() < width/2)
            p.setX(width/(2 * Main.getTileWidth()));

        //If player's location isn't at least half a screen from the right edge, set the camera to half a screen from the right edge
        else if((game.getLevel().getWidth() - p.getX()) * Main.getTileWidth() < width/2f)
            p.setX((game.getLevel().getWidth()) - width/(2 * Main.getTileWidth()));

        //If player's location isn't at least half a screen from the bottom edge, set the camera to half a screen from the bottom edge
        if(p.getY() * Main.getTileHeight() < (height/2 - 2))
            p.setY(height/(2 * Main.getTileHeight()));

        //If player's location isn't at least half a screen from the top edge, set the camera to half a screen from the top edge
        else if((game.getLevel().getHeight() - p.getY()) * Main.getTileHeight() < (3f * height/5f)/2f)
            p.setY((game.getLevel().getHeight()) - (3 * height/5)/(2 * Main.getTileHeight()));

        return p;
    }

    public void switchListener(String s) {
        switch (s) {
            case "Dialogue": {
                Gdx.input.setInputProcessor(dialogueUI.getStage());
                break;
            }
            default: {
                Gdx.input.setInputProcessor(mux);
                break;
            }
        }
    }

    public void startDialogue(Dialogue d) {
        game.getPlayer().setCurrentDestination(null);
        game.getPlayer().getDestinationQueue().clear();
        dialogueVisible = true;
        dialogueUI.setDialogue(d);
        switchListener("Dialogue");
        dialogueUI.getStage().setScrollFocus(dialogueUI.getDialogueOut());
    }

    public void endDialogue() {
        dialogueVisible = false;
        dialogueUI.clear();
        switchListener("");
    }

    public void process() {

        if(game.getPlayer().getCursor().isActive() && System.currentTimeMillis() -
                game.getPlayer().getLastActTime() >= Level.getActRate() * game.getPlayer().getEnergy_factor()) {
            ((PlayerAi)game.getPlayer().getAi()).processCursorMovement();
        }

        if(game.getPlayer().getLevel().isQueue_paused() && game.getPlayer().getLevel().getTurnQueue().contains(game.getPlayer())) {
            try {
                game.getPlayer().getLevel().nextAct();
            } catch (Exception e) {
                game.getPlayer().getLevel().setQueue_paused(true);
                System.err.println(e.getMessage());
            }
        }

        if(game.getPlayer().isRequestVisionUpdate()) {
            game.getPlayer().update_Extra_vision();
        }

        if (game.getPlayer().getEnergy_used() > 0 && game.getPlayer().isAllowed_to_act()) {
            game.getPlayer().process();
        }

        else if(game.getPlayer().isAllowed_to_act() && (game.getPlayer().getCurrentDestination() != null || !game.getPlayer().getDestinationQueue().isEmpty()) || game.getPlayer().getMoveDirection() != 0) {
            game.getPlayer().act(game.getPlayer().getLevel());
        }
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

    public boolean isShift() {
        return shift;
    }

    public void setShift(boolean shift) {
        this.shift = shift;
    }

    //</editor-fold>
}
