package collections.pool;

import org.junit.Test;

/**
 * Created by evikuzn on 6/5/2017.
 */
public class BoundedArrayQueueTest {
    @Test
    public void testBoundedArrayQueue() {
        Pool<Integer> p = new BoundedArrayQueue<>(3);

        Thread t1 = createSetThread(p, 1);
        Thread t2 = createSetThread(p, 2);
        Thread t3 = createSetThread(p, 3);
        Thread t4 = createSetThread(p, 4);
        Thread t5 = createSetThread(p, 5);
        Thread t6 = createSetThread(p, 6);

        Thread t7 = createGetThread(p);
        Thread t8 = createGetThread(p);
        Thread t9 = createGetThread(p);

        t1.start();
        t2.start();
        t3.start();
        t4.start();
        t5.start();
        t6.start();
        t7.start();
        t8.start();
        t9.start();
    }

    private Thread createSetThread(Pool<Integer> p, int value) {
        return new Thread(() -> {
            System.out.println("BoundedArrayQueue.set " + value);
            p.set(value);
        });
    }

    private Thread createGetThread(Pool<Integer> p) {
        return new Thread(() -> { System.out.println("BoundedArrayQueue.get " + p.get()); });
    }
}
