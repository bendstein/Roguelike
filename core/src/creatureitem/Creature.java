package creatureitem;

import actors.creatures.CreatureActor;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import creatureitem.ai.CreatureAi;
import creatureitem.effect.Effect;
import creatureitem.item.*;
import creatureitem.spell.Spell;
import utility.Utility;
import world.Level;
import world.geometry.Cursor;
import world.geometry.Line;
import world.geometry.Point;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

public class Creature {

    //<editor-fold desc="Instance Variables">

    /*
     * Stats
     */

    /**
     * The creature's current HP
     */
    protected int hp;

    /**
     * The creature's max HP
     */
    protected int hpMax;


    /**
     * Measure of how satiated the player is
     */
    protected int hunger;

    /**
     * Max Satiation
     */
    protected int hungerMax;


    /**
     * Measure of the creature's magic power
     */
    protected int mana;

    /**
     * Max mana
     */
    protected int manaMax;


    /**
     * The amount of experience this creature has
     */
    protected int exp;

    /**
     * The level of the creature, based off of its experience
     */
    protected int expLevel;


    /**
     * Measure of the creature's strength
     */
    protected int strength;

    /**
     * Measure of the creature's agility
     */
    protected int agility;

    /**
     * Measure of the creature's constitution
     */
    protected int constitution;

    /**
     * Measure of the creature's perception
     */
    protected int perception;

    /**
     * Measure of the creature's intelligence
     */
    protected int intelligence;


    /**
     * Bonus to damage, separate from strength bonus
     */
    protected int damageBonus;

    /**
     * Bonus to magic damage, separate from intelligence bonus
     */
    protected int magicBonus;

    /**
     * Bonus to defense, separate from natural armor
     */
    protected int defenseBonus;


    /*
     * Movement/Location
     */

    /**
     * The creature's x coordinate
     */
    protected int x;

    /**
     * The creature's y coordinate
     */
    protected int y;

    protected double rarity;

    public static final double SUPER_COMMON = 0d, COMMON = 1d, AVERAGE = 2d, UNCOMMON = 3d, RARE = 4d, LEGENDARY = 5d;

    /**
     * Reference to the level the creature is in
     */
    protected Level level;


    /*
     * Other
     */

    /**
     * The texture representing the creature
     */
    protected Texture texture;

    /**
     * The name of the creature
     */
    protected String name;

    /**
     * The character representing this creature
     */
    protected char glyph;

    /**
     * The behaviors the creature follows
     */
    protected creatureitem.ai.CreatureAi ai;

    /**
     * Creatures are hostile to creatures on different teams
     */
    protected int team;

    /**
     * Actor associated with this creature
     */
    protected Actor actor;

    /**
     * The system time last time this creature moved
     */
    protected long lastMovedTime;

    /**
     * Direction the creature is moving, based off of the numpad. If 0, not moving
     */
    protected int moveDirection;

    /*
     * Equipment
     */

    /**
     * A measure of a creature's ability to deal damage when unequipped
     */
    protected Weapon unarmedAttack;

    /**
     * The creature's natural armor
     */
    protected Armor natArmor;


    /**
     * A list of the creature's items
     */
    protected Inventory inventory;


    /**
     * The weapon in the creature's main hand
     */
    protected Weapon mainHand;

    /**
     * The ranged weapon that the creature is holding
     */
    protected RangedWeapon rangedWeapon;

    /**
     * The ammo the creature has in its quiver
     */
    protected Ammo quiver;

    /**
     * The armor the creature has on its body
     */
    protected Armor body;

    /**
     * Spells that the creature knows
     */
    protected ArrayList<Spell> spells;

    /**
     * A list of all effects currently active on the creature
     */
    protected ArrayList<Effect> activeEffects;

    //</editor-fold>

    public Creature(int hpMax, int hungerMax, int manaMax, int exp, int strength, int agility, int constitution, int perception, int intelligence,
                    Level level, String texturePath, String name, char glyph, int team, Weapon unarmedAttack, int natArmor) {

        this.hp = this.hpMax = hpMax + getAttributeBonus(constitution);
        this.hunger = this.hungerMax = hungerMax;
        this.manaMax = this.mana = manaMax;
        this.exp = exp;
        this.expLevel = 1;

        this.strength = strength;
        this.agility = agility;
        this.constitution = constitution;
        this.perception = perception;
        this.intelligence = intelligence;

        this.damageBonus = 0;
        this.magicBonus = 0;
        this.defenseBonus = 0;

        this.level = level;

        this.texture = new Texture(Gdx.files.internal(texturePath));

        this.name = name;
        this.glyph = glyph;

        this.team = team;
        this.rarity = 0d;

        this.inventory = new Inventory();

        this.unarmedAttack = unarmedAttack;
        this.natArmor = new Armor(natArmor);

        this.spells = new ArrayList<>();
        this.activeEffects = new ArrayList<>();

    }

