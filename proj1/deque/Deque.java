package deque;

import java.util.Iterator;

public interface Deque<Type> {
    void addFirst(Type item);
    void addLast(Type item);
    boolean isEmpty();
    int size();
    void printDeque();
    Type removeLast();
    Type removeFirst();
    Type get(int index);
}
