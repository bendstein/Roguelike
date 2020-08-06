package actors.world;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import game.Main;
import world.Level;
import world.Tile;
import world.thing.Light;

public class MinimapActor extends LevelActor {

    public MinimapActor(Level level) {
        super(level);
    }

    public MinimapActor(LevelActor lactor) {
        super(lactor.level);
        fogOfWar = lactor.fogOfWar;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        //if(!level.getPlayer().getSeen(0, 0))
        //((PlayerAi)level.getPlayer().getAi()).seenAll();

        /*
         * Draw floor tiles under walls to take care of transparency at corners
         */
        for(int i = 0; i < level.getWidth(); i++) {
            for(int j = 0; j < level.getHeight(); j++) {
                boolean canSee = level.getPlayer().canSee(i, j);
                boolean seen = level.getSeen(i, j);

                /*
                 * Draw things that appear on the map as long as the player has seen them
                 */
                if (canSee || seen)
                    drawAdjFloor(batch, i, j);
            }
        }

        for(int i = 0; i < level.getWidth(); i++) {
            for(int j = 0; j < level.getHeight(); j++) {
                boolean canSee = level.getPlayer().canSee(i, j);
                boolean seen = level.getSeen(i, j);

                if (canSee || seen) {
                    drawWall(batch, i, j);
                    drawThing(batch, i, j);
                    drawItem(batch, i, j);

                }

                if (canSee) {
                    if(level.getCreatureAt(i, j) != null) {
                        batch.draw(level.getCreatureAt(i, j).getTexture(), i * Main.getTileWidth(), j * Main.getTileHeight());
                    }
                }

                else if(!canSee && !seen) {
                    batch.setColor(Color.BLACK);
                    batch.draw(Tile.BOUNDS.getSprite(0), i * Main.getTileWidth(), j * Main.getTileHeight());
                    batch.setColor(Color.WHITE);
                }

            }
        }

        drawLighting(batch);

    }
}
