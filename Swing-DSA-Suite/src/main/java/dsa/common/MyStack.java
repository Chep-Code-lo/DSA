package dsa.common;
import java.util.ArrayDeque;
import java.util.Deque;

public class MyStack<E> {
    private final Deque<E> d = new ArrayDeque<>();
    public void push(E x){ d.push(x); }
    public E pop(){ if(d.isEmpty()) throw new IllegalStateException("Stack empty"); return d.pop(); }
    public E peek(){ if(d.isEmpty()) throw new IllegalStateException("Stack empty"); return d.peek(); }
    public boolean isEmpty(){ return d.isEmpty(); }
    public int size(){ return d.size(); }
    public void clear(){ d.clear(); }
    @Override public String toString(){ return d.toString(); }
}
