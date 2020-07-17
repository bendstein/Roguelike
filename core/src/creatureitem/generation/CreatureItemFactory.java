package creatureitem.generation;

import actors.creatures.CreatureActor;
import actors.creatures.PlayerActor;
import creatureitem.Creature;
import creatureitem.ai.PlayerAi;
import creatureitem.ai.monster.GoblinAi;
import creatureitem.ai.monster.ZombieAi;
import creatureitem.effect.Damage;
import creatureitem.effect.HealthEffect;
import creatureitem.effect.StatusEffect;
import creatureitem.item.*;
import creatureitem.Player;
import creatureitem.ai.monster.BatAi;
import creatureitem.ai.monster.FungusAi;
import creatureitem.spell.AOESpell;
import creatureitem.spell.LineSpell;
import creatureitem.spell.PointSpell;
import creatureitem.spell.Spell;
import world.Dungeon;
import world.Level;

import java.util.ArrayList;
import java.util.HashMap;

public class CreatureItemFactory {


    //<editor-fold desc="Instance Variables">
    /**
     * Reference to level we're making creatures in
     */
    private Level level;
    //</editor-fold>


    //<editor-fold desc="Getters and Setters">
    public Level getLevel() {
        return level;
    }

    public void setLevel(Level level) {
        this.level = level;
    }
    //</editor-fold>

    /**
     * @return A new player character
     */
    public Player newPlayer(ArrayList<String> messageQueue) {
        Player player = new Player(players.get("Player"));
        level.setPlayer(player);
        new creatureitem.ai.PlayerAi(player, messageQueue);
        new PlayerActor(player);

        Weapon dg = new Weapon((Weapon)items.get("Longsword"));
        Armor am = new Armor((Armor)items.get("Cloth Armor"));
        RangedWeapon sl = new RangedWeapon((RangedWeapon)items.get("Sling"));
        PointSpell spl = new PointSpell((PointSpell)spells.get("Deal Damage (Point)"));
        PointSpell spl2 = new LineSpell((LineSpell)spells.get("Deal Damage (Line)"));
        PointSpell spl3 = new AOESpell((AOESpell)spells.get("Deal Damage (Area)"));

        player.getInventory().add(dg);
        player.getInventory().add(am);
        player.getInventory().add(sl);
        player.equip(dg);
        player.equip(am);
        player.equip(sl);
        player.addSpell(spl);
        player.addSpell(spl2);
        player.addSpell(spl3);
        return player;
    }

    /**
     * @return A new fungus monster
     */
    public Creature newFungus() {
        Creature fungus = new Creature(creatures.get("Fungus"));
        new FungusAi(fungus, this);
        new CreatureActor(fungus);
        return fungus;
    }

    public Creature newBat() {
        Creature bat = new Creature(creatures.get("Bat"));
        new BatAi(bat);
        new CreatureActor(bat);
        return bat;
    }

    public Creature newZombie() {
        Creature zombie = new Creature(creatures.get("Zombie"));
        new ZombieAi(zombie);
        new CreatureActor(zombie);
        return zombie;
    }

    public Creature newGoblin() {
        Creature goblin = new Creature(creatures.get("Goblin"));

        RangedWeapon sling = new RangedWeapon((RangedWeapon)items.get("Sling"));
        Ammo ammo = new Ammo((Ammo) items.get("Rock"));
        ammo.setCount(level.getRandom().nextInt(15 - 8) + 8);

        goblin.getInventory().add(sling);
        goblin.getInventory().add(ammo);
        goblin.equip(sling);
        goblin.equip(ammo);

        new GoblinAi(goblin);
        new CreatureActor(goblin);
        return goblin;
    }

    public Ammo newRock() {
        Ammo rock = new Ammo((Ammo) items.get("Rock"));
        level.addAtEmptyLocation(rock);
        return rock;
    }

    public Weapon newLongsword() {
        Weapon longsword = new Weapon((Weapon)items.get("Longsword"));
        level.addAtEmptyLocation(longsword);
        return longsword;
    }

    public RangedWeapon newShortbow() {
        RangedWeapon shortbow = new RangedWeapon((RangedWeapon)items.get("Shortbow"));
        level.addAtEmptyLocation(shortbow);
        return shortbow;
    }

    public RangedWeapon newSling() {
        RangedWeapon sling = new RangedWeapon((RangedWeapon)items.get("Sling"));
        level.addAtEmptyLocation(sling);
        return sling;
    }

    public Ammo newArrow() {
        Ammo arrow = new Ammo((Ammo)items.get("Arrow"));
        level.addAtEmptyLocation(arrow);
        return arrow;
    }

    public Armor newArmor() {
        Armor armor = new Armor((Armor)items.get("Leather Armor"));
        level.addAtEmptyLocation(armor);
        return armor;
    }

