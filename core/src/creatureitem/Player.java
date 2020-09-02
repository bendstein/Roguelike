package creatureitem;

import actors.creatures.CreatureActor;
import actors.world.LevelActor;
import creatureitem.ai.types.NPCAi;
import creatureitem.ai.PlayerAi;
import creatureitem.item.*;
import creatureitem.spell.*;
import utility.Utility;
import world.Level;
import world.geometry.Cursor;
import world.geometry.Point;
import world.thing.DoorBehavior;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Objects;

public class Player extends creatureitem.Creature {

    //<editor-fold desc="Instance Variables">
    /**
     * True if player died
     */
    private boolean isDead;

    /**
     * The player will move towards the destination each turn, if not null.
     */
    private Point currentDestination;

    /**
     * If the current destination is null, will go to the next queued location, if it exists
     */
    private ArrayDeque<Point> destinationQueue;

    /**
     * The item we're preparing to launch
     */
    private Item toThrow;

    /**
     * The spell we're preparing to cast
     */
    private Spell toCast;

    /**
     * Cursor pointing at a tile on the map
     */
    private Cursor cursor;

    /**
     * Reference to the creature the player is speaking to
     */
    private Creature talkingTo;

    private boolean canSeeEverything;

    boolean allowed_to_act;
    //</editor-fold>

    public Player(int maxHP, int hungerMax, int exp, int strength, int agility, int constitution, int perception, int intelligence, int discipline, int charisma,
                  Level level, String texturePath, String name, char glyph, int team, Item unarmedAttack, int natArmor, float energy_factor, String ... properties) {
        super(maxHP, hungerMax, exp, strength, agility, constitution, perception, intelligence, discipline, charisma,
        level, texturePath, name, glyph, team, unarmedAttack, natArmor, energy_factor, properties);

        isDead = false;
        currentDestination = null;
        destinationQueue = new ArrayDeque<>();
        toThrow = null;
        cursor = new Cursor(0, 0);
        canSeeEverything = false;
        talkingTo = null;
        allowed_to_act = false;
    }

    public Player(Player player) {
        super(player);
        isDead = player.isDead;
        currentDestination = player.currentDestination;
        currentDestination = player.currentDestination;
        destinationQueue = player.destinationQueue;
        toThrow = player.toThrow;
        cursor = player.cursor;
        canSeeEverything = player.canSeeEverything;
        talkingTo = player.talkingTo;
        allowed_to_act = player.allowed_to_act;
    }

    //<editor-fold desc="Getters and Setters">
    public boolean isAllowed_to_act() {
        return allowed_to_act;
    }

    public void setAllowed_to_act(boolean allowed_to_act) {
        this.allowed_to_act = allowed_to_act;
    }

    public boolean isDead() {
        return isDead;
    }

    public void setDead(boolean dead) {
        isDead = dead;
    }

    public boolean[][] getSeenTiles() {
        return level.getSeen();
    }

    public void setSeenTiles(boolean[][] seenTiles) {
        level.setSeen(seenTiles);
    }

    public void setSeenAllTiles() {
        for(boolean[] b : getSeenTiles())
            Arrays.fill(b, true);
    }

    public void setSeen(int i, int j) {
        level.setSeen(i, j);
    }

    public boolean getSeen(int i, int j) {
        return level.getSeen(i, j);
    }

    @Override
    public void setLevel(Level level) {
        super.setLevel(level);
        if(level != null) {
            level.setPlayer(this);
        }
    }

    public void moveLevel(Level level) {
        if(this.level != null) {
            LevelActor lactor = this.level.getActor();
            this.level.setPlayer(null);
            this.level.remove(this);
            this.level.setActor(null);
            ((CreatureActor)getActor()).setDestination(null);
            if(lactor != null) lactor.switchLevel(this.level, level);
            setLevel(level);

            if(level != null) {
                level.setPlayer(this);
                level.setActor(lactor);
            }
        }

        else
            setLevel(level);

        spendEnergy(150);

        level.getDungeon().getGame().getPlayScreen().getUi().initMinimap();
        clear_extra_sight();
        update_Extra_vision();
        requestVisionUpdate = true;
        level.getTurn().act(level);
    }

    public Point getCurrentDestination() {
        return currentDestination;
    }

    public void setCurrentDestination(Point currentDestination) {
        this.currentDestination = currentDestination;
    }

    public ArrayDeque<Point> getDestinationQueue() {
        return destinationQueue;
    }

    public void setDestinationQueue(ArrayDeque<Point> destinationQueue) {
        this.destinationQueue = destinationQueue;
    }

