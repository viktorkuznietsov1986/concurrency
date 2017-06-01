package collections;

import java.util.concurrent.atomic.AtomicReference;

public class SynchronousDualQueue<T> implements Pool<T> {
	
	private enum NodeType { ITEM, RESERVATION };
	
	private class Node {
		volatile NodeType type;
		volatile AtomicReference<T> item;
		volatile AtomicReference<Node> next;
		
		Node(T data, NodeType type) {
			item = new AtomicReference<T>(data);
			next = new AtomicReference<Node>(null);
			this.type = type;
		}
	}
	
	private volatile AtomicReference<Node> head, tail;
	
	public SynchronousDualQueue() {
		Node sentinel = new Node(null, NodeType.ITEM);
		head = new AtomicReference<Node>(sentinel);
		tail = new AtomicReference<Node>(sentinel);
	}
	

	@Override
	public void set(T item) {
		Node offer = new Node(item, NodeType.ITEM);
		
		while (true) {
			Node t = tail.get(), h = head.get();
			
			if (h == t || t.type == NodeType.ITEM) {
				Node n = t.next.get();
				
				if (t == tail.get()) {
					if (n != null) {
						tail.compareAndSet(t, n);
					}
					else if (t.next.compareAndSet(n, offer)) {
						tail.compareAndSet(t, offer);
						
						while (offer.item.get() == item);
						
						h = head.get();
						
						if (offer == h.next.get()) {
							head.compareAndSet(h, offer);
						}
						
						return;
					}
				}
			}
			else {
				Node n = h.next.get();
				
				if (t != tail.get() || h != head.get() || n == null) {
					continue;
				}
				
				boolean success = n.item.compareAndSet(null, item);
				head.compareAndSet(h,n);
				
				if (success) {
					return;
				}
			}
		}
		
	}

	@Override
	public T get() {
		while (true) {
			Node h = head.get();
			Node t = tail.get();
			
			if (h == t) {
				
			}
			else {
				
			}
		}
	}

}
