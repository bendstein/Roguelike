package creatureitem;

import world.Level;
import world.geometry.Point;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class Entity {

    protected int x, y;

    protected ArrayList<String> properties;

    protected int energy_used;

    protected int current_energy;

    protected boolean can_act;

    protected float energy_factor;

    protected long lastActTime;

    public Entity() {
        x = y = 0;
        current_energy = energy_used = 0;
        can_act = true;
        properties = new ArrayList<>();
        energy_factor = 1;
        lastActTime = System.currentTimeMillis();
    }

    public Entity(int x, int y, boolean can_act, String ... properties) {
        this.x = x;
        this.y = y;
        this.energy_used = 0;
        this.can_act = can_act;
        this.properties = new ArrayList<>(Arrays.asList(properties));
        energy_factor = 1;
        lastActTime = System.currentTimeMillis();
    }

    public Entity(Entity entity) {
        this.x = entity.x;
        this.y = entity.y;
        this.current_energy = entity.current_energy;
        this.energy_used = entity.energy_used;
        this.can_act = entity.can_act;
        this.properties = new ArrayList<>(entity.properties);
        this.energy_factor = entity.energy_factor;
        lastActTime = entity.lastActTime;
    }

    //<editor-fold desc="Getters and Setters">

    public long getLastActTime() {
        return lastActTime;
    }

    public void setLastActTime(long lastActTime) {
        this.lastActTime = lastActTime;
    }

    public float getEnergy_factor() {
        return energy_factor;
    }

    public void setEnergy_factor(float energy_factor) {
        this.energy_factor = energy_factor;
    }

    public boolean isCan_act() {
        return can_act;
    }

    public void setCan_act(boolean can_act) {
        this.can_act = can_act;
    }

    public int getCurrent_energy() {
        return current_energy;
    }

    public void setCurrent_energy(int current_energy) {
        this.current_energy = current_energy;
    }

    public int getEnergy_used() {
        return energy_used;
    }

    public void setEnergy_used(int energy_used) {
        this.energy_used = energy_used;
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

    public Point getLocation() {
        return new Point(x, y);
    }

    public void setLocation(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void setLocation(Point p) {
        this.x = p.getX();
        this.y = p.getY();
    }

    public ArrayList<String> getProperties() {
        return properties;
    }

    public void setProperties(ArrayList<String> properties) {
        this.properties = properties;
    }

    public void addProperty(String s, String ... properties) {
        this.properties.add(s);
        this.properties.addAll(Arrays.asList(properties));
    }

    public boolean hasProperty(String s) {
        return properties.contains(s);
    }

    //</editor-fold>

    public void act(Level l) {
        spendEnergy(Level.getEnergyPerTurn());
        process(l);
    }

    public void process(Level l) {
        //commitEnergy();
        if(!l.getTurnQueue().contains(this))
            l.addToTurnQueue(this);
        l.nextTurn();
    }

    /**
     * Add used energy to current energy usage
     */
    public void commitEnergy() {
        current_energy += (int) Math.ceil(energy_used * energy_factor);
        energy_used = 0;
    }

    public void spendEnergy(int amount) {
        energy_used = Math.max(0, energy_used + amount);
        lastActTime = System.currentTimeMillis();
    }

    @Override
    public boolean equals(Object o) {
        return this == o;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, properties, energy_used, current_energy, can_act, energy_factor);
    }
}
