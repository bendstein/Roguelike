package creatureitem.generation;

import actors.creatures.CreatureActor;
import actors.creatures.PlayerActor;
import creatureitem.Creature;
import creatureitem.item.Item;
import creatureitem.Player;
import creatureitem.ai.monster.BatAi;
import creatureitem.ai.monster.FungusAi;
import world.Dungeon;
import world.Level;

import java.util.ArrayList;

public class CreatureItemFactory {


    //<editor-fold desc="Instance Variables">
    /**
     * Reference to level we're making creatures in
     */
    private Level level;
    //</editor-fold>


    //<editor-fold desc="Getters and Setters">
    public Level getLevel() {
        return level;
    }

    public void setLevel(Level level) {
        this.level = level;
    }
    //</editor-fold>

    /**
     * @return A new player character
     */
    public Player newPlayer(ArrayList<String> messageQueue) {
        Player player = new Player(level, "data/Player.png", "Player", '@', 0, 6, 0, 1, 3, 9);
        level.setPlayer(player);
        new creatureitem.ai.PlayerAi(player, messageQueue);
        new PlayerActor(player);
        //((PlayerAi)player.getAi()).seenAll();
        return player;
    }

    /**
     * @return A new fungus monster
     */
    public Creature newFungus() {
        Creature fungus = new Creature(level, "data/Fungus.png", "Fungus", 'f', 1, 1, 0, 1, 1, 1);
        new FungusAi(fungus, this);
        new CreatureActor(fungus);
        return fungus;
    }

    public Creature newBat() {
        Creature bat = new Creature(level, "data/Bat.png", "Bat", 'b', 2, 2, 0, 0, 1, 9);
        new BatAi(bat);
        new CreatureActor(bat);
        return bat;
    }

    public Item newRock() {
        Item rock = new Item(',', "data/Rock.png", "Rock");
        level.addAtEmptyLocation(rock);
        return rock;
    }
}
