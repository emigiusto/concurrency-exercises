package exercises02;

public class ReadWriteMonitor {

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
      while (readers > 0 || writer) {
          this.wait();
      }
      writer = true;
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  public synchronized void writeUnlock() {
    writer = false;
    this.notifyAll();
  }
}