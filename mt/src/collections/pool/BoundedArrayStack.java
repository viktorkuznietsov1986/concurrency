package collections.pool;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by Viktor on 6/9/17.
 */
public class BoundedArrayStack<T> implements Pool<T> {

    private T[] data;
    private volatile int top = 0;
    private Lock lock = new ReentrantLock();
    private Condition notEmptyCondition = lock.newCondition();
    private Condition notFullCondition = lock.newCondition();

    public BoundedArrayStack(int capacity) {
        data = (T[]) new Object[capacity];
    }

    @Override
    public void set(T item) {
        lock.lock();

        try {
            while (top == data.length) {
                notFullCondition.await();
            }

            data[top++] = item;

            if (top > 0) {
                notEmptyCondition.signalAll();
            }
        }
        catch (InterruptedException e) {

        }
        finally {
            lock.unlock();
        }
    }

    @Override
    public T get() {
        lock.lock();

        try {
            while (top == 0) {
                notEmptyCondition.await();
            }

            T result = data[--top];
            notFullCondition.signalAll();
            return result;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }

        return null;
    }
}
