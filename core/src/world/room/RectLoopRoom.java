package world.room;

import world.Tile;

public class RectLoopRoom extends RectRoom{

    public RectLoopRoom(int x, int y, int width, int height, int width_inner, int height_inner) {
        super(x, y, width, height);
        if(width_inner < width && height_inner < height) fillCenter(width_inner, height_inner);
    }

    public RectLoopRoom(int width, int height, int width_inner, int height_inner) {
        super(width, height);
        if(width_inner < width && height_inner < height) fillCenter(width_inner, height_inner);
    }

    public void fillCenter(int width_inner, int height_inner) {
        for(int i = (int) Math.floor((getWidth() - width_inner)/2d); i < getWidth() - (int) Math.floor((getWidth() - width_inner)/2d); i++) {
            for(int j = (int) Math.floor((getHeight() - height_inner)/2d); j < getHeight() - (int) Math.floor((getHeight() - height_inner)/2d); j++) {
                tiles[i][j] = null;
            }
        }
    }
}
