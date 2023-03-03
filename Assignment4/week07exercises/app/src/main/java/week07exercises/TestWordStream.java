// Week 3
// sestoft@itu.dk * 2015-09-09
package week07exercises;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.IntSummaryStatistics;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class TestWordStream {
  public static void main(String[] args) {
    String filename = "src/main/resources/english-words.txt";
    String wordsUrl = "https://staunstrups.dk/jst/english-words.txt";

    // 7.3.2
    // printLines(100, filename);

    // 7.3.3
    // printLinesWithMinLetters(22, filename);

    // 7.3.4
    // printSomeLineWithMinLetters(22, filename);

    // 7.3.5
    // getPalindromesStream(filename).forEach(System.out::println);

    // 7.3.6
    // getPalindromesStreamParallel(filename).forEach(System.out::println);

    // 7.3.6 Benchmarking
    // PrimeCountingPerf.Mark7("normal version", v ->
    // getPalindromesStream(filename).count());
    // PrimeCountingPerf.Mark7("parallel version", v ->
    // getPalindromesStreamParallel(filename).count());

    // 7.3.7
    // System.out.println(readWordStream(wordsUrl).count());

    // 7.3.8
    // This is "our" version
    System.out.println("Statistics for the file:");
    printStats(() -> readWords(filename));
    System.out.println("Statistics for the url:");
    printStats(() -> readWordStream(wordsUrl));

    System.out.println();
    System.out.println();

    // 7.3.8
    // This is the version according to the book
    // System.out.println("Statistics for the file:");
    // printStatsUsingBuiltInMethod(readWords(filename));
    // System.out.println("Statistics for the url:");
    // printStatsUsingBuiltInMethod(readWordStream(wordsUrl));

  }

  public static Stream<String> readWords(String filename) {
    try {
      BufferedReader reader = new BufferedReader(new FileReader(filename));
      return reader.lines();
    } catch (IOException exn) {
      return Stream.<String>empty();
    }
  }

  public static Stream<String> readWordStream(String url) {
    try {
      HttpURLConnection connection;
      connection = (HttpURLConnection) new URL(url).openConnection();
      BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
      return reader.lines();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return Stream.<String>empty();
  }

  public static void printLines(long numberOfLines, String filename) {
    readWords(filename)
        .limit(numberOfLines)
        .forEach(System.out::println);
  }

  public static void printLinesWithMinLetters(int minLetters, String filename) {
    readWords(filename)
        .filter(line -> line.length() >= minLetters)
        .forEach(System.out::println);
  }

  public static void printSomeLineWithMinLetters(int minLetters, String filename) {
    readWords(filename)
        .filter(line -> line.length() >= minLetters)
        .findFirst()
        .ifPresent(System.out::println);
  }

  public static boolean isPalindrome(String s) {
    return s.equals(new StringBuilder(s).reverse().toString());
  }

  public static Stream<String> getPalindromesStream(String filename) {
    return readWords(filename)
        .filter(TestWordStream::isPalindrome);
  }

  public static Stream<String> getPalindromesStreamParallel(String filename) {
    return readWords(filename)
        .parallel()
        .filter(TestWordStream::isPalindrome);
  }

  public static Map<Character, Integer> letters(String s) {
    Map<Character, Integer> res = new TreeMap<>();
    // TO DO: Implement properly
    return res;
  }

  // This is our own try for 7.3.8 before reading the relevant part in the book
  // Below is a method closer to the one in the book
  public static void printStats(Supplier<Stream<String>> streamSupplier) {
    System.out.print("Min: ");
    streamSupplier.get()
        .mapToInt(s -> s.length())
        .min()
        .ifPresent(System.out::println);

    System.out.print("Max: ");
    streamSupplier.get()
        .mapToInt(s -> s.length())
        .max()
        .ifPresent(System.out::println);

    System.out.print("Avg: ");
    streamSupplier.get()
        .mapToInt(s -> s.length())
        .average()
        .ifPresent(System.out::println);
  }

  // This is the book-equivalent method for 7.3.8
  public static void printStatsUsingBuiltInMethod(Stream<String> stream) {
    IntSummaryStatistics stats = stream
        .mapToInt(s -> s.length())
        .summaryStatistics();

    System.out.printf("count=%d, min=%d, max=%d, sum=%d, mean=%f%n",
        stats.getCount(), stats.getMin(), stats.getMax(),
        stats.getSum(), stats.getAverage());
  }

}
