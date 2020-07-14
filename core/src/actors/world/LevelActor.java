package actors.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import creatureitem.Creature;
import game.Main;
import world.Level;

import java.util.ArrayList;

public class LevelActor extends Actor {

    /**
     * Level this actor represents
     */
    protected Level level;
    protected Texture fogOfWar;

    public LevelActor(Level level) {
        this.level = level;
        setBounds(0, 0, level.getWidth() * Main.getTILE_SIZE(), level.getHeight() * Main.getTILE_SIZE());
        fogOfWar = new Texture(Gdx.files.internal("data/fogOfWar.png"));
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

        for(int i = 0; i < level.getWidth(); i++) {
            for(int j = 0; j < level.getHeight(); j++) {

                //If the player can see the tile, draw the tile and everything in it
                if(level.getPlayer().canSee(i, j)) {
                    batch.draw(level.getTileAt(i, j).getTexture(), i * Main.getTILE_SIZE(), j * Main.getTILE_SIZE());
                    if(level.getItemAt(i, j) != null) batch.draw(level.getItemAt(i, j).getTexture(), i * Main.getTILE_SIZE(), j * Main.getTILE_SIZE());
                    level.getPlayer().setSeen(i, j);
                }

                //If the player has seen the tile, but can't currently, draw only the tile, with FOW over it.
                else if(level.getPlayer().getSeen(i, j)) {
                    batch.enableBlending();
                    batch.draw(level.getTileAt(i, j).getTexture(), i * Main.getTILE_SIZE(), j * Main.getTILE_SIZE());
                    batch.draw(fogOfWar, i * Main.getTILE_SIZE(), j * Main.getTILE_SIZE());
                }
            }
        }

    }

    //<editor-fold desc="Getters and Setters">
    public Level getLevel() {
        return level;
    }

    public void setLevel(Level level) {
        this.level = level;
    }

    public Texture getFogOfWar() {
        return fogOfWar;
    }

    public void setFogOfWar(Texture fogOfWar) {
        this.fogOfWar = fogOfWar;
    }
    //</editor-fold>
}
