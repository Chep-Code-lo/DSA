package org.example.genericsort;

import java.util.Comparator;
import java.util.List;

public final class InsertionSort {
    private InsertionSort() {}

    public static <T> void sort(List<T> a, Comparator<T> cmp) {
        for (int i = 1; i < a.size(); i++) {
            T key = a.get(i);
            int j = i - 1;
            while (j >= 0 && cmp.compare(a.get(j), key) > 0) {
                a.set(j + 1, a.get(j));
                j--;
            }
            a.set(j + 1, key);
        }
    }
}
