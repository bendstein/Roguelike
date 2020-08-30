package actors.creatures;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Batch;
import creatureitem.Player;
import game.Main;
import utility.Utility;
import world.geometry.Cursor;
import world.geometry.Line;
import world.geometry.Point;
import world.thing.DoorBehavior;

import java.util.ArrayList;
import java.util.Collection;

public class PlayerActor extends CreatureActor {

    public PlayerActor(Player player) {
        super(player);
    }

    @Override
    public void act(float delta) {
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        //Draw the texture
        batch.draw(creature.getTexture(), currentLocation.getX(), currentLocation.getY());

        Cursor cursor = ((Player)creature).getCursor();

        //Draw the cursor if it's active
        if(cursor.isActive()) {

            //Draw the line if the cursor has one
            if(cursor.isHasLine()) {
                Collection<Point> l;

                if(!cursor.hasPath()) l = new Line(creature.getX(), cursor.getX(), creature.getY(), cursor.getY()).getPoints();
                else l = cursor.getPath();

                int i = 0;
                boolean obstacle = false;

                Point first_creature = null;
                Point firstUnseen = null;

                //Cursor is green if the location is acceptable (in range, not in an obstacle, etc), and red if it's not
                for(Point p : l) {

                    //If the player must have seen the cursor location for it to be visible
                    if(cursor.isMustSee()) {

                        //If the player has never seen the location where the cursor is
                        if(!((Player) creature).getSeen(p.getX(), p.getY())) {

                            //If it's the first cursor in the line, or if the cursor is following a straigh line, draw neutral
                            if(i == 0 || !cursor.hasPath())
                                batch.draw(cursor.getNeutralTexture(), Main.getTileWidth() * p.getX(), Main.getTileHeight() * p.getY());

                            //If this is the first continuous unseen tile, mark it as so
                            else if(firstUnseen == null)
                                firstUnseen = p;

                            continue;
                        }

                        //If this isn't the first continuous unseen tile, draw a straight line of neutral cursors from the first unseen to this
                        else if(firstUnseen != null) {
                            ArrayList<Point> l2 = new Line(firstUnseen, p).getPoints();
                            l2.remove(l2.size() - 1);

                            for(Point p2 : l2) {
                                batch.draw(cursor.getNeutralTexture(), Main.getTileWidth() * p2.getX(), Main.getTileHeight() * p2.getY());
                            }

                            //If the player has seen this tile, mark first unseen as null
                            if(((Player) creature).getSeen(p.getX(), p.getY()))
                                firstUnseen = null;
                        }
                    }

                    boolean stop = false;

                    //If vision isn't required for the cursor, or the player has seen its location
                    if(!cursor.isMustSee() || ((Player) creature).getSeen(p.getX(), p.getY())) {

                        //If we are considering obstacles and there is some obstruction here, draw negative
                        if(cursor.isConsiderObstacle() &&
                                ((!creature.getLevel().isPassable(p.getX(), p.getY()) &&
                                        (creature.getLevel().getThingAt(p.getX(), p.getY()) == null || !(creature.getLevel().getThingAt(p.getX(), p.getY()).getBehavior() instanceof DoorBehavior))) || obstacle)) {
                            batch.draw(cursor.getNegativeTexture(), Main.getTileWidth() * p.getX(), Main.getTileHeight() * p.getY());
                            obstacle = true;
                            stop = true;
                        }

                        //If we are considering only one creature, draw positive then negative after
                        else if(cursor.isConsiderOneCreature() && creature.getLevel().getCreatureAt(p.getX(), p.getY()) != null && !creature.getLevel().getCreatureAt(p.getX(), p.getY()).equals(creature)) {

                            if(first_creature == null) {
                                batch.draw(cursor.getPositiveTexture(), Main.getTileWidth() * p.getX(), Main.getTileHeight() * p.getY());
                                first_creature = p;
                            }

                        }
                    }

                    //If we reached an obstacle, don't consider range or creatures
                    if(stop) {

                    }

                    //If the cursor has no range
                    else if(!cursor.isHasRange()) {

                        //If we are considering only one creature, draw positive then negative after
                        if(cursor.isConsiderOneCreature() && first_creature != null) {
                                if(p.equals(first_creature))
                                    batch.draw(cursor.getPositiveTexture(), Main.getTileWidth() * p.getX(), Main.getTileHeight() * p.getY());
                                else
                                    batch.draw(cursor.getNegativeTexture(), Main.getTileWidth() * p.getX(), Main.getTileHeight() * p.getY());
                        }

                        //Draw positive at the endpoint
                        else if(p.equals(cursor))
                            batch.draw(cursor.getPositiveTexture(), Main.getTileWidth() * p.getX(), Main.getTileHeight() * p.getY());

                        //Draw neutral for the rest of the line
                        else
                            batch.draw(cursor.getNeutralTexture(), Main.getTileWidth() * p.getX(), Main.getTileHeight() * p.getY());
                    }

                    //If the cursor has a range
                    else {

                        //If the cursor hasn't reached its range
                        if(i <= cursor.getRange()) {

                            //If we are considering only one creature, draw positive then negative after
                            if(cursor.isConsiderOneCreature() && first_creature != null) {
                                if(p.equals(first_creature))
                                    batch.draw(cursor.getPositiveTexture(), Main.getTileWidth() * p.getX(), Main.getTileHeight() * p.getY());
                                else
                                    batch.draw(cursor.getNegativeTexture(), Main.getTileWidth() * p.getX(), Main.getTileHeight() * p.getY());
                            }

                            //Draw positive at the endpoint
                            else if(p.equals(cursor) || i == cursor.getRange())
                                batch.draw(cursor.getPositiveTexture(), Main.getTileWidth() * p.getX(), Main.getTileHeight() * p.getY());
                            //Draw neutral for the rest of the line
                            else
                                batch.draw(cursor.getNeutralTexture(), Main.getTileWidth() * p.getX(), Main.getTileHeight() * p.getY());
                        }

                        //Draw negative for anything past the range
                        else
                            batch.draw(cursor.getNegativeTexture(), Main.getTileWidth() * p.getX(), Main.getTileHeight() * p.getY());
                    }

                    i++;
                }

            }

            //Cursor is green if the location is acceptable (in range, not in an obstacle, etc), and red if it's not
            else {
                if (cursor.isConsiderObstacle() && !creature.getLevel().isPassable(cursor.getX(), cursor.getY())) {
                    if(!cursor.isMustSee() || ((Player) creature).getSeen(cursor.getX(), cursor.getY())) {
                        batch.draw(cursor.getNegativeTexture(), Main.getTileWidth() * cursor.getX(), Main.getTileHeight() * cursor.getY());
                    }
                    else {
                        batch.draw(cursor.getNeutralTexture(), Main.getTileWidth() * cursor.getX(), Main.getTileHeight() * cursor.getY());
                    }
                }
                else if (!cursor.isHasRange()) {
                    if(!cursor.isMustSee() || ((Player) creature).getSeen(cursor.getX(), cursor.getY())) {
                        batch.draw(cursor.getPositiveTexture(), Main.getTileWidth() * cursor.getX(), Main.getTileHeight() * cursor.getY());
                    }
                    else {
                        batch.draw(cursor.getNeutralTexture(), Main.getTileWidth() * cursor.getX(), Main.getTileHeight() * cursor.getY());
                    }
                }
                else {
                    int r = Utility.getDistance(creature.getLocation(), cursor);
                    if(cursor.isMustSee() && !((Player) creature).getSeen(cursor.getX(), cursor.getY())) {
                        batch.draw(cursor.getNeutralTexture(), Main.getTileWidth() * cursor.getX(), Main.getTileHeight() * cursor.getY());
                    }
                    else if (r <= cursor.getRange())
                        batch.draw(cursor.getPositiveTexture(), Main.getTileWidth() * cursor.getX(), Main.getTileHeight() * cursor.getY());
                    else
                        batch.draw(cursor.getNegativeTexture(), Main.getTileWidth() * cursor.getX(), Main.getTileHeight() * cursor.getY());
                }

            }


            //If there is an area around the cursor, and the cursor is in range, draw it
            if(!cursor.isHasRange() || Utility.getDistance(creature.getLocation(), cursor) <= cursor.getRange() + 1) {
                if(cursor.isHasArea()) {
                    for(int i = -cursor.getRadius(); i <= cursor.getRadius(); i++) {
                        for(int j = -cursor.getRadius(); j <= cursor.getRadius(); j++) {
                            //if(i == 0 && j == 0) continue;
                            if(cursor.getX() + i < 0 || cursor.getX() + i >= Gdx.graphics.getWidth() || cursor.getY() + j < 0 || cursor.getY() + j >= Gdx.graphics.getHeight()) continue;

                            if(cursor.isMustSee()) {
                                if(!((Player) creature).getSeen(cursor.getX() + i, cursor.getY() + j)) {
                                    if(i == 0 && j == 0) {
                                        batch.draw(cursor.getNeutralTexture(), Main.getTileWidth() * (cursor.getX() + i), Main.getTileHeight() * (cursor.getY() + j));
                                        continue;
                                    }
                                }
                            }

                            if(Math.pow(i, 2) + Math.pow(j, 2) <= Math.pow(cursor.getRadius(), 2)) {
                                boolean obstacle = false;
                                for(Point p : new Line(cursor.getX(), cursor.getX() + i, cursor.getY(), cursor.getY() + j)) {

                                    if(cursor.isMustSee()) {
                                        if(!((Player) creature).getSeen(p.getX(), p.getY())) {
                                            batch.draw(cursor.getNeutralTexture(), Main.getTileWidth() * p.getX(), Main.getTileHeight() * p.getY());
                                            continue;
                                        }
                                    }

                                    if(cursor.isConsiderObstacle() && (!creature.getLevel().isPassable(p.getX(), p.getY()) || obstacle)) {
                                        batch.draw(cursor.getNegativeTexture(), Main.getTileWidth() * p.getX(), Main.getTileHeight() * p.getY());
                                        obstacle = true;
                                    }
                                    else
                                        batch.draw(cursor.getPositiveTexture(), Main.getTileWidth() * p.getX(), Main.getTileHeight() * p.getY());
                                }
                            }


                            /*
                            if(cursor.isConsiderObstacle() && !creature.getLevel().getTileAt(cursor.getX() + i, cursor.getY() + j).isPassable())
                                batch.draw(cursor.getNegative().getTexture(), Main.getTILE_SIZE() * (cursor.getX() + i), Main.getTILE_SIZE() * (cursor.getY() + j));
                            else
                                batch.draw(cursor.getPositive().getTexture(), Main.getTILE_SIZE() * (cursor.getX() + i), Main.getTILE_SIZE() * (cursor.getY() + j));

                             */

                        }
                    }
                }
            }

        }
    }

}
