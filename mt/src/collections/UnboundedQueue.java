package collections;

import java.util.EmptyStackException;
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
			Node e = new Node(item);
			tail.next = e;
			tail = e;
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
			if (head.next == null) {
				throw new EmptyStackException();
			}
			
			result = head.next.value;
			head = head.next;
		}
		finally {
			deqLock.unlock();
		}
		
		return result;
	}

}
