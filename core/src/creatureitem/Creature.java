package creatureitem;

import actors.creatures.CreatureActor;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import creatureitem.ai.CreatureAi;
import creatureitem.effect.Effect;
import creatureitem.item.*;
import creatureitem.spell.*;
import utility.Utility;
import world.Level;
import world.Tile;
import world.geometry.AStarPoint;
import world.geometry.Cursor;
import world.geometry.Line;
import world.geometry.Point;

import java.util.*;

public class Creature extends Entity {

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
     * Measure creature's of mental discipline, focus and patience
     */
    protected int discipline;

    /**
     * Measure of creature's ability to influence the surrounding world
     */
    protected int charisma;


    /**
     * Bonus to damage
     */
    protected int damageBonus;

    /**
     * Bonus to magic damage
     */
    protected int magicBonus;

    /**
     * Bonus to defense, separate from natural armor
     */
    protected int defenseBonus;

    /*
     * Movement/Location
     */

    protected double rarity;

    public static final double SUPER_COMMON = 0d, COMMON = 1d, AVERAGE = 2d, UNCOMMON = 3d, RARE = 4d, LEGENDARY = 5d;

    /**
     * Reference to the level the creature is in
     */
    protected Level level;

    protected boolean[][] vision;

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
    protected CreatureAi ai;

    /**
     * Creatures are hostile to creatures on different teams
     */
    protected int team;

    /**
     * Actor associated with this creature
     */
    protected Actor actor;

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

    /**
     * All creatures/tiles the creature can see due to magic
     */
    protected ArrayList<Creature> extra_sight;

    protected boolean[][] extra_vision;

    protected boolean requestVisionUpdate;

    //</editor-fold>

    public Creature(int hpMax, int hungerMax, int manaMax, int exp, int strength, int agility, int constitution, int perception, int intelligence, int discipline, int charisma,
                    Level level, String texturePath, String name, char glyph, int team, Weapon unarmedAttack, int natArmor, float energy_factor, String ... properties) {
        super(-1, -1, true, properties);
        this.hp = this.hpMax = hpMax + getAttributeBonus(constitution);
        this.hunger = this.hungerMax = hungerMax;
        this.exp = exp;
        this.expLevel = 1;

        this.strength = strength;
        this.agility = agility;
        this.constitution = constitution;
        this.perception = perception;
        this.intelligence = intelligence;
        this.discipline = discipline;
        this.charisma = charisma;

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

        this.energy_factor = energy_factor;

        this.spells = new ArrayList<>();
        this.activeEffects = new ArrayList<>();
        this.extra_sight = new ArrayList<>();

        this.vision = new boolean[level == null? 0 : level.getWidth()][level == null? 0 : level.getHeight()];

        for (boolean[] sight : vision)
            Arrays.fill(sight, false);

        clear_Extra_vision();

        requestVisionUpdate = true;
    }

    public Creature(Creature creature) {
        super(creature);

        this.hp = this.hpMax = creature.hpMax;
        this.hunger = this.hungerMax = creature.hungerMax;

        this.exp = creature.exp;
        this.expLevel = 1;

        this.strength = creature.strength;
        this.agility = creature.agility;
        this.constitution = creature.constitution;
        this.perception = creature.perception;
        this.intelligence = creature.intelligence;
        this.discipline = creature.discipline;
        this.charisma = creature.charisma;

        this.damageBonus = creature.damageBonus;
        this.magicBonus = creature.magicBonus;
        this.defenseBonus = creature.defenseBonus;

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

        this.extra_sight = new ArrayList<>();

        this.vision = new boolean[creature.vision.length][creature.vision.length == 0? 0 : creature.vision[0].length];

        for(int i = 0; i < vision.length; i++)
            for(int j = 0; j < vision[0].length; j++)
                this.vision[i][j] = creature.vision[i][j];

        if(creature.extra_vision == null) {
            clear_Extra_vision();
        }
        else {
            this.extra_vision = new boolean[creature.extra_vision.length][creature.extra_vision.length == 0? 0 : creature.extra_vision[0].length];

            for(int i = 0; i < extra_vision.length; i++)
                for(int j = 0; j < extra_vision[0].length; j++)
                    this.extra_vision[i][j] = creature.extra_vision[i][j];
        }

        requestVisionUpdate = creature.requestVisionUpdate;
    }

