package creatureitem.ai.monster;

import creatureitem.Creature;
import creatureitem.ai.CreatureAi;
import world.geometry.Point;


public class ZombieAi extends CreatureAi {

    /**
     * Last known location of the player
     */
    private Point player_loc;

    /**
     * Chance the zombie will do nothing int its turn
     */
    private final double LAZY_CHANCE = 0.1;

    /**
     * Assign the AI to its creature
     *
     * @param creature The creature following this behavior set
     */
    public ZombieAi(Creature creature) {
        super(creature);
    }

    @Override
    public void onUpdate() {

        //Get the player location
        int player_x = creature.getLevel().getPlayer().getX();
        int player_y = creature.getLevel().getPlayer().getY();

        //Chance to do nothing on a turn
        if(creature.getLevel().getRandom().nextDouble() < LAZY_CHANCE) {
            creature.doAction("stop moving for a moment.");
            return;
        }

        //If the creature can't see the player, move to its last location. If it can't, wander around.
        if(!creature.canSee(player_x, player_y)) {
            if(player_loc != null)
                if(!moveToDestination(player_loc)) wander();
            else wander();
        }
        else {

            //Attack a random, enemy creature who is adjacent
            player_loc = new Point(player_x, player_y);
            boolean attack = false;
            for(int i = -1; i < 2; i++) {
                for(int j = -1; j < 2; j++) {
                    Creature foe = creature.getLevel().getCreatureAt(creature.getX() + i, creature.getY() + j);
                    if(foe != null && foe.canSee(creature.getX() + i, creature.getY() + j) && foe.getTeam() != creature.getTeam()) {
                        creature.attack(foe);
                        attack = true;
                    }
                }

            }

            //If no adj enemies, try to move towards the player, or move randomly if it can't.
            if(!attack) {
                if(!moveToDestination(player_loc)) wander();
            }
        }
    }

}
