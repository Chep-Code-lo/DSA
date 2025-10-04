package dsa.hash;
import java.util.*;

public class HashTableChaining<K,V> {
    private final List<Entry<K,V>>[] buckets; private final int m; private final java.util.function.ToIntFunction<K> hasher;
    @SuppressWarnings("unchecked")
    public HashTableChaining(int m, java.util.function.ToIntFunction<K> hasher){ this.m=m; this.hasher=hasher; buckets=new List[m]; for(int i=0;i<m;i++) buckets[i]=new LinkedList<>(); }
    private int h(K k){ return Math.floorMod(hasher.applyAsInt(k), m); }
    public void put(K k,V v){ var b=buckets[h(k)]; for(var e:b){ if(e.key.equals(k)){ e.val=v; return; } } b.add(new Entry<>(k,v)); }
    public V get(K k){ var b=buckets[h(k)]; for(var e:b) if(e.key.equals(k)) return e.val; return null; }
    public boolean remove(K k){ var b=buckets[h(k)]; var it=b.iterator(); while(it.hasNext()){ var e=it.next(); if(e.key.equals(k)){ it.remove(); return true; } } return false; }
    public String[] snapshot(){ String[] a=new String[m]; for(int i=0;i<m;i++){ if(buckets[i].isEmpty()) a[i]="·"; else a[i]=buckets[i].toString(); } return a; }
}
