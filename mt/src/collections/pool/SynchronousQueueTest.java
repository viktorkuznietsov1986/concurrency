package collections.pool;

import org.junit.Test;

public class SynchronousQueueTest {

	@Test
	public void testSynchronousQueue() {
		Pool<Integer> p = new SynchronousQueue<Integer>();
		
		Thread t1 = createSetThread(p, 1);
		Thread t2 = createSetThread(p, 2);
		Thread t3 = createSetThread(p, 3);
		Thread t4 = createSetThread(p, 4);
		Thread t5 = createSetThread(p, 5);
		Thread t6 = createSetThread(p, 6);
		
		Thread t7 = createGetThread(p);
		Thread t8 = createGetThread(p);
		Thread t9 = createGetThread(p);
		Thread t10 = createGetThread(p);
		Thread t11 = createGetThread(p);
		Thread t12 = createGetThread(p);
		
		t1.start();
		t2.start();
		t3.start();
		t4.start();
		t5.start();
		t6.start();
		t7.start();
		t8.start();
		t9.start();
		t10.start();
		t11.start();
		t12.start();
	}
	
	private Thread createSetThread(Pool<Integer> p, int value) {
		return new Thread(() -> {
			System.out.println("SynchronousQueue.set " + value);
			p.set(value);
		});
	}
	
	private Thread createGetThread(Pool<Integer> p) {
		return new Thread(() -> { 
			System.out.println("SynchronousQueue.get " + p.get()); 
			
		});
	}


}
