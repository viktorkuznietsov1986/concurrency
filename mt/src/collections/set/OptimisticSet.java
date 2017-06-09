package collections.set;

import java.util.Comparator;

public class OptimisticSet<T> implements Set<T> {
	
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
	
	public OptimisticSet(Comparator<T> comparator) {
		this.comparator = comparator;
		head = new Node();
		tail = new Node();
		head.next = tail;
	}

	@Override
	public boolean add(T element) {
		while (true) {
			Node curr = head.next;
			Node prev = head;
			
			while (curr != tail) {
				if (curr.data.equals(element)) {
					return false;
				}
				
				if (comparator.compare(curr.data, element) >= 1) {
					break;
				}
				
				prev = curr;
				curr = curr.next;
			}
			
			prev.lock();
			curr.lock();
			
			try {
				if (validate(prev,curr)) {
					Node a = new Node();
					a.data = element;
					prev.next = a;
					a.next = curr;
					
					synchronized(this) {
						++count;
					}
					
					return true;
				}
			}
			finally {
				prev.unlock();
				curr.unlock();
			}
		}
		
	}

	@Override
	public boolean contains(T element) {
		while (true) {
			Node prev = head;
			Node curr = head.next;
			while (curr != tail) {
				if (comparator.compare(curr.data, element) == 0) {
					break;
				}
				
				prev = curr;
				curr = curr.next;
			}
			
			curr.lock();
			prev.lock();
			
			try {
				if (validate(prev,curr)) {
					if (curr == tail) {
						return false;
					}
					
					return comparator.compare(curr.data, element) == 0;
				}
			}
			finally {
				curr.unlock();
				prev.unlock();
			}
		}
	}

	@Override
	public boolean remove(T element) {
		while (true) {
			Node curr = head.next;
			Node prev = head;
			
			while (curr != tail) {
				
				if (comparator.compare(curr.data, element) == 0) {
					break;
				}
				
				prev = curr;
				curr = curr.next;
			}
			
			prev.lock();
			curr.lock();
			
			try {
				if (validate(prev,curr)) {
					if (curr == tail) {
						return false;
					}
					
					prev.next = curr.next;
					--count;
					return true;
				}
			}
			finally {
				prev.unlock();
				curr.unlock();
			}
			
			
		}
	}
	
	private boolean validate(Node prev, Node curr) {
		Node n = head;
		
		while (n != tail) {
			if (n.data == prev.data) {
				return curr == prev.next;
			}
			
			n = n.next;
		}
		
		return false;
	}

}
