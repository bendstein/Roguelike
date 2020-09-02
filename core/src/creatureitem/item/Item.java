package creatureitem.item;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import creatureitem.Creature;
import creatureitem.item.behavior.*;
import creatureitem.item.behavior.equipable.Equipable;
import creatureitem.item.behavior.equipable.armor.Armor;
import creatureitem.item.behavior.equipable.weapon.*;
import creatureitem.item.builder.ItemBuilder;
import jdk.vm.ci.meta.Local;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;

public class Item {

    //<editor-fold desc="Instance Variables">
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

    protected int worth;

    //------------------------------------------------------------------------------------------------------------------

    /**
     * A reference to the slot this item is equipped in
     */
    protected ItemSlot equippedSlot;

    /**
     * List of references to slots this item is obstructing
     */
    protected ArrayList<ItemSlot> obstructing;

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
    //</editor-fold>

    public Item() {
    }

    public Item(char glyph, String texturePath, String name, String description, double rarity, int worth, ItemBuilder builder,
                String ... properties) {

        this.glyph = glyph;

        try {
            this.texture = new Texture(Gdx.files.internal(texturePath));
        } catch (Exception e) {
            this.texture = null;
        }

        this.name = name;
        this.description = description;
        this.properties = new ArrayList<>(Arrays.asList(properties));
        this.rarity = rarity;
        this.worth = worth;
        this.count = 1;
        setComponents(builder);
        this.equippedSlot = null;
        this.obstructing = new ArrayList<>();
    }

    public Item(char glyph, Texture texture, String name, String description, double rarity, int worth, ItemBuilder builder,
                String ... properties) {

        this.glyph = glyph;
        this.texture = texture;
        this.name = name;
        this.description = description;
        this.properties = new ArrayList<>(Arrays.asList(properties));
        this.rarity = rarity;
        this.worth = worth;
        this.count = 1;
        setComponents(builder);
        this.equippedSlot = null;
        this.obstructing = new ArrayList<>();
    }

