package creatureitem.ai;

import creatureitem.Creature;
import creatureitem.Player;
import world.Stairs;
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
        if(Creature.canEnter(x, y, creature.getLevel())) {
            creature.setCoordinates(x, y);
            System.out.println(x + ", " + y);

            if(creature.getLevel().getItemAt(x, y) != null) {
                creature.doAction("step on %s.", creature.getLevel().getItemAt(x, y).getName());
            }
            if(creature.getLevel().getStairsAt(x, y) != null) {
                creature.doAction("step on %s.", creature.getLevel().getStairsAt(x, y).isUp()? "the stairs going up" : "the stairs going down");
            }
        }

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
        for(int i = 0; i < creature.getLevel().getSeen().length; i++)
            for(int j = 0; j < creature.getLevel().getSeen()[0].length; j++)
                creature.getLevel().setSeen(i, j);
    }

    public boolean useStairs(char c) {
        Stairs s = creature.getLevel().getStairsAt(creature.getX(), creature.getY());
        if(s == null || s.getT().getGlyph() != c)
        creature.doAction("can't do that here!");
        else {
            creature.doAction("%s the stairs.", s.isUp()? "ascend" : "descend");

            if(s.getDestination() == null)
                return true;

            creature.getLevel().setPlayer(null);
            creature.getLevel().remove(creature);
            creature.setLevel(s.getDestination().getLevel());
            creature.getLevel().setActor(creature.getLevel().getDungeon().getLevelActor());
            creature.getLevel().addAt(s.getDestination().getX(), s.getDestination().getY(), creature);
            creature.getLevel().getDungeon().getFactory().setLevel(creature.getLevel());
        }

        return false;
    }
}
