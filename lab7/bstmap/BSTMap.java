package bstmap;

import java.util.Iterator;
import java.util.Set;

public class BSTMap<K extends Comparable<K>, V> implements Map61B<K, V> {
    private node tree;
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
        tree = new node();
        left = null;
        right = null;
        size = 0;
    }

    @Override
    public void clear() {
        tree = new node();
        left = null;
        right = null;
        size = 0;
    }

    @Override
    public boolean containsKey(K key) {
        if (tree.key != null) {
            if (key.equals(tree.key)) {
                return true;
            }
            if (key.compareTo(tree.key) < 0) {
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
        if (tree.key != null) {
            if (key.equals(tree.key)) {
                return tree.value;
            }
            if (key.compareTo(tree.key) < 0) {
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
            tree = newNode;
        } else {
            if (key.compareTo(tree.key) < 0) {
                if (left == null) {
                    node newNode = new node(key, value);
                    left = new BSTMap();
                    left.tree = newNode;
                } else {
                    left.put(key, value);
                }
            } else {
                if (right == null) {
                    node newNode = new node(key, value);
                    right = new BSTMap();
                    right.tree = newNode;
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
    }
}
