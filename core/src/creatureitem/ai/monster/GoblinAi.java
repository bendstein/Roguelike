package creatureitem.ai.monster;

import creatureitem.Creature;
import creatureitem.ai.CreatureAi;
import creatureitem.item.*;
import world.geometry.Cursor;
import world.geometry.Point;


public class GoblinAi extends CreatureAi {

    /**
     * Last known location of the player
     */
    private Point player_loc;

    /**
     * Assign the AI to its creature
     *
     * @param creature The creature following this behavior set
     */
    public GoblinAi(Creature creature) {
        super(creature);
    }

    @Override
    public void onUpdate() {

        //Get the player location
        int player_x = creature.getLevel().getPlayer().getX();
        int player_y = creature.getLevel().getPlayer().getY();

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

            //If no adj enemies, try to do a ranged attack on the player.
            if(!attack) {
                Cursor cursor = new Cursor(player_x, player_y, true);
                cursor.setPurpose("shoot");
                cursor.setActive(true);
                cursor.setHasLine(true);
                cursor.setConsiderObstacle(true);
                cursor.setRange(creature.getRangedWeapon().getRange());

                if (creature.canShoot(cursor)) {
                    creature.shootRangedWeapon(cursor);
                    attack = true;
                }
            }

            //If they didn't attack, try to move towards the player, or move randomly if it can't.
            if(!attack) {

                boolean useItem = false;
                for(Item i : creature.getInventory().getItems()) {
                    if(creature.isEquipped(i)) continue;
                    if(i instanceof Equipable) {
                        if(i instanceof Weapon && i.hasProperty("main hand") && (creature.getMainHand() == null || ((Weapon)i).getWeaponDamage().getAverage() > creature.getMainHand().getWeaponDamage().getAverage())) {
                            creature.equip(i);
                            useItem = true;
                        }
                        else if(i instanceof RangedWeapon && (i.hasProperty("ranged") && (creature.getRangedWeapon() == null || ((Weapon)i).getWeaponDamage().getAverage() > creature.getRangedWeapon().getWeaponDamage().getAverage()))) {
                            creature.equip(i);
                            useItem = true;
                        }
                        else if(i instanceof Ammo && (i.hasProperty(creature.getRangedWeapon().getAmmoType()) && (creature.getQuiver() == null || ((Ammo)i).getAmmoDamage().getAverage() > creature.getQuiver().getAmmoDamage().getAverage()))) {
                            creature.equip(i);
                            useItem = true;
                        }
                    }
                    else if(i instanceof Food && (double)creature.getHunger()/creature.getHungerMax() < 0.5) {
                        creature.eat((Food)i);
                        useItem = true;
                    }
                    if(useItem) break;
                }

                if(!useItem) {
                    if(creature.getLevel().getItemAt(creature.getX(), creature.getY()) != null)
                        creature.pickUp();
                    else if(!moveToDestination(player_loc)) wander();
                }

            }
        }
    }

}
