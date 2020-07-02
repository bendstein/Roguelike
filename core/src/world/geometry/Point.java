package world.geometry;

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
    //</editor-fold>
}
