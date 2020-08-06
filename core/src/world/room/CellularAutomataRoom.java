package world.room;

import utility.Utility;
import world.Tile;

import java.util.Random;

public class CellularAutomataRoom extends Room {

    private float alive = 0.4f;
    private int die = 2;
    private int remain = 4;
    private int reviveMin = 5;
    private int reviveMax = 8;
    private int steps = 2;
    private float minAreaRatio = .2f;

    public CellularAutomataRoom(int w, int h, Random random) {
        super(w, h);
        start(w, h, random);
    }

    public CellularAutomataRoom(int w, int h, Random random, float ... values) {
        super(w, h);
        for(int i = 0; i < values.length; i++) {
            switch (i) {
                case 0: {
                    if(values[i] <= 1 && values[i] >= 0) alive = values[i];
                    break;
                }
                case 1: {
                    if(values[i] > 0) die = (int)Math.ceil(values[i]);
                    break;
                }
                case 2: {
                    if(values[i] >= 0) remain = (int)Math.ceil(values[i]);
                    break;
                }
                case 3: {
                    if(values[i] >= 0) {
                        if(values.length <= i + 1 && values[i] <= reviveMax)
                            reviveMin = (int)Math.ceil(values[i]);
                        else if(values[i] <= values[i + 1])
                            reviveMin = (int)Math.ceil(values[i]);
                    }

                    break;
                }
                case 4: {
                    if(values[i] >= 0) {
                        if(values[i] >= values[i - 1])
                            reviveMax = (int)Math.ceil(values[i]);
                    }

                    break;
                }
                case 5: {
                    if(values[i] > 0) steps = (int)Math.ceil(values[i]);
                    break;
                }
                case 6: {
                    if(values[i] <= 1 && values[i] >= 0) minAreaRatio = values[i];
                    break;
                }
                default: {
                    break;
                }
            }
        }
        start(w, h, random);
    }

    public void start(int w, int h, Random random) {
        boolean[][] cells = new boolean[w][h];

        int tries;
        do {
            cells = init(cells, random);
            for (int i = 0; i < steps; i++) cells = step(cells);

            this.tiles = toRoom(cells);

            smooth(3);

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
        } while (!(getArea() >= (w * h * minAreaRatio)) || tries > 5);

    }

    public boolean[][] init(boolean[][] cells, Random random) {
        for(int i = 0; i < cells.length; i++) {
            for(int j = 0; j < cells[0].length; j++) {
                if(random.nextFloat() < alive)
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
                    if(adj < die) newCells[i][j] = false;
                    else if(adj >= remain) newCells[i][j] = true;
                    else newCells[i][j] = false;
                }
                else {
                    if(adj >= reviveMin && adj <= reviveMax) newCells[i][j] = true;
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
