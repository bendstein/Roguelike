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

        if(item == null) return;

        int j = containsEquivalent(item);

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
            int i = containsEquivalent(item);
            if(i == -1) return false;
            int countOriginal = items[i].count;
            items[i].decrementCount(1);
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
                item.assignCaster(null);
                count--;
                return;
            }
        }
    }

    public void remove(int i) {
        if(i >= count) return;
        remove(items[i]);
    }

    public boolean removeOne(int i) {
        if(i >= count) return false;
        return removeOne(items[i]);
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

    public int containsEquivalent(Item i) {
        for(int j = 0; j < items.length; j++) {
            if(items[j] == null) continue;
            if(items[j].equals(i) || items[j].equivalent(i))
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

    public Item top() {
        if(isEmpty()) return null;
        return items[count - 1];
    }

    public Item pop() {
        Item i = top();
        remove(i);
        return i;
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
        ArrayList<Item> it = new ArrayList<>();

        for(Item i : asList()) {
            if(i == null) continue;
            if(i.rarity <= raritymax && i.rarity >= raritymin)
                it.add(i);
        }

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
                .filter(i -> (i != null && i.getWorth() <= worthmax && i.getWorth() >= worthmin))
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

    public Item[] filterProperty(String ... properties) {
        ArrayList<Item> it = new ArrayList<>(asList()
                .stream()
                .filter(i -> (i != null && i.hasProperty(properties)))
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