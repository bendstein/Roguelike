package actors.creatures;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import game.ApplicationMain;
import creatureitem.Creature;

public class CreatureActor extends Actor {

    /**
     * Creature this actor represents
     */
    protected Creature creature;

    public CreatureActor(Creature creature) {
        this.creature = creature;
        this.creature.setActor(this);
        setBounds(creature.getX() * ApplicationMain.getTILE_SIZE(), creature.getY() * ApplicationMain.getTILE_SIZE(),
                ApplicationMain.getTILE_SIZE(), ApplicationMain.getTILE_SIZE());
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if(creature.getLevel().getPlayer() != null && creature.getLevel().getPlayer().canSee(creature.getX(), creature.getY()))
            batch.draw(creature.getTexture(), ApplicationMain.getTILE_SIZE() * creature.getX(), ApplicationMain.getTILE_SIZE() * creature.getY());
    }

    public Creature getCreature() {
        return creature;
    }

    public void setCreature(Creature creature) {
        this.creature = creature;
    }
}
