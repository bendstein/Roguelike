package world.geometry;

import java.util.Objects;

public class AStarPoint extends Point implements Comparable {

    int priority;

    public AStarPoint(int x, int y) {
        super(x, y);
        priority = 0;
    }

    public AStarPoint(int x, int y, int priority) {
        super(x, y);
        this.priority = priority;
    }

    //<editor-fold desc="Getters and Setters">
    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }
    //</editor-fold>

    /*
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AStarPoint point = (AStarPoint) o;
        return x == point.x &&
                y == point.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

     */

    @Override
    public int compareTo(Object o) {
        return priority - ((AStarPoint) o).priority;
    }
}
