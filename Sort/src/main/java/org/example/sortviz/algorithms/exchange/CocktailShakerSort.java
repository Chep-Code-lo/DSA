package org.example.sortviz.algorithms.exchange;
import org.example.sortviz.core.*;

import java.util.Comparator;
import java.util.List;

import static org.example.sortviz.core.Steps.*;

public class CocktailShakerSort<T> implements SortingAlgorithm<T> {
    @Override public String getName(){ return "Đổi chỗ lắc (Cocktail Shaker)"; }
    @Override public AlgorithmCategory getCategory(){ return AlgorithmCategory.EXCHANGE; }

    @Override
    public void sort(List<T> a, Comparator<? super T> cmp, StepEmitter<T> e) {
        e.emit(Step.note("Bắt đầu Cocktail Shaker Sort"));
        int start = 0, end = a.size()-1;
        boolean swapped = true;
        while (swapped){
            swapped = false;
            for (int j=start; j<end; j++){
                compare(j, j+1, e, "Forward pass");
                if (cmp.compare(a.get(j), a.get(j+1)) > 0){ swap(a, j, j+1, e, "Đổi chỗ"); swapped = true; }
            }
            markFinal(end, e, "Cố định phía phải"); end--;
            if (!swapped) break;
            swapped = false;
            for (int j=end; j>start; j--){
                compare(j-1, j, e, "Backward pass");
                if (cmp.compare(a.get(j-1), a.get(j)) > 0){ swap(a, j-1, j, e, "Đổi chỗ"); swapped = true; }
            }
            markFinal(start, e, "Cố định phía trái"); start++;
        }
        for (int k=start;k<=end;k++) markFinal(k, e, "Hoàn tất");
    }
}
