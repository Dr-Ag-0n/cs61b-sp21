package bstmap;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class BSTMap<K extends Comparable<K>, V> implements Map61B<K, V> {
    private Node<K, V> root;
    private int size;

    private static class Node<K, V> {
        K key;
        V value;
        Node<K, V> left;
        Node<K, V> right;

        Node() {
            key = null;
            value = null;
            left = null;
            right = null;
        }

        Node(K k, V v) {
            key = k;
            value = v;
            left = null;
            right = null;
        }
    }

    public BSTMap() {
        root = new Node<>();
        size = 0;
    }

    @Override
    public void clear() {
        root = new Node<>();
        size = 0;
    }

    @Override
    public boolean containsKey(K key) {
        if (size == 0) {
            return false;
        }
        return containsKeyHelper(root, key);
    }

    private boolean containsKeyHelper(Node<K, V> n, K key) {
        if (n == null) {
            return false;
        }
        if (key.equals(n.key)) {
            return true;
        } else if (key.compareTo(n.key) < 0) {
            return containsKeyHelper(n.left, key);
        } else {
            return containsKeyHelper(n.right, key);
        }
    }

    @Override
    public V get(K key) {
        if (size == 0) {
            return null;
        }
        return getHelper(root, key);
    }

    private V getHelper(Node<K, V> n, K key) {
        if (n == null) {
            return null;
        }
        if (key.equals(n.key)) {
            return n.value;
        } else if (key.compareTo(n.key) < 0) {
            return getHelper(n.left, key);
        } else {
            return getHelper(n.right, key);
        }
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void put(K key, V value) {
        if (size == 0) {
            Node<K, V> newNode = new Node<>(key, value);
            root = newNode;
            size += 1;
        } else {
            putHelper(root, key, value);
        }
    }

    private void putHelper(Node<K, V> n, K key, V value) {
        if (key.equals(n.key)) {
            n.value = value;
            return;
        } else if (key.compareTo(n.key) < 0) {
            if (n.left == null) {
                n.left = new Node<>(key, value);
                size += 1;
                return;
            }
            putHelper(n.left, key, value);
        } else {
            if (n.right == null) {
                n.right = new Node<>(key, value);
                size += 1;
                return;
            }
            putHelper(n.right, key, value);
        }
    }

    @Override
    public Set<K> keySet() {
        Set<K> set = new HashSet<>();
        keySetHelper(root, set);
        return set;
    }

    private void keySetHelper(Node<K, V> n, Set<K> s) {
        if (n == null) {
            return;
        }
        s.add(n.key);
        keySetHelper(n.left, s);
        keySetHelper(n.right, s);
    }

    @Override
    public V remove(K key) {
        if (size == 0 || !containsKey(key)) {
            return null;
        }
        V value = this.get(key);
        root = removeHelper(root, key);
        return value;
    }

    @Override
    public V remove(K key, V value) {
        if (get(key) != value) {
            return null;
        } else {
            V v = remove(key);
            return v;
        }
    }

    private int childNum(Node<K, V> n) {
        int num = 0;
        if (n.left != null) {
            num += 1;
        }
        if (n.right != null) {
            num += 1;
        }
        return num;
    }

    private Node<K, V> removeHelper(Node<K, V> n, K key) {
        Node<K, V> returnNode = n;
        if (n == null) {
            returnNode = null;
        } else if (key.equals(n.key)) {
            size -= 1;
            if (childNum(n) == 0) {
                returnNode = null;
            } else if (childNum(n) == 1) {
                if (n.left == null) {
                    returnNode = n.right;
                } else {
                    returnNode = n.left;
                }
            } else if (childNum(n) == 2) {
                Node<K, V> pre = findPre(n);
                Node<K, V> node = removeHelper(n, pre.key);
                pre.left = node.left;
                pre.right = node.right;
                returnNode = pre;
            }
        } else {
            n.left = removeHelper(n.left, key);
            n.right = removeHelper(n.right, key);
            returnNode = n;
        }
        return returnNode;
    }

    private Node<K, V> findPre(Node<K, V> n) {
        Node<K, V> node = n;
        if (node.left != null) {
            node = node.left;
        }
        while (node.right != null) {
            node = node.right;
        }
        return node;
    }

    @Override
    public Iterator<K> iterator() {
        Set<K> set = this.keySet();
        return set.iterator();
    }

    public void printInOrder() {
        printInOrderHelper(root, 0);
    }

    private void printInOrderHelper(Node<K, V> n, int index) {
        if (n != null) {
            printInOrderHelper(n.left, index + 1);
            System.out.println(" ".repeat(2 * index) + n.key);
            printInOrderHelper(n.right, index + 1);
        }
    }
}
