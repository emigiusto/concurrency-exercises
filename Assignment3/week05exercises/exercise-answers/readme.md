# Exercises Week 5

## Exercise 5.1

In this exercise you must perform, on your own hardware, some of the measurements done in the *Microbenchmarks note*.

**Mandatory**

5.1.1. Use `Benchmark.java` to run the `Mark1` through `Mark6` measurements.
Include the results in your hand-in, and reflect and comment on them: Are they plausible? Any surprises? Mention any cases where they deviate significantly from those shown in the Microbenchmarks note.

> Benchmark results can be found in the file `.../exercise-answers/exercise5-1-1.txt`.
> 
> Comments about comparision to the *Microbenchmarks note*:
> 
> 
> * Mark1: similar results
> * Mark2: Our ouput of `2,3 ns` is a lot lower than `30,5 ns` of the value in the benchmarks note.
> * Mark3: Similar here. Our results are all `2,2 ns`, the note shows results around `30 ns`.
> * Mark4: The note shows `30.3 ns +/- 0.137` while our output is `2,2 ns +/-  0,014`. This leads to the conclusion, that in our experimeent there is 68.3 % chance that the actual execution time for `multiply` is between `2,186 ns` and `2,214 ns`.
> * Mark5: Both, their results and ours start with a time of `100,0 ns`for 2 iterations, however, ours ends with 134217728 iteration of `2,2 ns`, theirs ends with 8388608 iterations and an average execution time of `30.3 ns`. This is a logical consequence that given the quicker individual execution times of our experiment, more executions can happen before the time limit is reached.
> * Mark6: Again, the main differenc is observed that our execution time for 8388608 executions is `2,2 ns` (with a standard deviation of `0,00`!) and theirs is `30.3 ns` with a standard deviation of `0.20`.
> 
> 
> The results seem to be plausible under the assumptions that our  
> experimentation machine is probably faster than theirs.

5.1.2. Use `Mark7` to measure the execution time for the mathematical functions `pow`, `exp`, and so on, as in Microbenchmarks note Section 4.2. Record the results in a text file along with appropriate system identification. Preferably do this on at least two different platforms, eg. your own computer and a fellow student/friends computer.

Include the results in your hand-in, and reflect and comment on them: Are they plausible? Any surprises? Mention any cases where they deviate significantly from those shown in the Microbenchmarks.

> Answer

## Exercise 5.2

In this exercise you must perform, on your own hardware, the measurement performed in the lecture using the example code in file TestTimeThreads.java.

**Mandatory**

1. First compile and run the thread timing code as is, using Mark6, to get a feeling for the variation and robustness of the results. Do not hand in the results but discuss any strangenesses, such as large variation in the time measurements for each case.

> Benchmark results can be found in the file `.../exercise-answers/exercise5-2-1.txt`. We can observe a decreasing tendency on the average running time as the number of iterations increase because program gets faster because the just-in-time compiler kicks in and optimizes parts of the code. Some outliers can be observed in iterations and can be attributed to other CPU processes consuming computational resources. This outlier measurement may be caused by the garbage collector accidentally performing
some work at that time, or the just-in-time compiler, or some other external disturbance.

2. Now change all the measurements to use Mark7, which reports only the final result. Record the results in a text file along with appropriate system identification.
Include the results in your hand-in, and reflect and comment on them: Are they plausible? Any surprises? Mention any cases where they deviate significantly from those shown in the lecture.

> Benchmark results can be found in the file `.../exercise-answers/exercise5-2-2.txt`. 

## Exercise 5.3

In this exercise you must use the benchmarking infrastructure to measure the performance of the prime counting example given in file TestCountPrimesThreads.java.

**Mandatory**

1. Measure the performance of the prime counting example on your own hardware,as a function of the number of threads used to determine whether a given number is a prime. Record system information as well as the measurement results for 1. . . 32 threads in a text file. If the measurements take excessively long time on your computer, you may measure just for 1. . . 16 threads instead.

> Results can be found in the file [exercise5-3-1.txt](exercise5-3-1.txt).

