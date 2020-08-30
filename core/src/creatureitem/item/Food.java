package creatureitem.item;

import creatureitem.Creature;
import creatureitem.effect.damage.Damage;
import creatureitem.spell.PointSpell;
import creatureitem.spell.Spell;

import java.util.Objects;

public class Food extends Item {

    /**
     * The amount of sustenance the food provides
     */
    protected int foodValue;

    protected Spell onEat;

    public Food(char glyph, String texturePath, String name, int worth, int foodValue) {
        super(glyph, texturePath, name, worth);
        this.properties.add("eat");
        this.foodValue = foodValue;
    }

    public Food(char glyph, String texturePath, String name, int worth, int foodValue, String... properties) {
        super(glyph, texturePath, name, worth, properties);
        this.properties.add("eat");
        this.foodValue = foodValue;
    }

    public Food(char glyph, String texturePath, String name, int worth, Damage throwDamage, int foodValue, String... properties) {
        super(glyph, texturePath, name, worth, throwDamage, properties);
        this.properties.add("eat");
        this.foodValue = foodValue;
    }

    public Food(Item item) {
        super(item);
        this.properties.add("eat");
        this.foodValue = (item instanceof Food)? ((Food) item).foodValue : 0;

        if(item instanceof Food) {
            this.onEat = ((Food) item).onEat == null? null : ((Food) item).onEat.copyOf(((Food) item).getOnEat());
        }
        else
            this.onEat = null;
    }


    //<editor-fold desc="Getters and Setters">
    public int getFoodValue() {
        return foodValue;
    }

    public void setFoodValue(int foodValue) {
        this.foodValue = foodValue;
    }

    public Spell getOnEat() {
        return onEat;
    }

    public void setOnEat(Spell onEat) {
        this.onEat = onEat;
    }

    //</editor-fold>

    @Override
    public void assignCaster(Creature c) {
        super.assignCaster(c);
        if(onEat != null) onEat.setCaster(c);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Food food = (Food) o;
        return foodValue == food.foodValue &&
                Objects.equals(onEat, food.onEat);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), foodValue, onEat);
    }
}
