package collections;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by evikuzn on 6/5/2017.
 */
public class BoundedArrayQueue<T> implements Pool<T> {

    private Lock enqLock = new ReentrantLock();
    private Lock deqLock = new ReentrantLock();
    private Condition notFullCondition = enqLock.newCondition();
    private Condition notEmptyCondition = deqLock.newCondition();
    private volatile AtomicInteger head = new AtomicInteger(0), tail = new AtomicInteger(0);
    private T[] data;

    public BoundedArrayQueue(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("capacity should be at least 1.");
        }

        data = (T[]) new Object[capacity];
    }

    @Override
    public void set(T item) {
        boolean canWakeDequers = false;

        enqLock.lock();

        try {
            while (tail.get() - head.get() == data.length) {
                notFullCondition.await();
            }

            data[tail.getAndIncrement() % data.length] = item;
            canWakeDequers = true;
        }
        catch (InterruptedException e) {

        }
        finally {
            enqLock.unlock();
        }

        if (canWakeDequers) {
            deqLock.lock();
            try {
                notEmptyCondition.signalAll();
            }
            finally {
                deqLock.unlock();
            }
        }

    }

    @Override
    public T get() {

        T result = null;
        boolean canWakeEnquers = false;

        deqLock.lock();

        try {
            while (tail.get() - head.get() == 0) {
                notEmptyCondition.await();
            }

            result = data[head.getAndIncrement() % data.length];

            canWakeEnquers = true;
        } catch (InterruptedException e) {

        } finally {
            deqLock.unlock();
        }

        if (canWakeEnquers) {
            enqLock.lock();

            try {
                notFullCondition.signalAll();
            }
            finally{
                enqLock.unlock();
            }
    }

        return result;
    }
}
