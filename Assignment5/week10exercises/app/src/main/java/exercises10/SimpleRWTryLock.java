package exercises10;

import java.util.concurrent.atomic.AtomicReference;

public class SimpleRWTryLock implements SimpleRWTryLockInterface {

    private final AtomicReference<Holders> holders;

    public SimpleRWTryLock() {
        this.holders = new AtomicReference<Holders>();
    }

    /**
     * 10.2.3
     * Implement the readerTryLock method. This is marginally more complicated
     * because multiple other threads may be (successfully) trying to lock at the
     * same time, or may be unlocking read locks at the same time. Hence you need to
     * repeatedly read the holders field, and, as long as it is either null or a
     * ReaderList, attempt to update the field with an extended reader list,
     * containing also the current thread.
     * 
     * (Although the SimpleRWTryLock is not intended to be reentrant, for the
     * purposes of this exercise you need not prevent a thread from taking the same
     * lock more than once).
     * 
     * 
     * Called by a thread that tries to obtain a read lock. It must succeed and
     * return true if the lock is held only by readers (or nobody), and return
     * false if the lock is held by a writer.
     * 
     * 10.2.7
     * Improve the `readerTryLock` method so that it prevents a thread from taking
     * the same lock more than once, instead an exception if it tries. For instance,
     * the calling thread may use the `contains` method to check whether it is not
     * on the readers list, and add itself to the list only if it is not. Explain
     * why such a solution would work in this particular case, even if the
     * test-then-set sequence is not atomic.
     */
    @Override
    public boolean readerTryLock() {
        final Thread callingThread = Thread.currentThread();

        Holders currentHolders;
        boolean result;

        // repeatedly read the holders field
        // as long as it is either null or a ReaderList
        // attempt to update the field with an extended reader list, containing also the
        // current thread

        do {
            currentHolders = holders.get();

            if (currentHolders instanceof Writer) {
                return false;
            }

            // This snippet is for 10.2.7
            if (currentHolders != null && ((ReaderList) currentHolders).contains(callingThread)) {
                throw new IllegalMonitorStateException("Calling thread is already holding a read lock.");
            }

            ReaderList extendedReaderList = new ReaderList(Thread.currentThread(), (ReaderList) currentHolders);

            result = holders.compareAndSet(currentHolders, extendedReaderList);

            if (result) {
                return true;
            }

        } while (currentHolders == null || currentHolders instanceof ReaderList);

        return result;

    }

    /**
     * Called to release a read lock, and must throw an exception if the calling
     * thread does not hold a read lock.
     * 
     * 
     * Implement the readerUnlock method. You should repeatedly read the holders
     * field and, as long as i) it is non-null and ii) refers to a ReaderList and
     * iii) the calling thread is on the reader list, create a new reader list where
     * the thread has been removed, and try to atomically store that in the holders
     * field; if this succeeds, it should return. If holders is null or does not
     * refer to a ReaderList or the current thread is not on the reader list, then
     * it must throw an exception.
     * For the readerUnlock method it is useful to implement a couple of auxiliary
     * methods on the immutable ReaderList:
     *
     * public boolean contains(Thread t) { ... }
     * public ReaderList remove(Thread t) { ... }
     */
    @Override
    public void readerUnlock() {
        final Thread callingThread = Thread.currentThread();

        Holders currentHolders;
        Holders removedReaderListHolders;
        boolean result;

        do {
            currentHolders = holders.get();

            if (currentHolders == null
                    || !(currentHolders instanceof ReaderList)
                    || !((ReaderList) currentHolders).contains(callingThread)) {
                throw new IllegalMonitorStateException("Cannot unlock reader because it's not locked.");
            }

            removedReaderListHolders = ((ReaderList) currentHolders).remove(callingThread);

            result = holders.compareAndSet(currentHolders, removedReaderListHolders);

            if (result) {
                return;
            }

        } while (currentHolders != null
                && currentHolders instanceof ReaderList
                && ((ReaderList) currentHolders).contains(callingThread));
    }

    /**
     * 10.2.1
     * Called by a thread that tries to obtain a write lock. It must succeed and
     * return true if the lock is not already held by any thread, and return false
     * if the lock is held by at least one reader or by a writer.
     */
    @Override
    public boolean writerTryLock() {
        return holders.compareAndSet(null, new Writer(Thread.currentThread()));

    }

    /**
     * 10.2.2
     * Called to release the write lock, and must throw an exception if the calling
     * thread does not hold a write lock.
     */
    @Override
    public void writerUnlock() {
        final Thread callingThread = Thread.currentThread();
        final Holders currentValue = holders.get();

        // check that the lock is currently held
        // and that the holder is the calling thread
        if (currentValue != null && currentValue.getThread() == callingThread) {
            // then release the lock by setting holders to null
            holders.compareAndSet(currentValue, null);
        } else {
            // or else throw an exception.
            throw new IllegalMonitorStateException("The thread that tries to unlock does not hold a lock.");
        }
    }

    // Challenging 7.2.7: You may add new methods

    public static abstract class Holders {
        protected final Thread thread;

        public Holders(Thread thread) {
            this.thread = thread;
        }

        public Thread getThread() {
            return this.thread;
        }

    }

    // Made public so it can be tested
    public static class ReaderList extends Holders {

        private final ReaderList next;

        public ReaderList(Thread thread) {
            this(thread, null);
        }

        public ReaderList(Thread thread, ReaderList next) {
            super(thread);
            this.next = next;
        }

        /**
         * Checks recursively if this thread is contained in the reader list
         * 
         * @param t Thread to check
         * @return true if it is contained
         */
        public boolean contains(Thread t) {
            return this.thread == t || (next != null && next.contains(t));
        }

        /**
         * Removes element recursively from this single linked list and returns a new
         * instance or ReaderList.
         * 
         * @param t Thread to remove
         * @return
         */
        public ReaderList remove(Thread t) {
            if (this.getThread() == t) {
                return this.next;
            } else if (this.next == null) {
                return new ReaderList(this.getThread());
            } else {
                return new ReaderList(this.getThread(), this.next.remove(t));
            }
        }

    }

    private static class Writer extends Holders {

        public Writer(Thread thread) {
            super(thread);
        }
    }

}
