package monitors;

public class Rooms {
	private final int numberOfRooms;
	private int numberOfThreads = 0;
	private int roomId = -1;
	private Handler handler = null;
	
	private boolean exitDone = true;
	
	public interface Handler {
		void onEmpty();
	}
	
	public Rooms(int m) {
		numberOfRooms = m;
	}
	
	public synchronized void enter(int i) {
		if (i < 0 && i >= numberOfRooms)
			throw new IllegalArgumentException();
		
		try {
			while (roomId != i && numberOfThreads > 0) {
				wait();
			}
			
			while (!exitDone && numberOfThreads == 0) {
				wait();
			}
			
			if (numberOfThreads == 0) {
				exitDone = false;
				roomId = i;
			}
			
			++numberOfThreads;
			
		}
		catch (InterruptedException ex) {
			
		}
	}
	
	public synchronized boolean exit() {
		
		if (numberOfThreads <= 0)
			throw new IllegalMonitorStateException();
		
		--numberOfThreads;
		
		if (numberOfThreads == 0) {
			if (handler != null) {
				handler.onEmpty();
			}
			
			exitDone = true;
			
			notifyAll();
		}
		
		return true;
	}
	
	public synchronized void setExitHandler(int i, Rooms.Handler h) {
		if (i < 0 && i >= numberOfRooms)
			throw new IllegalArgumentException();
		
		try {
			while (roomId != i) {
				wait();
			}
			
			handler = h;
		}
		catch (InterruptedException ex) {
			
		}
	}

}