2. Reflect and comment on the results; are they plausible? Is there any reasonable relation between the number of threads that gave best performance, and the number of cores in the computer you ran the benchmarks on? Any surprises?

> The MacBook we ran the experiments on has 8 cores in total (4 so called performance cores and 4 efficiency cores). We observe that the average execution time rises by ~19% when the fifth thread is introduced. This seems plausible to us since more use of the slower efficiency cores might be made when the fifth core is introduced. Until this point we observe significant performance gains the more threads are introduced.

> When running with 8 to 27 threads the execution time fluctuates between 1,700,000ns and 1,800,000ns with some outliers above 1,800,000ns. With 28 and more threads the performance seems to increase again as execution time goes down. This is surprising to us as the overhead for managing this many threads have negative influence on performance.

3. Now instead of the LongCounter class, use the java.util.concurrent.atomic.AtomicLong class for the counts. Perform the measurements again as indicated above. Discuss the results: is the performance of AtomicLong better or worse than that of LongCounter? Should one in general use adequate built-in classes and methods when they exist?

> The AtomicLong class does not perform slightly faster than the LongCounter, in the range of 6,0000 - 7,000ns. This might be coincidental and from this experiment we cannot confirm that the AtomicLong performs significantly faster.
If built-in classes and methods offer the needed functionality one should use them because they are well-tested and the likelyhood that they are implemented in a thread-safe manner is high.

**Challenging**

4. Now change the worker thread code in the lambda expression to work like a very performance-conscious developer might have written it. Instead of calling lc.increment() on a shared thread-safe variable lc fromallthethreads,createalocalvariablelong count = 0 inside the lambda (defining the computation of the thread), and increment that variable in the for-loop. This local variable is thread-confined and needs no synchronization. After the for-loop, add the local variable’s value to a shared AtomicLong, and at the end of the countParallelN method return the value of the AtomicLong.
This reduces the number of synchronizations from several hundred thousands to at most threadCount, which is at most 32. In theory this might make the code faster. Measure whether this is the case on your hardware.

## Exercise 5.4

In this exercise you should estimate whether there is a performance gain by declaring a shared variable as volatile. Consider this simple class that has both a volatile int and another int that is not declared volatile:

public class TestVolatile {
  private volatile int vCtr;
  private int ctr;
  public void vInc () {
        vCtr++; }
  public void inc () {
        ctr++;
} }

**Mandatory**

UseMark7(fromBendchmark.java)to compare the performance of incrementing a volatile int and a normal int. Include the results in your hand-in and comment on them: Are they plausible? Any surprises?

> Our test results suggest that using a volatile variable is significantly slower than using a non-volatile variable (by a factor of around 2). This is not surprising as using a volatile variable prevents the JIT from reordering and optimising instructions which in most cases slows down execution.

```
-PmainClass=exercises05.TestVolatile run

> Task :app:run
volatile                              6,0 ns       0,01   67108864
non-volatile                          3,5 ns       0,02  134217728
```

## Exercise 5.5

In this exercise you must write code searching for a string in a (large) text. Such a search is the core of any web-crawling service such as Google, Bing, Duck-Go-Go etc. Later in the semester, there will be a guest lecture from a Danish company providing a very specialized web-crawling solution that provides search results in real-time.
In this exercise you will work with the nonsense text found in: src/main/resources/long-text-file.txt (together with the other exercise code). You may read the file with this code:

## Mandatory

1. TestTimeSearch uses a slightly extended version of the LongCounter where two methods have been addedvoid add(long c)that increments the counter by c and void reset() that sets the counter to 0.
Extend LongCounter with these two methods in such a way that the counter can still be shared safely by several threads.

> Our implementation can be found in [LongCounter.java](../app/src/main/java/exercises05/LongCounter.java)


2. How many occurencies of "ipsum" is there in long-text-file.txt. Record the number in your solution.

> \# Occurences of ipsum :1430

3. Use Mark7 to benchmark the search function. Record the result in your solution.

