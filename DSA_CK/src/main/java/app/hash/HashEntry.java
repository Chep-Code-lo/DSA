package app.hash;

class HashEntry<K, V> {
  final K key;
  V value;
  boolean tomb;

  HashEntry(K k, V v) {
    this.key = k;
    this.value = v;
  }
}
