package creatureitem.item;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

public class Item {

    private char glyph;

    private Texture texture;

    private String name;

    public Item(char glyph, String texturePath, String name) {
        this.glyph = glyph;
        this.texture = new Texture(Gdx.files.internal(texturePath));
        this.name = name;
    }

    public char getGlyph() {
        return glyph;
    }

    public void setGlyph(char glyph) {
        this.glyph = glyph;
    }

    public Texture getTexture() {
        return texture;
    }

    public void setTexture(Texture texture) {
        this.texture = texture;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
