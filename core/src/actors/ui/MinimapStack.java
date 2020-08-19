package actors.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import world.geometry.Point;

/**
 * A LibGDX Actor Stack where each stack has a reference to its location in the 2D array,
 * for the minimap.
 */
public class MinimapStack extends Stack {

    private Point p;

    public MinimapStack(int x, int y) {
        super();
        p = new Point(x, y);
    }

    public MinimapStack(Point p) {
        super();
        this.p = p;
    }

    public MinimapStack() {
        super();
        p = new Point(-1, -1);
    }

    //<editor-fold desc="Getters and Setters">
    public Point getP() {
        return p;
    }

    public int getPX() {
        return p == null? -1 : p.getX();
    }

    public int getPY() {
        return p == null? -1 : p.getY();
    }

    public void setP(Point p) {
        this.p = p;
    }

    public void setPX(int x) {
        if(p == null) p = new Point(x, -1);
        else p.setX(x);
    }

    public void setPY(int y) {
        if(p == null) p = new Point(-1, y);
        else p.setY(y);
    }
    //</editor-fold>
}
