package collections.pool;

public abstract class QueuePool<T> implements Pool<T> {
	
	protected volatile Node head, tail;
	
	protected class Node {
		Node(T x) {
			value = x;
			next = null;
		}
		
		T value;
		volatile Node next;
	}
	
	public QueuePool() {
		head = new Node(null);
		tail = head;
	}
	
	@Override
	public void set(T item) { 
		enq(item);
	}
	
	@Override
	public T get() {
		return deq();
	}
	
	protected abstract void enq(T item);
	protected abstract T deq();
}
