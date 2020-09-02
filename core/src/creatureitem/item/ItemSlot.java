package creatureitem.item;

import creatureitem.item.behavior.equipable.Slot;

import java.util.Objects;

public class ItemSlot {

    /**
     * The item which is in this slot
     */
    private Item i;

    /**
     * The type of slot this is
     */
    private Slot slot;

    /**
     * 0 - Slot is empty
     * 1 - Slot is equipped with i
     * 2 - Slot is obstructed by i, and cannot be used
     */
    private int occupied;
    public static final int EMPTY = 0, EQUIPPED = 1, OBSTRUCTED = 2;

    /**
     * The name of this slot
     */
    private String name;

    public ItemSlot(Slot slot) {
        this.i = null;
        this.slot = slot.copy();
        this.occupied = 0;
        this.name = "";
    }

    public ItemSlot(Slot slot, String name) {
        this.i = null;
        this.slot = slot.copy();
        this.occupied = 0;
        this.name = name;
    }

    //<editor-fold desc="Getters and Setters">
    public Item getI() {
        return i;
    }

    public void setI(Item i) {
        this.i = i;
    }

    public Slot getSlot() {
        return slot;
    }

    public void setSlot(Slot slot) {
        this.slot = slot;
    }

    public int getOccupied() {
        return occupied;
    }

    public void setOccupied(int occupied) {
        this.occupied = occupied;
    }

    public boolean isEmpty() {
        return occupied == EMPTY;
    }

    public boolean isEquipped() {
        return occupied == EQUIPPED;
    }

    public boolean isObstructed() {
        return occupied == OBSTRUCTED;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    //</editor-fold>

    public void unequip() {
        this.i = null;
        this.occupied = 0;
    }

    public void equip(Item i) {
        if(i == null) return;
        if(this.i != null) this.i.unequip();

        this.i = i;
        i.setEquippedSlot(this);
        this.occupied = EQUIPPED;
    }

    public void obstruct(Item i) {
        if(i == null) return;
        if(this.i != null) this.i.unequip();

        this.i = i;
        i.getObstructing().add(this);
        this.occupied = OBSTRUCTED;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ItemSlot)) return false;
        ItemSlot itemSlot = (ItemSlot) o;
        return occupied == itemSlot.occupied &&
                Objects.equals(i, itemSlot.i) &&
                Objects.equals(slot, itemSlot.slot) &&
                Objects.equals(name, itemSlot.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(i, slot, occupied, name);
    }

    public ItemSlot copy() {
        ItemSlot it = new ItemSlot(slot, name);
        it.setI(i);
        it.setOccupied(occupied);
        return it;
    }
}
