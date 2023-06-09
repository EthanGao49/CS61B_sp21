package deque;

import java.util.Iterator;

public interface Deque<T> extends Iterable<T>{
    void addFirst(T item);
    void addLast(T item);
    boolean isEmpty();
    int size();
    void printDeque();
    T removeLast();
    T removeFirst();
    T get(int index);
}
