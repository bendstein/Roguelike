package actors.world;

import actors.creatures.CreatureActor;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import creatureitem.Creature;
import creatureitem.ai.PlayerAi;
import creatureitem.generation.CreatureItemFactory;
import game.Main;
import utility.Utility;
import world.Level;
import world.Tile;
import world.geometry.Point;
import world.geometry.floatPoint;
import world.thing.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.NavigableSet;
import java.util.concurrent.ConcurrentSkipListSet;

public class LevelActor extends Group {

    /**
     * Level this actor represents
     */
    protected Level level;
    protected Texture fogOfWar;
    protected static NavigableSet<CreatureActor> cull =
            new ConcurrentSkipListSet<>((o1, o2) -> {
                if(o1 == null || o1.getCreature() == null) {
                    if(o2 == null || o2.getCreature() == null) return 0;
                    else return -1;
                }

                else if(o2 == null || o2.getCreature() == null) return 1;

                return o1.getCreature().getName().compareTo(o2.getCreature().getName());
            });

    public LevelActor(Level level) {
        this.level = level;
        setBounds(0, 0, level.getWidth() * Main.getTileWidth(), level.getHeight() * Main.getTileHeight());
        fogOfWar = new Texture(Gdx.files.internal("data/fogOfWar.png"));
    }

