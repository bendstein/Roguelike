package world.geometry;

import utility.Utility;

import java.util.Objects;
import java.util.Stack;

public class Point {

    //<editor-fold desc="Instance Variables">
    /**
     * X coordinate
     */
    protected int x;

    /**
     * Y coordinate
     */
    protected int y;

    public final static int DISTANCE_MANHATTAN = 0, DISTANCE_CHEBYCHEV = 1, DISTANCE_EUCLIDEAN = 2, DISTANCE_ASTAR = 3;


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

    public void setLocation(int x, int y) {
        this.x = x;
        this.y = y;
    }
    //</editor-fold>

    public int manhattanDistanceFrom(Point p) {
        return Utility.getManhattanDistance(x, p.x, y, p.y);
    }

    public double euclideanDistanceFrom(Point p) {
        return Utility.getEuclideanDistance(x, p.x, y, p.y);
    }

    public int chebychevDistanceFrom(Point p) {
        return Utility.getChebshevDistance(x, p.x, y, p.y);
    }

    public int astarDistanceFrom(Point p, int heuristic, int[][] costs) {
        Stack<AStarPoint> astar = Utility.aStar(costs, heuristic,this, p);
        Line l = Utility.aStarPathToLine(astar);
        if(!l.getPoints().contains(this) || !l.getPoints().contains(p)) return -1;
        else return l.size();
    }

    public double avgDistanceFrom(int type, Line ... lines) {
        return avgDistanceFrom(type, null, lines);
    }

    public double minDistanceFrom(int type, Line ... lines) {
        return minDistanceFrom(type, null, lines);
    }

    public double avgDistanceFrom(int type, int[][] costs, Line ... lines) {
        double sum = 0;
        int count = 0;

        if(type == DISTANCE_ASTAR && costs == null)
            return 0;

        for(Line l : lines) {
            for(Point p : l) {
                count++;
                if(type == DISTANCE_MANHATTAN)
                    sum += manhattanDistanceFrom(p);
                else if(type == DISTANCE_CHEBYCHEV)
                    sum += chebychevDistanceFrom(p);
                else if(type == DISTANCE_EUCLIDEAN)
                    sum += euclideanDistanceFrom(p);
                else if(type == DISTANCE_ASTAR)
                    sum += astarDistanceFrom(p, Point.DISTANCE_CHEBYCHEV, costs);

            }
        }

        return sum/count;
    }

    public double minDistanceFrom(int type, int[][] costs, Line ... lines) {
        double minDistance = Double.MAX_VALUE;

        if((type == DISTANCE_ASTAR && costs == null) || lines == null || lines.length == 0)
            return 0;

        for(Line l : lines) {
            for(Point p : l) {
                if(type == DISTANCE_MANHATTAN)
                    minDistance = Math.min(minDistance, manhattanDistanceFrom(p));
                else if(type == DISTANCE_CHEBYCHEV)
                    minDistance = Math.min(minDistance, chebychevDistanceFrom(p));
                else if(type == DISTANCE_EUCLIDEAN)
                    minDistance = Math.min(minDistance, euclideanDistanceFrom(p));
                else if(type == DISTANCE_ASTAR)
                    minDistance = Math.min(minDistance, astarDistanceFrom(p, Point.DISTANCE_CHEBYCHEV, costs));
            }
        }

        return minDistance;
    }

    public double avgDistanceFrom(int type, Point ... points) {
        return avgDistanceFrom(type, null, points);
    }

    public double minDistanceFrom(int type, Point ... points) {
        return minDistanceFrom(type, null, points);
    }

    public double avgDistanceFrom(int type, int[][] costs, Point ... points) {
        double sum = 0;
        int count = 0;

        if(type == DISTANCE_ASTAR && costs == null)
            return 0;

        for(Point p : points) {
            count++;
            if(type == DISTANCE_MANHATTAN)
                sum += manhattanDistanceFrom(p);
            else if(type == DISTANCE_CHEBYCHEV)
                sum += chebychevDistanceFrom(p);
            else if(type == DISTANCE_EUCLIDEAN)
                sum += euclideanDistanceFrom(p);
            else if(type == DISTANCE_ASTAR)
                sum += astarDistanceFrom(p, Point.DISTANCE_CHEBYCHEV, costs);

        }

        return sum/count;
    }

    public double minDistanceFrom(int type, int[][] costs, Point ... points) {
        double minDistance = Double.MAX_VALUE;

        if((type == DISTANCE_ASTAR && costs == null) || points == null || points.length == 0)
            return 0;

        for(Point p : points) {
            if(type == DISTANCE_MANHATTAN)
                minDistance = Math.min(minDistance, manhattanDistanceFrom(p));
            else if(type == DISTANCE_CHEBYCHEV)
                minDistance = Math.min(minDistance, chebychevDistanceFrom(p));
            else if(type == DISTANCE_EUCLIDEAN)
                minDistance = Math.min(minDistance, euclideanDistanceFrom(p));
            else if(type == DISTANCE_ASTAR)
                minDistance = Math.min(minDistance, astarDistanceFrom(p, Point.DISTANCE_CHEBYCHEV, costs));
        }

        return minDistance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Point)) return false;
        Point point = (Point) o;
        return x == point.x &&
                y == point.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }


}
