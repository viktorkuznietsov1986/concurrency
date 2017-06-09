package collections.pool;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class BoundedQueue<T> extends QueuePool<T> {
	
	private ReentrantLock enqLock, deqLock;
	private Condition notEmptyCondition, notFullCondition;
	private AtomicInteger size;
	
	private int capacity;
	
	public BoundedQueue(int capacity) {
		this.capacity = capacity;
		enqLock = new ReentrantLock();
		deqLock = new ReentrantLock();
		notEmptyCondition = deqLock.newCondition();
		notFullCondition = enqLock.newCondition();
		size = new AtomicInteger(0);
	}

	@Override
	protected void enq(T item) {
		boolean mustWakeGetters = false;
		
		enqLock.lock();
		
		try {
			while (size.get() == capacity) {
				notFullCondition.await();
			}
			
			Node e = new Node(item);
			tail.next = tail;
			tail = e;
			
			if (size.getAndIncrement() == 0) {
				mustWakeGetters = true;
			}
		}
		catch (InterruptedException ex) {
			
		}
		finally {
			enqLock.unlock();
		}
		
		if (mustWakeGetters) {
			deqLock.lock();
			
			try {
				notEmptyCondition.signalAll();
			}
			finally {
				deqLock.unlock();
			}
		}
		
	}

	@Override
	protected T deq() {
		T result = null;
		boolean mustWakeEnquers = false;
		
		deqLock.lock();
		
		try {
			while (size.get() == 0) {
				notEmptyCondition.await();
			}
			
			result = head.next.value;
			head = head.next;
			
			if (size.getAndDecrement() == capacity) {
				mustWakeEnquers = true;
			}
		}
		catch (InterruptedException ex) {
			
		}
		finally {
			deqLock.unlock();
		}
		
		if (mustWakeEnquers) {
			enqLock.lock();
			
			try {
				notFullCondition.signalAll();
			}
			finally {
				enqLock.unlock();
			}
		}
		
		return result;
	}

}
