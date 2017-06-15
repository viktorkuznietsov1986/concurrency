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

		while (roomId != i && numberOfThreads > 0) {
			try {
				wait();
			}
			catch (InterruptedException e) {

			}
		}

		while (!exitDone && numberOfThreads == 0) {
			try {
				wait();
			}
			catch (InterruptedException e) {

			}
		}

		if (numberOfThreads == 0) {
			exitDone = false;
			roomId = i;
		}

		++numberOfThreads;
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
		
		while (roomId != i) {
			try {
				wait();
			}
			catch (InterruptedException e) {

			}
		}

		handler = h;

	}

}