    /**
     * Constructor for a fake item that will never show up physically, such as the player's fists.
     */
    public Item(String name, ItemBuilder builder, String ... properties) {
        this.glyph = ' ';
        texture = null;
        this.name = name;
        this.description = "";
        this.properties = new ArrayList<>(Arrays.asList(properties));
        this.rarity = 0;
        this.worth = 0;
        this.count = 0;
        this.usableComponent = builder.getUsableComponent();
        this.launchableComponent = builder.getLaunchableComponent();
        this.consumableComponent = builder.getConsumableComponent();
        this.meleeComponent = builder.getMeleeComponent();
        this.rangedComponent = builder.getRangedComponent();
        this.ammoComponent = builder.getAmmoComponent();
        this.armorComponent = builder.getArmorComponent();
        this.castableComponent = builder.getCastableComponent();
        this.equippedSlot = null;
        this.obstructing = new ArrayList<>();
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

    public void setComponents(ItemBuilder builder) {
        if(builder == null) return;

        this.usableComponent = builder.getUsableComponent();
        this.launchableComponent = builder.getLaunchableComponent();
        this.consumableComponent = builder.getConsumableComponent();
        this.meleeComponent = builder.getMeleeComponent();
        this.rangedComponent = builder.getRangedComponent();
        this.ammoComponent = builder.getAmmoComponent();
        this.armorComponent = builder.getArmorComponent();
        this.castableComponent = builder.getCastableComponent();

        setComponentKeyWords();
    }

    public void setComponentKeyWords() {
        if(usableComponent != null) {
            addProperty("use");
        }

        if(launchableComponent != null) {
            addProperty("throw");
        }

        if(consumableComponent != null) {
            if(consumableComponent.getSatiation() != 0)
                addProperty("eat");
            if(consumableComponent.getOnConsume() != null && consumableComponent.getOnConsume().length > 0)
                addProperty("quaff");
        }

        if(meleeComponent != null) {
            addProperty("equip", "melee");
        }

        if(rangedComponent != null) {
            addProperty("equip", "ranged");
        }

        if(ammoComponent != null) {
            addProperty("equip", "ammo");
        }

        if(armorComponent != null) {
            addProperty("equip", "armor");
        }

        if(castableComponent != null) {
            addProperty("zap");
        }
    }

    public boolean hasSlotType(int slot) {
        if(isMeleeWeapon() && meleeComponent.getSlot() != null && meleeComponent.getSlot().getMainSlot().getSlot() == slot) return true;
        if(isRangedWeapon() && rangedComponent.getSlot() != null && rangedComponent.getSlot().getMainSlot().getSlot() == slot) return true;
        if(isAmmo() && ammoComponent.getSlot() != null && ammoComponent.getSlot().getMainSlot().getSlot() == slot) return true;
        if(isArmor() && armorComponent.getSlot() != null && armorComponent.getSlot().getMainSlot().getSlot() == slot) return true;
        return false;
    }

    public Equipable.EquipSlot getSlotWithSlotType(int slot) {
        if(!hasSlotType(slot)) return null;

        if(isMeleeWeapon() && meleeComponent.getSlot() != null && meleeComponent.getSlot().getMainSlot().getSlot() == slot) return meleeComponent.getSlot();
        if(isRangedWeapon() && rangedComponent.getSlot() != null && rangedComponent.getSlot().getMainSlot().getSlot() == slot) return rangedComponent.getSlot();
        if(isAmmo() && ammoComponent.getSlot() != null && ammoComponent.getSlot().getMainSlot().getSlot() == slot) return ammoComponent.getSlot();
        if(isArmor() && armorComponent.getSlot() != null && armorComponent.getSlot().getMainSlot().getSlot() == slot) return armorComponent.getSlot();

        return null;
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

    public boolean isEquippable() {
        return isMeleeWeapon() || isRangedWeapon() || isAmmo() || isArmor();
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

    public void addProperty(String ... properties) {
        for(String p : properties) {
            if(!this.properties.contains(p))
                this.properties.add(p);
        }
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

    public void decrementCount() {
        decrementCount(1);
    }

    public void decrementCount(int i) {
        setCount(this.count - i);
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

    public int getWorth() {
        return worth;
    }

    public void setWorth(int worth) {
        this.worth = worth;
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

    public ItemSlot getEquippedSlot() {
        return equippedSlot;
    }

    public void setEquippedSlot(ItemSlot equippedSlot) {
        this.equippedSlot = equippedSlot;
    }

    public boolean isEquipped() {
        if(equippedSlot == null) return false;
        return equippedSlot.isEquipped();
    }

    public ArrayList<ItemSlot> getObstructing() {
        return obstructing;
    }

    public void setObstructing(ArrayList<ItemSlot> obstructing) {
        this.obstructing = obstructing;
    }

    //</editor-fold>

    public void unequip() {
        if(equippedSlot != null) {
            equippedSlot.unequip();
        }

        equippedSlot = null;

        if(obstructing != null) {
            for(ItemSlot is : obstructing)
                if(is != null) is.unequip();

            obstructing.clear();
        }
    }

    public Item copy() {
        ItemBuilder c = new ItemBuilder(this)
                .setAmmoComponent(ammoComponent == null? null : ammoComponent.copy())
                .setArmorComponent(armorComponent == null? null : armorComponent.copy())
                .setCastableComponent(castableComponent == null? null : castableComponent.copy())
                .setConsumableComponent(consumableComponent == null? null : consumableComponent.copy())
                .setLaunchableComponent(launchableComponent == null? null : launchableComponent.copy())
                .setMeleeComponent(meleeComponent == null? null : meleeComponent.copy())
                .setRangedComponent(rangedComponent == null? null : rangedComponent.copy())
                .setUsableComponent(usableComponent == null? null : usableComponent.copy());

        Item i = new Item(glyph, texture, name, description, rarity, worth, c, properties.toArray(new String[0]));
        i.equippedSlot = (equippedSlot == null? null : equippedSlot.copy());

        return i;
    }

    @Override
    public String toString() {
        return (hasProperty("stack") && count != 1)? String.format(Locale.getDefault(), "%s (%d)", name, count) : name;
    }

    public boolean equivalent(Item item) {
        if (this == item) return true;
        return glyph == item.glyph &&
                Objects.equals(name, item.name) &&
                Objects.equals(description, item.description) &&
                Objects.equals(properties, item.properties) &&
                Objects.equals(equippedSlot, item.equippedSlot) &&
                Objects.equals(obstructing, item.obstructing) &&
                Objects.equals(usableComponent, item.usableComponent) &&
                Objects.equals(launchableComponent, item.launchableComponent) &&
                Objects.equals(consumableComponent, item.consumableComponent) &&
                Objects.equals(meleeComponent, item.meleeComponent) &&
                Objects.equals(rangedComponent, item.rangedComponent) &&
                Objects.equals(ammoComponent, item.ammoComponent) &&
                Objects.equals(armorComponent, item.armorComponent) &&
                Objects.equals(castableComponent, item.castableComponent);
    }

}
