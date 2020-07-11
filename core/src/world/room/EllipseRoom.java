package world.room;

import world.Tile;

public class EllipseRoom extends Room {

    public EllipseRoom(int r) {
        super(0, 0, (2 * r) + 4, (2 * r) + 4);

        generate(r, r);
    }
    public EllipseRoom(int x, int y, int r) {
        super(x, y, (2 * r) + 4, (2 * r) + 4);

        generate(r, r);
    }

    public EllipseRoom(int x, int y, int xmod, int ymod) {
        super(x, y, (2 * xmod) + 4, (2 * ymod) + 4);

        generate(xmod, ymod);
    }

    public EllipseRoom(int width, int height) {
        super(0, 0, width, height);

        generate((width)/2, (height)/2);
    }


    public void generate(int xmod, int ymod) {

        for(int i = 0; i < getWidth(); i++)
            for(int j = 0; j < getHeight(); j++)
                tiles[i][j] = null;


        int xc = (int) Math.floor(getWidth()/2f);
        int yc = (int) Math.floor(getHeight()/2f);

        //Make a circle of floor
        for(int i = xc; i <= xmod + xc; i++) {
            for(int j = yc; j <= ymod + yc; j++) {
                int lhs = (int) Math.ceil((Math.pow(i - xc, 2)/Math.pow(xmod, 2) + Math.pow(j - yc, 2)/Math.pow(ymod, 2)));
                if(lhs <= 1) {
                    tiles[i][j] = Tile.FLOOR;
                    tiles[(-i + xc) + xc][j] = Tile.FLOOR;
                    tiles[i][(-j + yc) + yc] = Tile.FLOOR;
                    tiles[(-i + xc) + xc][(-j + yc) + yc] = Tile.FLOOR;
                }

            }
        }

        /*
        //Make walls around it
        for(int i = 0; i < getWidth(); i++) {
            for (int j = 0; j < getHeight(); j++) {
                if (tiles[i][j] == Tile.FLOOR) {
                    if (i + 1 < getWidth() && (tiles[i + 1][j] == Tile.BOUNDS || tiles[i + 1][j] == null))
                        tiles[i + 1][j] = Tile.WALL;
                    if (i - 1 > 0 && (tiles[i - 1][j] == Tile.BOUNDS || tiles[i - 1][j] == null))
                        tiles[i - 1][j] = Tile.WALL;
                    if (j + 1 < getHeight() && (tiles[i][j + 1] == Tile.BOUNDS || tiles[i][j + 1] == null))
                        tiles[i][j + 1] = Tile.WALL;
                    if (j - 1 > 0 && (tiles[i][j - 1] == Tile.BOUNDS || tiles[i][j - 1] == null))
                        tiles[i][j - 1] = Tile.WALL;

                }
            }
        }

        for(int i = 0; i < getWidth(); i++) {
            for (int j = 0; j < getHeight(); j++) {
                if (tiles[i][j] == Tile.WALL) {
                    if(i - 1 > 0 && i + 1 < getWidth() && tiles[i-1][j] == null && tiles[i+1][j] == null)
                        tiles[i-1][j] = tiles[i+1][j] = Tile.WALL;
                    if(j - 1 > 0 && j + 1 < getHeight() && tiles[i][j-1] == null && tiles[i][j+1] == null)
                        tiles[i][j-1] = tiles[i][j+1] = Tile.WALL;
                }
            }
        }

         */

    }
}
