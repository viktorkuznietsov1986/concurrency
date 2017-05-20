package monitors;

public class RoomsTest {

	public static void main(String[] args) {
		
		Rooms r = new Rooms(10);

		Thread t1 = createRoomsThread(r, 0);
		Thread t2 = createRoomsThread(r, 0);
		Thread t3 = createRoomsThread(r, 5);
		Thread t4 = createRoomsThread(r, 7);
		Thread t5 = createRoomsThread(r, 0);
		Thread t6 = createRoomsThread(r, 7);
		
		t1.start();
		t2.start();
		t3.start();
		t4.start();
		t5.start();
		t6.start();

	}
	
	private static Thread createRoomsThread(Rooms r, int roomNumber) {
		return new Thread(new Runnable() {
			@Override
			public void run() {
				
				r.enter(roomNumber);
				
				System.out.println("Entered the " + roomNumber + " room.");
				
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				r.exit();
			}
		});
	}

}
