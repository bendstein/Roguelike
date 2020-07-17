package actors.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import creatureitem.Creature;
import creatureitem.Player;
import creatureitem.ai.PlayerAi;
import game.Main;
import utility.Utility;
import world.Level;
import world.Tile;
import world.thing.DoorBehavior;
import world.thing.Stairs;
import world.thing.Thing;

import java.util.ArrayList;

public class LevelActor extends Actor {

    /**
     * Level this actor represents
     */
    protected Level level;
    protected Texture fogOfWar;

    public LevelActor(Level level) {
        this.level = level;
        setBounds(0, 0, level.getWidth() * Main.getTileWidth(), level.getHeight() * Main.getTileHeight());
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

        //if(level.getPlayer().getSeen(0, 0) == false)
            //((PlayerAi)level.getPlayer().getAi()).seenAll();

        for(int i = 0; i < level.getWidth(); i++) {
            for(int j = 0; j < level.getHeight(); j++) {

                if(level.getPlayer().canSee(i, j)) {
                    drawWall(batch, i, j);

                    if(level.getItemAt(i, j) != null) batch.draw(level.getItemAt(i, j).getTexture(), i * Main.getTileWidth(), j * Main.getTileHeight());
                    if(level.getThingAt(i, j) != null) {
                        Thing th = level.getThingAt(i, j);
                        if(th instanceof Stairs) {
                            if(((Stairs) th).isUp())
                                batch.draw(level.getThingAt(i, j).getTile().getSprite(1), i * Main.getTileWidth(), j * Main.getTileHeight());
                            else
                                batch.draw(level.getThingAt(i, j).getTile().getSprite(0), i * Main.getTileWidth(), j * Main.getTileHeight());
                        }
                        else if(th.getBehavior() instanceof DoorBehavior) {
                            Tile[][] adj = Utility.getAdjacentTiles(level.getTiles(), i, j);
                            if(adj[0][1] != null && adj[0][1] == Tile.WALL) {
                                if(th.isOpen())
                                    batch.draw(level.getThingAt(i, j).getTile().getSprite(4), i * Main.getTileWidth(), j * Main.getTileHeight());
                                else
                                    batch.draw(level.getThingAt(i, j).getTile().getSprite(0), i * Main.getTileWidth(), j * Main.getTileHeight());
                            }
                            else {
                                if(th.isOpen())
                                    batch.draw(level.getThingAt(i, j).getTile().getSprite(5), i * Main.getTileWidth(), j * Main.getTileHeight());
                                else
                                    batch.draw(level.getThingAt(i, j).getTile().getSprite(1), i * Main.getTileWidth(), j * Main.getTileHeight());
                            }
                        }

                    }
                    level.getPlayer().setSeen(i, j);
                }

                //If the player has seen the tile, but can't currently, draw only the tile, with FOW over it.
                else if(level.getPlayer().getSeen(i, j)) {
                    batch.enableBlending();
                    drawWall(batch, i, j);
                    if(level.getThingAt(i, j) != null) {
                        Thing th = level.getThingAt(i, j);
                        if(th instanceof Stairs) {
                            if(((Stairs) th).isUp())
                                batch.draw(level.getThingAt(i, j).getTile().getSprite(1), i * Main.getTileWidth(), j * Main.getTileHeight());
                            else
                                batch.draw(level.getThingAt(i, j).getTile().getSprite(0), i * Main.getTileWidth(), j * Main.getTileHeight());
                        }
                        else if(th.getBehavior() instanceof DoorBehavior) {
                            Tile[][] adj = Utility.getAdjacentTiles(level.getTiles(), i, j);
                            if(adj[0][1] != null && adj[0][1] == Tile.WALL) {
                                if(th.isOpen())
                                    batch.draw(level.getThingAt(i, j).getTile().getSprite(4), i * Main.getTileWidth(), j * Main.getTileHeight());
                                else
                                    batch.draw(level.getThingAt(i, j).getTile().getSprite(0), i * Main.getTileWidth(), j * Main.getTileHeight());
                            }
                            else {
                                if(th.isOpen())
                                    batch.draw(level.getThingAt(i, j).getTile().getSprite(5), i * Main.getTileWidth(), j * Main.getTileHeight());
                                else
                                    batch.draw(level.getThingAt(i, j).getTile().getSprite(1), i * Main.getTileWidth(), j * Main.getTileHeight());
                            }
                        }

                    }
                    batch.draw(fogOfWar, i * Main.getTileWidth(), j * Main.getTileHeight());
                }
            }
        }

    }

