package world.room;

import world.World;
import world.Tile;
import world.geometry.Point;

import java.util.*;

public abstract class Room extends World {

    /**
     * The level this room belongs to
     */
    protected World parent;

    /**
     * The x coordinate of the bottom left corner of the room in the parent's coordinate system.
     */
    protected int x;

    /**
     * The y coordinate of the bottom left corner of the room in the parent's coordinate system.
     */
    protected int y;

    public Room(Tile[][] tiles) {
        super(tiles);
        x = y = 0;
    }

    public Room(int x, int y, Tile[][] tiles) {
        super(tiles);
        this.x = x;
        this.y = y;
    }

    public Room() {
        super(new Tile[0][0]);
        x = y = 0;
    }

    public Room(int x, int y) {
        super(new Tile[0][0]);
        this.x = x;
        this.y = y;
    }

    public Room(int x, int y, int w, int h) {
        super(new Tile[w][h]);
        this.x = x;
        this.y = y;
    }

    /**
     * @param xp X coordinate
     * @param yp Y coordinate
     * @return The tile at (xp, yp) in the parent's coordinate system
     */
    public Tile getTileAtParent(int xp, int yp) {
        return tiles[xp - x][yp - y];
    }

    /**
     * Set the tile at (xp, yp) in the parent's coordinate system to be x
     * @param xp X coordinate
     * @param yp Y coordinate
     * @param t The tile
     */
    public void setTileAtParent(int xp, int yp, Tile t) {
        tiles[xp - x][yp - y] = t;
    }

    /**
     * @param x X coordinate
     * @return X coordinate in parent coordinate system
     */
    public int xToParentX(int x) {
        return x + this.x;
    }

    /**
     * @param x X coordinate
     * @return X coordinate in this room's coordinate system
     */
    public int parentXtoX(int x) {
        return x - this.x;
    }

    /**
     * @param y Y coordinate
     * @return y coordinate in parent coordinate system
     */
    public int yToParentY(int y) {
        return y + this.y;
    }

    /**
     * @param y Y coordinate
     * @return Y coordinate in this room's coordinate system
     */
    public int parentYtoY(int y) {
        return y - this.y;
    }

