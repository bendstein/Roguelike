package creatureitem.ai.monster;

import actors.creatures.CreatureActor;
import creatureitem.Creature;
import creatureitem.ai.CreatureAi;
import creatureitem.ai.types.TrackerAi;
import creatureitem.item.Item;
import game.Main;
import world.Tile;
import world.geometry.Cursor;
import world.geometry.Point;
import world.geometry.floatPoint;
import world.thing.DoorBehavior;

public class LooterAi extends TrackerAi {

    private Creature destCreature;

    private int worth_threshold;

    public LooterAi(Creature creature) {
        super(creature);
        dest = null;
        destCreature = null;
        worth_threshold = 0;
    }

    public LooterAi(Creature creature, int worth_threshold) {
        super(creature);
        dest = null;
        destCreature = null;
        this.worth_threshold = worth_threshold;
    }

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
        else if(creature.getLevel().getThingAt(x, y) != null &&
                creature.getLevel().getThingAt(x, y).getBehavior() instanceof DoorBehavior) {
            creature.getLevel().getThingAt(x, y).interact(creature);
        }
    }

    @Override
    public boolean attackTarget() {
        if(destCreature == null) return false;
        if(!creature.canSee(destCreature.getX(), destCreature.getY())) return false;
        if(creature.getLocation().chebychevDistanceFrom(destCreature.getLocation()) >= 2) return false;

        return attackPoint(destCreature.getX(), destCreature.getY());
    }

    @Override
    public boolean shootTarget() {
        if(destCreature == null) return false;
        if(!creature.canSee(destCreature.getX(), destCreature.getY())) return false;

        return shootPoint(destCreature.getLocation());
    }

    /**
     * Perform any actions that the creature does when it's time for it to do an action.
     */
    @Override
    public void onAct() {
        if(creature.getLevel().getItemAt(creature.getX(), creature.getY()) != null)
            creature.pickUp();
        else {
            if (dest != null) {

                if(destCreature != null) {
                    if(creature.canSee(destCreature.getLocation())) {
                        dest = destCreature.getLocation();
                    }

                }
                else if (creature.getLocation().equals(dest)) {
                    dest = null;
                }
                //Thing we're tracking has moved
                else if (creature.getLevel().getItemAt(dest.getX(), dest.getY()) == null)
                    dest = null;
            }

            if(dest == null) {
                //If we have no destination, search for one.
                int max_value = Integer.MIN_VALUE;
                Point max_point = null;
                for(int i = -creature.getVisionRadius(); i <= creature.getVisionRadius(); i++) {
                    for(int j = -creature.getVisionRadius(); j <= creature.getVisionRadius(); j++) {
                        Point current = new Point(creature.getX() + i, creature.getY() + j);
                        if(creature.canSee(current)) {
                            Item item = creature.getLevel().getItemAt(current.getX(), current.getY());
                            Creature cr = creature.getLevel().getCreatureAt(current.getX(), current.getY());
                            if(item != null) {
                                if(item.getWorth() >= worth_threshold) {
                                    if(item.getWorth() > max_value) {
                                        max_value = item.getWorth();
                                        if(max_point == null) max_point = new Point(current.getX(), current.getY());
                                        else max_point.setLocation(current.getX(), current.getY());
                                        destCreature = null;
                                    }
                                    else if(item.getWorth() == max_value) {
                                        if(creature.getLocation().chebychevDistanceFrom(current) < creature.getLocation().chebychevDistanceFrom(max_point)) {
                                            max_value = item.getWorth();
                                            if(max_point == null) max_point = new Point(current.getX(), current.getY());
                                            else max_point.setLocation(current.getX(), current.getY());
                                            destCreature = null;
                                        }
                                    }
                                }
                            }

                            //If the creature is wearing valuable equipment, attack it it's not on the same team
                            if(cr != null && cr.getTeam() != creature.getTeam()) {
                                int creatureValue = 0;
                                for(Item it : cr.equipped()) {
                                    if(it.getWorth() >= worth_threshold) creatureValue += it.getWorth();
                                }

                                if(creatureValue >= worth_threshold) {
                                    if(creatureValue > max_value) {
                                        max_value = creatureValue;
                                        if(max_point == null) max_point = new Point(current.getX(), current.getY());
                                        else max_point.setLocation(current.getX(), current.getY());
                                        destCreature = cr;
                                    }
                                    else if(creatureValue == max_value) {
                                        if(creature.getLocation().chebychevDistanceFrom(current) < creature.getLocation().chebychevDistanceFrom(max_point)) {
                                            max_value = creatureValue;
                                            if(max_point == null) max_point = new Point(current.getX(), current.getY());
                                            else max_point.setLocation(current.getX(), current.getY());
                                            destCreature = cr;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                dest = max_point;
            }

            boolean act = false;

            //If adj to destination, and creature at the destination, attack it.
            act = attackTarget();

            //Otherwise, if we can make a ranged attack on it, do it
            if(!act) {
                act = shootTarget();
            }

            //If no creature at destination, or not able to attack to destination, or no destination, but we're adjacent to a hostile creature, attack it
            if(!act) {
                act = attackRandom();
            }

            //Make a ranged attack on a random enemy in range
            if(!act) {
                act = shootRandom();
            }

            //If we didn't attack, use a random item
            if(!act) {
                act = useRandomItem();
            }

            //If we didn't act, move towards the destination
            if(!act) {
                if(dest != null) {
                    creature.moveTowardsAStar(dest);
                    act = true;
                }
            }

            //If all else fails, wander
            if(!act) {
                wander();
            }
        }
    }

    @Override
    public LooterAi copy() {
        return new LooterAi(creature);
    }

    //<editor-fold desc="Getters and Setters">
    public Point getDest() {
        return dest;
    }

    public void setDest(Point dest) {
        this.dest = dest;
    }

    public int getWorth_threshold() {
        return worth_threshold;
    }

    public void setWorth_threshold(int worth_threshold) {
        this.worth_threshold = worth_threshold;
    }
    //</editor-fold>
}
