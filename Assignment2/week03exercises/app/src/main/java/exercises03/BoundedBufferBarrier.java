package exercises03;

import java.util.LinkedList;
import java.util.concurrent.CyclicBarrier;

public class BoundedBufferBarrier implements BoundedBufferInteface<Object> {

    // fields are private for thread-safety
    private final LinkedList<Object> queue;
    private CyclicBarrier cb;
    private int max_capacity;
    private int capacity = 0;

    BoundedBufferBarrier(int capacity) {
        queue = new LinkedList<Object>();
        cb = new CyclicBarrier(capacity);
        this.max_capacity = capacity;
    }

    @Override
    public Object take() throws Exception {
        try {
            if (capacity==0) {
                cb.await();
            }
        } catch (InterruptedException e) { // Try-catch to make explicit what's going on.
            throw e;
        }
        System.out.println("Consumed: " + queue.peekFirst());
        System.out.println(queue);
        Object item = queue.pollFirst();
        cb.notifyAll();
        return item;
    }

    @Override
    public void insert(Object elem) throws Exception {
        try { //Wait for all threads before modifying the queue. In principle It's not possible.
            if (capacity>=max_capacity) {
                cb.await();
            }
        } catch (InterruptedException e) { // Try-catch to make explicit what's going on.
            throw e;
        }
        queue.addLast(elem);
        System.out.println("Produced: " + elem);
        System.out.println(queue);
        cb.notifyAll(); //notifyAll() is a method for all objects, not native to Cyclic
    }

    public static void main(String[] args) {

        final int capacity = 10;
        BoundedBufferBarrier bb = new BoundedBufferBarrier(capacity);
        Consumer1 consumer1 = new Consumer1(bb);
        Consumer1 consumer2 = new Consumer1(bb);
        Consumer1 consumer3 = new Consumer1(bb);

        Producer1 producer1 = new Producer1(bb);
        Producer1 producer2 = new Producer1(bb);
        Producer1 producer3 = new Producer1(bb);

        consumer1.start();
        consumer2.start();
        consumer3.start();

        producer1.start();
        producer2.start();
        producer3.start();
    }
}

class Producer1 extends Thread {
    BoundedBufferBarrier bb;

    Producer1(BoundedBufferBarrier bb) {
        this.bb = bb;
    }

    public void run() {
        for (int i = 1; i < 1000; i++) {
            try {
                bb.insert(i);
            } catch (Exception e) {
                e.printStackTrace();

            }
        }
    }

}

class Consumer1 extends Thread {
    BoundedBufferBarrier bb;

    Consumer1(BoundedBufferBarrier bb) {
        this.bb = bb;
    }

    public void run() {
        for (int i = 1; i < 1000; i++) {
            try {
                bb.take();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}