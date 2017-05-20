package monitors;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class SimpleReadWriteLock implements ReadWriteLock {
	
	int readers;
	boolean writer;
	Lock readLock, writeLock;
	
	public SimpleReadWriteLock() {
		readers = 0;
		writer = false;
		
		readLock = new ReadLock();
		writeLock = new WriteLock();
	}
	

	@Override
	public Lock readLock() {
		return readLock;
	}

	@Override
	public Lock writeLock() {
		return writeLock;
	}
	
	class ReadLock implements Lock {

		@Override
		public synchronized void lock() {
			try {
				while (writer) {
					SimpleReadWriteLock.this.wait(); 
				}
				
				++readers;
			}
			catch (InterruptedException ex) {
				
			}
		}
		
		@Override
		public synchronized void unlock() {
			
			--readers;
			
			if (readers == 0) {
				SimpleReadWriteLock.this.notifyAll();
			}
			
		}

		@Override
		public void lockInterruptibly() throws InterruptedException {
			// TODO Auto-generated method stub
			
		}

		@Override
		public Condition newCondition() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public boolean tryLock() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
			// TODO Auto-generated method stub
			return false;
		}

		
		
	}
	
	class WriteLock implements Lock {

		@Override
		public synchronized void lock() {
			
			try {
				while (readers > 0 || writer) {
					SimpleReadWriteLock.this.wait();
				}
				
				writer = true;
			}
			catch (InterruptedException ex) {
				
			}
			
		}
		
		@Override
		public synchronized void unlock() {

			writer = false;
			
			SimpleReadWriteLock.this.notifyAll();
			
		}

		@Override
		public void lockInterruptibly() throws InterruptedException {
			// TODO Auto-generated method stub
			
		}

		@Override
		public Condition newCondition() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public boolean tryLock() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
			// TODO Auto-generated method stub
			return false;
		}

		
		
	}

}
