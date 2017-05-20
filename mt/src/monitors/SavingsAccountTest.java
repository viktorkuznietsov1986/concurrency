package monitors;

public class SavingsAccountTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		final SavingsAccount acct = new SavingsAccount();
		
		Thread t1 = createWithdrawThread(acct, 100, false);
		Thread t2 = createDepositThread(acct, 10);
		Thread t3 = createDepositThread(acct, 100);
		Thread t4 = createWithdrawThread(acct, 50, true);
		Thread t5 = createDepositThread(acct, 500);
		Thread t6 = createWithdrawThread(acct, 120, true);
		
		t1.start();
		t2.start();
		t3.start();
		t4.start();
		t5.start();
		t6.start();
	}
	
	private static Thread createDepositThread(SavingsAccount a, int amount) {
		return new Thread(new Runnable() {
			@Override
			public void run() {
				a.deposit(amount);
				System.out.println("Deposited " + amount);
			}
		});
	}
	
	private static Thread createWithdrawThread(SavingsAccount a, int amount, boolean preferred) {
		return new Thread(new Runnable() {
			@Override
			public void run() {
				a.withdraw(amount, preferred);
				if (preferred) {
					System.out.println("Withdrawal preferred " + amount);
				}
				else {
					System.out.println("Withdrawal regular " + amount);
				}
			}
		});
	}

}