    /**
     * @param r Another room
     * @return true if this room overlaps with r
     */
    public boolean overlap(Room r) {
        if((x > r.x && x < (r.x + r.getWidth()) || ((x + getWidth()) > r.x && (x + getWidth()) < (r.x + r.getWidth())) &&
                ((y > r.y && y < (r.y + r.getHeight()) || ((y + getHeight()) > r.y && (y + getHeight()) < (r.y + r.getHeight()))))))
            return true;

        for(int i = Math.min(x, r.x); i < Math.max(x + getWidth(), r.x + r.getWidth()); i++) {
            for(int j = Math.min(y, r.y); j < Math.max(y + getHeight(), r.y + r.getHeight()); j++) {
                if(i >= x && i < x + getWidth() && i >= r.x && i <= r.x + r.getWidth()) {
                    if(j >= y && j < y + getHeight() && j >= r.y && j <= r.y + r.getHeight()) {
                        if(parentXtoX(i) >= 0 && parentXtoX(i) < getWidth() && r.parentXtoX(i) >= 0 && r.parentXtoX(i) < r.getWidth() &&
                                parentYtoY(j) >= 0 && parentYtoY(j) < getHeight() && r.parentYtoY(j) >= 0 && r.parentYtoY(j) < r.getHeight()) {
                            if ((getTileAtParent(i, j) != null && r.getTileAtParent(i, j) != null))
                                return true;
                            if(getTileAtParent(i, j) == Tile.FLOOR) {
                                Tile[][] adj = new Tile[][]{{null, null, null}, {null, null, null}, {null, null, null}};

                                if(r.parentXtoX(i - 1) >= 0 && r.parentXtoX(i) < r.getWidth() &&
                                        r.parentYtoY(j - 1) >= 0 && r.parentYtoY(j) < r.getHeight()) {
                                    adj[0][0] = r.getTileAtParent(i - 1, j - 1);
                                }
                                if(r.parentXtoX(i - 1) >= 0 && r.parentXtoX(i) < r.getWidth() &&
                                        r.parentYtoY(j) >= 0 && r.parentYtoY(j + 1) < r.getHeight()) {
                                    adj[0][2] = r.getTileAtParent(i - 1, j + 1);
                                }
                                if(r.parentXtoX(i) >= 0 && r.parentXtoX(i) < r.getWidth() &&
                                        r.parentYtoY(j - 1) >= 0 && r.parentYtoY(j) < r.getHeight()) {
                                    adj[1][0] = r.getTileAtParent(i, j - 1);
                                }
                                if(r.parentXtoX(i) >= 0 && r.parentXtoX(i) < r.getWidth() &&
                                        r.parentYtoY(j) >= 0 && r.parentYtoY(j + 1) < r.getHeight()) {
                                    adj[1][2] = r.getTileAtParent(i, j + 1);
                                }
                                if(r.parentXtoX(i) >= 0 && r.parentXtoX(i + 1) < r.getWidth() &&
                                        r.parentYtoY(j) >= 0 && r.parentYtoY(j) < r.getHeight()) {
                                    adj[2][1] = r.getTileAtParent(i + 1, j);
                                }
                                if(r.parentXtoX(i) >= 0 && r.parentXtoX(i + 1) < r.getWidth() &&
                                        r.parentYtoY(j - 1) >= 0 && r.parentYtoY(j) < r.getHeight()) {
                                    adj[2][0] = r.getTileAtParent(i + 1, j - 1);
                                }
                                if(r.parentXtoX(i) >= 0 && r.parentXtoX(i + 1) < r.getWidth() &&
                                        r.parentYtoY(j) >= 0 && r.parentYtoY(j + 1) < r.getHeight()) {
                                    adj[2][2] = r.getTileAtParent(i + 1, j + 1);
                                }

                                for(int i1 = 0; i1 < 3; i1++) {
                                    for(int j1 = 0; j1 < 3; j1++) {
                                        if(adj[i1][j1] != null && adj[i1][j1] != Tile.DOOR)
                                            return true;
                                    }
                                }
                            }

                        }

                    }
                }
            }
        }

        return false;
    }

    public void padRoom(int padx, int pady) {
        Tile[][] tilesPadded = new Tile[getWidth() + 2 * padx][getHeight() + 2 * pady];

        for(int i = 0; i < getWidth() + 2 * padx; i++) {
            for(int j = 0; j < getHeight() + 2 * pady; j++) {
                if(i < padx || i >= getWidth() + padx || j < pady || j >= getHeight() + pady)
                    tilesPadded[i][j] = null;
                else
                    tilesPadded[i][j] = tiles[i - padx][j - pady];

            }
        }

        this.tiles = tilesPadded;
    }

    public void reduceRoom() {
        int xmin = Integer.MAX_VALUE;
        int xmax = Integer.MIN_VALUE;
        int ymin = Integer.MAX_VALUE;
        int ymax = Integer.MIN_VALUE;

        for(int i = 0; i < getWidth(); i++) {
            for(int j = 0; j < getHeight(); j++) {
                if(tiles[i][j] != null) {
                    if(i < xmin) xmin = i;
                    if(i > xmax) xmax = i;
                    if(j < ymin) ymin = j;
                    if(j > ymax) ymax = j;
                }
            }
        }

        Tile[][] reduced = new Tile[xmax - xmin][ymax - ymin];
        for(int i = xmin; i < xmax; i++) {
            for(int j = ymin; j < ymax; j++) {
                reduced[i - xmin][j - ymin] = tiles[i][j];
            }
        }

        tiles = reduced;
    }

    public void coordinatesCenteredAt(int x, int y, int i, int j) {
        this.x = x - i;
        this.y = y - j;
    }

