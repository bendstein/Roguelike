package creatureitem.items.behaviors.equipable.weapon;

import creatureitem.Creature;
import creatureitem.effect.Effect;
import creatureitem.effect.damage.Damage;
import creatureitem.items.Item;
import creatureitem.spell.Spell;

public class MeleeWeapon extends Weapon {

    /**
     * Spell which the item applies on contact
     */
    protected Spell onHit;

    /**
     * The rate at which the item applies the onHit spell
     */
    protected float castRate;

    public MeleeWeapon(Item item, Damage damage, int toHitMod, Effect ... onEquip) {
        super(item, damage, toHitMod, onEquip);
        onHit = null;
        castRate = 0;
    }

    public MeleeWeapon(Item item, Damage damage, int range, int toHitMod, Effect ... onEquip) {
        super(item, damage, onEquip);
        this.range = range;
        onHit = null;
        castRate = 0;
    }

    public MeleeWeapon(Item item, Damage damage, int toHitMod, Spell onHit, float castRate, Effect ... onEquip) {
        super(item, damage, toHitMod, onEquip);
        this.onHit = onHit;
        this.castRate = castRate;
    }

    public MeleeWeapon(Item item, Damage damage, int range, int toHitMod, Spell onHit, float castRate, Effect ... onEquip) {
        super(item, damage, toHitMod, onEquip);
        this.range = range;
        this.onHit = onHit;
        this.castRate = castRate;
    }

    @Override
    public void attack(Creature wielder, Creature target) {
        if(wielder == null || target == null) return;

        /*
         * Make sure the wielder is the caster for the "on hit" spell
         */
        assignCaster(wielder);

        /*
         * TODO: wielder attacks target
         */

    }

    @Override
    public void cast(Creature target) {
        if(onHit == null || onHit.getCaster() == null) return;

        if(onHit.getCaster().getLevel().getRandom().nextFloat() < castRate) {
            onHit.getCaster().cast(onHit, target.getLocation());
        }

    }

    @Override
    public void assignCaster(Creature c) {
        super.assignCaster(c);

        if(onHit != null) {
            onHit.setCaster(c);
        }
    }

}
