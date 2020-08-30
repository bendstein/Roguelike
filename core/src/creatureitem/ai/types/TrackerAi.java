package creatureitem.ai.types;

import creatureitem.Creature;
import creatureitem.ai.CreatureAi;
import world.geometry.Point;

public class TrackerAi extends CreatureAi {

    /**
     * The creature's destination
     */
    protected Point dest;

    /**
     * Assign the AI to its creature
     *
     * @param creature The creature following this behavior set
     */
    public TrackerAi(Creature creature) {
        super(creature);
    }

    @Override
    public TrackerAi copy() {
        return new TrackerAi(creature);
    }

    public boolean attackTarget() {
        if(dest == null) return false;
        if(!creature.canSee(dest.getX(), dest.getY())) return false;
        if(creature.getLocation().chebychevDistanceFrom(dest) >= 2) return false;

        return attackPoint(dest.getX(), dest.getY());
    }

    public boolean shootTarget() {
        if(dest == null) return false;
        if(!creature.canSee(dest.getX(), dest.getY())) return false;

        return shootPoint(dest);
    }

    //<editor-fold desc="Getters and Setters">
    public Point getDest() {
        return dest;
    }

    public void setDest(Point dest) {
        this.dest = dest;
    }
    //</editor-fold>
}
