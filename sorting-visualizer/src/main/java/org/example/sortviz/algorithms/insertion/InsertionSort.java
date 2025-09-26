package org.example.sortviz.algorithms.insertion;

import org.example.sortviz.core.*;

import java.util.Comparator;
import java.util.List;

import static org.example.sortviz.core.Steps.*;

public class InsertionSort<T> implements SortingAlgorithm<T> {
    @Override public String getName() { return "Xen vào trực tiếp (Insertion)"; }
    @Override public AlgorithmCategory getCategory() { return AlgorithmCategory.INSERTION; }

    @Override
    public void sort(List<T> a, Comparator<? super T> cmp, StepEmitter<T> e) {
        e.emit(Step.note("Bắt đầu Insertion Sort"));
        for (int i=1;i<a.size();i++){
            T key = a.get(i);
            int j = i-1;
            e.emit(Step.note("Chọn key = a["+i+"]"));
            while (j>=0){
                compare(j, i, e, "So sánh a["+j+"] với key");
                if (cmp.compare(a.get(j), key) > 0){
                    set(a, j+1, a.get(j), e, "Dịch sang phải");
                    j--;
                } else break;
            }
            set(a, j+1, key, e, "Chèn key vào vị trí đúng");
            markFinal(j+1, e, "Đã đúng vị trí");
        }
        for (int i=0;i<a.size();i++) markFinal(i, e, "Hoàn tất");
    }
}
