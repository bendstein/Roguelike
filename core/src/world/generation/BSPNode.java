package world.generation;

import world.Tile;
import world.geometry.Line;
import world.geometry.Point;
import world.room.Room;
import world.room.tiledRoom;

import java.util.ArrayList;
import java.util.Random;

public class BSPNode {

    /**
     * The room corresponding to this node
     */
    tiledRoom room;

    /**
     * Child node. Left node if vertical split, bottom node if horizontal.
     */
    BSPNode left;

    /**
     * Child node. Right node if vertical split, top node if horizontal.
     */
    BSPNode right;

    /**
     * -1 - Horizontal, 0 - No split, 1 - Vertical
     */
    int direction;

    /**
     * The location of the split. If horizontal, y-coord. If vertical, x-coord.
     */
    int location;

    /**
     * What level this node is on
     */
    int depth;

    /**
     * Factor applied to split probability each level
     */
    double probFactor;

    /**
     * The minimum ratio between the size of either partition and the whole
     */
    double minRatio;

    /**
     * Prng
     */
    Random random;

    private final int MIN_DIM_TO_SPLIT = 14;

    public BSPNode(tiledRoom room, boolean split, int depth, double probFactor, double minRatio, Random random) {
        this.room = room;
        this.depth = depth;
        this.probFactor = probFactor;
        this.minRatio = minRatio;
        this.random = random;

        if(room.getWidth() <= MIN_DIM_TO_SPLIT && room.getHeight() <= MIN_DIM_TO_SPLIT) split = false;

        //If we're not splitting, create a rectangular room
        if(!split) {
            direction = location = 0;
            generateRect();
        }

        //Otherwise split and recursively create children
        else {
            if(room.getWidth() >= MIN_DIM_TO_SPLIT && room.getHeight() >= MIN_DIM_TO_SPLIT)
                direction = random.nextInt(2) == 0 ? -1 : 1;
            else if(room.getWidth() >= MIN_DIM_TO_SPLIT)
                direction = 1;
            else
                direction = -1;

            if(direction == 1) {
                location = random.nextInt((int)(Math.ceil(minRatio * room.getWidth()))) + (int)(Math.floor((1 - minRatio) * room.getWidth()));
            }

            else {
                location = random.nextInt((int)(Math.ceil(minRatio * room.getHeight()))) + (int)(Math.floor((1 - minRatio) * room.getHeight()));
            }

            split();
            combine();
        }
    }

    /**
     * Split the room in 2 and recursively create children
     */
    public void split() {

        Tile[][] leftTiles;
        Tile[][] rightTiles;

        //If split is horizontal
        if(direction == -1) {

            if(location < minRatio * room.getHeight() || (room.getHeight() - location) < minRatio * room.getHeight())
                location = (int) (minRatio * room.getHeight());

            leftTiles = new Tile[room.getWidth()][location];
            rightTiles = new Tile[room.getWidth()][room.getHeight() - location];

            for(int i = 0; i < room.getWidth(); i++) {
                for(int j = 0; j < room.getHeight(); j++) {
                    if(j < location)
                        leftTiles[i][j] = room.getTileAt(i, j);
                    else
                        rightTiles[i][j - location] = room.getTileAt(i, j);
                }
            }
        }

        //If split is vertical
        else {

            if(location < minRatio * room.getWidth() || (room.getWidth() - location) < minRatio * room.getWidth())
                location = (int) (minRatio * room.getWidth());

            leftTiles = new Tile[location][room.getHeight()];
            rightTiles = new Tile[room.getWidth() - location][room.getHeight()];

            for(int i = 0; i < room.getWidth(); i++) {
                for(int j = 0; j < room.getHeight(); j++) {
                    if(i < location)
                        leftTiles[i][j] = room.getTileAt(i, j);
                    else
                        rightTiles[i - location][j] = room.getTileAt(i, j);
                }
            }
        }

        boolean leftSplit = random.nextInt(1000)/1000d < Math.pow(probFactor, depth);
        boolean rightSplit = random.nextInt(1000)/1000d < Math.pow(probFactor, depth);

        this.left = new BSPNode(new tiledRoom(leftTiles, new Point(0, 0)), leftSplit, depth + 1, probFactor, minRatio, random);
        this.right = new BSPNode(new tiledRoom(rightTiles, new Point(0, 0)), rightSplit, depth + 1, probFactor, minRatio, random);
    }

