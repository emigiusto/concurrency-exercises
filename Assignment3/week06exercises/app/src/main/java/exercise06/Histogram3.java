package exercise06;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.google.common.util.concurrent.Striped;

public class Histogram3 implements Histogram {

  private final int[] bins;
  private static AtomicInteger total = new AtomicInteger(0);
  private final ReentrantLock[] locks;

  public Histogram3(int span, int locksNumber) {
    this.bins = new int[span];
    locks = new ReentrantLock[locksNumber];
    for (int i = 0; i < locksNumber; i++) {
      locks[i] = new ReentrantLock();
    }
  }

  private Lock getLock(int binNumber) {
    int lockNumber = binNumber % locks.length;
    Lock lock = locks[lockNumber];
    return lock;
  }

  public void increment(int binNumber) {
    Lock lock = getLock(binNumber);
    lock.lock();
    try {
      bins[binNumber] = bins[binNumber] + 1;
      total.incrementAndGet(); // Does it make sense to be inside the locked block?
    } finally {
      lock.unlock();
    }
  }

  public int getCount(int binNumber) {
    Lock lock = getLock(binNumber);
    lock.lock();
    try {
      return bins[binNumber];
    } finally {
      lock.unlock();
    }
  }

  public float getPercentage(int binNumber) {
    Lock lock = getLock(binNumber);
    lock.lock();
    try {
      return bins[binNumber] / bins.length;
    } finally {
      lock.unlock();
    }
  }

  public int getSpan() {
    return bins.length;
  }

  public int getTotal() {
    return total.get();
  }
}