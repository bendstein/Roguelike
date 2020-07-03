package creatureitem;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import creatureitem.ai.CreatureAi;
import creatureitem.item.Inventory;
import creatureitem.item.Item;
import utility.Utility;
import world.World;

import java.util.Locale;

public class Creature {

    //<editor-fold desc="Instance Variables">
    /**
     * Reference to the world the creature is in
     */
    protected World world;

    /**
     * The creature's x coordinate
     */
    protected int x;

    /**
     * The creature's y coordinate
     */
    protected int y;

    /**
     * The texture representing the creature
     */
    protected Texture texture;

    /**
     * The name of the creature
     */
    protected String name;

    /**
     * The behaviors the creature follows
     */
    protected creatureitem.ai.CreatureAi ai;

    /**
     * The creature's max HP
     */
    protected int maxHP;

    /**
     * The creature's current HP
     */
    protected int HP;

    /**
     * A measure of a creature's ability to evade an attack
     */
    protected int evasion;

    /**
     * A measure of a creature's ability to withstand an attack
     */
    protected int defense;

    /**
     * A measure of a creature's ability to deal damage
     */
    protected int attack;

    /**
     * Creatures are hostile to creatures on different teams
     */
    protected int team;

    /**
     * The system time last time this creature moved
     */
    protected long lastMovedTime;

    /**
     * Direction the creature is moving, based off of the numpad. If 0, not moving
     */
    protected int moveDirection;

    /**
     * Actor associated with this creature
     */
    protected Actor actor;

    /**
     * How far the creature can see
     */
    protected int visionRadius;

    protected Inventory inventory;

    //</editor-fold>

    public Creature(World world, String texturePath, String name, int team, int maxHP, int evasion, int defense, int attack, int visionRadius) {
        this.world = world;
        this.texture = new Texture(Gdx.files.internal(texturePath));
        this.name = name;
        this.team = team;
        this.maxHP = this.HP = maxHP;
        this.evasion = evasion;
        this.defense = defense;
        this.attack = attack;
        this.visionRadius = visionRadius;
        lastMovedTime = 0L;
        moveDirection = -1;
        actor = null;
        inventory = new Inventory();
    }

