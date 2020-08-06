package actors.creatures;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import game.Main;
import creatureitem.Creature;
import world.geometry.Point;
import world.geometry.floatPoint;

public class CreatureActor extends Actor {

    /**
     * Creature this actor represents
     */
    protected Creature creature;

    /**
     * For when the creature is moving. Current location is the location they are at,
     * and destination is the point they're moving to.
     */
    protected floatPoint currentLocation, destination;

    /**
     * Create the actor, assign its creature to it, and assign it to its creature
     * @param creature The creature belonging to this actor
     */
    public CreatureActor(Creature creature) {
        this.creature = creature;
        currentLocation = new floatPoint(0, 0);
        destination = null;
        if(creature != null) {
            this.creature.setActor(this);

            setBounds(creature.getX() * Main.getTileWidth(), creature.getY() * Main.getTileHeight(),
                    creature.getTexture().getWidth(), creature.getTexture().getHeight());
            currentLocation = new floatPoint(getX(), getY());
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        //Only draw the creature if they're visible
        if(creature == null) return;
        if(creature.getLevel() == null) return;
        if(creature.getLevel().getPlayer() != null && creature.getLevel().getPlayer().canSee(creature.getX(), creature.getY()))
            batch.draw(creature.getTexture(), currentLocation.getX(), currentLocation.getY());
    }

    public Creature getCreature() {
        return creature;
    }

    public CreatureActor setCreature(Creature creature) {
        this.creature = creature;

        if(creature != null) {
            setBounds(creature.getX() * Main.getTileWidth(), creature.getY() * Main.getTileHeight(),
                    creature.getTexture().getWidth(), creature.getTexture().getHeight());
        }

        if(creature != null && (creature.getActor() == null || !creature.getActor().equals(this))) creature.setActor(this);

        return this;
    }

    public floatPoint getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(floatPoint currentLocation) {
        this.currentLocation = currentLocation;
    }

    public floatPoint getDestination() {
        return destination;
    }

    public void setDestination(floatPoint destination) {
        this.destination = destination;
    }

    public void moveTowardDestination(float amount) {
        float dx = currentLocation.getX() - destination.getX();
        float dy = currentLocation.getY() - destination.getY();
        float angle = (float) Math.atan(dy/dx);

        float x = amount * (float) Math.cos(angle);
        float y = amount * (float) Math.sin(angle);

        if(dx > 0) {
            if(dy > 0) {
                x *= -1;
                y *= -1;
            }
            else if(dy < 0) {
                x *= -1;
                y *= -1;
            }
            else {
                x *= -1;
                y *= 1;
            }
        }
        else if(dx < 0) {
            if(dy > 0) {
                x *= 1;
                y *= 1;
            }
            else if(dy < 0) {
                x *= 1;
                y *= 1;
            }
            else {
                x *= 1;
                y *= 1;
            }
        }
        else {
            if(dy > 0) {
                x *= 1;
                y *= -1;
            }
            else if(dy < 0) {
                x *= 1;
                y *= -1;
            }
        }

        if(currentLocation.getX() < destination.getX() && currentLocation.getX() + x > destination.getX()) {
            x = 0;
            currentLocation.setX(destination.getX());
        }
        else if(currentLocation.getX() > destination.getX() && currentLocation.getX() + x < destination.getX()) {
            x = 0;
            currentLocation.setX(destination.getX());
        }
        else if(currentLocation.getX() == destination.getX())
            x = 0;

        if(currentLocation.getY() < destination.getY() && currentLocation.getY() + y > destination.getY()) {
            y = 0;
            currentLocation.setY(destination.getY());
        }
        else if(currentLocation.getY() > destination.getY() && currentLocation.getY() + y < destination.getY()) {
            y = 0;
            currentLocation.setY(destination.getY());
        }
        else if(currentLocation.getY() == destination.getY())
            y = 0;

        currentLocation.setLocation(currentLocation.getX() + x, currentLocation.getY() + y);
        setX(currentLocation.getX());
        setY(currentLocation.getY());
    }

    public CreatureActor copy() {
        return new CreatureActor(creature);
    }
}
