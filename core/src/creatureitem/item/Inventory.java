package creatureitem.item;

import java.util.ArrayList;
import java.util.Arrays;

public class Inventory {

    private Item[] items;
    private int count;

    public Inventory() {
        items = new Item[1];
        count = 0;
    }

    public Inventory(Inventory inventory) {
        this.items = new Item[1];
        count = 0;

        for(Item i : inventory.getItems())
            add(i);
    }

    public void add(Item item) {

        int j = contains(item);

        if(j == -1 || !item.hasProperty("stack")) {
            for(int i = 0; i < items.length; i++) {
                if(items[i] == null) {
                    items[i] = item;
                    count++;
                    break;
                }
            }
        }

        else {
            items[j].incrementCount(item.getCount());
        }

        if(count == items.length)
            doubleInventory();
    }

    public boolean removeOne(Item item) {

        if(item.hasProperty("stack") && item.getCount() >= 1) {
            if(contains(item) == -1) return false;
            int countOriginal = items[contains(item)].count;
            items[contains(item)].decrementCount(1);
            if(item.count == countOriginal) item.decrementCount(1);
        }

        if(!item.hasProperty("stack") || item.getCount() < 1) {
            remove(item);
            return true;
        }

        return false;

    }

    public void remove(Item item) {
        for(int i = 0; i < items.length; i++) {
            if(items[i] == null) continue;
            if(items[i].equals(item)) {
                items[i] = null;
                return;
            }
        }
    }

    public void doubleInventory() {
        Item[] itemsNew = new Item[items.length * 2];

        for(int i = 0; i < items.length; i++)
            itemsNew[i] = items[i];

        items = itemsNew;
    }

    public Item[] getItems() {
        return items;
    }

    public ArrayList<Item> asList() {
        return new ArrayList<>(Arrays.asList(items));
    }

    public int contains(Item i) {
        for(int j = 0; j < items.length; j++) {
            if(items[j] == null) continue;
            if(items[j].equals(i))
                return j;
        }

        return -1;
    }

    public void setItems(Item[] items) {
        this.items = items;
    }

    public int getCount() {
        return count;
    }

}
