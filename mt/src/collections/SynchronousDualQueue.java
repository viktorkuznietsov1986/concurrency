package collections;

import java.util.concurrent.atomic.AtomicReference;

public class SynchronousDualQueue<T> implements Pool<T> {
	
	private enum NodeType { ITEM, RESERVATION };
	
	private class Node {
		volatile NodeType type;
		volatile AtomicReference<T> item;
		volatile AtomicReference<Node> next;
		
		Node(T data, NodeType type) {
			item = new AtomicReference<>(data);
			next = new AtomicReference<>(null);
			this.type = type;
		}
	}
	
	private volatile AtomicReference<Node> head, tail;
	
	public SynchronousDualQueue() {
		Node sentinel = new Node(null, NodeType.ITEM);
		head = new AtomicReference<>(sentinel);
		tail = new AtomicReference<>(sentinel);
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
						
						while (offer.type == NodeType.ITEM);
						

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

				boolean success = false;

				if (n.type == NodeType.RESERVATION) {
					success = n.item.compareAndSet(null, item);
				}

				head.compareAndSet(h,n);

				if (success) {
					n.type = NodeType.ITEM;
					return;
				}
			}
		}
		
	}

	@Override
	public T get() {
	    Node offer = new Node(null, NodeType.RESERVATION);

		while (true) {
			Node h = head.get();
			Node t = tail.get();
			
			if (h == t || t.type == NodeType.ITEM) {
				Node n = h.next.get();

				if (h == head.get()) {
				    if (n != null) {
				        T data = n.item.get();
						n.type = NodeType.RESERVATION;
				        h.next.compareAndSet(n, n.next.get());
				        return data;
                    }
                    else if (t.next.compareAndSet(n, offer)) {
				        tail.compareAndSet(t, offer);

				        while (offer.type == NodeType.RESERVATION);

				        h = head.get();

				        if (offer == h.next.get()) {
                            T data = offer.item.get();

                            if (head.compareAndSet(h, offer)) {
 								offer.type = NodeType.RESERVATION;
                                head.compareAndSet(offer, t);

                                return data;
                            }

                        }
                    }
                }
			}
			else {
				Node n = h.next.get();

				if (t != tail.get() || h != head.get() || n == null) {
				    continue;
                }

                T data = n.item.get();

                if (data != null) {
                    n.item.compareAndSet(data, null);
                    head.compareAndSet(h, n);
                    head.compareAndSet(h, t);
                    return data;
                }
 			}
		}
	}

}
