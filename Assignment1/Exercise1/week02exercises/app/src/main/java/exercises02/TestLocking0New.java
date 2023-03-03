// For week 2

package exercises02;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class TestLocking0New {

  public static void main(String[] args) {
    final int count = 1_000_000;
    MysteryNew m = new MysteryNew();
    Thread t1 = new Thread(
        () -> {
          for (int i = 0; i < count; i++)
            m.addInstance(1);
        });
    Thread t2 = new Thread(
        () -> {
          for (int i = 0; i < count; i++)
            m.addStatic(1);
        });
    t1.start();
    t2.start();
    try {
      t1.join();
      t2.join();
    } catch (InterruptedException exn) {
    }
    System.out.printf("Sum is %f and should be %f%n", m.sum(), 2.0 * count);
  }
}

class MysteryNew {

  private static double sum = 0;

  private static Lock lock = new ReentrantLock();         // (1)

  public static synchronized void addStatic(double x) {
    lock.lock();
    try {
      sum += x;
    } finally {
      lock.unlock();
    }
  }

  public synchronized void addInstance(double x) {
    lock.lock();
    try {
      sum += x;
    } finally {
      lock.unlock();
    }
  }

  public static synchronized double sum() {
    return sum;
  }
}
