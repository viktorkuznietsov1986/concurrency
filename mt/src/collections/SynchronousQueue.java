package collections;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SynchronousQueue<T> implements Pool<T> {
	
	private volatile T item;
	private Lock lock;
	private Condition condition;
	private boolean enqueing;
	
	public SynchronousQueue() {
		item = null;
		lock = new ReentrantLock();
		condition = lock.newCondition();
		enqueing = false;
	}

	@Override
	public void set(T item) {
		lock.lock();
		
		try {
			while (enqueing) {
				condition.await();
			}
			
			enqueing = true;
			
			this.item = item;
			condition.signalAll();
			
			while (this.item != null) {
				condition.await();
			}
			
			enqueing = false;
			condition.signalAll();
		}
		catch (InterruptedException e) {
			
		}
		finally {
			lock.unlock();
		}
		
	}

	@Override
	public T get() {
		T result = null;
		lock.lock();
		
		try {
			while (this.item == null) {
				condition.await();
			}
			
			result = this.item;
			this.item = null;
			condition.signalAll();
		}
		catch (InterruptedException ex) {
			
		}
		finally {
			lock.unlock();
		}
		
		return result;
	}

}
