package deque;

public class LinkedListDeque<T> {
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

    public void addFirst(T item) {
        Node newNode = new Node(item, sentinel, sentinel.next);
        sentinel.next.previous = newNode;
        sentinel.next = newNode;
        size++;
    }

    public void addLast(T item) {
        Node newNode = new Node(item, sentinel.previous, sentinel);
        sentinel.previous.next = newNode;
        sentinel.previous = newNode;
        size++;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int size() {
        return size;
    }

    public void printDeque() {
        for (Node p = sentinel.next; p.item != null; p = p.next) {
            System.out.println(p.item + " ");
        }
        System.out.println();
    }

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
            return getRecursivehelper(p.next, index -1);
        }
    }
}
