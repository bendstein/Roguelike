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

    Dialogue dialogueRoot;

    public NPCAi(Creature npc) {
        super(npc);
        dialogueRoot = null;
    }

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


    @Override
    public void onDie() {
        ArrayList<Item> items = new ArrayList<>(creature.getInventory().asList());
        for(Item i : items) {
            creature.drop(i);
        }
        creature.getLevel().remove(creature);
    }

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


    @Override
    public CreatureAi copy() {
        NPCAi ai = new NPCAi(creature);
        ai.setDialogueRoot(dialogueRoot);
        return ai;
    }
}