    public Point placeRandomDoor(Random r) {
        ArrayList<Point> points = new ArrayList<>();
        padRoom(2, 2);

        for(int i = 0; i < getWidth(); i++) {
            for(int j = 0; j < getHeight(); j++) {

                if(tiles[i][j] == null) {
                    Tile[][] adj = getAdjacentTiles(i, j);
                    int count = 0;

                    for(int i1 = 0; i1 < adj.length; i1++) {
                        for(int j1 = 0; j1 < adj[0].length; j1++) {
                            if(adj[i1][j1] == Tile.FLOOR) {
                                if((i1 == 0 && j1 == 0) || (i1 == 0 && j1 == 2) || (i1 == 2 && j1 == 0) || (i1 == 2 && j1 == 2)) continue;
                                else count++;
                            }
                        }
                    }

                    if(count == 1) points.add(new Point(i, j));
                }
            }
        }

        int d = points.size() > 1? r.nextInt(points.size() - 1) : 0;
        tiles[points.get(d).getX()][points.get(d).getY()] = Tile.DOOR;

        return points.get(d);
    }

    public ArrayList<Point> getDoorLocations(Random r) {
        ArrayList<Point> points = new ArrayList<>();
        padRoom(2, 2);

        for(int i = 0; i < getWidth(); i++) {
            for(int j = 0; j < getHeight(); j++) {

                if(tiles[i][j] == null) {
                    Tile[][] adj = getAdjacentTiles(i, j);
                    int count = 0;

                    for(int i1 = 0; i1 < adj.length; i1++) {
                        for(int j1 = 0; j1 < adj[0].length; j1++) {
                            if(adj[i1][j1] == Tile.FLOOR) {
                                if((i1 == 0 && j1 == 0) || (i1 == 0 && j1 == 2) || (i1 == 2 && j1 == 0) || (i1 == 2 && j1 == 2)) continue;
                                else count++;
                            }
                        }
                    }

                    if(count == 1) points.add(new Point(i, j));
                }
            }
        }

        return points;
    }

    public ArrayList<Point> placeRandomCorridor(Random r) {
        ArrayList<Point> points = new ArrayList<>();
        int l = r.nextInt(14);
        int h = l < 3 ? r.nextInt(11) + 3 : r.nextInt(14);

        padRoom(Math.max(l + 1, 3), Math.max(h + 1, 3));

        for(int i = 0; i < getWidth(); i++) {
            for(int j = 0; j < getHeight(); j++) {

                if(tiles[i][j] == null) {
                    Tile[][] adj = getAdjacentTiles(i, j);
                    int count = 0;

                    for(int i1 = 0; i1 < adj.length; i1++) {
                        for(int j1 = 0; j1 < adj[0].length; j1++) {
                            if(adj[i1][j1] == Tile.FLOOR) {
                                if((i1 == 0 && j1 == 0) || (i1 == 0 && j1 == 2) || (i1 == 2 && j1 == 0) || (i1 == 2 && j1 == 2)) continue;
                                else count++;
                            }
                        }
                    }

                    if(count >= 1) points.add(new Point(i, j));
                }
            }
        }

        if(points.size() == 0)
            return points;

        int d = points.size() > 1? r.nextInt(points.size() - 1) : 0;

        int xh = points.get(d).getX();
        int yh = points.get(d).getY();

        boolean left, down, first = false;

        if(tiles[xh - 1][yh] == null && tiles[xh + 1][yh] != null) {
            left = true;
            first = true;
        }
        else if(tiles[xh - 1][yh] != null && tiles[xh + 1][yh] == null) {
            left = false;
            first = true;
        }
        else
            left = r.nextBoolean();

        if(tiles[xh][yh - 1] == null && tiles[xh][yh + 1] != null) {
            down = true;
        }
        else if(tiles[xh][yh - 1] != null && tiles[xh][yh + 1] == null) {
            down = false;
        }
        else
            down = r.nextBoolean();

        if(first) {
            if(left) {
                for(int i = xh; i > xh - l; i--) {
                    tiles[i][yh] = Tile.FLOOR;
                }
                xh -= l - 1;
            }
            else {
                for(int i = xh; i < xh + l; i++) {
                    tiles[i][yh] = Tile.FLOOR;
                }
                xh += l - 1;
            }

            if(down) {
                for(int j = yh; j > yh - h; j--) {
                    tiles[xh][j] = Tile.FLOOR;
                }
                yh -= h - 1;
            }
            else {
                for(int j = yh; j < yh + h; j++) {
                    tiles[xh][j] = Tile.FLOOR;
                }
                yh += h - 1;
            }
        }
        else {
            if(down) {
                for(int j = yh; j > yh - h; j--) {
                    tiles[xh][j] = Tile.FLOOR;
                }
                yh -= h - 1;
            }
            else {
                for(int j = yh; j < yh + h; j++) {
                    tiles[xh][j] = Tile.FLOOR;
                }
                yh += h - 1;
            }

            if(left) {
                for(int i = xh; i > xh - l; i--) {
                    tiles[i][yh] = Tile.FLOOR;
                }
                xh -= l - 1;
            }
            else {
                for(int i = xh; i < xh + l; i++) {
                    tiles[i][yh] = Tile.FLOOR;
                }
                xh += l - 1;
            }
        }

        ArrayList<Point> p = new ArrayList<>();

        if(first) {
            if(down) {
                p.add(new Point(xh, yh - 1));
                p.add(new Point(xh - 1, yh));
                p.add(new Point(xh + 1, yh));
            }
            else {
                p.add(new Point(xh, yh + 1));
                p.add(new Point(xh - 1, yh));
                p.add(new Point(xh + 1, yh));
            }
        }
        else {
            if(left) {
                p.add(new Point(xh - 1, yh));
                p.add(new Point(xh, yh + 1));
                p.add(new Point(xh, yh - 1));
            }
            else {
                p.add(new Point(xh + 1, yh));
                p.add(new Point(xh, yh + 1));
                p.add(new Point(xh, yh - 1));
            }
        }

        return p;
        //tiles[xh][yh] = Tile.DOOR;

        //return new Point(xh, yh);

    }

