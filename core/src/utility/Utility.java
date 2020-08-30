package utility;

import creatureitem.Creature;
import creatureitem.Player;
import game.Main;
import world.Level;
import world.Tile;
import world.geometry.AStarPoint;
import world.geometry.Line;
import world.geometry.Point;
import world.thing.DoorBehavior;
import world.thing.Stairs;

import java.util.*;

public class Utility {

    public static String makeSecondPerson(String thirdPerson) {
        String[] words = thirdPerson.split(" ");
        words[0] = words[0].concat("s");

        StringBuilder builder = new StringBuilder();
        for(String word : words) {
            builder.append(" ");

            builder.append(word);
        }

        return builder.toString().trim();
    }

    /**
     * @param x X coordinate
     * @param y Y coordinate
     * @return A 2d array of tiles who are adjacent to (x, y) in the level's coordinate system.
     */
    public static Tile[][] getAdjacentTiles(Tile[][] tiles, int x, int y) {
        Tile[][] adj = new Tile[3][3];

        for(int i = -1; i <= 1; i++) {
            for(int j = -1; j <= 1; j++) {
                if(x + i > 0 && x + i < tiles.length && y + j > 0 && y + j < tiles[0].length)
                    adj[i + 1][j + 1] = tiles[x + i][y + j];
                else
                    adj[i + 1][j + 1] = null;
            }
        }

        return adj;
    }

    public static int isAdjacentTo(Tile[][] tiles, Tile t, int x, int y, boolean four) {
        Tile[][] adj = getAdjacentTiles(tiles, x, y);

        int count = 0;

        for(int i = 0; i < 3; i++) {
            for(int j = 0; j < 3; j++) {
                if(i == 1 && j == 1) continue;

                if(four) {
                    if((i == 0 && j == 0) || (i == 0 && j == 2) || (i == 2 && j == 0) || (i == 2 && j == 2)) continue;
                }

                else if(adj[i][j] == t) count++;
            }
        }

        return count;
    }

    /**
     * @param tileCosts 2d array of tiles, in terms of their costs
     * @param p0 Origin point
     * @param p1 Destination point
     * @return A list of points making up the shortest path form p0 to p1
     */
    /*
    public static Stack<AStarPoint> aStar(int[][] tileCosts, Point p0, Point p1) {

        AStarPoint initial = new AStarPoint(p0.getX(), p0.getY());
        AStarPoint destination = new AStarPoint(p1.getX(), p1.getY());

        //Map from each point to the point preceding it
        HashMap<AStarPoint, AStarPoint> cameFrom = new HashMap<>();
        cameFrom.put(initial, null);

        //Map from each point to the cost it took to get there
        HashMap<AStarPoint, Integer> costSoFar = new HashMap<>();
        costSoFar.put(initial, 0);

        //Points to visit
        PriorityQueue<AStarPoint> heap = new PriorityQueue<>();
        heap.add(initial);

        AStarPoint current = null, next;
        int newCost;
        while (!heap.isEmpty()) {
            current = heap.remove();

            if (current.equals(destination)) break;

            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    int x = current.getX() + i;
                    int y = current.getY() + j;
                    if (x < 0 || x >= tileCosts.length || y < 0 || y >= tileCosts[0].length)
                        continue;
                    if (tileCosts[x][y] < 0)
                        continue;

                    next = new AStarPoint(x, y);
                    newCost = costSoFar.get(current) + tileCosts[x][y];

                    if (!costSoFar.containsKey(next) || newCost < costSoFar.get(next)) {
                        costSoFar.put(next, newCost);
                        next.setPriority(newCost + next.chebychevDistanceFrom(destination));
                        heap.add(next);
                        cameFrom.put(next, current);
                    }
                }
            }
        }

        Stack<AStarPoint> points = new Stack<>();
        while(current != null) {
            points.push(current);
            current = cameFrom.get(current);
        }

        return points;

    }

     */

