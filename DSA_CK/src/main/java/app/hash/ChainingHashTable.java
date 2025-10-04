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

  public String traceGet(K key) {
    StringBuilder sb = new StringBuilder();
    int m = buckets.length;
    int i = idx(key);
    sb.append("hash(").append(key).append(") % ").append(m).append(" = ").append(i).append('\n');
    Node<K, V> p = buckets[i];
    if (p == null) {
      sb.append("Bucket empty -> NOT FOUND");
      return sb.toString();
    }
    int pos = 0;
    while (p != null) {
      sb.append(String.format("Visit node %d: key=%s, val=%s\n", pos, String.valueOf(p.k), String.valueOf(p.v)));
      if (key == null ? p.k == null : key.equals(p.k)) {
        sb.append("Match in chain at position ").append(pos).append(" -> FOUND, value=").append(p.v);
        return sb.toString();
      }
      p = p.next;
      pos++;
    }
    sb.append("Reached end of chain -> NOT FOUND");
    return sb.toString();
  }
}
