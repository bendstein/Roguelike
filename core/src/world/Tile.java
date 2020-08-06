package world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import org.w3c.dom.Text;
import world.thing.Light;

public enum Tile {
    FLOOR('_', "data/FloorSheet.png", "Floor", 0),
    WALL('#', "data/WallSheet.png", "Wall", 5),
    BOUNDS('\\', "data/BoundarySheet.png", "Boundary", 0),
    STAIRS('<', "data/StairsSheet.png", "Stairs Up", 0),
    DOOR('+', "data/DoorSheet.png", "Door", 0),
    CURSOR('_', "data/CursorSheet.png", "Cursor", 0),
    BRAZIER('|', "data/BrazierSheet.png", "Brazier", 0),
    LIGHT('_', "data/LightSheet.png", "Light", 0);

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

    /**
     * Index of the tile to display under cursor in prefab builder.
     */
    private int neutral;
    //</editor-fold>

    Tile(char glyph, String texturePath, String name, int neutral) {
        this.glyph = glyph;
        this.texture = new Texture(Gdx.files.internal(texturePath));
        this.neutral = neutral;
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

    /**
     * @param s The sprite to get
     * @param part The part of the sprite to get: 0 - Corner, 1 - Top/Bottom Side, 2 - Left/Right Side
     * @return A portion of the sprite
     */
    public TextureRegion getSpritePart(int s, int part) {
        TextureRegion[][] split;

        switch (part) {
            case 0: {
                split = sprites[s].split(12, 14);
                return split[0][0];
            }
            case 1: {
                split = sprites[s].split(24, 14);
                return split[0][0];
            }
            case 2: {
                split = sprites[s].split(12, 28);
                return split[0][0];
            }
            default: {
                return sprites[s];
            }
        }
        /*
        switch(part) {
            case 0: {
                split = sprites[s].split(12, 14);
                return split[0][0];
            }
            case 1: {
                split = sprites[s].split(24, 14);
                return split[0][0];
            }
            case 2: {
                split = sprites[s].split(12, 14);
                return split[1][0];
            }
            case 3: {
                split = sprites[s].split(12, 28);
                return split[0][0];
            }
            case 4: {
                split = sprites[s].split(12, 28);
                return split[0][1];
            }
            case 5: {
                split = sprites[s].split(12, 14);
                return split[0][1];
            }
            case 6: {
                split = sprites[s].split(24, 14);
                return split[1][0];
            }
            case 7: {
                split = sprites[s].split(12, 14);
                return split[1][1];
            }
            default:
                return sprites[s];
        }

         */
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

    public boolean isNotPlacable() {
        return this == STAIRS || this == DOOR || this == BRAZIER || this == LIGHT || this == CURSOR;
    }

    public static boolean exists(String s) {
        for(Tile t : Tile.values())
            if(t.name.toUpperCase().equals(s.toUpperCase())) return true;
        return false;
    }

    public static Tile getTile(String s) {
        for(Tile t : Tile.values())
            if(t.name.toUpperCase().equals(s.toUpperCase())) return t;
        return null;
    }

    public int getNeutral() {
        return neutral;
    }

    public void setNeutral(int neutral) {
        this.neutral = neutral;
    }
}
