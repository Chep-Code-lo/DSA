package org.example.sortviz.algorithms.insertion;
import org.example.sortviz.core.*;

import java.util.Comparator;
import java.util.List;

import static org.example.sortviz.core.Steps.*;

public class BinaryInsertionSort<T> implements SortingAlgorithm<T> {
    @Override public String getName() { return "Xen vào nhị phân (Binary Insertion)"; }
    @Override public AlgorithmCategory getCategory() { return AlgorithmCategory.INSERTION; }

    @Override
    public void sort(List<T> a, Comparator<? super T> cmp, StepEmitter<T> e) {
        e.emit(Step.note("Bắt đầu Binary Insertion Sort"));
        for (int i=1;i<a.size();i++){
            T key = a.get(i);
            int lo = 0, hi = i;
            while (lo < hi){
                int mid = (lo+hi)/2;
                compare(mid, i, e, "So sánh key với a["+mid+"]");
                if (cmp.compare(key, a.get(mid)) < 0) hi = mid; else lo = mid+1;
            }
            // shift right from lo..i-1
            for (int j=i; j>lo; j--){ set(a, j, a.get(j-1), e, "Dịch sang phải"); }
            set(a, lo, key, e, "Chèn key tại vị trí tìm bằng nhị phân");
            markFinal(lo, e, "Đã đúng vị trí");
        }
        for (int i=0;i<a.size();i++) markFinal(i, e, "Hoàn tất");
    }
}
