package world.geometry;

import java.util.Objects;

public class Point {

    //<editor-fold desc="Instance Variables">
    /**
     * X coordinate
     */
    int x;

    /**
     * Y coordinate
     */
    int y;
    //</editor-fold>

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    //<editor-fold desc="Getters and Setters">
    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setLocation(int x, int y) {
        this.x = x;
        this.y = y;
    }
    //</editor-fold>

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Point)) return false;
        Point point = (Point) o;
        return x == point.x &&
                y == point.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

}