    public static Stack<AStarPoint> aStar(int[][] tileCosts, int heuristic, Point p0, Point p1) {

        AStarPoint initial = new AStarPoint(p0.getX(), p0.getY());
        AStarPoint destination = new AStarPoint(p1.getX(), p1.getY());

        //Map from each point to the point preceding it
        HashMap<AStarPoint, AStarPoint> cameFrom = new HashMap<>();
        cameFrom.put(initial, null);

        //Map from each point to the cost it took to get there
        HashMap<AStarPoint, Integer> costSoFar = new HashMap<>();
        costSoFar.put(initial, 0);

        //Points to visit
        PriorityQueue<AStarPoint> heap = new PriorityQueue<>();
        heap.add(initial);

        AStarPoint current = null, next;
        int newCost;
        while (!heap.isEmpty()) {
            current = heap.remove();

            if(current.equals(destination)) break;

            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    int x = current.getX() + i;
                    int y = current.getY() + j;
                    if (x < 0 || x >= tileCosts.length || y < 0 || y >= tileCosts[0].length)
                        continue;

                    next = new AStarPoint(x, y);
                    boolean ignorecost = tileCosts[destination.getX()][destination.getY()] < 0;

                    if (tileCosts[x][y] < 0 && !ignorecost)
                        continue;

                    newCost = costSoFar.get(current) + (ignorecost? 1 : tileCosts[x][y]);

                    if (!costSoFar.containsKey(next) || newCost < costSoFar.get(next)) {
                        costSoFar.put(next, newCost);
                        double heuristic_cost;

                        switch (heuristic) {
                            case Point.DISTANCE_CHEBYCHEV: {
                                heuristic_cost = next.chebychevDistanceFrom(destination);
                                break;
                            }
                            case Point.DISTANCE_EUCLIDEAN: {
                                heuristic_cost = next.euclideanDistanceFrom(destination);
                                break;
                            }
                            case Point.DISTANCE_MANHATTAN: {
                                heuristic_cost = next.manhattanDistanceFrom(destination);
                                break;
                            }
                            default: {
                                heuristic_cost = 0;
                                break;
                            }
                        }
                        next.setPriority(newCost + (int)heuristic_cost);
                        heap.add(next);
                        cameFrom.put(next, current);
                    }

                }

            }
            
        }

        Stack<AStarPoint> points = new Stack<>();
        while(current != null) {
            points.push(current);
            current = cameFrom.get(current);
        }

        if(!points.contains(destination))
            System.out.println();

        return points;

    }

    public static Stack<AStarPoint> aStarWithVision(int[][] tileCosts, Creature c, int heuristic, Point p0, Point p1) {

        AStarPoint initial = new AStarPoint(p0.getX(), p0.getY());
        AStarPoint destination = new AStarPoint(p1.getX(), p1.getY());

        //Map from each point to the point preceding it
        HashMap<AStarPoint, AStarPoint> cameFrom = new HashMap<>();
        cameFrom.put(initial, null);

        //Map from each point to the cost it took to get there
        HashMap<AStarPoint, Integer> costSoFar = new HashMap<>();
        costSoFar.put(initial, 0);

        //Points to visit
        PriorityQueue<AStarPoint> heap = new PriorityQueue<>();
        heap.add(initial);

        AStarPoint current = null, next;
        int newCost;
        while (!heap.isEmpty()) {
            current = heap.remove();

            if(current.equals(destination)) break;

            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    int x = current.getX() + i;
                    int y = current.getY() + j;
                    if (x < 0 || x >= tileCosts.length || y < 0 || y >= tileCosts[0].length)
                        continue;

                    next = new AStarPoint(x, y);
                    boolean ignorecost = tileCosts[destination.getX()][destination.getY()] < 0;

                    if ((tileCosts[x][y] < 0 && !ignorecost))
                        continue;

                    if(c instanceof Player && !((Player) c).getSeen(x, y))
                        continue;

                    newCost = costSoFar.get(current) + (ignorecost? 1 : tileCosts[x][y]);

                    if (!costSoFar.containsKey(next) || newCost < costSoFar.get(next)) {
                        costSoFar.put(next, newCost);
                        double heuristic_cost;

                        switch (heuristic) {
                            case Point.DISTANCE_CHEBYCHEV: {
                                heuristic_cost = next.chebychevDistanceFrom(destination);
                                break;
                            }
                            case Point.DISTANCE_EUCLIDEAN: {
                                heuristic_cost = next.euclideanDistanceFrom(destination);
                                break;
                            }
                            case Point.DISTANCE_MANHATTAN: {
                                heuristic_cost = next.manhattanDistanceFrom(destination);
                                break;
                            }
                            default: {
                                heuristic_cost = 0;
                                break;
                            }
                        }
                        next.setPriority(newCost + (int)heuristic_cost);
                        heap.add(next);
                        cameFrom.put(next, current);
                    }

                }

            }

        }

        Stack<AStarPoint> points = new Stack<>();
        while(current != null) {
            points.push(current);
            current = cameFrom.get(current);
        }

        if(!points.contains(destination))
            System.out.println();

        return points;

    }

    public static Line aStarPathToLine(Stack<AStarPoint> path) {
        return new Line(new ArrayList<>(path));
    }

    public static int[][] toCostArray(Tile[][] tiles) {
        int[][] costs = new int[tiles.length][tiles[0].length];
        for(int i = 0; i < tiles.length; i++) {
            for(int j = 0; j < tiles[0].length; j++) {
                if(tiles[i][j] == Tile.FLOOR) costs[i][j] = 1;
                else costs[i][j] = -1;
            }
        }

        return costs;
    }

    public static int[][] toCostArray(Level level) {
        return level.calculateCosts();
    }

    /**
     * @param x The x coordinate of the mouse on the screen
     * @param y The y coordinate of the mouse on the screen
     * @return The cursors coordinates mapped to the world
     */
    public static Point roundCursor(float x, float y) {
        return new Point((int) Math.floor(x/ Main.getTileWidth()), (int) Math.floor(y/ Main.getTileHeight()));
    }

    public static int getDistance(Point a, Point b) {
        Line l = new Line(a.getX(), b.getX(), a.getY(), b.getY());
        return l.size();
    }

    public static int getChebshevDistance(int x0, int x1, int y0, int y1) {
        return Math.max(Math.abs(x1 - x0), Math.abs(y1 - y0));
    }

    public static int getManhattanDistance(int x0, int x1, int y0, int y1) {
        return Math.abs(x1 - x0) + Math.abs(y1 - y0);
    }

    public static double getEuclideanDistance(int x0, int x1, int y0, int y1) {
        return Math.sqrt(Math.pow(x0 - x1, 2) + Math.pow(y0 - y1, 2));
    }

    public static void printTiles(Tile[][] tiles) {
        for(int i = 0; i < tiles.length; i++) {
            StringBuilder s = new StringBuilder();
            for(int j = 0; j < tiles.length; j++) {
                if(tiles[i][j] == null)
                    s.append("0");
                else
                    s.append(tiles[i][j].getGlyph());
            }
            System.out.println(s);
        }
    }

    public static void printLevel(Level l) {
        printLevel(l, null);
    }

    public static void printLevel(Level l, ArrayList<Point> markers) {
        for(int i = 0; i < l.getWidth(); i++) {
            StringBuilder s = new StringBuilder();
            for(int j = 0; j < l.getHeight(); j++) {
                if(l.getCreatureAt(i, j) != null)
                    s.append("C");
                else if(l.getItemAt(i, j) != null)
                    s.append("$");
                else if(l.getThingAt(i, j) != null)
                    if(l.getThingAt(i, j).getBehavior() instanceof DoorBehavior) s.append("|");
                    else if(l.getThingAt(i, j) instanceof Stairs) s.append(">");
                    else s.append("T");
                else if(markers != null && markers.contains(new Point(i, j)))
                    s.append("Q");
                else if(l.getTileAt(i, j) != null)
                    s.append(l.getTileAt(i, j).getGlyph());
                else
                    s.append("0");
            }
            System.out.println(s);
        }
    }

    public static <T> String findMostSimilarKey(HashMap<String, T> map, String key) {

        if(map.containsKey(key)) return key;

        else {
            //If key not in map, find the most similar one

            double maxStringSimilarity = Double.MIN_VALUE;
            double minValueDifference = Double.MAX_VALUE;
            String similar = "";
            for(String str : map.keySet()) {
                boolean sLonger = key.length() >= str.length();

                /*
                 * Split all keys, and the given key, on the delimeter "_"
                 */
                String[] longer = sLonger? key.split("_") : str.split("_");
                String[] shorter = !sLonger? key.split("_") : str.split("_");

                /*
                 * Calculate string similarity by dividing the cardinality of the intersection between the 2 strings by
                 * their union
                 */
                boolean[] intersect = new boolean[longer.length];
                int union = longer.length + shorter.length;

                for(int i = 0; i < longer.length; i++)
                    intersect[i] = i < shorter.length && longer[i].equals(shorter[i]);

                double strOverlap = ((double)intersect.length)/union;

                //If the string has an overlap at least that of the recorded max, check distance between numeric values
                if(strOverlap >= maxStringSimilarity) {
                    double valOverlap = 0d;
                    for(int i = 0; i < intersect.length; i++) {
                        if(!intersect[i]) {
                            double v1, v2;
                            try {
                                v1 = Double.parseDouble(longer[i]);
                                v2 = i < shorter.length? Double.parseDouble(shorter[i]) : 0d;
                            } catch (NumberFormatException ex) {
                                continue;
                            }

                            valOverlap += Math.pow(v1 - v2, 2);
                        }
                    }

                    if(Math.sqrt(valOverlap) < minValueDifference) {
                        maxStringSimilarity = strOverlap;
                        minValueDifference = valOverlap;
                        similar = str;
                    }

                }

            }

            /*
             * If there are any keys in the map, return the most similar one.
             */
            if(map.containsKey(similar)) {
                return similar;
            }
            else {
                return null;
            }
        }

    }
}
