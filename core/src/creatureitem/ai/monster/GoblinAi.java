package creatureitem.ai.monster;

import creatureitem.Creature;
import creatureitem.ai.CreatureAi;
import creatureitem.ai.types.TrackerAi;
import creatureitem.item.*;
import world.geometry.Cursor;
import world.geometry.Point;


public class GoblinAi extends TrackerAi {

    /**
     * Assign the AI to its creature
     *
     * @param creature The creature following this behavior set
     */
    public GoblinAi(Creature creature) {
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

        //If they didn't act, try to do a ranged attack on the player.
        if(!act)
            act = shootTarget();

        //If they didn't act, move toward the player's last known location
        if(!act) {
            if(dest != null)
                act = moveToDestination(dest);
        }

        //If they didn't act, do a ranged attack on a random opponent
        if(!act) {
            act = shootRandom();
        }

        //If they didn't act, pickup the item on the ground if it exists
        if(!act)
            act = pickup();


        //If they still haven't acted, wander.
        if(!act) {
            wander();
        }

    }

    @Override
    public GoblinAi copy() {
        return new GoblinAi(creature);
    }

}
