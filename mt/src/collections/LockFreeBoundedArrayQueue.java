package collections;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by evikuzn on 6/7/2017.
 */
public class LockFreeBoundedArrayQueue<T> implements Pool<T> {

    private T[] data;
    private volatile AtomicInteger head = new AtomicInteger(0), tail = new AtomicInteger(0);
    private volatile AtomicBoolean headMarked = new AtomicBoolean(false), tailMarked = new AtomicBoolean(false);

    public LockFreeBoundedArrayQueue(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("capacity should be positive integer.");
        }

        data = (T[]) new Object[capacity];
    }

    @Override
    public void set(T item) {
        while (true) {
            int h = head.get(), t = tail.get();

            if (t - h == data.length) {
                continue;
            }

            if (tailMarked.getAndSet(true)) {
                continue;
            }

            if (tail.get() == t && head.get() == h) {
                data[tail.getAndIncrement()%data.length] = item;
                tailMarked.set(false);
                return;
            }
        }
    }

    @Override
    public T get() {
        while (true) {
            int h = head.get(), t = tail.get();

            if (t - h == 0) {
                continue;
            }

            if (headMarked.getAndSet(true)) {
                continue;
            }

            if (head.get() == h  && head.get() == h) {
                T result = data[head.getAndIncrement()%data.length];
                headMarked.set(false);
                return result;
            }

        }


    }
}
