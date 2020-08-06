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
                creature.doAction("step on %s.", creature.getLevel().getItemAt(x, y).toString());
            }

            if(creature.getActor() != null)
                ((CreatureActor)creature.getActor()).setDestination(new floatPoint(x * Main.getTileWidth(), y * Main.getTileHeight()));
        }
    }

    /**
     * Stuff to do when the creature dies
     */
    public void onDie() {

        if(creature.getLevel().getRandom().nextDouble() < .3) creature.leaveCorpse();
        creature.getLevel().remove(creature);
    }

    /**
     * Perform any actions the creature does when it's time to update
     */
    public void onUpdate() {

        if(creature.getLevel().getTurn() % creature.getHungerRate() == creature.getHungerRate() - 1) creature.modifyHunger(-1);
        if(creature.getLevel().getTurn() % creature.getRegenRate() == creature.getRegenRate() - 1) creature.modifyHP(1);
        if(creature.getLevel().getTurn() % creature.getManaRegenRate() == creature.getManaRegenRate() -1) creature.modifyMana(1);
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

    public void wander() {
        wander(1);
    }

    public void wander(int times) {
        int mx = 0;
        int my = 0;

        for(int i = 0; i < times; i++) {
            mx += creature.getLevel().getRandom().nextInt(3) - 1;
            my += creature.getLevel().getRandom().nextInt(3) - 1;
        }
        creature.moveBy(mx, my);
    }

    public boolean moveToDestination(Point destination) {

        if(destination == null) return false;
        if(creature.getX() < 0 || creature.getX() >= creature.getLevel().getWidth() || creature.getY() < 0 ||
                creature.getY() >= creature.getLevel().getHeight()) return false;
        if(creature.getLevel().getTileAt(destination.getX(), destination.getY()) == Tile.WALL) return false;
        Point me = new Point(creature.getX(), creature.getY());
        Stack<AStarPoint> path = Utility.aStar(creature.getLevel().getCosts(), Point.DISTANCE_MANHATTAN, me, destination);

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

    public ArrayList<String> getMessages() {
        return new ArrayList<String>();
    }

    public void eatRandom() {
        int i;
        do {
            i = creature.getLevel().getRandom().nextInt(creature.getInventory().getItems().length);
        } while(creature.getInventory().getItems()[i] == null && !creature.getInventory().getItems()[i].hasProperty("eat"));

        Food f = (Food)creature.getInventory().getItems()[i];
        creature.getInventory().removeOne(f);
        creature.eat(f);

    }

    public int calculateLevel() {
        int i = 1;

        while(creature.getExp() > (int)(levelFunction(i)))
            i++;

        return i;
    }

    public int expToNextLevel() {
        return (int)levelFunction(creature.getExpLevel()) - creature.getExp();
    }

    protected double levelFunction(int i) {
        return (20 * Math.pow(i, 1.5)) + 1;
    }

    public void gainLevels(int lvlold) {
        for(int i = lvlold + 1; i <= creature.getExpLevel(); i++) {
            creature.modifyMaxHp((i * 2) + creature.getAttributeBonus(creature.getConstitution()));
            creature.setHp(creature.getHpMax());
            creature.modifyMana((i * 2) + creature.getAttributeBonus(creature.getIntelligence()));
            creature.setMana(creature.getManaMax());
        }

    }

    public boolean useStairs() {
        return false;
    }

    public double getLightVisionFactor() {
        return LIGHT_VISION_FACTOR;
    }

    public Creature getCreature() {
        return creature;
    }

    public CreatureAi setCreature(Creature creature) {
        this.creature = creature;
        if(creature != null && (creature.getAi() == null || !creature.getAi().equals(this))) creature.setAi(this);
        return this;
    }

    public CreatureAi copy() {
        return new CreatureAi(creature);
    }
}
