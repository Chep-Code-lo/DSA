package org.example.sortviz.core;


import org.example.sortviz.algorithms.exchange.BubbleSort;
import org.example.sortviz.algorithms.exchange.CocktailShakerSort;
import org.example.sortviz.algorithms.insertion.BinaryInsertionSort;
import org.example.sortviz.algorithms.insertion.InsertionSort;
import org.example.sortviz.algorithms.merge.MergeSortTD;
import org.example.sortviz.algorithms.selection.HeapSort;
import org.example.sortviz.algorithms.selection.SelectionSort;


import java.util.*;
import java.util.stream.Collectors;


public final class AlgorithmRegistry {
    private AlgorithmRegistry() {}


    private static final List<SortingAlgorithm<Integer>> ALL_INT = List.of(
            new InsertionSort<>(), new BinaryInsertionSort<>(),
            new BubbleSort<>(), new CocktailShakerSort<>(),
            new SelectionSort<>(), new HeapSort<>(),
            new MergeSortTD<>()
    );


    public static List<AlgorithmCategory> categories(){ return Arrays.asList(AlgorithmCategory.values()); }


    public static List<SortingAlgorithm<Integer>> byCategory(AlgorithmCategory cat){
        return ALL_INT.stream().filter(a -> a.getCategory()==cat).collect(Collectors.toList());
    }
}