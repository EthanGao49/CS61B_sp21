package deque;

import org.junit.Test;

import java.util.Comparator;
import java.util.Optional;

import static org.junit.Assert.assertEquals;

public class MaxArrayDequeTest {
    private class IntComparator implements Comparator<Integer> {
        public int compare(Integer o1, Integer o2) {
            if (o1 > o2) {
                return 1;
            } else if (o1 < o2) {
                return -1;
            }
            return 0;
        }
    }
    @Test
    public void maxTest() {
        Comparator<Integer> c = new IntComparator();
        MaxArrayDeque<Integer> testDeque = new MaxArrayDeque<Integer>(c);

        for (int i = 0; i < 100; i++) {
            testDeque.addFirst(i);
        }

        assertEquals(100, testDeque.size());
        for (int i = 0; i < 100; i++) {
            assertEquals(99 - i, (int) testDeque.get(i));
        }
        int maxValue = testDeque.max();
        assertEquals(99, maxValue);
    }
}
