package collections;

import java.util.Comparator;
import java.util.concurrent.atomic.AtomicMarkableReference;

public class LockFreeSet<T> implements Set<T> {
	
	private Comparator<T> comparator;
	private final Node head, tail;
	
	private class Node {
		T data;
		AtomicMarkableReference<Node> next;
	}
	
	class Window {
		public Node prev, curr;
		Window(Node myPrev, Node myCurr) {
			prev = myPrev;
			curr = myCurr;
		}
		
		
	}
	
	private Window find(Node head, T key) {
		Node prev = null, curr = null, succ = null;
		
		boolean[] marked = {false};
		boolean snip;
		
		retry: while(true) {
			prev = head;
			curr = prev.next.getReference();
			
			while (true) {
				succ = curr.next.getReference();
				
				while (marked[0]) {
					snip = prev.next.compareAndSet(curr, succ, false, false);
					
					if (!snip) {
						continue retry;
					}
					
					curr = succ;
					succ = curr.next.get(marked);
				}
				
				if (comparator.compare(curr.data, key) >= 1) {
					return new Window(prev, curr);
				}
				
				prev = curr;
				curr = succ;
			}
		}
	}
	
	public LockFreeSet(Comparator<T> comparator) {
		this.comparator = comparator;
		head = new Node();
		tail = new Node();
		head.next = new AtomicMarkableReference<Node>(tail, false);
	}
	

	@Override
	public boolean add(T element) {
		while (true) {
			Window window = find(head, element);
			
			Node prev = window.prev;
			Node curr = window.curr;
			
			if (curr.data.equals(element)) {
				return false;
			}
			else {
				Node n = new Node();
				n.data = element;
				n.next = new AtomicMarkableReference<Node>(curr, false);
				
				if (prev.next.compareAndSet(curr, n, false, false)) {
					return true;
				}
			}
		}
	}

	@Override
	public boolean contains(T element) {
		boolean[] marked = {false};
		
		Node curr = head;
		
		while (curr != tail) {
			if (comparator.compare(curr.data, element) == 0 && !marked[0]) {
				return true;
			}
			
			curr = curr.next.getReference();
			Node succ = curr.next.get(marked);
		}
		
		return false;
	}

	@Override
	public boolean remove(T element) {
		boolean snip;
		
		while (true) {
			Window window = find(head, element);
			
			Node prev = window.prev;
			Node curr = window.curr;
			
			if (curr == tail || comparator.compare(curr.data, element) != 0) {
				return false;
			}
			else {
				Node succ = curr.next.getReference();
				snip = curr.next.compareAndSet(succ, succ, false, true);
				
				if (!snip) {
					continue;
				}
				
				prev.next.compareAndSet(curr, succ, false, false);
				return true;
			}
			
		}
	}

}
