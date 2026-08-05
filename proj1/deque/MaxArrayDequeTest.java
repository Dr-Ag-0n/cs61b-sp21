package deque;

import org.junit.Test;
import static org.junit.Assert.*;
import java.util.Comparator;

public class MaxArrayDequeTest {
    public class maxInteger implements Comparator<Integer> {
        @Override
        public int compare(Integer i1, Integer i2) {
            return i1 - i2;
        }
    }

    public class minInteger implements Comparator<Integer> {
        @Override
        public int compare(Integer i1, Integer i2) {
            return i2 - i1;
        }
    }

    @Test
    public void Test1() {
        maxInteger max = new maxInteger();
        MaxArrayDeque<Integer> MADeque = new MaxArrayDeque<>(max);
        for (int i = 0; i < 100; i++) {
            MADeque.addFirst(i);
        }
        MADeque.addLast(100);
        int maxItem = MADeque.max();
        assertEquals(maxItem, 100);
    }

    @Test
    public void Test2() {
        maxInteger max = new maxInteger();
        minInteger min = new minInteger();
        MaxArrayDeque<Integer> MADeque = new MaxArrayDeque<>(max);
        for (int i = 0; i < 100; i++) {
            MADeque.addFirst(i);
        }
        MADeque.addLast(-1);
        int minItem = MADeque.max(min);
        assertEquals(minItem, -1);
    }
}