    /**
     * Combine the 2 children into 1
     */
    public void combine() {

        int x0 = 0, x = 0, y0 = 0, y = 0;

        //In each room, punch a hole in the wall for a door
        if(random.nextBoolean()) {
            if(random.nextBoolean()) {
                for(int i = 0; i < left.room.getWidth(); i++) {
                    for(int j = 0; j < left.room.getHeight(); j++) {
                        if(left.room.getTiles()[i][j] == Tile.WALL) {
                            x = i;
                            do {
                                y = random.nextInt(left.room.getHeight());
                            } while (left.room.getTiles()[x][y] != Tile.WALL);
                        }
                    }
                }
            }
            else {
                for(int i = left.room.getWidth() - 1; i >= 0; i--) {
                    for(int j = 0; j < left.room.getHeight(); j++) {
                        if(left.room.getTiles()[i][j] == Tile.WALL) {
                            x = i;
                            do {
                                y = random.nextInt(left.room.getHeight());
                            } while (left.room.getTiles()[x][y] != Tile.WALL);
                        }
                    }
                }
            }
        }
        else {
            if(random.nextBoolean()) {
                for(int i = 0; i < left.room.getWidth(); i++) {
                    for(int j = 0; j < left.room.getHeight(); j++) {
                        if(left.room.getTiles()[i][j] == Tile.WALL) {
                            y = j;
                            do {
                                x = random.nextInt(left.room.getWidth());
                            } while (left.room.getTiles()[x][y] != Tile.WALL);
                        }
                    }
                }
            }
            else {
                for(int i = 0; i < left.room.getWidth(); i++) {
                    for(int j = left.room.getHeight() - 1; j < left.room.getHeight(); j++) {
                        if(left.room.getTiles()[i][j] == Tile.WALL) {
                            y = j;
                            do {
                                x = random.nextInt(left.room.getWidth());
                            } while (left.room.getTiles()[x][y] != Tile.WALL);
                        }
                    }
                }
            }
        }

        left.room.getTiles()[x][y] = Tile.DOOR;

        int x1 = 0, y1 = 0;
        //In each room, punch a hole in the wall for a door
        if(random.nextBoolean()) {
            if(random.nextBoolean()) {
                for(int i = 0; i < right.room.getWidth(); i++) {
                    for(int j = 0; j < right.room.getHeight(); j++) {
                        if(right.room.getTiles()[i][j] == Tile.WALL) {
                            x1 = i;
                            do {
                                y1 = random.nextInt(right.room.getHeight());
                            } while (right.room.getTiles()[x1][y1] != Tile.WALL);
                        }
                    }
                }
            }
            else {
                for(int i = right.room.getWidth() - 1; i >= 0; i--) {
                    for(int j = 0; j < right.room.getHeight(); j++) {
                        if(right.room.getTiles()[i][j] == Tile.WALL) {
                            x1 = i;
                            do {
                                y1 = random.nextInt(right.room.getHeight());
                            } while (right.room.getTiles()[x1][y1] != Tile.WALL);
                        }
                    }
                }
            }
        }
        else {
            if(random.nextBoolean()) {
                for(int i = 0; i < right.room.getWidth(); i++) {
                    for(int j = 0; j < right.room.getHeight(); j++) {
                        if(right.room.getTiles()[i][j] == Tile.WALL) {
                            y1 = j;
                            do {
                                x1 = random.nextInt(right.room.getWidth());
                            } while (right.room.getTiles()[x1][y1] != Tile.WALL);
                        }
                    }
                }
            }
            else {
                for(int i = 0; i < right.room.getWidth(); i++) {
                    for(int j = right.room.getHeight() - 1; j < right.room.getHeight(); j++) {
                        if(right.room.getTiles()[i][j] == Tile.WALL) {
                            y1 = j;
                            do {
                                x1 = random.nextInt(right.room.getWidth());
                            } while (right.room.getTiles()[x1][y1] != Tile.WALL);
                        }
                    }
                }
            }
        }

        right.room.getTiles()[x1][y1] = Tile.DOOR;

        //If split is horizontal
        if(direction == -1) {

            for(int i = 0; i < room.getWidth(); i++) {
                for(int j = 0; j < room.getHeight(); j++) {
                    if(j < location)
                        room.setTileAt(i, j, left.room.getTiles()[i][j]);
                    else
                        room.setTileAt(i, j, right.room.getTiles()[i][j - location]);
                }
            }

            for(int i = 0; i < room.getWidth(); i++) {
                for(int j = location; j >= 0; j--) {
                    if(room.getTiles()[i][j] == Tile.WALL) {
                        y1 = j ;
                        break;
                    }
                }
            }

            do {
                x1 = random.nextInt(room.getWidth());
            } while (room.getTiles()[x1][y1] != Tile.WALL);

            for(int i = 0; i < room.getWidth(); i++) {
                for(int j = room.getHeight() - 1; j >= location; j--) {
                    if(room.getTiles()[i][j] == Tile.WALL) {
                        y0 = j;
                        break;
                    }
                }
            }

            do {
                x0 = random.nextInt(room.getWidth());
            } while (room.getTiles()[x0][y0] != Tile.WALL);

        }

        //If split is vertical
        else {

            for(int i = 0; i < room.getWidth(); i++) {
                for(int j = 0; j < room.getHeight(); j++) {
                    if(i < location)
                        room.setTileAt(i, j, left.room.getTiles()[i][j]);
                    else
                        room.setTileAt(i, j, right.room.getTiles()[i - location][j]);
                }
            }

            for(int i = location - 1; i >= 0; i--) {
                for(int j = 0; j < room.getHeight(); j++) {
                    if(room.getTiles()[i][j] == Tile.WALL) {
                        x1 = i;
                        break;
                    }
                }
            }

            do {
                y1 = random.nextInt(room.getHeight());
            } while (room.getTiles()[x1][y1] != Tile.WALL);

            for(int i = room.getWidth() - 1; i >= location; i--) {
                for(int j = 0; j < room.getHeight(); j++) {
                    if(room.getTiles()[i][j] == Tile.WALL) {
                        x0 = i;
                        break;
                    }
                }
            }

            do {
                y0 = random.nextInt(room.getHeight());
            } while (room.getTiles()[x0][y0] != Tile.WALL);


        }

        Line l = new Line(x0, x1, y0, y1);
        Point prev = null;

        if(l.size() > 2)
            for(Point p : l) {
                if((p.getX() == x0 && p.getY() == y0) || (p.getX() == x1 && p.getY() == y1))
                    room.setTileAt(p.getX(), p.getY(), Tile.DOOR);
                else {
                    if(prev != null) {
                        if(p.getX() != prev.getX() && p.getY() != prev.getY()) {
                            if(random.nextBoolean())
                                room.setTileAt(p.getX(), prev.getY(), Tile.FLOOR);
                            else
                                room.setTileAt(prev.getX(), p.getY(), Tile.FLOOR);
                        }
                    }
                    room.setTileAt(p.getX(), p.getY(), Tile.FLOOR);
                }
                prev = p;
            }



    }

