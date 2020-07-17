package actors.creatures;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Batch;
import creatureitem.Player;
import game.Main;
import creatureitem.Creature;
import utility.Utility;
import world.Tile;
import world.geometry.Cursor;
import world.geometry.Line;
import world.geometry.Point;

public class PlayerActor extends CreatureActor {

    public PlayerActor(Player player) {
        super(player);
    }

    @Override
    public void act(float delta) {

        //Allow for continuous motion
        if(System.currentTimeMillis() - creature.getLastMovedTime() >= 100) {

            if(((Player)creature).getCurrentDestination() == null && ((Player) creature).getDestinationQueue().isEmpty()) {
                switch (creature.getMoveDirection()) {
                    case 0 : {
                        break;
                    }
                    case Input.Keys.NUMPAD_7: {
                        if(!((Player)creature).getCursor().isActive()) creature.moveBy(-1, 1);
                        else ((Player) creature).moveCursorBy(-1, 1);
                        break;
                    }
                    case Input.Keys.NUMPAD_8: {
                        if(!((Player)creature).getCursor().isActive()) creature.moveBy(0, 1);
                        else ((Player) creature).moveCursorBy(0, 1);
                        break;
                    }
                    case Input.Keys.NUMPAD_9: {
                        if(!((Player)creature).getCursor().isActive()) creature.moveBy(1, 1);
                        else ((Player) creature).moveCursorBy(1, 1);
                        break;
                    }
                    case Input.Keys.NUMPAD_4: {
                        if(!((Player)creature).getCursor().isActive()) creature.moveBy(-1, 0);
                        else ((Player) creature).moveCursorBy(-1, 0);
                        break;
                    }
                    case Input.Keys.NUMPAD_5: {
                        if(!((Player)creature).getCursor().isActive()) creature.moveBy(0, 0);
                        break;
                    }
                    case Input.Keys.NUMPAD_6: {
                        if(!((Player)creature).getCursor().isActive()) creature.moveBy(1, 0);
                        else ((Player) creature).moveCursorBy(1, 0);
                        break;
                    }
                    case Input.Keys.NUMPAD_1: {
                        if(!((Player)creature).getCursor().isActive()) creature.moveBy(-1, -1);
                        else ((Player) creature).moveCursorBy(-1, -1);
                        break;
                    }
                    case Input.Keys.NUMPAD_2: {
                        if(!((Player)creature).getCursor().isActive()) creature.moveBy(0, -1);
                        else ((Player) creature).moveCursorBy(0, -1);
                        break;
                    }
                    case Input.Keys.NUMPAD_3: {
                        if(!((Player)creature).getCursor().isActive()) creature.moveBy(1, -1);
                        else ((Player) creature).moveCursorBy(1, -1);
                        break;
                    }
                    default:

                }
            }

            //Move to next location in the queue
            if(((Player) creature).getCurrentDestination() == null && !((Player) creature).getDestinationQueue().isEmpty())
                ((Player) creature).setCurrentDestination(((Player) creature).dequeueDestination());

            if(((Player) creature).getCurrentDestination() != null)
                if(!creature.getAi().moveToDestination(((Player) creature).getCurrentDestination()))
                    ((Player) creature).setCurrentDestination(null);

            //Stop automatic movement if there is an adjacent enemy
            boolean adjenemy = false;
            for(int i = -1; i <= 1; i++) {
                for(int j = -1; j <= 1; j++) {
                    if(i == 0 && j == 0) continue;
                    int cx = creature.getX() + i;
                    int cy = creature.getY() + j;

                    if(cx < 0 || cx >= creature.getLevel().getWidth() || cy < 0 || cy >= creature.getLevel().getHeight()) continue;

                    if(creature.getLevel().getCreatureAt(cx, cy) != null) {
                        adjenemy = true;
                        break;
                    }

                }
                if(adjenemy) break;
            }


            //Don't stop the automatic movement if moving the cursor
            if(adjenemy && !((Player) creature).getCursor().isActive()) {
                ((Player) creature).setCurrentDestination(null);
                ((Player) creature).getDestinationQueue().clear();
            }

        }

    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        //Draw the texture
        batch.draw(creature.getTexture(), Main.getTileWidth() * creature.getX(), Main.getTileHeight() * creature.getY());

        Cursor cursor = ((Player)creature).getCursor();

        //Draw the cursor if it's active
        if(cursor.isActive()) {

            //Draw the line if the cursor has one
            if(cursor.isHasLine()) {
                Line l = new Line(creature.getX(), cursor.getX(), creature.getY(), cursor.getY());

                int i = 0;
                boolean obstacle = false;

                //Cursor is green if the location is acceptable (in range, not in an obstacle, etc), and red if it's not
                for(Point p : l) {

                    if(cursor.isConsiderObstacle() && (!creature.getLevel().isPassable(p.getX(), p.getY()) || obstacle)) {
                        batch.draw(cursor.getNegativeTexture(), Main.getTileWidth() * p.getX(), Main.getTileHeight() * p.getY());
                        obstacle = true;
                    }

                    else if(!cursor.isHasRange()) {
                        if(p.equals(cursor))
                            batch.draw(cursor.getPositiveTexture(), Main.getTileWidth() * p.getX(), Main.getTileHeight() * p.getY());
                        else
                            batch.draw(cursor.getNeutralTexture(), Main.getTileWidth() * p.getX(), Main.getTileHeight() * p.getY());
                    }
                    else {
                        if(i <= cursor.getRange()) {
                            if(p.equals(cursor) || i == cursor.getRange())
                                batch.draw(cursor.getPositiveTexture(), Main.getTileWidth() * p.getX(), Main.getTileHeight() * p.getY());
                            else
                                batch.draw(cursor.getNeutralTexture(), Main.getTileWidth() * p.getX(), Main.getTileHeight() * p.getY());
                        }
                        else
                            batch.draw(cursor.getNegativeTexture(), Main.getTileWidth() * p.getX(), Main.getTileHeight() * p.getY());
                    }
                    i++;
                }

            }

            //Cursor is green if the location is acceptable (in range, not in an obstacle, etc), and red if it's not
            else
                if(cursor.isConsiderObstacle() && !creature.getLevel().isPassable(cursor.getX(), cursor.getY()))
                    batch.draw(cursor.getNegativeTexture(), Main.getTileWidth() * cursor.getX(), Main.getTileHeight() * cursor.getY());
                else if(!cursor.isHasRange())
                    batch.draw(cursor.getPositiveTexture(), Main.getTileWidth() * cursor.getX(), Main.getTileHeight() * cursor.getY());
                else {
                    int r = Utility.getDistance(creature.getLocation(), cursor);
                    if(r <= cursor.getRange())
                        batch.draw(cursor.getPositiveTexture(), Main.getTileWidth() * cursor.getX(), Main.getTileHeight() * cursor.getY());
                    else
                        batch.draw(cursor.getNegativeTexture(), Main.getTileWidth() * cursor.getX(), Main.getTileHeight() * cursor.getY());
                }


            //If there is an area around the cursor, and the cursor is in range, draw it
            if(!cursor.isHasRange() || Utility.getDistance(creature.getLocation(), cursor) <= cursor.getRange() + 1) {
                if(cursor.isHasArea()) {
                    for(int i = -cursor.getRadius(); i <= cursor.getRadius(); i++) {
                        for(int j = -cursor.getRadius(); j <= cursor.getRadius(); j++) {
                            //if(i == 0 && j == 0) continue;
                            if(cursor.getX() + i < 0 || cursor.getX() + i >= Gdx.graphics.getWidth() || cursor.getY() + j < 0 || cursor.getY() + j >= Gdx.graphics.getHeight()) continue;

                            if(Math.pow(i, 2) + Math.pow(j, 2) <= Math.pow(cursor.getRadius(), 2)) {
                                boolean obstacle = false;
                                for(Point p : new Line(cursor.getX(), cursor.getX() + i, cursor.getY(), cursor.getY() + j)) {

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
