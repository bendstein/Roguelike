package utility;

import java.util.Arrays;

public class DynamicArray<E> {

    protected E[] array;

    protected int size;

    public DynamicArray(E[] array) {
        this.array = array;
        this.size = 0;

        for(E i : array) {
            if(i != null) size++;
        }
    }

    public void add(E element) {

        for(int i = 0; i < array.length; i++) {
            if(array[i] == null) {
                array[i] = element;
                size++;
                break;
            }
        }

        if(size >= capacity()) {
            increaseCapacity();
        }

    }

    public void remove(E element) {

        for(int i = 0; i < array.length; i++) {
            if(array[i] != null && array[i].equals(element)) {
                array[i] = null;
                size--;
                break;
            }
        }

    }

    public E remove(int i) {

        E element = array[i];
        array[i] = null;
        size--;
        return element;
    }

    private void increaseCapacity() {
        array = Arrays.copyOf(array, array.length * 2);
    }

    public boolean contains(E element) {
        for(int i = 0; i < capacity(); i++) {
            if(array[i] != null && array[i].equals(element))
                return true;
        }

        return false;
    }

    public int indexOf(E element) {
        for(int i = 0; i < capacity(); i++) {
            if(array[i] != null && array[i].equals(element))
                return i;
        }

        return -1;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public E get(int i) {
        return array[i];
    }

    //<editor-fold desc="Getters and Setters">
    public E[] getArray() {
        return array;
    }

    public void setArray(E[] array) {
        this.array = array;
    }

    public int size() {
        return size;
    }

    public int capacity() {
        return array.length;
    }
    //</editor-fold>

}
