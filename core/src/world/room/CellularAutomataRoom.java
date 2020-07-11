package world.room;

import world.Tile;

import java.util.Random;

public class CellularAutomataRoom extends Room {

    private final float ALIVE = 0.4f;
    private final int DIE = 2;
    private final int REMAIN = 4;
    private final int REVIVE_MIN = 5;
    private final int REVIVE_MAX = 8;
    private final int STEPS = 2;
    private final float MIN_AREA_RATIO = .2f;

    public CellularAutomataRoom(int w, int h, Random random) {
        super(w, h);
        boolean[][] cells = new boolean[w][h];

        int tries = 0;
        do {
            cells = init(cells, random);
            for (int i = 0; i < STEPS; i++) cells = step(cells);

            this.tiles = toRoom(cells);

            int rx, ry;
            tries = 0;
            do {
                rx = random.nextInt(w);
                ry = random.nextInt(h);
                tries++;

                if(tries > 5) break;
            } while(tiles[rx][ry] != Tile.FLOOR);

            if(tries > 5) continue;

            this.tiles = flood(rx, ry);
        } while (!(getArea() >= (w * h * MIN_AREA_RATIO)) || tries > 5);


    }

    public boolean[][] init(boolean[][] cells, Random random) {
        for(int i = 0; i < cells.length; i++) {
            for(int j = 0; j < cells[0].length; j++) {
                if(random.nextFloat() < ALIVE)
                    cells[i][j] = true;
                else
                    cells[i][j] = false;
            }
        }

        return cells;
    }

    public boolean[][] step(boolean[][] cells) {
        boolean[][] newCells = new boolean[cells.length][cells[0].length];

        for(int i = 0; i < cells.length; i++) {
            for(int j = 0; j < cells[0].length; j++) {
                int adj = getAdjDead(cells, i, j);
                if(cells[i][j]) {
                    if(adj < DIE) newCells[i][j] = false;
                    else if(adj >= REMAIN) newCells[i][j] = true;
                    else newCells[i][j] = false;
                }
                else {
                    if(adj >= REVIVE_MIN && adj <= REVIVE_MAX) newCells[i][j] = true;
                    else newCells[i][j] = false;
                }
            }
        }

        return newCells;
    }

    public int getAdjDead(boolean[][] cells, int x, int y) {
        int count = 0;

        for(int i = -1; i <= 1; i++) {
            for(int j = -1; j <= 1; j++) {
                if(x + i < 0 || x + i >= cells.length || y + j < 0 || y + j >= cells[0].length) continue;
                else if(x + i == 0 && y + j == 0) continue;
                else if(!cells[x + i][y + j]) count++;
            }
        }

        return count;
    }

    public Tile[][] toRoom(boolean[][] cells) {
        Tile[][] tiles = new Tile[cells.length][cells[0].length];
        for(int i = 0; i < cells.length; i++) {
            for(int j = 0; j < cells[0].length; j++) {
                if(cells[i][j]) tiles[i][j] = Tile.FLOOR;
                else tiles[i][j] = null;
            }
        }

        return tiles;
    }
}
