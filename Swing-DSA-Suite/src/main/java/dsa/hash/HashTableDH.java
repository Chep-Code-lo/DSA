package dsa.hash;

public class HashTableDH<K,V> {
    private Object[] keys; private Object[] vals; private int m; private int size=0;
    private final java.util.function.ToIntFunction<K> h1;
    private final java.util.function.ToIntFunction<K> h2;
    private static final Object TOMBSTONE = new Object();
    private double lfThreshold = 0.7;

    public HashTableDH(int m, java.util.function.ToIntFunction<K> h1, java.util.function.ToIntFunction<K> h2){
        this.m=m; this.h1=h1; this.h2=h2; keys=new Object[m]; vals=new Object[m];
    }
    public void setLoadFactorThreshold(double t){ if(t<=0||t>=0.95) throw new IllegalArgumentException("0 < t < 0.95"); this.lfThreshold=t; }
    public double loadFactor(){ return (double)size/m; }
    private int H1(K k){ return Math.floorMod(h1.applyAsInt(k), m); }
    private int H2(K k){ int x = Math.floorMod(h2.applyAsInt(k), m); x = (x==0?1:x); return x; } // step != 0

    public void put(K k, V v){
        if((size+1) > (int)(lfThreshold*m)) rehash(nextPrime(m<<1));
        int i = H1(k), step = H2(k), firstTomb=-1, probes=0;
        while(probes<m){
            if(keys[i]==null){ if(firstTomb!=-1) i=firstTomb; keys[i]=k; vals[i]=v; size++; return; }
            if(keys[i]==TOMBSTONE){ if(firstTomb==-1) firstTomb=i; }
            else if(keys[i].equals(k)){ vals[i]=v; return; }
            i = (i + step) % m; probes++;
        }
        throw new IllegalStateException("Table full");
    }
    public V get(K k){
        int i = H1(k), step = H2(k), probes=0;
        while(probes<m){
            Object kk = keys[i];
            if(kk==null) return null;
            if(kk!=TOMBSTONE && kk.equals(k)) return (V)vals[i];
            i = (i + step) % m; probes++;
        }
        return null;
    }
    public boolean remove(K k){
        int i = H1(k), step = H2(k), probes=0;
        while(probes<m){
            Object kk = keys[i];
            if(kk==null) return false;
            if(kk!=TOMBSTONE && kk.equals(k)){ keys[i]=TOMBSTONE; vals[i]=null; size--; return true; }
            i = (i + step) % m; probes++;
        }
        return false;
    }
    public int size(){ return size; }
    public int capacity(){ return m; }
    public String[] snapshot(){ String[] a=new String[m]; for(int i=0;i<m;i++){ Object k=keys[i]; if(k==null) a[i]="·"; else if(k==TOMBSTONE) a[i]="#"; else a[i]=k+":"+vals[i]; } return a; }

    private void rehash(int newM){
        Object[] oldK=keys, oldV=vals; int oldM=m;
        m=newM; keys=new Object[m]; vals=new Object[m]; size=0;
        for(int i=0;i<oldM;i++){
            Object ok = oldK[i]; if(ok!=null && ok!=TOMBSTONE){ @SuppressWarnings("unchecked") K k=(K)ok; @SuppressWarnings("unchecked") V v=(V)oldV[i]; put(k,v); }
        }
    }
    private static boolean isPrime(int x){ if(x<2) return false; if(x%2==0) return x==2; for(int i=3;i*i<=x;i+=2) if(x%i==0) return false; return true; }
    private static int nextPrime(int x){ while(!isPrime(x)) x++; return x; }
}
