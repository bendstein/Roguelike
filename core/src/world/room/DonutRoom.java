package world.room;

import world.Tile;

public class DonutRoom extends EllipseRoom {

    public DonutRoom(int r, int r_inner) {
        super(r);
        if(r_inner < r) fillCenter(r_inner, r_inner);
    }
    public DonutRoom(int x, int y, int r, int r_inner) {
        super(x, y, r);
        if(r_inner < r) fillCenter(r_inner, r_inner);
    }

    public DonutRoom(int x, int y, int xmod, int ymod, int xmod_inner, int ymod_inner) {
        super(x, y, xmod, ymod);
        if(xmod_inner < xmod && ymod_inner < ymod) fillCenter(xmod_inner, ymod_inner);
    }

    public void fillCenter(int xmod_inner, int ymod_inner) {
        int xc = (int) Math.floor(getWidth()/2f);
        int yc = (int) Math.floor(getHeight()/2f);

        //Make a circle of floor
        for(int i = xc; i <= xmod_inner + xc; i++) {
            for(int j = yc; j <= ymod_inner + yc; j++) {
                int lhs = (int) Math.ceil((Math.pow(i - xc, 2)/Math.pow(xmod_inner, 2) + Math.pow(j - yc, 2)/Math.pow(ymod_inner, 2)));
                if(lhs <= 1) {
                    tiles[i][j] = null;
                    tiles[(-i + xc) + xc][j] = null;
                    tiles[i][(-j + yc) + yc] = null;
                    tiles[(-i + xc) + xc][(-j + yc) + yc] = null;
                }

            }
        }
    }
}
