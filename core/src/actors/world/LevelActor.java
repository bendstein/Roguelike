package actors.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import creatureitem.Creature;
import game.ApplicationMain;
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
        setBounds(0, 0, level.getWidth() * ApplicationMain.getTILE_SIZE(), level.getHeight() * ApplicationMain.getTILE_SIZE());
        fogOfWar = new Texture(Gdx.files.internal("data/fogOfWar.png"));
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        ArrayList<Creature> creatureQueue = level.addCreatureQueue();

        for(Creature c : creatureQueue)
            getParent().addActor(c.getActor());

        level.clearCreatureQueue();
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        for(int i = 0; i < level.getWidth(); i++) {
            for(int j = 0; j < level.getHeight(); j++) {
                if(level.getPlayer().canSee(i, j)) {
                    batch.draw(level.getTileAt(i, j).getTexture(), i * ApplicationMain.getTILE_SIZE(), j * ApplicationMain.getTILE_SIZE());
                    if(level.getItemAt(i, j) != null) batch.draw(level.getItemAt(i, j).getTexture(), i * ApplicationMain.getTILE_SIZE(), j * ApplicationMain.getTILE_SIZE());
                    level.getPlayer().setSeen(i, j);
                }
                else if(level.getPlayer().getSeen(i, j)) {
                    batch.enableBlending();
                    batch.draw(level.getTileAt(i, j).getTexture(), i * ApplicationMain.getTILE_SIZE(), j * ApplicationMain.getTILE_SIZE());
                    batch.draw(fogOfWar, i * ApplicationMain.getTILE_SIZE(), j * ApplicationMain.getTILE_SIZE());
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
