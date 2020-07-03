package creatureitem.item;

public class Inventory {

    private Item[] items;
    private int count;

    public Inventory() {
        items = new Item[1];
        count = 0;
    }

    public void add(Item item) {
        for(int i = 0; i < items.length; i++) {
            if(items[i] == null) {
                items[i] = item;
                count++;
                break;
            }
        }

        if(count == items.length)
            doubleInventory();
    }

    public void remove(Item item) {
        for(int i = 0; i < items.length; i++) {
            if(items[i] == item) {
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
}
