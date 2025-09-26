package com.example;

public class HashTable {
    private int m = 7;               // kích thước bảng
    private Integer[] keys;          // lưu khóa (student ID)
    private String[] values;         // lưu giá trị (student name)

    public HashTable() {
        keys = new Integer[m];
        values = new String[m];
    }

    // Hàm băm: chia lấy dư
    private int hash(int key) {
        return key % m;
    }

    // Thêm cặp (key, value) vào bảng
    public void put(int key, String value) {
        int index = hash(key);

        // Linear probing để xử lý đụng độ
        while (keys[index] != null) {
            if (keys[index] == key) {
                // cập nhật nếu trùng khóa
                values[index] = value;
                return;
            }
            index = (index + 1) % m;
        }

        // gán khóa và giá trị
        keys[index] = key;
        values[index] = value;
    }

    // Lấy giá trị từ khóa
    public String get(int key) {
        int index = hash(key);
        int start = index;

        // Linear probing để tìm
        while (keys[index] != null) {
            if (keys[index] == key) {
                return values[index];
            }
            index = (index + 1) % m;
            if (index == start) break; // tránh vòng lặp vô hạn
        }
        return null;
    }

    // In bảng băm
    public void printTable() {
        System.out.println("Index | Key  | Value");
        for (int i = 0; i < m; i++) {
            System.out.printf("%5d | %4s | %s\n",
                    i, keys[i] == null ? "-" : keys[i].toString(),
                    values[i] == null ? "-" : values[i]);
        }
    }
}
