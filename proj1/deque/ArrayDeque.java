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
     *
     */
    private int getOffsetIndex(int index, int offset){
        if (index + offset > array.length - 1){
            return index + offset - array.length;
        } else if (index + offset < 0) {
            return index + offset + array.length;
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
        int endItemIndex = getOffsetIndex(frontIndex, size);
        int frontItemCounts, endItemCounts, updatedFrontIndex;
        if (frontIndex <= endItemIndex) {
            // ItemCounts = size + 1(sentinel)
            frontItemCounts = size;
            endItemCounts = 0;
            updatedFrontIndex = frontIndex;
        } else {
            endItemCounts = array.length - frontIndex;
            frontItemCounts = size - endItemCounts;
            updatedFrontIndex = resizedArray.length - endItemIndex;
        }
        System.arraycopy(array, 0, resizedArray, 0, frontItemCounts);
        System.arraycopy(array, frontIndex, resizedArray, updatedFrontIndex, endItemCounts);
        // update the frontIndex
        frontIndex = updatedFrontIndex;
        array = resizedArray;
    }

    @Override
    public void addFirst(Type item) {
        if (size == array.length){
            resize(array.length*2);
        }
        frontIndex = getOffsetIndex(frontIndex, -1);
        array[frontIndex] = item;
        size += 1;
    }

    @Override
    public void addLast(Type item) {
        if (size == array.length){
            resize(array.length*2);
        }
        array[getOffsetIndex(frontIndex, size)] = item;
        if (isEmpty()){
            frontIndex = getOffsetIndex(frontIndex, size);
        }
        size += 1;
    }

    @Override
    public Type removeFirst() {
        if (isEmpty()){
            return null;
        }
        Type x = get(0);
        array[frontIndex] = null;
        frontIndex = getOffsetIndex(frontIndex, 1);
        size -= 1;
        return x;
    }

    @Override
    public Type removeLast() {
        if (isEmpty()){
            return null;
        }
        Type x = get(size - 1);
        array[getOffsetIndex(frontIndex, size)] = null;
        size -= 1;
        return x;
    }

    @Override
    public void printDeque() {

    }
}
