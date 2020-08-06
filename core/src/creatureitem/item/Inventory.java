package creatureitem.item;

import world.geometry.Point;

import java.util.*;
import java.util.stream.Collectors;

public class Inventory implements Iterable<Item>  {

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

    public void addAll(Item[] toAdd) {
        for(Item i : toAdd)
            add(i);
    }

    public void addAll(Inventory i) {
        if(i == null) return;
        addAll(i.items);
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
                count--;
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

    public boolean isEmpty() {
        return count == 0;
    }

    public Inventory prune() {
        HashSet<Item> pruned = new HashSet<>(asList()
                .stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toList()));

        items = new Item[1];
        count = 0;

        for(Item i : pruned)
            add(i);

        return this;
    }

    public static Inventory combine(Inventory i, Inventory i2) {
        Inventory newInventory = new Inventory();
        newInventory.addAll(i.items);
        newInventory.addAll(i2.items);
        return newInventory;
    }

    public Item[] filterRarity(double raritymin, double raritymax) {
        ArrayList<Item> it = new ArrayList<Item>(asList()
                .stream()
                .filter(i -> (i != null && i.rarity <= raritymax && i.rarity >= raritymin))
                .collect(Collectors.toList()));
        Item[] items = new Item[it.size()];

        for(Item item : it) {
            for(int i = 0; i < items.length; i++)
                if(items[i] == null) {
                    items[i] = item;
                    break;
                }
        }

        return items;
    }

    public Item[] filterWorth(int worthmin, int worthmax) {
        ArrayList<Item> it = new ArrayList<>(asList()
                .stream()
                .filter(i -> (i != null && i.worth <= worthmax && i.worth >= worthmin))
                .collect(Collectors.toList()));

        Item[] items = new Item[it.size()];

        for(Item item : it) {
            for(int i = 0; i < items.length; i++)
                if(items[i] == null) {
                    items[i] = item;
                    break;
                }
        }

        return items;
    }

    @Override
    public Iterator<Item> iterator() {
        return Arrays.asList(items).iterator();
    }

}
