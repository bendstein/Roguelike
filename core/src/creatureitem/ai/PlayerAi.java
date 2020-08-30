package creatureitem.ai;

import actors.creatures.CreatureActor;
import actors.world.LevelActor;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import creatureitem.Creature;
import creatureitem.Player;
import game.Main;
import utility.Utility;
import world.Level;
import world.geometry.Point;
import world.geometry.floatPoint;
import world.thing.DoorBehavior;
import world.thing.Entrance;
import world.thing.Stairs;
import world.Tile;

import java.util.ArrayList;

public class PlayerAi extends CreatureAi {

    //<editor-fold desc="Instance Variables">
    /**
     * Messages to display
     */
    private ArrayList<String> messages;
    //</editor-fold>

    public PlayerAi(Player player, ArrayList<String> messages) {
        super(player);
        this.messages = messages;
    }

    /**
     * Perform any actions the creature does on entering a new tile
     * @param x X coordinate
     * @param y Y coordinate
     * @param tile The tile they're trying to enter
     */
    @Override
    public void onEnter(int x, int y, Tile tile) {

        /*
         * If trying to move into a closed door, open the door instead of moving.
         */
        if(creature.getLevel().getThingAt(x, y) != null && creature.getLevel().getThingAt(x, y).getBehavior() instanceof DoorBehavior
                && !creature.getLevel().getThingAt(x, y).isOpen()) {
            creature.getLevel().getThingAt(x, y).interact(creature);
        }

        /*
         * If the creature is moving into an open tile, move
         */
        else if(Creature.canEnter(x, y, creature.getLevel())) {
            //if(creature.getActor() != null)
                //((CreatureActor)creature.getActor()).setCurrentLocation(new floatPoint(creature.getX() * Main.getTileWidth(), creature.getY() * Main.getTileHeight()));

            creature.setCoordinates(x, y);
            if(((Player)creature).getCurrentDestination() == creature.getLocation()) {
                ((Player)creature).setCurrentDestination(null);
            }

            if(creature.getLevel().getItemAt(x, y) != null) {
                creature.doAction("step on %s.",
                        creature.getLevel().getInventoryAt(x, y).getCount() == 1?
                                creature.getLevel().getItemAt(x, y).toString() :
                                creature.getLevel().getItemAt(x, y).toString() + ", amongst other things");
            }
            if(creature.getLevel().getThingAt(x, y) != null && creature.getLevel().getThingAt(x, y) instanceof Stairs) {
                creature.doAction("step on %s.", ((Stairs)creature.getLevel().getThingAt(x, y)).isUp()? "the stairs going up" : "the stairs going down");
            }

            if(creature.getActor() != null)
                ((CreatureActor)creature.getActor()).setDestination(new floatPoint(x * Main.getTileWidth(), y * Main.getTileHeight()));
        }

    }

    /**
     * Stuff to do when the creature dies
     */
    @Override
    public void onDie() {
        ((Player)creature).setDead(true);
        creature.getLevel().remove(creature);
    }

    /**
     * Perform any actions that the creature does when it's time for it to do an action.
     */
    @Override
    public void onAct() {

        boolean acted = false;

        if(System.currentTimeMillis() - creature.getLastActTime() >= Level.getActRate()) {
            if(!((Player)creature).getCursor().isActive()) {
                acted = processMovement();
            }
            else {
                processCursorMovement();
            }
        }

        /*
         * If the player hasn't yet acted automatically, process user input.
         */
        if(acted) {
            creature.process();
        }

    }

    public boolean processMovement() {

        /*
         * If cursor is active, don't move
         */
        if(((Player)creature).getCursor().isActive()) return false;

        boolean acted = false;

        /*
         * If the creature does not have a destination, and is holding down a move button, move continuously
         */
        if(((Player)creature).getCurrentDestination() == null && ((Player) creature).getDestinationQueue().isEmpty() && creature.getMoveDirection() != 0) {
            switch (creature.getMoveDirection()) {
                case Input.Keys.NUMPAD_7: {
                    creature.moveBy(-1, 1);
                    acted = true;
                    break;
                }
                case Input.Keys.NUMPAD_8: {
                    creature.moveBy(0, 1);
                    acted = true;
                    break;
                }
                case Input.Keys.NUMPAD_9: {
                    creature.moveBy(1, 1);
                    acted = true;
                    break;
                }
                case Input.Keys.NUMPAD_4: {
                    creature.moveBy(-1, 0);
                    acted = true;
                    break;
                }
                case Input.Keys.NUMPAD_5: {
                    creature.moveBy(0, 0);
                    acted = true;
                    break;
                }
                case Input.Keys.NUMPAD_6: {
                    creature.moveBy(1, 0);
                    acted = true;
                    break;
                }
                case Input.Keys.NUMPAD_1: {
                    creature.moveBy(-1, -1);
                    acted = true;
                    break;
                }
                case Input.Keys.NUMPAD_2: {
                    creature.moveBy(0, -1);
                    acted = true;
                    break;
                }
                case Input.Keys.NUMPAD_3: {
                    creature.moveBy(1, -1);
                    acted = true;
                    break;
                }
                default:
                    break;
            }
        }

        if(acted) return true;

        /*
         * If the player has no destination, set destination as next destination in the queue, if there is one
         */
        if(((Player) creature).getCurrentDestination() == null && !((Player) creature).getDestinationQueue().isEmpty())
            ((Player) creature).setCurrentDestination(((Player) creature).dequeueDestination());

        /*
         * If the player has a destination, and hasn't yet acted, move towards it.
         */
        if(((Player) creature).getCurrentDestination() != null) {
            if (!creature.getAi().moveToDestination(((Player) creature).getCurrentDestination())) {
                ((Player) creature).setCurrentDestination(null);
                ((Player) creature).getCursor().clearPath();
                ((Player) creature).getCursor().setActive(false);
                acted = true;
            }

            /*
             * If the screen is displaying a cursor path, update it
             */
            if(((Player) creature).getCursor().isHasLine() && ((Player) creature).getCursor().hasPath()) {
                ((Player) creature).getCursor().setPath(Utility.aStarPathToLine(Utility.aStarWithVision(creature.getLevel().getCosts(), creature,
                        Point.DISTANCE_MANHATTAN, creature.getLocation(), ((Player) creature).getCursor())).getPoints());
            }

        }

        if(acted) return true;

        //Stop automatic movement if there is an adjacent enemy
        boolean adjenemy = false;
        for(int i = -1; i <= 1; i++) {
            for(int j = -1; j <= 1; j++) {
                if(i == 0 && j == 0) continue;
                int cx = creature.getX() + i;
                int cy = creature.getY() + j;

                if(cx < 0 || cx >= creature.getLevel().getWidth() || cy < 0 || cy >= creature.getLevel().getHeight()) continue;

                if(creature.getLevel().getCreatureAt(cx, cy) != null &&
                        creature.getLevel().getCreatureAt(cx, cy).getTeam() != creature.getTeam()) {
                    adjenemy = true;
                    break;
                }

            }
            if(adjenemy) break;
        }

        if(adjenemy) {
            ((Player) creature).setCurrentDestination(null);
            ((Player) creature).getDestinationQueue().clear();
        }

        return acted;
    }

