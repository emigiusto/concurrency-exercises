package testingconcurrency;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;

public class ConcurrentSetSyncTest {

  // Variable with set under test
  private ConcurrentIntegerSetSync setSync;

  CyclicBarrier barrier; //Barrier to maximize contention and to avoid sequencial execution

  @BeforeEach
  public void initialize() {
    // init set
    setSync = new ConcurrentIntegerSetSync();
  }

  @RepeatedTest(5000)
/*   @Disabled */
  @DisplayName("Add one element to Sync Set")
  public void testingIntegerSetSyncAdd() {
    barrier = new CyclicBarrier(16 + 1); //Testing with 16 threads
    for (int i = 0; i < 16; i++) { //Testing with 16 threads
      new Thread(
        () -> {
          try {
            barrier.await(); // wait until all threads are ready
            setSync.add(1);
            barrier.await(); // wait until all threads are finished
          } catch (InterruptedException | BrokenBarrierException e) {
            e.printStackTrace();
          }
        }
      ).start();
    }
    try {
      barrier.await();
      barrier.await();
    } catch (InterruptedException | BrokenBarrierException e) {
      e.printStackTrace();
    }
    assertEquals(1, setSync.size());
  }

  @RepeatedTest(5000)
/*   @Disabled */
  @DisplayName("Remove one element from Sync Set")
  public void testingIntegerSetSyncRemove() {
    barrier = new CyclicBarrier(16 + 1); //Testing with 16 threads and barrier to increase probability of interleavings

    for (int  i = 0; i < 16; i++) { //Testing with 16 threads
      new Thread(
        () -> {
          try {
            barrier.await(); // wait until all threads are ready
            setSync.add(2);
            setSync.remove(2);
            barrier.await(); // wait until all threads are finished
          } catch (InterruptedException | BrokenBarrierException e) {
            e.printStackTrace();
          }
        }
      ).start();
    }
    try {
      barrier.await();
      barrier.await();
    } catch (InterruptedException | BrokenBarrierException e) {
      e.printStackTrace();
    }
    assertEquals( 0, setSync.size());
  }
}
