package creatureitem.items.behaviors.equipable.weapon;

import creatureitem.Creature;
import creatureitem.effect.Effect;
import creatureitem.effect.damage.Damage;
import creatureitem.items.Item;
import creatureitem.spell.Spell;

public class Ammo extends Weapon {

    /**
     * The type of ammo this item is
     */
    String type;

    /**
     * The spell this ammo casts on impact
     */
    Spell onHit;

    public Ammo(Item item, Damage damage, String type, Effect... onEquip) {
        super(item, damage, onEquip);
        this.type = type;
    }

    public Ammo(Item item, Damage damage, String type, Spell onHit, Effect... onEquip) {
        super(item, damage, onEquip);
        this.type = type;
        this.onHit = onHit;
    }

    public Ammo(Item item, Damage damage, String type, int toHitMod, Effect... onEquip) {
        super(item, damage, toHitMod, onEquip);
        this.type = type;
    }

    public Ammo(Item item, Damage damage, String type, int toHitMod, Spell onHit, Effect... onEquip) {
        super(item, damage, toHitMod, onEquip);
        this.type = type;
        this.onHit = onHit;
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

        onHit.getCaster().cast(onHit, target.getLocation());
    }

    @Override
    public void assignCaster(Creature c) {
        super.assignCaster(c);

        if(onHit != null) {
            onHit.setCaster(c);
        }
    }

}
