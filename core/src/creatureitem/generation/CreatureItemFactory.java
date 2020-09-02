package creatureitem.generation;

import actors.creatures.CreatureActor;
import actors.creatures.PlayerActor;
import creatureitem.Creature;
import creatureitem.ai.CreatureAi;
import creatureitem.ai.types.NPCAi;
import creatureitem.ai.PlayerAi;
import creatureitem.ai.monster.*;
import creatureitem.effect.StatusEffect;
import creatureitem.effect.damage.Damage;
import creatureitem.effect.HealthEffect;
import creatureitem.effect.effects.LightSwitch;
import creatureitem.effect.effects.MagicMapping;
import creatureitem.effect.effects.ObliterateWall;
import creatureitem.effect.effects.SeeMinds;
import creatureitem.item.*;
import creatureitem.Player;
import creatureitem.item.behavior.Consumable;
import creatureitem.item.behavior.Launchable;
import creatureitem.item.behavior.equipable.Equipable;
import creatureitem.item.behavior.equipable.Slot;
import creatureitem.item.behavior.equipable.armor.Armor;
import creatureitem.item.behavior.equipable.weapon.Ammo;
import creatureitem.item.behavior.equipable.weapon.MeleeWeapon;
import creatureitem.item.behavior.equipable.weapon.RangedWeapon;
import creatureitem.item.builder.ItemBuilder;
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

        return player;

    }

    public static Item newItem(String s) {
        try {
            return items.get(s).copy();
        } catch (Exception e) {
            return null;
        }
    }

    public static Spell newSpell(String s) {
        if(spells.get(s) instanceof PointSpell) {
            if(spells.get(s) instanceof AOESpell) {
                if(spells.get(s) instanceof SplashSpell) {
                    return ((SplashSpell)spells.get(s)).copy();
                }
                else {
                    return ((AOESpell) spells.get(s)).copy();
                }
            }
            else if(spells.get(s) instanceof LineSpell) {
                return ((LineSpell) spells.get(s)).copy();
            }
            else if(spells.get(s) instanceof MassSpell) {
                return ((MassSpell)spells.get(s)).copy();
            }
            else {
                return ((PointSpell) spells.get(s)).copy();
            }
        }
        else {
            return spells.get(s).copy();
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

                if(i == null) continue;

                if(i.isAmmo() && i.hasProperty("stack")) i.setCount((int)((Math.random() * 20) + 1));

                c.pickUp(i, false, false);
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
            put("Player", new Player(10, 60, 0,
                    12, 14, 12, 8, 14, 10, 10,
                    null, "data/Player.png",
                    "Player", '@', 0,
                    new Item("Fist", new ItemBuilder().setMeleeComponent(new MeleeWeapon(null, new Damage(1, 4, 0), null, 1))),
                    1, 1f));

        }
    };
    public static final HashMap<String, Creature> creatures = new HashMap<String, Creature>() {
        {
            put("Bat", new Creature(5, 35, 0,
                    6, 14, 8, 18, 10, 0, 0,
                    null, "data/Bat.png",
                    "Bat", 'b',
                    1,
                    new Item("Claw", new ItemBuilder().setMeleeComponent(new MeleeWeapon(null, new Damage(1, 4, 0), null, 1))),
                    0, .5f,
                    "Animal", "Flying"));
            put("Fungus", new Creature(3, 80, 1,
                    6, 8, 12, 2, 2, 0, 0,
                    null, "data/Fungus.png",
                    "Fungus", 'f',
                    1,
                    new Item("Spore", new ItemBuilder().setMeleeComponent(new MeleeWeapon(null, new Damage(1, 4, 0), null, 1, 2))),
                    0, 2f,
                    "Fungus", "Stationary"));
            put("Goblin", new Creature(8, 60, 0,
                    4, 6, 6, 14, 8, 8, 6,
                    null, "data/Goblin.png",
                    "Goblin", 'g',
                    1,
                    new Item("Claw", new ItemBuilder().setMeleeComponent(new MeleeWeapon(null, new Damage(1, 4, 0), null, 0))),
                    1, 1f));
            put("Looter", new Creature(10, 65, 0,
                    10, 10, 10, 10, 8, 8, 10,
                    null, "data/Villager.png",
                    "Looter", 'l',
                    1,
                    new Item("Fist", new ItemBuilder().setMeleeComponent(new MeleeWeapon(null, new Damage(1, 4, 1), null, 1))),
                    0, 1f,
                    "Abandoned"));
            put("Villager", new Creature(3, 80, 0,
                    8, 8, 8, 8, 8, 8, 8,
                    null, "data/Villager.png",
                    "Villager", 'v',
                    0,
                    new Item("Fist", new ItemBuilder().setMeleeComponent(new MeleeWeapon(null, new Damage(1, 4, 0), null, 1))),
                    1, 1f));
            put("Zombie", new Creature(7, 35, 0,
                    10, 6, 10, 8, 1, 0, 0,
                    null, "data/Zombie.png",
                    "Zombie", 'z',
                    1,
                    new Item("Fist", new ItemBuilder().setMeleeComponent(new MeleeWeapon(null, new Damage(1, 4, 0), null, 1))),
                    2, 1.5f,
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
                            add("Arrow");
                            add("Shortbow");
                            add("Rock");
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
            put("Rock", new Item(',', "data/Rock.png", "Rock", "", Item.SUPER_COMMON, 1,
                    new ItemBuilder().setAmmoComponent(new Ammo(null, new Damage(1, 2, 0, "Bludgeon"), "bullet"))
                                        .setLaunchableComponent(new Launchable(null, new Damage(1, 2, 0, "Bludgeon"))),
                    "stack"));
            put("Longsword", new Item('\\', "data/Weapon.png", "Longsword", "", Item.SUPER_COMMON, 10,
                    new ItemBuilder().setMeleeComponent(new MeleeWeapon(null, new Damage(1, 8, 0, "Slash"),
                            new Equipable.EquipSlot(new Slot(Slot.HELD)), 0))));
            put("Godsword", new Item('\\', "data/Weapon.png", "Godsword", "", Item.SUPER_COMMON, 10,
                    new ItemBuilder().setMeleeComponent(new MeleeWeapon(null, new Damage(1, 10, 1000, "Slash"),
                            new Equipable.EquipSlot(new Slot(Slot.HELD), new Slot(Slot.HELD)), 100))));
            put("Dagger", new Item('\\', "data/Weapon.png", "Dagger", "", Item.SUPER_COMMON, 10,
                    new ItemBuilder().setMeleeComponent(new MeleeWeapon(null, new Damage(1, 4, 1, "Pierce"),
                            new Equipable.EquipSlot(new Slot(Slot.HELD)), 2))
                                    .setLaunchableComponent(new Launchable(null, new Damage(1, 4, 1, "Pierce")))));
            put("Shortbow", new Item(')', "data/RangedWeapon.png", "Shortbow", "", Item.SUPER_COMMON, 10,
                    new ItemBuilder().setRangedComponent(new RangedWeapon(null, new Damage(1, 6, 0), 5, 1, "arrow"))));
            put("Arrow", new Item('^', "data/Arrow.png", "Arrow", "", Item.SUPER_COMMON, 10,
                    new ItemBuilder().setAmmoComponent(new Ammo(null, new Damage(1, 4, 0, "Pierce"), "arrow")),
                    "stack"));
            put("Sling", new Item(')', "data/Sling.png", "Sling", "", Item.SUPER_COMMON, 10,
                    new ItemBuilder().setRangedComponent(new RangedWeapon(null, new Damage(1, 4, 0), 4, 1, "bullet"))));
            put("Leather Armor", new Item('&', "data/Leather Armor.png", "Leather Armor", "", Item.SUPER_COMMON, 10,
                    new ItemBuilder().setArmorComponent(new Armor(null, 5, new Equipable.EquipSlot(new Slot(Slot.BODY, 1))))));
            put("Cloth Armor", new Item('&', "data/Cloth Armor.png", "Cloth Armor", "", Item.SUPER_COMMON, 10,
                    new ItemBuilder().setArmorComponent(new Armor(null, 2, new Equipable.EquipSlot(new Slot(Slot.BODY, 1))))));
            put("Health Potion", new Item('!', "data/Potion.png", "Health Potion", "", Item.SUPER_COMMON, 10,
                    new ItemBuilder().setConsumableComponent(new Consumable(null, 0, new HealthEffect(0, new Damage(1, 6, 1), true))),
                    "stack", "shatter"));
            put("Regeneration Potion", new Item('!', "data/Potion.png", "Regeneration Potio", "", Item.SUPER_COMMON, 10,
                    new ItemBuilder().setConsumableComponent(new Consumable(null, 0, new HealthEffect(8, new Damage(0, 1, 1), true))),
                    "stack", "shatter"));
            put("Poison Potion", new Item('!', "data/Potion.png", "Poison Potion", "", Item.SUPER_COMMON, 10,
                    new ItemBuilder().setConsumableComponent(new Consumable(null, 0, new HealthEffect(6, new Damage(0, 1, 1, "Poison"), false))),
                    "stack", "shatter"));
            put("Heroism Potion", new Item('!', "data/Potion.png", "Heroism Potion", "", Item.SUPER_COMMON, 10,
                    new ItemBuilder().setConsumableComponent(new Consumable(null, 0, new StatusEffect(new int[]{0, 0, 0, 0, 0, 0, 0, 0, 5, 5, 0}, true, 10))),
                    "stack", "shatter"));
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