    public Potion newHealthPotion() {
        Potion p = new Potion((Potion)items.get("Health Potion"));
        level.addAtEmptyLocation(p);
        return p;
    }

    public Potion newRegenPotion() {
        Potion p = new Potion((Potion)items.get("Regeneration Potion"));
        level.addAtEmptyLocation(p);
        return p;
    }

    public Potion newPoisonPotion() {
        Potion p = new Potion((Potion)items.get("Poison Potion"));
        level.addAtEmptyLocation(p);
        return p;
    }

    public Potion newHeroismPotion() {
        Potion p = new Potion((Potion)items.get("Heroism Potion"));
        level.addAtEmptyLocation(p);
        return p;
    }

    public final HashMap<String, Player> players = new HashMap<String, Player>() {
        {
            /*
            maxHP, hungerMax, exp, strength, agility, constitution, perception,
        level, texturePath, name, glyph, team, unarmedAttack, natArmor
             */
            put("Player", new Player(10, 60, 12, 0, 12, 14, 12, 14, 14,
                    level, "data/Player.png", "Player", '@', 0,
                    new Weapon("Fist", new Damage(1, 4, 0), 0), 1));

        }
    };
    public final HashMap<String, Creature> creatures = new HashMap<String, Creature>() {
        {
            put("Fungus", new Creature(3, 80, 0, 1, 6, 8, 12, 2, 2,
                    level, "data/Fungus.png", "Fungus", 'f', 1,
                    new Weapon("Spore", new Damage(1, 4, 0), 0), 0));
            put("Bat", new Creature(5, 35, 0, 3, 6, 14, 8, 18, 10,
                    level, "data/Bat.png", "Bat", 'b', 2,
                    new Weapon("Claw", new Damage(1, 4, 0), 1), 0));
            put("Zombie", new Creature(7, 35, 0, 5, 10, 6, 10, 8, 1,
                    level, "data/Zombie.png", "Zombie", 'z', 1,
                    new Weapon("Fist", new Damage(1, 4, 1), 0), 2));
            put("Goblin", new Creature(8, 60, 0, 8, 4, 6, 6, 14, 8,
                    level, "data/Goblin.png", "Goblin", 'g', 1,
                    new Weapon("Claw", new Damage(1, 4, 1), 0), 1));
        }
    };
    public final HashMap<String, Item> items = new HashMap<String, Item>() {
        {
            put("Rock", new Ammo(',', "data/Rock.png", "Rock", new Damage(1, 2, 0), new Damage(1, 2, 0), "throw", "stack", "bullet"));
            put("Longsword", new Weapon('\\', "data/Weapon.png", "Longsword", new Damage(1, 8, 0), 0, "main hand"));
            put("Dagger", new Weapon('\\', "data/Dagger.png", "Dagger", new Damage(1, 4, 0), 1, "main hand"));
            put("Shortbow", new RangedWeapon(')', "data/RangedWeapon.png", "Shortbow", new Damage(1, 6, 0), 1, 6, "arrow"));
            put("Arrow", new Ammo('^', "data/Arrow.png", "Arrow", new Damage(1, 3, 0), "arrow", "stack"));
            put("Sling", new RangedWeapon(')', "data/Sling.png", "Sling", new Damage(1, 4, 0), -1, 5, "bullet"));
            put("Leather Armor", new Armor('&', "data/Leather Armor.png", "Leather Armor", 2, "body"));
            put("Cloth Armor", new Armor('&', "data/Cloth Armor.png", "Cloth Armor", 1, "body"));
            put("Health Potion", new Potion('!', "data/Potion.png", "Health Potion", new HealthEffect(0, new Damage(1, 4, 1), true)));
            put("Regeneration Potion", new Potion('!', "data/Potion.png", "Regeneration Potion", new HealthEffect(8, new Damage(0, 1, 1), true)));
            put("Poison Potion", new Potion('!', "data/Potion.png", "Poison Potion", new HealthEffect(6, new Damage(0, 1, 1), false)));
            put("Heroism Potion", new Potion('!', "data/Potion.png", "Heroism Potion", new StatusEffect(new int[]{0, 0, 0, 0, 0, 0, 0, 0, 5, 5, 0}, true, 10)));
        }
    };

    public final HashMap<String, Spell> spells = new HashMap<String, Spell>() {
        {
            put("Deal Damage (Point)",
                    new PointSpell(new HealthEffect(0, new Damage(1, 6, 1), false),
                    "Deal Damage (Point)", 3, 5));
            put("Deal Damage (Line)",
                    new LineSpell(new HealthEffect(0, new Damage(1, 6, 1), false),
                            "Deal Damage (Line)", 5, 5));
            put("Deal Damage (Area)",
                    new AOESpell(new HealthEffect(0, new Damage(1, 6, 1), false),
                            "Deal Damage (Area)", 7, 5, 3));
        }
    };
}
