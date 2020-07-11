package world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

public enum Tile {
    FLOOR('_', "data/Floor.png", "Floor"),
    WALL('#', "data/Wall.png", "Wall"),
    BOUNDS('\\', "data/Boundary.png", "Boundary"),
    STAIRS_DOWN('<', "data/Downstairs.png", "Stairs Down"),
    STAIRS_UP('>', "data/Upstairs.png", "Stairs Up"),
    DOOR('+', "data/Door.png", "Door");

    //<editor-fold desc="Instance Variables">
    /**
     * The texture corresponding to the tile
     */
    private Texture texture;

    /**
     * The name of the tile
     */
    private String name;

    private char glyph;
    //</editor-fold>

    Tile(char glyph, String texturePath, String name) {
        this.glyph = glyph;
        this.texture = new Texture(Gdx.files.internal(texturePath));
        this.name = name;
    }

    //<editor-fold desc="Getters">
    public Texture getTexture() {
        return texture;
    }

    public String getName() {
        return name;
    }

    public char getGlyph() {
        return glyph;
    }

    //</editor-fold>

    /**
     * @return true if a creature can dig through this tile
     */
    public boolean isDiggable() {
        return this == Tile.WALL;
    }

    /**
     * @return True if a creature can open this tile
     */
    public boolean isOpenable() {
        return this == Tile.DOOR;
    }

    /**
     * @return true if a creature can walk through this tile without digging
     */
    public boolean isGround() {
        return this != WALL && this != BOUNDS;
    }
}
