package world.geometry;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import world.Tile;

import java.util.Collection;
import java.util.Objects;
import java.util.Stack;

public class Cursor extends Point {

    /**
     * Whether or not there should be a line following the cursor
     */
    private boolean hasLine;

    /**
     * Whether or not the cursor has an area
     */
    private boolean hasArea;

    /**
     * The radius of the cursor area, if it has a surrounding area
     */
    private int radius;

    /**
     * Whether or not the cursor should be currently shown
     */
    private boolean isActive;

    /**
     * Whether or not the camera should follow the cursor
     */
    private boolean follow;

    /**
     * If the cursor has a range
     */
    private boolean hasRange;

    /**
     * The range the cursor has if it has a range
     */
    private int range;

    /**
     * Whether or not the cursor cares about obstacles
     */
    private boolean considerObstacle;

    /**
     * What the cursor is currently being used for
     */
    private String purpose;

    private Collection<Point> path;

    private int negative, neutral, positive;

    public Cursor(int x, int y) {
        super(x, y);
        hasLine = false;
        path = null;
        isActive = false;
        follow = false;
        hasRange = false;
        range = 0;
        considerObstacle = false;
        purpose = "";
        negative = 1;
        neutral = 2;
        positive = 0;
    }

    public Cursor(int x, int y, boolean hasLine) {
        super(x, y);
        this.hasLine = hasLine;
        path = null;
        isActive = false;
        follow = false;
        hasRange = false;
        range = 0;
        considerObstacle = false;
        purpose = "";
        negative = 1;
        neutral = 2;
        positive = 0;
    }

    public void moveBy(int mx, int my) {
        setX(x + mx);
        setY(y + my);
    }

    //<editor-fold desc="Getters and Setters">
    public Collection<Point> getPath() {
        return path;
    }

    public boolean hasPath() {
        return path != null;
    }

    public void setPath(Collection<Point> path) {
        this.path = path;
    }

    public void clearPath() {
        this.path = null;
    }

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

    public boolean isHasArea() {
        return hasArea;
    }

    public void setHasArea(boolean hasArea) {
        this.hasArea = hasArea;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
        hasArea = true;
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

    public int getNegative() {
        return negative;
    }

    public TextureRegion getNegativeTexture() {
        return Tile.CURSOR.getSprite(negative);
    }

    public void setNegative(int negative) {
        this.negative = negative;
    }

    public int getNeutral() {
        return neutral;
    }

    public TextureRegion getNeutralTexture() {
        return Tile.CURSOR.getSprite(neutral);
    }

    public void setNeutral(int neutral) {
        this.neutral = neutral;
    }

    public int getPositive() {
        return positive;
    }

    public TextureRegion getPositiveTexture() {
        return Tile.CURSOR.getSprite(positive);
    }

    public void setPositive(int positive) {
        this.positive = positive;
    }

    public Point point() {
        return new Point(x, y);
    }
    //</editor-fold>


}
