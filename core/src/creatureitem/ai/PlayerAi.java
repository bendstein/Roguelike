package creatureitem.ai;

import creatureitem.Creature;
import creatureitem.Player;
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
        if(Creature.canEnter(x, y, creature.getWorld())) {
            creature.setCoordinates(x, y);

            if(creature.getWorld().getItemAt(x, y) != null) {
                creature.doAction("step on %s.", creature.getWorld().getItemAt(x, y).getName());
            }
        }


        /*
        else if(creature.getWorld().getTileAt(x, y).isDiggable() || creature.getWorld().getTileAt(x, y) == Tile.BOUNDS) {
            creature.setCoordinates(x, y);
            creature.getWorld().dig(x, y);
        }

         */

        else
            creature.doAction("bump into the %s.", tile.getName());
    }

    /**
     * Perform any actions the creature does when a new message appears
     * @param message The message being sent
     */
    @Override
    public void onNotify(String message) {
        messages.add(message);
    }

    @Override
    public ArrayList<String> getMessages() {
        return messages;
    }

    public void seenAll() {
        for(int i = 0; i < ((Player)creature).getSeenTiles().length; i++)
            for(int j = 0; j < ((Player)creature).getSeenTiles()[0].length; j++)
                ((Player) creature).setSeen(i, j);
    }
}
