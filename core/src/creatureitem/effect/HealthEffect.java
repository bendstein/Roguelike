package creatureitem.effect;

import creatureitem.Creature;

import java.util.Objects;
import java.util.Random;

public class HealthEffect extends Effect {

    /**
     * The amount the effect changes the creature's health
     */
    protected Damage amount;

    /**
     * True if healing, false otherwise
     */
    protected boolean positive;

    public HealthEffect() {
        super();
    }

    public HealthEffect(Damage amount, boolean positive) {
        this.amount = amount;
        this.positive = positive;
    }

    public HealthEffect(int duration, Damage amount, boolean positive) {
        this.duration = this.remainingDuration = duration;
        this.amount = amount;
        this.positive = positive;
    }

    public HealthEffect(HealthEffect effect) {
        super(effect);
    }

    @Override
    public void affect() {

    }

    @Override
    public void affect(Creature c) {
        int mod = amount.getDamage(c.getLevel().getRandom()) * (positive? 1 : -1);

        if(positive)
            c.doAction("recover %d hp.", Math.abs(mod));
        else
            c.doAction("take %d damage.", Math.abs(mod));

        boolean died = c.modifyHP(mod, true);
        if(caster != null && caster instanceof Creature && died) {
            ((Creature) caster).modifyExp(c.getExp());
        }
    }

    @Override
    public void done() {

    }

    @Override
    public void done(Creature c) {

    }

    @Override
    public HealthEffect makeCopy(Effect effect) {
        HealthEffect copy = new HealthEffect();
        copy.duration = effect.duration;
        copy.remainingDuration = effect.remainingDuration;
        copy.caster = effect.caster;
        copy.positive = effect instanceof HealthEffect? ((HealthEffect) effect).positive : null;
        copy.amount = effect instanceof HealthEffect? ((HealthEffect) effect).amount : null;

        return copy;
    }

    //<editor-fold desc="Getters and Setters">
    public Damage getAmount() {
        return amount;
    }

    public void setAmount(Damage amount) {
        this.amount = amount;
    }

    public boolean isPositive() {
        return positive;
    }

    public void setPositive(boolean positive) {
        this.positive = positive;
    }

    //</editor-fold>

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        HealthEffect that = (HealthEffect) o;
        return positive == that.positive &&
                Objects.equals(amount, that.amount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), amount, positive);
    }
}
