package creatureitem.effect.damage;

import java.util.*;

public class Damage {

    /**
     * Number of times the die is rolled
     */
    private int numberOfDice;

    /**
     * Number of faces on the die
     */
    private int dieType;

    /**
     * Number added to the total damage after the roll
     */
    private int modifier;

    /**
     * The type of damage this deals
     */
    private Damage_Type type;

    public Damage(int numberOfDice, int dieType, int modifier) {
        this.numberOfDice = numberOfDice;
        this.dieType = dieType;
        this.modifier = modifier;
        this.type = Damage_Type.UNTYPED;
    }

    public Damage(int numberOfDice, int dieType, int modifier, Damage_Type type) {
        this.numberOfDice = numberOfDice;
        this.dieType = dieType;
        this.modifier = modifier;
        this.type = type;
    }

    public Damage(int numberOfDice, int dieType, int modifier, String type) {
        this.numberOfDice = numberOfDice;
        this.dieType = dieType;
        this.modifier = modifier;
        this.type = Damage_Type.getType(type);
    }

    public Damage(Damage damage) {
        this.numberOfDice = damage.numberOfDice;
        this.dieType = damage.dieType;
        this.modifier = damage.modifier;
        this.type = damage.type;
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

    public Damage_Type getType() {
        return type;
    }

    public void setType(Damage_Type type) {
        this.type = type;
    }

    //</editor-fold>

    public Damage copy() {
        return new Damage(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Damage)) return false;
        Damage damage = (Damage) o;
        return numberOfDice == damage.numberOfDice &&
                dieType == damage.dieType &&
                modifier == damage.modifier &&
                type.equals(((Damage) o).type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(numberOfDice, dieType, modifier, type);
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

        if(type != null) {
            s = s.concat(String.format(Locale.getDefault(), " %s", type.getName()));
        }

        return s;
    }
}
