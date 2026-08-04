package flik;

import org.junit.Test;
import static org.junit.Assert.*;

public class FlikTest {
    @Test
    public void Test1() {
        int i = 0;
        int j = 0;
        while(i < 500) {
            boolean out = Flik.isSameNumber(i, j);
            assertTrue(out);
            i++;
            j++;
        }
    }

    @Test
    public void Test2() {
        int i = 128;
        int j = 128;
        boolean out = Flik.isSameNumber(i, j);
        assertTrue(out);
    }
}
