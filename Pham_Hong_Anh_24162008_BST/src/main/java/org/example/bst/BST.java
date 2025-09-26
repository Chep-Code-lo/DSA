package org.example.bst;
import java.util.*;
public class BST<K extends Comparable<K>, V> {
    private static final class Node<K, V> {
        K key;
        V val;
        Node<K, V> left, right;
        int size;

        Node(K k, V v) {
            key = k;
            val = v;
            size = 1;
        }
    }

    public static final class ViewNode<T extends Comparable<T>> {
        public final T key;
        public final ViewNode<T> left, right;
        public final int size;

        ViewNode(T key, ViewNode<T> left, ViewNode<T> right, int size) {
            this.key = key;
            this.left = left;
            this.right = right;
            this.size = size;
        }
    }

    private Node<K, V> root;

    public int size() {
        return size(root);
    }

    public boolean isEmpty() {
        return root == null;
    }

    private int size(Node<K, V> x) {
        return x == null ? 0 : x.size;
    }

    public int height() {
        return height(root);
    }

    private int height(Node<K, V> x) {
        return x == null ? -1 : 1 + Math.max(height(x.left), height(x.right));
    }

    public void put(K key, V val) {
        if (key == null) throw new IllegalArgumentException("key == null");
        root = put(root, key, val);
    }

    private Node<K, V> put(Node<K, V> x, K key, V val) {
        if (x == null) return new Node<>(key, val);
        int cmp = key.compareTo(x.key);
        if (cmp < 0) x.left = put(x.left, key, val);
        else if (cmp > 0) x.right = put(x.right, key, val);
        else x.val = val; 
        x.size = 1 + size(x.left) + size(x.right);
        return x;
    }

    public V get(K key) {
        Node<K, V> x = root;
        while (x != null) {
            int cmp = key.compareTo(x.key);
            if (cmp < 0) x = x.left;
            else if (cmp > 0) x = x.right;
            else return x.val;
        }
        return null;
    }

    public boolean containsKey(K key) {
        return get(key) != null;
    }

    // --------------- min/max, floor/ceil ---------------
    public K minKey() {
        if (root == null) throw new NoSuchElementException("empty");
        return min(root).key;
    }

    private Node<K, V> min(Node<K, V> x) {
        while (x.left != null) x = x.left;
        return x;
    }

    public K maxKey() {
        if (root == null) throw new NoSuchElementException("empty");
        return max(root).key;
    }

    private Node<K, V> max(Node<K, V> x) {
        while (x.right != null) x = x.right;
        return x;
    }

    public K floor(K key) {
        Node<K, V> x = floor(root, key);
        return x == null ? null : x.key;
    }

    private Node<K, V> floor(Node<K, V> x, K key) {
        if (x == null) return null;
        int cmp = key.compareTo(x.key);
        if (cmp == 0) return x;
        if (cmp < 0) return floor(x.left, key);
        Node<K, V> t = floor(x.right, key);
        return (t != null) ? t : x;
    }

    public K ceil(K key) {
        Node<K, V> x = ceil(root, key);
        return x == null ? null : x.key;
    }

    private Node<K, V> ceil(Node<K, V> x, K key) {
        if (x == null) return null;
        int cmp = key.compareTo(x.key);
        if (cmp == 0) return x;
        if (cmp > 0) return ceil(x.right, key);
        Node<K, V> t = ceil(x.left, key);
        return (t != null) ? t : x;
    }

    public V remove(K key) {
        if (key == null) throw new IllegalArgumentException("key == null");
        @SuppressWarnings("unchecked") V[] box = (V[]) new Object[1];
        root = delete(root, key, box);
        return box[0];
    }

