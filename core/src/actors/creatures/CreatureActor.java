package actors.creatures;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import game.Main;
import creatureitem.Creature;

public class CreatureActor extends Actor {

    /**
     * Creature this actor represents
     */
    protected Creature creature;

    /**
     * Create the actor, assign its creature to it, and assign it to its creature
     * @param creature The creature belonging to this actor
     */
    public CreatureActor(Creature creature) {
        this.creature = creature;
        this.creature.setActor(this);
        setBounds(creature.getX() * Main.getTileWidth(), creature.getY() * Main.getTileHeight(),
                Main.getTileWidth(), Main.getTileHeight());
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        //Only draw the creature if they're visible
        if(creature.getLevel().getPlayer() != null && creature.getLevel().getPlayer().canSee(creature.getX(), creature.getY()))
            batch.draw(creature.getTexture(), Main.getTileWidth() * creature.getX(), Main.getTileHeight() * creature.getY());
    }

    public Creature getCreature() {
        return creature;
    }

    public void setCreature(Creature creature) {
        this.creature = creature;
    }
}
