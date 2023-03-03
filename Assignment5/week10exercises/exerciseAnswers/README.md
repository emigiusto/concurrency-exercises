# Exercises Week 10

## Exercise 10.1

**Mandatory**

**10.1.1.** Write a class CasHistogram implementing the above interface. Explain why the methods increment, getBins, getSpan and getAndClear are thread-safe.

> Implementation can be found in [CasHistogram.java](../app/src/main/java/exercises10/CasHistogram.java).
>
> It is thread-safe because the bin values are accessed and modified only by atomic operations (`get()` and `compareAndSet()`). If those fail, they will be retried. This is a case of optimistic concurrency, as it is assumed it will succeed in a few number of tries.

**10.1.2.** Write a parallel functional correctness test for CasHistogram to check that it correctly stores the number of primes in the range (0, 4999999); as you did in exercise 6.3.3 in week 6. You must use JUnit 5 and the techniques we covered in week 4. The test must be executed with 2n threads where n ∈ {0, . . . , 4}. To assert correctness, perform the same computation sequentially using the class Histogram1 from week 6. Your test must check that each bin of the resulting CasHistogram (executed in parallel) equals the result of the same bin in Histogram1 (executed sequentially).
Note: The method getAndClear was not part of the Histogram interface in week 6. Consequently, you will need to implement a getAndClear method for Histogram1 so that you can implement the new interface. This is just a technicality, since the method is not used in the test.

> Our test is implemented in [TestHistograms.java](../app/src/test/java/exercises10/TestHistograms.java).

**10.1.3.** Measure the overall time to run the program above for CasHistogram and the lock-based Histogram week 6, concretely, Histogram2. For this task you should not use JUnit 5, as it is does offer good support to measure performance. Instead you can use the code in the file TestCASLockHistogram.java. It contains boilerplate code to evaluate the performance of counting prime factors using two Histogram classes. To execute it, simply create two objects named histogramCAS and histogramLock containing your implementation of Histogram using CAS (CasHistogram) and your implementation of Histogram using a single lock from week 6 (Histogram2).
What implementation performs better? The (coarse) lock-based implementation or the CAS-based one?
Is this result you got expected? Explain why.

Note: Most likely, your implementation for Histogram2 from week 6 does not have a getAndClear method for the same reason as we mentioned above. Simply implement a lock-based method for this exercise so that Histogram2 can implement the new version of the Histogram interface.

> Our results:
> ```
> Histogram2   (countParallel; 1 threads):      1386807202,2 ns  8565543,85   2
> HistogramCAS (countParallel; 1 threads):      1405898654,2 ns  3933461,06   2
> Histogram2   (countParallel; 2 threads):      1079428829,2 ns 63498323,42   2
> HistogramCAS (countParallel; 2 threads):       919733325,0 ns 17783064,42   2
> Histogram2   (countParallel; 4 threads):      1227769298,0 ns 75700741,51   2
> HistogramCAS (countParallel; 4 threads):       575699035,4 ns 17294515,83   2
> Histogram2   (countParallel; 8 threads):      1367163862,6 ns 26453987,48   2
> HistogramCAS (countParallel; 8 threads):       451768268,8 ns 10622328,02   2
> Histogram2   (countParallel; 16 threads):     1349147860,4 ns  6040337,56   2
> HistogramCAS (countParallel; 16 threads):      440437068,7 ns  3452531,15   2
> ```
>
> As we see, the time for the CAS version decreases, that is needed for performing `"countPrimesParallel()"`. This becomes more obvious with increasing numbers of threads. As the congestions is probably not very high, this makes sense, as the CAS version does not use locks and therefore spends less time waiting.

## Exercise 10.2

**Mandatory**

**10.2.1.** Implement the writerTryLock method. It must check that the lock is currently unheld and then atomi- cally set holders to an appropriate Writer object.

> Implemented in [`SimpleRWTryLock#writerTryLock()`](../app/src/main/java/exercises10/SimpleRWTryLock.java)

