package bstmap;

import org.junit.Test;

public class MyTest {
    @Test
    public void printTest() {
        BSTMap<Integer, Integer> t = new BSTMap<>();
        t.put(4, 4);
        t.put(2, 2);
        t.put(6, 6);
        t.put(1, 1);
        t.put(3, 3);
        t.put(5, 5);
        t.put(7, 7);
        t.printInOrder();
    }
}
