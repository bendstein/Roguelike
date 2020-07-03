package actors.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import creatureitem.Creature;
import game.ApplicationMain;
import world.World;

import java.util.ArrayList;

public class WorldActor extends Actor {

    /**
     * World this actor represents
     */
    protected World world;
    protected Texture fogOfWar;

    public WorldActor(World world) {
        this.world = world;
        this.world.setActor(this);
        setBounds(0, 0, world.getWidth() * ApplicationMain.getTILE_SIZE(), world.getHeight() * ApplicationMain.getTILE_SIZE());
        fogOfWar = new Texture(Gdx.files.internal("data/fogOfWar.png"));
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        ArrayList<Creature> creatureQueue = world.addCreatureQueue();

        for(Creature c : creatureQueue)
            getParent().addActor(c.getActor());

        world.clearCreatureQueue();
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        for(int i = 0; i < world.getWidth(); i++) {
            for(int j = 0; j < world.getHeight(); j++) {
                if(world.getPlayer().canSee(i, j)) {
                    batch.draw(world.getTileAt(i, j).getTexture(), i * ApplicationMain.getTILE_SIZE(), j * ApplicationMain.getTILE_SIZE());
                    if(world.getItemAt(i, j) != null) batch.draw(world.getItemAt(i, j).getTexture(), i * ApplicationMain.getTILE_SIZE(), j * ApplicationMain.getTILE_SIZE());
                    world.getPlayer().setSeen(i, j);
                }
                else if(world.getPlayer().getSeen(i, j)) {
                    batch.enableBlending();
                    batch.draw(world.getTileAt(i, j).getTexture(), i * ApplicationMain.getTILE_SIZE(), j * ApplicationMain.getTILE_SIZE());
                    batch.draw(fogOfWar, i * ApplicationMain.getTILE_SIZE(), j * ApplicationMain.getTILE_SIZE());
                }
            }
        }
    }
}
