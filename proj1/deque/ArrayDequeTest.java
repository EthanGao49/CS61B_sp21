package deque;

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
    public void resizeTest() {
        ArrayDeque<Integer> testArrayDeque = new ArrayDeque<>();

        for (int i = 0; i < 100; i ++){
            testArrayDeque.addLast(i);
        }
        for (int i = 0; i < 100; i ++){
            assertEquals((int) testArrayDeque.get(i), i);
        }
    }
}
