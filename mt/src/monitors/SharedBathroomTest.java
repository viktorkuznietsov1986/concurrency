package monitors;

public class SharedBathroomTest {
	public static void main(String[] args) {
		//SharedBathroom b = new SharedBathroomLocks();
		SharedBathroom b = new SharedBathroomObject();
		
		Thread m1 = createMaleThread(b);
		Thread m2 = createMaleThread(b);
		Thread m3 = createMaleThread(b);
		Thread m4 = createMaleThread(b);
		Thread m5 = createMaleThread(b);
		
		Thread f1 = createFemaleThread(b);
		Thread f2 = createFemaleThread(b);
		Thread f3 = createFemaleThread(b);
		
		m1.start();
		m2.start();
		f1.start();
		f2.start();
		m3.start();
		f3.start();
		m4.start();
		m5.start();

	}
	
	private static Thread createMaleThread(SharedBathroom bathroom) {
		return new Thread(new Runnable() {
			@Override
			public void run() {
				bathroom.enterMale();
				System.out.println("Male entered and brushing teeth.");
				bathroom.leaveMale();
			}
		});
	}
	
	private static Thread createFemaleThread(SharedBathroom bathroom) {
		return new Thread(new Runnable() {
			@Override
			public void run() {
				bathroom.enterFemale();
				System.out.println("Female entered and taking shower.");
				bathroom.leaveFemale();
			}
		});
	}
}