    public Tile[][] flood(int x, int y) {
        HashSet<Point> visited = new HashSet<>();
        ArrayDeque<Point> queue = new ArrayDeque<>();
        queue.add(new Point(x, y));

        while(!queue.isEmpty()) {

            //Dequeue the current node, and add it to visited
            Point current = queue.remove();
            visited.add(current);

            //For each surrounding point, if it has not been visited, and is not null, add it to the queue
            for(int i = -1; i <= 1; i++) {
                for(int j = -1; j <= 1; j++) {
                    //Don't check current
                    if(i == 0 && j == 0) continue;
                    //Only check if not out of bounds
                    if(!(current.getX() + i < 0 || current.getX() + i >= tiles.length ||
                            current.getY() + j < 0 || current.getY() + j >= tiles[0].length)) {

                        //Don't add null points
                        if(tiles[current.getX() + i][current.getY() + j] != Tile.FLOOR) continue;
                        Point to_add = new Point(current.getX() + i, current.getY() + j);

                        //Add the point to the queue if it hasn't yet been visited
                        if(!visited.contains(to_add) && !queue.contains(to_add))
                            queue.add(to_add);
                    }
                }
            }
        }

        //Make the new tile array only containing visited nodes
        Tile[][] newTiles = new Tile[tiles.length][tiles[0].length];
        for(int i = 0; i < tiles.length; i++) {
            for(int j = 0; j < tiles[0].length; j++) {
                if(visited.contains(new Point(i, j))) newTiles[i][j] = Tile.FLOOR;
                else newTiles[i][j] = null;
            }
        }

        return newTiles;
    }

    public int getArea() {
        int count = 0;
        for(int i = 0; i < getWidth(); i++) {
            for(int j = 0; j < getHeight(); j++) {
                if(tiles[i][j] == Tile.FLOOR) count++;
            }
        }

        return count;
    }

    //<editor-fold desc="Getters and Setters">
    public World getParent() {
        return parent;
    }

    public void setParent(World parent) {
        this.parent = parent;
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
    //</editor-fold>
}
