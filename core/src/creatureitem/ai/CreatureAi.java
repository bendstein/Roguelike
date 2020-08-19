package creatureitem.ai;

import actors.creatures.CreatureActor;
import creatureitem.Creature;
import creatureitem.Player;
import creatureitem.item.Food;
import game.Main;
import utility.Utility;
import world.Tile;
import world.geometry.AStarPoint;
import world.geometry.Line;
import world.geometry.Point;
import world.geometry.floatPoint;

import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.Stack;

public class CreatureAi {

    //<editor-fold desc="Instance Variables">
    /**
     * The creature following this behavior set
     */
    protected Creature creature;

    /**
     * The creature can see things that are lit if they are within
     * a factor of LIGHT_VISION_FACTOR of their normal vision
     * radius
     */
    private final double LIGHT_VISION_FACTOR = 3;
    //</editor-fold>

    /**
     * Assign the AI to its creature
     * @param creature The creature following this behavior set
     */
    public CreatureAi(Creature creature) {
        this.creature = creature;
        if(creature != null) this.creature.setAi(this);
    }

    /**
     * Perform any actions the creature does on entering a new tile
     * @param x X coordinate
     * @param y Y coordinate
     * @param tile The tile they're trying to enter
     */
    public void onEnter(int x, int y, Tile tile) {

        if(Creature.canEnter(x, y, creature.getLevel())) {

            if(creature.getActor() != null)
                ((CreatureActor)creature.getActor()).setCurrentLocation(new floatPoint(creature.getX() * Main.getTileWidth(), creature.getY() * Main.getTileHeight()));

            creature.setCoordinates(x, y);
            if(creature.getLevel().getItemAt(x, y) != null) {
                creature.doAction("step on %s.",
                        creature.getLevel().getInventoryAt(x, y).getCount() == 1?
                                creature.getLevel().getItemAt(x, y).toString() :
                                creature.getLevel().getItemAt(x, y).toString() + ", amongst other things");
            }

            if(creature.getActor() != null)
                ((CreatureActor)creature.getActor()).setDestination(new floatPoint(x * Main.getTileWidth(), y * Main.getTileHeight()));
        }
    }

    /**
     * Stuff to do when the creature dies
     */
    public void onDie() {

        creature.leaveCorpse();
        creature.getLevel().remove(creature);
    }

    /**
     * Perform any actions the creature does when it's time to update
     */
    public void onUpdate() {

        if(creature.getLevel().getTurn() % creature.getHungerRate() == creature.getHungerRate() - 1) creature.modifyHunger(-1);
        if(creature.getLevel().getTurn() % creature.getRegenRate() == creature.getRegenRate() - 1) creature.modifyHP(1);
        if(creature.getLevel().getTurn() % creature.getManaRegenRate() == creature.getManaRegenRate() -1) creature.modifyMana(1);
        creature.update_Extra_vision();
    }

    /**
     * Perform any actions the creature does when a new message appears
     * @param message The message being sent
     */
    public void onNotify(String message) {

    }

    /**
     * @param x X coordinate
     * @param y Y coordinate
     * @return true if the creature can see the tile at (x, y)
     */
    public boolean canSee(int x, int y) {

        if(creature.getLevel().isOutOfBounds(x, y)) return false;

        if(creature instanceof Player) {
            if(((Player) creature).canSeeEverything()) return true;
        }

        if(creature.get_Extra_vision(x, y))
            return true;

        /*
         * If the tile is outside of your range of sight and is not lit up, you can't see it.
         * If the tile is outside of your range of sight times some factor (1.5), you can't see it, even if it is lit.
         */
        if(Math.pow(creature.getX() - x, 2) + Math.pow(creature.getY() - y, 2) > Math.pow(creature.getVisionRadius(), 2) &&
                !creature.getLevel().getStaticLit(x, y))
                return false;

        else if(Math.pow(creature.getX() - x, 2) + Math.pow(creature.getY() - y, 2) > Math.pow(creature.getVisionRadius() * LIGHT_VISION_FACTOR, 2))
            return false;

        for(Point p : new Line(creature.getX(), x, creature.getY(), y)) {

            if(creature.getLevel().getTileAt(p.getX(), p.getY()) == Tile.BOUNDS)
                continue;
            else if(p.getX() == x && p.getY() == y)
                continue;
            else if(creature.getLevel().isPassable(p.getX(), p.getY()))
                continue;

            /*
             * This next chunk just lets creatures see 1 tile through doors and similar Things
             */
            boolean canSee = false;
            for(int i = -1; i <= 1; i++) {
                for(int j = -1; j <= 1; j++) {
                    int creaturex = creature.getX(), creaturey = creature.getY();
                    int seex = creaturex + i, seey = creaturey + j;

                    if(creature.getLevel().getThingAt(seex, seey) != null && !creature.getLevel().getThingAt(seex, seey).isOpen()) {
                        for(int i1 = -1; i1 <= 1; i1++) {
                            for(int j1 = -1; j1 <= 1; j1++) {
                                if(x == seex + i1 && y == seey + j1)
                                    canSee = true;
                            }
                        }

                    }
                }
            }

            if(canSee) continue;


            return false;
        }

        return true;
    }

