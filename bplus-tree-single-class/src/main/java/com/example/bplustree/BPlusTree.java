package com.example.bplustree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

public final class BPlusTree<K extends Comparable<K>, V> {
    private final int order;
    private Node<K> root;
    private LeafNode<K, V> firstLeaf;
    private int size;

    public BPlusTree(int order) {
        if (order < 3) {
            throw new IllegalArgumentException("order must be at least 3");
        }
        this.order = order;
        LeafNode<K, V> leaf = new LeafNode<K, V>();
        this.root = leaf;
        this.firstLeaf = leaf;
    }

    public V put(K key, V value) {
        validateKey(key);
        LeafNode<K, V> leaf = findLeaf(key);
        int index = findKeyIndex(leaf.keys, key);
        if (index >= 0) {
            V previous = leaf.values.set(index, value);
            return previous;
        }

        int insertion = insertionPoint(index);
        leaf.keys.add(insertion, key);
        leaf.values.add(insertion, value);
        size++;

        if (leaf.keys.size() == order) {
            splitLeaf(leaf);
        }
        return null;
    }

    public V get(K key) {
        validateKey(key);
        LeafNode<K, V> leaf = findLeaf(key);
        int index = findKeyIndex(leaf.keys, key);
        if (index < 0) {
            return null;
        }
        return leaf.values.get(index);
    }

    public boolean containsKey(K key) {
        return get(key) != null || containsStoredNull(key);
    }

    public V remove(K key) {
        validateKey(key);
        LeafNode<K, V> leaf = findLeaf(key);
        int index = findKeyIndex(leaf.keys, key);
        if (index < 0) {
            return null;
        }

        V removed = leaf.values.remove(index);
        leaf.keys.remove(index);
        size--;
        rebuildFromEntries(allEntries());
        return removed;
    }

    public List<V> rangeSearch(K fromInclusive, K toInclusive) {
        validateKey(fromInclusive);
        validateKey(toInclusive);
        if (fromInclusive.compareTo(toInclusive) > 0) {
            throw new IllegalArgumentException("fromInclusive must be <= toInclusive");
        }

        List<V> values = new ArrayList<V>();
        LeafNode<K, V> leaf = findLeaf(fromInclusive);
        while (leaf != null) {
            for (int index = 0; index < leaf.keys.size(); index++) {
                K key = leaf.keys.get(index);
                if (key.compareTo(toInclusive) > 0) {
                    return values;
                }
                if (key.compareTo(fromInclusive) >= 0) {
                    values.add(leaf.values.get(index));
                }
            }
            leaf = leaf.next;
        }
        return values;
    }

    public List<K> keys() {
        List<K> keys = new ArrayList<K>();
        LeafNode<K, V> leaf = firstLeaf;
        while (leaf != null) {
            keys.addAll(leaf.keys);
            leaf = leaf.next;
        }
        return keys;
    }

    public K firstKey() {
        ensureNotEmpty();
        return firstLeaf.keys.get(0);
    }

    public K lastKey() {
        ensureNotEmpty();
        LeafNode<K, V> leaf = firstLeaf;
        while (leaf.next != null) {
            leaf = leaf.next;
        }
        return leaf.keys.get(leaf.keys.size() - 1);
    }

