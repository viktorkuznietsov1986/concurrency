package monitors;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class SimpleReentrantLock implements Lock {

	long owner, holdCount;
	
	public SimpleReentrantLock() {
		owner = holdCount = 0;
	}
	
	@Override
	public synchronized void lock() {
		long me = Thread.currentThread().getId();
		
		try {
			if (owner == me) {
				++holdCount;
				return;
			}
			
			while (holdCount != 0) {
				this.wait();
			}
			
			owner = me;
			holdCount = 1;
		}
		catch (InterruptedException ex) {
			
		}
		
		
		
	}
	
	@Override
	public synchronized void unlock() {
		long me = Thread.currentThread().getId();
		
		if (owner != me || holdCount == 0) {
			throw new IllegalMonitorStateException();
		}
		
		--holdCount;
		
		if (holdCount == 0) {
			this.notify();
		}
		
	}

	@Override
	public void lockInterruptibly() throws InterruptedException {
		throw new UnsupportedOperationException();
		
	}

	@Override
	public Condition newCondition() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean tryLock() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
		throw new UnsupportedOperationException();
	}

	

}
