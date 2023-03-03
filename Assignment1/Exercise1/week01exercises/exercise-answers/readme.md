# Exercises week 1

## Exercise 1.1

1. Output was observed between 16.000.000 and 20.000.000 aproximately, after 20 runs. As the expected output would be 20M, this is evidence of race conditions ocurring in this execution.

2. It's not guaranteed, but the effects of race conditions are remarkably more unlikely to happen. No it’s not guaranteed. The scheduler might let the first thread run when it has already incremented the counter to 100, and only afterwards starts the 2nd thread.

3. count = count +1 produces the same output as count++ and count += 1 (they compile to the same code).
All of these contain a temporary variable assignment for the calculation and a second assignment to assign the result to count. Therefore, none of these are atomic and behave exactly the same.

4. Introducing the Lock concept in the thread execution the expected output is always returned:
    ```java
    public void increment() {
      l.lock();
      count = count + 1;
      l.unlock();
    }
    ```
As we can observe from the previous experiments, race conditions are present here as the output depends on the interleaving.
Everytime the count update statement is executed (critical section), we ensure that only one thread is running (mutual exclusion).

5. The only relevant line of the code in terms of defining a critical section, is the count update as it is a shared variable in memory between threads

6. I stored the comparison of compiling the code with count = count + 1 vs the code with count++ in the pdf called "Comparison count = count+1 vs count++.pdf" in the root directory

    -> But I haven't reached any conclusions from it.

7. 	The output turns out to be negative for every test.

The compiler does several optimizations to the program to improve performance. This might result in reordering instructions to optimize performance. 
Processors also try to optimize things, for instance, a processor might read the current value of a variable from a temporary register
(which contains the last read value of the variable), instead of main memory (which has the latest value of the variable).
	

## Exercise 1.2

1. Write a program that creates a Printer object p, and then creates and starts two threads.
    
    > The solution can be found in the class `PrinterExperiment.java`.

2. Describe and provide an interleaving where this happens.

    > Interleaving for printing `--|-|-|`:
    > 
    >  `t1(1), t2(1), t1(2), t1(1), t1(1), t2(2), ...`
    
3. Use Java ReentrantLock to ensure that the program outputs the expected sequence `-|-|-|-|....`

    Compile and run the improved program to see whether it works. Explain why your solution is correct, and why it is not possible for incorrect patterns, such as in the output above, to appear.

    > Solution to be found in `PrinterWithLocksExperiment.java`.
    > 
    > The critical section consists of `(2)` and `(3)` (the printing of `-` and immediately afterwards `|`). This part is made mutual exclusive by acquiring a `ReentrantLock` `(1)`. In all cases, the lock is released by the `finally` block `(4)`.

4. Explain, in terms of the happens-before relation, why your solution for part 3 produces the correct output.

    > Our solution is correct in terms of the happens-before relation because lock.unlock() (a) happens before lock.lock() (b) in the thread definition and they belong to the same thread - with an implicit lock.unlock() in the beginning of the first execution.

## Exercise 1.3

1. Modify the behaviour of the Turnstile thread class so that that exactly 15000 enter the park...
    > The solution can be found in `CounterThreads2Covid.java`

2. Explain why your solution is correct, and why it always output 15000.

    > Our solution is correct because it's mutual exclusive (critical section is locked and unlocked), there are no deadlocks (in our `finally{}` we ensure that the thread gets unlocked eventually).

## Exercise 1.4

1. Compare the categories in the concurrency note and Goetz, try to find some examples of systems which are in- cluded in the categories of Goetz, but not in those in the concurrency note, and vice versa (if possible - if not possible, argue why).

    > **Concurrency Note**: Examples of systems which are included in the categories of the concurrency note, but not in those of Goetz
    >
    > Inherent: User facing input/output
    >
    > Hidden: Cloud computing, virtual machines
    
    > **Goetz**: Examples of systems which are included in the categories of Goetz, but not in those in the concurrency note
    >
    > Resource utilization: Front-Applicaition executing code while waiting for a database response.

2. Find examples of 3 systems in each of the categories in the Concurrency note which you have used yourself (as a programmer or user).

    > Inherent: Online Banking, Printer, Facebook
    >
    > Exploitation: Windows, iPhone, Gameboy
    >
    > Hidden: Azure Cosmos DB, Citrix shared desktop, Google Drive