    public LevelActor copy() {
        LevelActor lactor = new LevelActor(level);
        lactor.setFogOfWar(fogOfWar);
        return lactor;
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        ArrayList<Creature> creatureQueue = level.addCreatureQueue();

        if(!level.getCreatures().contains(level.getPlayer()))
            level.addAt(level.getPlayer().getX(), level.getPlayer().getY(), level.getPlayer());

        //Add new actors to the world
        for(Creature c : creatureQueue)
            addActor(c.getActor());

        ArrayList<Creature> marked = new ArrayList<>();
        for(Creature c : level.getCreatures()) {
            if(((CreatureActor)c.getActor()).getDestination() != null) {
                ((CreatureActor)c.getActor()).moveTowardDestination(Main.getTileHeight()/(1f/.2f));
            }
            else
                continue;
            if(((CreatureActor)c.getActor()).getDestination().equals(((CreatureActor)c.getActor()).getCurrentLocation()))
                ((CreatureActor)c.getActor()).setDestination(null);

            if(c.getActor() == null) {
                CreatureActor ca = CreatureItemFactory.newActor(c);
                ca.setCurrentLocation(new floatPoint(c.getX() * Main.getTileWidth(), c.getY() * Main.getTileHeight()));
                marked.add(c);
            }

            if(!((CreatureActor)c.getActor()).getCreature().equals(c)) {

                if(!((CreatureActor)c.getActor()).getCreature().getActor().equals(c.getActor())) {
                    cull.add((CreatureActor)c.getActor());
                }

                CreatureActor ca = ((CreatureActor) c.getActor()).copy();
                ca.setCreature(c);
                ca.setCurrentLocation(new floatPoint(c.getX() * Main.getTileWidth(), c.getY() * Main.getTileHeight()));
                marked.add(c);
            }
        }

        for(Creature c : marked) {
            level.remove(c);
            level.addAt(c.getX(), c.getY(), c);
        }

        for(CreatureActor ca : cull) {
            ca.getParent().removeActor(ca);
            cull.remove(ca);
        }

        if(level.isRequestLightingUpdate())
            level.updateStaticLit();

        level.clearCreatureQueue();
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

                boolean not_on_screen = true;
                for(int i1 = -1; i1 <= 1; i1 += 1) {
                    for(int j1 = -1; j1 <= 1; j1 += 1) {
                        if(level
                                .getDungeon()
                                .getGame()
                                .getPlayScreen()
                                .getCamera()
                                .frustum
                                .pointInFrustum((i + i1) * Main.getTileWidth(), (j + j1) * Main.getTileHeight(), 0)) {
                            not_on_screen = false;
                        }
                    }
                    if(!not_on_screen) break;
                }

                if(not_on_screen) continue;

                boolean canSee = level.getPlayer().canSee(i, j);
                boolean seen = level.getSeen(i, j);

                /*
                 * Draw things that appear on the map as long as the player has seen them
                 */
                if (canSee || seen) {
                    drawWall(batch, i, j);

                    drawThing(batch, i, j);
                    drawItem(batch, i, j);
                }

                /*
                 * Draw things that appear on the map as long as the player can currently see them.
                 * Set the tile as being seen by the player
                 */
                if (canSee) {
                    if(!level.getSeen(i, j)) {
                        level.calculateOrientations();
                    }
                    level.getPlayer().setSeen(i, j);
                }

                /*
                 * Draw FOW in places the player has seen but can't currently see
                 */
                else if (seen) {
                    drawFOW(batch, Light.PURPLE, 8, i, j);
                }
            }
        }

        for(Creature c : level.getCreatures()) {
            if(level.getPlayer().canSee(c.getX(), c.getY()))
                c.getActor().draw(batch, parentAlpha);
        }

        drawLighting(batch);
        drawDarkness(batch);

    }

    public void drawAdjFloor(Batch batch, int i, int j) {
        if(level.getTileAt(i, j) != Tile.FLOOR) return;

        Tile[][] adj = level.getAdjacentTiles(i, j);

        for(int x = 0; x < adj.length; x++) {
            for(int y = 0; y < adj[0].length; y++) {
                if(adj[x][y] == Tile.WALL)
                    batch.draw(level.getTileAt(i, j).getSprite(0), (i + (x - 1)) * Main.getTileWidth(), (j + (y - 1)) * Main.getTileHeight());
            }
        }
    }

    public void drawWall(Batch batch, int i, int j) {

        Tile t = level.getTileAt(i, j);
        for(int x : level.getOrientation(i, j)) {
            batch.draw(t.getSprite(x), i * Main.getTileWidth(), j * Main.getTileHeight());
        }

        /*
        //If the player can see the tile, draw the tile and everything in it

        Tile t = level.getTileAt(i, j);

        if(t == Tile.WALL) {
            Tile[][] adj = Utility.getAdjacentTiles(level.getTiles(), i, j);
            boolean[][] toChange = new boolean[adj.length][adj[0].length];
            //For the intents of this method, treat null as if it was the same tile as t.
            //Same with walls the player can't see
            for(int indexi = 0; indexi < adj.length; indexi++) {
                for(int indexj = 0; indexj < adj[0].length; indexj++) {
                    if(adj[indexi][indexj] == null) toChange[indexi][indexj] = true;
                    else if(!level.getSeen(i + indexi - 1, j + indexj - 1)) adj[indexi][indexj] = t;
                    else toChange[indexi][indexj] = false;
                }
            }

            for(int indexi = 0; indexi < adj.length; indexi++) {
                for(int indexj = 0; indexj < adj[0].length; indexj++) {
                    if(toChange[indexi][indexj]) adj[indexi][indexj] = t;
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

         */
    }

    public void drawItem(Batch batch, int i, int j) {
        if(level.getItemAt(i, j) != null) batch.draw(level.getItemAt(i, j).getTexture(), i * Main.getTileWidth(), j * Main.getTileHeight());
    }

    public void drawThing(Batch batch, int i, int j) {
        Thing t = level.getThingAt(i, j);
        if(level.getThingAt(i, j) != null) {
            if(t.getOrientation() != -1)
                batch.draw(t.getTile().getSprite(t.getOrientation()), i * Main.getTileWidth(), j * Main.getTileHeight());
        }
        /*
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
            else if(th instanceof Light) {
                if(th instanceof LightRandom && System.currentTimeMillis() - ((LightRandom) th).getLastChange() > ((LightRandom) th).getCurrentRate())
                    ((LightRandom) th).changeColors();
                switch (((Light) th).getPosition()) {
                    case Light.LEFT: {
                        if(((Light) th).isActive()) {
                            batch.draw(level.getThingAt(i, j).getTile().getSprite(0), i * Main.getTileWidth(), j * Main.getTileHeight());
                        }
                        else {
                            batch.draw(level.getThingAt(i, j).getTile().getSprite(4), i * Main.getTileWidth(), j * Main.getTileHeight());
                        }
                        break;
                    }
                    case Light.TOP: {
                        if(((Light) th).isActive()) {
                            batch.draw(level.getThingAt(i, j).getTile().getSprite(2), i * Main.getTileWidth(), j * Main.getTileHeight());
                        }
                        else {
                            batch.draw(level.getThingAt(i, j).getTile().getSprite(6), i * Main.getTileWidth(), j * Main.getTileHeight());
                        }
                        break;
                    }
                    case Light.RIGHT: {
                        if(((Light) th).isActive()) {
                            batch.draw(level.getThingAt(i, j).getTile().getSprite(1), i * Main.getTileWidth(), j * Main.getTileHeight());
                        }
                        else {
                            batch.draw(level.getThingAt(i, j).getTile().getSprite(5), i * Main.getTileWidth(), j * Main.getTileHeight());
                        }
                        break;
                    }
                    case Light.BOTTOM: {
                        if(((Light) th).isActive()) {
                            batch.draw(level.getThingAt(i, j).getTile().getSprite(3), i * Main.getTileWidth(), j * Main.getTileHeight());
                        }
                        else {
                            batch.draw(level.getThingAt(i, j).getTile().getSprite(7), i * Main.getTileWidth(), j * Main.getTileHeight());
                        }
                        break;
                    }
                    default: {
                        break;
                    }
                }

            }
            else
                batch.draw(level.getThingAt(i, j).getTile().getSprite(0), i * Main.getTileWidth(), j * Main.getTileHeight());

        }

         */
    }

    public void drawLighting(Batch batch) {

        batch.enableBlending();

        ArrayList<Light>[][] emittingArray = level.getEmitting();
        for(int i = 0; i < level.getWidth(); i++) {
            for(int j = 0; j < level.getHeight(); j++) {

                boolean not_on_screen = true;
                for(int i1 = -1; i1 <= 1; i1 += 1) {
                    for(int j1 = -1; j1 <= 1; j1 += 1) {
                        if(level
                                .getDungeon()
                                .getGame()
                                .getPlayScreen()
                                .getCamera()
                                .frustum
                                .pointInFrustum((i + i1) * Main.getTileWidth(), (j + j1) * Main.getTileHeight(), 0)) {
                            not_on_screen = false;
                        }
                    }
                    if(!not_on_screen) break;
                }

                if(not_on_screen) continue;

                /*
                 * Get all lights shining on this tile
                 */
                ArrayList<Light> emitting = emittingArray[i][j];

                /*
                 * If there are none, don't draw any lights
                 */
                if(emitting == null || emitting.isEmpty()) continue;


                /*
                 * If the tile is out of sight range, don't draw it
                 */
                Point p = new Point(i, j);

                /*
                if(p.chebychevDistanceFrom(level.getPlayer().getLocation()) >
                        level.getPlayer().getVisionRadius() * level.getPlayer().getAi().getLightVisionFactor())
                    continue;

                 */

                if(!level.getPlayer().canSee(p)) continue;

                /*
                 * Otherwise, get the closest light
                 */
                Light closest = null;
                double closestDistance = Integer.MAX_VALUE;

                for(Light l : emitting) {
                    if(!l.isActive()) continue;
                    double distance = l.getLocation().euclideanDistanceFrom(p);
                    if(closestDistance > distance) {
                        closestDistance = distance;
                        closest = l instanceof LightRandom? (LightRandom)l : l;
                    }
                }

                if(closest == null) continue;

                //Shouldn't matter, but I'll do it anyways
                closestDistance = Math.abs(closestDistance);

                float intensityMod = ((float)Math.log((closest.getRange() - 1) -
                        (closestDistance))) /
                        ((float)Math.log(closest.getRange() - 1));
                //float intensityMod = (float)Math.exp(-1.5 * closestDistance/closest.getRange());

                if(closestDistance >= closest.getRange() - 1) {
                    drawFOW(batch, Light.GREY2, 5, i, j);
                }
                else if(closestDistance >= closest.getRange() - 2) {
                    drawFOW(batch, Light.GREY2, 3, i, j);
                }
                else {
                    closest.getTint().a = closest.getBrightness() * intensityMod;
                    batch.setColor(closest.getTint());
                    for(int times = 0; times < closest.getIntensity() * intensityMod; times++) {
                        int[] colors = closest.getColor(i - closest.getX() + closest.getRange(), j - closest.getY() + closest.getRange());

                        float percentFinished = (float)times/(closest.getIntensity() * intensityMod);
                        //float percentDistance = (float)(Math.sqrt(Math.pow(i, 2) + Math.pow(j, 2))/l.getRange());
                        int currentColorIndex = (int)Math.floor(percentFinished * (colors.length));

                        //for(int it = 0; it < colors.length * intensityMod; it++)
                        //batch.draw(Tile.LIGHT.getSprite(colors[it]), (x + i) * Main.getTileWidth(), (y + j) * Main.getTileHeight());

                        batch.draw(Tile.LIGHT.getSprite(colors[currentColorIndex]),
                                i * Main.getTileWidth(), j * Main.getTileHeight());
                    }

                }

                batch.setColor(Color.WHITE);


            }
        }

        /*
        ArrayList<Light> lights = new ArrayList<>();
        for(Thing t : level.getThings())
            if(t instanceof Light && ((Light) t).isActive()) lights.add((Light) t);

        lights.forEach(l -> {
            int x = l.getX(), y = l.getY();

            if(Utility.getChebshevDistance(x, level.getPlayer().getX(), y, level.getPlayer().getY()) - l.getRange() >
                    level.getPlayer().getVisionRadius() * level.getPlayer().getAi().getLightVisionFactor())
                return;

            for(int i = -l.getRange(); i <= l.getRange(); i++) {
                for(int j = -l.getRange(); j <= l.getRange(); j++) {
                    if(!level.isOutOfBounds(x + i, y + j) &&
                            l.canLight(x + i, y + j, level) != 0) {
                        if(!sight || level.getPlayer().canSee(x + i, y + j)) {

                            double circle = Math.pow(i, 2) + Math.pow(j, 2);

                            float intensityMod = ((float)Math.log((l.getRange() - 1) -
                                    (Math.sqrt(circle)))) /
                                    ((float)Math.log(l.getRange() - 1));

                            if(circle >= Math.pow(l.getRange() - 1, 2)) {
                                if(level.getEmitting(x + i, y + j).size() == 1) drawFOW(batch, Light.GREY2, 5, x + i, y + j);
                                continue;
                            }

                            else if(circle >= Math.pow(l.getRange() - 2, 2)) {
                                if(level.getEmitting(x + i, y + j).size() == 1) drawFOW(batch, Light.GREY2, 3, x + i, y + j);
                                continue;
                            }

                            //batch.setColor(new Color(255f, 255f, 255f, l.getBrightness() * intensityMod));
                            l.getTint().a = l.getBrightness() * intensityMod;
                            batch.setColor(l.getTint());
                            for(int times = 0; times < l.getIntensity() * intensityMod; times++) {
                                int[] colors = l.getColor(i + l.getRange(), j + l.getRange());

                                float percentFinished = (float)times/(l.getIntensity() * intensityMod);
                                //float percentDistance = (float)(Math.sqrt(Math.pow(i, 2) + Math.pow(j, 2))/l.getRange());
                                int currentColorIndex = (int)Math.floor(percentFinished * (colors.length));

                                //for(int it = 0; it < colors.length * intensityMod; it++)
                                    //batch.draw(Tile.LIGHT.getSprite(colors[it]), (x + i) * Main.getTileWidth(), (y + j) * Main.getTileHeight());

                                batch.draw(Tile.LIGHT.getSprite(colors[currentColorIndex]), (x + i) * Main.getTileWidth(), (y + j) * Main.getTileHeight());
                            }
                            batch.setColor(Color.WHITE);

                        }

                    }
                }
            }
        });

         */

    }

    public void drawFOW(Batch batch, int color, int intensity, int i, int j) {
        batch.enableBlending();
        batch.setColor(0, 0, 0, 1);

        for(int it = 0; it < intensity; it++)
            batch.draw(Tile.LIGHT.getSprite(color), i * Main.getTileWidth(), j * Main.getTileHeight());

        batch.setColor(Color.WHITE);
    }

    public void drawDarkness(Batch batch) {
        for(int i = 0; i < level.getWidth(); i++) {
            for(int j = 0; j < level.getHeight(); j++) {

                boolean not_on_screen = true;
                for(int i1 = -1; i1 <= 1; i1 += 1) {
                    for(int j1 = -1; j1 <= 1; j1 += 1) {
                        if(level
                                .getDungeon()
                                .getGame()
                                .getPlayScreen()
                                .getCamera()
                                .frustum
                                .pointInFrustum((i + i1) * Main.getTileWidth(), (j + j1) * Main.getTileHeight(), 0)) {
                            not_on_screen = false;
                        }
                    }
                    if(!not_on_screen) break;
                }

                if(not_on_screen) continue;

                if(!level.getPlayer().canSee(i, j) || !level.getStaticLit(i, j))
                    drawFOW(batch, Light.GREY3, 5, i, j);
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

    public static NavigableSet<CreatureActor> getCull() {
        return cull;
    }

    public static void setCull(NavigableSet<CreatureActor> cull) {
        LevelActor.cull = cull;
    }

    public static void addCull(CreatureActor ca) {
        cull.add(ca);
    }

    public void switchLevel(Level level, Level l) {
        for(Creature c : level.getCreatures()) {
            c.getActor().remove();
        }

        for(Creature c : l.getCreatures()) {
            addActor(c.getActor());
        }

        if(l.getActor() != null) {
            l.setActor(this);
            level.setActor(null);
            setLevel(l);
        }

        if(l != null) l.calculateOrientations();
    }

    //</editor-fold>
}
