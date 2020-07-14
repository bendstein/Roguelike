package creatureitem;

import creatureitem.ai.PlayerAi;
import creatureitem.item.*;
import world.Level;
import world.World;
import world.geometry.Cursor;
import world.geometry.Line;
import world.geometry.Point;

import java.util.ArrayDeque;

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
     * Cursor pointing at a tile on the map
     */
    private Cursor cursor;

    private int turnsToProcess;

    //</editor-fold>

    public Player(int maxHP, int hungerMax, int exp, int strength, int agility, int constitution, int perception,
                  Level level, String texturePath, String name, char glyph, int team, Weapon unarmedAttack, int natArmor) {
        super(maxHP, hungerMax, exp, strength, agility, constitution, perception,
        level, texturePath, name, glyph, team, unarmedAttack, natArmor);

        isDead = false;
        currentDestination = null;
        destinationQueue = new ArrayDeque<>();
        toThrow = null;
        cursor = new Cursor(0, 0);
        turnsToProcess = 0;
    }

    public Player(Player player) {
        super(player);
        isDead = player.isDead;
        currentDestination = player.currentDestination;
        currentDestination = player.currentDestination;
        destinationQueue = player.destinationQueue;
        toThrow = player.toThrow;
        cursor = player.cursor;
        turnsToProcess = 0;
    }

    //<editor-fold desc="Getters and Setters">
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

    public void setSeen(int i, int j) {
        level.setSeen(i, j);
    }

    public boolean getSeen(int i, int j) {
        return level.getSeen(i, j);
    }

    @Override
    public void setLevel(Level level) {
        super.setLevel(level);
        if(level != null) level.setPlayer(this);
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

    public int getTurnsToProcess() {
        return turnsToProcess;
    }

    public void setTurnsToProcess(int turnsToProcess) {
        this.turnsToProcess = turnsToProcess;
    }

    public void increaseTurnsToProcess(int i) {
        this.turnsToProcess += i;
    }

    //</editor-fold>

    /**
     * Move the player by <mx, my>
     * @param mx X distance
     * @param my Y distance
     */
    public void moveBy(int mx, int my) {

        if(cursor.isActive()) moveCursorBy(mx, my);

        else {
            if(x + mx < 0 || x + mx >= level.getWidth() || y + my < 0 || y + my >= level.getHeight()) return;

            Creature foe = level.getCreatureAt(x + mx, y + my);
            if(foe == null)
                ai.onEnter(x + mx, y + my, level.getTileAt(x + mx, y + my));
            else if(foe.team != team)
                attack(foe);
            level.update();
            lastMovedTime = System.currentTimeMillis();
        }

    }

    public void moveCursorBy(int mx, int my) {
        int x = cursor.getX();
        int y = cursor.getY();

        if(x + mx < 0 || x + mx >= level.getWidth() || y + my < 0 || y + my >= level.getHeight()) return;

        cursor.setX(x + mx);
        cursor.setY(y + my);
        lastMovedTime = System.currentTimeMillis();

    }

    public void throwItem() {
        super.throwItem(toThrow, cursor);
        level.update();
    }

    public void prepThrow(Item i) {
        setToThrow(i);
        cursor.setPurpose("throw");
        cursor.setLocation(x, y);
        cursor.setActive(true);
        cursor.setHasLine(true);
        cursor.setConsiderObstacle(true);
        cursor.setRange(getThrowRange());
    }

    public void prepShoot() {
        if(rangedWeapon == null || quiver == null) return;
        cursor.setPurpose("shoot");
        cursor.setLocation(x, y);
        cursor.setActive(true);
        cursor.setHasLine(true);
        cursor.setConsiderObstacle(true);
        cursor.setRange(rangedWeapon.getRange());
    }

    public void shoot() {
        super.shootRangedWeapon(cursor);
        level.update();
    }

    public void processTurns() {
        long time = System.currentTimeMillis();
        while(turnsToProcess > 0) {
            if(System.currentTimeMillis() - time >= 100) {
                level.update();
                turnsToProcess--;
            }
        }
    }

    @Override
    public void eat(Food f) {
        String hungerOriginal = hungerToString();
        modifyHunger(f.getFoodValue());
        doAction("eat %s.", f.getName());

        if(!hungerOriginal.equals(hungerToString()) && !(hungerToString().equals("Starving") || hungerToString().equals("Hungry")))
            doAction("feel %s.", hungerToString());

        inventory.removeOne(f);
        increaseTurnsToProcess(1);
    }

    @Override
    public boolean equip(Item i) {
        if(!(i instanceof Equipable)) return false;

        if(i instanceof Weapon && i.hasProperty("main hand")) {
            unequip(mainHand);
            mainHand = (Weapon) i;
            ((Weapon)i).setEquipped(true);
            doAction("equip %s.", i.toString());
            return true;
        }

        else if(i instanceof RangedWeapon) {
            unequip(rangedWeapon);
            rangedWeapon = (RangedWeapon) i;
            ((RangedWeapon)i).setEquipped(true);
            doAction("equip %s.", i.toString());

            if(quiver != null && !quiver.hasProperty(rangedWeapon.getAmmoType()))
                unequip(quiver);

            return true;
        }

        else if(i instanceof Ammo && (rangedWeapon == null || i.hasProperty(rangedWeapon.getAmmoType()))) {
            unequip(quiver);
            quiver = (Ammo) i;
            ((Ammo)i).setEquipped(true);
            doAction("quiver %s.", i.toString());
            return true;
        }

        else if(i instanceof Armor && i.hasProperty("body")) {
            unequip(body);
            body = (Armor)i;
            ((Armor)i).setEquipped(true);
            doAction("equip %s.", i.toString());
            increaseTurnsToProcess(1);
            return true;
        }

        return false;
    }

    @Override
    public boolean equip(Item i, boolean notify) {
        if(!(i instanceof Equipable)) return false;

        if(i instanceof Weapon && i.hasProperty("main hand")) {
            unequip(mainHand);
            mainHand = (Weapon) i;
            ((Weapon)i).setEquipped(true);
            if(notify) doAction("equip %s.", i.toString());
            return true;
        }

        else if(i instanceof RangedWeapon) {
            unequip(rangedWeapon);
            rangedWeapon = (RangedWeapon) i;
            ((RangedWeapon)i).setEquipped(true);
            if(notify) doAction("equip %s.", i.toString());

            if(quiver != null && !quiver.hasProperty(rangedWeapon.getAmmoType()))
                unequip(quiver);

            return true;
        }

        else if(i instanceof Ammo && (rangedWeapon == null || i.hasProperty(rangedWeapon.getAmmoType()))) {
            unequip(quiver);
            quiver = (Ammo) i;
            ((Ammo)i).setEquipped(true);
            if(notify) doAction("quiver %s.", i.toString());
            return true;
        }

        else if(i instanceof Armor && i.hasProperty("body")) {
            unequip(body);
            body = (Armor)i;
            ((Armor)i).setEquipped(true);
            if(notify) doAction("equip %s.", i.toString());
            increaseTurnsToProcess(1);
            return true;
        }

        return false;
    }

    @Override
    public boolean unequip(Item i) {
        if(!(i instanceof Equipable) || (!isEquipped(i) && !((Equipable)i).isEquipped())) return true;

        ((Equipable)i).setEquipped(false);

        if(mainHand != null && mainHand.equals(i)) {
            mainHand = null;
            doAction("unequip %s.", i.toString());
            return true;
        }

        else if(rangedWeapon != null && rangedWeapon.equals(i)) {
            rangedWeapon = null;
            doAction("unequip %s.", i.toString());
            return true;
        }

        else if(quiver != null && quiver.equals(i)) {
            quiver = null;
            doAction("unequip %s.", i.toString());
            return true;
        }

        else if(body != null && body.equals(i)) {
            body = null;
            doAction("unequip %s.", i.toString());
            increaseTurnsToProcess(1);
            return true;
        }


        return false;

    }

    @Override
    public boolean unequip(Item i, boolean notify) {
        if(!(i instanceof Equipable) || (!isEquipped(i) && !((Equipable)i).isEquipped())) return true;

        ((Equipable)i).setEquipped(false);

        if(mainHand != null && mainHand.equals(i)) {
            mainHand = null;
            if(notify) doAction("unequip %s.", i.toString());
            return true;
        }

        else if(rangedWeapon != null && rangedWeapon.equals(i)) {
            rangedWeapon = null;
            if(notify) doAction("unequip %s.", i.toString());
            return true;
        }

        else if(quiver != null && quiver.equals(i)) {
            quiver = null;
            if(notify) doAction("unequip %s.", i.toString());
            return true;
        }

        else if(body != null && body.equals(i)) {
            body = null;
            if(notify) doAction("unequip %s.", i.toString());
            increaseTurnsToProcess(1);
            return true;
        }


        return false;

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
}
