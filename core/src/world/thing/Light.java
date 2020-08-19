package world.thing;

import com.badlogic.gdx.graphics.Color;
import game.Main;
import utility.Utility;
import world.Level;
import world.Tile;
import world.geometry.AStarPoint;
import world.geometry.Line;
import world.geometry.Point;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.Stack;

public class Light extends Thing {

    /**
     * Color of the emitted light
     */
    protected int[] color;

    protected Color tint;

    /**
     * Intensity of the color of the light
     */
    protected int intensity;

    /**
     * Max brightness of the emitted light
     */
    protected float brightness;

    /**
     * Range of the emitted light
     */
    protected int range;

    /**
     * Whether the light is activated
     */
    protected boolean active;

    protected boolean ignoreObstacles;

    protected int position;

    public static final int WHITE = 0, GREY0 = 1, GREY1 = 2, GREY2 = 3, GREY3 = 4,
        GREY4 = 5, GREY5 = 6, GREY6 = 7, RED = 8, ORANGE = 9, YELLOW = 10,
        YELLOW_GREEN = 11, GREEN = 12, BLUE_GREEN = 13, LIGHT_BLUE = 14,
        BLUE = 15, PURPLE = 16, PINK = 16, MAGENTA = 17;

    public static final int LEFT = 0, TOP = 1, RIGHT = 2, BOTTOM = 3;

    public Light(Tile tile, int color, Color tint, int range, float brightness, int intensity, boolean active) {
        super(tile);
        this.color = new int[]{color};
        this.range = range;
        this.brightness = brightness;
        this.intensity = intensity;
        this.active = active;
        ignoreObstacles = true;
        this.tint = tint;
        this.position = -1;
    }

    public Light(Tile tile, boolean open, int color, Color tint, int range, float brightness, int intensity, boolean active) {
        super(tile, open);
        this.color = new int[]{color};
        this.range = range;
        this.brightness = brightness;
        this.intensity = intensity;
        this.active = active;
        ignoreObstacles = true;
        this.tint = tint;
        this.position = -1;
    }

    public Light(Tile tile, int[] color, Color tint, int range, float brightness, int intensity, boolean active) {
        super(tile);
        this.open = true;
        this.color = color;
        this.range = range;
        this.brightness = brightness;
        this.intensity = intensity;
        this.active = active;
        ignoreObstacles = true;
        this.tint = tint;
        this.position = -1;
    }

    public Light(Tile tile, boolean open, int[] color, Color tint, int range, float brightness, int intensity, boolean active) {
        super(tile, open);
        this.color = color;
        this.range = range;
        this.brightness = brightness;
        this.intensity = intensity;
        this.active = active;
        ignoreObstacles = true;
        this.tint = tint;
        this.position = -1;
    }

    public Light(Tile tile, int color, int range, float brightness, int intensity, boolean active) {
        super(tile);
        this.color = new int[]{color};
        this.range = range;
        this.brightness = brightness;
        this.intensity = intensity;
        this.active = active;
        ignoreObstacles = true;
        this.tint = new Color(1f, 1f, 1f, 1f);
        this.position = -1;
    }

    public Light(Tile tile, boolean open, int color, int range, float brightness, int intensity, boolean active) {
        super(tile, open);
        this.color = new int[]{color};
        this.range = range;
        this.brightness = brightness;
        this.intensity = intensity;
        this.active = active;
        ignoreObstacles = true;
        this.tint = new Color(1f, 1f, 1f, 1f);
        this.position = -1;
    }

    public Light(Tile tile, int[] color, int range, float brightness, int intensity, boolean active) {
        super(tile);
        this.color = color;
        this.range = range;
        this.brightness = brightness;
        this.intensity = intensity;
        this.active = active;
        ignoreObstacles = true;
        this.tint = new Color(1f, 1f, 1f, 1f);
        this.position = -1;
    }

    public Light(Tile tile, boolean open, int[] color, int range, float brightness, int intensity, boolean active) {
        super(tile, open);
        this.color = color;
        this.range = range;
        this.brightness = brightness;
        this.intensity = intensity;
        this.active = active;
        ignoreObstacles = true;
        this.tint = new Color(1f, 1f, 1f, 1f);
        this.position = -1;
    }

    public Light(Light light) {
        super(light);
        this.color = light.color;
        this.range = light.range;
        this.brightness = light.brightness;
        this.intensity = light.intensity;
        this.active = light.active;
        this.ignoreObstacles = light.ignoreObstacles;
        this.tint = new Color(light.tint.r, light.tint.g, light.tint.b, light.tint.a);
        this.position = light.position;
    }

