package assignment10;

import java.util.Comparator;

public class MyQueue<T> extends Heap<T>{

    public MyQueue() {
        super();
    }

    public MyQueue(Comparator cmp){
        super(cmp);
    }

    public T dequeue(){
        return super.removeFirst();
    }

    public void enqueue(T x){
        super.add(x);
    }

    @Override
    public void add(T x) {
        return;
    }

    @Override
    public T removeFirst() {
        return null;
    }

    @Override
    public String toString() {
        //TODO: edit this as needed
        return super.toString();
    }
}
