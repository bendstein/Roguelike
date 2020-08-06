package world.geometry;

import java.util.ArrayList;
import java.util.Iterator;

public class Line implements Iterable<Point> {

    private ArrayList<Point> points;

    public Line(int x0, int x1, int y0, int y1) {
        points = new ArrayList<>();

        int dx = Math.abs(x1 - x0);
        int dy = Math.abs(y1 - y0);

        int sx = x0 < x1? 1 : -1;
        int sy = y0 < y1? 1 : -1;

        int err = dx - dy;

        while(true) {
            points.add(new Point(x0, y0));

            if(x0 == x1 && y0 == y1)
                break;

            int e2 = err * 2;

            if(e2 > -dx) {
                err -= dy;
                x0 += sx;
            }
            if(e2 < dx) {
                err += dx;
                y0 += sy;
            }
        }
    }

    public Line(int x0, int x1, int y0, int y1, int maxRange) {
        points = new ArrayList<>();

        int dx = Math.abs(x1 - x0);
        int dy = Math.abs(y1 - y0);

        int sx = x0 < x1? 1 : -1;
        int sy = y0 < y1? 1 : -1;

        int err = dx - dy;

        while(true) {
            points.add(new Point(x0, y0));

            if(x0 == x1 && y0 == y1)
                break;

            if(points.size() >= maxRange) break;

            int e2 = err * 2;

            if(e2 > -dx) {
                err -= dy;
                x0 += sx;
            }
            if(e2 < dx) {
                err += dx;
                y0 += sy;
            }
        }
    }

    public Line(ArrayList<Point> points) {
        this.points = points;
    }

    public ArrayList<Point> getPoints() {
        return points;
    }

    public void setPoints(ArrayList<Point> points) {
        this.points = points;
    }

    public int size() {
        return points.size();
    }

    @Override
    public Iterator<Point> iterator() {
        return points.iterator();
    }
}
