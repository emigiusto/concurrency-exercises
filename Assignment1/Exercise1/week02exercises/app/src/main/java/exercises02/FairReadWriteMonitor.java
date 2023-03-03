package exercises02;

public class FairReadWriteMonitor {

  private int readers = 0;
  private boolean writer = false;

  //////////////////////////
  // Read lock operations //
  //////////////////////////

  public synchronized void readLock() {
    try {
      while (writer) {
        this.wait();
      }
      readers++;
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  public synchronized void readUnlock() {
    readers--;
    if (readers == 0) {
      this.notifyAll();
    }
  }

  ///////////////////////////
  // Write lock operations //
  ///////////////////////////

  public synchronized void writeLock() {
    try {
      writer = true; // This line is added to make the Monitor fair
      while (readers > 0) {
        this.wait();
      }
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  public synchronized void writeUnlock() {
    writer = false;
    this.notifyAll();
  }
}