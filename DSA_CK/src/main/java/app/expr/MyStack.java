package app.expr;

import java.util.*;

public class MyStack<E> {
  private final ArrayList<E> a = new ArrayList<>();

  public void push(E x) {
    a.add(x);
  }

  public E pop() {
    if (a.isEmpty()) throw new EmptyStackException();
    return a.remove(a.size() - 1);
  }

  public E peek() {
    if (a.isEmpty()) throw new EmptyStackException();
    return a.get(a.size() - 1);
  }

  public boolean isEmpty() {
    return a.isEmpty();
  }

  public int size() {
    return a.size();
  }

  public void clear() {
    a.clear();
  }

  // Return a snapshot with top element first
  List<E> snapshotTopFirst() {
    ArrayList<E> s = new ArrayList<>();
    for (int i = a.size() - 1; i >= 0; i--) s.add(a.get(i));
    return s;
  }
}
