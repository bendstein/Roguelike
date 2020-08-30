package creatureitem.items;

import com.badlogic.gdx.graphics.Texture;
import creatureitem.Creature;
import creatureitem.items.behaviors.*;
import creatureitem.items.behaviors.equipable.*;
import creatureitem.items.behaviors.equipable.weapon.*;
import creatureitem.items.behaviors.equipable.armor.*;
import creatureitem.items.builder.ItemBuilder;

import java.util.ArrayList;
import java.util.Arrays;

public class Item {

    /**
     * Character representing this item
     */
    protected char glyph;

    /**
     * Texture representing this item
     */
    protected Texture texture;

    //------------------------------------------------------------------------------------------------------------------

    /**
     * The name of this item
     */
    protected String name;

    /**
     * A description of this item
     */
    protected String description;

    /**
     * Properties which this item has
     */
    protected ArrayList<String> properties;

    /**
     * The number of copies in this stack
     */
    protected int count;

    //------------------------------------------------------------------------------------------------------------------

    /**
     * How common this item is
     */
    protected double rarity;

    /**
     * Preset rarities
     */
    public static final double SUPER_COMMON = 0d, COMMON = 1d, AVERAGE = 2d, UNCOMMON = 3d, RARE = 4d, LEGENDARY = 5d;

    //------------------------------------------------------------------------------------------------------------------

    /**
     * Item components
     */
    protected Usable usableComponent;

    protected Launchable launchableComponent;

    protected Consumable consumableComponent;

    protected MeleeWeapon meleeComponent;

    protected RangedWeapon rangedComponent;

    protected Ammo ammoComponent;

    protected Armor armorComponent;

    protected Castable castableComponent;

    //------------------------------------------------------------------------------------------------------------------


    public Item(char glyph, Texture texture, String name, String description, double rarity, ItemBuilder builder,
                String ... properties) {

        this.glyph = glyph;
        this.texture = texture;
        this.name = name;
        this.description = description;
        this.properties = new ArrayList<>(Arrays.asList(properties));
        this.rarity = rarity;
        this.count = 1;
        this.usableComponent = builder.getUsableComponent();
        this.launchableComponent = builder.getLaunchableComponent();
        this.consumableComponent = builder.getConsumableComponent();
        this.meleeComponent = builder.getMeleeComponent();
        this.rangedComponent = builder.getRangedComponent();
        this.ammoComponent = builder.getAmmoComponent();
        this.armorComponent = builder.getArmorComponent();
        this.castableComponent = builder.getCastableComponent();
    }

    /**
     * Pass the item holder to all components
     * @param c The item holder
     */
    public void assignCaster(Creature c) {
        if(usableComponent != null) usableComponent.assignCaster(c);
        if(launchableComponent != null) launchableComponent.assignCaster(c);
        if(consumableComponent != null) consumableComponent.assignCaster(c);
        if(meleeComponent != null) meleeComponent.assignCaster(c);
        if(rangedComponent != null) rangedComponent.assignCaster(c);
        if(ammoComponent != null) ammoComponent.assignCaster(c);
        if(armorComponent != null) armorComponent.assignCaster(c);
        if(castableComponent != null) castableComponent.assignCaster(c);
    }

    //<editor-fold desc="Component Check">
    /**
     * @return true if the item has the respective component
     */
    public boolean isUsable() {
        return usableComponent != null;
    }

    public boolean isLaunchable() {
        return launchableComponent != null;
    }

    public boolean isComsumable() {
        return consumableComponent != null;
    }

    public boolean isMeleeWeapon() {
        return meleeComponent != null;
    }

    public boolean isRangedWeapon() {
        return rangedComponent != null;
    }

    public boolean isAmmo() {
        return ammoComponent != null;
    }

    public boolean isArmor() {
        return armorComponent != null;
    }

    public boolean isCastable() {
        return castableComponent != null;
    }
    //</editor-fold>

    //<editor-fold desc="Getters and Setters">

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ArrayList<String> getProperties() {
        return properties;
    }

    public void setProperties(ArrayList<String> properties) {
        this.properties = properties;
    }

    public boolean hasProperty(String ... properties) {

        for(String s : properties) {
            if(!this.properties.contains(s)) {
                return false;
            }
        }

        return true;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        if(!hasProperty("stack")) return;
        this.count = count;
    }

    public void incrementCount() {
        incrementCount(1);
    }

    public void incrementCount(int i) {
        setCount(this.count + i);
    }

    public double getRarity() {
        return rarity;
    }

    public void setRarity(double rarity) {
        this.rarity = rarity;
    }

    public Usable getUsableComponent() {
        return usableComponent;
    }

    public void setUsableComponent(Usable usableComponent) {
        this.usableComponent = usableComponent;
    }

    public Launchable getLaunchableComponent() {
        return launchableComponent;
    }

    public void setLaunchableComponent(Launchable launchableComponent) {
        this.launchableComponent = launchableComponent;
    }

    public Consumable getConsumableComponent() {
        return consumableComponent;
    }

    public void setConsumableComponent(Consumable consumableComponent) {
        this.consumableComponent = consumableComponent;
    }

    public MeleeWeapon getMeleeComponent() {
        return meleeComponent;
    }

    public void setMeleeComponent(MeleeWeapon meleeComponent) {
        this.meleeComponent = meleeComponent;
    }

    public RangedWeapon getRangedComponent() {
        return rangedComponent;
    }

    public void setRangedComponent(RangedWeapon rangedComponent) {
        this.rangedComponent = rangedComponent;
    }

    public Ammo getAmmoComponent() {
        return ammoComponent;
    }

    public void setAmmoComponent(Ammo ammoComponent) {
        this.ammoComponent = ammoComponent;
    }

    public Armor getArmorComponent() {
        return armorComponent;
    }

    public void setArmorComponent(Armor armorComponent) {
        this.armorComponent = armorComponent;
    }

    public Castable getCastableComponent() {
        return castableComponent;
    }

    public void setCastableComponent(Castable castableComponent) {
        this.castableComponent = castableComponent;
    }

    //</editor-fold>

}
