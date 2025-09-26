package org.example.sortviz.algorithms.selection;
import org.example.sortviz.core.*;

import java.util.Comparator;
import java.util.List;

import static org.example.sortviz.core.Steps.*;

public class SelectionSort<T> implements SortingAlgorithm<T> {
    @Override public String getName(){ return "Lựa chọn trực tiếp (Selection)"; }
    @Override public AlgorithmCategory getCategory(){ return AlgorithmCategory.SELECTION; }

    @Override
    public void sort(List<T> a, Comparator<? super T> cmp, StepEmitter<T> e) {
        e.emit(Step.note("Bắt đầu Selection Sort"));
        int n = a.size();
        for (int i=0;i<n-1;i++){
            int min=i;
            for (int j=i+1;j<n;j++){
                compare(min, j, e, "Tìm min trong đoạn chưa sắp");
                if (cmp.compare(a.get(j), a.get(min)) < 0) min = j;
            }
            if (min!=i) swap(a, i, min, e, "Đưa min về đầu đoạn");
            markFinal(i, e, "Cố định vị trí " + i);
        }
        markFinal(n-1, e, "Hoàn tất");
    }
}
