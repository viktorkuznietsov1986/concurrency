package collections;

import java.util.concurrent.locks.ReentrantLock;

public class UnboundedQueue<T> extends QueuePool<T> {
	
	private ReentrantLock enqLock, deqLock;
	
	public UnboundedQueue() {
		enqLock = new ReentrantLock();
		deqLock = new ReentrantLock();
	}

	@Override
	protected void enq(T item) {
		enqLock.lock();
		
		try {
			
		}
		finally {
			enqLock.unlock();
		}
		
	}

	@Override
	protected T deq() {
		T result = null;
		deqLock.lock();
		
		try {
			
		}
		finally {
			deqLock.unlock();
		}
		
		return result;
	}

}
