package world.geometry;

public class Cursor extends Point {

    /**
     * Whether or not there should be a line following the cursor
     */
    boolean hasLine;

    /**
     * Whether or not the cursor should be currently shown
     */
    boolean isActive;

    /**
     * Whether or not the camera should follow the cursor
     */
    boolean follow;

    /**
     * If the cursor has a range
     */
    boolean hasRange;

    /**
     * The range the cursor has if it has a range
     */
    int range;

    /**
     * Whether or not the cursor cares about obstacles
     */
    boolean considerObstacle;

    /**
     * What the cursor is currently being used for
     */
    String purpose;

    public Cursor(int x, int y) {
        super(x, y);
        hasLine = false;
        isActive = false;
        follow = false;
        hasRange = false;
        range = 0;
        considerObstacle = false;
        purpose = "";
    }

    public Cursor(int x, int y, boolean hasLine) {
        super(x, y);
        this.hasLine = hasLine;
        isActive = false;
        follow = false;
        hasRange = false;
        range = 0;
        considerObstacle = false;
        purpose = "";
    }

    //<editor-fold desc="Getters and Setters">
    public boolean isHasLine() {
        return hasLine;
    }

    public void setHasLine(boolean hasLine) {
        this.hasLine = hasLine;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public boolean isFollow() {
        return follow;
    }

    public void setFollow(boolean follow) {
        this.follow = follow;
    }

    public boolean isHasRange() {
        return hasRange;
    }

    public void setHasRange(boolean hasRange) {
        this.hasRange = hasRange;
    }

    public int getRange() {
        return range;
    }

    public void setRange(int range) {
        this.range = range;
        hasRange = true;
    }

    public boolean isConsiderObstacle() {
        return considerObstacle;
    }

    public void setConsiderObstacle(boolean considerObstacle) {
        this.considerObstacle = considerObstacle;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    //</editor-fold>
}
