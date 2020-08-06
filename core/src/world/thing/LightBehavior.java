package world.thing;

import creatureitem.Creature;

public class LightBehavior extends ThingBehavior {

    public LightBehavior(Light light) {
        super(light);
    }

    @Override
    public boolean onInteract(Creature c) {
        ((Light)thing).setActive(!((Light)thing).isActive());
        c.getLevel().updateStaticLit();
        return true;
    }

    @Override
    public void onInteract() {
        ((Light)thing).setActive(!((Light)thing).isActive());
    }
}
