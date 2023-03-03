# Exercises week 2

# Exercise 2.1

1. Use Java Intrinsic Locks (i.e., `synchronized`) to implement a monitor ensuring that the access to the shared resource by reader and writer threads is according to the specification above. You may use the code in the lectures and Chapter 8 of Herlihy as inspiration, but do not feel obliged copy that structure

   > See classes `ReadAndWriteMonior.java` which is called by `ReadersWriters.java`

2. Is your solution fair towards writer threads? In other words, does your solution ensure that if a writer thread wants to write, then it will eventually do so? If so, explain why. If not, modify part 1. so that your implementation satisfies this fairness requirement, and then explain why your new solution satisfies the requirement.

   > No, Writers will have to wait until no readers are holding the intrinsic lock. It is not fair towards the Writers as it's not guaranteed that they can write, as long as the scheduler picks Readers. As long as the count of active Readers > 0 no Writer will be able to write.
   
   > A solution implementing the fairness concept can be observed in classes `FairReadAndWriteMonitor.java` and `FairReadersWriters.java`.

   > This new solution prevents readers in threads to run their block of code if a Writer is already awaiting to hold the intrinsic lock. Therefore, we can observe a more symmetric distributions of readers and writers in the outcome.

3. What type of fairness does your solution enforce? Hint: Consider the two types of fairness we discussed in class, see lecture slides.

   > The solution enforces weak fairness because it does not guarantee that a writer that is ready to write will eventually do so.

4. Is it possible to ensure strong fairness using ReentrantLock or intrinsic java locks (synchronized)? Explain why.

   > It is possible to ensure strong fairness using an `ReentrantLock` by calling it with `new ReentrantLock(true)`. It is not possible to ensure strong fairness when using the keyword `synchronized`. It uses weak fairness by default.

# Exercise 2.2

1.  Compile and run the example as is. Do you observe the "main" thread’s write to mi.value remains invisible to the t thread, so that it loops forever? Independently of your observation, is it possible that the program loops forever? Explain your answer.

    > Yes, we observe that it loops forever. When the threads run on different CPU cores, they are allowed to have different registers or cache. Therefore the thread may run forever as it will never know the other thread changed the value to 42. It's value of `mi` is still `0`.

2.  Use Java Intrinsic Locks (synchronized) on the methods of the MutableInteger to ensure that thread t always terminates. Explain why your solution prevents thread t from running forever.

    > `TestMutableInteger.class`: The Java Intrinisic Lock mechanism (`synchronized`) will lock and unlock the the critical sections (which in this case is the method body of `MubableInteger#get`). The unlocking causes the registers/caches to be flushed "to memory levels shared by all CPUs".

3.  Would thread `t` always terminate if `get()` is not defined as synchronized? Explain your answer.

    > What we tried: When we removed all `synchronized` keywords _and_ the `Thread.sleep()` block the thread `t` did terminate. We assume this is caused by the JVM that flushes the cache after a certain time or depending on other criteria.
    However, it could happen that `t` does not terminate when the register of the CPU core where the thread runs is never flushed and thus it will never notice the change of the variable from `0` to `42`.

4.  Remove all the locks in the program, and define value in MutableInteger as a volatile variable.
    Does thread t always terminate in this case? Explain your answer.

     > Yes, the threads will always terminate if value is defined as volatile. The reason is that volatile variables will always be stored in main (shared) memory. The difference with the solutions implementing Locks of the previous exercises is that volatile variables can't ensure mutual exclusion.

5.  Explain parts 3. and 4. in terms of the happens-before relation.

    > Answer

# Exercise 2.3

1. Compile the program and run it several times. Show the results you get. Are there any race conditions?

   > Yes, from the output of the program it can be observed that we are in the presence of race conditions.

2. Explain why race conditions appear when t1 and t2 use the Mystery object. Hint: Consider (a) what it means for an instance method to be synchronized, and (b) what it means for a static method to be synchro- nized.

   > Static methods belong to a class while non-static methods belong to an object. Using the synchronized keyword on a static class enforces a class-level lock while using it on a non-static method enforces an object-level lock. Therefore, race-conditions occur in TestLocking0.java.

3. Implement a new version of the class Mystery so that the execution of t1 and t2 does not produce race conditions, without changing the modifiers of the field and methods in the Mystery class. That is, you should not make any static field into an instance field (or vice versa), and you should not make any static method into an instance method (or vice versa).

   > Our implementation works because we are using a static lock which is accessible by both static and non-static methods. Therefore, addStatic() and addInstance() use _the same_ lock.
