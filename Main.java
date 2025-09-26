import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class Main {
    static class HashTableChaining{
        final int m;
        final LinkedList<Integer>[] buckets;

        @SuppressWarnings("unchecked")
        HashTableChaining(int m){
            this.m = m;
            this.buckets = new LinkedList[m];
            for (int i = 0; i < m; i++) buckets[i] = new LinkedList<>();
        }

        int hash(int key) { return key % m; }

        void insert(int key) { buckets[hash(key)].add(key); }

        int searchCount(int key){
            LinkedList<Integer> b = buckets[hash(key)];
            int cmp = 0;
            for(int x : b){
                cmp++;
                if (x == key) break;
            }
            if(!b.contains(key)) cmp = b.size();
            return cmp;
        }

        int maxBucketLen(){
            int mx = 0;
            for (LinkedList<Integer> b : buckets) mx = Math.max(mx, b.size());
            return mx;
        }

        double avgBucketLen(){
            long sum = 0;
            for (LinkedList<Integer> b : buckets) sum += b.size();
            return (double) sum / m;
        }

        void printTable(){
            System.out.println("=== HashTable (m=" + m + ") ===");
            for (int i = 0; i < m; i++) {
                System.out.print("Bucket[" + i + "]: ");
                for (int x : buckets[i]) System.out.print(x + " -> ");
                System.out.println("null");
            }
        }
    }

    static List<Integer> uniqueRandoms(int n, int lo, int hi, long seed){
        Random rng = (seed == 0) ? new Random() : new Random(seed);
        HashSet<Integer> set = new HashSet<>(n * 2);
        while (set.size() < n) set.add(lo + rng.nextInt(hi - lo + 1));
        return new ArrayList<>(set);
    }

    public static void main(String[] args){
        final int N = 1000, LO = 1000, HI = 9999, M = 79, Q = 50;
        long seed = 0;
        List<Integer> keys = uniqueRandoms(N, LO, HI, seed);
        int[] arr = keys.stream().mapToInt(Integer::intValue).toArray();
        HashTableChaining ht = new HashTableChaining(M);
        for (int k : arr) ht.insert(k);

        System.out.println("=== THIẾT LẬP ===");
        System.out.println("Số phần tử (N)   : " + N);
        System.out.println("Miền giá trị     : [" + LO + ", " + HI + "] (khóa duy nhất)");
        System.out.println("Kích thước bảng m: " + M + " (division method, chaining)");
        System.out.println("Số truy vấn (Q)  : " + Q);

        System.out.println("=== THỐNG KÊ BUCKET ===");
        System.out.printf("Độ dài bucket TB : %.2f%n", ht.avgBucketLen());
        System.out.println("Độ dài bucket max: " + ht.maxBucketLen());

        ht.printTable();

        ThreadLocalRandom rnd = ThreadLocalRandom.current();
        int totalLinear = 0, totalHash = 0;
        for (int i = 0; i < Q; i++) {
            int key = rnd.nextInt(LO, HI + 1);
            int cmpLin = 0;
            boolean found = false;
            for (int x : arr) {
                cmpLin++;
                if (x == key) { found = true; break; }
            }
            if (!found) cmpLin = arr.length;
            totalLinear += cmpLin;
            totalHash += ht.searchCount(key);
        }

        System.out.println("=== SO SÁNH SỐ LẦN SO SÁNH (comparisons) ===");
        System.out.println("Tổng so sánh - Linear (trên mảng " + N + "): " + totalLinear);
        System.out.println("Tổng so sánh - Hash (duyệt chain)     : " + totalHash);
        System.out.printf("Trung bình/1 truy vấn - Linear: %.2f%n", (double) totalLinear / Q);
        System.out.printf("Trung bình/1 truy vấn - Hash  : %.2f%n", (double) totalHash / Q);
    }
}