    public void drawWall(Batch batch, int i, int j) {
        //If the player can see the tile, draw the tile and everything in it

        Tile t = level.getTileAt(i, j);
        if(t == Tile.WALL) {
            Tile[][] adj = Utility.getAdjacentTiles(level.getTiles(), i, j);
            //For the intents of this method, treat null as if it was the same tile as t.
            for(int indexi = 0; indexi < 3; indexi++) {
                for(int indexj = 0; indexj < 3; indexj++) {
                    if(adj[indexi][indexj] == null) adj[indexi][indexj] = t;
                }
            }

            //Note: (0, 0) is bottom left corner, not top left.
            //No walls
            if(adj[0][1] != t && adj[1][0] != t && adj[1][2] != t && adj[2][1] != t) {
                batch.draw(level.getTileAt(i, j).getSprite(15), i * Main.getTileWidth(), j * Main.getTileHeight());
            }
            //No walls on: bottom, left, right
            else if(adj[0][1] != t && adj[1][0] != t && adj[1][2] != t && adj[2][1] == t) {
                batch.draw(level.getTileAt(i, j).getSprite(12), i * Main.getTileWidth(), j * Main.getTileHeight());
            }
            //No walls on: bottom, left, top
            else if(adj[0][1] != t && adj[1][0] != t && adj[1][2] == t && adj[2][1] != t) {
                batch.draw(level.getTileAt(i, j).getSprite(11), i * Main.getTileWidth(), j * Main.getTileHeight());
            }
            //No walls on: bottom, left
            else if(adj[0][1] != t && adj[1][0] != t && adj[1][2] == t && adj[2][1] == t) {
                batch.draw(level.getTileAt(i, j).getSprite(8), i * Main.getTileWidth(), j * Main.getTileHeight());

                if(adj[2][2] != t) {
                    batch.draw(level.getTileAt(i, j).getSprite(18), i * Main.getTileWidth(), j * Main.getTileHeight());
                }
            }
            //No walls on: bottom, right, top
            else if(adj[0][1] != t && adj[1][0] == t && adj[1][2] != t && adj[2][1] != t) {
                batch.draw(level.getTileAt(i, j).getSprite(3), i * Main.getTileWidth(), j * Main.getTileHeight());

                if(adj[0][0] != t) {
                    batch.draw(level.getTileAt(i, j).getSprite(17), i * Main.getTileWidth(), j * Main.getTileHeight());
                }
            }
            //No walls on: bottom, right
            else if(adj[0][1] != t && adj[1][0] == t && adj[1][2] != t && adj[2][1] == t) {
                batch.draw(level.getTileAt(i, j).getSprite(0), i * Main.getTileWidth(), j * Main.getTileHeight());
                if(adj[2][0] != t) {
                    batch.draw(level.getTileAt(i, j).getSprite(19), i * Main.getTileWidth(), j * Main.getTileHeight());
                }
            }
            //No walls on: bottom, top
            else if(adj[0][1] != t && adj[1][0] == t && adj[1][2] == t && adj[2][1] != t) {
                batch.draw(level.getTileAt(i, j).getSprite(7), i * Main.getTileWidth(), j * Main.getTileHeight());
            }
            //No walls on: bottom
            else if(adj[0][1] != t && adj[1][0] == t && adj[1][2] == t && adj[2][1] == t) {
                batch.draw(level.getTileAt(i, j).getSprite(4), i * Main.getTileWidth(), j * Main.getTileHeight());

                if(adj[2][0] != t) {
                    batch.draw(level.getTileAt(i, j).getSprite(19), i * Main.getTileWidth(), j * Main.getTileHeight());
                }
                if(adj[2][2] != t) {
                    batch.draw(level.getTileAt(i, j).getSprite(18), i * Main.getTileWidth(), j * Main.getTileHeight());
                }
            }
            //No walls on: left, right, top
            else if(adj[0][1] == t && adj[1][0] != t && adj[1][2] != t && adj[2][1] != t) {
                batch.draw(level.getTileAt(i, j).getSprite(14), i * Main.getTileWidth(), j * Main.getTileHeight());
            }
            //No walls on: left, right
            else if(adj[0][1] == t && adj[1][0] != t && adj[1][2] != t && adj[2][1] == t) {
                batch.draw(level.getTileAt(i, j).getSprite(13), i * Main.getTileWidth(), j * Main.getTileHeight());
            }
            //No walls on: left, top
            else if(adj[0][1] == t && adj[1][0] != t && adj[1][2] == t && adj[2][1] != t) {
                batch.draw(level.getTileAt(i, j).getSprite(10), i * Main.getTileWidth(), j * Main.getTileHeight());

                if(adj[0][2] != t) {
                    batch.draw(level.getTileAt(i, j).getSprite(16), i * Main.getTileWidth(), j * Main.getTileHeight());
                }
            }
            //No walls on: left
            else if(adj[0][1] == t && adj[1][0] != t && adj[1][2] == t && adj[2][1] == t) {
                batch.draw(level.getTileAt(i, j).getSprite(9), i * Main.getTileWidth(), j * Main.getTileHeight());
            }
            //No walls on: right, top
            else if(adj[0][1] == t && adj[1][0] == t && adj[1][2] != t && adj[2][1] != t) {
                batch.draw(level.getTileAt(i, j).getSprite(2), i * Main.getTileWidth(), j * Main.getTileHeight());

                if(adj[0][0] != t) {
                    batch.draw(level.getTileAt(i, j).getSprite(17), i * Main.getTileWidth(), j * Main.getTileHeight());
                }
            }
            //No walls on: right
            else if(adj[0][1] == t && adj[1][0] == t && adj[1][2] != t && adj[2][1] == t) {
                batch.draw(level.getTileAt(i, j).getSprite(1), i * Main.getTileWidth(), j * Main.getTileHeight());

                if(adj[0][0] != t) {
                    batch.draw(level.getTileAt(i, j).getSprite(17), i * Main.getTileWidth(), j * Main.getTileHeight());
                }
                if(adj[2][0] != t) {
                    batch.draw(level.getTileAt(i, j).getSprite(19), i * Main.getTileWidth(), j * Main.getTileHeight());
                }
            }
            //No walls on: top
            else if(adj[0][1] == t && adj[1][0] == t && adj[1][2] == t && adj[2][1] != t) {
                batch.draw(level.getTileAt(i, j).getSprite(6), i * Main.getTileWidth(), j * Main.getTileHeight());
                if(adj[0][0] != t) {
                    batch.draw(level.getTileAt(i, j).getSprite(17), i * Main.getTileWidth(), j * Main.getTileHeight());
                }
                if(adj[0][2] != t) {
                    batch.draw(level.getTileAt(i, j).getSprite(16), i * Main.getTileWidth(), j * Main.getTileHeight());
                }
            }
            //All walls
            else if(adj[0][1] == t && adj[1][0] == t && adj[1][2] == t && adj[2][1] == t) {
                batch.draw(level.getTileAt(i, j).getSprite(5), i * Main.getTileWidth(), j * Main.getTileHeight());

                if(adj[0][0] != t) {
                    batch.draw(level.getTileAt(i, j).getSprite(17), i * Main.getTileWidth(), j * Main.getTileHeight());
                }
                if(adj[2][0] != t) {
                    batch.draw(level.getTileAt(i, j).getSprite(19), i * Main.getTileWidth(), j * Main.getTileHeight());
                }
                if(adj[0][2] != t) {
                    batch.draw(level.getTileAt(i, j).getSprite(16), i * Main.getTileWidth(), j * Main.getTileHeight());
                }
                if(adj[2][2] != t) {
                    batch.draw(level.getTileAt(i, j).getSprite(18), i * Main.getTileWidth(), j * Main.getTileHeight());
                }
            }

        }
        else
            batch.draw(level.getTileAt(i, j).getSprite(0), i * Main.getTileWidth(), j * Main.getTileHeight());
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
