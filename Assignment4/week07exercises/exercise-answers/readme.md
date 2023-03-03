# Exercises Week 7

## Exercise 7.1

**Not mandatory**

You may use this Java skeleton as a starting point of the exercise.
import java.util.function.Function;

```Java
class LambdaExample {
  public static void main(String[] args) { new LambdaExample(); }
  public LambdaExample() {
    System.out.println("I: "+increment(f));
    //To be filled in
  }
  Function<Integer, Integer> f = (x) -> x+1;
}
```

**7.1.1** Write the (missing) code for the increment function to make the output of the LambdaExample: I: 9

> See [LambdaExample.java](../app/src/main/java/week07exercises/LambdaExample.java).

**7.1.2** Change the code in LambdaExample so that the function f multiplies with 5 (instead of incrementing).

> See [LambdaExample.java](../app/src/main/java/week07exercises/LambdaExample.java).

**7.1.3** These code snippets are from Benchmark.java and Benchmarkable.java in Week05/exercises-code ... /...:
    
```Java
---- Benchmark.java
import java.util.function.IntToDoubleFunction;
...
public Benchmark() {
...
    Mark6("multiply", i -> multiply(i));
    Mark6("multiply", Benchmark::multiply);
...
}
IntToDoubleFunction f) {
...
    dummy += f.applyAsDouble(i);
}
---- Benchmarkable.java
import java.util.function.IntToDoubleFunction;
IntToDoubleFunction {
  public void setup() { }
  public abstract double applyAsDouble(int i);
}
```

Write a short explanation of what happens in the two lines (emphasize explaining the two lambda expres- sions):
      
```Java
Mark6("multiply", i -> multiply(i));
Mark6("multiply", Benchmark::multiply);
```

> Answer:<br/> The first lambda expression defines a function that takes an element i and returns the result of multiply(i). The second lambda expression references to the method multiply() of the Benchmark class as a function.

**7.1.4*** Write a new version of Mark6 called Mark6int that will only accept measuring functions that takes an integer argument and delivers an integer result (e.g. intcountSequential in Exercise 7.2). Like Mark6, Mark6int should measure the running time of the function given as the second argument.

```Java
public static double Mark6int(String msg, ???) {
//To be filled in
}
```

> Our implemententation can be found in [Mark6int.java](../app/src/main/java/week07exercises/Mark6int.java)


## Exercise 7.2

**Not mandatory**  

You may find this in Week07/code-exercises ... /PrimeCountingPerf.java.   
In addition to counting the number of primes (in the range: 2..range) this program also measures the running time of the loop.  
Note, in your solution you may change this declaration (and initialization) long count= 0;

**7.2.1** Compile and run PrimeCountingPerf.java. Record the result in a text file.  

> The result can be found on `exercise-answers\exercise 7.2\exercise 7-2-1.txt`. Under our running conditions the calculation took 4361921.0 ns.

**7.2.2** Fill in the Java code using a stream for counting the number of primes (in the range: 2..range). Record the result in a text file.
> The implementation can be found on the class `PrimeCountingPerf#countIntStream()`.  
> The result can be found on `exercise-answers\exercise 7.2\exercise 7-2-2.txt`. This implementation takes on average the same computational time as the sequential solution.

**7.2.3** Add code to the stream expression that prints all the primes in the range 2..range. To test this program reduce range to a small number e.g. 1000.

> The implementation can be found on the class `PrimeCountingPerf#printPrimeNumbers()`.

**7.2.4** Fill in the Java code using the intermediate operation parallel for counting the number of primes (in the range: 2..range). Record the result in a text file.

> The result can be found on `exercise-answers\exercise 7.2\exercise 7-2-4.txt`.  
> Adding the parallel() intermediate operation reduces the running time by roughly 70%. This is using the Parallel Java 8 stream implementations, which are also thread safe.

**7.2.5** Add another prime counting method using a parallelStream for counting the number of primes (in the range: 2..range). Measure its performance using Mark7 in a way similar to how we measured the performance of the other three ways of counting primes. 

> The result can be found on `exercise-answers\exercise 7.2\exercise 7-2-5.txt`. The results show a similar performance compared with using the parallel() intermediate operation.


## Exercise 7.3
This exercise is about processing a large body of English words, using streams of strings. In particular, we use the words in the ﬁle `app/src/main/resources/english-words.txt`, in the exercises project directory.

The exercises below should be solved without any explicit loops (or recursion) as far as possible (that is, use streams).

**7.3.1**
Starting from the TestWordStream.java ﬁle, complete the `readWords` method and check that you can read the ﬁle as a stream and count the number of English words in it. For the `english-words.txt` ﬁle on the course homepage the result should be 235,886.

> Found in `TestWordStream#readWords`.

**7.3.2**
Write a stream pipeline to print the ﬁrst 100 words from the ﬁle.

> The method `printLines(long numberOfLines, String filename)` implements this. Calling it by `printLines(100, filename)` print the first 100 words.

**7.3.3**
Write a stream pipeline to ﬁnd and print all words that have at least 22 letters.

> Method that does this: `printLinesWithMinLetters(22, filename)`.

**7.3.4**
Write a stream pipeline to ﬁnd and print some word that has at least 22 letters.

> Method that does this: `printSomeLineWithMinLetters(22, filename)`.

**7.3.5**
Write a method boolean `isPalindrome(String s)` that tests whether a word s is a palindrome: a word that is the same spelled forward and backward. Write a stream pipeline to ﬁnd all palindromes and print them.

> Method that does this: `getPalindromesStream(filename).forEach(System.out::println)`. The `isPalindrome(String s)` method is implemented accordingly.

**7.3.6**
Make a parallel version of the palindrome-printing stream pipeline. Is it possible to observe whether it is faster or slower than the sequential one?

> The function that is a parallel version of *7.3.5* is called by `getPalindromesStreamParallel(filename).forEach(System.out::println)`. We tried to benchmark this by using the `Mark7` function, however, in order to not measure the time of the "printing" we measured the time it takes to call the reducer function `count()` to terminate the stream.
> 
> ```java
> PrimeCountingPerf.Mark7("normal version", v -> getPalindromesStream(filename).count());
> PrimeCountingPerf.Mark7("parallel version", v -> getPalindromesStreamParallel(filename).count());
> ``` 
> 
> This is the outcome:
> ```
> normal version                 16837197,1 ns  175408,62         16
> parallel version               12113562,8 ns   78414,59         32
> ```


**7.3.7**
Make a new version of the method `readWordStream` which can fetch the list of words from the internet. There is a (slightly modiﬁed) version of the word list at this URL: https://staunstrups.dk/jst/english-words.txt. Use this version of readWordStream to count the number of words (similarly to question 7.2.1). Note, the number of words is not the same in the two ﬁles !!

> This method call implement it: `System.out.println(readWordStream(wordsUrl).count())`.

**7.3.8**
Use a stream pipeline that turns the stream of words into a stream of their lengths, to ﬁnd and print the minimal, maximal and average word lengths.

Hint: There is a simple solution using an operator exampliﬁed on p. 141 of Java Precisely (included in the readings for this week).

> Our version is implemented in `printStats(Supplier<Stream<String>> streamSupplier)`.