    public void enqueueDestination(Point p) {
        destinationQueue.add(p);
    }

    public Point dequeueDestination() {
        Point p = destinationQueue.getFirst();
        destinationQueue.removeFirst();
        return p;
    }

    public Item getToThrow() {
        return toThrow;
    }

    public void setToThrow(Item toThrow) {
        this.toThrow = toThrow;
    }

    public Cursor getCursor() {
        return cursor;
    }

    public void setCursor(Cursor cursor) {
        this.cursor = cursor;
    }

    public Spell getToCast() {
        return toCast;
    }

    public void setToCast(Spell toCast) {
        this.toCast = toCast;
    }

    public Creature getTalkingTo() {
        return talkingTo;
    }

    public void setTalkingTo(Creature talkingTo) {
        this.talkingTo = talkingTo;
    }

    public boolean canSeeEverything() {
        return canSeeEverything;
    }

    public void setCanSeeEverything(boolean canSeeEverything) {
        this.canSeeEverything = canSeeEverything;
    }

    //</editor-fold>

    /**
     * Move the player by <mx, my>
     * @param mx X distance
     * @param my Y distance
     */
    public void moveBy(int mx, int my) {

        if(cursor.isActive()) moveCursorBy(mx, my);
        else movePlayerBy(mx, my);

    }

    public void movePlayerBy(int mx, int my) {
        moveTo(x + mx, y + my);

    }

    public void moveCursorBy(int mx, int my) {
        int x = cursor.getX();
        int y = cursor.getY();

        if(x + mx < 0 || x + mx >= level.getWidth() || y + my < 0 || y + my >= level.getHeight()) return;

        cursor.moveBy(mx, my);

        if(cursor.isHasLine() && cursor.hasPath()) {
            cursor.setPath(Utility
                    .aStarPathToLine(Utility
                            .aStarWithVision(level.getCosts(), this, Point.DISTANCE_MANHATTAN, getLocation(), cursor)).getPoints());
        }

        lastActTime = System.currentTimeMillis();
        level.getDungeon().getGame().getPlayScreen().getUi().setRequestMinimapUpdate(true);
    }

    public void throwItem() {
        super.throwItem(toThrow, cursor);
    }

    /*
     * The following methods are for setting up the player's cursor
     */
    public void prepThrow(Item i) {
        setToThrow(i);
        cursor.setPurpose("throw");
        cursor.setLocation(x, y);
        cursor.setActive(true);
        cursor.clearPath();
        cursor.setHasLine(true);
        cursor.setMustSee(false);
        cursor.setConsiderObstacle(true);
        getCursor().setConsiderOneCreature(false);
        cursor.setRange(getThrowRange());
        cursor.setHasArea(false);

        cursor.setPositive(0);
        cursor.setNegative(1);
        cursor.setNeutral(2);
    }

    public void prepShoot() {
        if(rangedWeapon == null || quiver == null) return;
        cursor.setPurpose("shoot");
        cursor.setLocation(x, y);
        cursor.setActive(true);
        cursor.clearPath();
        cursor.setHasLine(true);
        cursor.setMustSee(false);
        cursor.setConsiderObstacle(true);
        getCursor().setConsiderOneCreature(true);
        cursor.setRange(rangedWeapon.getI().getRangedComponent().getRange());
        cursor.setHasArea(false);

        cursor.setPositive(0);
        cursor.setNegative(1);
        cursor.setNeutral(2);
    }

    public void prepAttack() {
        cursor.setPurpose("attack");
        cursor.setLocation(x, y);
        cursor.setActive(true);
        cursor.clearPath();
        cursor.setHasLine(true);
        cursor.setMustSee(false);
        cursor.setConsiderObstacle(true);
        getCursor().setConsiderOneCreature(true);
        cursor.setRange(getMainHand() == null? 1 : getMainHand().getMeleeComponent().getRange());
        cursor.setHasArea(false);

        cursor.setPositive(0);
        cursor.setNegative(1);
        cursor.setNeutral(2);
    }

    public void prepCast(Spell s) {
        if(s instanceof PointSpell) {

            cursor.setPurpose("zap");
            cursor.setLocation(x, y);
            cursor.setActive(true);
            cursor.clearPath();
            cursor.setHasLine(true);
            cursor.setMustSee(true);
            cursor.setConsiderObstacle(!s.isIgnoreObstacle());
            getCursor().setConsiderOneCreature(false);
            cursor.setRange(((PointSpell)s).getRange());

            cursor.setPositive(0);
            cursor.setNegative(1);
            cursor.setNeutral(2);
            cursor.setHasArea(false);

            if(s instanceof LineSpell) {
                cursor.setNeutral(0);
                getCursor().setConsiderOneCreature(((LineSpell) s).isEffectOne());
            }
            if(s instanceof AOESpell) cursor.setRadius(((AOESpell) s).getRadius());

            toCast = s;
        }
    }