    //<editor-fold desc="Getters and Setters">

    public boolean isRequestVisionUpdate() {
        return requestVisionUpdate;
    }

    public void setRequestVisionUpdate(boolean requestVisionUpdate) {
        this.requestVisionUpdate = requestVisionUpdate;
    }

    public boolean[][] getVision() {
        return vision;
    }

    public void setVision(boolean[][] vision) {
        this.vision = vision;
    }

    public ArrayList<Item> equipped() {
        ArrayList<Item> equipped = new ArrayList<>();
        if(quiver != null) equipped.add(quiver);
        if(mainHand != null) equipped.add(mainHand);
        if(rangedWeapon != null) equipped.add(rangedWeapon);
        if(body != null) equipped.add(body);
        return equipped;
    }

    /**
     * @param exp Modifier to experience
     */
    public void modifyExp(int exp) {
        setExp(this.exp + exp);
    }

    /**
     * @param exp Modifer to experience
     * @param notify Whether or not to notify the creature that their experience changed
     */
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

    /**
     * Change the creature's current HP
     * @param mod Amount to change HP by
     * @param notifyOnDie Whether to notify if the creature died
     * @return true if the creature died
     */
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

        if(hunger < 1 && getLevel().getTurnNumber() % 5 == 0)
            modifyHP(-1);
    }

    public void modifyMaxHunger(int mod) {
        hungerMax = Math.max(hungerMax, 0);
    }

    public Level getLevel() {
        return level;
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

    public int getDiscipline() {
        return discipline;
    }

    public void setDiscipline(int discipline) {
        this.discipline = discipline;
    }

    public int getCharisma() {
        return charisma;
    }

    public void setCharisma(int charisma) {
        this.charisma = charisma;
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

    public ArrayList<Creature> getExtra_sight() {
        return extra_sight;
    }

    public void setExtra_sight(ArrayList<Creature> extra_sight) {
        this.extra_sight = extra_sight;
    }

    public void clear_extra_sight() {
        this.extra_sight.clear();
    }

    public void add_extra_sight(Creature c) {
        extra_sight.add(c);
    }

    public void remove_extra_sight(Creature c) {
        if(c == null) return;
        extra_sight.remove(c);
    }

    public boolean[][] getExtra_vision() {
        return extra_vision;
    }

    public void setExtra_vision(boolean[][] extra_vision) {
        this.extra_vision = extra_vision;
    }

    public void clear_Extra_vision() {
        if(level == null)
            return;

        this.extra_vision = new boolean[level.getWidth()][level.getHeight()];

        for (boolean[] sight : extra_vision)
            Arrays.fill(sight, false);
    }

    public boolean update_Extra_vision() {

        if(extra_vision == null) {
            clear_Extra_vision();

            if(extra_vision == null) {
                return false;
            }
        }

        if(extra_vision.length == 0)
            return false;

        if(extra_sight == null)
            return false;

        boolean changed = false;
        boolean[][] copy = new boolean[extra_vision.length][extra_vision[0].length];

        for(int i = 0; i < copy.length; i++) {
            System.arraycopy(extra_vision[i], 0, copy[i], 0, copy[0].length);
        }

        clear_Extra_vision();

        ArrayList<Creature> toRemove = new ArrayList<>();
        for(int i = 0; i < extra_vision.length; i++) {
            for(int j = 0; j < extra_vision[0].length; j++) {
                for(Creature c : extra_sight) {
                    if(c.equals(this)) continue;
                    if(c.getLevel() == null || c.getLevel().getCreatureAt(c.getX(), c.getY()) == null ||
                            !c.getLevel().getCreatureAt(c.getX(), c.getY()).equals(c)) {
                        toRemove.add(c);
                        continue;
                    }
                    extra_vision[i][j] |= c.canSee(i, j);
                    if(extra_vision[i][j] != copy[i][j])
                        changed = true;
                    if(extra_vision[i][j])
                        break;
                }
            }
        }

        extra_sight.removeAll(toRemove);

        if(requestVisionUpdate) {
            updateVision();
            changed = false;
        }

        return changed;

    }

    public boolean get_Extra_vision(int x, int y) {
        if(extra_vision == null) return false;

        try {
            return extra_vision[x][y];
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return false;
        }
    }

    public boolean get_Extra_vision(Point p) {
        return get_Extra_vision(p.getX(), p.getY());
    }

    //</editor-fold>

    /**
     * Move the creature through a wall by (wx, wy)
     * @param wx X distance
     * @param wy Y Distance
     */
    public void dig(int wx, int wy) {
        modifyHunger(-10);
        doAction("dig out %s.", level.getTileAt(wx, wy).getName());
        level.dig(wx, wy);
        spendEnergy(200);
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
        boolean miss = !toHit(getMainHand(), foe);

        if(miss) {
            doAction("attack %s.", foe.name);
            foe.doAction("dodge the attack!");
        }

        else {
            if(mainHand == null) damage = unarmedAttack.getWeaponDamage().getDamage(getLevel().getRandom()) +
                    getAttributeBonus(strength) + damageBonus;
            else damage = mainHand.getWeaponDamage().getDamage(getLevel().getRandom()) + getAttributeBonus(strength) + damageBonus;

            damage = Math.max(0, damage - foe.getArmor());
            damage *= -1;

            boolean died = foe.modifyHP(damage);
            doAction("attack %s for %d damage.", foe.name, Math.abs(damage));

            if(getMainHand() != null && getMainHand().getOnHit() != null && level.getRandom().nextDouble() < getMainHand().getImpactSpellProbability()) {
                getMainHand().getOnHit().setCaster(this);
                cast(getMainHand().getOnHit(), foe.getLocation());
            }

            if(died) {
                doAction("kill %s.", foe.name);
                modifyExp(foe.getExp());
            }
        }

        spendEnergy(100);

    }

    public void attack(Point p) {

        int px = p.getX(), py = p.getY();
        Creature foe = null;

        for(int i = 1; i <= (mainHand == null? 1 : mainHand.getReach()); i++) {
            if(this.x < px) {
                if(this.y < py) {
                    foe = level.getCreatureAt(x + i, y + i);

                    if(foe != null) {
                        break;
                    }
                }
                else if(this.y > py) {
                    foe = level.getCreatureAt(x + i, y - i);

                    if(foe != null) {
                        break;
                    }
                }
                else {
                    foe = level.getCreatureAt(x + i, y);

                    if(foe != null) {
                        break;
                    }
                }
            }
            else if(this.x > px) {
                if(this.y < py) {
                    foe = level.getCreatureAt(x - i, y + i);

                    if(foe != null) {
                        break;
                    }
                }
                else if(this.y > py) {
                    foe = level.getCreatureAt(x - i, y - i);

                    if(foe != null) {
                        break;
                    }
                }
                else {
                    foe = level.getCreatureAt(x - i, y);

                    if(foe != null) {
                        break;
                    }
                }
            }
            else {
                if(this.y < py) {
                    foe = level.getCreatureAt(x, y + i);

                    if(foe != null) {
                        break;
                    }
                }
                else if(this.y > py) {
                    foe = level.getCreatureAt(x, y - i);

                    if(foe != null) {
                        break;
                    }
                }
                else {
                    break;
                }
            }
        }

        if(foe != null) {
            attack(foe);
        }
        else {
            doAction("swing at the air!");
        }
    }

    /**
     * Broadcast a message that you did something
     * @param message The message to send
     * @param params Formatting
     */
    public void doAction(String message, Object ... params) {
        if(level == null) return;

        for(Creature other : level.getCreatures()) {

            if(other == null)
                continue;

            if(other.equals(this)) {
                notify(String.format(Locale.getDefault(), "You %s", message), params);
            }
            else if(other.canSee(getLocation())) {
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
        moveTo(x + mx, y + my);
    }

    public void moveTo(Point p) {
        moveTo(p.getX(), p.getY());
    }

    public void moveTo(int x, int y) {
        if(x < 0 || x >= level.getWidth() || y < 0 || y >= level.getHeight()) return;

        Creature foe = null;
        boolean ignore = false;

        for(int i = 0; i < (mainHand == null? 1 : mainHand.getReach()); i++) {
            if(this.x < x) {
                if(this.y < y) {
                    foe = level.getCreatureAt(x + i, y + i);

                    if(foe != null) {
                        if(foe.team == team && i >= 1) ignore = true;
                        break;
                    }
                }
                else if(this.y > y) {
                    foe = level.getCreatureAt(x + i, y - i);

                    if(foe != null) {
                        if(foe.team == team && i >= 1) ignore = true;
                        break;
                    }
                }
                else {
                    foe = level.getCreatureAt(x + i, y);

                    if(foe != null) {
                        if(foe.team == team && i >= 1) ignore = true;
                        break;
                    }
                }
            }
            else if(this.x > x) {
                if(this.y < y) {
                    foe = level.getCreatureAt(x - i, y + i);

                    if(foe != null) {
                        if(foe.team == team && i >= 1) ignore = true;
                        break;
                    }
                }
                else if(this.y > y) {
                    foe = level.getCreatureAt(x - i, y - i);

                    if(foe != null) {
                        if(foe.team == team && i >= 1) ignore = true;
                        break;
                    }
                }
                else {
                    foe = level.getCreatureAt(x - i, y);

                    if(foe != null) {
                        if(foe.team == team && i >= 1) ignore = true;
                        break;
                    }
                }
            }
            else {
                if(this.y < y) {
                    foe = level.getCreatureAt(x, y + i);

                    if(foe != null) {
                        if(foe.team == team && i >= 1) ignore = true;
                        break;
                    }
                }
                else if(this.y > y) {
                    foe = level.getCreatureAt(x, y - i);

                    if(foe != null) {
                        if(foe.team == team && i >= 1) ignore = true;
                        break;
                    }
                }
                else {
                    break;
                }
            }
        }

        if(foe == null || ignore) {
            ai.onEnter(x, y, level.getTileAt(x, y));
            spendEnergy(100);
            requestVisionUpdate = true;
        }
        else if(foe.team != team) {
            attack(foe);
        }
        else {
            spendEnergy(100);
        }

    }

    public void moveTowards(Point p) {
        int mx, my;
        mx = Integer.compare(p.getX(), x);
        my = Integer.compare(p.getY(), y);
        moveBy(mx, my);
    }

    public void moveTowardsAStar(Point p) {
        try {
            Stack<AStarPoint> path = Utility.aStarWithVision(level.getCosts(), this, Point.DISTANCE_CHEBYCHEV, getLocation(), p);
            while(path.peek().getX() == x && path.peek().getY() == y) path.pop();
            if(!path.isEmpty()) {
                moveTowards(path.pop());
            }
        } catch (EmptyStackException ignored) {

        }

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
        try {
            return vision[x][y];
        } catch (IndexOutOfBoundsException e) {
            return false;
        }
    }

    public boolean canSee(Point p) {
        try {
            return vision[p.getX()][p.getY()];
        } catch (Exception e) {
            return false;
        }
    }

    public void updateVision() {
        requestVisionUpdate = false;
        level.getDungeon().getGame().getPlayScreen().getUi().setRequestMinimapUpdate(true);
        vision = new boolean[level.getWidth()][level.getHeight()];
        for(int i = 0; i < level.getWidth(); i++) {
            for(int j = 0; j < level.getHeight(); j++) {
                vision[i][j] = ai.canSee(i, j);
            }
        }
    }

    /**
     * Update a creature's statuses
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

    @Override
    public void act(Level l) {

        boolean changed = update_Extra_vision();
        if(changed)
            requestVisionUpdate = true;

        ai.onAct();

        /*
         * If energy used is 0, the creature did nothing and is waiting
         * until its next action.
         */
        if(energy_used <= 0) {
            energy_used = Level.getEnergyPerTurn()/2;
        }
        process();
    }

    public void process() {
        process(level);
    }

    @Override
    public void process(Level l) {
        l.advance(this);
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
        pickUp(true);
    }

    public void pickUp(boolean notify) {
        if(level.getInventoryAt(x, y) == null || level.getInventoryAt(x, y).isEmpty())
            pickUp(null, notify, true);
        else
            pickUp(level.getInventoryAt(x, y).top(), notify, true);
    }

    /**
     * Pick up an item off the ground and put it in your inventory
     */
    public void pickUp(Item i) {
        pickUp(i, true, true);
    }

    public void pickUp(Item i, boolean notify, boolean energy) {
        if(i == null || level == null || level.getInventoryAt(x, y) == null || level.getInventoryAt(x, y).isEmpty() || level.getInventoryAt(x, y).contains(i) == -1) {
            if (notify) doAction("grab at the ground.");
        }
        else {
            if(notify) doAction("pick up %s.", i.toString());

            i.assignCaster(this);

            if(i instanceof Equipable) {
                if(i instanceof Ammo) {
                    Ammo a = (Ammo)i;
                    inventory.add(a);
                }
                else if(i instanceof Armor) {
                    Armor a = (Armor)i;
                    inventory.add(a);
                }
                else if(i instanceof Weapon) {
                    if(i instanceof RangedWeapon) {
                        RangedWeapon a = (RangedWeapon)i;
                        inventory.add(a);
                    }
                    else {
                        Weapon a = (Weapon)i;
                        inventory.add(a);
                    }
                }
                else {
                    Equipable a = (Equipable)i;
                    inventory.add(a);
                }
            }
            else if(i instanceof Potion) {
                Potion a = (Potion)i;
                inventory.add(a);
            }
            else if(i instanceof Food) {
                Food a = (Food)i;
                inventory.add(a);
            }
            else {
                Item a = i;
                inventory.add(a);
            }

            level.getInventoryAt(x, y).remove(i);
        }

        if(energy) spendEnergy(50);
    }

    /**
     * Place an item from your inventory on the ground
     * @param item The item to drop
     */
    public void drop(Item item) {
        drop(item, true);
    }

    public void drop(Item item, boolean notify) {
        drop(item, notify, true, true);
    }

    public void drop(Item item, boolean notify, boolean remove, boolean energy) {
        drop(item, x, y, notify, remove, energy);
    }

    public void drop(Item item, int x, int y) {
       drop(item, x, y, true);
    }

    public void drop(Item item, int x, int y, boolean notify) {
        drop(item, x, y, notify, true, true);
    }

    public void drop(Item item, int x, int y, boolean notify, boolean remove, boolean energy) {

        if(energy) spendEnergy(50);

        if(item == null) return;

        item.assignCaster(null);

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

    }

    public void throwItem(Item item, Cursor cursor) {
        boolean depleted = inventory.removeOne(item);
        Item i;

        spendEnergy(60);

        if(item instanceof Potion) i = new Potion((Potion)item);
        else if(item instanceof Equipable) {
            if(item instanceof Weapon) {
                if(item instanceof RangedWeapon) {
                    i = new RangedWeapon((RangedWeapon)item);
                }
                else {
                    i = new Weapon((Weapon)item);
                }
            }
            else if(item instanceof Armor) {
                i = new Armor((Armor)item);
            }
            else if(item instanceof Ammo) {
                i = new Ammo((Ammo)item);
            }
            else {
                i = new Equipable((Equipable)item);
            }
        }
        else if(item instanceof Food) {
            i = new Food(item);
        }
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
                    (i.hasProperty("throw"))) {
                damage = true;
                break;
            }
        }

        if(damage) {

            /*
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

             */
            throwAttack(i, level.getCreatureAt(current.getX(), current.getY()));
        }
        else {
            doAction("throw %s.", i.getName());
            if(i.hasProperty("shatter"))
                notify("It shatters on impact!");
            else {
                if(i.getOnThrow() == null)
                    drop(i, current.getX(), current.getY(), false, false, false);
            }
        }

        if(i.getOnThrow() != null) {
            i.assignCaster(this);
            cast(i.getOnThrow(), current);
            i.assignCaster(null);
        }

        if(depleted) {
            unequip(item, true, false);
        }

    }

    public boolean canShoot(Point cursor) {
        if(quiver == null || rangedWeapon == null) return false;

        Line path = new Line(this.getX(), cursor.getX(), this.getY(), cursor.getY(), rangedWeapon.getRange() + 1);

        for(Point p : path) {
            if(p.equals(getLocation())) continue;
            if(!level.isPassable(p.getX(), p.getY())) return false;
        }

        return true;
    }

    public void shootRangedWeapon(Point cursor) {
        if(!canShoot(cursor)) return;

        spendEnergy(100);

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
            if(i.getOnThrow() == null && getLevel().getRandom().nextDouble() < 0.4) recover = true;
        }
        else {
            if(i.getOnThrow() == null)
                drop(i, current.getX(), current.getY(), false, false, false);
            doAction("shoot %s.", i.getName());
        }

        if(i.getOnThrow() != null) {
            i.assignCaster(this);
            cast(i.getOnThrow(), current);
            i.assignCaster(null);
        }

        if(depleted) {
            unequip(quiver, false, false);
        }
        if(recover) {
            drop(i, current.getX(), current.getY(), false, false, false);
        }

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

        boolean miss = toHit(getRangedWeapon(), foe);

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


        for(Effect e : activeEffects) {
            e.done(this);
        }
    }

    public void leaveCorpse() {
        if(getLevel().getRandom().nextDouble() < .3) {
            Food corpse = new Food('%', "data/Corpse.png", String.format(Locale.getDefault(), "%s corpse", name), 1, hpMax * 3, "stack");
            level.addAt(x, y, corpse);
        }

        for(Item i : inventory.getItems()) {
            if(i == null) continue;
            if(i.hasProperty("stack")) {
                int c = i.getCount();
                c = getLevel().getRandom().nextInt(Math.max((c - (int)(c * 0.3d)), 1)) + (int)(c * 0.3d);
                i.setCount(c);
            }
            drop(i, false);
        }
    }

    public void eat(Food f) {
        eat(f, true);
    }

    public void eat(Food f, boolean notify) {
        eat(f, notify, true);
    }

    public void eat(Food f, boolean notify, boolean energy) {

        if(notify) doAction("eat %s.", f.getName());

        if(f.getOnEat() != null) {
            f.assignCaster(this);
            cast(f.getOnEat(), getLocation());
        }

        inventory.removeOne(f);

        hunger = Math.min(hungerMax, hunger + f.getFoodValue());

        if(energy) spendEnergy(100);
    }

    public void drink(Potion p) {
        drink(p, true);
    }

    public void drink(Potion p, boolean notify) {
        drink(p, notify, true);
    }

    public void drink(Potion p, boolean notify, boolean energy) {
        if(notify) doAction("drink %s.", p.getName());

        if(p.getOnDrink() != null) {
            p.assignCaster(this);
            cast(p.getOnDrink(), getLocation());
        }

        inventory.removeOne(p);

        if(energy) spendEnergy(100);
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
        return equip(i, true);
    }

    public boolean equip(Item i, boolean notify) {
        return equip(i, notify, true);
    }

    public boolean equip(Item i, boolean notify, boolean energy) {

        if(inventory.contains(i) != -1) {
            pickUp(i, notify, energy);
        }

        if(!(i instanceof Equipable)) return false;

        boolean equipped = false;

        if(i instanceof Weapon && i.hasProperty("main hand")) {
            unequip(mainHand);
            mainHand = (Weapon) i;
            ((Weapon)i).setEquipped(true);
            if(notify) doAction("equip %s.", i.toString());
            if(energy) spendEnergy(100);
            equipped = true;
        }

        else if(i instanceof RangedWeapon) {
            unequip(rangedWeapon);
            rangedWeapon = (RangedWeapon) i;
            ((RangedWeapon)i).setEquipped(true);
            if(notify) doAction("equip %s.", i.toString());

            if(quiver != null && !quiver.hasProperty(rangedWeapon.getAmmoType()))
                unequip(quiver);

            if(energy) spendEnergy(80);
            equipped = true;
        }

        else if(i instanceof Ammo && (rangedWeapon == null || i.hasProperty(rangedWeapon.getAmmoType()))) {
            unequip(quiver);
            quiver = (Ammo) i;
            ((Ammo)i).setEquipped(true);
            if(notify) doAction("quiver %s.", i.toString());

            if(energy) spendEnergy(60);
            equipped = true;
        }

        else if(i instanceof Armor && i.hasProperty("body")) {
            unequip(body);
            body = (Armor)i;
            ((Armor)i).setEquipped(true);
            if(notify) doAction("equip %s.", i.toString());

            if(energy) spendEnergy(150);
            equipped = true;
        }

        if(equipped) {
            applyEffect(((Equipable) i).getOnEquip());
        }

        return equipped;
    }

    public boolean unequip(Item i) {
        return unequip(i, true);
    }

    public boolean unequip(Item i, boolean notify) {
        return unequip(i, notify, true);
    }

    public boolean unequip(Item i, boolean notify, boolean energy) {
        if(!(i instanceof Equipable) || (!isEquipped(i) && !((Equipable)i).isEquipped())) return true;

        ((Equipable)i).setEquipped(false);

        removeEffect(((Equipable) i).getOnEquip());

        if(mainHand != null && mainHand.equals(i)) {
            mainHand = null;
            if(notify) doAction("unequip %s.", i.toString());

            if(energy) spendEnergy(100);
            return true;
        }

        else if(rangedWeapon != null && rangedWeapon.equals(i)) {
            rangedWeapon = null;
            if(notify) doAction("unequip %s.", i.toString());

            if(energy) spendEnergy(80);
            return true;
        }

        else if(quiver != null && quiver.equals(i)) {
            quiver = null;
            if(notify) doAction("unequip %s.", i.toString());

            if(energy) spendEnergy(60);
            return true;
        }

        else if(body != null && body.equals(i)) {
            body = null;
            if(notify) doAction("unequip %s.", i.toString());

            if(energy) spendEnergy(150);
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

        if(e == null) return;

        Effect e_copy = e.makeCopy(e);
        e_copy.affect(this);
        e_copy.changeRemainingDurationBy(-1);
        if(!hasEffect(e_copy)) activeEffects.add(e_copy);
    }

    public void addSpell(Spell s) {
        spells.add(s);
        s.setCaster(this);
    }

    public void cast(Spell s, Point p) {
        //spendEnergy(s.getCast_energy());
        if(s instanceof LineSpell) {
            ((LineSpell)s).cast(getLocation(), p);
        }
        else if(s instanceof AOESpell) {
            ((AOESpell)s).cast(p.getX(), p.getY());
        }
        else if(s instanceof MassSpell) {
            ((MassSpell)s).cast();
        }
        else if(s instanceof PointSpell) {
            ((PointSpell)s).cast(p);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Creature)) return false;
        Creature creature = (Creature) o;
        return x == creature.x &&
                y == creature.y &&
                glyph == creature.glyph &&
                team == creature.team &&
                Objects.equals(name, creature.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, name, glyph, team);
    }
}
