package com.example;

public class HashTableDemo {
    public static void main(String[] args) {
        HashTable ht = new HashTable();

        // Chèn dữ liệu
        ht.put(1001, "An");
        ht.put(1008, "Bình");  // đụng độ với 1001 vì 1001%7=2, 1008%7=2
        ht.put(1015, "Chi");   // đụng độ tiếp, linear probing
        ht.put(1003, "Dũng");

        // In ra bảng băm
        ht.printTable();

        // Truy xuất dữ liệu
        System.out.println("\nTìm 1008 → " + ht.get(1008));
        System.out.println("Tìm 1015 → " + ht.get(1015));
    }
}