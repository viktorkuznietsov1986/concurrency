package collections;

import java.util.Comparator;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class CourceGrainedSet<T> implements Set<T> {
	
	private Lock lock = new ReentrantLock();
	private final Node head, tail;
	private final Comparator<T> comparator;
	private volatile int count = 0;
	
	private class Node {
		volatile T data;
		volatile Node next;
	}
	
	public CourceGrainedSet(Comparator<T> comp) {
		comparator = comp;
		head = new Node();
		tail = new Node();
		head.next = tail;
	}

	@Override
	public boolean add(T element) {

		lock.lock();
		
		try {
			Node curr = head.next;
			Node prev = head;
			
			while (curr != tail) {
				if (curr.data.hashCode() == element.hashCode()) {
					return false;
				}
				
				if (comparator.compare(curr.data, element) >= 1) {
					break;
				}
				
				prev = curr;
				curr = curr.next;
			}
			
			Node a = new Node();
			a.data = element;
			prev.next = a;
			a.next = curr;
			
			++count;
			
			return true;
			
		}
		finally {
			lock.unlock();
		}
	}

	@Override
	public boolean contains(T element) {
		lock.lock();
		
		try {
			Node curr = head.next;
			while (curr != tail) {
				if (comparator.compare(curr.data, element) == 0) {
					return true;
				}
				
				curr = curr.next;
			}
		}
		finally {
			lock.unlock();
		}
		
		return false;
	}

	@Override
	public boolean remove(T element) {
		lock.lock();
		
		try {
			Node curr = head.next;
			Node prev = head;
			
			while (curr != tail) {
				
				if (comparator.compare(curr.data, element) == 0) {
					break;
				}
				
				prev = curr;
				curr = curr.next;
			}
			
			if (curr == tail) {
				return false;
			}
			
			prev.next = curr.next;
			--count;
			return true;
		}
		finally {
			lock.unlock();
		}
	}

}
