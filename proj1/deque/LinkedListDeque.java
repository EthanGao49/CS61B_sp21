package deque;

public class LinkedListDeque<T> implements Deque<T>{

    public class DequeNode {
        public T item;
        public DequeNode next;
        public DequeNode last;
        public DequeNode(T i, DequeNode l, DequeNode n){
            item = i;
            next = n;
            last = l;
        }
    }

    private int size;
    private DequeNode sentinel;

    public LinkedListDeque() {
        size = 0;
        sentinel = new DequeNode(null, null, null);
        //Reduce the number of special cases
        sentinel.last = sentinel;
        sentinel.next = sentinel;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
       return size == 0;
    }

    @Override
    public void addFirst(T item) {
        DequeNode firstNoNode = sentinel.next;
        sentinel.next = new DequeNode(item, sentinel, firstNoNode);
        firstNoNode.last = sentinel.next;
        size += 1;
    }

    @Override
    public void addLast(T item) {
        DequeNode lastNode = sentinel.last;
        sentinel.last = new DequeNode(item, lastNode, sentinel);
        lastNode.next = sentinel.last;
        size += 1;
    }

    @Override
    public T removeFirst() {
        if (isEmpty()) {
            return null;
        }
        T x = sentinel.next.item;
        sentinel.next = sentinel.next.next;
        sentinel.next.last = sentinel;
        size -= 1;
        return x;
    }

    @Override
    public T removeLast() {
       if (isEmpty()) {
           return null;
       }
       T x = sentinel.last.item;
       sentinel.last = sentinel.last.last;
       sentinel.last.next = sentinel;
       size -= 1;
       return x;
    }

    @Override
    public T get(int index) {
       if (index > size || isEmpty()){
           return null;
       }
       DequeNode p = sentinel.next;
       while (index > 0){
           index -= 1;
           p = p.next;
       }
       return p.item;
    }

    @Override
    public void printDeque() {
        DequeNode p = sentinel.next;
        for (int i = 0; i < size; i++){
            System.out.print(p.item + " ");
            p = p.next;
        }
    }
}
