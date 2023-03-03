# Exercises Week 9

## Exercise 9.1

In the file code-exercises/.../Stopwatch.java you find a complete Java version of the stopwatch example used in the lecture and material for this week.

**Mandatory**

**9.1.1.** Revise the stopwatch, so it can measure 1/10 th of a second.

> Implemented and commented within [stopwatchUI.java](../app/src/main/java/exercises09/stopwatchUI.java) and [SecCounter.java](../app/src/main/java/exercises09/SecCounter.java).

**9.1.2.** There is potential race condition in the Stopwatch. It is implicitly assumed that the display always shows exacly the value in the seconds field in SecCounter and in the display. Both (the seconds field in SecCounter and the display) are updated in the updateTime method. However, this is not coded as a critical section. Could there be an interleaving where the seconds field in SecCounter and the display have different values? If there is, how can this problem be solved? .

> Yes, such interleaving can happen. The seconds field can be incremented with the user pressing the reset button before the display was updated.\
> In this case the variable 'secs' is updated. Then the lC is reset due to the user interaction. secs still holds the old value and for a short amount of time the counter is updated to an old value.

```Java
// Interleaving
jframe.t1(lC.incr()) // tenthOfSeconds = 11
jframe.t2(lC.reset()) // display set to "0:00:00:0"
jframe.t1(tf.setText("0:00:01:0")) // display set to "0:00:01:1"
```

**9.1.3.** Make a version (Stopwatch2) that has two independent stopwatches, each with their own buttons and dis- play.

> [Stopwatch2.java](../app/src/main/java/exercises09/Stopwatch2.java)\
> `gradle -PmainClass="exercises09.Stopwatch2" run`

**9.1.4.** Make a version (StopwatchN) that have N independent stopwatches, each with their own buttons and display. Choose N, so one row of stopwatches fit on your screen.

> [StopwatchN.java](../app/src/main/java/exercises09/StopwatchN.java)
> A general solution for N number of StopWatch is presented in `Stopwatch2.java`.
> Run by passing the number of stop watches as paramenter in --args`
>
> ```
> gradle -PmainClass="exercises09.StopwatchN" run --args="NumberOfStopWatches"
> ```
>
> e.g.
>
> ```
> gradle -PmainClass=exercises09.StopwatchN run --args="8"
> ```

## Exercise 9.2

This exercise makes sure that you have a working version of RxJava and is able to use it to run a few simple examples.

**9.2.1.** Make sure you can run the simple examples in steps 6 and 7 from: https://www.tutorialspoint.com/rxjava/rxjava_environment_setup.htm. Make sure that you get the same result as in the tutorial.

>

**9.2.2.** Run the example from: https://www.tutorialspoint.com/rxjava/rxjava_single_observable.htm. Make sure that you get the same result as in the tutorial.

> Implemented in `ObservableTester.java`.

**9.2.3.** Run the example:
https://www.tutorialspoint.com/rxjava/rxjava_from_scheduler.htm. You may want to rename the class e.g. to ScheduleTester.java to avoid overwriting the code for ObservableTester.java
Write down your own explanation of what happens in this example.

> Implemented in `ScheduleTester.java`, the output is the expected one.
>
> In the main method of class ScheduleTester, the `.just()` method is used to create an `Observable` that emits these items ("A", "AB", "ABC").
>
> `flatMap()` executes the function it receives on every item. This function is `getLengthWithDelay()`, which itself returns `Observable`s. They are returned after a random delay (0-2s) and they are integers containing the length of the initially emitted items. These items are again returned as `ObservableSource`s. In this case, for each of the original items, it returns a new `Observable`. (An `Observable` itself is a `ObservableSource` as it implements its interface.)
>
> `doOnNext()` prints out the current thread name. `subscribeOn()` then asynchronously subscribes to the `Observable`s on the specified thread pool (`Executors.newFixedThreadPool(3)`).
> 
> All items emitted by the functions within `flatMap()` are then pushed downstream and subscribed. As the printed thread pool names tell, it seems to subscribe on same `Scheduler` as previously specified. The `onNext` `Consumer` then simply prints the thread name and the item length (which was the item it received).
>
> We slightly modified the example so it will terminate eventually. The `ExecutorService` is therefore initialized in the beginning as a local variable. In the end a `shutdown()` call will terminate it.

## Exercise 9.3

In this example you should use the RxJava concepts to make some versions of a stopwatch. In the file code-exercises/.../StopwatchRx.java you will find (most of) the code for a RxJava based version of the stopwatch.

**Mandatory**

**9.3.1.** Replace the line //TO-DO in code-exercises/.../StopwatchRx.java with code that uses the Rx classes (display and timer) to make a working version of StopWatchRx.

> Implemented in `StopwatchRx.java`. The change consists in adding the following line:

```Java
timer.subscribe(display);
```

Where the observer, in this case the display, subscribes to changes in the timer (observable). Other observers can also subscribe to the timer, allowing an easy way to scale the number of displays

**9.3.2.** Revise the code from the first step of this exercise so that all buttons are made into observables. (Hint: You may use code-exercises/.../rxButton.java as an inspiration.)

> [StopwatchRx.java](../app/src/main/java/exercises09/StopwatchRx.java) and [stopwatchRxUI.java](../app/src/main/java/exercises09/stopwatchRxUI.java). We adapted stopwatchUI in order to add event listeners to the button from the StopwatchRx class.\
> `gradle -PmainClass=exercises09.StopwatchRx run`

## Exercise 9.4

In this exercise you should make an RxJava based solution of (part of) exercise 7.3 from week 7. Mandatory

**9.4.1.** Make an observable Observable<String> readWords that can read the file english-words.txt file. It should override:
public void subscribe(ObservableEmitter<String> s) so that each s.onNext pro- vides the next line from english-words.txt.

> Our implementation is in [ReadWords.java](../app/src/main/java/exercises09/ReadWords.java).
> 
> The tasks *9.4.**3***, *9.4.**4*** and *9.4.**5*** can be started with passing **3**, **4** or **5** as `args`.
> 
> `gradle -PmainClass="exercises09.ReadWords" run --args="<sub-assignment-number>"`

**9.4.2.** Make an observer Observer<String> display= new Observer<String>() that will print the word emitted from Observable<String> readWords i.e. one string every time onNext is called.

> see 9.4.1

**9.4.3.** Write a Java program that prints the first 100 word from english-words.txt using the the observable readWords and the observer display.

> see 9.4.1

**9.4.4.** Write a RxJava program to find and print all words that have at least 22 letters.

**Challenging**

**9.4.5.** Write a Java Rxprogram to find all palindromes and print them (use the isPalindrome) method from Exercise 5.2.

> see 9.4.1
