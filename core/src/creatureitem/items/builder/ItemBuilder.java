package creatureitem.items.builder;

import creatureitem.items.behaviors.Castable;
import creatureitem.items.behaviors.Consumable;
import creatureitem.items.behaviors.Launchable;
import creatureitem.items.behaviors.Usable;
import creatureitem.items.behaviors.equipable.armor.Armor;
import creatureitem.items.behaviors.equipable.weapon.Ammo;
import creatureitem.items.behaviors.equipable.weapon.MeleeWeapon;
import creatureitem.items.behaviors.equipable.weapon.RangedWeapon;

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

    public ItemBuilder() {
        clear();
    }

    public void clear() {
        usableComponent = null;
        launchableComponent = null;
        consumableComponent = null;
        meleeComponent = null;
        rangedComponent = null;
        ammoComponent = null;
        armorComponent = null;
        castableComponent = null;
    }

    //<editor-fold desc="Getters and Setters">

    public Usable getUsableComponent() {
        return usableComponent;
    }

    public ItemBuilder setUsableComponent(Usable usableComponent) {
        this.usableComponent = usableComponent;
        return this;
    }

    public Launchable getLaunchableComponent() {
        return launchableComponent;
    }

    public ItemBuilder setLaunchableComponent(Launchable launchableComponent) {
        this.launchableComponent = launchableComponent;
        return this;
    }

    public Consumable getConsumableComponent() {
        return consumableComponent;
    }

    public ItemBuilder setConsumableComponent(Consumable consumableComponent) {
        this.consumableComponent = consumableComponent;
        return this;
    }

    public MeleeWeapon getMeleeComponent() {
        return meleeComponent;
    }

    public ItemBuilder setMeleeComponent(MeleeWeapon meleeComponent) {
        this.meleeComponent = meleeComponent;
        return this;
    }

    public RangedWeapon getRangedComponent() {
        return rangedComponent;
    }

    public ItemBuilder setRangedComponent(RangedWeapon rangedComponent) {
        this.rangedComponent = rangedComponent;
        return this;
    }

    public Ammo getAmmoComponent() {
        return ammoComponent;
    }

    public ItemBuilder setAmmoComponent(Ammo ammoComponent) {
        this.ammoComponent = ammoComponent;
        return this;
    }

    public Armor getArmorComponent() {
        return armorComponent;
    }

    public ItemBuilder setArmorComponent(Armor armorComponent) {
        this.armorComponent = armorComponent;
        return this;
    }

    public Castable getCastableComponent() {
        return castableComponent;
    }

    public ItemBuilder setCastableComponent(Castable castableComponent) {
        this.castableComponent = castableComponent;
        return this;
    }

    //</editor-fold>
}
