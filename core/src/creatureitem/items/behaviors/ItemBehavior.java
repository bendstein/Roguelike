package creatureitem.items.behaviors;

import creatureitem.Creature;
import creatureitem.items.Item;

public abstract class ItemBehavior {

    /**
     * The item this behavior is associated with
     */
    Item item;

    public ItemBehavior(Item item) {
        this.item = item;
    }

    public abstract void assignCaster(Creature c);

    //<editor-fold desc="Getters and Setters">
    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }
    //</editor-fold>
}
