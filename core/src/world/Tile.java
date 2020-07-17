package world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public enum Tile {
    FLOOR('_', "data/FloorSheet.png", "Floor", 0),
    WALL('#', "data/WallSheet.png", "Wall", 1),
    BOUNDS('\\', "data/BoundarySheet.png", "Boundary", -1),
    STAIRS('<', "data/StairsSheet.png", "Stairs Up", 2),
    DOOR('+', "data/DoorSheet.png", "Door", 1),
    CURSOR('_', "data/CursorSheet.png", "Green Cursor", -2);

    //<editor-fold desc="Instance Variables">
    /**
     * The texture corresponding to the tile
     */
    private Texture texture;

    private TextureRegion[] sprites;

    /**
     * The name of the tile
     */
    private String name;

    private char glyph;

    private int type;
    //</editor-fold>

    Tile(char glyph, String texturePath, String name, int type) {
        this.glyph = glyph;
        this.texture = new Texture(Gdx.files.internal(texturePath));
        sprites = new TextureRegion[]{
                new TextureRegion(texture, 0, 0, 24, 28),
                new TextureRegion(texture, 24, 0, 24, 28),
                new TextureRegion(texture, 48, 0, 24, 28),
                new TextureRegion(texture, 72, 0, 24, 28),
                new TextureRegion(texture, 0, 28, 24, 28),
                new TextureRegion(texture, 24, 28, 24, 28),
                new TextureRegion(texture, 48, 28, 24, 28),
                new TextureRegion(texture, 72, 28, 24, 28),
                new TextureRegion(texture, 0, 56, 24, 28),
                new TextureRegion(texture, 24, 56, 24, 28),
                new TextureRegion(texture, 48, 56, 24, 28),
                new TextureRegion(texture, 72, 56, 24, 28),
                new TextureRegion(texture, 0, 84, 24, 28),
                new TextureRegion(texture, 24, 84, 24, 28),
                new TextureRegion(texture, 48, 84, 24, 28),
                new TextureRegion(texture, 72, 84, 24, 28),
                new TextureRegion(texture, 0, 112, 24, 28),
                new TextureRegion(texture, 24, 112, 24, 28),
                new TextureRegion(texture, 48, 112, 24, 28),
                new TextureRegion(texture, 72, 112, 24, 28),
        };
        this.name = name;
        this.type = type;
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

    public TextureRegion[] getSprites() {
        return sprites;
    }

    public TextureRegion getSprite(int i) {
        return sprites[i];
    }

    //</editor-fold>

    /**
     * @return true if a creature can dig through this tile
     */
    public boolean isDiggable() {
        return this == Tile.WALL;
    }

    /**
     * @return true if a creature can walk through this tile without digging
     */
    public boolean isGround() {
        return this != WALL && this != BOUNDS;
    }

    public boolean isPassable() {
        return this != WALL && this != BOUNDS && this != DOOR;
    }
}
