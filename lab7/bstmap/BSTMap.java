package bstmap;

import java.util.Iterator;
import java.util.Set;

public class BSTMap<K extends Comparable<K>, V> implements Map61B<K, V> {
    private node Item;
    private int size;
    private BSTMap left;
    private BSTMap right;

    private class node {
        K key;
        V value;

        public node() {
            key = null;
            value = null;
        }

        public node(K k, V v) {
            key = k;
            value = v;
        }
    }

    public BSTMap() {
        Item = new node();
        left = null;
        right = null;
        size = 0;
    }

    @Override
    public void clear() {
        Item = new node();
        left = null;
        right = null;
        size = 0;
    }

    @Override
    public boolean containsKey(K key) {
        if (Item.key != null) {
            if (key.equals(Item.key)) {
                return true;
            }
            if (key.compareTo(Item.key) < 0) {
                if (left != null) {
                    return left.containsKey(key);
                }
            } else {
                if (right != null) {
                    return right.containsKey(key);
                }
            }
        }
        return false;
    }

    @Override
    public V get(K key) {
        if (Item.key != null) {
            if (key.equals(Item.key)) {
                return Item.value;
            }
            if (key.compareTo(Item.key) < 0) {
                if (left != null) {
                    return (V) left.get(key);
                }
            } else {
                if (right != null) {
                    return (V) right.get(key);
                }
            }
        }
        return null;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void put(K key, V value) {
        if (size == 0) {
            node newNode = new node(key, value);
            Item = newNode;
        } else {
            if (key.compareTo(Item.key) < 0) {
                if (left == null) {
                    node newNode = new node(key, value);
                    left = new BSTMap();
                    left.Item = newNode;
                    left.size += 1;
                } else {
                    left.put(key, value);
                }
            } else {
                if (right == null) {
                    node newNode = new node(key, value);
                    right = new BSTMap();
                    right.Item = newNode;
                    right.size += 1;
                } else {
                    right.put(key, value);
                }
            }
        }
        size += 1;
    }

    @Override
    public Set<K> keySet() {
        throw new UnsupportedOperationException();
    }

    @Override
    public V remove(K key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public V remove(K key, V value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterator<K> iterator() {
        throw new UnsupportedOperationException();
    }

    public void printInOrder() {
        printHelper(this, 0);
    }

    private void printHelper(BSTMap t, int index) {
        if (t == null) {
            return;
        }
        printHelper(t.left, index + 1);
        System.out.println(" ".repeat(2 * index)  + t.Item.key);
        printHelper(t.right, index + 1);
    }
}
