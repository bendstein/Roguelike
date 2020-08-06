package world.geometry;

public class floatPoint {

    //<editor-fold desc="Instance Variables">
    /**
     * X coordinate
     */
    float x;

    /**
     * Y coordinate
     */
    float y;
    //</editor-fold>

    public floatPoint(float x, float y) {
        this.x = x;
        this.y = y;
    }

    //<editor-fold desc="Getters and Setters">
    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public void setLocation(float x, float y) {
        this.x = x;
        this.y = y;
    }
    //</editor-fold>
}
