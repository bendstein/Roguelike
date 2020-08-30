package creatureitem.items.behaviors;

import creatureitem.Creature;
import creatureitem.effect.Effect;
import creatureitem.items.Item;

public class Consumable extends ItemBehavior {

    /**
     * The effects applied to the creature which consumes this item
     */
    protected Effect[] onConsume;

    public Consumable(Item item, Effect ... onConsume) {
        super(item);
        this.onConsume = onConsume;
    }

    @Override
    public void assignCaster(Creature c) {
        if(onConsume != null) {
            for(Effect e : onConsume) {
                e.setCaster(c);
            }
        }
    }

    //<editor-fold desc="Getters and Setters">
    public Effect[] getOnConsume() {
        return onConsume;
    }

    public void setOnConsume(Effect[] onConsume) {
        this.onConsume = onConsume;
    }
    //</editor-fold>
}
