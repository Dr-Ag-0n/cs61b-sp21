package deque;

import java.util.Iterator;

public class LinkedListDeque<T> implements Deque<T>, Iterable<T> {
    private class Node {
        T item;
        Node next;
        Node previous;

        Node(T i, Node p, Node n) {
            item = i;
            previous = p;
            next = n;
        }
    }

    private int size;
    private Node sentinel;

    public LinkedListDeque() {
        sentinel = new Node(null, null, null);
        sentinel.previous = sentinel;
        sentinel.next = sentinel;
        size = 0;
    }

    @Override
    public void addFirst(T item) {
        Node newNode = new Node(item, sentinel, sentinel.next);
        sentinel.next.previous = newNode;
        sentinel.next = newNode;
        size++;
    }

    @Override
    public void addLast(T item) {
        Node newNode = new Node(item, sentinel.previous, sentinel);
        sentinel.previous.next = newNode;
        sentinel.previous = newNode;
        size++;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void printDeque() {
        for (Node p = sentinel.next; p.item != null; p = p.next) {
            System.out.println(p.item + " ");
        }
        System.out.println();
    }

    @Override
    public T removeFirst() {
        T returnItem;
        if (size > 0) {
            returnItem = sentinel.next.item;
            sentinel.next = sentinel.next.next;
            sentinel.next.previous = sentinel;
            size--;
        } else {
            returnItem = null;
        }
        return returnItem;
    }

    @Override
    public T removeLast() {
        T returnItem;
        if (size > 0) {
            returnItem = sentinel.previous.item;
            sentinel.previous = sentinel.previous.previous;
            sentinel.previous.next = sentinel;
            size--;
        } else {
            returnItem = null;
        }
        return returnItem;
    }

    @Override
    public T get(int index) {
        Node p = sentinel.next;
        for (int i = 0; i < index; i++) {
            p = p.next;
        }
        return p.item;
    }

    public T getRecursive(int index) {
        return getRecursivehelper(sentinel.next, index);
    }

    private T getRecursivehelper(Node p, int index) {
        if (index == 0) {
            return p.item;
        } else {
            return getRecursivehelper(p.next, index - 1);
        }
    }

    public Iterator<T> iterator() {
        return new DequeIterator();
    }

    private class DequeIterator implements Iterator<T> {
        private int wizPos;

        public DequeIterator() {
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
        LinkedListDeque<T> other = (LinkedListDeque<T>) o;
        if (this.size() != other.size()) {
            return false;
        }
        Iterator<T> thisIt = this.iterator();
        Iterator<T> otherIt = other.iterator();

        while (thisIt.hasNext() && otherIt.hasNext()) {
            T itemThis = thisIt.next();
            T itemOther = otherIt.next();

            if (itemThis == null) {
                if (itemOther != null) {
                    return false;
                }
            } else if (!itemThis.equals(itemOther)) {
                return false;
            }
        }
        return true;
    }
}