    //<editor-fold desc="Getters and Setters">
    public int[] getColor() {
        return color;
    }

    public int getColor(int i, int j, int num) {
        return getColor()[num];
    }

    public int[] getColor(int i, int j) {
        return getColor();
    }

    public void setColor(int[] color) {
        this.color = color;
    }

    public int getRange() {
        return range;
    }

    public void setRange(int range) {
        this.range = range;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }



    public float getBrightness() {
        return brightness;
    }

    public void setBrightness(float brightness) {
        this.brightness = brightness;
    }

    public int getIntensity() {
        return intensity;
    }

    public void setIntensity(int intensity) {
        this.intensity = intensity;
    }

    public boolean isIgnoreObstacles() {
        return ignoreObstacles;
    }

    public void setIgnoreObstacles(boolean ignoreObstacles) {
        this.ignoreObstacles = ignoreObstacles;
    }

    public Color getTint() {
        return tint;
    }

    public void setTint(Color tint) {
        this.tint = tint;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    //</editor-fold>

    public int canLight(int x, int y, Level l) {
        return canLight(x, y, l, true);
    }

    public int canLight(int x, int y, Level l, boolean checkAdj) {
        /*
         * If the light is out of range, return 0 (can't light)
         */
        if(l.isOutOfBounds(x, y)) return 0;

        /*
         * If the light is out of range, return 0 (can't light)
         */
        if(Math.pow(this.x - x, 2) + Math.pow(this.y - y, 2) > Math.pow(range, 2))
            return 0;

        /*
         * If the light can't be obstructed, return 1
         */
        if(!ignoreObstacles)
            return 1;

        Line ln = new Line(this.x, x, this.y, y);

        boolean stopped = false;
        for(Point p : ln) {

            if(l.getTileAt(p.getX(), p.getY()) == Tile.BOUNDS) {
                continue;
            }
            else if(p.getX() == x && p.getY() == y) {
                continue;
            }
            else if(l.isPassable(p.getX(), p.getY())) {
                continue;
            }
            else if(!l.isPassable(p.getX(), p.getY())) {
                stopped = true;
                break;
            }
        }

        /*
         * If the light was not obstructed, return 1 (Can light normally)
         */
        if(!stopped)
            return 1;

        /*
         * If the light was obstructed, but is adjacent to a normally lit tile,
         * return 2 (Can be lit indirectly), else return 0, (can't light). If it is a wall, return 0.
         *
         * If this is checked in a recursive call already, just return 0.
         *
         * This allows for more dispersed lighting.
         */
        if(!checkAdj) return 0;

        else {
            //if(!l.isPassable(x, y)) return 0;
            for(int i = -1; i <= 1; i++) {
                for(int j = -1; j <= 1; j++) {
                    if(canLight(x + i, y + j, l, false) == 1
                            && l.isPassable(x + i, y + j)) {
                        return 2;
                    }
                }
            }

            return 0;
        }
    }

    public void determinePosition(Level l) {
        Tile[][] adj = Utility.getAdjacentTiles(l.getTiles(), x, y);
        ArrayList<Point> walls = new ArrayList<>();

        for(int x = 0; x < adj.length; x++) {
            for(int y = 0; y < adj.length; y++) {
                if((x != y) && (x == 1 || y == 1)) {
                    if(adj[x][y] == Tile.WALL)
                        walls.add(new Point(x - 1, y - 1));
                }
            }
        }

        if(walls.isEmpty()) return;

        Point p = walls.get(l.getRandom().nextInt(walls.size()));

        if(p.getX() == -1) {
            if(p.getY() == 0) {
                position = LEFT;
            }
        }
        else if(p.getX() == 0) {
            if(p.getY() == -1) {
                position = BOTTOM;
            }
            else if(p.getY() == 1) {
                position = TOP;
            }
        }
        else if(p.getX() == 1) {
            if(p.getY() == 0) {
                position = RIGHT;
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Light)) return false;
        if (!super.equals(o)) return false;
        Light light = (Light) o;
        return intensity == light.intensity &&
                Float.compare(light.brightness, brightness) == 0 &&
                range == light.range &&
                active == light.active &&
                ignoreObstacles == light.ignoreObstacles &&
                Arrays.equals(color, light.color);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(super.hashCode(), intensity, brightness, range, active, ignoreObstacles);
        result = 31 * result + Arrays.hashCode(color);
        return result;
    }


}
