package collections.pool;

public interface Pool<T> {
	void set(T item);
	T get();
}
