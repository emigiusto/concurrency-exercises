# Exercises week 4

## Exercise 4.1

**Mandatory**

1. Implement a functional correctness test that finds concurrency errors in the add(Integer element) method in ConcurrentIntegerSetBuggy. 
Describe the interleaving that your test finds.

    > Implementation of the test can be found in method `testingIntegerSetBuggyAdd()` of class [**ConcurrentSetTest.java**](../app/src/test/java/testingconcurrency/ConcurrentSetTest.java). When tested with 5000 repetitions in a multithread running environment, it can be observed that in less than 1% of the test results show an incorrect output. The HashSet that should always remain with size 1 (as we are always adding the same element), in less than 1% of the cases (ca. 2/5000), the tests fail and return size = 2.\
    An interleaving that represents this situations is:\

    `HashSet#add()` is not an atomic operation. The underlying implementation of the `HashSet` uses a `HashMap`, and stores its values as `keys` in the `HashMap`. The following method calls are performed and the relevant method is `HashMap#putVal` which performs a range of checks before inserting a new value, however, [its implementation](https://github.com/openjdk/jdk/blob/master/src/java.base/share/classes/java/util/HashMap.java) is not mutual exclusive:

    > ```
    > (t1) ConcurrentIntegerSetBuggy#add(1)
    > (t2) ConcurrentIntegerSetBuggy#add(1)
    > (t1) HashSet#add(1) -> not atomic
    > (t2) HashSet#add(1) -> not atomic
    > (t1) HashMap#put(1, ...)
    > (t2) HashMap#put(1, ...)
    > (t1) HashMap#putVal(hash(1), 1, ...) -> Method not thread-safe; hash-check happens before element is inserted (w/o mutual exclusivity). If the thread is interrupted at this point right after the check...
    > (t2) HashMap#putVal(hash(1), 1, ...) -> ...this element will also be inserted.
    > ...
    ```


2. Implement a functional correctness test that finds concurrency errors in the remove(Integer element) method in ConcurrentIntegerSetBuggy. Describe the interleaving that your test finds.
    > Implementation of the test can be found in method `testingIntegerSetBuggyRemove()` of class [**ConcurrentSetBuggyTest.java**](../app/src/test/java/testingconcurrency/ConcurrentSetTest.java).\
    In this case, the remove method was tested in 15.000 repetetions, and the tests failed in less than 1% of the cases again, but this time the expected result was 0 (size of the HashSet after adding and deleting the same number, but the failed tests showed 1 and -1 as outputs.\
    Possible interleaving:\
    `HashSet#remove()` is not an atomic operation (explanation analog to the one above):
    > ```
    > (t1) ConcurrentIntegerSetBuggy#remove(2)
    > (t2) ConcurrentIntegerSetBuggy#remove(2)
    > (t1) HashSet#remove(2) -> not atomic
    > (t2) HashSet#remove(2) -> not atomic
    > (t1) HashMap#remove(2)
    > (t2) HashMap#remove(2)
    > (t1) HashMap#removeNode(hash(2), 2, ...) -> Method not thread-safe
    > (t2) HashMap#removeNode(hash(2), 2, ...) -> Method not thread-safe

3. In the class ConcurrentIntegerSetSync, implement fixes to the errors you found in the previous exercises. Run the tests again to increase your confidence that your updates fixed the problems. In addition, explain why your solution fixes the problems discovered by your tests.    
    > Implementation of the tests can be found in methods `testingIntegerSetSyncRemove()` and `testingIntegerSetSyncAdd()` of class [**ConcurrentSetSyncTest.java**](../app/src/test/java/testingconcurrency/ConcurrentSetTest.java). The synchronized keyword was added to methods that write in a share variable (the hashset)

4. Run your tests on the ConcurrentIntegerSetLibrary. Discuss the results.
    > we found no evidence of race conditions when running the tests using the native Java concurrency Package. The implementation is using a intrinsec implementation of synchronized. The class `ConcurrentSkipListSet` used is not completely thread safe though, as the bulk operations addAll, removeAll, retainAll, containsAll, equals, and toArray are not guaranteed to be performed atomically.\
    Also, it compromises performance in comparison with Java's HashSet as `ConcurrentSkipListSet` provides expected average log(n) time cost for the contains, add, and remove, contrary to the average constant time of Java HashSet.

5. Do a failure on your tests above prove that the tested collection is not thread-safe? Explain your answer.
    > Yes, because it means that we have proof of the existance of race conditions when several threads wants to execute methods of the class tested. This provides a counterexample of the *safety* property.

6. Does passing your tests above prove that the tested collection is thread-safe (when only using add() and remove())? Explain your answer.
    > Not at all: In general, the abscense of failures in the tests can never be a proof of the abscense of a bug. For this case, there are several conditions that can affect the results, such as:
    > - Not testing all methods that potentially are in use
    > - Sometimes the threads can run synchronously depending on the CPU cores resource allocation
    > - If the code executed runs too fast, the overlap clock time between threads can be extremely small and interleavings are very unlikely
    > - As we assume the scheduling to be *non-deterministic*, we could just have been "lucky" and caught an interleaving that lets our tests pass

**Challenging**

7. It is possible that size() returns a value different than the actual number of elements in the set. Give an interleaving showing how this is possible.

> ...

8. Is it possible to write a test that detects the interleaving you provided in 7? Explain your answer.

> ...

## Exercise 4.2

**Challenging**

1. Implement the above modification in your fair reader-writer monitor from week 2. That is, it should allow at most 5 readers accessing the resource.

> ...

2. Write a functional correctness test that checks whether there are ever more than 5 readers accessing the resource.

> ...