    public int height() {
        int height = 1;
        Node<K> current = root;
        while (!current.leaf) {
            InternalNode<K> internal = castInternal(current);
            current = internal.children.get(0);
            height++;
        }
        return height;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public void clear() {
        LeafNode<K, V> leaf = new LeafNode<K, V>();
        root = leaf;
        firstLeaf = leaf;
        size = 0;
    }

    private void splitLeaf(LeafNode<K, V> leaf) {
        int split = order / 2;
        LeafNode<K, V> right = new LeafNode<K, V>();
        moveTail(leaf.keys, right.keys, split);
        moveTail(leaf.values, right.values, split);

        right.next = leaf.next;
        leaf.next = right;

        insertIntoParent(leaf, right.keys.get(0), right);
    }

    private void splitInternal(InternalNode<K> node) {
        int middleIndex = node.keys.size() / 2;
        K promote = node.keys.get(middleIndex);
        InternalNode<K> right = new InternalNode<K>();

        for (int index = middleIndex + 1; index < node.keys.size(); index++) {
            right.keys.add(node.keys.get(index));
        }
        for (int index = middleIndex + 1; index < node.children.size(); index++) {
            Node<K> child = node.children.get(index);
            right.children.add(child);
            child.parent = right;
        }

        trimList(node.keys, middleIndex);
        trimList(node.children, middleIndex + 1);

        insertIntoParent(node, promote, right);
    }

    private void insertIntoParent(Node<K> left, K key, Node<K> right) {
        InternalNode<K> parent = left.parent;
        if (parent == null) {
            InternalNode<K> newRoot = new InternalNode<K>();
            newRoot.keys.add(key);
            newRoot.children.add(left);
            newRoot.children.add(right);
            left.parent = newRoot;
            right.parent = newRoot;
            root = newRoot;
            return;
        }

        int childIndex = parent.children.indexOf(left);
        parent.keys.add(childIndex, key);
        parent.children.add(childIndex + 1, right);
        right.parent = parent;

        if (parent.children.size() > order) {
            splitInternal(parent);
        }
    }

    private LeafNode<K, V> findLeaf(K key) {
        Node<K> current = root;
        while (!current.leaf) {
            InternalNode<K> internal = castInternal(current);
            int childIndex = 0;
            while (childIndex < internal.keys.size() && key.compareTo(internal.keys.get(childIndex)) >= 0) {
                childIndex++;
            }
            current = internal.children.get(childIndex);
        }
        return castLeaf(current);
    }

    private boolean containsStoredNull(K key) {
        LeafNode<K, V> leaf = findLeaf(key);
        int index = findKeyIndex(leaf.keys, key);
        return index >= 0;
    }

    private List<Entry<K, V>> allEntries() {
        List<Entry<K, V>> entries = new ArrayList<Entry<K, V>>();
        LeafNode<K, V> leaf = firstLeaf;
        while (leaf != null) {
            for (int index = 0; index < leaf.keys.size(); index++) {
                entries.add(new Entry<K, V>(leaf.keys.get(index), leaf.values.get(index)));
            }
            leaf = leaf.next;
        }
        return entries;
    }

    private void rebuildFromEntries(List<Entry<K, V>> entries) {
        clear();
        for (Entry<K, V> entry : entries) {
            put(entry.key, entry.value);
        }
    }

    private int findKeyIndex(List<K> keys, K key) {
        return Collections.binarySearch(keys, key);
    }

    private int insertionPoint(int binarySearchResult) {
        return -binarySearchResult - 1;
    }

    private <E> void moveTail(List<E> source, List<E> target, int fromIndex) {
        while (source.size() > fromIndex) {
            target.add(source.remove(fromIndex));
        }
    }

    private <E> void trimList(List<E> values, int sizeToKeep) {
        while (values.size() > sizeToKeep) {
            values.remove(values.size() - 1);
        }
    }

    private void validateKey(K key) {
        if (key == null) {
            throw new IllegalArgumentException("key is required");
        }
    }

    private void ensureNotEmpty() {
        if (isEmpty()) {
            throw new NoSuchElementException("tree is empty");
        }
    }

    @SuppressWarnings("unchecked")
    private InternalNode<K> castInternal(Node<K> node) {
        return (InternalNode<K>) node;
    }

    @SuppressWarnings("unchecked")
    private LeafNode<K, V> castLeaf(Node<K> node) {
        return (LeafNode<K, V>) node;
    }

    private static class Node<K extends Comparable<K>> {
        final List<K> keys = new ArrayList<K>();
        final boolean leaf;
        InternalNode<K> parent;

        private Node(boolean leaf) {
            this.leaf = leaf;
        }
    }

    private static final class InternalNode<K extends Comparable<K>> extends Node<K> {
        final List<Node<K>> children = new ArrayList<Node<K>>();

        private InternalNode() {
            super(false);
        }
    }

    private static final class LeafNode<K extends Comparable<K>, V> extends Node<K> {
        final List<V> values = new ArrayList<V>();
        LeafNode<K, V> next;

        private LeafNode() {
            super(true);
        }
    }

    private static final class Entry<K extends Comparable<K>, V> {
        private final K key;
        private final V value;

        private Entry(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }
}
