package creatureitem.generation;

import actors.creatures.CreatureActor;
import actors.creatures.PlayerActor;
import creatureitem.Creature;
import creatureitem.item.Item;
import creatureitem.Player;
import creatureitem.ai.monster.BatAi;
import creatureitem.ai.monster.FungusAi;
import creatureitem.ai.PlayerAi;
import world.World;

import java.util.ArrayList;

public class CreatureItemFactory {


    //<editor-fold desc="Instance Variables">
    /**
     * Reference to world we're making creatures in
     */
    private World world;
    //</editor-fold>

    public CreatureItemFactory(World world) {
        this.world = world;
    }

    /**
     * @return A new player character
     */
    public Player newPlayer(ArrayList<String> messageQueue) {
        Player player = new Player(world, "data/Player.png", "Player", 0, 6, 0, 1, 3, 9);
        world.setPlayer(player);
        new creatureitem.ai.PlayerAi(player, messageQueue);
        new PlayerActor(player);
        ((PlayerAi)player.getAi()).seenAll();
        return player;
    }

    /**
     * @return A new fungus monster
     */
    public Creature newFungus() {
        Creature fungus = new Creature(world, "data/Fungus.png", "Fungus", 1, 1, 0, 1, 2, 1);
        new FungusAi(fungus, this);
        new CreatureActor(fungus);
        return fungus;
    }

    public Creature newBat() {
        Creature bat = new Creature(world, "data/Bat.png", "Bat", 2, 2, 0, 0, 1, 9);
        new BatAi(bat);
        new CreatureActor(bat);
        return bat;
    }

    public Item newRock() {
        Item rock = new Item(',', "data/Rock.png", "Rock");
        world.addAtEmptyLocation(rock);
        return rock;
    }
}