    public Creature(Creature creature) {
        this.hp = this.hpMax = creature.hpMax;
        this.hunger = this.hungerMax = creature.hungerMax;
        this.mana = this.manaMax = creature.manaMax;

        this.exp = creature.exp;
        this.expLevel = 1;

        this.strength = creature.strength;
        this.agility = creature.agility;
        this.constitution = creature.constitution;
        this.perception = creature.perception;
        this.intelligence = creature.intelligence;

        this.damageBonus = creature.damageBonus;
        this.magicBonus = creature.magicBonus;
        this.defenseBonus = creature.defenseBonus;

        this.x = creature.x;
        this.y = creature.y;

        this.level = creature.level;
        this.rarity = creature.rarity;

        this.texture = new Texture(creature.getTexture().getTextureData());

        this.name = creature.name;
        this.glyph = creature.glyph;

        this.ai = creature.ai == null? null : creature.ai.copy();
        if(this.ai != null) this.ai.setCreature(this);

        this.team = creature.team;

        this.actor = creature.actor == null? null : ((CreatureActor)creature.actor).copy();
        if(this.actor != null) ((CreatureActor)this.actor).setCreature(this);

        this.lastMovedTime = 0L;
        this.moveDirection = 0;

        this.unarmedAttack = creature.unarmedAttack;
        this.natArmor = new Armor(creature.getNatArmor());

        this.inventory = new Inventory(creature.inventory);

        this.mainHand = creature.mainHand;
        this.rangedWeapon = creature.rangedWeapon;
        this.quiver = creature.quiver;
        this.body = creature.body;

        this.spells = new ArrayList<>(creature.spells);
        this.activeEffects = new ArrayList<>(creature.activeEffects);
    }

