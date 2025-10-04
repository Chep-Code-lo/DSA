package dsa.hash;

public class HashTableLP<K,V> {
    private static class Tomb { }
    private final Object TOMBSTONE = new Tomb();
    private Object[] keys; private Object[] vals; private int m; private int size=0;
    private final java.util.function.ToIntFunction<K> hasher;
    private double lfThreshold = 0.7;

    public HashTableLP(int m, java.util.function.ToIntFunction<K> hasher){ this.m=m; keys=new Object[m]; vals=new Object[m]; this.hasher=hasher; }
    public void setLoadFactorThreshold(double t){ if(t<=0||t>=0.95) throw new IllegalArgumentException("0 < t < 0.95"); this.lfThreshold=t; }
    public double loadFactor(){ return (double)size/m; }
    private int h(K k){ return Math.floorMod(hasher.applyAsInt(k), m); }

    public void put(K k, V v){
        if((size+1) > (int)(lfThreshold*m)) rehash(nextPrime(m<<1));
        int i=h(k); int firstTomb=-1;
        for(;;){
            if(keys[i]==null){ if(firstTomb!=-1) i=firstTomb; keys[i]=k; vals[i]=v; size++; return; }
            if(keys[i]==TOMBSTONE){ if(firstTomb==-1) firstTomb=i; }
            else if(keys[i].equals(k)){ vals[i]=v; return; }
            i = (i+1)%m;
        }
    }
    public V get(K k){ int i=h(k); for(;;){ if(keys[i]==null) return null; if(keys[i]!=TOMBSTONE && keys[i].equals(k)) return (V)vals[i]; i=(i+1)%m; } }
    public boolean remove(K k){ int i=h(k); for(;;){ if(keys[i]==null) return false; if(keys[i]!=TOMBSTONE && keys[i].equals(k)){ keys[i]=TOMBSTONE; vals[i]=null; size--; return true; } i=(i+1)%m; } }
    public int size(){ return size; }
    public int capacity(){ return m; }

    private void rehash(int newM){
        Object[] oldK=keys, oldV=vals; int oldM=m;
        m=newM; keys=new Object[m]; vals=new Object[m]; size=0;
        for(int i=0;i<oldM;i++){
            Object ok = oldK[i]; if(ok!=null && ok!=TOMBSTONE){ @SuppressWarnings("unchecked") K k=(K)ok; @SuppressWarnings("unchecked") V v=(V)oldV[i]; put(k,v); }
        }
    }
    private static boolean isPrime(int x){ if(x<2) return false; if(x%2==0) return x==2; for(int i=3;i*i<=x;i+=2) if(x%i==0) return false; return true; }
    private static int nextPrime(int x){ while(!isPrime(x)) x++; return x; }
    public String[] snapshot(){ String[] a=new String[m]; for(int i=0;i<m;i++){ Object k=keys[i]; if(k==null) a[i]="·"; else if(k==TOMBSTONE) a[i]="#"; else a[i]=k+":"+vals[i]; } return a; }
}
