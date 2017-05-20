package collections;

import static org.junit.Assert.*;

import java.util.Comparator;

import org.junit.Test;

public class CourceGrainedSetTest {

	@Test
	public void testAddSingleThread() {
		Set<Integer> s = new CourceGrainedSet<Integer>(new Comparator<Integer>() {

			@Override
			public int compare(Integer o1, Integer o2) {
				return o1-o2;
			}
			
		});
		
		assertTrue(s.add(5));
		assertTrue(s.add(6));
		assertTrue(s.add(1));
		assertFalse(s.add(1));
	}
	
	@Test
	public void testContains() {
		Set<Integer> s = new CourceGrainedSet<Integer>(new Comparator<Integer>() {

			@Override
			public int compare(Integer o1, Integer o2) {
				return o1-o2;
			}
			
		});
		
		assertTrue(s.add(5));
		assertTrue(s.add(6));
		assertTrue(s.add(1));
		
		Thread t1 = new Thread(new Runnable() {

			@Override
			public void run() {
				assertTrue(s.contains(5));
			}
			
		});
		
		Thread t2 = new Thread(new Runnable() {

			@Override
			public void run() {
				assertTrue(s.contains(6));
			}
			
		});
		
		Thread t3 = new Thread(new Runnable() {

			@Override
			public void run() {
				assertTrue(s.contains(1));
			}
			
		});
		
		Thread t4 = new Thread(new Runnable() {

			@Override
			public void run() {
				assertFalse(s.contains(9));
			}
			
		});
		
		t1.start();
		t2.start();
		t3.start();
		t4.start();
		
	}
	
	@Test
	public void testRemoveSingleThread() {
		Set<Integer> s = new CourceGrainedSet<Integer>(new Comparator<Integer>() {

			@Override
			public int compare(Integer o1, Integer o2) {
				return o1-o2;
			}
			
		});
		
		assertTrue(s.add(5));
		assertTrue(s.add(6));
		assertTrue(s.add(1));
		assertTrue(s.remove(1));
		assertFalse(s.contains(1));
		assertTrue(s.remove(5));
		assertFalse(s.contains(5));
		assertTrue(s.remove(6));
		assertFalse(s.contains(6));
		assertFalse(s.remove(1));
		assertFalse(s.remove(9));
	}

}
