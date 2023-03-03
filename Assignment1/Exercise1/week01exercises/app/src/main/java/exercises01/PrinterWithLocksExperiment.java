package exercises01;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class PrinterWithLocksExperiment {

  Printer p = new Printer();
  Lock lock = new ReentrantLock();

  public PrinterWithLocksExperiment() {

    Thread t1 = new Thread(() -> callPrintForever());
    Thread t2 = new Thread(() -> callPrintForever());

    t1.start();
    t2.start();

  }

  private void callPrintForever() {
    while (true) {
      p.print();
    }
  }

  public class Printer {
    Printer() {
    }

    public void print() {
      lock.lock();                              // (1)
      try {
        System.out.print("-");                  // (2)
        try {
          Thread.sleep(50);
        } catch (InterruptedException exn) {
        }
        System.out.print("|");                  // (3)
      } finally {
        lock.unlock();                          // (4)
      }
    }
  }

  public static void main(String[] args) {
    new PrinterWithLocksExperiment();
  }

}