    public boolean processCursorMovement() {

        if(!((Player)creature).getCursor().isActive()) return false;

        if(((Player)creature).getCurrentDestination() == null && ((Player) creature).getDestinationQueue().isEmpty()) {
            switch (creature.getMoveDirection()) {
                case 0 : {
                    return false;
                }
                case Input.Keys.NUMPAD_7: {
                    ((Player) creature).moveCursorBy(-1, 1);
                    return true;
                }
                case Input.Keys.NUMPAD_8: {
                    ((Player) creature).moveCursorBy(0, 1);
                    return true;
                }
                case Input.Keys.NUMPAD_9: {
                    ((Player) creature).moveCursorBy(1, 1);
                    return true;
                }
                case Input.Keys.NUMPAD_4: {
                    ((Player) creature).moveCursorBy(-1, 0);
                    return true;
                }
                case Input.Keys.NUMPAD_5: {
                    ((Player) creature).moveCursorBy(0, 0);
                    return true;
                }
                case Input.Keys.NUMPAD_6: {
                    ((Player) creature).moveCursorBy(1, 0);
                    return true;
                }
                case Input.Keys.NUMPAD_1: {
                    ((Player) creature).moveCursorBy(-1, -1);
                    return true;
                }
                case Input.Keys.NUMPAD_2: {
                    ((Player) creature).moveCursorBy(0, -1);
                    return true;
                }
                case Input.Keys.NUMPAD_3: {
                    ((Player) creature).moveCursorBy(1, -1);
                    return true;
                }
                default:
                    break;
            }
        }

        return false;
    }

    /**
     * Perform any actions the creature does when a new message appears
     * @param message The message being sent
     */
    @Override
    public void onNotify(String message) {
        messages.add(message);
    }

    /**
     * @return The creature's message queue
     */
    @Override
    public ArrayList<String> getMessages() {
        return messages;
    }

    /**
     * Mark all tiles on the player's current level as seen
     */
    public void seenAll() {
        for(int i = 0; i < creature.getLevel().getSeen().length; i++)
            for(int j = 0; j < creature.getLevel().getSeen()[0].length; j++)
                creature.getLevel().setSeen(i, j);
        creature.getLevel().getDungeon().getGame().getPlayScreen().getUi().setRequestMinimapUpdate(true);
    }

    /**
     * Called when the creature tries to use stairs.
     * @return true if the player successfully used the stairs.
     */
    @Override
    public boolean useStairs() {

        Point p = new Point(-1, -1);

        if(creature.getLevel().getThingAt(creature.getX(), creature.getY()) instanceof Entrance) {
            Entrance e = (Entrance) creature.getLevel().getThingAt(creature.getX(), creature.getY());
            ((Player)creature).moveLevel(e.getDestination().getLevel());
            creature.getLevel().addAt(e.getDestination().getX(), e.getDestination().getY(), creature);
            p.setLocation(e.getDestination().getX(), e.getDestination().getY());
        }

        else {
            if(!(creature.getLevel().getThingAt(creature.getX(), creature.getY()) instanceof Stairs)) return false;
            Stairs s = (Stairs)creature.getLevel().getThingAt(creature.getX(), creature.getY());
            if(s == null) {
                creature.doAction("can't do that here!");
                return false;
            }
            else {
                creature.doAction("%s the stairs.", s.isUp()? "ascend" : "descend");

                ((Player)creature).moveLevel(s.getDestination().getLevel());
                creature.getLevel().addAt(s.getDestination().getX(), s.getDestination().getY(), creature);
                p.setLocation(s.getDestination().getX(), s.getDestination().getY());
            }
        }

        creature.getLevel().getDungeon().getGame().getPlayScreen().getUi().setCurrentPlayerLocation(p);
        return true;
    }

    /**
     * @return A deep copy of this
     */
    @Override
    public PlayerAi copy() {
        return new PlayerAi((Player)creature, messages);
    }
}
