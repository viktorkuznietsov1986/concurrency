package collections.pool;

import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by evikuzn on 6/12/2017.
 */
public class EliminationArray<T> {
    private static final int duration = 1000;
    private LockFreeExchanger<T>[] exchanger;
    private Random random;

    public EliminationArray(int capacity) {
        exchanger = (LockFreeExchanger<T>[]) new LockFreeExchanger[capacity];

        for (int i = 0; i < capacity; ++i) {
            exchanger[i] = new LockFreeExchanger<>();
        }

        random = new Random();
    }

    public T visit(T value, int range) throws TimeoutException {
        int slot = random.nextInt(range);

        return (exchanger[slot].exchange(value, duration, TimeUnit.MILLISECONDS));
    }
}