    //<editor-fold desc="Getters and Setters">
    public World getWorld() {
        return world;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public creatureitem.ai.CreatureAi getAi() {
        return ai;
    }

    public void setAi(CreatureAi ai) {
        this.ai = ai;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Texture getTexture() {
        return texture;
    }

    public void setTexture(Texture texture) {
        this.texture = texture;
    }

    public void setTexture(String texturePath) {
        this.texture = new Texture(Gdx.files.internal(texturePath));
    }

    public int getTeam() {
        return team;
    }

    public void setTeam(int team) {
        this.team = team;
    }

    public int getMaxHP() {
        return maxHP;
    }

    public void setMaxHP(int maxHP) {
        this.maxHP = maxHP;
    }

    public int getHP() {
        return HP;
    }

    public void setHP(int HP) {
        this.HP = HP;
    }

    public int getEvasion() {
        return evasion;
    }

    public void setEvasion(int evasion) {
        this.evasion = evasion;
    }

    public int getDefense() {
        return defense;
    }

    public void setDefense(int defense) {
        this.defense = defense;
    }

    public int getAttack() {
        return attack;
    }

    public void setAttack(int attack) {
        this.attack = attack;
    }

    public int getVisionRadius() {
        return visionRadius;
    }

    /**
     * Set both x and y coordinates
     * @param x X coordinate
     * @param y Y coordinate
     */
    public void setCoordinates(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public long getLastMovedTime() {
        return lastMovedTime;
    }

    public void setLastMovedTime(long lastMovedTime) {
        this.lastMovedTime = lastMovedTime;
    }

    public int getMoveDirection() {
        return moveDirection;
    }

    public void setMoveDirection(int moveDirection) {
        this.moveDirection = moveDirection;
    }

    public Actor getActor() {
        return actor;
    }

    public void setActor(Actor actor) {
        this.actor = actor;
    }

    //</editor-fold>

    /**
     * Change the creature's current HP
     * @param mod Amount to change HP by
     * @return true if the creature died
     */
    public boolean modifyHP(int mod) {
        if(HP + mod <= 0) {
            world.remove(this);
            return true;
        }
        else {
            HP = Math.min(maxHP, HP + mod);
            return false;
        }
    }

    /**
     * Change the creature's max HP
     * @param mod The amount to change maxHP by
     * @return true if the creature died
     */
    public boolean modifyMaxHp(int mod) {
        if(maxHP + mod <= 0) {
            world.remove(this);
            return true;
        }
        else {
            maxHP += mod;
            return false;
        }
    }

    /**
     * Move the creature through a wall by (wx, wy)
     * @param wx X distance
     * @param wy Y Distance
     */
    public void dig(int wx, int wy) {
        world.dig(wx, wy);
    }

    /**
     * Attack the foe creature
     * @param foe The creature to attack
     */
    public void attack(Creature foe) {
        int damage = -1 * Math.max(world.getRandom().nextInt(attack + 1) - foe.defense, 0);
        boolean died = foe.modifyHP(damage);
        doAction("attack %s for %d damage.", foe.name, Math.abs(damage));

        if(died)
            doAction("kill %s.", foe.name);

    }

    public void doAction(String message, Object ... params) {
        int r = 9;

        for(int ox = -r; ox < r + 1; ox++) {
            for(int oy = -r; oy < r + 1; oy++) {
                if(Math.pow(ox, 2) + Math.pow(oy, 2) > Math.pow(r, 2))
                    continue;

                Creature other = world.getCreatureAt(x + ox, y + oy);

                if(other == null)
                    continue;
                else if(other == this)
                    other.notify(String.format(Locale.getDefault(), "You %s", message), params);
                else if(other.canSee(x, y))
                    other.notify(String.format(Locale.getDefault(), "%s %s", name, Utility.makeSecondPerson(message)), params);
            }
        }
    }

    /**
     * Move the creature by (mx, my), and perform any behaviors dictated by its ai.
     * If there is a creature there, attack it instead of moving.
     * @param mx X distance
     * @param my Y distance
     */
    public void moveBy(int mx, int my) {
        Creature foe = world.getCreatureAt(x + mx, y + my);
        if(foe == null)
            ai.onEnter(x + mx, y + my, world.getTileAt(x + mx, y + my));
        else if(foe.team != team)
            attack(foe);
        lastMovedTime = System.currentTimeMillis();
    }

    /**
     * @param x X coord
     * @param y Y coord
     * @return true if a creature can enter this tile
     */
    public static boolean canEnter(int x, int y, World world) {
        return (world.getTileAt(x, y).isGround() &&
                world.getCreatureAt(x, y) == null && !world.queuedCreatureAt(x, y));
    }

    public boolean canSee(int x, int y) {
        return ai.canSee(x, y);
    }

    /**
     * Creature should do its turn
     */
    public void update() {
        ai.onUpdate();
    }

    /**
     * Received a message
     */
    public void notify(String message, Object ... params) {
        ai.onNotify(String.format(message, params));
    }

    public void pickUp() {
        Item item = world.getItemAt(x, y);

        if(item == null)
            doAction("grab at the ground.");
        else {
            doAction("pickup the %s.", item.getName());
            inventory.add(world.removeItemAt(x, y));
        }

    }

    public void drop(Item item) {
        inventory.remove(item);

        for(int i = 0; i <= 3; i++) {
            for(int j = 0; j <= 3; j++) {
                if(world.addAt(x + i, y + j, item)) {
                    doAction("drop the %s.", item.getName());
                    return;
                }
                else if(world.addAt(x + i, y - j, item)) {
                    doAction("drop the %s.", item.getName());
                    return;
                }
                else if(world.addAt(x - i, y + j, item)) {
                    doAction("drop the %s.", item.getName());
                    return;
                }
                else if(world.addAt(x - i, y - j, item)) {
                    doAction("drop the %s.", item.getName());
                    return;
                }
            }
        }

        doAction("drop the %s into the void.", item.getName());
    }

}
