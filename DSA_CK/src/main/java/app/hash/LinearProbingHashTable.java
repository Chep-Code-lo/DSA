package app.hash;

import java.util.*;

public class LinearProbingHashTable<K, V> implements HashTable<K, V> {
  public String tracePut(K key, V value) {
    StringBuilder sb = new StringBuilder();
    if (n + 1 > a.length * MAX_LF) {
      sb.append("Load factor vượt ngưỡng, thực hiện rehash\n");
    }
    int i = idx(key);
    int steps = 0;
    while (true) {
      HashEntry<K, V> e = a[i];
      sb.append(String.format("Probe %d -> slot %d: %s\n", steps, i,
          (e == null ? "EMPTY" : (e.tomb ? "TOMB" : ("KEY=" + e.key + ", VAL=" + e.value)))));
      if (e == null || e.tomb) {
        sb.append("Ghi mới tại slot ").append(i).append("\n");
        break;
      }
      if (Objects.equals(e.key, key)) {
        sb.append("Cập nhật giá trị tại slot ").append(i).append("\n");
        break;
      }
      i = (i + 1) % a.length;
      steps++;
    }
    return sb.toString();
  }

  public String traceRemove(K key) {
    StringBuilder sb = new StringBuilder();
    int i = idx(key), start = i, steps = 0;
    while (a[i] != null) {
      HashEntry<K, V> e = a[i];
      sb.append(String.format("Probe %d -> slot %d: %s\n", steps, i,
          (e == null ? "EMPTY" : (e.tomb ? "TOMB" : ("KEY=" + e.key + ", VAL=" + e.value)))));
      if (!e.tomb && Objects.equals(e.key, key)) {
        sb.append("Đánh dấu tomb tại slot ").append(i).append("\n");
        return sb.toString();
      }
      i = (i + 1) % a.length;
      steps++;
      if (i == start) break;
    }
    sb.append("Không tìm thấy key để xóa\n");
    return sb.toString();
  }
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

  public String traceGet(K key) {
    StringBuilder sb = new StringBuilder();
    int m = a.length;
    int i = idx(key);
    sb.append("hash(").append(key).append(") % ").append(m).append(" = ").append(i).append('\n');
    int steps = 0;
    while (steps < m) {
      HashEntry<K, V> e = a[i];
      sb.append(String.format("Probe %d -> slot %d: %s\n", steps, i,
          (e == null ? "EMPTY" : (e.tomb ? "TOMB" : ("KEY=" + e.key + ", VAL=" + e.value)))));
      if (e == null) {
        sb.append("Encountered EMPTY -> stop: NOT FOUND");
        return sb.toString();
      }
      if (!e.tomb && (key == null ? e.key == null : key.equals(e.key))) {
        sb.append("Match at slot ").append(i).append(" -> FOUND, value=").append(e.value);
        return sb.toString();
      }
      i = (i + 1) % m;
      steps++;
    }
    sb.append("Scanned all slots -> NOT FOUND");
    return sb.toString();
  }
}
