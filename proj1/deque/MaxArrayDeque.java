package deque;

import java.util.Comparator;

public class MaxArrayDeque<T> extends ArrayDeque<T> {
    private Comparator<T> comparator;

    public MaxArrayDeque(Comparator<T> c) {
        super();
        comparator = c;
    }

    public T max() {
        int size = this.size();
        if (size == 0) {
            return null;
        }
        int maxIndex = 0;
        for (int i = 1; i < size; i++) {
            T max = this.get(maxIndex);
            T item = this.get(i);
            if (comparator.compare(max, item) < 0) {
                maxIndex = i;
            }
        }
        return this.get(maxIndex);
    }

    public T max(Comparator<T> c) {
        int size = this.size();
        if (size == 0) {
            return null;
        }
        int maxIndex = 0;
        for (int i = 1; i < size; i++) {
            T max = this.get(maxIndex);
            T item = this.get(i);
            if (c.compare(max, item) < 0) {
                maxIndex = i;
            }
        }
        return this.get(maxIndex);
    }
}
