package deque;

public class ArrayDeque<Type> implements Deque<Type>{
    private int frontIndex;
    private int size;
    private Type[] array;

    public ArrayDeque(){
        size = 0;
        frontIndex = 0;
        array = (Type []) new Object[8];
    }

    /**  Take in an index and a direction, return the index of the next node
     *
     * @param index the index of the current node
     * @param offset the value is positive for next node and negative for previous node
     *               offset is equal to or more than -1!
     *
     */
    public int getOffsetIndex(int index, int offset){
        assert (offset >= -1);

        if (isEmpty()){
            //return the index of the sentinel
            return 0;
        }
        if (index + offset > array.length - 1){
            // array[0] is used for sentinel
            return index + offset - array.length + 1;
        } else if (index + offset < 1) {
            return array.length - 1;
        }
        return index + offset;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public Type get(int index) {
        assert (index >= 0 && index < size);
        return array[getOffsetIndex(frontIndex, index)];
    }

    @Override
    public int size() {
        return size;
    }

    public void resize(int n) {
        Type[] resizedArray = (Type[]) new Object[n];
        int frontItemCounts = frontIndex;
        // The current value of size is 1 bigger than the real value.
        int endItemCounts = size - frontItemCounts;
        int updatedFrontIndex = resizedArray.length - endItemCounts;
        System.arraycopy(array, 0, resizedArray, 0, frontItemCounts);
        System.arraycopy(array, frontIndex, resizedArray, updatedFrontIndex, endItemCounts);
        // update the frontIndex
        frontIndex = updatedFrontIndex;
        array = resizedArray;
    }

    @Override
    public void addFirst(Type item) {
        size += 1;
        if (size > array.length - 1){
            resize(array.length*2);
        }
        frontIndex = getOffsetIndex(frontIndex, -1);
        array[frontIndex] = item;
    }

    @Override
    public void addLast(Type item) {
        if (isEmpty()) {
            //If the ArrayDeque is empty, the frontIndex is always 0.
            frontIndex = 1;
        }
        size += 1;
        if (size > array.length - 1){
            resize(array.length*2);
        }
        array[getOffsetIndex(frontIndex, size - 1)] = item;
    }

    @Override
    public Type removeFirst() {
        return null;
    }

    @Override
    public Type removeLast() {
        return null;
    }

    @Override
    public void printDeque() {

    }
}
