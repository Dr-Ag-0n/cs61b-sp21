package deque;

import java.util.Iterator;

public class ArrayDeque<T> implements Deque<T>, Iterable<T> {
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

    @Override
    public void addFirst(T item) {
        if (size == items.length - 1) {
            resize(items.length * 2);
        }
        items[nextFirst] = item;
        nextFirst = (nextFirst - 1 + items.length) % items.length;
        size++;
    }

    @Override
    public void addLast(T item) {
        if (size == items.length - 1) {
            resize(items.length * 2);
        }
        items[nextLast] = item;
        nextLast = (nextLast + 1) % items.length;
        size++;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void printDeque() {
        int p = (nextFirst + 1) % items.length;
        while (p != nextLast) {
            System.out.println(items[p]);
            p = (p + 1) % items.length;
        }
    }

    @Override
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

    @Override
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

    @Override
    public T get(int index) {
        if (index >= size) {
            return null;
        }
        return items[(nextFirst + 1 + index) % items.length];
    }

    public Iterator<T> iterator() {
        return new DequeIterator();
    }

    private class DequeIterator implements Iterator<T> {
        private int wizPos;

        DequeIterator() {
            wizPos = 0;
        }

        @Override
        public boolean hasNext() {
            return wizPos < size();
        }

        @Override
        public T next() {
            T returnItem = get(wizPos);
            wizPos += 1;
            return returnItem;
        }
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (!(o instanceof Deque)) {
            return false;
        }
        Deque<T> other = (Deque<T>) o;
        if (this.size() != other.size()) {
            return false;
        }
        for (int i = 0; i < this.size(); i++) {
            if (!this.get(i).equals(other.get(i))) {
                return false;
            }
        }
        return true;
    }
}
