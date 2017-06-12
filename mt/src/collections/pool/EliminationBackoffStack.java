package collections.pool;

import locks.Backoff;

import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by evikuzn on 6/12/2017.
 */
public class EliminationBackoffStack<T> extends LockFreeStack<T> {

    private static final int capacity = 100;
    private EliminationArray<T> eliminationArray = new EliminationArray<>(capacity);
    private static ThreadLocal<RangePolicy> policy = new ThreadLocal<RangePolicy>() {
        protected synchronized RangePolicy initialValue() {
            return new RangePolicy(capacity);
        }
    };

    @Override
    public void set(T item) {
        RangePolicy p = policy.get();
        Node n = new Node(item);
        while (true) {
            if (tryPush(n)) {
                return;
            }
            else try {
                T otherValue = eliminationArray.visit(item, p.getRange());

                if (otherValue == null) {
                    p.recordEliminationSuccess();
                    return;
                }
            } catch (TimeoutException e) {
                p.recordEliminationTimeout();
            }
        }
    }

    @Override
    public T get() {
        RangePolicy p = policy.get();

        while (true) {
            Node n = tryPop();

            if (n != null) {
                return n.value;
            }
            else try {
                T otherValue = eliminationArray.visit(null, p.getRange());

                if (otherValue != null) {
                    p.recordEliminationSuccess();
                    return otherValue;
                }
            }
            catch (TimeoutException e) {
                p.recordEliminationTimeout();
            }
        }
    }
}
