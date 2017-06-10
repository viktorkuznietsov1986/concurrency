package collections.pool;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Viktor on 6/9/17.
 */
public class LockFreeBoundedArrayStack<T> implements Pool<T> {

    private T[] data;
    private AtomicInteger top = new AtomicInteger(0);
    private AtomicBoolean marked = new AtomicBoolean(false);

    public LockFreeBoundedArrayStack(int capacity) {
        if (capacity <= 0)
            throw new IllegalArgumentException();

        data = (T[]) new Object[capacity];
    }

    @Override
    public void set(T item) {
        while (true) {
            int t = top.get();

            if (t == data.length)
                continue;

            if (marked.getAndSet( true))
                continue;

            boolean succeeded = false;

            if (t == top.get()) {
                data[top.getAndIncrement()] = item;
                succeeded = true;
            }

            marked.set(false);

            if (succeeded)
                return;


        }
    }

    @Override
    public T get() {
        while (true) {
            int t = top.get();

            if (t == 0)
                continue;

            if (marked.getAndSet(true))
                continue;

            T result = null;
            boolean succeeded = false;
            if (t == top.get()) {
                result = data[top.decrementAndGet()];
                succeeded = true;
            }

            marked.set(false);

            if (succeeded)
                return result;


        }
    }
}
