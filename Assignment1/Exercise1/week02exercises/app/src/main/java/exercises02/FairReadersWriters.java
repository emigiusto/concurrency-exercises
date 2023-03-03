package exercises02;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class FairReadersWriters {

  public FairReadersWriters() {
    FairReadWriteMonitor m = new FairReadWriteMonitor();

    final int numReadersWriters = 10;

    for (int i = 0; i < numReadersWriters; i++) {

      // start a reader
      new Thread(
          () -> {
            m.readLock();
            System.out.println(
                " Reader " + Thread.currentThread().getId() + " started reading");
            // read
            System.out.println(
                " Reader " + Thread.currentThread().getId() + " stopped reading");
            m.readUnlock();
          }).start();

      // start a writer
      new Thread(
          () -> {
            m.writeLock();
            System.out.println(
                " Writer " + Thread.currentThread().getId() + " started writing");
            // write
            System.out.println(
                " Writer " + Thread.currentThread().getId() + " stopped writing");
            m.writeUnlock();
          }).start();
    }
  }

  // gradle -PmainClass=exercises02.ReadersWriters run --warning-mode none

  public static void main(String[] args) {
    new FairReadersWriters();
  }

}