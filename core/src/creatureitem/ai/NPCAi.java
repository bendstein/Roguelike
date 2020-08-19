package creatureitem.ai;

import actors.creatures.CreatureActor;
import creatureitem.Creature;
import creatureitem.item.Item;
import game.Main;
import screens.Dialogue;
import world.Tile;
import world.geometry.floatPoint;

import java.util.ArrayList;

public class NPCAi extends CreatureAi {

    //<editor-fold desc="Instance Variables">

    /**
     * The starting dialogue for this NPC's dialogue tree
     */
    private Dialogue dialogueRoot;
    //</editor-fold>

    public NPCAi(Creature npc) {
        super(npc);
        dialogueRoot = null;
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

            if(creature.getActor() != null)
                ((CreatureActor)creature.getActor()).setCurrentLocation(new floatPoint(creature.getX() * Main.getTileWidth(), creature.getY() * Main.getTileHeight()));
            creature.setCoordinates(x, y);
            if(creature.getLevel().getItemAt(x, y) != null) {
                creature.doAction("step on %s.", creature.getLevel().getItemAt(x, y).toString());
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
        ArrayList<Item> items = new ArrayList<>(creature.getInventory().asList());
        for(Item i : items) {
            creature.drop(i);
        }
        creature.getLevel().remove(creature);
    }

    /**
     * Perform any actions the creature does when it's time to update
     */
    @Override
    public void onUpdate() {
        if(creature.getLevel().getTurn() % creature.getRegenRate() == creature.getRegenRate() - 1) creature.modifyHP(1);
        if(creature.getLevel().getTurn() % creature.getManaRegenRate() == creature.getManaRegenRate() -1) creature.modifyMana(1);

        if(creature.getLevel().getItemAt(creature.getX(), creature.getY()) != null)
            creature.pickUp();
        else
            wander();
    }

    //<editor-fold desc="Getters and Setters">
    public Dialogue getDialogueRoot() {
        return dialogueRoot;
    }

    public void setDialogueRoot(Dialogue dialogueRoot) {
        this.dialogueRoot = dialogueRoot;
    }
    //</editor-fold>

    /**
     * @return A deep copy of this
     */
    @Override
    public CreatureAi copy() {
        NPCAi ai = new NPCAi(creature);
        ai.setDialogueRoot(dialogueRoot);
        return ai;
    }
}
