package monitors;

public class SavingsAccount {
	
	private int _balance = 0;
	
	private int _preferredWithdrawals = 0;
	
	

	public synchronized void deposit(int k) {
		
		_balance += k;
			
		notifyAll();
	}
	
	public synchronized void withdraw(int k, boolean preferred) {
		
		try {
			if (preferred) {
				++_preferredWithdrawals;
			}
			else {
				while  (_preferredWithdrawals > 0) {
					wait();
				}
			}
			
			while (_balance < k) {
				
				wait();
			}
			
			if (preferred) {
				--_preferredWithdrawals;
				notifyAll();
			}
			
			_balance -= k;
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public int getBalance() {
		return _balance;
	}
}
