package creatureitem.generation;

import actors.creatures.CreatureActor;
import actors.creatures.PlayerActor;
import creatureitem.Creature;
import creatureitem.ai.CreatureAi;
import creatureitem.ai.types.NPCAi;
import creatureitem.ai.PlayerAi;
import creatureitem.ai.monster.*;
import creatureitem.effect.damage.Damage;
import creatureitem.effect.HealthEffect;
import creatureitem.effect.effects.LightSwitch;
import creatureitem.effect.effects.MagicMapping;
import creatureitem.effect.effects.ObliterateWall;
import creatureitem.effect.StatusEffect;
import creatureitem.effect.effects.SeeMinds;
import creatureitem.item.*;
import creatureitem.Player;
import creatureitem.spell.*;
import utility.Utility;
import world.Tile;
import world.thing.DoorBehavior;
import world.thing.Thing;
import world.thing.ThingBehavior;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class CreatureItemFactory {

    /**
     * @return A new player character
     */
    public static Player newPlayer(ArrayList<String> messageQueue) {
        Player player = new Player(players.get("Player"));
        new PlayerAi(player, messageQueue);
        new PlayerActor(player);

        /*
        Weapon dg = new Weapon((Weapon)items.get("Godsword"));
        Armor am = new Armor((Armor)items.get("Cloth Armor"));
        RangedWeapon sl = new RangedWeapon((RangedWeapon)items.get("Sling"));
        PointSpell spl = new PointSpell((PointSpell)spells.get("Deal Damage (Point)"));
        PointSpell spl2 = new LineSpell((LineSpell)spells.get("Deal Damage (Line)"));
        PointSpell spl3 = new AOESpell((AOESpell)spells.get("Deal Damage (Area)"));
        PointSpell spl4 = new SplashSpell((SplashSpell)spells.get("Deal Damage (Splash)"));
        PointSpell spl5 = new LineSpell((LineSpell)spells.get("Obliterate Walls (Line)"));
        PointSpell spl6 = new AOESpell((AOESpell)spells.get("Obliterate Walls (Area)"));
        PointSpell spl7 = new AOESpell((AOESpell)spells.get("Explosion (Area)"));
        PointSpell spl8 = new PointSpell((PointSpell)spells.get("Magic Map"));
        PointSpell spl9 = new PointSpell((PointSpell)spells.get("Murder"));
        PointSpell spl10 = new AOESpell((AOESpell)spells.get("Extravision"));
        PointSpell spl11 = new PointSpell((PointSpell) spells.get("Light Switch"));
        PointSpell spl12 = new MassSpell((MassSpell) spells.get("Mass Murder"));
        PointSpell spl13 = new MassSpell((MassSpell) spells.get("Mass Extravision"));

        player.getInventory().add(dg);
        player.getInventory().add(am);
        player.getInventory().add(sl);
        player.equip(dg);
        player.equip(am);
        player.equip(sl);
        player.addSpell(spl);
        player.addSpell(spl2);
        player.addSpell(spl3);
        player.addSpell(spl4);
        player.addSpell(spl5);
        player.addSpell(spl6);
        player.addSpell(spl7);
        player.addSpell(spl8);
        player.addSpell(spl9);
        player.addSpell(spl10);
        player.addSpell(spl11);
        player.addSpell(spl12);
        player.addSpell(spl13);

         */

        return player;

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

    public static Spell newSpell(String s) {
        if(spells.get(s) instanceof PointSpell) {
            if(spells.get(s) instanceof AOESpell) {
                if(spells.get(s) instanceof SplashSpell) {
                    return new SplashSpell((SplashSpell)spells.get(s));
                }
                else {
                    return new AOESpell((AOESpell) spells.get(s));
                }
            }
            else if(spells.get(s) instanceof LineSpell) {
                return new LineSpell((LineSpell) spells.get(s));
            }
            else if(spells.get(s) instanceof MassSpell) {
                return new MassSpell((MassSpell)spells.get(s));
            }
            else {
                return new PointSpell((PointSpell) spells.get(s));
            }
        }
        else {
            return spells.get(s).copyOf(spells.get(s));
        }
    }

    public static Creature newCreature(String s) {
        return new Creature(creatures.get(s));
    }

    public static void stockCreature(Creature c, int level, Random r) {
        String key = c.getName() + "_" + level;

        if(!default_inventories.containsKey(key)) {
            key = Utility.findMostSimilarKey(default_inventories, key);
        }

        /*
         * If there's a pool to draw from, do so
         */
        if(key != null && !default_inventories.get(key).isEmpty()) {
            /*
             * Choose a random potential inventory
             */
            ArrayList<String> items = default_inventories.get(key).get(default_inventories.get(key).size() == 1? 0 : r.nextInt(default_inventories.get(key).size() - 1));

            /*
             * Add all items to inventory, and equip them if there is nothing better to equip
             */
            for(String s : items) {
                Item i = newItem(s);
                i.assignCaster(c);

                if(i instanceof Equipable) {
                    if(i instanceof Ammo) {
                        Ammo a = new Ammo((Ammo)i);
                        a.setCount(r.nextInt(11) + 1);

                        c.getInventory().add(a);

                        if(c.getQuiver() == null) {
                            if(c.getRangedWeapon() == null || a.hasProperty(c.getRangedWeapon().getAmmoType())) {
                                c.equip(a, false, false);
                            }
                        }
                        else {
                            if(c.getRangedWeapon() == null || a.hasProperty(c.getRangedWeapon().getAmmoType())) {
                                if(a.getAmmoDamage().getAverage() > c.getQuiver().getAmmoDamage().getAverage())
                                    c.equip(a, false, false);
                            }
                        }
                    }
                    else if(i instanceof Armor) {
                        Armor a = new Armor((Armor)i);
                        c.getInventory().add(a);

                        if(a.hasProperty("body")) {
                            if(c.getBody() == null || a.getArmor() > c.getBody().getArmor()) {
                                c.equip(a, false, false);
                            }
                        }
                    }
                    else if(i instanceof Weapon) {
                        if(i instanceof RangedWeapon) {
                            RangedWeapon a = new RangedWeapon((RangedWeapon)i);
                            c.getInventory().add(a);

                            if(c.getRangedWeapon() == null) {
                                if(c.getQuiver() == null || c.getQuiver().hasProperty(a.getAmmoType())) {
                                    c.equip(a, false, false);
                                }
                            }
                            else {
                                if(c.getQuiver() == null) {
                                    if(a.getWeaponDamage().getAverage() > c.getRangedWeapon().getWeaponDamage().getAverage())
                                        c.equip(a, false, false);
                                }
                                else {
                                    if(c.getQuiver().hasProperty(a.getAmmoType()) && a.getWeaponDamage().getAverage() > c.getRangedWeapon().getWeaponDamage().getAverage()) {
                                        c.equip(a, false, false);
                                    }
                                }

                            }
                        }
                        else {
                            Weapon a = new Weapon((Weapon)i);
                            c.getInventory().add(a);

                            if(a.hasProperty("main hand")) {
                                if(c.getMainHand() == null || a.getWeaponDamage().getAverage() > c.getMainHand().getWeaponDamage().getAverage()) {
                                    c.equip(a, false, false);
                                }
                            }
                        }
                    }
                    else {
                        Equipable a = new Equipable((Equipable) i);
                        c.getInventory().add(a);
                    }
                }
                else if(i instanceof Potion) {
                    Potion a = new Potion((Potion)i);
                    c.getInventory().add(a);
                }
                else if(i instanceof Food) {
                    Food a = new Food(i);
                    c.getInventory().add(a);
                }
                else {
                    Item a = new Item(i);
                    c.getInventory().add(a);
                }
            }
        }

        if(!default_spellbooks.containsKey(key)) {
            key = Utility.findMostSimilarKey(default_spellbooks, key);
        }

        /*
         * If there's a pool to draw from, do so
         */
        if(key != null && !default_spellbooks.get(key).isEmpty()) {

            /*
             * Choose a random potential spellbook
             */
            ArrayList<String> spells = default_spellbooks.get(key).get(default_spellbooks.get(key).size() == 1? 0 : r.nextInt(default_spellbooks.get(key).size() - 1));

            /*
             * Add all items to spellbook
             */
            for(String s : spells) {
                c.addSpell(newSpell(s));
            }
        }

    }

    public static Thing newThing(String s) {
        return new Thing(things.get(s));
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

    public static ThingBehavior newBehavior(String s) {
        return behaviors.get(s).copy();
    }

    public static ThingBehavior newBehavior(String s, Thing t) {
        return behaviors.get(s).copy().setThing(t);
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
            put("Player", new Player(10, 60, 200, 0,
                    12, 14, 12, 8, 14, 10, 10,
                    null, "data/Player.png",
                    "Player", '@', 0,
                    new Weapon("Fist", new Damage(1, 4, 0), 0), 1, 1f));

        }
    };
    public static final HashMap<String, Creature> creatures = new HashMap<String, Creature>() {
        {
            put("Bat", new Creature(5, 35, 0, 3,
                    6, 14, 8, 18, 10, 0, 0,
                    null, "data/Bat.png",
                    "Bat", 'b',
                    1,
                    new Weapon("Claw", new Damage(1, 4, 0), 1), 0, .5f,
                    "Animal", "Flying"));
            put("Fungus", new Creature(3, 80, 0, 1,
                    6, 8, 12, 2, 2, 0, 0,
                    null, "data/Fungus.png",
                    "Fungus", 'f',
                    1,
                    new Weapon("Spore", new Damage(1, 4, 0), 0), 0, 2f,
                    "Fungus", "Stationary"));
            put("Goblin", new Creature(8, 60, 0, 8,
                    4, 6, 6, 14, 8, 8, 6,
                    null, "data/Goblin.png",
                    "Goblin", 'g',
                    1,
                    new Weapon("Claw", new Damage(1, 4, 1), 0), 1, 1f));
            put("Looter", new Creature(10, 65, 0, 10,
                    10, 10, 10, 10, 8, 8, 10,
                    null, "data/Villager.png",
                    "Looter", 'l',
                    1,
                    new Weapon("Fist", new Damage(1, 4, 1), 0), 0, 1f,
                    "Abandoned"));
            put("Villager", new Creature(3, 80, 0, 1,
                    8, 8, 8, 8, 8, 8, 8,
                    null, "data/Villager.png",
                    "Villager", 'v',
                    0,
                    new Weapon("Fist", new Damage(1, 4, 0), 0), 1, 1f));
            put("Zombie", new Creature(7, 35, 0, 5,
                    10, 6, 10, 8, 1, 0, 0,
                    null, "data/Zombie.png",
                    "Zombie", 'z',
                    1,
                    new Weapon("Fist", new Damage(1, 4, 1), 0), 2, 1.5f,
                    "Undead"));
        }
    };

    public static final HashMap<String, ArrayList<ArrayList<String>>> default_inventories = new HashMap<String, ArrayList<ArrayList<String>>>() {
        {
            put("MONSTERNAME_DANGERLEVEL", new ArrayList<ArrayList<String>>() {
                {
                    /*
                     * Potential inventories the monster can have at the danger level
                     */
                    add(new ArrayList<String>() {
                        {
                            //Add item names here
                        }
                    });
                }
            });
            put("Player_1", new ArrayList<ArrayList<String>>() {
                {
                    /*
                     * Potential inventories the monster can have at the danger level
                     */
                    add(new ArrayList<String>() {
                        {
                            add("Dagger");
                            add("Cloth Armor");
                            add("Godsword");
                            add("Health Potion");
                            add("Poison Potion");
                        }
                    });
                }
            });
            put("Bat_1", new ArrayList<ArrayList<String>>() {
                {
                    /*
                     * Potential inventories the monster can have at the danger level
                     */
                    add(new ArrayList<String>() {
                        {
                            //Add item names here
                        }
                    });
                }
            });
            put("Fungus_1", new ArrayList<ArrayList<String>>() {
                {
                    /*
                     * Potential inventories the monster can have at the danger level
                     */
                    add(new ArrayList<String>() {
                        {
                            //Add item names here
                        }
                    });
                }
            });
            put("Goblin_1", new ArrayList<ArrayList<String>>() {
                {
                    /*
                     * Potential inventories the monster can have at the danger level
                     */
                    add(new ArrayList<String>() {
                        {
                            add("Rock");
                            add("Dagger");
                            add("Sling");
                        }
                    });

                    add(new ArrayList<String>() {
                        {
                            add("Dagger");
                            add("Health Potion");
                            add("Cloth Armor");
                        }
                    });
                }
            });
            put("Looter_1", new ArrayList<ArrayList<String>>() {
                {
                    /*
                     * Potential inventories the monster can have at the danger level
                     */
                    add(new ArrayList<String>() {
                        {
                            add("Dagger");
                            add("Shortbow");
                            add("Arrow");
                            add("Cloth Armor");
                        }
                    });
                    add(new ArrayList<String>() {
                        {
                            add("Longsword");
                            add("Leather Armor");
                        }
                    });
                }
            });
            put("Villager_1", new ArrayList<ArrayList<String>>() {
                {
                    /*
                     * Potential inventories the monster can have at the danger level
                     */
                    add(new ArrayList<String>() {
                        {
                            add("Dagger");
                            add("Cloth Armor");
                        }
                    });
                }
            });
            put("Zombie_1", new ArrayList<ArrayList<String>>() {
                {
                    /*
                     * Potential inventories the monster can have at the danger level
                     */
                    add(new ArrayList<String>() {
                        {
                            add("Rock");
                        }
                    });
                }
            });

        }
    };
    public static final HashMap<String, ArrayList<ArrayList<String>>> default_spellbooks = new HashMap<String, ArrayList<ArrayList<String>>>() {
        {
            put("MONSTERNAME_DANGERLEVEL", new ArrayList<ArrayList<String>>() {
                {
                    /*
                     * Potential spellbooks the monster can have at the danger level
                     */
                    add(new ArrayList<String>() {
                        {
                            //Add spell names here
                        }
                    });
                }
            });
            put("Player_1", new ArrayList<ArrayList<String>>() {
                {
                    /*
                     * Potential spellbooks the monster can have at the danger level
                     */
                    add(new ArrayList<String>() {
                        {
                            add("Deal Damage (Point)");
                            add("Deal Damage (Line)");
                            add("Deal Damage (Area)");
                            add("Deal Damage (Splash)");
                            add("Obliterate Walls (Line)");
                            add("Obliterate Walls (Area)");
                            add("Explosion (Area)");
                            add("Magic Map");
                            add("Murder");
                            add("Extravision");
                            add("Light Switch");
                            add("Mass Murder");
                            add("Mass Extravision");
                        }
                    });
                }
            });
            put("Bat_1", new ArrayList<ArrayList<String>>() {
                {
                    /*
                     * Potential spellbooks the monster can have at the danger level
                     */
                    add(new ArrayList<String>() {
                        {
                            //Add spell names here
                        }
                    });
                }
            });
            put("Fungus_1", new ArrayList<ArrayList<String>>() {
                {
                    /*
                     * Potential spellbooks the monster can have at the danger level
                     */
                    add(new ArrayList<String>() {
                        {
                            //Add spell names here
                        }
                    });
                }
            });
            put("Goblin_1", new ArrayList<ArrayList<String>>() {
                {
                    /*
                     * Potential spellbooks the monster can have at the danger level
                     */
                    add(new ArrayList<String>() {
                        {
                            //Add spell names here
                        }
                    });
                }
            });
            put("Looter_1", new ArrayList<ArrayList<String>>() {
                {
                    /*
                     * Potential spellbooks the monster can have at the danger level
                     */
                    add(new ArrayList<String>() {
                        {
                            //Add spell names here
                        }
                    });
                }
            });
            put("Villager_1", new ArrayList<ArrayList<String>>() {
                {
                    /*
                     * Potential spellbooks the monster can have at the danger level
                     */
                    add(new ArrayList<String>() {
                        {
                            //Add spell names here
                        }
                    });
                }
            });
            put("Zombie_1", new ArrayList<ArrayList<String>>() {
                {
                    /*
                     * Potential spellbooks the monster can have at the danger level
                     */
                    add(new ArrayList<String>() {
                        {
                            //Add spell names here
                        }
                    });
                }
            });
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
            put("Looter", new LooterAi(null, 10));
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
            put("Rock", new Ammo(',', "data/Rock.png", "Rock", 1, new Damage(1, 2, 0, "Bludgeon"), new Damage(1, 2, 0, "Bludgeon"), "throw", "stack", "bullet"));
            put("Longsword", new Weapon('\\', "data/Weapon.png", "Longsword", 10, new Damage(1, 8, 0, "Slash"), 0, "main hand"));
            put("Godsword", new Weapon('\\', "data/Weapon.png", "Godsword", 1000000, new Damage(3, 1000, 10000, "Slash"), new Spell[]{}, null,
                    new PointSpell("", true, false, false, 2, 0, false,
                            new HealthEffect(6, new Damage(10, 100, 100, "Poison"), false)), .8d, 100, 2, "main hand"));
            put("Dagger", new Weapon('\\', "data/Dagger.png", "Dagger", 5, new Damage(1, 4, 0, "Pierce"), 1, "main hand"));
            put("Shortbow", new RangedWeapon(')', "data/RangedWeapon.png", "Shortbow", 5, new Damage(1, 6, 0), 1, 6, "arrow"));
            put("Arrow", new Ammo('^', "data/Arrow.png", "Arrow", 1, new Damage(1, 3, 0, "Pierce"), "arrow", "stack"));
            put("Sling", new RangedWeapon(')', "data/Sling.png", "Sling", 2, new Damage(1, 4, 0), -1, 5, "bullet"));
            put("Leather Armor", new Armor('&', "data/Leather Armor.png", "Leather Armor", 5, 2, "body"));
            put("Cloth Armor", new Armor('&', "data/Cloth Armor.png", "Cloth Armor", 2, 1, "body"));
            put("Health Potion", new Potion('!', "data/Potion.png", "Health Potion", 20, new Spell[]{},
                    new PointSpell("", true, false, false, 1, 0, false,
                            new HealthEffect(0, new Damage(1, 6, 1), true))));
            put("Regeneration Potion", new Potion('!', "data/Potion.png", "Regeneration Potion", 25, new Spell[]{},
                    new PointSpell("", true, false, false, 1, 0, false,
                            new HealthEffect(8, new Damage(0, 1, 1), true))));
            put("Poison Potion", new Potion('!', "data/Potion.png", "Poison Potion", 10, new Spell[]{},
                    new PointSpell("", true, false, false, 1, 0, false,
                            new HealthEffect(6, new Damage(0, 1, 1, "Poison"), false))));
            put("Heroism Potion", new Potion('!', "data/Potion.png", "Heroism Potion", 25, new Spell[]{},
                    new PointSpell("", true, false, false, 1, 0, false,
                            new StatusEffect(new int[]{0, 0, 0, 0, 0, 0, 0, 0, 5, 5, 0}, true, 10))));
        }
    };
    public static final HashMap<String, Thing> things = new HashMap<String, Thing>() {
        {
            put("Door", new Thing(Tile.DOOR));
        }
    };
    public static final HashMap<String, ThingBehavior> behaviors = new HashMap<String, ThingBehavior>() {
        {
            put("Door", new DoorBehavior(null));
        }
    };

    public static final HashMap<String, Spell> spells = new HashMap<String, Spell>() {
        {
            put("Deal Damage (Point)",
                    new PointSpell("Deal Damage (Point)", true,false, false,5, 100, true,
                            new HealthEffect(0, new Damage(1, 6, 1), false)));
            put("Deal Damage (Line)",
                    new LineSpell("Deal Damage (Line)", true,true, false,false, 5, 125, true,
                            new HealthEffect(0, new Damage(1, 6, 1), false)));
            put("Deal Damage (Area)",
                    new AOESpell("Deal Damage (Area)", true, false, false, 5, 3, 150, true,
                            new HealthEffect(0, new Damage(1, 6, 1), false)));
            put("Deal Damage (Splash)",
                    new SplashSpell("Deal Damage (Splash)", true, false, false, 5, 5, 150, true,
                            new HealthEffect(0, new Damage(1, 6, 1), false)));
            put("Obliterate Walls (Line)",
                    new LineSpell("Obliterate Wall (Line)", false, false,true, false, 7, 125, true,
                            new ObliterateWall()));
            put("Obliterate Walls (Area)",
                    new AOESpell("Obliterate Wall (Area)", false, true, false, 7, 4, 150, true,
                            new ObliterateWall()));
            put("Explosion (Area)",
                    new AOESpell("Explosion (Area)", false, false, true, 7, 4, 200, true,
                            new ObliterateWall(), new HealthEffect(0, new Damage(2, 8, 2), false)));
            put("Magic Map",
                    new PointSpell("Magic Map", true, false, false, 0, 500, true,
                            new MagicMapping()));
            put("Murder",
                    new PointSpell("Murder", true, false, true, 100, 100, true,
                            new HealthEffect(0, new Damage(1, 10, 1_000_000), false)));
            put("Extravision",
                    new AOESpell("Extravision", true, false, true, 10, 5, 150, true,
                            new SeeMinds(35)));
            put("Light Switch",
                    new PointSpell("Light Switch", true, false, true, 0, 100, true,
                            new LightSwitch()));
            put("Mass Murder",
                    new MassSpell("Mass Murder", true, 500, true,
                            new HealthEffect(0, new Damage(1, 10, 1_000_000), false)));
            put("Mass Extravision",
                    new MassSpell("Mass Extravision", true, 10, true,
                            new SeeMinds(200)));
        }
    };
}
