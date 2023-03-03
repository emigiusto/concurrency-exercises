package exercises09;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

public class ScheduleTester {
  public static void main(String[] args) throws InterruptedException {
    ExecutorService executorService = Executors.newFixedThreadPool(3);

    Observable.just("A", "AB", "ABC")
        .flatMap(v -> getLengthWithDelay(v)
            .doOnNext(s -> System.out.println("Processing Thread "
                + Thread.currentThread().getName()))
            .subscribeOn(Schedulers.from(executorService)))
        .subscribe(length -> System.out.println("Receiver Thread "
            + Thread.currentThread().getName()
            + ", Item length " + length));

    Thread.sleep(10000);
    executorService.shutdown();
  }

  protected static Observable<Integer> getLengthWithDelay(String v) {
    Random random = new Random();
    try {
      Thread.sleep(random.nextInt(3) * 1000);
      return Observable.just(v.length());
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    return null;
  }
}