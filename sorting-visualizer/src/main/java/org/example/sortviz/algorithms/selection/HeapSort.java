package org.example.sortviz.algorithms.selection;
import org.example.sortviz.core.*;

import java.util.Comparator;
import java.util.List;

import static org.example.sortviz.core.Steps.*;

public class HeapSort<T> implements SortingAlgorithm<T> {
    @Override public String getName(){ return "Heap Sort (Lựa chọn bằng Heap)"; }
    @Override public AlgorithmCategory getCategory(){ return AlgorithmCategory.SELECTION; }

    @Override
    public void sort(List<T> a, Comparator<? super T> cmp, StepEmitter<T> e) {
        e.emit(Step.note("Xây Max-Heap"));
        int n = a.size();
        for (int i=n/2 - 1; i>=0; i--) sink(a, n, i, cmp, e);
        e.emit(Step.note("Rút gốc và heapify lại"));
        for (int end=n-1; end>0; end--){
            swap(a, 0, end, e, "Đưa max về cuối");
            markFinal(end, e, "Cố định cuối mảng");
            sink(a, end, 0, cmp, e);
        }
        markFinal(0, e, "Hoàn tất");
    }

    private void sink(List<T> a, int n, int i, Comparator<? super T> cmp, StepEmitter<T> e){
        while (true){
            int l = 2*i+1, r=2*i+2, largest = i;
            if (l<n){ compare(largest, l, e, "So với trái"); if (cmp.compare(a.get(l), a.get(largest))>0) largest = l; }
            if (r<n){ compare(largest, r, e, "So với phải"); if (cmp.compare(a.get(r), a.get(largest))>0) largest = r; }
            if (largest==i) break;
            swap(a, i, largest, e, "Đổi với con lớn hơn");
            i = largest;
        }
    }
}
