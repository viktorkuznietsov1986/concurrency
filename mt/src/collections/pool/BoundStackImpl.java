package collections.pool;

import java.util.EmptyStackException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by evikuzn on 6/13/2017.
 */
public class BoundStackImpl<T> implements Pool<T> {

    class Node {
        T data;
        volatile Node next;
        Node(T data) {
            this.data = data;
            this.next = null;
        }
    }

    private Lock lock = new ReentrantLock();
    private Condition notEnoughPops = lock.newCondition();
    private volatile int pushCount = 0;
    private volatile int popCount = 0;
    private final int difference;
    private volatile Node top = null;

    public BoundStackImpl(int difference) {
        this.difference = difference;
    }

    @Override
    public void set(T item) {
        lock.lock();

        try {
            while (pushCount - popCount == difference) {
                    notEnoughPops.await();
            }

            Node n = new Node(item);
            n.next = top;
            top = n;

            ++pushCount;

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public T get() {
        lock.lock();

        try {
            //if (pushCount-popCount == 0)
              //  throw new EmptyStackException();

            Node n = top;
            top = n.next;

            T result = n.data;
            ++popCount;

            notEnoughPops.signalAll();

            return result;
        } finally {
            lock.unlock();
        }
    }
}
