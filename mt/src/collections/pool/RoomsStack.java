package collections.pool;

import monitors.Rooms;

import java.util.EmptyStackException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by evikuzn on 6/14/2017.
 */
public class RoomsStack<T> implements Pool<T> {

    private static final int PUSH = 0, POP = 1;

    private AtomicInteger top;
    private T[] items;
    private Rooms rooms;

    public RoomsStack(int capacity) {
        top = new AtomicInteger(0);
        items = (T[]) new Object[capacity];
        rooms = new Rooms(2);
    }

    @Override
    public void set(T item) {

        while (true) {
            rooms.enter(PUSH);

            try {
                int i = top.getAndIncrement();

                if (i >= items.length) {
                    top.getAndDecrement();

                    rooms.setExitHandler(PUSH, () -> {
                        if (i >= items.length) {
                            T[] nItems = (T[]) new Object[items.length * 2];
                            for (int j = 0; j < items.length; ++j) {
                                nItems[j] = items[j];
                            }

                            items = nItems;
                        }
                    });

                    continue;
                }

                items[i] = item;
                return;
            } finally {
                rooms.exit();
            }
        }


    }

    @Override
    public T get() {

        rooms.enter(POP);

        try {
            int i = top.getAndDecrement()-1;

            if (i < 0) {
                top.getAndIncrement();
                throw new EmptyStackException();
            }

            return items[i];
        }
        finally {
            rooms.exit();
        }

    }
}
