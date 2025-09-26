package org.example.sortviz.core;

import java.util.Comparator;
import java.util.List;


public interface SortingAlgorithm<T> {
    String getName();
    AlgorithmCategory getCategory();


    /**
     * Implementations should mutate the list and emit steps via emitter.
     */
    void sort(List<T> a, Comparator<? super T> cmp, StepEmitter<T> emitter);
}