**10.2.2.** Implement the writerUnlock method. It must check that the lock is currently held and that the holder is the calling thread, and then release the lock by setting holders to null; or else throw an exception.

> Implemented in [`SimpleRWTryLock#writerUnlock()`](../app/src/main/java/exercises10/SimpleRWTryLock.java)

**10.2.3.** Implement the readerTryLock method. This is marginally more complicated because multiple other threads may be (successfully) trying to lock at the same time, or may be unlocking read locks at the same time. Hence you need to repeatedly read the holders field, and, as long as it is either null or a Read- erList, attempt to update the field with an extended reader list, containing also the current thread.
(Although the SimpleRWTryLock is not intended to be reentrant, for the purposes of this exercise you need not prevent a thread from taking the same lock more than once).

> Implemented in [`SimpleRWTryLock#readerTryLock()`](../app/src/main/java/exercises10/SimpleRWTryLock.java)

**10.2.4.** Implement the readerUnlock method. You should repeatedly read the holders field and, as long as i) it is non-null and ii) refers to a ReaderList and iii) the calling thread is on the reader list, create a new reader list where the thread has been removed, and try to atomically store that in the holders field; if this succeeds, it should return. If holders is null or does not refer to a ReaderList or the current thread is not on the reader list, then it must throw an exception.
For the readerUnlock method it is useful to implement a couple of auxiliary methods on the immutable ReaderList:

```Java
       public boolean contains(Thread t) { ... }
       public ReaderList remove(Thread t) { ... }
```

> Implemented in [`SimpleRWTryLock#readerUnlock()`](../app/src/main/java/exercises10/SimpleRWTryLock.java)

**10.2.5.** Write simple sequential JUnit 5 correctness tests that demonstrate that your read-write lock works with a single thread. Your test should check, at least, that:
• It is not possible to take a read lock while holding a write lock.
• It is not possible to take a write lock while holding a read lock.
• It is not possible to unlock a lock that you do not hold (both for read and write unlock).
You may write other tests to increase your confidence that your lock implementation is correct.

> Implemented in [`TestLocks.Tests_10_2_5`](../app/src/test/java/exercises10/TestLocks.java) in the methods:
> * `cantReadLock_whileHoldingWriteLock()`
> * `cantWriteLock_whileHoldingReadLock()`
> * `cantUnlockWriteLock_ifNotHoldingLock()`
> * `cantUnlockReadLock_ifNotHoldingLock()`
> * `cantUnlockWriteLock_ifNotHoldingLock_twoThreads()`
> * `cantUnlockReadLock_ifNotHoldingLock_twoThreads()`

**10.2.6.** Finally, write a parallel functional correctness test that checks that two writers cannot acquire the lock at the same time. You must use JUnit 5 and the techniques we covered in week 4. Note that for this exercise readers are irrelevant. Intuitively, the test should create two or more writer threads that acquire and release the lock. You should instrument the test to check whether there were 2 or more threads holding the lock at the same time. This check must be performed when all threads finished their execution. This test should be performed with enough threads so that race conditions may occur (if the lock has bugs).

> Implemented in [`TestLocks.Tests_10_2_6#twoWritersCantLockAtSameTime()`](../app/src/test/java/exercises10/TestLocks.java)
> An exception would be thrown if there were two or more locks held at the same time. (?)


**Challenging**

**10.2.7.** Improve the `readerTryLock` method so that it prevents a thread from taking the same lock more than once, instead an exception if it tries. For instance, the calling thread may use the `contains` method to check whether it is not on the readers list, and add itself to the list only if it is not. Explain why such a solution would work in this particular case, even if the test-then-set sequence is not atomic.

> Implemented in [`SimpleRWTryLock#readerTryLock()`](../app/src/main/java/exercises10/SimpleRWTryLock.java) and tests in [`TestLocks.Tests_10_2_7#cantAcquireTwoReadLockFromSameThread()`](../app/src/test/java/exercises10/TestLocks.java).