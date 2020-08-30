package creatureitem.spell;

import creatureitem.Creature;
import creatureitem.effect.Effect;

public class MassSpell extends PointSpell {

    public MassSpell(String name, boolean ignoreCaster, int cast_energy, boolean notify, Effect... effects) {
        super(name, true, true, ignoreCaster, Integer.MAX_VALUE, cast_energy, notify, effects);
    }

    public MassSpell(PointSpell spell) {
        super(spell);
    }

    public void cast() {
        if(caster == null) {
            return;
        }

        /*
        if(caster.getMana() - cost < 0) {
            caster.doAction("invoke %s, but it does nothing.", name);
            return;
        }

         */

        if(notify) caster.doAction("cast %s!", name);

        boolean hit = false;
        if(caster.getLevel() == null) {
            if(!ignoreCaster) {
                hit = true;
                for (Effect effect : effects)
                    caster.applyEffect(effect);
            }

        }

        else {
            for(Creature c : caster.getLevel().getCreatures()) {
                if(c.equals(caster)) continue;

                hit = true;
                for(Effect effect : effects)
                    c.applyEffect(effect);
            }
        }

        if(!hit) {
            if(notify) caster.notify("...but nothing happens!");
        }
    }

    /**
     * @param s The spell to copy
     * @return A deep copy of the given spell
     */
    @Override
    public MassSpell copyOf(Spell s) {
        return (MassSpell) super.copyOf(s);
    }
}
