
package exercises10;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

// JUnit testing imports
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import exercises10.SimpleRWTryLock.ReaderList;

public class TestReaderList {

    private ReaderList readerList;
    private Thread thread;

    @BeforeEach
    public void initialize() {
        this.thread = Thread.currentThread();
        this.readerList = new ReaderList(Thread.currentThread());
    }

    @Test
    public void testContains() throws InterruptedException {

        Thread t1 = new Thread(() -> {
            readerList = new ReaderList(Thread.currentThread(), readerList);
        });

        Thread t2 = new Thread(() -> {
            readerList = new ReaderList(Thread.currentThread(), readerList);
        });
        Thread t3 = new Thread(() -> {
            readerList = new ReaderList(Thread.currentThread(), readerList);
        });

        t1.start();
        t2.start();
        t3.start();

        t1.join();
        t2.join();
        t3.join();

        assertTrue(readerList.contains(t1));
        assertTrue(readerList.contains(t2));
        assertTrue(readerList.contains(t3));
        assertTrue(readerList.contains(thread));
    }

    @Test
    public void testRemove() throws InterruptedException {

        Thread t1 = new Thread(() -> {
            readerList = new ReaderList(Thread.currentThread(), readerList);
        });

        Thread t2 = new Thread(() -> {
            readerList = new ReaderList(Thread.currentThread(), readerList);
        });
        Thread t3 = new Thread(() -> {
            readerList = new ReaderList(Thread.currentThread(), readerList);
        });

        t1.start();
        t2.start();
        t3.start();

        t1.join();
        t2.join();
        t3.join();

        // readerList has to be reassigned because it's immutable
        readerList = readerList.remove(t1);
        assertFalse(readerList.contains(t1));

        readerList = readerList.remove(t3);
        assertFalse(readerList.contains(t3));

        readerList = readerList.remove(thread);
        assertFalse(readerList.contains(thread));

        readerList = readerList.remove(t2); 
        // ReaderList is "empty", therefore the "head" is null
        assertNull(readerList);
    }

}