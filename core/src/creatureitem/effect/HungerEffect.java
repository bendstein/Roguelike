package creatureitem.effect;

import creatureitem.Creature;
import world.Level;

import java.util.Objects;

public class HungerEffect extends Effect {

    /**
     * The amount that the effect changes the creature's hunger
     */
    int amount;

    public HungerEffect() {
        super();
    }

    public HungerEffect(int amount) {
        super();
        this.amount = amount;
    }

    public HungerEffect(int duration, int amount) {
        super();
        this.duration = this.remainingDuration = duration;
        this.amount = amount;
    }

    public HungerEffect(HungerEffect effect) {
        super(effect);
    }

    @Override
    public void affect() {

    }

    @Override
    public void affect(int x, int y, Level l) {
        if(l.getCreatureAt(x, y) != null) affect(l.getCreatureAt(x, y));
    }

    @Override
    public void affect(Creature c) {
        c.modifyHunger(amount);
    }

    @Override
    public void done() {

    }

    @Override
    public void done(Creature c) {

    }

    @Override
    public HungerEffect makeCopy(Effect effect) {
        HungerEffect copy = new HungerEffect();
        copy.caster = effect.caster;
        copy.duration = effect.duration;
        copy.remainingDuration = effect.remainingDuration;
        copy.amount = effect instanceof HungerEffect? ((HungerEffect) effect).amount : 0;

        return copy;
    }


    //<editor-fold desc="Getters and Setters">
    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
    //</editor-fold>


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        HungerEffect that = (HungerEffect) o;
        return amount == that.amount;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), amount);
    }
}
