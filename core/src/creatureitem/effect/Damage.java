package creatureitem.effect;

import java.util.Locale;
import java.util.Objects;
import java.util.Random;

public class Damage {

    /**
     * Number of times the die is rolled
     */
    int numberOfDice;

    /**
     * Number of faces on the die
     */
    int dieType;

    /**
     * Number added to the total damage after the roll
     */
    int modifier;

    public Damage(int numberOfDice, int dieType, int modifier) {
        this.numberOfDice = numberOfDice;
        this.dieType = dieType;
        this.modifier = modifier;
    }

    public Damage(Damage damage) {
        this.numberOfDice = damage.numberOfDice;
        this.dieType = damage.dieType;
        this.modifier = damage.modifier;
    }

    /**
     * @param random A prng
     * @return The damage to apply from rolling the dice
     */
    public int getDamage(Random random) {
        int damage = modifier;

        for(int i = 0; i < numberOfDice; i++)
            damage += random.nextInt(dieType);

        return Math.max(0, damage);
    }

    public int getAverage() {
        return Math.floorDiv(((numberOfDice * dieType) + modifier) + modifier, 2);
    }

    //<editor-fold desc="Getters and Setters">
    public int getNumberOfDice() {
        return numberOfDice;
    }

    public void setNumberOfDice(int numberOfDice) {
        this.numberOfDice = numberOfDice;
    }

    public int getDieType() {
        return dieType;
    }

    public void setDieType(int dieType) {
        this.dieType = dieType;
    }

    public int getModifier() {
        return modifier;
    }

    public void setModifier(int modifier) {
        this.modifier = modifier;
    }
    //</editor-fold>

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Damage damage = (Damage) o;
        return numberOfDice == damage.numberOfDice &&
                dieType == damage.dieType &&
                modifier == damage.modifier;
    }

    @Override
    public int hashCode() {
        return Objects.hash(numberOfDice, dieType, modifier);
    }

    @Override
    public String toString() {
        String s = String.format(Locale.getDefault(), "%dd%d", numberOfDice, dieType);

        if(modifier < 0) {
            s = s.concat(String.format(Locale.getDefault(), " - %d", Math.abs(modifier)));
        }
        else if(modifier > 0) {
            s = s.concat(String.format(Locale.getDefault(), " + %d", modifier));
        }

        return s;
    }
}
