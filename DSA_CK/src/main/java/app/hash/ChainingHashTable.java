package app.hash;

public class ChainingHashTable<K, V> implements HashTable<K, V> {
  static class Node<K, V> {
    K k;
    V v;
    Node<K, V> next;

    Node(K k, V v) {
      this.k = k;
      this.v = v;
    }
  }

  private Node<K, V>[] buckets;
  int n = 0;

  @SuppressWarnings("unchecked")
  public ChainingHashTable(int m) {
    buckets = (Node<K, V>[]) new Node[m];
  }

  private int idx(Object key) {
    return (key == null ? 0 : Math.floorMod(key.hashCode(), buckets.length));
  }

  @Override
  public void put(K key, V value) {
    int i = idx(key);
    Node<K, V> h = buckets[i];
    for (Node<K, V> p = h; p != null; p = p.next) {
      if (java.util.Objects.equals(p.k, key)) {
        p.v = value;
        return;
      }
    }
    Node<K, V> q = new Node<>(key, value);
    q.next = h;
    buckets[i] = q;
    n++;
  }

  @Override
  public V get(K key) {
    int i = idx(key);
    for (Node<K, V> p = buckets[i]; p != null; p = p.next)
      if (java.util.Objects.equals(p.k, key)) return p.v;
    return null;
  }

  @Override
  public boolean remove(K key) {
    int i = idx(key);
    Node<K, V> p = buckets[i], prev = null;
    while (p != null) {
      if (java.util.Objects.equals(p.k, key)) {
        if (prev == null) buckets[i] = p.next;
        else prev.next = p.next;
        n--;
        return true;
      }
      prev = p;
      p = p.next;
    }
    return false;
  }

  @Override
  public int size() {
    return n;
  }

  @Override
  public int capacity() {
    return buckets.length;
  }

  @Override
  public String debugView() {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < buckets.length; i++) {
      sb.append(String.format("[%02d] ", i));
      Node<K, V> p = buckets[i];
      if (p == null) sb.append("∅");
      while (p != null) {
        sb.append(p.k + "→" + p.v);
        if (p.next != null) sb.append(" -> ");
        p = p.next;
      }
      sb.append('\n');
    }
    return sb.toString();
  }
}
