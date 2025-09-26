package org.example.sortviz.core;
@FunctionalInterface
public interface StepEmitter<T> {
    void emit(Step<T> step);
}