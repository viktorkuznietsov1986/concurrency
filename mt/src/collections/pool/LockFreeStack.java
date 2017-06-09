package collections.pool;

import locks.Backoff;

import java.util.EmptyStackException;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by evikuzn on 6/8/2017.
 */
public class LockFreeStack<T> implements Pool<T> {

    class Node {
        public T value;
        public Node next;
        public Node(T value) {
            this.value = value;
            this.next = null;
        }
    }

    private AtomicReference<Node> top = new AtomicReference<>(null);
    private static final int MIN_DELAY = 10;
    private static final int MAX_DELAY = 1000;
    private Backoff backoff = new Backoff(MIN_DELAY, MAX_DELAY);

    @Override
    public void set(T item) {
        Node node = new Node(item);

        while (true) {
            if (tryPush(node)) {
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

    protected boolean tryPush(Node node) {
        Node oldTop = top.get();
        node.next = oldTop;
        return top.compareAndSet(oldTop, node);
    }

    protected Node tryPop() throws EmptyStackException {
        Node oldTop = top.get();

        if (oldTop == null) {
            throw new EmptyStackException();
        }
        else {
            Node newTop = oldTop.next;
            if (top.compareAndSet(oldTop, newTop)) {
                return oldTop;
            }
            else {
                return null;
            }
        }
    }
}