```
# OS:   Mac OS X; 11.5.2; aarch64
# JVM:  Homebrew; 17.0.4.1
# CPU:  null; 8 "cores"
# Date: 2022-10-13T09:39:48+0200
search()                        6185927,0 ns  124414,98         64
```

4. Extend the code in TestTimeSearch with a new method
        private static long countParallelN(String target,
                          String[] lineArray, int N, LongCounter lc) {
        // uses N threads to search lineArray
          ...
}
Fill in the body of countParallelN in such a way that the method uses N threads to search the lineArray. Provide a few test results that make plausible that your code works correctly.

```
gradle -PmainClass=exercises05.TestTimeSearch run<br/>
# Occurences of ipsum :1430 (countParallelN with 1 threads)
# Occurences of ipsum :1430 (countParallelN with 2 threads)
# Occurences of ipsum :1430 (countParallelN with 3 threads)
# Occurences of ipsum :1430 (countParallelN with 4 threads)
# Occurences of ipsum :1430 (countParallelN with 5 threads)
# Occurences of ipsum :1430 (countParallelN with 6 threads)
# Occurences of ipsum :1430 (countParallelN with 7 threads)
# Occurences of ipsum :1430 (countParallelN with 8 threads)
# Occurences of ipsum :1430 (countParallelN with 9 threads)
# Occurences of ipsum :1430 (countParallelN with 10 threads)
# Occurences of ipsum :1430 (countParallelN with 11 threads)
# Occurences of ipsum :1430 (countParallelN with 12 threads)
# Occurences of ipsum :1430 (countParallelN with 13 threads)
# Occurences of ipsum :1430 (countParallelN with 14 threads)
# Occurences of ipsum :1430 (countParallelN with 15 threads)
# Occurences of ipsum :1430 (countParallelN with 16 threads)
# Occurences of ipsum :1430 (countParallelN with 17 threads)
# Occurences of ipsum :1430 (countParallelN with 18 threads)
# Occurences of ipsum :1430 (countParallelN with 19 threads)
# Occurences of ipsum :1430 (countParallelN with 20 threads)
# Occurences of ipsum :1430 (countParallelN with 21 threads)
# Occurences of ipsum :1430 (countParallelN with 22 threads)
# Occurences of ipsum :1430 (countParallelN with 23 threads)
# Occurences of ipsum :1430 (countParallelN with 24 threads)
# Occurences of ipsum :1430 (countParallelN with 25 threads)
# Occurences of ipsum :1430 (countParallelN with 26 threads)
# Occurences of ipsum :1430 (countParallelN with 27 threads)
# Occurences of ipsum :1430 (countParallelN with 28 threads)
# Occurences of ipsum :1430 (countParallelN with 29 threads)
# Occurences of ipsum :1430 (countParallelN with 30 threads)
# Occurences of ipsum :1430 (countParallelN with 31 threads)
# Occurences of ipsum :1430 (countParallelN with 32 threads)
```

```
# Occurences of lectus :1890 (countParallelN with 1 threads)
# Occurences of lectus :1890 (countParallelN with 2 threads)
# Occurences of lectus :1890 (countParallelN with 3 threads)
# Occurences of lectus :1890 (countParallelN with 4 threads)
# Occurences of lectus :1890 (countParallelN with 5 threads)
...
```

5. Use Mark7 to benchmark countParallelN. Record the result in your solution and provide a small discussion of the timing results.

> Our results can be found in [exercise5-5-5.txt](exercise5-5-5.txt).<br/>
Again, we observe the performance to increase until more than 4 threads are used. We see the running time halving to about one third of the running time for a sequential search (from 6395085,3 ns with 1 thread to 2053699,0 ns with 4 threads). This was to be expected with our experiments in exercise 5.3 in mind.<br/>
After this the running time increases with each additional thread until it stabilizes between 2,600,000 and 2,900,000ns with some outliers above 3,000,000ns. A cause for this behaviour could be that the add and get methods of the LongCounter are highly frequenced and act as a bottleneck for the high number of threads. Also the large overhead for managing the threads might explain missing performance gains with more threads.<br/>
Our conclusion from this experiment is that the optimal number of threads for this particular setup is 4.