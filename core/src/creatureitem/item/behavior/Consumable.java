package creatureitem.item.behavior;

import creatureitem.Creature;
import creatureitem.effect.Effect;
import creatureitem.item.Item;

import java.util.Arrays;
import java.util.Objects;

public class Consumable extends ItemBehavior {

    /**
     * The effects applied to the creature which consumes this item
     */
    protected Effect[] onConsume;

    /**
     * The amount of hunger consuming this item satiates the creature
     */
    protected int satiation;

    public Consumable(Item item, int satiation, Effect ... onConsume) {
        super(item);
        this.onConsume = onConsume;
        this.satiation = satiation;
    }

    //<editor-fold desc="Getters and Setters">
    public Effect[] getOnConsume() {
        return onConsume;
    }

    public void setOnConsume(Effect[] onConsume) {
        this.onConsume = onConsume;
    }

    public int getSatiation() {
        return satiation;
    }

    public void setSatiation(int satiation) {
        this.satiation = satiation;
    }

    //</editor-fold>

    @Override
    public void assignCaster(Creature c) {
        if(onConsume != null) {
            for(Effect e : onConsume) {
                e.setCaster(c);
            }
        }
    }

    @Override
    public Consumable copy() {
        Consumable c = new Consumable(item, satiation);

        if(onConsume == null || onConsume.length == 0) return c;

        Effect[] e = new Effect[onConsume.length];

        for(int i = 0; i < e.length; i++) {
            if(onConsume[i] != null) e[i] = onConsume[i].makeCopy(onConsume[i]);
        }

        c.setOnConsume(e);

        return c;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Consumable)) return false;
        Consumable that = (Consumable) o;
        return satiation == that.satiation &&
                Arrays.equals(onConsume, that.onConsume);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(satiation);
        result = 31 * result + Arrays.hashCode(onConsume);
        return result;
    }
}
