package actors.creatures;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Batch;
import creatureitem.Player;
import game.Main;
import creatureitem.Creature;
import utility.Utility;
import world.Tile;
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
        batch.draw(creature.getTexture(), Main.getTILE_SIZE() * creature.getX(), Main.getTILE_SIZE() * creature.getY());

        //Draw the cursor if it's active
        if(((Player)creature).getCursor().isActive()) {

            //Draw the line if the cursor has one
            if(((Player)creature).getCursor().isHasLine()) {
                Line l = new Line(creature.getX(), ((Player)creature).getCursor().getX(), creature.getY(), ((Player)creature).getCursor().getY());

                int i = 0;
                boolean obstacle = false;

                //Cursor is green if the location is acceptable (in range, not in an obstacle, etc), and red if it's not
                for(Point p : l) {

                    if(((Player)creature).getCursor().isConsiderObstacle() && (!creature.getLevel().getTileAt(p.getX(), p.getY()).isPassable() || obstacle)) {
                        batch.draw(Tile.CURSOR_RED.getTexture(), Main.getTILE_SIZE() * p.getX(), Main.getTILE_SIZE() * p.getY());
                        obstacle = true;
                    }

                    else if(!((Player)creature).getCursor().isHasRange())
                        batch.draw(Tile.CURSOR_GREEN.getTexture(), Main.getTILE_SIZE() * p.getX(), Main.getTILE_SIZE() * p.getY());
                    else {
                        if(i <= ((Player)creature).getCursor().getRange())
                            batch.draw(Tile.CURSOR_GREEN.getTexture(), Main.getTILE_SIZE() * p.getX(), Main.getTILE_SIZE() * p.getY());
                        else
                            batch.draw(Tile.CURSOR_RED.getTexture(), Main.getTILE_SIZE() * p.getX(), Main.getTILE_SIZE() * p.getY());
                    }
                    i++;
                }

            }

            //Cursor is green if the location is acceptable (in range, not in an obstacle, etc), and red if it's not
            else
                if(((Player)creature).getCursor().isConsiderObstacle() && !creature.getLevel().getTileAt(((Player)creature).getCursor().getX(), ((Player)creature).getCursor().getY()).isPassable())
                    batch.draw(Tile.CURSOR_RED.getTexture(), Main.getTILE_SIZE() * ((Player)creature).getCursor().getX(), Main.getTILE_SIZE() * ((Player)creature).getCursor().getY());
                else if(!((Player)creature).getCursor().isHasRange())
                    batch.draw(Tile.CURSOR_GREEN.getTexture(), Main.getTILE_SIZE() * ((Player)creature).getCursor().getX(), Main.getTILE_SIZE() * ((Player)creature).getCursor().getY());
                else {
                    int r = Utility.getDistance(creature.getLocation(), ((Player)creature).getCursor());
                    if(r <= ((Player)creature).getCursor().getRange())
                        batch.draw(Tile.CURSOR_GREEN.getTexture(), Main.getTILE_SIZE() * ((Player)creature).getCursor().getX(), Main.getTILE_SIZE() * ((Player)creature).getCursor().getY());
                    else
                        batch.draw(Tile.CURSOR_RED.getTexture(), Main.getTILE_SIZE() * ((Player)creature).getCursor().getX(), Main.getTILE_SIZE() * ((Player)creature).getCursor().getY());
                }
        }
    }

}
