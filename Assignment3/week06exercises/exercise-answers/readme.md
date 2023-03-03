# Exercises Week 6

## Exercise 6.1

This exercise is based on the program AccountExperiments.java (in the exercises directory for week 6). It generates a number of transactions to move money between accounts. Each transaction simulate transaction time by sleeping 50 milliseconds. The transactions are randomly generated, but ensures that the source and target accounts are not the same.

**Mandatory**

**6.1.1.** Use Mark7 (from Benchmark.java in the benchmarking package) to measure the execution time and verify that the time it takes to run the program is proportional to the transaction time.

> As we can see from our experiment (see below), we experience an approximate proportionality between the transaction time (50 ms) and the number of transactions performed: Doing 10 transactions takes ca. 500 ms, doing 20 transactions takes ca. 1,000 ms, 40 transactions -> ca. 2,000 ms.
>
> We ran the exeperiment 3 times with the following lines:
> ```java
> Benchmark.Mark7(String.format("Do %s transactions", NO_TRANSACTION), i -> doNTransactions(NO_TRANSACTION));
> Benchmark.Mark7(String.format("Do %s transactions", 2 * NO_TRANSACTION), i -> doNTransactions(2 * NO_TRANSACTION));
> Benchmark.Mark7(String.format("Do %s transactions", 4 * NO_TRANSACTION), i -> doNTransactions(4 * NO_TRANSACTION));
> ```
> 
> Our output is:
> ```
> Do 10 transactions            503770409.3 ns 1996502.47          2
> Do 20 transactions           1006320300.0 ns  516579.11          2
> Do 40 transactions           2012314642.2 ns  567157.58          2
> ```

**6.1.2.** Now consider the version in ThreadsAccountExperimentsMany.java (in the directory exercise61). The first four lines of the transfer method are:
```java
Account min = accounts[Math.min(source.id, target.id)];
Account max = accounts[Math.max(source.id, target.id)];
  synchronized(min){
    synchronized(max){
```
Explain why the calculation of min and max are necessary? Eg. what could happen if the code was written
like this:
```java
Account s = accounts[source.id];
Account t = accounts[target.id];
  synchronized(s){
    synchronized(t){
```
Run the program with both versions of the code shown above and explain the results of doing this.

>If we switch to the implementation that doesn't calculate the Min and Max, we will probably enter in a deadlock state because if two paralel threads access this particular section with nested locks: 
>```java
>  synchronized(s){
>   synchronized(t){
>```
> If there are two transactions taking place in paralel, lets say:  
> Account A --> Account B  
> Account B --> Account A  
> It may happen that, for instance, Thread 1 adquires the lock from Account A, and before locking Account B, Thread 2 locks account B and waits for A, entering a deadlock state. One of the potential interleavings that lead to this state is:  
(t1) Lock account A  
(t2) Lock account B  
(t1) Tries to Lock account B, it's locked so it will wait until it is unlocked again  
(t2) Tries to Lock account A, it's locked so it will wait until it is unlocked again

**6.1.3.** Change the program in `ThreadsAccountExperimentsMany.java` to use a the executor framework instead of raw threads. Make it use a fixed size thread pool. For now do not worry about terminating the main thread, but insert a print statement in the `doTransaction` method, so you can see that all executors are active.

> The changes are implemented in the class `ThreadsAccountExperimentsMany.java`. The essential parts are:
>
> 1. A `TransactionTask implements Runnable` is created. Its `run` method will perform a random transaction.
> 2. A fixed size thread pool is created `pool = Executors.newFixedThreadPool(10)`
> 3. `pool.execute(new TransactionTask())` is called 50 times.


**6.1.4.** Ensure that the executor shuts down after all tasks has been executed.

> The corresponding class is `ThreadsAccountExperimentsManyTerminating.java`.
>
> * We've implemented this by making use of the Java `Future<T>`.
> * Our `TransactionTask` implements `Callable<Boolean>` instead of `Runnable`.
> * The `boolean` indicates whether a transaction has been performed successfully (in case an exception is raised within `Transaction.transfer()` it would be `false`).
> * In our experiment, we use `pool.invokeAll()` to submit an entire list of tasks of type `Runnable`.
> * Next, we'll wait for all `Future`s to have finished by calling the blocking method `future.get()`.
> * When they've all finished we'll call `pool.shutdown()` 



## Exercise 6.2

Use the code in file `TestCountPrimesThreads.java` (in the exercises directory for week 6) to count prime numbers using threads.

**Mandatory**

**6.2.1.** Report and comment on the results you get from running `TestCountPrimesThreads.java`.

> * Our results can be found in the file `.../exercise-answers/exercise6-2-1.txt`.
> * Most primes (512) were found in the shortest time using `countParallelNLocal()` with 6 threads (851058,9 ns).
> * The computer used for the experiment has 8 cores and the times needed to find the same amount of primes was similar, using 7, 8, 9 and 10 threads while using the same method.
> * In every almost every try, the method `countParallelNLocal` (using a shared array) performed better than `countParallelN` (using an `AtomicLong`).
> * In any case (besides `amount_of_threads = 1`), the multi-threading solutions performed better than the sequential method.

**6.2.2.** Rewrite `TestCountPrimesthreads.java` using Futures for the tasks of each of the threads in part 1. Run your solutions and report results. How do they compare with the results from the version using threads?

> * The implementation can be found within the file `TestCountPrimesthreads.java`. The relevant parts are: The class `PrimeCountingTask` and the method `countParallelNTasks(int range, int threadCount)`.
> * Result from running that class can be found in the file `.../exercise-answers/exercise6-2-2.txt`.
> * Running the class and comparing the results shows, that the implementation with Futures and tasks performs slightly better than the version using a shared data structure (the `AtomicLong`) but still worse than the method using a local variable, when using between 1-13 threads. It performs the worst of those three, when using more than 13 threads.

