package collections.pool;

import java.util.EmptyStackException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by Viktor on 6/9/17.
 */
public class UnboundedLockStack<T> implements Pool<T> {

    class Node {
        T data;
        Node next;

        Node(T data) {
            this.data = data;
            this.next = null;
        }
    }

    private Node top = null;
    private Lock lock = new ReentrantLock();

    @Override
    public void set(T item) {
        Node node = new Node(item);

        lock.lock();

        try {
            node.next = top;
            top = node;

        }
        finally {
            lock.unlock();
        }
    }

    @Override
    public T get() {
        lock.lock();

        try {
            if (top == null) {
                throw new EmptyStackException();
            }

            T result = top.data;
            top = top.next;

            return result;
        }
        finally {
            lock.unlock();
        }
    }
}
