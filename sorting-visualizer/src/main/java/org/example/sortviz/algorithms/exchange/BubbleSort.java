package org.example.sortviz.algorithms.exchange;

import org.example.sortviz.core.*;

import java.util.Comparator;
import java.util.List;

import static org.example.sortviz.core.Steps.*;

public class BubbleSort<T> implements SortingAlgorithm<T> {
    @Override public String getName(){ return "Đổi chỗ nổi bọt (Bubble)"; }
    @Override public AlgorithmCategory getCategory(){ return AlgorithmCategory.EXCHANGE; }

    @Override
    public void sort(List<T> a, Comparator<? super T> cmp, StepEmitter<T> e) {
        e.emit(Step.note("Bắt đầu Bubble Sort"));
        int n = a.size();
        for (int i=0;i<n-1;i++){
            for (int j=0;j<n-1-i;j++){
                compare(j, j+1, e, "So sánh cặp kề");
                if (cmp.compare(a.get(j), a.get(j+1)) > 0){
                    swap(a, j, j+1, e, "Đổi chỗ do ngược thứ tự");
                }
            }
            markFinal(n-1-i, e, "Phần tử lớn nhất đã về cuối");
        }
        for (int k=0;k<n;k++) markFinal(k, e, "Hoàn tất");
    }
}