    public void prepLook() {
        getCursor().setPurpose("look");
        getCursor().setFollow(true);
        getCursor().setHasRange(false);
        getCursor().setHasLine(false);
        getCursor().setMustSee(true);
        getCursor().setConsiderObstacle(false);
        getCursor().setConsiderOneCreature(false);
        getCursor().setHasArea(false);
        getCursor().setLocation(getX(), getY());
        getCursor().setPositive(0);
        getCursor().setNegative(1);
        getCursor().setNeutral(2);
        getCursor().setActive(true);
    }

    public void prepMove() {
        getCursor().setPurpose("move");
        getCursor().setFollow(true);
        getCursor().setHasRange(false);
        getCursor().clearPath();
        getCursor().setHasLine(true);
        getCursor().setMustSee(true);
        getCursor().setConsiderObstacle(true);
        getCursor().setConsiderOneCreature(false);
        getCursor().setHasArea(false);
        getCursor().setLocation(getX(), getY());
        getCursor().setPositive(0);
        getCursor().setNegative(1);
        getCursor().setNeutral(2);
        getCursor().setPath(Utility.aStarPathToLine(Utility.aStarWithVision(getLevel().getCosts(), this,
                Point.DISTANCE_MANHATTAN, getLocation(), getCursor())).getPoints());
        getCursor().setActive(true);
    }

    public void performCursorAction() {
        switch (cursor.getPurpose().toLowerCase()) {
            case "shoot": {
                shoot();
                break;
            }
            case "throw": {
                throwItem();
                break;
            }
            case "attack": {
                attack(cursor);
                break;
            }
            case "interact": {
                if(level.getThingAt(cursor.getX(), cursor.getY()).getBehavior() != null) {
                    level.getThingAt(cursor.getX(), cursor.getY()).interact(this);
                }
                break;
            }
            case "zap": {
                cast();
                break;
            }
            case "look":
            case "move": {
                enqueueDestination(cursor);
                break;
            }
        }

        deactivateCursor();
    }

    public void deactivateCursor() {
        getCursor().setPurpose("");
        getCursor().setActive(false);
    }

    public void shoot() {
        super.shootRangedWeapon(cursor);
    }

    /**
     * Creature should do its turn
     */
    @Override
    public void update() {
        super.update();

        if(level.getCreatureAt(x, y) == null) {
            level.addAt(x, y, this);
        }
    }

    @Override
    public void act(Level l) {
        boolean changed = update_Extra_vision();
        if(changed)
            requestVisionUpdate = true;
        allowed_to_act = true;
        ai.onAct();
    }

    @Override
    public void process(Level l) {
        allowed_to_act = false;
        level.advance(this);
    }

    public void cast() {
        super.cast(toCast, cursor);
        toCast = null;
    }

    @Override
    public void eat(Item f) {
        if(f == null || !f.isComsumable()) return;

        String hungerOriginal = hungerToString();

        doAction("eat %s.", f.getName());
        f.assignCaster(this);

        modifyHunger(f.getConsumableComponent().getSatiation());

        if(!hungerOriginal.equals(hungerToString()) && !(hungerToString().equals("Starving") || hungerToString().equals("Hungry")))
            doAction("feel %s.", hungerToString());

        inventory.removeOne(f);
        spendEnergy(100);
    }

    @Override
    public void modifyExp(int exp) {
        if(exp > 0) doAction("gain %d experience.", exp);
        else if(exp < 0) doAction("lose %d experience.", exp);
        setExp(this.exp + exp);
    }

    @Override
    public void modifyExp(int exp, boolean notify) {
        if(notify) {
            if(exp > 0) doAction("gain %d experience.", exp);
            else if(exp < 0) doAction("lose %d experience.", exp);
        }
        setExp(this.exp + exp, notify);
    }

    public void talkTo(Creature c) {
        if(!(c.getAi() instanceof NPCAi)) return;
        talkingTo = c;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Player)) return false;
        if (!super.equals(o)) return false;
        Player player = (Player) o;
        return isDead == player.isDead &&
                (ai instanceof PlayerAi? player.getAi() instanceof PlayerAi : true);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), ai, isDead);
    }
}
