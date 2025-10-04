package dsa.hash;
public class Entry<K,V> { public final K key; public V val; public Entry(K k,V v){ key=k; val=v; } public String toString(){ return key+"→"+val; } }
