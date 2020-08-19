package world.geometry;

import java.util.Objects;

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


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof floatPoint)) return false;
        floatPoint that = (floatPoint) o;
        return Float.compare(that.x, x) == 0 &&
                Float.compare(that.y, y) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}
