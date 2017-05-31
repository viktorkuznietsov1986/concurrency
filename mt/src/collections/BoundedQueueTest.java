package collections;

import static org.junit.Assert.*;

import org.junit.Test;

public class BoundedQueueTest {
	
	@Test
	public void testBoundedQueue() {
		Pool<Integer> p = new BoundedQueue<Integer>(3);
		
		Thread t1 = createSetThread(p, 1);
		Thread t2 = createSetThread(p, 1);
		Thread t3 = createSetThread(p, 1);
		Thread t4 = createSetThread(p, 1);
		Thread t5 = createSetThread(p, 1);
		Thread t6 = createSetThread(p, 1);
		
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
			p.set(value);
		});
	}
	
	private Thread createGetThread(Pool<Integer> p) {
		return new Thread(() -> { p.get(); });
	}

}
