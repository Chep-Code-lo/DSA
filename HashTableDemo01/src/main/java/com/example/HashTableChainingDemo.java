package com.example;

public class HashTableChainingDemo {
    public static void main(String[] args) {
        HashTableChaining ht = new HashTableChaining();

        // Chèn dữ liệu (cố tình gây đụng độ với %7)
        ht.put(1001, "An");    // 1001 % 7 = 2
        ht.put(1008, "Bình");  // 1008 % 7 = 2 (đụng độ)
        ht.put(1015, "Chi");   // 1015 % 7 = 2 (đụng độ)
        ht.put(1003, "Dũng");  // 1003 % 7 = 6
        ht.put(1022, "Em");    // 1022 % 7 = 2 (đụng độ tiếp)
        ht.put(1009, "Giang"); // 1009 % 7 = 3

        ht.printTable();

        System.out.println("\nTìm 1008 → " + ht.get(1008));
        System.out.println("Tìm 1015 → " + ht.get(1015));

        System.out.println("\nXóa 1008 → " + (ht.remove(1008) ? "OK" : "Không thấy"));
        ht.printTable();
    }
}