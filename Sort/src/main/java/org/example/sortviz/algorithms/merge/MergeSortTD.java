package org.example.sortviz.algorithms.merge;
import org.example.sortviz.core.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.example.sortviz.core.Steps.*;

public class MergeSortTD<T> implements SortingAlgorithm<T> {
    @Override public String getName(){ return "Trộn trên‑xuống (Merge Top‑Down)"; }
    @Override public AlgorithmCategory getCategory(){ return AlgorithmCategory.MERGE; }

    @Override
    public void sort(List<T> a, Comparator<? super T> cmp, StepEmitter<T> e) {
        e.emit(Step.note("Bắt đầu Merge Sort TD"));
        List<T> aux = new ArrayList<>(a);
        sort(a, aux, 0, a.size()-1, cmp, e);
        for (int i=0;i<a.size();i++) markFinal(i, e, "Hoàn tất");
    }

    private void sort(List<T> a, List<T> aux, int lo, int hi, Comparator<? super T> cmp, StepEmitter<T> e){
        if (lo>=hi) return;
        int mid = lo + (hi-lo)/2;
        sort(a, aux, lo, mid, cmp, e);
        sort(a, aux, mid+1, hi, cmp, e);
        merge(a, aux, lo, mid, hi, cmp, e);
    }

    private void merge(List<T> a, List<T> aux, int lo, int mid, int hi, Comparator<? super T> cmp, StepEmitter<T> e){
        for (int k=lo;k<=hi;k++) aux.set(k, a.get(k));
        int i=lo, j=mid+1;
        for (int k=lo;k<=hi;k++){
            if (i>mid) { set(a, k, aux.get(j++), e, "Sao chép phải"); }
            else if (j>hi) { set(a, k, aux.get(i++), e, "Sao chép trái"); }
            else {
                compare(i, j, e, "Chọn nhỏ hơn để trộn");
                if (cmp.compare(aux.get(j), aux.get(i)) < 0) set(a, k, aux.get(j++), e, "Lấy từ nửa phải");
                else set(a, k, aux.get(i++), e, "Lấy từ nửa trái");
            }
        }
    }
}
