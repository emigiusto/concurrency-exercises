package exercises10;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.concurrent.BrokenBarrierException;
// Concurrency imports
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicInteger;

// JUnit testing imports
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

public class TestLocks {

    private SimpleRWTryLockInterface lock;;

    @BeforeEach
    public void initialize() {
        lock = new SimpleRWTryLock();
    }

    @Nested
    @DisplayName("Tests 10.2.5")
    class Tests_10_2_5 {

        // It is not possible to take a read lock while holding a write lock.
        @Test
        public void cantReadLock_whileHoldingWriteLock() {
            assertTrue(lock.writerTryLock());
            assertFalse(lock.readerTryLock());
        }

        // It is not possible to take a write lock while holding a read lock.
        @Test
        public void cantWriteLock_whileHoldingReadLock() {
            assertTrue(lock.readerTryLock());
            assertFalse(lock.writerTryLock());
        }

        // It is not possible to unlock a lock that you do not hold (both for read and
        // write unlock).
        @Test
        public void cantUnlockWriteLock_ifNotHoldingLock() {
            try {
                lock.writerUnlock();
            } catch (IllegalMonitorStateException e) {
                assertEquals("The thread that tries to unlock does not hold a lock.", e.getMessage());
            }
        }

        @Test
        public void cantUnlockReadLock_ifNotHoldingLock() {
            try {
                lock.readerUnlock();
            } catch (IllegalMonitorStateException e) {
                assertEquals("Cannot unlock reader because it's not locked.", e.getMessage());
            }
        }

        /**
         * Helper class to store results from within threads
         */
        private static class InnerThreadTestResults {
            public boolean lockAcquired;
            public Exception exception;
        }

        // (2 threads) It is not possible to unlock a lock that you do not hold (both
        // for read and write unlock).
        @Test
        public void cantUnlockWriteLock_ifNotHoldingLock_twoThreads() throws InterruptedException {
            final InnerThreadTestResults testResults = new InnerThreadTestResults();
            testResults.lockAcquired = false;
            testResults.exception = null;

            Thread t1 = new Thread(() -> testResults.lockAcquired = lock.readerTryLock());
            Thread t2 = new Thread(() -> {
                try {
                    lock.readerUnlock();
                } catch (IllegalMonitorStateException e) {
                    testResults.exception = e;
                }
            });

            t1.start();
            t1.join();

            t2.start();
            t2.join();

            assertTrue(testResults.lockAcquired);
            assertEquals("Cannot unlock reader because it's not locked.", testResults.exception.getMessage());
        }

        @Test
        public void cantUnlockReadLock_ifNotHoldingLock_twoThreads() throws InterruptedException {
            final InnerThreadTestResults testResults = new InnerThreadTestResults();

            testResults.lockAcquired = false;
            testResults.exception = null;

            Thread t1 = new Thread(() -> testResults.lockAcquired = lock.writerTryLock());
            Thread t2 = new Thread(() -> {
                try {
                    lock.readerUnlock();
                } catch (IllegalMonitorStateException e) {
                    testResults.exception = e;
                }
            });

            t1.start();
            t1.join();

            t2.start();
            t2.join();

            assertTrue(testResults.lockAcquired);
            assertEquals("Cannot unlock reader because it's not locked.", testResults.exception.getMessage());
        }
    }

    /*
     * Finally, write a parallel functional correctness test that checks that two
     * writers cannot acquire the lock at the same time. You must use JUnit 5 and
     * the techniques we covered in week 4.
     * 
     * (Note that for this exercise readers are irrelevant.)
     * 
     * Intuitively, the test should create two or more writer threads
     * that acquire and release the lock. You should instrument the test to check
     * whether there were 2 or more threads holding the lock at the same time. This
     * check must be performed when all threads finished their execution. This test
     * should be performed with enough threads so that race conditions may occur (if
     * the lock has bugs).
     */
    @Nested
    @DisplayName("Tests 10.2.6")
    class Tests_10_2_6 {

        CyclicBarrier barrier; // Barrier to maximize contention and to avoid sequencial execution

        @RepeatedTest(3000)
        @DisplayName("Add one element to Sync Set")
        public void twoWritersCantLockAtSameTime() {

            int threadCount = 16 + 1;
            AtomicInteger concurrentWriteLockCount = new AtomicInteger(0);

            // / Testing with 16 threads
            barrier = new CyclicBarrier(threadCount);

            for (int i = 0; i < 16; i++) { // Testing with 16 threads
                new Thread(() -> {
                    try {
                        barrier.await(); // wait until all threads are ready
                        if (lock.writerTryLock()) {
                            concurrentWriteLockCount.incrementAndGet();

                            barrier.await();

                            synchronized (this) {
                                lock.writerUnlock();
                                concurrentWriteLockCount.compareAndSet(1, 0);
                            }
                        }
                        barrier.await(); // wait until all threads are finished
                    } catch (InterruptedException | BrokenBarrierException e) {
                        e.printStackTrace();
                        fail();
                    }
                }).start();
            }

            try {
                barrier.await();
                barrier.await();
            } catch (InterruptedException | BrokenBarrierException e) {
                e.printStackTrace();
                fail();
            }
            assertTrue(concurrentWriteLockCount.get() < 2, "Actual number locks at the same time: " + concurrentWriteLockCount.get());
        }
    }

    @Nested
    @DisplayName("Tests 10.2.7")
    class Tests_10_2_7 {

        /**
         * It is not possible to take two read locks at the same time from the same
         * thread
         */
        @Test
        public void cantAcquireTwoReadLockFromSameThread() {
            assertTrue(lock.readerTryLock());

            try {
                lock.readerTryLock();
            } catch (IllegalMonitorStateException e) {
                assertEquals("Calling thread is already holding a read lock.", e.getMessage());
            }
        }
    }

}
