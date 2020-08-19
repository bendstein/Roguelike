package creatureitem.item;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.physics.box2d.BodyDef;
import creatureitem.effect.damage.Damage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;

public class Item {

    protected char glyph;

    protected Texture texture;

    protected String name;

    protected ArrayList<String> properties;

    protected int count;

    protected Damage throwDamage;

    protected BodyDef itemDef;

    protected int worth;

    protected double rarity;

    public static final double SUPER_COMMON = 0d, COMMON = 1d, AVERAGE = 2d, UNCOMMON = 3d, RARE = 4d, LEGENDARY = 5d;

    public Item(char glyph, String texturePath, int worth, String name) {
        this.glyph = glyph;
        if(!texturePath.equals(""))
            this.texture = new Texture(Gdx.files.internal(texturePath));
        else this.texture = null;
        this.worth = worth;
        this.name = name;
        this.rarity = 0;
        this.properties = new ArrayList<>();
        this.properties.add("drop");
        throwDamage = new Damage(0, 1, 0);
        itemDef = new BodyDef();
        itemDef.type = BodyDef.BodyType.StaticBody;
    }

    public Item(char glyph, String texturePath, String name, int worth, String ... properties) {
        this.glyph = glyph;
        if(!texturePath.equals(""))
            this.texture = new Texture(Gdx.files.internal(texturePath));
        else this.texture = null;
        this.worth = worth;
        this.rarity = 0;
        this.name = name;
        this.properties = new ArrayList<>(Arrays.asList(properties));
        this.properties.add("drop");
        throwDamage = new Damage(0, 1, 0);
        itemDef = new BodyDef();
        itemDef.type = BodyDef.BodyType.StaticBody;

        if(hasProperty("stack")) {
            count = 1;
        }
    }

    public Item(char glyph, String texturePath, String name, int worth, Damage throwDamage, String ... properties) {
        this.glyph = glyph;
        if(!texturePath.equals(""))
            this.texture = new Texture(Gdx.files.internal(texturePath));
        else this.texture = null;
        this.worth = worth;
        this.rarity = 0;
        this.name = name;
        this.properties = new ArrayList<>(Arrays.asList(properties));
        this.properties.add("drop");
        this.throwDamage = throwDamage;
        itemDef = new BodyDef();
        itemDef.type = BodyDef.BodyType.StaticBody;

        if(hasProperty("stack")) {
            count = 1;
        }
    }

    public Item(Item item) {
        this.glyph = item.glyph;
        this.texture = item.texture;
        this.worth = item.worth;
        this.rarity = item.rarity;
        this.name = item.name;
        this.properties = item.properties;
        this.count = item.count;
        this.throwDamage = item.throwDamage;
        this.itemDef = item.itemDef;
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

    public ArrayList<String> getProperties() {
        return properties;
    }

    public void setProperties(ArrayList<String> properties) {
        this.properties = properties;
    }

    public boolean hasProperty(String ... properties) {
        for(int i = 0; i < properties.length; i++) {
            if(this.properties.contains(properties[i])) return true;
        }

        return false;
    }

    public void addProperty(String property) {
        if(!properties.contains(property)) properties.add(property);
    }

    public int getCount() {
        if(!hasProperty("stack")) return 1;
        return count;
    }

    public void setCount(int count) {
        if(!hasProperty("stack")) return;
        this.count = count;
    }

    public void incrementCount(int i) {
        if(!hasProperty("stack")) return;
        this.count += i;
    }

    public void decrementCount(int i) {
        if(!hasProperty("stack")) return;
        this.count -= i;
    }

    public Damage getThrowDamage() {
        return throwDamage;
    }

    public void setThrowDamage(Damage throwDamage) {
        this.throwDamage = throwDamage;
    }

    public BodyDef getItemDef() {
        return itemDef;
    }

    public void setItemDef(BodyDef itemDef) {
        this.itemDef = itemDef;
    }

    public int getWorth() {
        return worth;
    }

    public void setWorth(int worth) {
        this.worth = worth;
    }

    public double getRarity() {
        return rarity;
    }

    public void setRarity(double rarity) {
        this.rarity = rarity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Item)) return false;
        Item item = (Item) o;
        return glyph == item.glyph &&
                throwDamage == item.throwDamage &&
                worth == item.worth &&
                rarity == item.rarity &&
                Objects.equals(name, item.name) &&
                Objects.equals(properties, item.properties);
    }

    @Override
    public int hashCode() {
        return Objects.hash(glyph, name, properties, throwDamage, worth, rarity);
    }

    @Override
    public String toString() {
        if(hasProperty("stack") && count > 1) return String.format(Locale.getDefault(), "%s (%d)", name, count);
        else return name;
    }
}
