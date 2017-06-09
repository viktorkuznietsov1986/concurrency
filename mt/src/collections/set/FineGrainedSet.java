package collections.set;

import java.util.Comparator;

public class FineGrainedSet<T> implements Set<T> {

	private final Node head, tail;
	private final Comparator<T> comparator;
	private volatile int count = 0;
	
	private class Node {
		volatile T data;
		volatile Node next;
		volatile boolean locked = false;
		
		synchronized void lock() {
			try {
				while (locked) {
					wait();
				}
				
				locked = true;
			}
			catch (InterruptedException ex) {
				
			}
			
		}
		
		synchronized void unlock() {
			if (!locked) {
				throw new IllegalMonitorStateException();
			}
			
			locked = false;
			notifyAll();
		}
	}
	
	public FineGrainedSet(Comparator<T> comparator) {
		this.comparator = comparator;
		head = new Node();
		tail = new Node();
		head.next = tail;
	}
	
	@Override
	public boolean add(T element) {
		
		head.lock();
		Node prev = head;
		
		try {
			Node curr = prev.next;
			curr.lock();
			
			try {
				while (curr != tail) {
					if (curr.data.equals(element)) {
						return false;
					}
					
					if (comparator.compare(curr.data, element) >= 1) {
						break;
					}
					
					prev.unlock();
					prev = curr;
					
					curr = prev.next;
					curr.lock();
				}
				
				Node a = new Node();
				a.data = element;
				prev.next = a;
				a.next = curr;
				
				synchronized(this) {
					++count;
				}
			}
			finally {
				curr.unlock();
			}
			
			
		}
		finally {
			prev.unlock();
		}
		
		return true;
	}

	@Override
	public boolean contains(T element) {
		
		head.lock();
		Node prev = head;
		
		try {
			
			Node curr = prev.next;
			curr.lock();
			
			try {
				while (curr != tail) {
					if (comparator.compare(curr.data, element) == 0) {
						return true;
					}
				
					prev.unlock();
					prev = curr;
					
					curr = curr.next;
					curr.lock();
				}
			}
			finally {
				curr.unlock();
			}
		}
		finally {
			prev.unlock();
		}
		
		return false;
	}

	@Override
	public boolean remove(T element) {
		head.lock();
		Node prev = head;
		
		try {
			Node curr = prev.next;
			curr.lock();
			
			try {
				while (curr != tail) {
					
					if (comparator.compare(curr.data, element) == 0) {
						break;
					}
					
					prev.unlock();
					prev = curr;
					
					curr = curr.next;
					curr.lock();
				}
				
				if (curr == tail) {
					return false;
				}
				
				prev.next = curr.next;
				
				synchronized(this) {
					--count;
				}
				return true;
			}
			finally {
				curr.unlock();
			}
		}
		finally {
			prev.unlock();
		}
		
	}

}
