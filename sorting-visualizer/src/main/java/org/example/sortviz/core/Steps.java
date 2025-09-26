package org.example.sortviz.core;
import java.util.Collections;
import java.util.List;


public final class Steps {
    private Steps() {}


    public static <T> void compare(int i, int j, StepEmitter<T> e, String note){ e.emit(Step.compare(i,j,note)); }


    public static <T> void swap(List<T> a, int i, int j, StepEmitter<T> e, String note){
        Collections.swap(a, i, j);
        e.emit(Step.swap(i, j, note));
    }


    public static <T> void set(List<T> a, int i, T v, StepEmitter<T> e, String note){
        a.set(i, v);
        e.emit(Step.set(i, v, note));
    }


    public static <T> void markFinal(int i, StepEmitter<T> e, String note){ e.emit(Step.markFinal(i, note)); }
}