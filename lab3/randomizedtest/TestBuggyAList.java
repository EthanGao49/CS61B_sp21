package randomizedtest;

import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Created by hug.
 */
public class TestBuggyAList {
  // YOUR TESTS HERE
    @Test
    public void testThreeAddThreeRemove(){
        AListNoResizing<Integer> testAListNoResizing = new AListNoResizing<>();
        BuggyAList<Integer> testBuggyAList = new BuggyAList<>();
        testAListNoResizing.addLast(4);
        testAListNoResizing.addLast(5);
        testAListNoResizing.addLast(6);
        testBuggyAList.addLast(4);
        testBuggyAList.addLast(5);
        testBuggyAList.addLast(6);

        assert(testAListNoResizing.removeLast() == testBuggyAList.removeLast());
        assert(testAListNoResizing.removeLast() == testBuggyAList.removeLast());
        assert(testAListNoResizing.removeLast() == testBuggyAList.removeLast());
    }

    @Test
    public void randomizedTest(){
        AListNoResizing<Integer> L = new AListNoResizing<>();
        BuggyAList<Integer> B = new BuggyAList<>();

        int N = 5000;
        for (int i = 0; i < N; i += 1) {
            int operationNumber = StdRandom.uniform(0, 4);
            if (operationNumber == 0) {
                // addLast
                int randVal = StdRandom.uniform(0, 100);
                L.addLast(randVal);
                B.addLast(randVal);
            } else if (operationNumber == 1) {
                // size
                int size = L.size();
                int sizeB = B.size();
                assert (size == sizeB);
            } else if (operationNumber == 2) {
                if (L.size() == 0) return;
                int lastVal = L.getLast();
                int lastValB = B.getLast();
                assert (lastValB == lastVal);
            } else if (operationNumber == 3){
                if (L.size() == 0) return;
                int removedVal = L.removeLast();
                int removedValB = B.removeLast();
                assert (removedValB == removedVal);
            }
        }

    }
}
