package creatureitem.item.behavior;

import creatureitem.Creature;
import creatureitem.effect.damage.Damage;
import creatureitem.item.Item;
import creatureitem.spell.Spell;

import java.util.Objects;

public class Launchable extends ItemBehavior {

    /**
     * The spell that is cast when this item impacts something after being thrown
     */
    protected Spell onHit;

    /**
     * The damage that is inflicted when this item is thrown
     */
    protected Damage throwDamage;

    public Launchable(Item item, Damage throwDamage) {
        super(item);
        this.throwDamage = throwDamage;
        onHit = null;
    }

    public Launchable(Item item, Damage throwDamage, Spell onHit) {
        super(item);
        this.throwDamage = throwDamage;
        this.onHit = onHit;
    }

    //<editor-fold desc="Getters and Setters">
    public Spell getOnHit() {
        return onHit;
    }

    public void setOnHit(Spell onHit) {
        this.onHit = onHit;
    }

    public Damage getThrowDamage() {
        return throwDamage;
    }

    public void setThrowDamage(Damage throwDamage) {
        this.throwDamage = throwDamage;
    }
    //</editor-fold>

    @Override
    public void assignCaster(Creature c) {
        if(onHit != null) {
            onHit.setCaster(c);
        }
    }

    @Override
    public Launchable copy() {
        return new Launchable(null, throwDamage == null? null : throwDamage.copy(), onHit == null? null : onHit.copy());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Launchable)) return false;
        Launchable that = (Launchable) o;
        return Objects.equals(onHit, that.onHit) &&
                Objects.equals(throwDamage, that.throwDamage);
    }

    @Override
    public int hashCode() {
        return Objects.hash(onHit, throwDamage);
    }
}
