package utility;

import world.World;
import world.geometry.Point;

import java.util.ArrayList;

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

    /*
    public static ArrayList<Point> dijkstra(World world, Point p0, Point p1) {

        class Node {

            Point p;

            Node nw, n, ne, w, e, sw, s, se;

            public Node(Point p) {
                this.p = p;
                nw = n = ne = w = e = sw = s = se = null;
            }

            public Node(Node parent, int position) {
                nw = n = ne = w = e = sw = s = se = null;
                int x, y;

                switch (position) {
                    case 7: {
                        nw = parent;
                        parent.se = this;
                        x = parent.p.getX() - 1;
                        y = parent.p.getY() + 1;
                        break;
                    }
                    case 8: {
                        n = parent;
                        parent.s = this;
                        x = parent.p.getX();
                        y = parent.p.getY() + 1;
                        break;
                    }
                    case 9: {
                        ne = parent;
                        parent.sw = this;
                        x = parent.p.getX() + 1;
                        y = parent.p.getY() + 1;
                        break;
                    }
                    case 4: {
                        e = parent;
                        parent.w = this;
                        x = parent.p.getX() - 1;
                        y = parent.p.getY();
                        break;
                    }
                    case 6: {
                        w = parent;
                        parent.e = this;
                        x = parent.p.getX() + 1;
                        y = parent.p.getY();
                        break;
                    }
                    case 1: {
                        sw = parent;
                        parent.ne = this;
                        x = parent.p.getX() - 1;
                        y = parent.p.getY() - 1;
                        break;
                    }
                    case 2: {
                        s = parent;
                        parent.n = this;
                        x = parent.p.getX();
                        y = parent.p.getY() - 1;
                        break;
                    }
                    case 3: {
                        se = parent;
                        parent.nw = this;
                        x = parent.p.getX() + 1;
                        y = parent.p.getY() - 1;
                        break;
                    }
                    default:
                        x = y = 0;
                }

                p = new Point(x, y);
            }

            public void setAtPosition(Node node, int position) {
                switch (position) {
                    case 7: {
                        nw = node;
                        if(node != null) node.sw = this;
                        break;
                    }
                    case 8: {
                        n = node;
                        if(node != null) node.s = this;
                        break;
                    }
                    case 9: {
                        ne = node;
                        if(node != null) node.sw = this;
                        break;
                    }
                    case 4: {
                        e = node;
                        if(node != null) node.w = this;
                        break;
                    }
                    case 6: {
                        w = node;
                        if(node != null) node.e = this;
                        break;
                    }
                    case 1: {
                        sw = node;
                        if(node != null) node.ne = this;
                        break;
                    }
                    case 2: {
                        s = node;
                        if(node != null) node.n = this;
                        break;
                    }
                    case 3: {
                        se = node;
                        if(node != null) node.nw = this;
                        break;
                    }
                }
            }

            public void addChild(int position) {
                Node child = new Node(this, position);
            }
        }

        Node initial = new Node(p0);


    }

     */
}
