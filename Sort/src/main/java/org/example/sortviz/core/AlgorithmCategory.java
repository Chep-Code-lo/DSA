package org.example.sortviz.core;
public enum AlgorithmCategory {
    INSERTION("Xen vào"),
    EXCHANGE("Đổi chỗ"),
    SELECTION("Lựa chọn"),
    MERGE("Trộn");


    private final String vi;
    AlgorithmCategory(String vi) { this.vi = vi; }
    @Override public String toString() { return vi; }
}