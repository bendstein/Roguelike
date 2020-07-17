package creatureitem.effect;

import creatureitem.Creature;

import java.util.Arrays;
import java.util.Objects;

public class StatusEffect extends Effect {

    /**
     * Amount that each stat is modified.
     * Order: HPMax, HungerMax, ManaMax, strength, agility, constitution, perception, intelligence, damageBonus, defenseBonus, magicBonus
     */
    protected int[] amounts;

    /**
     * True if it applies the affect only once in the duration
     */
    boolean once;

    public StatusEffect() {
        super();
    }

    public StatusEffect(int[] amounts) {
        this.duration = this.remainingDuration = 0;
        this.amounts = amounts;
        this.once = true;
    }

    public StatusEffect(int[] amounts, boolean once) {
        this.duration = this.remainingDuration = 0;
        this.amounts = amounts;
        this.once = once;
    }

    public StatusEffect(int[] amounts, boolean once, int duration) {
        this.duration = this.remainingDuration = duration;
        this.amounts = amounts;
        this.once = once;
    }

    @Override
    public void affect() {

    }

    @Override
    public void affect(Creature c) {
        if(once && c.hasEffect(this)) return;

        for(int i = 0; i < amounts.length; i++) {
            switch (i) {
                case 0: {
                    c.modifyMaxHp(amounts[i]);
                    break;
                }
                case 1: {
                    c.modifyMaxHunger(amounts[i]);
                    break;
                }
                case 2: {
                    c.setStrength(c.getStrength() + amounts[i]);
                    break;
                }
                case 3: {
                    c.setAgility(c.getAgility() + amounts[i]);
                    break;
                }
                case 4: {
                    c.setConstitution(c.getConstitution() + amounts[i]);
                    break;
                }
                case 5: {
                    c.setPerception(c.getPerception() + amounts[i]);
                    break;
                }
                case 6: {
                    c.setDamageBonus(c.getDamageBonus() + amounts[i]);
                    break;
                }
                case 7: {
                    c.setDefenseBonus(c.getDefenseBonus() + amounts[i]);
                    break;
                }
                default: {
                    break;
                }
            }
        }
    }

    @Override
    public void done() {

    }

    @Override
    public void done(Creature c) {
        for(int i = 0; i < amounts.length; i++) {
            switch (i) {
                case 0: {
                    c.modifyMaxHp(-amounts[i]);
                    break;
                }
                case 1: {
                    c.modifyMaxHunger(-amounts[i]);
                    break;
                }
                case 2: {
                    c.modifyManaMax(-amounts[i]);
                    break;
                }
                case 3: {
                    c.setStrength(c.getStrength() - amounts[i]);
                    break;
                }
                case 4: {
                    c.setAgility(c.getAgility() - amounts[i]);
                    break;
                }
                case 5: {
                    c.setConstitution(c.getConstitution() - amounts[i]);
                    break;
                }
                case 6: {
                    c.setPerception(c.getPerception() - amounts[i]);
                    break;
                }
                case 7: {
                    c.setIntelligence(c.getIntelligence() - amounts[i]);
                    break;
                }
                case 8: {
                    c.setDamageBonus(c.getDamageBonus() - amounts[i]);
                    break;
                }
                case 9: {
                    c.setDefenseBonus(c.getDefenseBonus() - amounts[i]);
                    break;
                }
                case 10: {
                    c.setMagicBonus(c.getMagicBonus() - amounts[i]);
                    break;
                }
                default: {
                    break;
                }
            }
        }
    }

    @Override
    public Effect makeCopy(Effect effect) {
        StatusEffect copy = new StatusEffect();
        copy.caster = effect.caster;
        copy.duration = effect.duration;
        copy.remainingDuration = effect.remainingDuration;
        copy.amounts = effect instanceof StatusEffect? ((StatusEffect) effect).amounts : new int[]{};
        copy.once = effect instanceof StatusEffect? ((StatusEffect) effect).once : true;

        return copy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        StatusEffect that = (StatusEffect) o;
        return once == that.once &&
                Arrays.equals(amounts, that.amounts);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(super.hashCode(), once);
        result = 31 * result + Arrays.hashCode(amounts);
        return result;
    }
}
