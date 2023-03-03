package exercises03;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Semaphore;

public class BoundedBuffer<T> implements BoundedBufferInteface<T> {

    // fields are private for thread-safety
    private final Queue<T> queue;
    private /* static */ final Semaphore prodSem = new Semaphore(0);;
    private /* static */ final Semaphore conSem = new Semaphore(0);
    private /* static */ final Semaphore readWriteSem = new Semaphore(1);

    public BoundedBuffer(int capacity) {
        prodSem.release(capacity);
        queue = new LinkedList<T>();
    }

    @Override
    public T take() throws InterruptedException {
        try {
            conSem.acquire();
        } catch (InterruptedException e) { // Try-catch to make explicit what's going on.
            conSem.release();
            throw e;
        }
        try {
            readWriteSem.acquire();
        } catch (InterruptedException e) {
            conSem.release(); // Releasing both semaphores in case readWriteSem throws an exception
            readWriteSem.release();
            throw e;
        }

        System.out.println("Consumed: " + queue.peek());
        T item = queue.poll();
        System.out.println(queue);
        prodSem.release(); // release prodSem because one spot in the array got free
        readWriteSem.release();

        return item;
    }

    @Override
    public void insert(T elem) throws InterruptedException {
        try {
            prodSem.acquire();
        } catch (InterruptedException e) {// Try-catch to make explicit what's going on.
            prodSem.release();
            throw e;
        }

        try {
            readWriteSem.acquire();
        } catch (InterruptedException e) { // Try-catch to make explicit what's going on.
            prodSem.release(); // Releasing both semaphores in case readWriteSem throws an exception
            readWriteSem.release();
            throw e;
        }
        queue.offer(elem);
        System.out.println("Produced: " + elem);
        System.out.println(queue);
        conSem.release(); // release conSem because one spot in the array got filled
        readWriteSem.release();
    }

    public static void main(String[] args) {

        final int capacity = 10;
        BoundedBuffer<Integer> bb = new BoundedBuffer<Integer>(capacity);
        IntegerConsumer consumer1 = new IntegerConsumer(bb);
        IntegerConsumer consumer2 = new IntegerConsumer(bb);
        IntegerConsumer consumer3 = new IntegerConsumer(bb);

        IntegerProducer producer1 = new IntegerProducer(bb);
        IntegerProducer producer2 = new IntegerProducer(bb);
        IntegerProducer producer3 = new IntegerProducer(bb);

        consumer1.start();
        consumer2.start();
        consumer3.start();

        producer1.start();
        producer2.start();
        producer3.start();
    }
}

class IntegerProducer extends Thread {
    BoundedBuffer<Integer> bb;

    public IntegerProducer(BoundedBuffer<Integer> bb) {
        this.bb = bb;
    }

    public void run() {
        for (int i = 1; i < 1000; i++) {
            try {
                bb.insert(i);
            } catch (InterruptedException e) {
                e.printStackTrace();

            }
        }
    }

}

class IntegerConsumer extends Thread {
    BoundedBuffer<Integer> bb;

    public IntegerConsumer(BoundedBuffer<Integer> bb) {
        this.bb = bb;
    }

    public void run() {
        for (int i = 1; i < 1000; i++) {
            try {
                bb.take();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}