package creatureitem.item.behavior.equipable;

import java.util.Objects;

public class Slot {

    /**
     * The equipment slot
     */
    private int slot;

    /**
     * The quantity of said slot that it occupies.
     */
    private int count;

    /**
     * HELD: The equipment can be held in the creature's hand.
     * RANGED: The equipment can be held in the ranged weapon slot.
     * QUIVER: The equipment can be held in the creature's quiver.
     * FACE: The equipment can be held on the creature's face.
     * HEAD: The equipment can be held on the creature's head.
     * BODY: The equipment can be held on the creature's torso.
     * FINGERS: The equipment can be held on the creature's fingers.
     * HANDS: The equipment can be held on the creature's hands.
     * KNECK: The equipment can be held on the creature's kneck.
     * CLOAK: The equipment can be held in the creature's cloak slot.
     * WRIST: The equipment can be held on the creature's wrist.
     * FEET: The equipment can be held on the creature's feet.
     * WAIST: The equipment can be held on the creature's waist.
     */
    public static final int HELD = 0, RANGED = 1, QUIVER = 2, FACE = 3, HEAD = 4, BODY = 5, FINGERS = 6, HANDS = 7,
            KNECK = 8, CLOAK = 9, WRIST = 10, FEET = 11, WAIST = 12;

    public Slot(int slot) {
        this.slot = slot;
        count = 1;
    }

    public Slot(int slot, int count) {
        this.slot = slot;
        this.count = count;
    }

    //<editor-fold desc="Getters and Setters">
    public int getSlot() {
        return slot;
    }

    public void setSlot(int slot) {
        this.slot = slot;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
    //</editor-fold>

    public Slot copy() {
        return new Slot(slot, count);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Slot)) return false;
        Slot slot1 = (Slot) o;
        return slot == slot1.slot &&
                count == slot1.count;
    }

    @Override
    public int hashCode() {
        return Objects.hash(slot, count);
    }

    public String slotToString() {
        switch (slot) {
            case HELD: return "held";
            case RANGED: return "ranged";
            case QUIVER: return "ammo";
            case FACE: return "face";
            case HEAD: return "head";
            case BODY: return "body";
            case FINGERS: return "fingers";
            case HANDS: return "hands";
            case KNECK: return "kneck";
            case CLOAK: return "cloak";
            case WRIST: return "wrist";
            case FEET: return "feet";
            case WAIST: return "waist";
        }

        return "";
    }
}
