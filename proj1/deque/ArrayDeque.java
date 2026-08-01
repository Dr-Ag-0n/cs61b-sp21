package deque;

public class ArrayDeque<T> {
    private int size;
    private T[] items;
    private int nextFirst;
    private int nextLast;

    public ArrayDeque() {
        items = (T[]) new Object[8];
        size = 0;
        nextFirst = 0;
        nextLast = 1;
    }

    public void addFirst(T item) {
        if (size == items.length - 1) {
            resize(items.length * 2);
        }
        items[nextFirst] = item;
        nextFirst = (nextFirst - 1 + items.length) % items.length;
        size++;
    }

    public void addLast(T item) {
        if (size == items.length - 1) {
            resize(items.length * 2);
        }
        items[nextLast] = item;
        nextLast = (nextLast + 1) % items.length;
        size++;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int size() {
        return size;
    }

    public void printDeque() {
        int p = (nextFirst + 1) % items.length;
        while(p != nextLast){
            System.out.println(items[p]);
            p = (p + 1) % items.length;
        }
    }

    public T removeFirst() {
        T returnItem;
        if (size > 0) {
            returnItem = items[(nextFirst + 1) % items.length];
            nextFirst = (nextFirst + 1) % items.length;
            size--;
        } else {
            returnItem = null;
        }
        if (size < items.length / 4 && items.length > 8) {
            resize(items.length / 2);
        }
        return returnItem;
    }

    public T removeLast() {
        T returnItem;
        if (size > 0) {
            returnItem = items[(nextLast - 1 + items.length) % items.length];
            nextLast = (nextLast - 1 + items.length) % items.length;
            size--;
        } else {
            returnItem = null;
        }
        if (size < items.length / 4 && items.length > 8) {
            resize(items.length / 2);
        }
        return returnItem;
    }

    private void resize(int newSize) {
        T[] a = (T[]) new Object[newSize];
        int p = (nextFirst + 1) % items.length;
        int i = 1;
        while (p != nextLast) {
            a[i] = items[p];
            p = (p + 1) % items.length;
            i++;
        }
        nextFirst = 0;
        nextLast = size + 1;
        items = a;
    }

    public T get(int index) {
        if (index >= size) {
            return null;
        }
        return items[(nextFirst + 1 + index) % items.length];
    }
}
