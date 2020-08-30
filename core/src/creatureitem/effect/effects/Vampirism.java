package creatureitem.effect.effects;

import creatureitem.Creature;
import creatureitem.effect.Effect;
import world.Level;

public class Vampirism extends Effect {

    private float steal_proportion;

    private int damage;

    public Vampirism(float steal_proportion) {
        this.steal_proportion = steal_proportion;
        this.damage = 0;
    }

    /**
     * What the effect does when it is active
     */
    @Override
    public void affect() {
        if(caster instanceof Creature) {
            ((Creature) caster).modifyHP((int) (damage * steal_proportion));
        }

        damage = 0;
    }

    /**
     * What the effect does when it is active
     *
     * @param x X location
     * @param y Y location
     * @param l The level to effect
     */
    @Override
    public void affect(int x, int y, Level l) {
        affect();
    }

    /**
     * What the effect does when it is active
     *
     * @param c The creature to apply the effect to
     */
    @Override
    public void affect(Creature c) {
        affect();
    }

    /**
     * What the effect does when it is over
     */
    @Override
    public void done() {

    }

    /**
     * What the effect does when it is over
     *
     * @param c The creature which the effect was applied to
     */
    @Override
    public void done(Creature c) {

    }

    @Override
    public Vampirism makeCopy(Effect effect) {
        Vampirism copy = new Vampirism(effect instanceof Vampirism? ((Vampirism)effect).steal_proportion : 0f);
        copy.damage = effect instanceof Vampirism? ((Vampirism)effect).damage : 0;
        return copy;
    }

    public float getSteal_proportion() {
        return steal_proportion;
    }

    public void setSteal_proportion(float steal_proportion) {
        this.steal_proportion = steal_proportion;
    }

    public int getDamage() {
        return damage;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }
}
