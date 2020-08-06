package prefabbuilder;

import actors.creatures.CreatureActor;
import actors.world.LevelActor;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import creatureitem.Creature;
import game.Main;
import world.Level;
import world.thing.Light;

import java.util.ArrayList;

public class PrefabBuilderActor extends LevelActor {

    public PrefabBuilderActor(Level level) {
        super(level);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        ArrayList<Creature> creatureQueue = level.addCreatureQueue();

        //Add new actors to the world
        for(Creature c : creatureQueue)
            getParent().addActor(c.getActor());

        level.clearCreatureQueue();
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {

        /*
         * Draw floor tiles under walls to take care of transparency at corners
         */
        for(int i = 0; i < level.getWidth(); i++) {
            for(int j = 0; j < level.getHeight(); j++) {

                /*
                 * Draw things that appear on the map
                 */
                drawAdjFloor(batch, i, j);
            }
        }

        for(int i = 0; i < level.getWidth(); i++) {
            for(int j = 0; j < level.getHeight(); j++) {

                /*
                 * Draw things that appear on the map
                 */
                drawWall(batch, i, j);
                drawThing(batch, i, j);
                drawItem(batch, i, j);
                drawCreatures(batch);

            }
        }

        drawLighting(batch);

    }

    public void drawCreatures(Batch b) {
        for(Creature c : level.getCreatures()) {
            if(c.getActor() != null) {
                CreatureActor creatureActor = (CreatureActor) c.getActor();
                b.draw(c.getTexture(), creatureActor.getCurrentLocation().getX(), creatureActor.getCurrentLocation().getY());
            }
        }
    }
}
