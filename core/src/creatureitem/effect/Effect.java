package creatureitem.effect;

import creatureitem.Creature;

import java.util.Objects;

public abstract class Effect {

    /**
     * How long the effect lasts
     */
    protected int duration;

    /**
     * How many remaining turns the effect has
     */
    protected int remainingDuration;

    /**
     * Whatever object made this effect
     */
    protected Object caster;

    public Effect() {

    }

    public Effect(Effect effect) {
        makeCopy(effect);
    }

    /**
     * What the effect does when it is active
     */
    public abstract void affect();

    /**
     * What the effect does when it is active
     * @param c The creature to apply the effect to
     */
    public abstract void affect(Creature c);

    /**
     * What the effect does when it is over
     */
    public abstract void done();

    /**
     * What the effect does when it is over
     * @param c The creature which the effect was applied to
     */
    public abstract void done(Creature c);

    /**
     * @return true if the effect duration is over
     */
    public boolean isDone() {
        return remainingDuration < 1;
    }

    public abstract Effect makeCopy(Effect effect);

    //<editor-fold desc="Getters and setters">
    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getRemainingDuration() {
        return remainingDuration;
    }

    public void setRemainingDuration(int remainingDuration) {
        this.remainingDuration = remainingDuration;
    }

    public void changeRemainingDurationBy(int mod) {
        this.remainingDuration += mod;
    }

    public Object getCaster() {
        return caster;
    }

    public void setCaster(Object caster) {
        this.caster = caster;
    }

    //</editor-fold>


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Effect effect = (Effect) o;
        return duration == effect.duration &&
                remainingDuration == effect.remainingDuration;
    }

    @Override
    public int hashCode() {
        return Objects.hash(duration, remainingDuration);
    }
}
