package tester;

import static org.junit.Assert.*;
import org.junit.Test;
import student.StudentArrayDeque;
import edu.princeton.cs.introcs.StdRandom;

public class TestArrayDequeEC {
    @Test
    public void randomizedTest() {
        StudentArrayDeque<Integer> SADeque = new StudentArrayDeque<>();
        ArrayDequeSolution<Integer> ADeque = new ArrayDequeSolution<>();

        StringBuilder printOut = new StringBuilder();

        Integer N = 5000;
        for (Integer i = 0; i < N; i += 1) {
            Integer operationNumber = StdRandom.uniform(0, 5);
            if (operationNumber == 0) {
                // addLast
                Integer randVal = StdRandom.uniform(0, 100);
                printOut.append("addLast(" + randVal + ")\n");
                SADeque.addLast(randVal);
                ADeque.addLast(randVal);
            } else if(operationNumber == 1) {
                // addFirst
                Integer randVal = StdRandom.uniform(0, 100);
                printOut.append("addFirst(" + randVal + ")\n");
                SADeque.addFirst(randVal);
                ADeque.addFirst(randVal);
            } else if (operationNumber == 2) {
                // size
                Integer size = SADeque.size();
                Integer studentSize = ADeque.size();
                printOut.append("size()\n");
                assertEquals(printOut.toString(), size, studentSize);
            } else if (operationNumber == 3) {
                // removeLast
                if (!SADeque.isEmpty()) {
                    Integer removeLast = SADeque.removeLast();
                    Integer studentRemoveLast = ADeque.removeLast();
                    printOut.append("removeLast()\n");
                    assertEquals(printOut.toString(), removeLast, studentRemoveLast);
                }
            } else if (operationNumber == 4) {
                // removeFirst
                if (!SADeque.isEmpty()) {
                    Integer removeFirst = SADeque.removeFirst();
                    Integer studentRemoveFirst = ADeque.removeFirst();
                    printOut.append("removeFirst()\n");
                    assertEquals(printOut.toString(), removeFirst, studentRemoveFirst);
                }
            } else if (operationNumber == 5) {
                // get
                if (!SADeque.isEmpty() && !ADeque.isEmpty()) {
                    Integer randIndex = StdRandom.uniform(0, Math.min(SADeque.size(), ADeque.size()));
                    Integer item = ADeque.get(randIndex);
                    Integer studentItem = SADeque.get(randIndex);
                    printOut.append("get(" + randIndex + ")\n");
                    assertEquals(printOut.toString(), item, studentItem);
                }
            }
        }
    }
}
