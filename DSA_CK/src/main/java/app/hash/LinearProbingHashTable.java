package app.hash;

import java.util.*;

public class LinearProbingHashTable<K, V> implements HashTable<K, V> {
  private HashEntry<K, V>[] a;
  int n = 0;
  final double MAX_LF = 0.6;

  @SuppressWarnings("unchecked")
  public LinearProbingHashTable(int m) {
    a = (HashEntry<K, V>[]) new HashEntry[m];
  }

  private int idx(Object key) {
    return (key == null ? 0 : Math.floorMod(key.hashCode(), a.length));
  }

  private void rehash() {
    HashEntry<K, V>[] old = a;
    @SuppressWarnings("unchecked")
    HashEntry<K, V>[] b = (HashEntry<K, V>[]) new HashEntry[nextCap(a.length)];
    a = b;
    n = 0;
    for (HashEntry<K, V> e : old) {
      if (e != null && !e.tomb) put(e.key, e.value);
    }
  }

  private int nextCap(int cur) {
    int[] primes = {
      11, 19, 29, 41, 59, 79, 109, 149, 199, 257, 331, 419, 541, 673, 809, 1009, 1229, 1481, 1783,
      2141, 2591, 3079, 3581, 4099, 4621, 5167, 5741, 6361, 7013
    };
    for (int p : primes) if (p > cur) return p;
    return cur * 2 + 1;
  }

  @Override
  public void put(K key, V value) {
    if (n + 1 > a.length * MAX_LF) rehash();
    int i = idx(key);
    while (true) {
      if (a[i] == null || a[i].tomb) {
        a[i] = new HashEntry<>(key, value);
        n++;
        return;
      }
      if (Objects.equals(a[i].key, key)) {
        a[i].value = value;
        return;
      }
      i = (i + 1) % a.length;
    }
  }

  @Override
  public V get(K key) {
    int i = idx(key), start = i;
    while (a[i] != null) {
      if (!a[i].tomb && Objects.equals(a[i].key, key)) return a[i].value;
      i = (i + 1) % a.length;
      if (i == start) break;
    }
    return null;
  }

  @Override
  public boolean remove(K key) {
    int i = idx(key), start = i;
    while (a[i] != null) {
      if (!a[i].tomb && Objects.equals(a[i].key, key)) {
        a[i].tomb = true;
        n--;
        return true;
      }
      i = (i + 1) % a.length;
      if (i == start) break;
    }
    return false;
  }

  @Override
  public int size() {
    return n;
  }

  @Override
  public int capacity() {
    return a.length;
  }

  @Override
  public String debugView() {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < a.length; i++) {
      HashEntry<K, V> e = a[i];
      sb.append(String.format("[%02d] ", i));
      if (e == null) sb.append("∅");
      else if (e.tomb) sb.append("†tomb");
      else sb.append(e.key + "→" + e.value);
      sb.append('\n');
    }
    return sb.toString();
  }
}
