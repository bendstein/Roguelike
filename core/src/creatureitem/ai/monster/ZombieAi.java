package creatureitem.ai.monster;

import creatureitem.Creature;
import creatureitem.ai.CreatureAi;
import creatureitem.ai.types.TrackerAi;
import world.geometry.Point;


public class ZombieAi extends TrackerAi {

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

    /**
     * Perform any actions that the creature does when it's time for it to do an action.
     */
    @Override
    public void onAct() {
        //Get the player location
        int player_x = creature.getLevel().getPlayer().getX();
        int player_y = creature.getLevel().getPlayer().getY();

        //Chance to do nothing on a turn, and recover a little bit of energy.
        if(creature.getLevel().getRandom().nextDouble() < LAZY_CHANCE) {
            creature.doAction("stop moving for a moment.");
            creature.spendEnergy(-20);
            creature.process();
            return;
        }

        //Whether or not the creature has acted
        boolean act = false;

        //If they can see the player, mark its location
        if(creature.canSee(player_x, player_y))
            dest = new Point(player_x, player_y);

        //Attack the player, if they are adjacent
        act = attackTarget();

        //If they didn't act, attack a random, enemy creature who is adjacent
        if(!act)
            act = attackRandom();

        //If they didn't act, move toward the player's last known location
        if(!act) {
            if(dest != null)
                act = moveToDestination(dest);
        }

        //If they still haven't acted, wander.
        if(!act) {
            wander();
        }

    }

    @Override
    public ZombieAi copy() {
        return new ZombieAi(creature);
    }
}
