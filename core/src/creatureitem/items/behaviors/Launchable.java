package creatureitem.items.behaviors;

import creatureitem.Creature;
import creatureitem.effect.damage.Damage;
import creatureitem.items.Item;
import creatureitem.spell.Spell;

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
    }

    public Launchable(Item item, Damage throwDamage, Spell onHit) {
        super(item);
        this.throwDamage = throwDamage;
        this.onHit = onHit;
    }

    @Override
    public void assignCaster(Creature c) {
        if(onHit != null) {
            onHit.setCaster(c);
        }
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
}