    /**
     * @see public void wander(int times)
     */
    public void wander() {
        wander(1);
    }

    /**
     * Move by a chebychev distance of 1 tile
     * @param times the number of times to move
     */
    public void wander(int times) {
        int mx = 0;
        int my = 0;

        for(int i = 0; i < times; i++) {
            mx += creature.getLevel().getRandom().nextInt(3) - 1;
            my += creature.getLevel().getRandom().nextInt(3) - 1;
        }
        creature.moveBy(mx, my);
    }

    /**
     * Move toward the next point in the shortest path to the destination
     * @param destination The point the creature is moving toward
     * @return true if the creature successfully moved.
     */
    public boolean moveToDestination(Point destination) {

        if(destination == null) return false;
        if(creature.getX() < 0 || creature.getX() >= creature.getLevel().getWidth() || creature.getY() < 0 ||
                creature.getY() >= creature.getLevel().getHeight()) return false;
        if(creature.getLevel().getTileAt(destination.getX(), destination.getY()) == Tile.WALL) return false;
        Point me = new Point(creature.getX(), creature.getY());
        Stack<AStarPoint> path = Utility.aStar(creature.getLevel().getCosts(), Point.DISTANCE_MANHATTAN, me, destination);

        for(int i = 0; i < creature.getLevel().getWidth(); i++) {
            for(int j = 0; j < creature.getLevel().getHeight(); j++) {

                if(i == creature.getX() && j == creature.getY())
                    System.out.print(" @");
                else {
                    System.out.print(creature.getLevel().getCosts()[i][j] < 0?
                            creature.getLevel().getCosts()[i][j] : " " + creature.getLevel().getCosts()[i][j]);
                }

                System.out.print(" ");
            }
            System.out.println();
        }

        System.out.println();

        Point next;
        try {
            next = path.pop();
            while(next.equals(me))

                next = path.pop();
        } catch (EmptyStackException e) {
            destination = null;
            return false;
        }

        creature.moveTowards(next);
        return true;
    }

    /**
     * @return The creature's message queue (always empty)
     */
    public ArrayList<String> getMessages() {
        return new ArrayList<String>();
    }

    /**
     * Eat a random food item from inventory
     */
    public void eatRandom() {
        int i;
        do {
            i = creature.getLevel().getRandom().nextInt(creature.getInventory().getItems().length);
        } while(creature.getInventory().getItems()[i] == null && !creature.getInventory().getItems()[i].hasProperty("eat"));

        Food f = (Food)creature.getInventory().getItems()[i];
        creature.getInventory().removeOne(f);
        creature.eat(f);

    }

    /**
     * @return The creature's level, based off of their experience and level function
     */
    public int calculateLevel() {
        int i = 1;

        while(creature.getExp() > (int)(levelFunction(i)))
            i++;

        return i;
    }

    /**
     * @return The number of experience points for the creature to gain a level
     */
    public int expToNextLevel() {
        return (int)levelFunction(creature.getExpLevel()) - creature.getExp();
    }

    /**
     * @param i The creature's current level
     * @return The minimum amount of experience to be at level i
     */
    protected double levelFunction(int i) {
        return (20 * Math.pow(i, 1.5)) + 1;
    }

    /**
     * Calculate stat improvements for gaining levels
     * @param lvlold The level the creature was before gaining the levels
     */
    public void gainLevels(int lvlold) {
        for(int i = lvlold + 1; i <= creature.getExpLevel(); i++) {
            creature.modifyMaxHp((i * 2) + creature.getAttributeBonus(creature.getConstitution()));
            creature.setHp(creature.getHpMax());
            creature.modifyMana((i * 2) + creature.getAttributeBonus(creature.getIntelligence()));
            creature.setMana(creature.getManaMax());
        }

    }

    /**
     * Called when the creature tries to use stairs.
     * @return false
     */
    public boolean useStairs() {
        return false;
    }

    /**
     * @return LIGHT_VISION_FACTOR
     */
    public double getLightVisionFactor() {
        return LIGHT_VISION_FACTOR;
    }

    /**
     * @return The creature associated with this instance of CreatureAi
     */
    public Creature getCreature() {
        return creature;
    }

    /**
     * @param creature The creature associated with this instance of CreatureAi
     * @return this
     */
    public CreatureAi setCreature(Creature creature) {
        this.creature = creature;
        if(creature != null && (creature.getAi() == null || !creature.getAi().equals(this))) creature.setAi(this);
        return this;
    }

    /**
     * @return A deep copy of this
     */
    public CreatureAi copy() {
        return new CreatureAi(creature);
    }
}
