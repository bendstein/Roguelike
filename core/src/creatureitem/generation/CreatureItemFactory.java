package creatureitem.generation;

import actors.creatures.CreatureActor;
import actors.creatures.PlayerActor;
import creatureitem.Creature;
import creatureitem.ai.CreatureAi;
import creatureitem.ai.NPCAi;
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
import screens.Dialogue;
import world.Dungeon;
import world.Level;

import java.util.ArrayList;
import java.util.HashMap;

public class CreatureItemFactory {

    /**
     * @return A new player character
     */
    public static Player newPlayer(ArrayList<String> messageQueue) {
        Player player = new Player(players.get("Player"));
        new PlayerAi(player, messageQueue);
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

    public static Creature newVillager() {
        Creature villager = new Creature(creatures.get("Villager"));
        new NPCAi(villager);
        new CreatureActor(villager);

        Dialogue d0 = new Dialogue("Testing2.", true, new String[]{"a", "a", "a", "a", "a",
                "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a",
                "a", "a", "a", "a", "a"}, new Dialogue[]{});

        Dialogue d1 = new Dialogue("Testing.", true, new String[]{"Test"}, new Dialogue[]{d0});


        Dialogue d2 = new Dialogue("Bjork Test.", true, new String[]{"Test (Bjork)", "Bye."}, new Dialogue[]{d1});

        ((NPCAi)villager.getAi()).setDialogueRoot(d2);
        return villager;
    }

    /**
     * @return A new fungus monster
     */
    public static Creature newFungus() {
        Creature fungus = new Creature(creatures.get("Fungus"));
        new FungusAi(fungus);
        new CreatureActor(fungus);
        return fungus;
    }

    public static Creature newBat() {
        Creature bat = new Creature(creatures.get("Bat"));
        new BatAi(bat);
        new CreatureActor(bat);
        return bat;
    }

    public static Creature newZombie() {
        Creature zombie = new Creature(creatures.get("Zombie"));
        new ZombieAi(zombie);
        new CreatureActor(zombie);
        return zombie;
    }

    public static Creature newGoblin(Level level) {
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

    public static Ammo newRock() {
        return new Ammo((Ammo) items.get("Rock"));
    }

    public static Weapon newLongsword() {
        return new Weapon((Weapon)items.get("Longsword"));
    }

    public static RangedWeapon newShortbow() {
        return new RangedWeapon((RangedWeapon)items.get("Shortbow"));
    }

    public static RangedWeapon newSling(Level level) {
        return new RangedWeapon((RangedWeapon)items.get("Sling"));
    }

    public static Ammo newArrow() {
        return new Ammo((Ammo)items.get("Arrow"));
    }

    public static Armor newArmor() {
        return new Armor((Armor)items.get("Leather Armor"));
    }

    public static Potion newHealthPotion() {
        return new Potion((Potion)items.get("Health Potion"));
    }

    public static Potion newRegenPotion() {
        return new Potion((Potion)items.get("Regeneration Potion"));
    }

    public static Potion newPoisonPotion() {
        return new Potion((Potion)items.get("Poison Potion"));
    }

    public static Potion newHeroismPotion() {
        return new Potion((Potion)items.get("Heroism Potion"));
    }

    public static Item newItem(String s) {
        if(items.get(s) instanceof Equipable) {
            if (items.get(s) instanceof Ammo)
                return new Ammo((Ammo) items.get(s));
            else if (items.get(s) instanceof Weapon) {
                if (items.get(s) instanceof RangedWeapon)
                    return new RangedWeapon((RangedWeapon) items.get(s));
                else
                    return new Weapon((Weapon) items.get(s));
            } else if (items.get(s) instanceof Armor)
                return new Armor((Armor) items.get(s));
            else
                return new Equipable((Equipable) items.get(s));
        }
        else if(items.get(s) instanceof Potion)
            return new Potion((Potion)items.get(s));
        else
            return new Item(items.get(s));
    }

    public static Creature newCreature(String s) {
        return new Creature(creatures.get(s));
    }

    public static CreatureAi newAi(String s) {
        return ais.get(s).copy();
    }

    public static CreatureAi newAi(String s, Creature c) {
        return ais.get(s).copy().setCreature(c);
    }

    public static CreatureAi newAi(Creature c) {
        return ais.get(c.getName()).copy().setCreature(c);
    }

    public static CreatureActor newActor(String s) {
        if(actors.containsKey(s)) return actors.get(s).copy();
        else return actors.get("Creature").copy();
    }

    public static CreatureActor newActor(String s, Creature c) {
        if(actors.containsKey(s)) return actors.get(s).copy().setCreature(c);
        else return actors.get("Creature").copy().setCreature(c);
    }

    public static CreatureActor newActor(Creature c) {
        if(actors.containsKey(c.getName())) return actors.get(c.getName()).copy().setCreature(c);
        else return actors.get("Creature").copy().setCreature(c);
    }

    public static final HashMap<String, Player> players = new HashMap<String, Player>() {
        {
            /*
            maxHP, hungerMax, exp, strength, agility, constitution, perception,
        level, texturePath, name, glyph, team, unarmedAttack, natArmor
             */
            put("Player", new Player(10, 60, 12, 0,
                    12, 14, 12, 8, 14,
                    null, "data/Player.png",
                    "Player", '@', 0,
                    new Weapon("Fist", new Damage(1, 4, 0), 0), 1));

        }
    };
    public static final HashMap<String, Creature> creatures = new HashMap<String, Creature>() {
        {
            put("Villager", new Creature(3, 80, 0, 1,
                    8, 8, 8, 8, 8,
                    null, "data/Villager.png",
                    "Villager", 'v',
                    0,
                    new Weapon("Fist", new Damage(1, 4, 0), 0), 1));
            put("Fungus", new Creature(3, 80, 0, 1,
                    6, 8, 12, 2, 2,
                    null, "data/Fungus.png",
                    "Fungus", 'f',
                    1,
                    new Weapon("Spore", new Damage(1, 4, 0), 0), 0));
            put("Bat", new Creature(5, 35, 0, 3,
                    6, 14, 8, 18, 10,
                    null, "data/Bat.png",
                    "Bat", 'b',
                    1,
                    new Weapon("Claw", new Damage(1, 4, 0), 1), 0));
            put("Zombie", new Creature(7, 35, 0, 5,
                    10, 6, 10, 8, 1,
                    null, "data/Zombie.png",
                    "Zombie", 'z',
                    1,
                    new Weapon("Fist", new Damage(1, 4, 1), 0), 2));
            put("Goblin", new Creature(8, 60, 0, 8,
                    4, 6, 6, 14, 8,
                    null, "data/Goblin.png",
                    "Goblin", 'g',
                    1,
                    new Weapon("Claw", new Damage(1, 4, 1), 0), 1));
        }
    };
    public static final HashMap<String, CreatureAi> ais = new HashMap<String, CreatureAi>() {
        {
            put("Player", new PlayerAi(null, null));
            put("Villager", new NPCAi(null));
            put("Fungus", new FungusAi(null));
            put("Bat", new BatAi(null));
            put("Zombie", new ZombieAi(null));
            put("Goblin", new GoblinAi(null));
        }
    };
    public static final HashMap<String, CreatureActor> actors = new HashMap<String, CreatureActor>() {
        {
            put("Player", new PlayerActor(null));
            put("Creature", new CreatureActor(null));
        }
    };

    public static final HashMap<String, Item> items = new HashMap<String, Item>() {
        {
            put("Rock", new Ammo(',', "data/Rock.png", "Rock", 1, new Damage(1, 2, 0), new Damage(1, 2, 0), "throw", "stack", "bullet"));
            put("Longsword", new Weapon('\\', "data/Weapon.png", "Longsword", 10, new Damage(1, 8, 0), 0, "main hand"));
            put("Dagger", new Weapon('\\', "data/Dagger.png", "Dagger", 5, new Damage(1, 4, 0), 1, "main hand"));
            put("Shortbow", new RangedWeapon(')', "data/RangedWeapon.png", "Shortbow", 5, new Damage(1, 6, 0), 1, 6, "arrow"));
            put("Arrow", new Ammo('^', "data/Arrow.png", "Arrow", 1, new Damage(1, 3, 0), "arrow", "stack"));
            put("Sling", new RangedWeapon(')', "data/Sling.png", "Sling", 2, new Damage(1, 4, 0), -1, 5, "bullet"));
            put("Leather Armor", new Armor('&', "data/Leather Armor.png", "Leather Armor", 5, 2, "body"));
            put("Cloth Armor", new Armor('&', "data/Cloth Armor.png", "Cloth Armor", 2, 1, "body"));
            put("Health Potion", new Potion('!', "data/Potion.png", "Health Potion", 20, new HealthEffect(0, new Damage(1, 4, 1), true)));
            put("Regeneration Potion", new Potion('!', "data/Potion.png", "Regeneration Potion", 25,new HealthEffect(8, new Damage(0, 1, 1), true)));
            put("Poison Potion", new Potion('!', "data/Potion.png", "Poison Potion", 10, new HealthEffect(6, new Damage(0, 1, 1), false)));
            put("Heroism Potion", new Potion('!', "data/Potion.png", "Heroism Potion", 25, new StatusEffect(new int[]{0, 0, 0, 0, 0, 0, 0, 0, 5, 5, 0}, true, 10)));
        }
    };

    public static final HashMap<String, Spell> spells = new HashMap<String, Spell>() {
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
