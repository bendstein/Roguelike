package creatureitem.ai;

import actors.creatures.CreatureActor;
import actors.world.LevelActor;
import com.badlogic.gdx.Gdx;
import creatureitem.Creature;
import creatureitem.Player;
import game.Main;
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
        else if(Creature.canEnter(x, y, creature.getLevel())) {
            if(creature.getActor() != null)
                ((CreatureActor)creature.getActor()).setCurrentLocation(new floatPoint(creature.getX() * Main.getTileWidth(), creature.getY() * Main.getTileHeight()));

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
    public CreatureAi copy() {
        return new PlayerAi((Player)creature, messages);
    }
}