    private Node<K, V> delete(Node<K, V> x, K key, V[] box) {
        if (x == null) return null;
        int cmp = key.compareTo(x.key);
        if (cmp < 0) x.left = delete(x.left, key, box);
        else if (cmp > 0) x.right = delete(x.right, key, box);
        else {
            box[0] = x.val;
            if (x.right == null) return x.left;
            if (x.left == null) return x.right;
            Node<K, V> t = x;
            x = min(t.right);           
            x.right = deleteMin(t.right);
            x.left = t.left;            
        }
        x.size = 1 + size(x.left) + size(x.right);
        return x;
    }

    public void removeMin() {
        if (root != null) root = deleteMin(root);
    }

    private Node<K, V> deleteMin(Node<K, V> x) {
        if (x.left == null) return x.right;
        x.left = deleteMin(x.left);
        x.size = 1 + size(x.left) + size(x.right);
        return x;
    }

    public void removeMax() {
        if (root != null) root = deleteMax(root);
    }

    private Node<K, V> deleteMax(Node<K, V> x) {
        if (x.right == null) return x.left;
        x.right = deleteMax(x.right);
        x.size = 1 + size(x.left) + size(x.right);
        return x;
    }
    public Iterable<K> keysInOrder() {
        List<K> r = new ArrayList<>();
        inorder(root, r);
        return r;
    }

    private void inorder(Node<K, V> x, List<K> r) {
        if (x == null) return;
        inorder(x.left, r);
        r.add(x.key);
        inorder(x.right, r);
    }

    public Iterable<K> keysPreOrder() {
        List<K> r = new ArrayList<>();
        preorder(root, r);
        return r;
    }

    private void preorder(Node<K, V> x, List<K> r) {
        if (x == null) return;
        r.add(x.key);
        preorder(x.left, r);
        preorder(x.right, r);
    }

    public Iterable<K> keysPostOrder() {
        List<K> r = new ArrayList<>();
        postorder(root, r);
        return r;
    }

    private void postorder(Node<K, V> x, List<K> r) {
        if (x == null) return;
        postorder(x.left, r);
        postorder(x.right, r);
        r.add(x.key);
    }

    public Iterable<K> keysLevelOrder() {
        List<K> r = new ArrayList<>();
        if (root == null) return r;
        Deque<Node<K, V>> q = new ArrayDeque<>();
        q.add(root);
        while (!q.isEmpty()) {
            Node<K, V> n = q.removeFirst();
            r.add(n.key);
            if (n.left != null) q.addLast(n.left);
            if (n.right != null) q.addLast(n.right);
        }
        return r;
    }

    public boolean isBST() {
        return isBST(root, null, null);
    }

    private boolean isBST(Node<K, V> x, K lo, K hi) {
        if (x == null) return true;
        if (lo != null && x.key.compareTo(lo) <= 0) return false;
        if (hi != null && x.key.compareTo(hi) >= 0) return false;
        return isBST(x.left, lo, x.key) && isBST(x.right, x.key, hi);
    }

    public void printPretty() {
        printPretty(root, "", false);
    }

    private void printPretty(Node<K, V> x, String prefix, boolean isLeft) {
        if (x == null) return;
        if (x.right != null) printPretty(x.right, prefix + (isLeft ? "│   " : "    "), false);
        System.out.println(prefix + (isLeft ? "└── " : "┌── ") + x.key);
        if (x.left != null) printPretty(x.left, prefix + (isLeft ? "    " : "│   "), true);
    }
    public ViewNode<K> snapshot() {
        return snapshot(root);
    }

    private ViewNode<K> snapshot(Node<K, V> x) {
        if (x == null) return null;
        var L = snapshot(x.left);
        var R = snapshot(x.right);
        return new ViewNode<>(x.key, L, R, x.size);
    }
    public List<K> searchPath(K key) {
        List<K> path = new ArrayList<>();
        Node<K, V> x = root;
        while (x != null) {
            path.add(x.key);
            int cmp = key.compareTo(x.key);
            if (cmp < 0) x = x.left;
            else if (cmp > 0) x = x.right;
            else break;
        }
        return path;
    }
}
