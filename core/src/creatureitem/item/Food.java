package creatureitem.item;

import creatureitem.effect.Damage;

import java.util.Objects;

public class Food extends Item {

    /**
     * The amount of sustenance the food provides
     */
    int foodValue;

    public Food(char glyph, String texturePath, String name, int foodValue) {
        super(glyph, texturePath, name);
        this.properties.add("eat");
        this.foodValue = foodValue;
    }

    public Food(char glyph, String texturePath, String name, int foodValue, String... properties) {
        super(glyph, texturePath, name, properties);
        this.properties.add("eat");
        this.foodValue = foodValue;
    }

    public Food(char glyph, String texturePath, String name, Damage throwDamage, int foodValue, String... properties) {
        super(glyph, texturePath, name, throwDamage, properties);
        this.properties.add("eat");
        this.foodValue = foodValue;
    }

    public Food(Item item) {
        super(item);
        this.properties.add("eat");
        this.foodValue = (item instanceof Food)? ((Food) item).foodValue : 0;
    }


    //<editor-fold desc="Getters and Setters">
    public int getFoodValue() {
        return foodValue;
    }

    public void setFoodValue(int foodValue) {
        this.foodValue = foodValue;
    }
    //</editor-fold>


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Food food = (Food) o;
        return foodValue == food.foodValue;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), foodValue);
    }
}
