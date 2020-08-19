package world.room;

import world.Tile;
import world.geometry.Point;

import java.util.ArrayList;
import java.util.Random;

public class LRoom extends Room {

    /**
     * Width and height of the base
     */
    private int w, h;

    /**
     * Width and height of the shorter part
     */
    private int w1, h1;

    /**
     * Orientation of the room, such that the smaller portion is pointing in said orientation.
     * UP = 0, LEFT = 1, RIGHT = 2, DOWN = 3
     */
    private int orientation;

    public final int UL = 0, UR = 1 , RU = 2, RD = 3, DR = 4, DL = 5, LD = 6, LU = 7;

    public LRoom(int x, int y, int w, int h, int w1, int h1, int orientation) {
        super(x, y, w + w1, h + Math.max(h1, 1));
        this.w = w;
        this.h = h;
        this.w1 = w1;
        this.h1 = Math.max(h1, 1);
        this.orientation = orientation;

        build();
    }

    public LRoom(int w, int h, int w1, int h1, int orientation) {
        super(0, 0, w + w1, h + Math.max(h1, 1));
        this.w = w;
        this.h = h;
        this.w1 = w1;
        this.h1 = Math.max(h1, 1);
        this.orientation = orientation;

        build();
    }

    public void build() {
        for(int i = 0; i < w + this.w1; i++) {
            for(int j = 0; j < h + this.h1; j++) {
                if(i == 0 || j == 0) System.out.print("");
                    //tiles[i][j] = Tile.WALL;
                else if(j < h - 1) {
                    if(i == w + this.w1 - 1) System.out.print("");
                        //tiles[i][j] = Tile.WALL;
                    else
                        tiles[i][j] = Tile.FLOOR;
                }
                else if(j == h - 1) {
                    if(i < w - 1)
                        tiles[i][j] = Tile.FLOOR;
                    else System.out.print("");
                    //tiles[i][j] = Tile.WALL;
                }
                else {
                    if(j == h + this.h1 - 1 && i < w - 1) System.out.print("");
                        //tiles[i][j] = Tile.WALL;
                    else if(i < w - 1)
                        tiles[i][j] = Tile.FLOOR;
                    else if(i == w - 1) System.out.print("");
                        //tiles[i][j] = Tile.WALL;
                    else
                        tiles[i][j] = null;
                }
            }
        }

        //Transform the room as needed
        if(orientation == UR || orientation == RD || orientation == DL || orientation == LU)
            tiles = mirror(tiles, false);
        if(orientation == LU || orientation == LD)
            tiles = rotate(tiles, false, 1);
        else if(orientation == RU || orientation == RD)
            tiles = rotate(tiles, true, 1);
        else if(orientation == DR || orientation == DL)
            tiles = rotate(tiles, true, 2);
    }
}