## Exercise 6.3

A histogram is a collection of bins, each of which is an integer count. The span of the histogram is the number of bins. In the problems below a span of 30 will be sufficient; in that case the bins are numbered
0. . . 29.

Consider this Histogram interface for creating histograms:
```java
interface Histogram {
  public void increment(int bin);
  public int getCount(int bin);
  public float getPercentage(int bin);
  public int getSpan();
  public int getTotal();
}
```

Method call `increment(7)` will add one to bin 7; method call `getCount(7)` will return the current count in bin 7; method call `getPercentage(7)` will return the current percentage of total in bin 7; method `getSpan()` will return the number of bins; method call `getTotal()` will return the current total of all bins.

There is a non-thread-safe implementation of Histogram1 in file SimpleHistogram.java. You may assume that the
dump method given there is called only when no other thread manipulates the histogram and therefore does not
require locking, and that the span is fixed (immutable) for any given Histogram object.

**Mandatory**

**6.3.1.** Make a thread-safe implementation, class `Histogram2` implementing the interface `Histogram`. Use suitable modifiers (final and synchronized) in a copy of the `Histogram1` class. This class must use at most one lock to ensure mutual exclusion.

Explain what fields and methods need modifiers and why. Does the `getSpan` method need to be synchronized?

> The solution can be found in `Histogram2.java`. The class defines total as an Atomic Integer initialized in 0 as it has to be updated everytime a thread is calling `increment()` and return in `getTotal`.   
The methods `getCount()`, `increment()` and `getPercentage()`are implemented by the used of the intrisic lock of the array bins[] to avoid race conditions when accessing that shared data structure.  
The method `getSpan()` doesn't need to implement locking as it is only accesing a variable defined as **final**.

**6.3.2.** Now create a new class, Histogram3 (implementing the Histogram interface) that uses lock striping. You can start with a copy of Histogram2. Then, the constructor of Histogram3 must take an additional parameter nrLocks which indicates the number of locks that the histogram uses. You will have to associate a lock to each bin. Note that, if the number of locks is less than the number of bins, you may use the same lock for more than one bin. Try to distribute locks evenly among bins; consider the modulo operation % for this task.

> The implementation can be found in `Histogram3.java`. The changes in this class compared to `Histogram2`can be observed in the constructor, where an array of ReentrantLock is iniatializated:
> ```java
> locks = new ReentrantLock[locksNumber];
> for (int i = 0; i < locksNumber; i++) {
>     locks[i] = new ReentrantLock();
> }
> ```
> To adquire the correct lock corresponding to the stripe matching the bin number, the function `getLock()` was defined as follows:
> ```java
> private Lock getLock(int binNumber) {
>   int lockNumber = binNumber % locks.length;
>   Lock lock = locks[lockNumber];
>   return lock;
> }
> ```
> Finally, every critical section of code on the class was surrounded by a `lock()` and `unlock()` statement, previously retrieving the corresponding lock. Here is an example of `getCount()`:
> ```java
> public int getCount(int binNumber) {
>     Lock lock = getLock(binNumber);
>     lock.lock();
>     try {
>       return bins[binNumber];
>     } finally {
>       lock.unlock();
>     }
>   }
> ```
> Lock striping is in some cases a good tradeoff between running all class operations with a single lock (and create bottlenecks because of code running synchronously) and creating an inifinite number of different locks, which may benefit concurrency but at a high memory cost, compromising the final performance.

**6.3.3.** Now consider again counting the number of prime factors in a number p. Use the `Histogram2` class to write a program with multiple threads that counts how many numbers in the range 0. . . 4 999 999 have 0 prime factors, how many have 1 prime factor, how many have 2 prime factors, and so on. You may draw inspiration from the `TestCountPrimesThreads.java`.  
The correct result should look like this:
```
0: 2
1: 348513
2: 979274
3: 1232881
4: 1015979
5: 660254
6: 374791
7: 197039
8: 98949
9: 48400
```
and so on, showing that 348 513 numbers in 0. . . 4 999 999 have 1 prime factor (those are the prime numbers), 979 274 numbers have 2 prime factors, and so on. (The 2 numbers that have 0 prime factors are 0 and 1). And of course the numbers in the second column should add up to 5 000 000.  
Hint: There is a class `HistogramPrimesThreads.java` which you can use a starting point for this exercise. That class contains a method `countFactors(int p)` which returns the number of prime factors of p. This might be handy for the exercise.

> The implementation can be found in `HistogramPrimesThreadsH2.java`. The number of paralel threads to solve the problem can be defined in the constructor and has to be in the range between 1 and 30.
For range = 5_000_000, the expected result was obtained. The strategy, similarly to a previous exercise, was to separate the numbers in the range in equal partitions among the available threads defined and iniatilizated.  
No race conditions were observed after multiple tests varying the number of threads.

**6.3.4.** Finally, evaluate the effect of lock striping on the performance of part 3. Create a new class where you
use Mark7 to measure the performance of Histogram3 with increasing number of locks to compute the
number of prime factors in 0. . . 4 999 999. Report your results and comment on them. Is there a increase or
not? Why?

> The implementation can be found in `HistogramPrimesThreadsTest.java` and test results on `exercise-answers/exercise6-3-4.txt`.
With a fixed 8 threads and 5.000.000 as range, we could identify a considerable increase in performance testing from 1 to 5 stripes.  
> After that the performance remains stable until the end of the test (from 5 to 19 stripes), this might be related to the cost of using and mantaining more locks in the implementation.   
> In conclusion, the best solution for this range  as regards performance would involve using 5 locks (or stripes).