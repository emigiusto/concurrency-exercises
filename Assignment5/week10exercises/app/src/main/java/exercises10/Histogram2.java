package exercises10;

import java.util.concurrent.atomic.AtomicInteger;

public class Histogram2 implements Histogram {

  private final int[] bins;
  private static AtomicInteger total = new AtomicInteger(0);

  public Histogram2(int span) {
    this.bins = new int[span];
}

  public void increment(int binNumber) {
    synchronized(bins){
      bins[binNumber] = bins[binNumber] + 1;
    }
    total.incrementAndGet();
  }

  public int getCount(int binNumber) {
    synchronized(bins){
      return bins[binNumber];
    }
  }

  public float getPercentage(int binNumber) {
    synchronized(bins){
      return bins[binNumber]/bins.length;
    }
  }

  public int getSpan() {
    return bins.length;
  }

  public int getTotal() {
    return total.get();
  }

  public int getAndClear(int binNumber) {
    synchronized(bins){
      int oldValue = bins[binNumber];
      bins[binNumber] = 0;
      return oldValue;
    }
  }
}