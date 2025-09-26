package com.example;

import java.util.LinkedList;
import java.util.Objects;

public class HashTableChaining {
    private int m = 7;                       // kích thước bảng (số bucket)
    private LinkedList<Entry>[] buckets;     // m danh sách liên kết

    @SuppressWarnings("unchecked")
    public HashTableChaining() {
        buckets = new LinkedList[m];
        for (int i = 0; i < m; i++) buckets[i] = new LinkedList<>();
    }

    private int hash(int key) {              // Division method
        int idx = key % m;
        return idx < 0 ? idx + m : idx;
    }

    // Thêm/cập nhật
    public void put(int key, String value) {
        int idx = hash(key);
        for (Entry e : buckets[idx]) {
            if (e.key == key) {              // cập nhật nếu trùng khóa
                e.value = value;
                return;
            }
        }
        buckets[idx].add(new Entry(key, value)); // thêm mới vào bucket
    }

    // Lấy
    public String get(int key) {
        int idx = hash(key);
        for (Entry e : buckets[idx]) {
            if (e.key == key) return e.value;
        }
        return null;
    }

    // Xóa (tùy chọn)
    public boolean remove(int key) {
        int idx = hash(key);
        return buckets[idx].removeIf(e -> e.key == key);
    }

    public void printTable() {
        System.out.println("Bucket | Entries (key:value)");
        for (int i = 0; i < m; i++) {
            System.out.print(String.format("%6d | ", i));
            if (buckets[i].isEmpty()) {
                System.out.println("-");
            } else {
                StringBuilder sb = new StringBuilder();
                for (Entry e : buckets[i]) {
                    sb.append(e.key).append(":").append(e.value).append("  ");
                }
                System.out.println(sb);
            }
        }
    }
}

