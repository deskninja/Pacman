package assignment10;

import java.util.*;

/**
 * Implementation of a Binary Heap on array.
 *
 * @author Jonathan Oliveros and Joshua Wells
 *
 * @param <E> type of the element of the heap
 */
public class Heap<E> {

  /**
   * the size of the heap storage space when first created
   */
  private static final int INITIAL_CAPACITY = 10;
  /**
   * the underlying storage of the heap
   */
  private E[] rep;
  /**
   * the comparator used between objects in the heap
   */
  private Comparator<E> order;
  /**
   * the last spot in {@code rep} with data
   */
  private int end;

  /**
   * No argument constructor, builds an empty heap.
   *
   * @ensures this is a heap
   */
  public Heap() {
    inferOrder();
    clear();
  }

  /**
   * Constructor from a comparator, builds an empty heap with the given ordering.
   *
   * @param cmp comparator for the new heap
   * @ensures this is a heap
   */
  public Heap(Comparator<E> cmp) {
    changeOrder(cmp);
    clear();
  }

  /**
   * Constructor from an array, builds a heap from the given array elements.
   *
   * @param args elements to put in this heap
   * @ensures this is a heap
   */
  public Heap(E[] args) {
    inferOrder();
    clear();
    for (E item : args) {
      add(item);
    }
  }

  /**
   * Adds {@code x} to this maintaining the heap property.
   *
   * @param x element to be added
   * @modifies this
   */
  public void add(E x) {
    if (size() == this.rep.length - 1)
      this.rep = doubleSize(rep);
    this.end++;
    this.rep[end] = x;
    siftUp(end);
  }

  /**
   * Removes and returns the first element from the heap.
   *
   * @return the first element of this
   * @requires this is not empty
   * @modifies this
   */
  public E removeFirst() {
    if (size() < 1)
      throw new NoSuchElementException();
    E temp = rep[0];
    rep[0] = rep[end];
    this.end--;
    siftDown(0);
    return temp;
  }

  /**
   * Reports the number of elements in this heap.
   *
   * @return number of elements in this
   */
  public int size() {
    return this.end + 1;
  }

  /**
   * Change the order of this heap to the given one.
   *
   * @param cmp new ordering relation
   * @modifies this
   */
  public void changeOrder(Comparator<E> cmp) {
    order = cmp;
    if (this.size() > 1) {
      heapify(0);
    }
  }

  @SuppressWarnings("unchecked")
  public void clear() {
    this.end = -1;
    this.rep = (E[]) new Object[INITIAL_CAPACITY];
  }

  /**
   * Puts the contents of this heap in a list in a sorted order and returns it,
   * emptying the heap in the process.
   *
   * @return contents of the heap in a sorted order
   * @modifies this
   */
  public List<E> sort() {
    List<E> sorted = new ArrayList<>();
    while (this.size() > 0)
      sorted.add(removeFirst());

    return sorted;
  }

  /**
   * Converts the given complete binary tree into a heap.
   *
   * @param start index of the root in the underlying array representation
   * @ensures the rep is a heap
   * @requires the rep is a complete binary tree
   * @modifies this
   */
  public void heapify(int start) {
    int left = start * 2 + 1;
    int right = start * 2 + 2;
    if (rep.length > left) {
      heapify(left);
    }
    if (rep.length > right) {
      heapify(right);
    }
    siftDownAndCheck(start);
  }

  @Override
  public String toString() {
    StringBuilder s = new StringBuilder();
    s.append("[");
    int index = 0;
    while (index < this.end) {
      s.append(rep[index]);
      s.append(",");
      index++;
    }
    if (size() > 0) {
      s.append(rep[this.end]);
    }
    s.append("]");
    return s.toString();
  }

  /**
   * Switches two elements in the heap
   *
   * @param index1 int the first index
   * @param index2 int the second index
   * @modifies {@code rep}
   */
  private void switchElements(int index1, int index2) {
    E temp = rep[index1];
    rep[index1] = rep[index2];
    rep[index2] = temp;
  }

  /**
   * creates a new array of double the size
   *
   * @param x E[] the array to double
   * @return newArray E[] with the same data as x but twice as many cells
   */
  private E[] doubleSize(E[] x) {
    @SuppressWarnings("unchecked")
    E[] newArray = (E[]) new Object[x.length * 2];
    for (int i = 0; i < x.length; i++) {
      newArray[i] = x[i];
    }
    return newArray;
  }

  /**
   * Infers the order based on the type of the elements, {@code E}. If
   * {@code E extends Comparable}, it gets the order from that relation,
   * otherwise, it tries to infer it by comparing the hashcodes of the two
   * elements.
   */
  private void inferOrder() {
    order = new Comparator<E>() {

      @SuppressWarnings("unchecked")
      @Override
      public int compare(E x, E y) {
        try {
          return ((Comparable<E>) x).compareTo(y);
        } catch (ClassCastException e) {
          return Integer.compare(x.hashCode(), y.hashCode());
        }
      }
    };
  }

  /**
   * Sifts the root of the tree down appropriately so that the resulting tree is a
   * heap. When a root is sifted, the switched element is also sifted.
   *
   * @param start the element to sift
   * @ensures the rep is a heap
   * @requires the rep is a complete binary tree
   * @modifies this
   */
  private void siftDownAndCheck(int start) {
    int left = start * 2 + 1;
    int right = start * 2 + 2;
    if (size() > left && order.compare(rep[start], rep[left]) > 0) {
      if (size() > right && order.compare(rep[left], rep[right]) > 0) {
        switchElements(start, right);
        siftDownAndCheck(right);
      } else {
        switchElements(start, left);
        siftDownAndCheck(left);
      }
    } else if (size() > right && order.compare(rep[start], rep[right]) > 0) {
      switchElements(start, right);
    }
  }

  /**
   * Sifts the root of the tree down appropriately so that the resulting tree is a
   * heap.
   *
   * @param start index of the root in the underlying array representation
   * @requires both left and right subtrees are heaps
   * @ensures the rep is a heap
   * @modifies this
   */
  private void siftDown(int start) {
    int left = start * 2 + 1;
    int right = start * 2 + 2;
    if (size() > left && order.compare(rep[start], rep[left]) > 0) {
      if (size() > right && order.compare(rep[left], rep[right]) > 0) {
        switchElements(start, right);
      } else {
        switchElements(start, left);
      }
    } else if (size() > right && order.compare(rep[start], rep[right]) > 0) {
      switchElements(start, right);
    }
  }

  /**
   * this method sorts an element by swapping elements above it to fit it into the
   * heap
   *
   * @param index the location of the element to be sorted
   */
  private void siftUp(int index) {
    if (index > 0) {
      int parent = 0;
      if (index % 2 == 1) {
        parent = (index - 1) / 2;
        if (order.compare(this.rep[parent], this.rep[index]) > 0) {
          switchElements(parent, index);
          siftUp(parent);
        }
      } else {
        parent = (index - 2) / 2;
        if (order.compare(this.rep[parent], this.rep[index]) > 0) {
          switchElements(parent, index);
          siftUp(parent);
        }
      }
    }
  }
}