package app.hash;

public interface HashTable<K, V>{
  void put(K key, V value);
  V get(K key);
  boolean remove(K key);
  int size();
  int capacity();
  String debugView();
  String traceGet(K key);
}
