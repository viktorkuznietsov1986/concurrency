package monitors;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SharedBathroomLocks implements SharedBathroom {
	private Lock lock = new ReentrantLock();
	private Condition femaleCanEnter = lock.newCondition();
	private Condition maleCanEnter = lock.newCondition();
	
	private int malesInBathroom = 0;
	private int femalesInBathroom = 0;

	@Override
	public void enterMale() {
		
		lock.lock();
		
		try {
			while (femalesInBathroom > 0) {
				maleCanEnter.await();
			}
			
			++malesInBathroom;
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally {
			lock.unlock();
		}
		
	}

	@Override
	public void leaveMale() {
		lock.lock();
		
		try {
			--malesInBathroom;
			
			if (malesInBathroom == 0) {
				femaleCanEnter.signalAll();
			}
		}
		finally {
			lock.unlock();
		}
		
	}

	@Override
	public void enterFemale() {
		lock.lock();
		
		try {
			while (malesInBathroom > 0) {
				femaleCanEnter.await();
			}
			
			++femalesInBathroom;
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally {
			lock.unlock();
		}
		
	}

	@Override
	public void leaveFemale() {
		lock.lock();
		
		try {
			--femalesInBathroom;
			
			if (femalesInBathroom == 0) {
				maleCanEnter.signalAll();
			}
		}
		finally {
			lock.unlock();
		}
		
	}
	
	

}