    /**
     * Generate a rectangular room
     */
    public void generateRect() {
        int x, y, w, h;
        final int MIN = 12;

        //Fill room with void
        for(int i = 0; i < room.getWidth(); i++) {
            for(int j = 0; j < room.getHeight(); j++) {
                room.getTiles()[i][j] = Tile.BOUNDS;
            }
        }

        if(room.getWidth() <= MIN) w = room.getWidth();
        else w = random.nextInt((room.getWidth() - 2) - (2 * room.getWidth()/3)) + (2 * room.getWidth()/3);

        if(room.getHeight() <= MIN) h = room.getHeight();
        else h = random.nextInt((room.getHeight() - 2) - (2 * room.getHeight()/3)) + (2 * room.getHeight()/3);

        //X offset for room is anywhere from 0 (room against left side) to room.getWidth() - w (room against right side).
        x = random.nextInt((room.getWidth() - w) + 1);

        //Y offset for room is anywhere from 0 (room against bottom side) to room.getHeight() - h (room against top side).
        y = random.nextInt((room.getHeight() - h) + 1);

        //int doorx, doory;
        //doorx = random.nextInt(w - x - 1) + x;
        //doory = (doorx == x || doorx == w - 1) ?
                //random.nextInt(h - 2 - (y + 1)) + y + 1 :
                //random.nextInt(h - 1 - y) + y;

        for(int i = x; i < w + x; i++) {
            for(int j = y; j < h + y; j++) {
                if(i == x || j == y || i == w + x - 1 || j == h + y - 1)
                    room.getTiles()[i][j] = Tile.WALL;
                else
                    room.getTiles()[i][j] = Tile.FLOOR;
            }
        }

    }

    //<editor-fold desc="Getters and Setters">
    public tiledRoom getRoom() {
        return room;
    }

    public void setRoom(tiledRoom room) {
        this.room = room;
    }

    public BSPNode getLeft() {
        return left;
    }

    public void setLeft(BSPNode left) {
        this.left = left;
    }

    public BSPNode getRight() {
        return right;
    }

    public void setRight(BSPNode right) {
        this.right = right;
    }

    public int getDirection() {
        return direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    public int getLocation() {
        return location;
    }

    public void setLocation(int location) {
        this.location = location;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public double getProbFactor() {
        return probFactor;
    }

    public void setProbFactor(double probFactor) {
        this.probFactor = probFactor;
    }

    public double getMinRatio() {
        return minRatio;
    }

    public void setMinRatio(double minRatio) {
        this.minRatio = minRatio;
    }

    public Random getRandom() {
        return random;
    }

    public void setRandom(Random random) {
        this.random = random;
    }
    //</editor-fold>
}
