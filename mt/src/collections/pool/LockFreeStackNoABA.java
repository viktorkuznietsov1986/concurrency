package collections.pool;

import locks.Backoff;

import java.util.EmptyStackException;
import java.util.concurrent.atomic.AtomicStampedReference;

/**
 * Created by Viktor on 6/9/17.
 */
public class LockFreeStackNoABA<T> implements Pool<T> {

    class Node {
        public T value;
        public Node next;
        public Node(T value) {
            this.value = value;
            this.next = null;
        }
    }

    private AtomicStampedReference<Node> top = new AtomicStampedReference<>(null, 0);
    private static final int MIN_DELAY = 10;
    private static final int MAX_DELAY = 1000;
    private Backoff backoff = new Backoff(MIN_DELAY, MAX_DELAY);
    private ThreadLocal<Node> nodesToDelete = new ThreadLocal<>();


    @Override
    public void set(T item) {
        while (true) {
            if (tryPush(item)) {
                return;
            }
            else {
                try {
                    backoff.backoff();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public T get() {
        while (true) {
            Node n = tryPop();

            if (n != null) {
                n.next = nodesToDelete.get();
                nodesToDelete.set(n);
                return n.value;
            }
            else {
                try {
                    backoff.backoff();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private boolean tryPush(T item) {
        int[] stamp = {0};
        Node oldNode = top.get(stamp);
        Node newNode = new Node(item);
        newNode.next = oldNode;

        return top.compareAndSet(oldNode, newNode, stamp[0], stamp[0]+1);
    }

    private Node tryPop() {
        int[] stamp = {0};
        Node n = top.get(stamp);

        if (n == null) {
            throw new EmptyStackException();
        }

        if (top.compareAndSet(n, n.next, stamp[0], stamp[0]+1)) {
            return n;
        }

        return null;
    }
}
