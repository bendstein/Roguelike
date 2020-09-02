package creatureitem.effect.damage;

public enum Damage_Type {
    FIRE("Fire"), WATER("Water"), EARTH("Earth"), AIR("Air"),
    ACID("Acid"), LIGHT("Light"), DARK("Dark"), ICE("Ice"),
    ENERGY("Energy"), POISON("Poison"),
    BLUDGEON("Bludgeon"), PIERCE("Pierce"), SLASH("Slash"),
    UNTYPED("Untyped");

    private String name;

    Damage_Type(String name) {
        this.name = name;
    }

    //<editor-fold desc="Getters and Setters">
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static Damage_Type getType(String name) {
        Damage_Type d = FIRE;
        for(Damage_Type dt : Damage_Type.values()) {
            if(dt.getName().equals(name)) {
                d = dt;
                break;
            }
        }

        return d;
    }
    //</editor-fold>
}
