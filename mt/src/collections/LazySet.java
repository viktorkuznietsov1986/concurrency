package collections;

import java.util.Comparator;

public class LazySet<T> implements Set<T> {

	private final Node head, tail;
	private final Comparator<T> comparator;
	private volatile int count = 0;
	
	private class Node {
		volatile T data;
		volatile Node next;
		volatile boolean locked = false;
		volatile boolean marked = false;
		
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
	
	public LazySet(Comparator<T> comparator) {
		this.comparator = comparator;
		head = new Node();
		tail = new Node();
		head.next = tail;
	}

	@Override
	public boolean add(T element) {
		while (true) {
			Node prev = head;
			Node curr = head.next;
			
			while (curr != tail) {
				
				if (comparator.compare(curr.data, element) >= 0) {
					break;
				}
				
				prev = curr;
				curr = curr.next;
			}
			
			prev.lock();
			
			try {
				curr.lock();
				
				try {
					if (validate(prev,curr)) {
						if (curr != tail && curr.data.equals(element)) {
							return false;
						}
						
						Node n = new Node();
						n.data = element;
						prev.next = n;
						n.next = curr;
						++count;
						return true;
					}
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

	@Override
	public boolean contains(T element) {
		Node curr = head.next;
		
		while (curr != tail) {
			if (comparator.compare(curr.data, element) == 0) {
				return !curr.marked;
			}
			
			curr = curr.next;
		}
		
		return false;
	}

	@Override
	public boolean remove(T element) {
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
			
			prev.lock();
			
			try {
				curr.lock();
				
				try {
					if (validate(prev,curr)) {
						if (curr == tail) {
							return false;
						}
						
						curr.marked = true;
						prev.next = curr.next;
						--count;
						return true;
					}
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
	
	private boolean validate(Node prev, Node curr) {
		return !prev.marked && !curr.marked && prev.next == curr;
	}
}
