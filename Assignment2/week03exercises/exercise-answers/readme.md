# Exercises Week 3

## Exercise 3.1

**Mandatory**

1. Implement a classBoundedBuffer<T> as described above using only Java Semaphore for synchronization — i.e., Java Lock or intrinsic locks (synchronized) cannot be used.

> Implementation can be found in class [**BoundedBuffer.java**](app/src/main/java/exercises03/BoundedBuffer.java).

2. Explain why your implementation of BoundedBuffer<T> is thread-safe. Hint: Recall our definition of thread-safe class, and the elements to identify/consider in analyzing thread-safe classes (see slides).

> The class is considered thread-safe because all method calls and fields accesses don't result in race conditions. 
>* **Class state & Mutual Exclusion:**
The access to the field of type `Queue` (implemented by `LinkedList`), which is a shared variable is controlled by the `readWriteSem` Semaphore.
>* **Escaping:**
All semaphores and the Queue are defined as `private final` in the class, so there is no possibility to access them from out of the scope. We only publicate the methods `insert()` and `take()` to ensure thread-safe modification of the bounded buffer.
>* **(Safe)publication:**
All the classes' data structures are beings instantiated at construction 
>* **Immutability:**
We made all fields of BoundedBuffer `final` which makes them immutable.


3. Is it possible to implement BoundedBuffer<T> using Barriers? Explain your answer.

> Yes, it is possible. The BoundedBuffer can be implemented using a CyclicBarrier. In this it can be defined that the ConsumerThreads have to wait for ProducerThreads if the queue is empty. Similarly, it can be defined that producer threads have to wait when the capacity of the queue is reached.

4. One of the two constructors to Semaphore has an extra parameter named fair. Explain what it does, and explain if it matters in this example. If it does not matter in this example, find an example where it does matter.

> The parameter fair guarantees FIFO access to the critical section. It does matter if the order in which producers write to the bounded buffer is relevant. For the consumers it does not matter because they always retrieve the first element from the queue.

## Exercise 3.2

**Mandatory**

1. Implement a thread-safe version of Person using Java intrinsic locks (synchronized). Hint: The Person class may include more attributes than those stated above; including static attributes.

> The class is `Person.java`. Comments about design choices can be found within the file.

2. Explain why your implementation of the Person constructor is thread-safe, and why subsequent accesses to a created object will never refer to partially created objects.

> It is thread-safe because:
> - The mutable state does not _escape_: Member variables are private and complex object state is immutable (`String`).
> - State is safely publicated
> - Mutable class state is mutually exclusive due to the use of Java intrinsic lock (`synchronized`) for the setter.

3. Implement a main thread that starting several threads the create and use instances of the Person class.

> The method starting multiple threads is at `Person#main()`.

4. Assuming that you did not find any errors when running 3. Is your experiment in 3 sufficient to prove that
your implementation is thread-safe?

> No it is not sufficient. It is not a formal verification (which would be needed for a prove). It just shows, that one specific interleaving produces valid results.

## Exercise 3.3

**Challenging**

1. Implement a Semaphore thread-safe class using Java Lock. Use the description of semaphore provided in the slides.

> ...

2. Implement a (non-cyclic) Barrier thread-safe class using your implementation of Semaphore above. Use the description of Barrier provided in the slides.

> ...