    //<editor-fold desc="Getters and Setters">
    public Level getLevel() {
        return level;
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

    public CreatureAi getAi() {
        return ai;
    }

    public void setAi(CreatureAi ai) {
        this.ai = ai;
        if(ai != null) ai.setCreature(this);
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

    public char getGlyph() {
        return glyph;
    }

    public void setGlyph(char glyph) {
        this.glyph = glyph;
    }

    public int getTeam() {
        return team;
    }

    public void setTeam(int team) {
        this.team = team;
    }

    public int getHpMax() {
        return hpMax;
    }

    public void setHpMax(int hpMax) {
        this.hpMax = hpMax;
    }

    public int getHp() {
        return hp;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }

    public int getEvasion() {
        return getAttributeBonus(agility);
    }

    public int getArmor() {
        int total = natArmor.getArmor() + defenseBonus;

        total += body == null? 0 : body.getArmor();

        return total;
    }

    public Weapon getUnarmedAttack() {
        return unarmedAttack;
    }

    public void setUnarmedAttack(Weapon unarmedAttack) {
        this.unarmedAttack = unarmedAttack;
    }

    public int getVisionRadius() {
        return Math.max(1, 4 + getAttributeBonus(perception));
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

    public void setActor(CreatureActor actor) {
        this.actor = actor;
        if(actor != null) actor.setCreature(this);
    }

    public Inventory getInventory() {
        return inventory;
    }

    public void setLevel(Level level) {
        this.level = level;
    }

    public Point getLocation() {
        return new Point(x, y);
    }

    public int getHunger() {
        return hunger;
    }

    public void setHunger(int hunger) {
        this.hunger = hunger;
    }

    public int getHungerMax() {
        return hungerMax;
    }

    public void setHungerMax(int hungerMax) {
        this.hungerMax = hungerMax;
    }

    public Weapon getMainHand() {
        return mainHand;
    }

    public void setMainHand(Weapon mainHand) {
        this.mainHand = mainHand;
    }

    public RangedWeapon getRangedWeapon() {
        return rangedWeapon;
    }

    public void setRangedWeapon(RangedWeapon rangedWeapon) {
        this.rangedWeapon = rangedWeapon;
    }

    public Ammo getQuiver() {
        return quiver;
    }

    public void setQuiver(Ammo quiver) {
        this.quiver = quiver;
    }

    public Armor getNatArmor() {
        return natArmor;
    }

    public void setNatArmor(Armor natArmor) {
        this.natArmor = natArmor;
    }

    public Armor getBody() {
        return body;
    }

    public void setBody(Armor body) {
        this.body = body;
    }

    public int getExp() {
        return exp;
    }

    public void setExp(int exp) {
        this.exp = exp;
        int oldlvl = expLevel;
        int lvl = ai.calculateLevel();
        this.expLevel = ai.calculateLevel();

        if(lvl > oldlvl)
            doAction("advance to level %d!", lvl);

        else if(lvl < oldlvl)
            doAction("lose experience, and regress to level %d?!", lvl);

        ai.gainLevels(oldlvl);
    }

    public void setExp(int exp, boolean notify) {
        this.exp = exp;
        int oldlvl = expLevel;
        int lvl = ai.calculateLevel();
        this.expLevel = ai.calculateLevel();

        if(notify) {
            if(lvl > oldlvl)
                doAction("advance to level %d!", lvl);

            else if(lvl < oldlvl)
                doAction("lose experience, and regress to level %d?!", lvl);
        }

        ai.gainLevels(oldlvl);
    }

    public int getExpLevel() {
        return expLevel;
    }

    public int getHungerRate() {
        return 25/(getAttributeBonus(constitution) == 0? 1 : getAttributeBonus(constitution));
    }

    public int getRegenRate() {
        return 40/(getAttributeBonus(constitution) == 0? 1 : getAttributeBonus(constitution));
    }

    public int getManaRegenRate() {
        return 20/(getAttributeBonus(intelligence) == 0? 1 : getAttributeBonus(intelligence));
    }

    public int getStrength() {
        return strength;
    }

    public void setStrength(int strength) {
        this.strength = strength;
    }

    public int getAgility() {
        return agility;
    }

    public void setAgility(int agility) {
        this.agility = agility;
    }

    public int getConstitution() {
        return constitution;
    }

    public void setConstitution(int constitution) {
        this.constitution = constitution;
    }

    public int getPerception() {
        return perception;
    }

    public void setPerception(int perception) {
        this.perception = perception;
    }

    public int getAttributeBonus(int attribute) {
        return Math.max(((attribute % 2 == 0)? (attribute - 10)/2 : (attribute - 11)/2), -5);
    }

    public ArrayList<Effect> getActiveEffects() {
        return activeEffects;
    }

    public void setActiveEffects(ArrayList<Effect> activeEffects) {
        this.activeEffects = activeEffects;
    }

    public boolean hasEffect(Effect effect) {
        return activeEffects.contains(effect);
    }

    public void removeEffect(Effect effect) {
        activeEffects.remove(effect);
    }

    public int getDamageBonus() {
        return damageBonus;
    }

    public void setDamageBonus(int damageBonus) {
        this.damageBonus = damageBonus;
    }

    public int getDefenseBonus() {
        return defenseBonus;
    }

    public void setDefenseBonus(int defenseBonus) {
        this.defenseBonus = defenseBonus;
    }

    public int getMana() {
        return mana;
    }

    public void setMana(int mana) {
        this.mana = mana;
    }

    public int getManaMax() {
        return manaMax;
    }

    public void setManaMax(int manaMax) {
        this.manaMax = manaMax;
    }

    public int getIntelligence() {
        return intelligence;
    }

    public void setIntelligence(int intelligence) {
        this.intelligence = intelligence;
    }

    public int getMagicBonus() {
        return magicBonus;
    }

    public void setMagicBonus(int magicBonus) {
        this.magicBonus = magicBonus;
    }

    public ArrayList<Spell> getSpells() {
        return spells;
    }

    public void setSpells(ArrayList<Spell> spells) {
        this.spells = spells;
    }

    public double getRarity() {
        return rarity;
    }

    public void setRarity(double rarity) {
        this.rarity = rarity;
    }

    //</editor-fold>

    public void modifyExp(int exp) {
        setExp(this.exp + exp);
    }

    public void modifyExp(int exp, boolean notify) {
        setExp(this.exp + exp, notify);
    }

    /**
     * Change the creature's current HP
     * @param mod Amount to change HP by
     * @return true if the creature died
     */
    public boolean modifyHP(int mod) {
        if(hp + mod <= 0) {
            die();
            return true;
        }
        else {
            hp = Math.min(hpMax, hp + mod);
            return false;
        }
    }

    public boolean modifyHP(int mod, boolean notifyOnDie) {
        if(hp + mod <= 0) {
            if(notifyOnDie) doAction("collapse to the floor.");
            die();
            return true;
        }
        else {
            hp = Math.min(hpMax, hp + mod);
            return false;
        }
    }

    /**
     * Change the creature's max HP
     * @param mod The amount to change maxHP by
     * @return true if the creature died
     */
    public boolean modifyMaxHp(int mod) {
        if(hpMax + mod <= 0) {
            die();
            return true;
        }
        else {
            hpMax += mod;
            return false;
        }
    }

    public void modifyHunger(int mod) {
        String hungerOriginal = hungerToString();
        int hungerTotal = hunger + mod;
        hunger = Math.max(Math.min(hungerTotal, hungerMax), 0);
        if(!hungerToString().equals(hungerOriginal) && (hungerToString().equals("Starving") || hungerToString().equals("Hungry")))
            doAction("feel %s.", hungerToString());

        if(hungerTotal > hungerMax) {
            hungerMax += hungerTotal/2;
            doAction("look a little heavier!");
        }

        if(hunger < 1 && getLevel().getTurn() % 5 == 0)
            modifyHP(-1);
    }

    public void modifyMaxHunger(int mod) {
        hungerMax = Math.max(hungerMax, 0);
    }

    public void modifyMana(int mod) {
        mana = Math.max(0, Math.min(mana + mod, manaMax));
    }

    public void modifyManaMax(int mod) {
        manaMax = Math.max(0, manaMax + mod);
    }

    /**
     * Move the creature through a wall by (wx, wy)
     * @param wx X distance
     * @param wy Y Distance
     */
    public void dig(int wx, int wy) {
        modifyHunger(-10);
        doAction("dig out %s.", level.getTileAt(wx, wy).getName());
        level.dig(wx, wy);
    }

    public boolean toHit(Item w, Creature c) {

        int toHit = (w instanceof Weapon)? ((Weapon)w).getToHitMod()  + getAttributeBonus(agility) + level.getRandom().nextInt(20) :
                getAttributeBonus(agility) + level.getRandom().nextInt(20);
        int toDodge = c.getEvasion() + level.getRandom().nextInt(20);

        return toHit > toDodge;
    }

    /**
     * Attack the foe creature
     * @param foe The creature to attack
     */
    public void attack(Creature foe) {
        int damage;
        boolean miss = toHit(foe.getMainHand(), foe);

        if(miss) {
            doAction("attack %s.", foe.name);
            foe.doAction("dodge the attack!");
            return;
        }

        if(mainHand == null) damage = unarmedAttack.getWeaponDamage().getDamage(getLevel().getRandom()) +
                getAttributeBonus(strength) + damageBonus;
        else damage = mainHand.getWeaponDamage().getDamage(getLevel().getRandom()) + getAttributeBonus(strength) + damageBonus;

        damage = Math.max(0, damage - foe.getArmor());
        damage *= -1;

        boolean died = foe.modifyHP(damage);
        doAction("attack %s for %d damage.", foe.name, Math.abs(damage));

        if(died) {
            doAction("kill %s.", foe.name);
            modifyExp(foe.getExp());
        }

    }

    /**
     * Broadcast a message that you did something
     * @param message The message to send
     * @param params Formatting
     */
    public void doAction(String message, Object ... params) {
        if(level == null) return;
        int r = 9;

        for(int ox = -r; ox < r + 1; ox++) {
            for(int oy = -r; oy < r + 1; oy++) {
                //if(Math.pow(ox, 2) + Math.pow(oy, 2) > Math.pow(r, 2))
                if(Math.abs(ox) + Math.abs(oy) > r)
                    continue;

                Creature other = level.getCreatureAt(x + ox, y + oy);

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
        if(x + mx < 0 || x + mx >= level.getWidth() || y + my < 0 || y + my >= level.getHeight()) return;

        Creature foe = level.getCreatureAt(x + mx, y + my);
        if(foe == null)
            ai.onEnter(x + mx, y + my, level.getTileAt(x + mx, y + my));
        else if(foe.team != team)
            attack(foe);
        lastMovedTime = System.currentTimeMillis();
    }

    public void moveTo(Point p) {
        if(p.getX() < 0 || p.getX() >= level.getWidth() || p.getY() < 0 || p.getY() >= level.getHeight()) return;

        Creature foe = level.getCreatureAt(p.getX(), p.getY());
        if(foe == null)
            ai.onEnter(p.getX(), p.getY(), level.getTileAt(p.getX(), p.getY()));
        else if(foe.team != team)
            attack(foe);
        lastMovedTime = System.currentTimeMillis();
    }

    public void moveTowards(Point p) {
        int mx, my;
        mx = Integer.compare(p.getX(), x);
        my = Integer.compare(p.getY(), y);
        moveBy(mx, my);
    }

    /**
     * @param x X coord
     * @param y Y coord
     * @return true if a creature can enter this tile
     */
    public static boolean canEnter(int x, int y, Level level) {
        return (x >= 0 && y >= 0 && x < level.getWidth() && y < level.getHeight() && /*level.getTileAt(x, y).isGround()*/ level.isPassable(x, y) &&
                level.getCreatureAt(x, y) == null && !level.queuedCreatureAt(x, y));
    }

    /**
     * @param x X coord
     * @param y Y coord
     * @return true if the creature can see the tile at (x, y)
     */
    public boolean canSee(int x, int y) {
        return ai.canSee(x, y);
    }

    public boolean canSee(Point p) {
        return ai.canSee(p.getX(), p.getY());
    }

    /**
     * Creature should do its turn
     */
    public void update() {
        ai.onUpdate();

        ArrayList<Effect> toRemove = new ArrayList<>();
        for(Effect effect : activeEffects) {
            if(!effect.isDone()) {
                effect.affect(this);
                effect.changeRemainingDurationBy(-1);
            }
            else {
                effect.done(this);
                toRemove.add(effect);
            }
        }

        activeEffects.removeAll(toRemove);
    }

    /**
     * Received a message
     */
    public void notify(String message, Object ... params) {
        ai.onNotify(String.format(message, params));
    }

    /**
     * Pick up an item off the ground and put it in your inventory
     */
    public void pickUp() {

        if(level.getItemAt(x, y) == null)
            doAction("grab at the ground.");
        else {
            doAction("pick up %s.", level.getItemAt(x, y).toString());
            if(level.getItemAt(x, y) instanceof Potion)
                ((Potion) level.getItemAt(x, y)).getEffect().setCaster(this);
            inventory.add(level.removeItem(x, y));
        }

    }

    public void pickUp(boolean notify) {
        if(level.getItemAt(x, y) == null)
            if(notify) doAction("grab at the ground.");
        else {
            if(notify) doAction("pick up %s.", level.getItemAt(x, y).toString());
            if(level.getItemAt(x, y) instanceof Potion)
                ((Potion) level.getItemAt(x, y)).getEffect().setCaster(this);
            inventory.add(level.removeItem(x, y));
        }

    }

    /**
     * Place an item from your inventory on the ground
     * @param item The item to drop
     */
    public void drop(Item item) {
        drop(item, true);
    }

    public void drop(Item item, boolean notify) {
        drop(item, notify, true);
    }

    public void drop(Item item, boolean notify, boolean remove) {
        if(remove) {
            if(isEquipped(item)) {
                boolean uneqipped = unequip(item, false);
                if(!uneqipped) return;
            }
            inventory.remove(item);
        }

        if(item == null) return;

        for(int i = 0; i <= 3; i++) {
            for(int j = 0; j <= 3; j++) {
                if(level.addAt(x + i, y + j, item)) {
                    if(notify) doAction("drop %s.", item.toString());
                    return;
                }
                else if(level.addAt(x + i, y - j, item)) {
                    if(notify) doAction("drop %s.", item.toString());
                    return;
                }
                else if(level.addAt(x - i, y + j, item)) {
                    if(notify) doAction("drop %s.", item.toString());
                    return;
                }
                else if(level.addAt(x - i, y - j, item)) {
                    if(notify) doAction("drop %s.", item.toString());
                    return;
                }
            }
        }

        if(notify) doAction("drop %s into the void.", item.toString());
    }

    public void drop(Item item, int x, int y) {
       drop(item, x, y, true);
    }

    public void drop(Item item, int x, int y, boolean notify) {
        drop(item, x, y, notify, true);
    }

    public void drop(Item item, int x, int y, boolean notify, boolean remove) {
        if(remove) {
            if(isEquipped(item)) {
                boolean uneqipped = unequip(item, false);
                if(!uneqipped) return;
            }
            inventory.remove(item);
        }

        for(int i = 0; i <= 3; i++) {
            for(int j = 0; j <= 3; j++) {
                if(level.addAt(x + i, y + j, item)) {
                    if(notify) notify("%s falls to the ground.", !item.toString().equals("")? Character.toUpperCase(item.toString().charAt(0)) + item.toString().substring(1) : item.toString());
                    return;
                }
                else if(level.addAt(x + i, y - j, item)) {
                    if(notify) notify("%s falls to the ground.", !item.toString().equals("")? Character.toUpperCase(item.toString().charAt(0)) + item.toString().substring(1) : item.toString());
                    return;
                }
                else if(level.addAt(x - i, y + j, item)) {
                    if(notify) notify("%s falls to the ground.", !item.toString().equals("")? Character.toUpperCase(item.toString().charAt(0)) + item.toString().substring(1) : item.toString());
                    return;
                }
                else if(level.addAt(x - i, y - j, item)) {
                    if(notify) notify("%s falls to the ground.", !item.toString().equals("")? Character.toUpperCase(item.toString().charAt(0)) + item.toString().substring(1) : item.toString());
                    return;
                }
            }
        }

        notify("%s falls into the void.", !item.toString().equals("")? Character.toUpperCase(item.toString().charAt(0)) + item.toString().substring(1) : item.toString());
    }

    public void throwItem(Item item, Cursor cursor) {
        boolean depleted = inventory.removeOne(item);
        Item i;

        if(item instanceof Potion) i = new Potion((Potion)item);
        else i = new Item(item);

        i.setCount(1);

        Line path = new Line(this.getX(), cursor.getX(), this.getY(), cursor.getY(), getThrowRange() + 1);

        Point current = getLocation();
        boolean damage = false;
        for(Point p : path) {
            if(p.equals(getLocation())) continue;
            if(!level.isPassable(p.getX(), p.getY())) break;
            current = p;

            if(level.getCreatureAt(current.getX(), current.getY()) != null &&
                    level.getCreatureAt(current.getX(), current.getY()).getTeam() != team &&
                    (i.hasProperty("throw") || i instanceof Potion)) {
                damage = true;
                break;
            }
        }

        if(damage) {
            if(i instanceof Potion) {
                if(toHit(i, level.getCreatureAt(current.getX(), current.getY()))) {
                    doAction("throw %s at %s.", i.getName(), level.getCreatureAt(current.getX(), current.getY()).name);
                    notify("It shatters on impact and applies its affect!");
                    level.getCreatureAt(current.getX(), current.getY()).drink(((Potion) i), false);
                }
                else {
                    doAction("throw %s at %s.", i.getName(), level.getCreatureAt(current.getX(), current.getY()).name);
                    notify("It misses, and breaks into pieces upon hitting the floor.");
                }

            }
            else throwAttack(i, level.getCreatureAt(current.getX(), current.getY()));
        }
        else {
            doAction("throw %s.", i.getName());
            if(i.hasProperty("shatter"))
                notify("It shatters on impact!");
            else {
                drop(i, current.getX(), current.getY(), false, false);
            }
        }

        if(depleted)
            unequip(item);

    }

    public boolean canShoot(Cursor cursor) {
        if(quiver == null || rangedWeapon == null) return false;

        Line path = new Line(this.getX(), cursor.getX(), this.getY(), cursor.getY(), rangedWeapon.getRange() + 1);

        //Point current = getLocation();
        boolean damage = false;
        for(Point p : path) {
            if(p.equals(getLocation())) continue;
            if(!level.isPassable(p.getX(), p.getY())) return false;
            //current = p;

            /*
            if(level.getCreatureAt(current.getX(), current.getY()) != null) {
                damage = true;
                break;
            }

             */
        }

        return true;
    }

    public void shootRangedWeapon(Cursor cursor) {
        if(!canShoot(cursor)) return;

        boolean depleted = inventory.removeOne(quiver);
        Ammo i = new Ammo(quiver);
        i.setCount(1);

        Line path = new Line(this.getX(), cursor.getX(), this.getY(), cursor.getY(), rangedWeapon.getRange() + 1);

        Point current = getLocation();
        boolean damage = false;
        for(Point p : path) {
            if(p.equals(getLocation())) continue;
            if(!level.isPassable(p.getX(), p.getY())) break;
            current = p;

            if(level.getCreatureAt(current.getX(), current.getY()) != null) {
                damage = true;
                break;
            }
        }

        boolean recover = false;
        if(damage) {
            rangedWeaponAttack(level.getCreatureAt(current.getX(), current.getY()));
            if(getLevel().getRandom().nextDouble() < 0.4) recover = true;
        }
        else {
            drop(i, current.getX(), current.getY(), false);
            doAction("shoot %s.", i.getName());
        }

        if(depleted)
            unequip(quiver, false);
        if(recover)
            drop(i, current.getX(), current.getY(), false, false);

    }

    public void throwAttack(Item i, Creature foe) {

        boolean miss = toHit(i, foe);

        if(miss) {
            doAction("throw %s at %s.", i.getName(), foe.name);
            if(foe.canSee(getLocation()))
                foe.doAction("dodge the attack!");
            else
                foe.notify("A %s flies out of the darkness and whizzes past your head!", i.getName());
            return;
        }

        int damage = -1 * Math.max(0, (i.getThrowDamage().getDamage(getLevel().getRandom()) + getAttributeBonus(strength)/2));
        boolean died = foe.modifyHP(damage);
        doAction("throw %s at %s for %d damage.", i.getName(), foe.name, Math.abs(damage));

        if(!foe.canSee(getLocation()))
            foe.notify("A %s flies out of the darkness and deals %d damage!", i.getName(), Math.abs(damage));

        if(died) {
            doAction("kill %s.", foe.name);
            modifyExp(foe.getExp());
        }
    }

    public void rangedWeaponAttack(Creature foe) {

        boolean miss = toHit(foe.getRangedWeapon(), foe);

        if(miss) {
            doAction("shoot %s at %s.", quiver.getName(), foe.name);

            if(foe.canSee(getLocation()))
                foe.doAction("dodge the attack!");
            else
                foe.notify("A %s flies out of the darkness and whizzes past your head!", quiver.getName());
            return;
        }

        int damage = -1 * Math.max(0, (rangedWeapon.getWeaponDamage().getDamage(getLevel().getRandom()) + quiver.getAmmoDamage().getDamage(getLevel().getRandom()) + getAttributeBonus(agility)));
        boolean died = foe.modifyHP(damage);
        doAction("shoot %s at %s for %d damage.", quiver.getName(), foe.name, Math.abs(damage));

        if(!foe.canSee(getLocation()))
            foe.notify("A %s flies out of the darkness and deals %d damage!", quiver.getName(), Math.abs(damage));

        if(died) {
            doAction("kill %s.", foe.name);
            modifyExp(foe.getExp());
        }
    }

    public int getThrowRange() {
        return Math.max(1, 2 + getAttributeBonus(strength));
    }

    public void setActor(Actor actor) {
        this.actor = actor;
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    public void die() {
        ai.onDie();
    }

    public void leaveCorpse() {
        Food corpse = new Food('%', "data/Corpse.png", String.format(Locale.getDefault(), "%s corpse", name), 1, hpMax * 3, "stack");
        level.addAt(x, y, corpse);

        for(Item i : inventory.getItems()) {
            if(i == null) continue;
            if(getLevel().getRandom().nextDouble() < .5) {
                if(i.hasProperty("stack")) {
                    int c = i.getCount();
                    c = getLevel().getRandom().nextInt(Math.min((c - (int)(c * 0.3d)), 1)) + (int)(c * 0.3d);
                    i.setCount(c);
                }
                drop(i, false);
            }
        }
    }

    public void eat(Food f) {
        inventory.removeOne(f);
        hunger = Math.min(hungerMax, hunger + f.getFoodValue());
        doAction("eat %s.", f.getName());
    }

    public void eat(Food f, boolean notify) {
        inventory.removeOne(f);
        hunger = Math.min(hungerMax, hunger + f.getFoodValue());
        if(notify) doAction("eat %s.", f.getName());
    }

    public void drink(Potion p) {
        inventory.removeOne(p);
        Potion pc = new Potion(p);
        pc.getEffect().setRemainingDuration(pc.getEffect().getDuration());
        applyEffect(pc.getEffect());
        doAction("drink %s.", pc.getName());
    }

    public void drink(Potion p, boolean notify) {
        inventory.removeOne(p);
        applyEffect(p.getEffect());
        if(notify) doAction("drink %s.", p.getName());
    }

    public String hungerToString() {
        if(hunger < hungerMax * .1)
            return "Starving";
        else if(hunger < hungerMax * .2)
            return "Hungry";
        else if(hunger == hungerMax)
            return "Stuffed";
        else if(hunger > hungerMax * .9)
            return "Full";
        else if(hunger < hungerMax * .8)
            return "Satisfied";
        else
            return "Satiated";
    }

    public boolean equip(Item i) {
        if(!(i instanceof Equipable)) return false;

        if(i instanceof Weapon && i.hasProperty("main hand")) {
            unequip(mainHand);
            mainHand = (Weapon) i;
            ((Weapon)i).setEquipped(true);
            return true;
        }

        else if(i instanceof RangedWeapon) {
            unequip(rangedWeapon);
            rangedWeapon = (RangedWeapon) i;
            ((RangedWeapon)i).setEquipped(true);

            if(quiver != null && !quiver.hasProperty(rangedWeapon.getAmmoType()))
                unequip(quiver);

            return true;
        }

        else if(i instanceof Ammo && (rangedWeapon == null || i.hasProperty(rangedWeapon.getAmmoType()))) {
            unequip(quiver);
            quiver = (Ammo) i;
            ((Ammo)i).setEquipped(true);
            return true;
        }

        else if(i instanceof Armor && i.hasProperty("body")) {
            unequip(body);
            body = (Armor)i;
            ((Armor)i).setEquipped(true);
            return true;
        }

        return false;
    }

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

            if(quiver != null && !quiver.hasProperty(rangedWeapon.getAmmoType()))
                unequip(quiver);

            if(notify) doAction("equip %s.", i.toString());

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

            return true;
        }

        return false;
    }

    public boolean unequip(Item i) {
        if(!(i instanceof Equipable) || (!isEquipped(i) && !((Equipable)i).isEquipped())) return true;

        ((Equipable)i).setEquipped(false);

        if(mainHand != null && mainHand.equals(i)) {
            mainHand = null;
            return true;
        }

        else if(rangedWeapon != null && rangedWeapon.equals(i)) {
            rangedWeapon = null;
            return true;
        }

        else if(quiver != null && quiver.equals(i)) {
            quiver = null;
            return true;
        }

        else if(body != null && body.equals(i)) {
            body = null;
            return true;
        }


        return false;

    }

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
            return true;
        }


        return false;

    }

    public boolean isEquipped(Item i) {
        if(!(i instanceof Equipable)) return false;

        if(mainHand != null && mainHand.equals(i) && ((Equipable)i).isEquipped()) return true;

        if(rangedWeapon != null && rangedWeapon.equals(i) && ((Equipable)i).isEquipped()) return true;

        if(quiver != null && quiver.equals(i) && ((Equipable)i).isEquipped()) return true;

        if(body != null && body.equals(i) && ((Equipable)i).isEquipped()) return true;

        return false;
    }

    public void applyEffect(Effect e) {
        e.affect(this);
        e.changeRemainingDurationBy(-1);
        if(!hasEffect(e)) activeEffects.add(e);
    }

    public void addSpell(Spell s) {
        spells.add(s);
        s.setCaster(this);
    }

    public void cast(Spell s) {

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Creature)) return false;
        Creature creature = (Creature) o;
        return hpMax == creature.hpMax &&
                hp == creature.hp &&
                hunger == creature.hunger &&
                hungerMax == creature.hungerMax &&
                exp == creature.exp &&
                expLevel == creature.expLevel &&
                strength == creature.strength &&
                agility == creature.agility &&
                constitution == creature.constitution &&
                perception == creature.perception &&
                damageBonus == creature.damageBonus &&
                defenseBonus == creature.defenseBonus &&
                x == creature.x &&
                y == creature.y &&
                glyph == creature.glyph &&
                team == creature.team &&
                lastMovedTime == creature.lastMovedTime &&
                moveDirection == creature.moveDirection &&
                Objects.equals(level, creature.level) &&
                Objects.equals(name, creature.name) &&
                Objects.equals(ai, creature.ai) &&
                Objects.equals(unarmedAttack, creature.unarmedAttack) &&
                Objects.equals(natArmor, creature.natArmor) &&
                Objects.equals(inventory, creature.inventory) &&
                Objects.equals(mainHand, creature.mainHand) &&
                Objects.equals(rangedWeapon, creature.rangedWeapon) &&
                Objects.equals(quiver, creature.quiver) &&
                Objects.equals(body, creature.body) &&
                Objects.equals(activeEffects, creature.activeEffects);
    }

    @Override
    public int hashCode() {
        return Objects.hash(hpMax, hp, hunger, hungerMax, exp, expLevel, strength, agility, constitution, perception, damageBonus, defenseBonus, x, y, level, name, glyph, ai, team, lastMovedTime, moveDirection, unarmedAttack, natArmor, inventory, mainHand, rangedWeapon, quiver, body, activeEffects);
    }
}
