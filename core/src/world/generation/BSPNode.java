package world.generation;

import world.room.RectRoom;
import world.room.Room;
import world.room.TiledRoom;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Random;

public class BSPNode {

    /**
     * Rooms that this node contains
     */
    private ArrayList<Room> rooms;

    /**
     * Left/bottom child and right/top child
     */
    private BSPNode lc, rc;

    /**
     * What iteration of generation we're on
     */
    private int iteration;

    /**
     * Probability of split
     */
    private float p;

    /**
     * Dimensions for room
     */
    private int w, h;

    /**
     * Minimum dimensions for a room to split
     */
    private int w_min, h_min;

    /**
     * Minimum dimensions for a generated room
     */
    private final int widthMin = 5, heightMin = 5;

    /**
     * X and y coordinates for bottom left corner of the room,
     */
    private int x, y;

    /**
     * prng
     */
    private Random random;

    /**
     * Whether or not to split the room
     */
    boolean split;

    /**
     * Whether the split horizontal or vertical
     */
    boolean horizontal;

    public BSPNode(int iteration, float p, int w, int w_min, int h, int h_min, int x, int y, Random random) {
        this.iteration = iteration;
        this.p = p;
        this.w = w;
        this.w_min = w_min;
        this.h = h;
        this.h_min = h_min;
        this.x = x;
        this.y = y;
        this.random = random;
        rooms = new ArrayList<>();

        //Start the splitting process
        start();

        if(lc != null) rooms.addAll(lc.rooms);
        if(rc != null) rooms.addAll(rc.rooms);

    }

    public void start() {
        split = true;
        horizontal = false;

        //Decide whether or not to split, and if so, what direction
        if(random.nextFloat() >= Math.pow(p, iteration))
            split = false;
        else if(.25 * w > w_min && .25 * h > h_min)
            horizontal = random.nextBoolean();
        else if(.25 * w > w_min)
            horizontal = false;
        else if (.25 * h > h_min)
            horizontal = true;
        else
            split = false;

        //If we're not splitting, generate a room within the bounds
        if(!split) {
            generateRoom();
            return;
        }

        //Otherwise, split the room along the axis perpendicular to the one chosen
        float ratio = (random.nextFloat() * .5f) + .25f;

        if(horizontal) {
            lc = new BSPNode(iteration + 1, p, w, w_min, ((int) Math.floor(ratio * h)), h_min, x, y, random);
            rc = new BSPNode(iteration + 1, p, w, w_min, (int) Math.ceil((1f - ratio) * h), h_min, x, (int) Math.floor(ratio * h), random);
        }
        else {
            lc = new BSPNode(iteration + 1, p, (int) Math.floor(ratio * w), w_min, h, h_min, x, y, random);
            rc = new BSPNode(iteration + 1, p, (int) Math.ceil((1f - ratio) * w), w_min, h, h_min, (int) Math.floor(ratio * w), y, random);
        }

        System.out.printf("Parent: (%d, %d), (%d, %d)\n", x, y, w, h);
        System.out.printf("\tLeft Child: (%d, %d), (%d, %d)\n", lc.x, lc.y, lc.w, lc.h);
        System.out.printf("\tRight Child: (%d, %d), (%d, %d)\n", rc.x, rc.y, rc.w, rc.h);
        System.out.print("");

    }

    public void generateRoom() {

        //Leave a padding area of 1 on all sides of the region
        x = x + 1;
        y = y + 1;
        int wadj = w - 2;
        int hadj = h - 2;

        //Choose random dimensions for the room
        if(wadj == widthMin) {
            w = widthMin;
        }
        else
            try {
                //w = random.nextInt(wadj - widthMin) + widthMin;
                w = Math.max((int) Math.ceil(wadj * ((random.nextFloat() * 0.5) + 0.5)), widthMin);
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        if(hadj == heightMin) {
            h = heightMin;
        }
        else
            try {
                h = Math.max((int) Math.ceil(hadj * ((random.nextFloat() * 0.5) + 0.5)), heightMin);
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }

        //Choose a random point for the room so that it is within the bounds
        if(w != widthMin) {
            try {
                x = random.nextInt(wadj - w) + x;
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }

        if(h != heightMin) {
            try {
                y = random.nextInt(hadj - h) + y;
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }

        //Create the room
        rooms.add(new RectRoom(x, y, w, h));

    }

    //<editor-fold desc="Getters and Setters">
    public ArrayList<Room> getRooms() {
        return rooms;
    }

    public void setRooms(ArrayList<Room> rooms) {
        this.rooms = rooms;
    }

    public BSPNode getLc() {
        return lc;
    }

    public void setLc(BSPNode lc) {
        this.lc = lc;
    }

    public BSPNode getRc() {
        return rc;
    }

    public void setRc(BSPNode rc) {
        this.rc = rc;
    }

    public int getIteration() {
        return iteration;
    }

    public void setIteration(int iteration) {
        this.iteration = iteration;
    }

    public float getP() {
        return p;
    }

    public void setP(float p) {
        this.p = p;
    }

    public int getW() {
        return w;
    }

    public void setW(int w) {
        this.w = w;
    }

    public int getH() {
        return h;
    }

    public void setH(int h) {
        this.h = h;
    }

    public int getW_min() {
        return w_min;
    }

    public void setW_min(int w_min) {
        this.w_min = w_min;
    }

    public int getH_min() {
        return h_min;
    }

    public void setH_min(int h_min) {
        this.h_min = h_min;
    }

    public int getWidthMin() {
        return widthMin;
    }

    public int getHeightMin() {
        return heightMin;
    }

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

    public Random getRandom() {
        return random;
    }

    public void setRandom(Random random) {
        this.random = random;
    }

    public boolean isSplit() {
        return split;
    }

    public void setSplit(boolean split) {
        this.split = split;
    }

    public boolean isHorizontal() {
        return horizontal;
    }

    public void setHorizontal(boolean horizontal) {
        this.horizontal = horizontal;
    }
    //</editor-fold>
}
