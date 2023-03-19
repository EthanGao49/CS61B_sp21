package deque;

import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;
import static org.junit.Assert.*;

public class ArrayDequeTest {
    @Test
    public void addGetTest() {
        ArrayDeque<Integer> testArrayDeque = new ArrayDeque<>();

        assertTrue(testArrayDeque.isEmpty());

        testArrayDeque.addFirst(1);
        testArrayDeque.addFirst(2);;
        testArrayDeque.addFirst(3);

        assertTrue(testArrayDeque.size() == 3);
        assertEquals((int) testArrayDeque.get(0), 3);
        assertEquals((int) testArrayDeque.get(1), 2);
        assertEquals((int) testArrayDeque.get(2), 1);

        testArrayDeque.addLast(4);
        testArrayDeque.addLast(5);
        testArrayDeque.addLast(6);

        assertTrue(testArrayDeque.size() == 6);
        assertEquals((int) testArrayDeque.get(3), 4);
        assertEquals((int) testArrayDeque.get(4), 5);
        assertEquals((int) testArrayDeque.get(5), 6);

    }

    @Test
    public void resizeExpandTest() {
        ArrayDeque<Integer> testArrayDeque = new ArrayDeque<>();
        // expand the array
        for (int i = 0; i < 100; i ++) {
            testArrayDeque.addLast(i);
        }
        for (int i = 0; i < 100; i ++) {
            assertEquals((int) testArrayDeque.get(i), i);
        }

        ArrayDeque<Integer> testDeque = new ArrayDeque<>();
        for (int i = 0; i < 100; i ++) {
            testDeque.addFirst(i);
        }
        for (int i = 0; i < 100; i ++) {
            assertEquals((int) testDeque.get(i), 99 - i);
        }

    }

    @Test
    public void removeTest() {
        ArrayDeque<Integer> testArrayDeque = new ArrayDeque<>();
        for (int i = 0; i < 100; i ++) {
            testArrayDeque.addLast(i);
        }
        for (int i = 0; i < 100; i ++) {
            assertEquals((int) testArrayDeque.removeFirst(), i);
        }

        for (int i = 0; i < 100; i++) {
            testArrayDeque.addFirst(i);
        }
        for (int i = 0; i < 100; i++) {
            assertEquals((int) testArrayDeque.removeLast(), i);
        }

    }

    @Test
    public void removeEmptyTest() {
        ArrayDeque<Integer> testArrayDeque = new ArrayDeque<>();

        assertEquals(null, testArrayDeque.removeFirst());
        assertEquals(null, testArrayDeque.removeLast());
    }

    @Test
    public void resizeShortenTest() {
        ArrayDeque<Integer> testArrayDeque = new ArrayDeque<>();

        for (int i = 0; i < 100; i++) {
            testArrayDeque.addLast(i);
        }
        for (int i = 0; i < 90; i++) {
            testArrayDeque.removeLast();
        }
        testArrayDeque.resize(20);
        for (int i = 0; i < testArrayDeque.size(); i++) {
            assertEquals(i, (int) testArrayDeque.get(i));
        }
    }

    @Test
    public void randomizedTest(){
        ArrayDeque<Integer> L = new ArrayDeque<>();

        int N = 10000;
        for (int i = 0; i < N; i += 1) {
            int operationNumber = StdRandom.uniform(0, 4);
            if (operationNumber == 0) {
                // addLast
                int randVal = StdRandom.uniform(0, 100);
                L.addLast(randVal);
            } else if (operationNumber == 1) {
                // addFirst
                int randVal = StdRandom.uniform(0, 100);
                L.addFirst(randVal);
            } else if (operationNumber == 2) {
                // removeLast
                if (L.isEmpty()) return;
                int lastVal = L.get(L.size() - 1);
                assert (L.removeLast() == lastVal);
            } else if (operationNumber == 3) {
                // removeFirst
                if (L.isEmpty()) return;
                int firstVal = L.get(0);
                assert (L.removeFirst() == firstVal);
            }
        }

    }
}
