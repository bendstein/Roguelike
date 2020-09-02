package creatureitem.item.behavior.equipable;

import creatureitem.Creature;
import creatureitem.effect.Effect;
import creatureitem.item.Item;
import creatureitem.item.behavior.ItemBehavior;

import java.util.Arrays;
import java.util.Objects;

public abstract class Equipable extends ItemBehavior {

    /**
     * Effects that the item applies when equipped
     */
    protected Effect[] onEquip;

    /**
     * True if something has equipped this item
     */
    protected boolean isEquipped;

    /**
     * The slot which the equipment occupies.
     */
    protected EquipSlot slot;

    public Equipable(Item item, EquipSlot slot) {
        super(item);
        this.onEquip = null;
        this.isEquipped = false;
        this.slot = slot.copy();
    }

    public Equipable(Item item, EquipSlot slot, Effect ... onEquip) {
        super(item);
        this.onEquip = onEquip;
        isEquipped = false;
        this.slot = slot == null? null : slot.copy();
    }

    //<editor-fold desc="Getters and Setters">
    public Effect[] getOnEquip() {
        return onEquip;
    }

    public void setOnEquip(Effect[] onEquip) {
        this.onEquip = onEquip;
    }

    public boolean isEquipped() {
        return isEquipped;
    }

    public void setEquipped(boolean equipped) {
        isEquipped = equipped;
    }

    public EquipSlot getSlot() {
        return slot;
    }

    public void setSlot(EquipSlot slot) {
        this.slot = slot;
    }

    //</editor-fold>

    @Override
    public void assignCaster(Creature c) {
        if(onEquip != null) {
            for(Effect e : onEquip)
                e.setCaster(c);
        }

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Equipable)) return false;
        Equipable equipable = (Equipable) o;
        return isEquipped == equipable.isEquipped &&
                Arrays.equals(onEquip, equipable.onEquip) &&
                Objects.equals(slot, equipable.slot);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(isEquipped, slot);
        result = 31 * result + Arrays.hashCode(onEquip);
        return result;
    }

    /**
     * Where the equipment would be equipped
     */
    public static class EquipSlot {

        /**
         * The primary slot that the equipment occupies.
         */
        private Slot mainSlot;

        /**
         * Other slots that the equipment occupies.
         */
        private Slot[] occupiedSlots;

        public EquipSlot(Slot mainSlot, Slot... occupiedSlots) {
            this.mainSlot = mainSlot == null? null : mainSlot.copy();
            if(occupiedSlots == null || occupiedSlots.length == 0) this.occupiedSlots = new Slot[0];
            else {
                this.occupiedSlots = new Slot[occupiedSlots.length];
                for(int i = 0; i < occupiedSlots.length; i++) {
                    this.occupiedSlots[i] = occupiedSlots[i] == null? null : occupiedSlots[i].copy();
                }
            }

        }

        //<editor-fold desc="Getters and Setters">
        public Slot getMainSlot() {
            return mainSlot;
        }

        public void setMainSlot(Slot mainSlot) {
            this.mainSlot = mainSlot;
        }

        public Slot[] getOccupiedSlots() {
            return occupiedSlots;
        }

        public void setOccupiedSlots(Slot[] occupiedSlots) {
            this.occupiedSlots = occupiedSlots;
        }
        //</editor-fold>

        public EquipSlot copy() {
            Slot m = mainSlot == null ? null : mainSlot.copy();
            Slot[] o = new Slot[occupiedSlots.length];
            for (int i = 0; i < occupiedSlots.length; i++)
                o[i] = occupiedSlots[i] == null? null : occupiedSlots[i].copy();

            return new EquipSlot(m, o);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof EquipSlot)) return false;
            EquipSlot that = (EquipSlot) o;
            return Objects.equals(mainSlot, that.mainSlot) &&
                    Arrays.equals(occupiedSlots, that.occupiedSlots);
        }

        @Override
        public int hashCode() {
            int result = Objects.hash(mainSlot);
            result = 31 * result + Arrays.hashCode(occupiedSlots);
            return result;
        }
    }
}
