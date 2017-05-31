package collections;

import java.util.EmptyStackException;
import java.util.concurrent.atomic.AtomicReference;

public class LockFreeQueue<T> implements Pool<T> {
	
	protected class Node {
		Node(T x) {
			value = x;
			next = new AtomicReference<Node>(null);
		}
		
		T value;
		AtomicReference<Node> next;
	}
	
	protected AtomicReference<Node> head, tail;
	
	public LockFreeQueue() {
		Node n = new Node(null);
		head = new AtomicReference<Node>(n);
		tail = new AtomicReference<Node>(n);
	}
	
	@Override
	public void set(T item) {
		enq(item);
	}

	@Override
	public T get() {
		return deq();
	}
	
	protected void enq(T item) {
		Node node = new Node(item);
		
		while (true) {
			Node last = tail.get();
			Node next = last.next.get();
			
			if (last == tail.get()) {
				if (next == null) {
					if (last.next.compareAndSet(next, node)) {
						tail.compareAndSet(last, node);
						return;
					}
				}
				else {
					tail.compareAndSet(last, next);
				}
			}
			
		}
		
	}

	protected T deq() {
		while (true) {
			Node first = head.get();
			Node last = tail.get();
			Node next = first.next.get();
			
			if (first == head.get()) {
				if (first == last) {
					if (next == null) {
						throw new EmptyStackException();
					}

					tail.compareAndSet(last, next);
				}
				else {
					T value = next.value;
					if (head.compareAndSet(first, next)) {
						return value;
					}
				}
			}
		}
	}
}
