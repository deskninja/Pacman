package assignment10;

import java.util.Comparator;

/**
 * creates a priority queue from a heap data structure
 * 
 * @author Jonathan Oliveros and Joshua Wells
 * @param <T>
 */
public class MyQueue<T> extends Heap<T> {

	/**
	 * constructor with a comparator
	 * 
	 * @param cmp
	 */
	public MyQueue(Comparator cmp) {
		super(cmp);
	}

	/**
	 * returns the highest priority item
	 * 
	 * @return T from the queue
	 * @ensures this is a priority queue
	 */
	public T dequeue() {
		// ensure the order is correct for any updated values
		heapify(0);
		return removeFirst();
	}

	/**
	 * @param x object to add
	 * @ensures this is a priority queue
	 */
	public void enqueue(T x) {
		super.add(x);
	}
}
