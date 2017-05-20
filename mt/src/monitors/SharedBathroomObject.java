package monitors;

public class SharedBathroomObject implements SharedBathroom {
	
	private int malesInBathroom = 0;
	private int femalesInBathroom = 0;

	@Override
	public synchronized void enterMale() {
		try {
			while (femalesInBathroom > 0) {
				wait();
			}
			
			++malesInBathroom;
		}
		catch (InterruptedException ex) {
			
		}
	}

	@Override
	public synchronized void leaveMale() {
		--malesInBathroom;
		
		if (malesInBathroom == 0) {
			notifyAll();
		}
		
	}

	@Override
	public synchronized void enterFemale() {

		try {
			while (malesInBathroom > 0) {
				wait();
			}
			
			++femalesInBathroom;
		}
		catch (InterruptedException ex) {
			
		}
		
	}

	@Override
	public synchronized void leaveFemale() {
		--femalesInBathroom;
		
		if (femalesInBathroom == 0) {
			notifyAll();
		}
		
	}

}
