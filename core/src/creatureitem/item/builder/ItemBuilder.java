package creatureitem.item.builder;

import creatureitem.item.Item;
import creatureitem.item.behavior.Castable;
import creatureitem.item.behavior.Consumable;
import creatureitem.item.behavior.Launchable;
import creatureitem.item.behavior.Usable;
import creatureitem.item.behavior.equipable.armor.Armor;
import creatureitem.item.behavior.equipable.weapon.Ammo;
import creatureitem.item.behavior.equipable.weapon.MeleeWeapon;
import creatureitem.item.behavior.equipable.weapon.RangedWeapon;

public class ItemBuilder {

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

    protected Item i;

    public ItemBuilder() {
        clear();
    }

    public ItemBuilder(Item i) {
        clear();
        this.i = i;
    }

    public ItemBuilder clear() {
        i = null;
        usableComponent = null;
        launchableComponent = null;
        consumableComponent = null;
        meleeComponent = null;
        rangedComponent = null;
        ammoComponent = null;
        armorComponent = null;
        castableComponent = null;
        return this;
    }

    //<editor-fold desc="Getters and Setters">

    public Usable getUsableComponent() {
        return usableComponent;
    }

    public ItemBuilder setUsableComponent(Usable usableComponent) {
        if(usableComponent == null) return this;
        this.usableComponent = usableComponent;
        if(i != null) usableComponent.setItem(i);
        return this;
    }

    public Launchable getLaunchableComponent() {
        return launchableComponent;
    }

    public ItemBuilder setLaunchableComponent(Launchable launchableComponent) {
        if(launchableComponent == null) return this;
        this.launchableComponent = launchableComponent;
        if(i != null) launchableComponent.setItem(i);
        return this;
    }

    public Consumable getConsumableComponent() {
        return consumableComponent;
    }

    public ItemBuilder setConsumableComponent(Consumable consumableComponent) {
        if(consumableComponent == null) return this;
        this.consumableComponent = consumableComponent;
        if(i != null) consumableComponent.setItem(i);
        return this;
    }

    public MeleeWeapon getMeleeComponent() {
        return meleeComponent;
    }

    public ItemBuilder setMeleeComponent(MeleeWeapon meleeComponent) {
        if(meleeComponent == null) return this;
        this.meleeComponent = meleeComponent;
        if(i != null) meleeComponent.setItem(i);
        return this;
    }

    public RangedWeapon getRangedComponent() {
        return rangedComponent;
    }

    public ItemBuilder setRangedComponent(RangedWeapon rangedComponent) {
        if(rangedComponent == null) return this;
        this.rangedComponent = rangedComponent;
        if(i != null) rangedComponent.setItem(i);
        return this;
    }

    public Ammo getAmmoComponent() {
        return ammoComponent;
    }

    public ItemBuilder setAmmoComponent(Ammo ammoComponent) {
        if(ammoComponent == null) return this;
        this.ammoComponent = ammoComponent;
        if(i != null) ammoComponent.setItem(i);
        return this;
    }

    public Armor getArmorComponent() {
        return armorComponent;
    }

    public ItemBuilder setArmorComponent(Armor armorComponent) {
        if(armorComponent == null) return this;
        this.armorComponent = armorComponent;
        if(i != null) armorComponent.setItem(i);
        return this;
    }

    public Castable getCastableComponent() {
        return castableComponent;
    }

    public ItemBuilder setCastableComponent(Castable castableComponent) {
        if(castableComponent == null) return this;
        this.castableComponent = castableComponent;
        if(i != null) castableComponent.setItem(i);
        return this;
    }

    public Item getItem() {
        return i;
    }

    public ItemBuilder setItem(Item i) {
        this.i = i;
        return this;
    }

    //</editor-fold>
}
